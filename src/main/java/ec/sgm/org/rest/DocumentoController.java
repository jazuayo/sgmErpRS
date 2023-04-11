package ec.sgm.org.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.repository.CategoriaRepository;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.DocumentoSerie;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.DocumentoConsultaReq;
import ec.sgm.org.model.DocumentoReq;
import ec.sgm.org.model.DocumentosResp;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.DocumentoRepository;
import ec.sgm.org.repository.DocumentoSerieRepository;
import ec.sgm.org.repository.EstadoRepository;
import ec.sgm.org.repository.OrganizacionRepository;
import ec.sgm.org.service.DocumentoService;

@RestController
@RequestMapping("/documento")
public class DocumentoController {
	private static final Logger LOGGER = LogManager.getLogger(DocumentoController.class);
	@Autowired
	private DocumentoRepository repository;
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private CategoriaRepository categoriaRepository;
	@Autowired
	private OrganizacionRepository organizacionRepository;
	@Autowired
	private DocumentoSerieRepository documentoSerieRepository;
	@Autowired
	private DocumentoService documentoService;

	/**
	 * Lista los documentos
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<Documento> listar() throws SigmaException {
		try {
			return repository.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error listar los documentos", e);
		}
	}

	/**
	 * Ingresa o actualiza el documento
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping
	@Transactional
	public Documento grabar(@RequestBody DocumentoReq registro) throws SigmaException {
		try {

			Estado estado = estadoRepository.findById(registro.getEstadoCod()).orElse(null);
			Categoria origen = categoriaRepository.findById(registro.getOrigenCod()).orElse(null);
			Organizacion organizacion = organizacionRepository.findById(registro.getOrganizacionCod()).orElse(null);

			Documento documento = new Documento();
			documento.setDocumentoCod(registro.getDocumentoCod());
			documento.setDocumentoDes(registro.getDocumentoDes());
			documento.setEstado(estado);
			documento.setInicio(registro.getInicio());
			documento.setLongitud(registro.getLongitud());
			documento.setOrden(registro.getOrden());
			documento.setOrganizacion(organizacion);
			documento.setOrigen(origen);
			documento.setSecuencial(registro.getSecuencial());
			documento.setUsuario(registro.getUsuario());
			documento.setCe(registro.getCe());
			documento.setSeries(registro.getSeries());
			repository.save(documento);
			List<DocumentoSerie> seriesActualizados = documentoSerieRepository
					.findByDocumentoCodOrderByDocSerieId(registro.getDocumentoCod());
			documento.setSeries(seriesActualizados);

			return documento;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al registrar el documento", e);
		}
	}

	/**
	 * Lista los documentos por organización y origen/categoria
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */

	@PostMapping(value = "/buscarPorOrganizacionOrigen")
	public List<DocumentosResp> buscarPorOrganizacionOrigen(@RequestBody DocumentoConsultaReq registro)
			throws SigmaException {
		try {
			Categoria origen = categoriaRepository.findById(registro.getOrigenCod()).orElse(null);
			Organizacion organizacion = organizacionRepository.findById(registro.getOrganizacionCod()).orElse(null);
			List<Documento> documentos = repository.findByOrganizacionAndOrigenOrderByOrden(organizacion, origen);
			List<DocumentosResp> respuesta = new ArrayList<DocumentosResp>();
			for (Documento documento : documentos) {
				DocumentosResp resp = new DocumentosResp();
				resp.setDocumentoCod(documento.getDocumentoCod());
				resp.setDocumentoDes(documento.getDocumentoDes());
				resp.setSecuencial(documento.getSecuencial());
				resp.setLongitud(documento.getLongitud());
				resp.setInicio(documento.getInicio());
				resp.setOrden(documento.getOrden());
				resp.setEstado(documento.getEstado());
				resp.setOrigen(documento.getOrigen());
				resp.setOrganizacion(documento.getOrganizacion());
				resp.setSeries(documento.getSeries());
				resp.setUsuario(documento.getUsuario());
				resp.setSriAts(documento.getSriAts());
				resp.setCe(documento.getCe());
				resp.setInventario(documento.getInventario());
				String secuencia = documentoService.recuperaSecuencia(documento.getDocumentoCod());
				resp.setSecuencia(secuencia);
				respuesta.add(resp);
			}

			return respuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar los documentos por organización y origen/categoria.", e);
		}
	}

	/**
	 * Eliminar el documento
	 * 
	 * @param id
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{id}")
	public HashMap<String, String> eliminar(@PathVariable("id") String id) throws SigmaException {
		try {
			repository.deleteById(id);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar el documento", e);
		}
	}

	/**
	 * Lista los documentos por organización
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/organizacion/{organizacionCod}")
	public List<Documento> buscaDocumentosPorOrganizacion(@PathVariable("organizacionCod") String organizacionCod)
			throws SigmaException {
		try {
			Organizacion organizacion = organizacionRepository.findById(organizacionCod).orElse(null);
			List<Documento> documentos = repository.findByOrganizacionOrderByDocumentoDes(organizacion);
			return documentos;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar los documentos po organización.", e);
		}
	}

}

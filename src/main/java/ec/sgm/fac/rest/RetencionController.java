package ec.sgm.fac.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaDateMidnight;
import ec.sgm.SigmaException;
import ec.sgm.ce.modelo.ClaveAcceso;
import ec.sgm.ce.service.SriService;
import ec.sgm.core.Constantes;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.Retencion;
import ec.sgm.fac.entity.RetencionDetalle;
import ec.sgm.fac.modelo.RetencionReq;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.fac.repository.RetencionRepository;
import ec.sgm.fac.service.NotificaDocumentos;
import ec.sgm.fac.service.RetencionPDFService;
import ec.sgm.fac.service.RetencionXMLService;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.DocumentoRepository;
import ec.sgm.org.repository.EstadoRepository;
import ec.sgm.org.repository.OrganizacionRepository;
import ec.sgm.org.service.DocumentoService;
import ec.sgm.org.service.ParametroService;

@RestController
@RequestMapping("/retencion")
public class RetencionController {
	private static final Logger LOGGER = LogManager.getLogger(RetencionController.class);
	@Autowired
	private ParametroService parametroService;
	@Autowired
	private RetencionRepository retencionRepository;
	@Autowired
	private OrganizacionRepository organizacionRepository;
	@Autowired
	private DocumentoRepository documentoRepository;
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private RetencionPDFService servicePdfRetencion;
	@Autowired
	private RetencionXMLService serviceXmlRetencion;
	@Autowired
	private FacturaRepository repositoryFactura;
	@Autowired
	private DocumentoService documentoService;
	@Autowired
	private NotificaDocumentos envioComprobante;
	@Autowired
	private SigmaDateMidnight serviceFecha;

	/**
	 * Inserta o actualiza las retenciones
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@Transactional
	@PostMapping
	public Retencion grabar(@RequestBody RetencionReq registro) throws SigmaException {

		try {
			Documento documento = documentoRepository.findById(registro.getDocumentoCod()).orElse(null);
			Organizacion organizacion = organizacionRepository.findById(registro.getOrganizacionCod()).orElse(null);
			Estado estado = estadoRepository.findById(registro.getEstadoCod()).orElse(null);
			Retencion retencion = new Retencion();
			retencion.setRetencionId(registro.getRetencionId());
			retencion.setDocumento(documento);
			retencion.setOrganizacion(organizacion);
			retencion.setEstado(estado);
			retencion.setAutorizacionFecha(serviceFecha.DateMidnight(registro.getAutorizacionFecha()));
			retencion.setAutorizacionNumero(registro.getAutorizacionNumero());
			retencion.setDocumentoNumero(registro.getDocumentoNumero());

			if (retencion.getRetencionId() == null && documento.getCe().booleanValue() == true) {
				System.out.println("Genera secuencia:" + retencion.getDocumentoNumero().indexOf("-"));
				retencion.setDocumentoNumero(documentoService.recuperaIncrementaSecuencia(documento.getDocumentoCod()));
			}

			retencion.setFechaEmite(serviceFecha.DateMidnight(registro.getFechaEmite()));
			retencion.setUsuario(registro.getUsuario());

			Long retencionId = retencionRepository.save(retencion).getRetencionId();

			// Registra el detalle de la retención
			Factura factura = repositoryFactura.findById(registro.getDetalles().get(0).getFactura().getDocumentoId())
					.get();
			for (RetencionDetalle detalle : registro.getDetalles()) {
				detalle.setRetencionId(retencionId);
				detalle.setFactura(factura);
			}
			retencion.setDetalles(registro.getDetalles());
			retencion.setRetencionId(retencionId);
			retencionRepository.save(retencion);
			return retencion;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al registrar la retencion.", e);
		}
	}

	/**
	 * Recupera retencione por organización
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/recuperaPorOrganizacion/{organizacionCod}")
	public List<Retencion> buscarPorOrganizacion(@PathVariable("organizacionCod") String organizacionCod)
			throws SigmaException {
		try {
			Organizacion organizacion = organizacionRepository.findById(organizacionCod).orElse(null);
			List<Retencion> retenciones = retencionRepository.findByOrganizacionOrderByRetencionId(organizacion);
			return retenciones;
		} catch (Exception e) {
			throw new SigmaException("Error al listar las retenciones por organizacion.", e);
		}
	}

	/**
	 * Eliminar la retencion / cambiar el estado de la retencion a ANULADO
	 * 
	 * @param id
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{id}")
	public HashMap<String, String> eliminar(@PathVariable("id") Long id) throws SigmaException {
		try {

			Retencion retencion = retencionRepository.findById(id).orElse(null);

			if (retencion == null) {
				LOGGER.error("Registro no encontrado en DB");
				throw new SigmaException("Registro no encontrado.");
			}
			Estado estadoAnulado = estadoRepository.findById(Constantes.ESTADO_ANULADO).orElse(null);
			retencion.setEstado(estadoAnulado);
			retencionRepository.save(retencion);
			// retencionRepository.deleteById(id);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar la retencion.", e);
		}
	}

	/**
	 * Generar retencion pdf - CT
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/procesar/pdf/{retencionId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] procesarRetencionPdf(@PathVariable("retencionId") Long retencionId)
			throws SigmaException {
		try {
			return servicePdfRetencion.generarPdfRetencion(retencionId);
		} catch (Exception e) {
			LOGGER.error("Error al procesar la retencion pdf: " + e.getMessage());
			throw new SigmaException("Error al procesar el documento, generar pdf retencion", e);

		}
	}

	/**
	 * Generar retencion xml - CT
	 * 
	 * @param retencionId
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/procesar/xml/{retencionId}")
	public HashMap<String, String> procesarRetencionXml(@PathVariable("retencionId") Long retencionId)
			throws SigmaException {
		try {
			Retencion retencion = retencionRepository.findById(retencionId).get();
			Boolean esElectronico = retencion.getDocumento().getCe();

			if (esElectronico == null || !esElectronico) {
				System.out.println("No es comprobante electronico");
				return MensajeResponse.ok();
			}

			String organizacionCod = retencion.getOrganizacion().getOrganizacionCod();
			String pathArchivoFirmado = null;
			boolean esProduccion = false;

			if (retencion.getSriEstado() == null || retencion.getSriEstado().trim().length() < 2) {
				ClaveAcceso claveAcceso = new ClaveAcceso();
				claveAcceso.setFechaEmision(retencion.getFechaEmite());
				claveAcceso.setTipoComprobante(retencion.getDocumento().getSriCe());
				claveAcceso.setNumeroRuc(parametroService.findValorByClave("sriRuc", organizacionCod));
				claveAcceso.setTipoAmbiente(parametroService.findValorByClave("sriCeAmbiente", organizacionCod));
				claveAcceso.setDocumentoNumero(retencion.getDocumentoNumero());
				claveAcceso.setCodigoNumerico(parametroService.findValorByClave("sriCeComplemento", organizacionCod));
				retencion.setAutorizacionNumero(SriService.generaClaveAcceso(claveAcceso));
				retencionRepository.save(retencion);
				pathArchivoFirmado = serviceXmlRetencion.generaArchivosXml(retencionId);
				System.out.println("archivo firmado:" + pathArchivoFirmado);
			}
			try {
				if ("2".equalsIgnoreCase(parametroService.findValorByClave("sriCeAmbiente", organizacionCod))) {
					esProduccion = true;
				}
				HashMap<String, Object> resultado = SriService.procesar(retencion.getSriEstado(),
						retencion.getAutorizacionNumero(), pathArchivoFirmado, esProduccion);
				if (resultado != null && resultado.containsKey(SriService.ESTADO_KEY)) {
					retencion.setSriEstado((String) resultado.get(SriService.ESTADO_KEY));
					retencion.setSriNota((String) resultado.get("nota"));
					if (resultado.containsKey("fecha")) {
						retencion.setAutorizacionFecha((Date) resultado.get("fecha"));
					}
					Estado estado = estadoRepository.findById("AUT").orElse(null);
					retencion.setEstado(estado);
					retencionRepository.save(retencion);
				} else {
					LOGGER.error("procesando SRI: No se ha recuperado un estado");
					return MensajeResponse.error("No se ha recuperado un estado");
				}
			} catch (Exception e) {
				LOGGER.error("procesando SRI:" + e.getMessage());
				return MensajeResponse.error(e.getMessage());
			}
			//envioComprobante.notificaEmailDocRetenciones(retencion);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error("Error al procesar:" + e.getMessage());
			return MensajeResponse.error(e.getMessage());
		}
	}

	/**
	 * Recuperar la retencion por el id
	 * 
	 * @param retencionId
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/recuperar/registro/{retencionId}")
	public Retencion recuperarRetencionPorId(@PathVariable("retencionId") Long retencionId) throws SigmaException {
		try {
			Retencion retencion = retencionRepository.findById(retencionId).orElse(null);
			return retencion;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en recuperar registro de retencion.", e);
		}
	}

}

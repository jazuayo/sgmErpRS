package ec.sgm.org.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import ec.sgm.core.Fecha;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.entity.Parametro;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.model.ParametroPrecioCalculaResp;
import ec.sgm.org.model.ParametroReq;
import ec.sgm.org.model.ParametroResp;
import ec.sgm.org.repository.OrganizacionRepository;
import ec.sgm.org.repository.ParametroRepository;

/**
 * 
 * @author SIGMA - TL
 *
 */
@RestController
@RequestMapping("/parametro")
public class ParametroController {
	private static final Logger LOGGER = LogManager.getLogger(ParametroController.class);
	@Autowired
	private ParametroRepository repository;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;
	@Autowired
	private ParametroRepository parametroRepository;

	/**
	 * Lista los parámetros
	 * 
	 * @return
	 */
	@GetMapping
	public List<ParametroResp> listar() throws SigmaException {
		try {
			List<Parametro> parametros = repository.findAll();
			List<ParametroResp> parametrosResp = new ArrayList<>();
			for (Parametro parametro : parametros) {
				ParametroResp parametroResp = new ParametroResp();
				// DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				// String fechaDesde = parametro.getFechaDesde().format(formato);
				// String fechaHasta = parametro.getFechaHasta().format(formato);
				parametroResp.setParametroId(parametro.getParametroId());
				parametroResp.setParametroDes(parametro.getParametroDes());
				parametroResp.setClave(parametro.getClave());
				parametroResp.setValor(parametro.getValor());
				parametroResp.setFechaDesde(Fecha.formatoReportes(parametro.getFechaDesde()));
				parametroResp.setFechaHasta(Fecha.formatoReportes(parametro.getFechaHasta()));
				parametroResp.setOrganizacion(parametro.getOrganizacion());
				parametrosResp.add(parametroResp);
			}
			return parametrosResp;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error listar las parámetros", e);
		}
	}

	/**
	 * Ingresa o actualiza un parámetro
	 * 
	 * @param registro
	 * @return
	 */
	@PostMapping
	public HashMap<String, String> grabar(@RequestBody ParametroReq registro) throws SigmaException {
		try {
			Parametro parametro = new Parametro();
			if (registro.getParametroId() != null && registro.getParametroId().intValue() == 0)
				registro.setParametroId(null);
			Organizacion organizacion = repositoryOrganizacion.findById(registro.getOrganizacionCod()).orElse(null);
			parametro.setParametroId(registro.getParametroId());
			parametro.setParametroDes(registro.getParametroDes());
			parametro.setClave(registro.getClave());
			parametro.setValor(registro.getValor());
			parametro.setFechaDesde(registro.getFechaDesde());
			parametro.setFechaHasta(registro.getFechaHasta());
			parametro.setOrganizacion(organizacion);
			repository.save(parametro);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al registrar el parámetro", e);
		}
	}

	/**
	 * Elimina un parámetro
	 * 
	 * @param id
	 * @return
	 */
	@PostMapping(value = "/{id}")
	public HashMap<String, String> eliminar(@PathVariable("id") Integer id) throws SigmaException {
		try {
			repository.deleteById(id);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar el parámetro", e);
		}
	}

	/**
	 * Listar parametros de "precioCalcula" por organizacion CT-Sigma
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "precio/{organizacionCod}")
	public List<ParametroPrecioCalculaResp> listarPrecioCalculaPorOrganizacion(
			@PathVariable("organizacionCod") String organizacionCod) throws SigmaException {
		try {
			String clave = "precioCalcula";
			List<ParametroPrecioCalculaResp> respuesta = new ArrayList<ParametroPrecioCalculaResp>();
			List<Parametro> parametros = parametroRepository.findByClaveAndOrganizacionCodOrderByParametroDes(clave,
					organizacionCod);
			for (Parametro parametro : parametros) {
				ParametroPrecioCalculaResp parametroResp = new ParametroPrecioCalculaResp();
				parametroResp.setParametroId(parametro.getParametroId());
				parametroResp.setParametroDes(parametro.getParametroDes());
				parametroResp.setClave(parametro.getClave());
				parametroResp.setValor(parametro.getValor());
				String valor = parametro.getValor().substring(parametro.getValor().indexOf("/") + 1);
				Double valorNum = Double.parseDouble(valor);
				parametroResp.setValorNum(valorNum);
				respuesta.add(parametroResp);
			}
			return respuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar parametros de precio calcula por org.", e);
		}
	}

	/**
	 * Buscar parametro por org y clave
	 * 
	 * @param organizacionCod
	 * @param clave
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/clave/{organizacionCod}/{clave}")
	public ParametroResp parametroValor(@PathVariable("organizacionCod") String organizacionCod,
			@PathVariable("clave") String clave) throws SigmaException {
		try {
			ParametroResp parametroResp = new ParametroResp();
			List<Parametro> parametros = parametroRepository.findByClaveAndOrganizacionCodOrderByParametroDes(clave,
					organizacionCod);
			if (parametros.size() > 1) {
				LOGGER.error("Existen mas de dos parametros para la misma clave y organizacion, " + clave);
				throw new SigmaException("Existen mas de dos parametos para la misma clave y organizacion",
						"Error al recuperar parametro");
			}
			Parametro parametro = new Parametro();

			if (parametros.size() == 1) {
				parametro = parametros.get(0);
				parametroResp.setFechaDesde(parametro.getFechaDesde().toString());
				parametroResp.setFechaHasta(parametro.getFechaHasta().toString());
			}

			parametroResp.setClave(clave);
			parametroResp.setOrganizacion(parametro.getOrganizacion());
			parametroResp.setParametroDes(parametro.getParametroDes());
			parametroResp.setParametroId(parametro.getParametroId());
			parametroResp.setValor(parametro.getValor());
			return parametroResp;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al recuperar parametro", e);
		}
	}
}

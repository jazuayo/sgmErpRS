package ec.sgm.cta.rest;

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
import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.cta.entity.PlanCuentaTipo;
import ec.sgm.cta.modelo.PlanCuentaReq;
import ec.sgm.cta.repository.PlanCuentaRepository;
import ec.sgm.cta.repository.PlanCuentaTipoRepository;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.OrganizacionRepository;

/**
 * 
 * @author SIGMA - TL
 *
 */
@RestController
@RequestMapping("/planCuenta")
public class PlanCuentaController {
	private static final Logger LOGGER = LogManager.getLogger(PlanCuentaController.class);

	@Autowired
	private PlanCuentaRepository repository;

	@Autowired
	private PlanCuentaTipoRepository repositoryPlanCuentaTipo;

	@Autowired
	private OrganizacionRepository repositoryOrganizacion;

	/**
	 * Ingresa o actualiza el plan de cuentas
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping
	public HashMap<String, String> grabar(@RequestBody PlanCuentaReq registro) throws SigmaException {
		try {
			PlanCuentaTipo planCuentaTipo = repositoryPlanCuentaTipo.findById(registro.getCuentaTipoCod()).orElse(null);
			Organizacion organizacion = repositoryOrganizacion.findById(registro.getOrganizacionCod()).orElse(null);
			PlanCuenta planCuenta = new PlanCuenta();
			planCuenta.setCuentaCod(registro.getCuentaCod());
			planCuenta.setCuentaNum(registro.getCuentaNum());
			planCuenta.setCuentaDes(registro.getCuentaDes());
			planCuenta.setObservaciones(registro.getObservaciones());
			planCuenta.setMovimiento(registro.getMovimiento());
			planCuenta.setNivel(registro.getNivel());
			planCuenta.setFechaDesde(registro.getFechaDesde());
			planCuenta.setFechaHasta(registro.getFechaHasta());
			planCuenta.setOperativa(registro.getOperativa());
			// planCuenta.setCtaPlanCuentaPadre(planCuentaPadre);
			planCuenta.setPlanCuentaTipo(planCuentaTipo);
			planCuenta.setOrganizacion(organizacion);
			planCuenta.setUsuario(registro.getUsuario());
			repository.save(planCuenta);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al registrar el plan de cuenta.", e);
		}
	}

	/**
	 * Elimina un registro del plan de cuentas
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
			throw new SigmaException("Error al eliminar el plan de cuentas.", e);
		}
	}

	/**
	 * Listar todas las cuentas de la organizacion
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/organizacion/{organizacionCod}")
	public List<PlanCuenta> buscaCuentasPorOrganizacion(@PathVariable("organizacionCod") String organizacionCod)
			throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).orElse(null);
			List<PlanCuenta> planCuentas = repository.findByOrganizacionOrderByCuentaCod(organizacion);
			return planCuentas;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar plan de cuentas por organización.", e);
		}
	}

	/**
	 * Listar las cuentas solo movimiento y organizacion - CT
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/movimiento/organizacion/{organizacionCod}")
	public List<PlanCuenta> cuentasMovimientoPorOrganizacion(@PathVariable("organizacionCod") String organizacionCod)
			throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			List<PlanCuenta> respuesta = repository.cuentaEsMovimientoPorOrganizacion(organizacion);
			return respuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar plan de cuentas de moviento por organización.", e);
		}
	}

	/**
	 * Listar las cuentas solo operativa y organizacion - CT
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/operativa/organizacion/{organizacionCod}")
	public List<PlanCuenta> cuentasOperativaPorOrganizacion(@PathVariable("organizacionCod") String organizacionCod)
			throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			List<PlanCuenta> respuesta = repository.cuentaEsOperativaPorOrganizacion(organizacion);
			return respuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar plan de cuentas operativas por organización.", e);
		}
	}

}

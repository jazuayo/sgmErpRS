package ec.sgm.cta.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.cta.entity.PlanCuentaTipo;
import ec.sgm.cta.repository.PlanCuentaTipoRepository;

/**
 * 
 * @author SIGMA - TL
 *
 */
@RestController
@RequestMapping("/planCuentaTipo")
public class PlanCuentaTipoController {
	private static final Logger LOGGER = LogManager.getLogger(PlanCuentaTipoController.class);

	@Autowired
	private PlanCuentaTipoRepository repository;

	/**
	 * Listar los tipos de planes de cuenta
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<PlanCuentaTipo> listar() throws SigmaException {
		try {
			List<PlanCuentaTipo> planCuentaTipos = repository.findAll();
			return planCuentaTipos;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error listar los tipos de planes de cuenta.", e);
		}
	}
}

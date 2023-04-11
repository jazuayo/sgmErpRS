package ec.sgm.cta.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.cta.repository.PlanCuentaRepository;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.OrganizacionRepository;

@Service
public class PlanCuentaService {
	private static final Logger LOGGER = LogManager.getLogger(PlanCuentaService.class);
	@Autowired
	private PlanCuentaRepository planCuentaRepository;
	@Autowired
	private OrganizacionRepository organizacionRepository;

	public PlanCuenta findByCuentaCod(String cuentaCod) throws SigmaException {
		PlanCuenta planCuenta = planCuentaRepository.findById(cuentaCod).orElse(null);
		if (planCuenta == null) {
			LOGGER.error("No se ha encontrado la cuenta:" + cuentaCod);
			throw new SigmaException("No se ha encontrado la cuenta:" + cuentaCod);
		}
		return planCuenta;
	}

	public PlanCuenta findByNumCuentaAndOrganizacion(String numCuenta, String organizacionCod) throws SigmaException {
		Organizacion organizacion = organizacionRepository.findById(organizacionCod).orElse(null);
		List<PlanCuenta> cuentas = planCuentaRepository.findByCuentaNumAndOrganizacion(numCuenta, organizacion);

		if (cuentas.isEmpty()) {
			LOGGER.error("No se ha encontrado la cuenta:" + numCuenta + " y organizacion: " + organizacionCod);
			throw new SigmaException(
					"No se ha encontrado la cuenta:" + numCuenta + " y organizacion: " + organizacionCod);
		}
		if (cuentas.size() > 1) {
			LOGGER.error("Multiples registros:" + numCuenta + " y organizacion: " + organizacionCod);
			throw new SigmaException("Multiples registros:" + numCuenta + " y organizacion: " + organizacionCod);
		}
		return cuentas.get(0);
	}

}

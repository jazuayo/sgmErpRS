package ec.sgm.fac.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.entity.FormaPago;
import ec.sgm.fac.repository.FormaPagoRepository;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.OrganizacionRepository;

@RestController
@RequestMapping("/formaPago")
public class FormaPagoController {
	private static final Logger LOGGER = LogManager.getLogger(FormaPagoController.class);
	@Autowired
	private FormaPagoRepository repositoryFormaPago;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;

	/**
	 * Listar las formas de pago por organizacion
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/{organizacionCod}")
	public List<FormaPago> listar(@PathVariable("organizacionCod") String organizacionCod) throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			List<FormaPago> formasPago = repositoryFormaPago.findByOrganizacion(organizacion);
			return formasPago;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException(e.getMessage(), e);
		}
	}
}

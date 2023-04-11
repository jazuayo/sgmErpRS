package ec.sgm.fac.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.entity.PersonaTipo;
import ec.sgm.fac.repository.PersonaTipoRepository;

@RestController
@RequestMapping("/personaTipo")
public class PersonaTipoController {
	private static final Logger LOGGER = LogManager.getLogger(PersonaTipoController.class);
	@Autowired
	private PersonaTipoRepository repositoryPersonaTipo;

	/**
	 * Listar todos los tipos de personas
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<PersonaTipo> listar() throws SigmaException {
		try {
			List<PersonaTipo> personaTipos = repositoryPersonaTipo.findAll();
			return personaTipos;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar personas", e);
		}
	}
}

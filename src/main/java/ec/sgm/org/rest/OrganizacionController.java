package ec.sgm.org.rest;

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
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.OrganizacionRepository;

@RestController
@RequestMapping("/organizacion")
public class OrganizacionController {
	private static final Logger LOGGER = LogManager.getLogger(OrganizacionController.class);
	@Autowired
	private OrganizacionRepository repository;

	/**
	 * Lista las organizaciones
	 * 
	 * @return
	 */
	@GetMapping
	public List<Organizacion> listar() throws SigmaException {
		try {
			return repository.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error listar las organizaciones", e);
		}
	}

	/**
	 * Ingresa o actualiza una organización
	 * 
	 * @param registro
	 * @return
	 */
	@PostMapping
	public HashMap<String, String> grabar(@RequestBody Organizacion registro) throws SigmaException {
		try {
			repository.save(registro);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al registrar la organización", e);
		}
	}

	/**
	 * Elimina la organización
	 * 
	 * @param id
	 * @return
	 */
	@PostMapping(value = "/{id}")
	public HashMap<String, String> eliminar(@PathVariable("id") String id) throws SigmaException {
		try {
			repository.deleteById(id);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar la organización", e);
		}
	}
}

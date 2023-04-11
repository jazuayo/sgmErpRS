package ec.sgm.org.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.repository.CategoriaRepository;
import ec.sgm.org.entity.Categoria;

@RestController
@RequestMapping("/origen")
public class OrigenController {
	private static final Logger LOGGER = LogManager.getLogger(OrigenController.class);
	@Autowired
	private CategoriaRepository repository;

	/**
	 * Lista los origenes
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<Categoria> listar() throws SigmaException {
		try {
			return repository.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error listar los origenes", e);
		}
	}

}

package ec.sgm.org.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.repository.CategoriaTipoRepository;
import ec.sgm.org.entity.CategoriaTipo;

@RestController
@RequestMapping("/categoriaTipo")
public class CategoriaTipoController {
	private static final Logger LOGGER = LogManager.getLogger(CategoriaTipoController.class);
	@Autowired
	private CategoriaTipoRepository repository;

	/**
	 * Lista los tipos de categorias
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<CategoriaTipo> listar() throws SigmaException {
		try {
			return repository.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error listar los tipos de categorías.", e);
		}
	}
}

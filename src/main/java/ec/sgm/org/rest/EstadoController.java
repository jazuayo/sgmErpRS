package ec.sgm.org.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.repository.EstadoRepository;

@RestController
@RequestMapping("/estado")
public class EstadoController {
	private static final Logger LOGGER = LogManager.getLogger(EstadoController.class);
	@Autowired
	private EstadoRepository repository;

	/**
	 * Lista los estados
	 * 
	 * @return
	 */
	@GetMapping
	public List<Estado> listar() throws SigmaException {
		try {
			return repository.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error listar los estados", e);
		}
	}

}

package ec.sgm.org.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.org.entity.Lugar;
import ec.sgm.org.repository.LugarRepository;

@RestController
@RequestMapping("/lugar")
public class LugarController {
	private static final Logger LOGGER = LogManager.getLogger(LugarController.class);
	@Autowired
	private LugarRepository repositoryLugar;

	@GetMapping
	public List<Lugar> listar() throws SigmaException {
		try {
			return repositoryLugar.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar los lugares", e);
		}
	}
}

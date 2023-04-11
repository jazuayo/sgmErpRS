package ec.sgm.org.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.org.entity.ImpuestoTipo;
import ec.sgm.org.repository.ImpuestoTipoRepository;

@RestController
@RequestMapping("/impuestoTipo")
public class ImpuestoTipoController {
	private static final Logger LOGGER = LogManager.getLogger(ImpuestoTipoController.class);
	@Autowired
	private ImpuestoTipoRepository repositoryImpuestoTipo;

	/**
	 * Listar todos los tipos de impuestos.
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<ImpuestoTipo> listar() throws SigmaException {
		try {
			return repositoryImpuestoTipo.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error listar los tipos de items.", e);
		}
	}
}

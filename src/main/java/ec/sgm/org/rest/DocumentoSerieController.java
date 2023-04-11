package ec.sgm.org.rest;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.DocumentoSerieRepository;

@RestController
@RequestMapping("/documentoSerie")
public class DocumentoSerieController {
	private static final Logger LOGGER = LogManager.getLogger(DocumentoSerieController.class);
	@Autowired
	private DocumentoSerieRepository repository;

	/**
	 * Eliminar la serie del documento
	 * 
	 * @param id
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{id}")
	public HashMap<String, String> eliminar(@PathVariable("id") Long id) throws SigmaException {
		try {
			repository.deleteById(id);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar la serie por documento.", e);
		}
	}

}

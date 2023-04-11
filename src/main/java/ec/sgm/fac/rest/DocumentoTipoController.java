package ec.sgm.fac.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.entity.DocumentoTipo;
import ec.sgm.fac.repository.DocumentoTipoRepository;

@RestController
@RequestMapping("/documentoTipo")
public class DocumentoTipoController {
	private static final Logger LOGGER = LogManager.getLogger(DocumentoTipoController.class);
	@Autowired
	private DocumentoTipoRepository repositoryDocumentoTipo;

	/**
	 * Listar todos los tipos de documentos
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<DocumentoTipo> listar() throws SigmaException {
		try {
			return repositoryDocumentoTipo.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException(e.getMessage(), e);
		}
	}
}

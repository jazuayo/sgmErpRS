package ec.sgm.fac.rest;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.fac.service.NotificaDocumentos;
import ec.sgm.org.model.MensajeResponse;

@RestController
@RequestMapping("/notifica")
public class NotificaController {
	@Autowired
	private NotificaDocumentos envioComprobante;
	@Autowired
	private FacturaRepository repositoryFactura;

	@GetMapping(value = "/factura/{documentoId}")
	public HashMap<String, String> notificaFactura(@PathVariable("documentoId") Long documentoId)
			throws SigmaException {
		envioComprobante.notificaEmailDocumentosFac(repositoryFactura.findById(documentoId).orElse(null));
		return MensajeResponse.ok();
	}
}

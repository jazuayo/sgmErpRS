package ec.sgm.cta.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.cta.entity.ComprobanteTmp;
import ec.sgm.cta.repository.ComprobanteAutCabRepository;
import ec.sgm.cta.service.ComprobanteTmpService;
import ec.sgm.org.model.MensajeResponse;

@RestController
@RequestMapping("/comprobanteTemp")
public class ComprobanteTempController {
	private static final Logger LOGGER = LogManager.getLogger(ComprobanteTempController.class);
	@Autowired
	private ComprobanteTmpService comprobanteTmpService;
	@Autowired
	private ComprobanteAutCabRepository comprobanteAutCabRepository;

	@PostMapping("/agregarTmp")
	public HashMap<String, String> agregarTmp(@RequestBody List<ComprobanteTmp> comprobantes) throws Exception {

		if (comprobantes == null) {
			String mensaje = "No existen datos a procesar";
			LOGGER.error(mensaje);
			throw new SigmaException(mensaje);
		}
		if (comprobantes.size() == 0) {
			String mensaje = "No existen comprobantes a procesar";
			LOGGER.error(mensaje);
			throw new SigmaException(mensaje);
		}
		String organizacionCod = comprobantes.get(0).getOrganizacionCod();
		Date fecha = comprobantes.get(0).getFecha();
		if (!"AC".contentEquals(organizacionCod)) {
			String mensaje = "organizacionCod no registrada para migrar comprobantes:" + organizacionCod;
			LOGGER.error(mensaje);
			throw new SigmaException(mensaje);
		}
		if (comprobantes.size() == 1) {
			String comprobanteCod = comprobantes.get(0).getComprobanteCod();
			if ("ELIMINAR".contentEquals(comprobanteCod)) {
				comprobanteTmpService.eliminaMigrados(organizacionCod, fecha);
				return MensajeResponse.ok();
			}
		}
		comprobanteTmpService.pctaSyncValidaReferencias(comprobantes);

		int registros = comprobanteTmpService.grabar(comprobantes);
		LOGGER.info(registros + " registros temporales grabados.");

		comprobanteTmpService.pctaSyncComprobanteFinaliza(organizacionCod, fecha);
		return MensajeResponse.ok();
	}

}

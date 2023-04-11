package ec.sgm.fac.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.modelo.AtsReq;
import ec.sgm.fac.service.AtsService;

/**
 * Generar ATS
 * 
 * @author CT
 *
 */
@RestController
@RequestMapping("/ATS")
public class ATSController {
	private static final Logger LOGGER = LogManager.getLogger(ATSController.class);
	@Autowired
	private AtsService ats;

	/**
	 * Generar ATS
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] ATS(@RequestBody AtsReq registro) throws SigmaException {
		try {
			return ats.generaATS(registro.getFechaGenera(), registro.getOrganizacionCod());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error generar ATS.", e);
		}
	}
}

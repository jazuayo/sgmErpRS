package ec.sgm.org.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.org.entity.Parametro;
import ec.sgm.org.repository.ParametroRepository;

@Service
public class ParametroService {
	private static final Logger LOGGER = LogManager.getLogger(ParametroService.class);

	public final static String CTA_DOCUMENTO_UTILIZA = "ctaDocUti_";
	public final static String CTA_CUENTA_PARAMETRO = "ctaPrm_";

	@Autowired
	private ParametroRepository parametroRepository;

	public String findValorByClave(String clave, String organizacionCod) throws SigmaException {
		List<Parametro> parametros = parametroRepository.findByClaveAndOrganizacionCodOrderByParametroDes(clave,
				organizacionCod);

		for (Parametro parametro : parametros) {
			if (parametro == null) {
				LOGGER.error("Parametro nulo definido para la clave:" + clave + " organizacion:" + organizacionCod);
				throw new SigmaException(
						"Parametro nulo definido para la clave:" + clave + " organizacion:" + organizacionCod);
			}
			return parametro.getValor();
		}
		LOGGER.error("Parametro no definido para la clave:" + clave + " organizacion:" + organizacionCod);
		throw new SigmaException("Parametro no definido para la clave:" + clave + " organizacion:" + organizacionCod);
	}

	/**
	 * TODO temporal
	 * 
	 * @param clave
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	public String findValorByClaveOpcional(String clave, String organizacionCod) throws SigmaException {
		List<Parametro> parametros = parametroRepository.findByClaveAndOrganizacionCodOrderByParametroDes(clave,
				organizacionCod);

		for (Parametro parametro : parametros) {
			if (parametro != null && parametro.getValor() != null && !parametro.getValor().isEmpty()) {
				return parametro.getValor();
			}
		}
		return null;
	}

	public String ctaDocumentoUtiliza(String modulo, String organizacionCod) throws SigmaException {
		return findValorByClave(CTA_DOCUMENTO_UTILIZA + modulo, organizacionCod);
	}

	public String ctaCuentaParametrizada(String clave, String organizacionCod) throws SigmaException {
		return findValorByClave(CTA_CUENTA_PARAMETRO + clave, organizacionCod);
	}
}
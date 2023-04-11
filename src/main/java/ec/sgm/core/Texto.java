package ec.sgm.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.sgm.SigmaException;

/**
 *
 * @author Marco
 */
public class Texto {
	private static final Logger LOGGER = LogManager.getLogger(Texto.class);

	public static String cuentaFormato(String cuentaCod) throws SigmaException {
		try {
			if (cuentaCod.indexOf("_") != -1) {
				return cuentaCod.split("_")[1];
			} else {
				return cuentaCod;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar formato de cuenta para reporte.", e);
		}
	}

	public static String lpad(String texto, int longitud, String relleno) {
		if (texto.length() >= longitud) {
			return texto;
		}
		StringBuilder sb = new StringBuilder();
		while (sb.length() < longitud - texto.length()) {
			sb.append(relleno);
		}
		sb.append(texto);
		return sb.toString();
	}

	public static String lpad(Integer numero, int longitud, String relleno) {
		return lpad(String.valueOf(numero).trim(), longitud, relleno);
	}

	public static String rpad(String texto, int longitud, String relleno) {
		if (texto.length() >= longitud) {
			return texto;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(texto);
		while (sb.length() < longitud) {
			sb.append(relleno);
		}
		return sb.toString();
	}

	public static String rpad(Integer numero, int longitud, String relleno) {
		return rpad(String.valueOf(numero).trim(), longitud, relleno);
	}
}

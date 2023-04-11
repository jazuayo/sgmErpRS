package ec.sgm.core;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import ec.sgm.SigmaException;

public class SgmRequest {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> obtieneMap(HttpServletRequest request) throws SigmaException {
		try {
			return new ObjectMapper().readValue(request.getInputStream(), Map.class);
		} catch (IOException e) {
			throw new SigmaException("Error al recuperar el grupo de datos en el request", e);
		}
	}

	public static Object obtieneValor(Map<String, Object> peticion, String clave) throws SigmaException {
		Object valor = peticion.get(clave);
		if (valor == null) {
			throw new SigmaException("No se ha definido el valor para la clave:" + clave + " en el request");
		}
		return valor;
	}

	public static String obtieneValorString(Map<String, Object> peticion, String clave) throws SigmaException {
		return (String) SgmRequest.obtieneValor(peticion, clave);
	}

	public static Long obtieneValorLong(Map<String, Object> peticion, String clave) throws SigmaException {
		Object valor = SgmRequest.obtieneValor(peticion, clave);
		try {
			return Long.valueOf((int) valor);
		} catch (Exception e) {
			throw new SigmaException(
					"El tipo de dato no es Long para la clave:" + clave + " en el request => " + e.getMessage(), e);
		}
	}

	public static Date obtieneValorDate(Map<String, Object> peticion, String clave) throws SigmaException {
		Object valor = SgmRequest.obtieneValor(peticion, clave);
		try {
			return Fecha.stringToDate((String) valor);
		} catch (Exception e) {
			throw new SigmaException("El tipo de dato no es Date(dd/MM/yyyy) para la clave:" + clave
					+ " en el request => " + e.getMessage(), e);
		}
	}
}

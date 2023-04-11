package ec.sgm.org.model;

import java.util.HashMap;

import lombok.Data;

@Data
public class MensajeResponse {

	public static HashMap<String, String> ok() {
		HashMap<String, String> respuesta = new HashMap<>();
		respuesta.put("respuesta", "OK");
		return respuesta;
	}

	public static HashMap<String, String> mensaje(String mensaje) {
		HashMap<String, String> respuesta = new HashMap<>();
		respuesta.put("respuesta", "OK");
		respuesta.put("mensaje", mensaje);
		return respuesta;
	}

	public static HashMap<String, String> error(String mensaje) {
		HashMap<String, String> respuesta = new HashMap<>();
		respuesta.put("respuesta", "ERR");
		respuesta.put("mensaje", mensaje);
		return respuesta;
	}
}

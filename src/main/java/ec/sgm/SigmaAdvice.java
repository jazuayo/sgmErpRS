package ec.sgm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SigmaAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(SigmaException.class)
	public ResponseEntity<Map<String, Object>> handleSigmaException(SigmaException sigmaException) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		respuesta.put("message", sigmaException.getMessage());
		respuesta.put("mensajeUsuario", sigmaException.getMensajeUsuario());
		return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

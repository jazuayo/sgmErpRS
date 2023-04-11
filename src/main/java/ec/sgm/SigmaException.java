package ec.sgm;

/**
 * Exception personalizada
 */

public class SigmaException extends Exception {
	private static final long serialVersionUID = 1L;
	private String mensajeUsuario;

	public SigmaException(String errorMessage) {
		super(errorMessage);
	}

	public SigmaException(String errorMessage, String mensajeUsuario) {
		super(errorMessage);
		this.setMensajeUsuario(mensajeUsuario);
	}

	public SigmaException(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}

	public boolean isMensajeUsuario() {
		if (mensajeUsuario == null)
			return false;
		if (mensajeUsuario.trim().length() < 0)
			return false;
		return true;
	}

	public String getMensajeUsuario() {
		return mensajeUsuario;
	}

	public void setMensajeUsuario(String mensajeUsuario) {
		this.mensajeUsuario = mensajeUsuario;
	}

}

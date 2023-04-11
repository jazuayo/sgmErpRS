package ec.sgm.ce.util;

import java.util.ArrayList;
import java.util.List;

public class ServicioWebResponse {
	private Boolean error = Boolean.FALSE;
	private List<String> errorDetalles = new ArrayList<>();
	private Integer codigo;
	private String respuesta;
	private String estado;

	public void agregaError(String errorDetalle) {
		error = Boolean.TRUE;
		errorDetalles.add(errorDetalle);
	}

	public String getErrores() {
		String errores = "";
		int numero = 0;
		for (String errorDetalle : errorDetalles) {
			numero++;
			if (numero == 1)
				errores = errorDetalle;
			else
				errores = errores + "\n" + errorDetalle;
		}
		if (errores.length() > 0)
			errores = errores + "\n";
		errores = "Codigo de respuesta:" + codigo;
		return errores;
	}

	public Boolean getError() {
		return error;
	}

	public void setError(Boolean error) {
		this.error = error;
	}

	public List<String> getErrorDetalles() {
		return errorDetalles;
	}

	public void setErrorDetalles(List<String> errorDetalles) {
		this.errorDetalles = errorDetalles;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}

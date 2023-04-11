package ec.sgm.ce.modelo;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class ClaveAcceso {
	private String fechaEmision;
	private String tipoComprobante;
	private String numeroRuc;
	private String tipoAmbiente;
	private String serie;
	private String secuencial;
	private String codigoNumerico;
	private String tipoEmision = "1";
	private String digitoVerificador;

	public void setFechaEmision(Date fechaEmision) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		this.fechaEmision = format.format(fechaEmision);
	}

	public void setDocumentoNumero(String documentoNumero) {
		this.serie = documentoNumero.substring(0, 3) + documentoNumero.substring(4, 7);
		this.secuencial = documentoNumero.substring(8);
	}
}

package ec.sgm.cta.modelo;

import java.util.Date;

import lombok.Data;

@Data
public class ReportesReq {
	private String tipoRepCod;
	private String cuentaIniCod;
	private String cuentaFinCod;
	private String nivel;
	private Date fechaDesde;
	private Date fechaHasta;
	private String organizacionCod;
	private String usuarioCod;
}

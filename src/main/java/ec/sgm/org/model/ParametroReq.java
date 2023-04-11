package ec.sgm.org.model;

import java.util.Date;

import lombok.Data;
@Data
public class ParametroReq {
	private Integer parametroId;
	private String parametroDes;
	private String clave;
	private String valor;
	private Date fechaDesde;
	private Date fechaHasta;
	private String organizacionCod;
}

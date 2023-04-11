package ec.sgm.cta.modelo;

import java.util.Date;

import lombok.Data;

/**
 * 
 * @author SIGMA - TL
 *
 */
@Data
public class ComprobanteConsultaReq {
	private String organizacionCod;
	private Date fechaDesde;
	private Date fechaHasta;
	private String comprobanteCod;
	private String concepto;	
}

package ec.sgm.fac.modelo;

import java.util.Date;

import lombok.Data;

/**
 * 
 * @author CT
 *
 */
@Data
public class FacturaConsultaReq {
	private String organizacionCod;
	private Date fechaDesde;
	private Date fechaHasta;
	private String personaNombre;
	private String categoriaCod;
}

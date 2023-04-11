package ec.sgm.cta.modelo;

import java.time.LocalDate;


import lombok.Data;

/**
 * 
 * @author SIGMA - TL
 *
 */
@Data
public class PlanCuentaReq {

	private String cuentaCod;
	private String cuentaNum;
	private String cuentaDes;
	private String observaciones;
	private Boolean movimiento;
	private Integer nivel;
	private LocalDate fechaDesde;
	private LocalDate fechaHasta;
	private Boolean operativa;
	private String cuentaCodPad;
	private String cuentaTipoCod;
	private String organizacionCod;
	private String usuario;
}

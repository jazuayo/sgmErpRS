package ec.sgm.fac.modelo;

import java.util.Date;

import lombok.Data;

@Data
public class ReporteFacturaReq {
	private String orgCod;
	private String categoriaCod;
	private Date fechaDesde;
	private Date fechaHasta;
	private Long itemId;
	private String formatoReporte;
}

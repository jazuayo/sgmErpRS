package ec.sgm.fac.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ec.sgm.fac.entity.RetencionDetalle;
import lombok.Data;

@Data
public class RetencionReq {
	private Long retencionId;
	private String organizacionCod;
	private Date fechaEmite;
	private String documentoCod;
	private String documentoNumero;
	private String autorizacionNumero;
	private Date autorizacionFecha;
	private String estadoCod;
	private List<RetencionDetalle> detalles = new ArrayList<RetencionDetalle>();
	private String usuario = ".";
}

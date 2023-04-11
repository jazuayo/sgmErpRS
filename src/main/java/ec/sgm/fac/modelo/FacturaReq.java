package ec.sgm.fac.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ec.sgm.fac.entity.FacturaDetalle;
import ec.sgm.fac.entity.FacturaImpuesto;
import lombok.Data;

@Data
public class FacturaReq {
	private Long documentoId;
	private String categoriaCod;
	private String organizacionCod;
	private Long personaId;
	private Date fechaEmite;
	private String documentoCod;
	private String documentoTipoCod;
	private String documentoNumero;
	private Double documentoValor;
	private String autorizacionNumero;
	private Date autorizacionFecha;
	private String estadoCod;
	private String observaciones;
	private Double saldoPendiente;
	private String formaPagoCod;
	private Long plazoDias;

	private Set<FacturaDetalle> detalles = new HashSet<FacturaDetalle>();
	private List<FacturaImpuesto> impuestos = new ArrayList<FacturaImpuesto>();

	private Long facturaModifica;
	private String usuario;
}

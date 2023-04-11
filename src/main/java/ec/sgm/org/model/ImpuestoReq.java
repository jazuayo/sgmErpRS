package ec.sgm.org.model;

import java.util.List;

import ec.sgm.org.entity.ImpuestoCategoria;
import lombok.Data;

@Data
public class ImpuestoReq {
	private String impuestoCod;
	private String impuestoDes;
	private String impuestoTipoCod;
	private Double porcentaje;
	private String porcentajeSri;
	private List<ImpuestoCategoria> categorias;
}

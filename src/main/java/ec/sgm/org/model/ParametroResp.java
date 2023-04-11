package ec.sgm.org.model;


import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Data
public class ParametroResp {
	private Integer parametroId;
	private String parametroDes;
	private String clave;
	private String valor;
	private String fechaDesde;
	private String fechaHasta;
	private Organizacion organizacion;
}

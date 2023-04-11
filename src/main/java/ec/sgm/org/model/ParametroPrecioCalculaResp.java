package ec.sgm.org.model;

import lombok.Data;

@Data
public class ParametroPrecioCalculaResp {
	private Integer parametroId;
	private String parametroDes;
	private String clave;
	private String valor;
	private Double valorNum;
}

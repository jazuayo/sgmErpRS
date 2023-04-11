package ec.sgm.org.model;

import lombok.Data;

@Data
public class CategoriaReq {
	private String categoriaCod;
	private String categoriaDes;
	private String categoriaTipoCod;
	private String organizacionCod;
	private String usuario;
	private Long catOrgId;
}

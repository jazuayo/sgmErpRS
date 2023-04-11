package ec.sgm.org.model;

import lombok.Data;

@Data
public class CategoriaResp {
	private String categoriaCod;
	private String categoriaDes;
	private Long catOrgId;
	private String categoriaTipoDes;
	private String categoriaTipoCod;
	private String organizacionDes;
	private String organizacionCod;
}

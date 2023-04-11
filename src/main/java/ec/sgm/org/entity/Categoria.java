package ec.sgm.org.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "ORG_CATEGORIA")
@Data
public class Categoria {
	@Id
	@NotEmpty
	private String categoriaCod;
	@NotEmpty
	private String categoriaDes;
	@ManyToOne
	@NotNull
	@JoinColumn(name = "CATEGORIA_TIPO_COD", referencedColumnName = "categoriaTipoCod")
	private CategoriaTipo categoriaTipo;
	private String usuario = ".";

}

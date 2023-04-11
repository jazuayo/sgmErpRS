package ec.sgm.org.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 *
 * @author sigma
 */
@Entity
@Table(name = "ORG_CATEGORIA_TIPO")
@Data
public class CategoriaTipo {
	@Id
	@NotNull
	@Size(min = 1, max = 30)
	private String categoriaTipoCod;
	@NotEmpty
	private String categoriaTipoDes;

}

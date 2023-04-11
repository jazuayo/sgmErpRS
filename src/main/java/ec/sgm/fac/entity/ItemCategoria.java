package ec.sgm.fac.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Entity
@Table(name = "FAC_ITEM_CATEGORIA")
@Data
public class ItemCategoria {
	@Id
	@SequenceGenerator(name = "FAC_ITEM_CATEGORIASQ", sequenceName = "FAC_ITEM_CATEGORIASQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FAC_ITEM_CATEGORIASQ")
	private Long itemCategoriaId;
	@NotNull
	private Long itemId;
	@NotNull
	@JoinColumn(name = "CATEGORIA_COD", referencedColumnName = "CATEGORIACOD")
	@ManyToOne
	private Categoria categoria;
	@NotNull
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne
	private Organizacion organizacion;
	@NotNull
	@JoinColumn(name = "CUENTA_COD", referencedColumnName = "CUENTA_COD")
	@ManyToOne(optional = false)
	private PlanCuenta cuenta;
	@NotEmpty
	private String usuario = ".";

}

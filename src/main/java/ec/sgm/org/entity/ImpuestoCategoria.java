package ec.sgm.org.entity;

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
import lombok.Data;

@Entity
@Table(name = "ORG_IMPUESTO_CATEGORIA")
@Data
public class ImpuestoCategoria {
	@Id
	@SequenceGenerator(name = "ORG_IMPUESTO_CATEGORIASQ", sequenceName = "ORG_IMPUESTO_CATEGORIASQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORG_IMPUESTO_CATEGORIASQ")
	private Long impuestoCategoriaId;
	@NotEmpty
	private String impuestoCod;
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

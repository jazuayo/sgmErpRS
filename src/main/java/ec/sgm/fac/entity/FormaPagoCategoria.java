package ec.sgm.fac.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Entity
@Table(name = "FAC_FORMA_PAGO_CAT")
@Data
public class FormaPagoCategoria {
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "FAC_FORMAS_PAGOQ", name = "FAC_FORMAS_PAGOQ")
	@GeneratedValue(generator = "FAC_FORMAS_PAGOQ", strategy = GenerationType.SEQUENCE)
	private Long formaPagoCatId;

	@JoinColumn(name = "FORMA_PAGO_COD", referencedColumnName = "FORMAPAGOCOD")
	@ManyToOne(optional = false)
	private FormaPago formaPagoCod;

	@JoinColumn(name = "ORIGEN_COD", referencedColumnName = "CATEGORIACOD")
	@ManyToOne(optional = false)
	private Categoria origen = new Categoria();

	@JoinColumn(name = "CUENTA_COD", referencedColumnName = "CUENTA_COD")
	@ManyToOne(optional = false)
	private PlanCuenta cuentaCod;
	@NotNull
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;
}

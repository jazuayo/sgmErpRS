package ec.sgm.fac.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Entity
@Table(name = "FAC_FORMA_PAGO")
@Data
public class FormaPago {
	@Id
	@NotEmpty
	private String formaPagoCod;
	@NotEmpty
	private String formaPagoDes;
	@NotEmpty
	private String formaPagoSri;
	@NotNull
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;

}

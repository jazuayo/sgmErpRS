package ec.sgm.cta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "CTA_PLAN_CUENTA_TIPO")
@Data
public class PlanCuentaTipo {
	@Id
	@NotEmpty
	@Column(name = "CUENTA_TIPO_COD")
	private String cuentaTipoCod;
	@NotEmpty
	@Column(name = "CUENTA_TIPO_DES")
	private String cuentaTipoDes;
	@NotNull
	@Column(name = "ORDEN")
	private Integer orden;
	@NotNull
	@Column(name = "SIGNO")
	private Integer signo;
}

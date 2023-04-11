package ec.sgm.cta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Entity
@Table(name = "CTA_PLAN_CUENTA_TIPO_ORG")
@Data
public class PlanCuentaTipoOrg {
	@Id
	@Column(name = "ID")
	private Long id;
	@Column(name = "CUENTA_TIPO_COD")
	private String cuentaTipoCod;
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;
	@Column(name = "CUENTA_COD")
	private String cuentaCod;
	@Column(name = "SIGNO")
	private Integer signo;
	@Column(name = "ORDEN")
	private Integer orden;

}

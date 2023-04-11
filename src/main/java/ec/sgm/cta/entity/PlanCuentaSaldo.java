package ec.sgm.cta.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "CTA_PLAN_CUENTA_SALDO")
@Data
public class PlanCuentaSaldo {
	@Id
	@Column(name = "id")
	private Long id;
	@Column(name = "origen")
	private String origen;
	@Column(name = "nivel")
	private Integer nivel;
	@Column(name = "CUENTA_COD")
	private String cuentaCod;
	@Column(name = "DEBITO_INICIO")
	private Double debitoInicio = 0.0;
	@Column(name = "DEBITO_PERIODO")
	private Double debitoPeriodo = 0.0;
	@Column(name = "DEBITO_SALDO")
	private Double debitoSaldo = 0.0;
	@Column(name = "CREDITO_INICIO")
	private Double creditoInicio = 0.0;
	@Column(name = "CREDITO_PERIODO")
	private Double creditoPeriodo = 0.0;
	@Column(name = "CREDITO_SALDO")
	private Double creditoSaldo = 0.0;
}

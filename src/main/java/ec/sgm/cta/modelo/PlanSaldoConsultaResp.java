package ec.sgm.cta.modelo;

import lombok.Data;

/**
 * 
 * @author CT Sigma
 *
 */
@Data
public class PlanSaldoConsultaResp {

	private String organizacion;
	private String cuenta;
	private Double debitoInicial = 0.0;
	private Double creditoInicial = 0.0;
	private Double debitos = 0.0;
	private Double credito = 0.0;

	public PlanSaldoConsultaResp(String organizacion, String cuenta, Double debitoInicial, Double creditoInicial,
			Double debitos, Double credito) {
		this.organizacion = organizacion;
		this.cuenta = cuenta;
		this.debitoInicial = debitoInicial;
		this.creditoInicial = creditoInicial;
		this.debitos = debitos;
		this.credito = credito;
	}

	public PlanSaldoConsultaResp() {
		super();
	}
}
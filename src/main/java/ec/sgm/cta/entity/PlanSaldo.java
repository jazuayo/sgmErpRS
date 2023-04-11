package ec.sgm.cta.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import lombok.Data;

/**
 * 
 * @author CT Sigma
 *
 */
@Entity
@Table(name = "CAL_CTA_PLAN_SALDO")
@Data
public class PlanSaldo {
	@Id
	@Column(name = "PLAN_SALDO_ID")
	private Long planSaldoId;
	@Column(name = "ETAPA")
	private Integer etapa;
	@Column(name = "ORGANIZACION_COD")
	private String organizacionCod;
	@Column(name = "CUENTA_COD")
	private String cuentaCod;
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	@Column(name = "FECHA")
	private Date fecha;
	@Column(name = "DEBITO_INICIAL")
	private Double debitoInicial;
	@Column(name = "CREDITO_INICIAL")
	private Double creditoInicial;
	@Column(name = "SALDO_INICIAL")
	private Double saldoInicial;
	@Column(name = "DEBITOS")
	private Double debitos;
	@Column(name = "CREDITOS")
	private Double creditos;
	@Column(name = "SALDO")
	private Double saldo;
	@Column(name = "CUENTA_FORMATO")
	private String cuentaFormato;
	@Column(name = "CUENTA_DES")
	private String cuentaDes;
	@Column(name = "NIVEL")
	private Integer nivel;
	@Column(name = "CUENTA_TIPO_COD")
	private String cuentaTipoCod;
	@Column(name = "USUARIO_COD")
	private String usuarioCod;
	@Column(name = "CUENTA_TIPO_DES")
	private String cuentaTipoDes;
	@Column(name = "ORDEN")
	private Integer orden;

}

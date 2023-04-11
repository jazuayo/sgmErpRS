package ec.sgm.cta.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "CTA_COMPROBANTE_CUENTA")
@Data
public class ComprobanteCuenta {

	@Id
	@SequenceGenerator(name = "ctaComprobanteCuentaSq", sequenceName = "CTA_COMPROBANTE_CUENTASQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ctaComprobanteCuentaSq")
	@Column(name = "COMPB_CTA_ID")
	private Long idReg;
	@Column(name = "LINEA")
	private Integer linea = 0;
	@Column(name = "DEBITO")
	private BigDecimal debito = new BigDecimal("0");
	@Column(name = "CREDITO")
	private BigDecimal credito = new BigDecimal("0");
	@Column(name = "CONCEPTO")
	private String concepto;
//	private String comprobanteCod;
	@JoinColumn(name = "COMPROBANTE_COD", referencedColumnName = "comprobanteCod")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JsonBackReference
	private Comprobante comprobante;

	@JoinColumn(name = "CUENTA_COD", referencedColumnName = "CUENTA_COD")
	@ManyToOne(optional = false)
	@NotNull
	private PlanCuenta cuenta;
	@Column(name = "DOCUMENTO_ID")
	private Long documentoId;
	@Column(name = "PERSONA_ID")
	private Long personaId;
	@Column(name = "CENTRO_COD")
	private String centroCod = null;

	@Transient
	private int idRelacion;

	public ComprobanteCuenta() {
	}

	public ComprobanteCuenta(Long idReg) {
		this.idReg = idReg;
	}

	public ComprobanteCuenta(Long idReg, Integer linea, BigDecimal debito, BigDecimal credito) {
		this.idReg = idReg;
		this.linea = linea;
		this.debito = debito;
		this.credito = credito;
	}

}

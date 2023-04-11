package ec.sgm.cta.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "CTA_COMPROBANTE_TMP_CUENTA")
@Data
@NoArgsConstructor
public class ComprobanteTmpCuenta {
	@Id
	@SequenceGenerator(name = "ctaComprobanteTmpCuentaSq", sequenceName = "CTA_COMPROBANTE_TMP_CUENTASQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ctaComprobanteTmpCuentaSq")
	@Column(name = "COMPB_CTA_ID")
	private Long idReg;

	@JoinColumn(name = "COMPROBANTE_COD", referencedColumnName = "comprobanteCod")
	@ManyToOne(optional = false)
	@JsonBackReference
//	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private ComprobanteTmp comprobante;

	@Column(name = "LINEA")
	private Integer linea = 0;
	@Column(name = "DEBITO")
	private BigDecimal debito = new BigDecimal("0");
	@Column(name = "CREDITO")
	private BigDecimal credito = new BigDecimal("0");
	@Column(name = "CONCEPTO")
	private String concepto;
	@NotNull
	@Column(name = "CUENTA_COD")
	private String cuentaCod;
	@Column(name = "DOCUMENTO_ID")
	private Long documentoId;
	@Column(name = "PERSONA_ID")
	private Long personaId;
	@Column(name = "CENTRO_COD")
	private String centroCod = null;
}

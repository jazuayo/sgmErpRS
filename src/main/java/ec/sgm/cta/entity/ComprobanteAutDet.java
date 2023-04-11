package ec.sgm.cta.entity;

import java.math.BigDecimal;

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

import lombok.Data;

/**
 *
 * @author MP
 */
@Entity
@Table(name = "CTA_COMPROBANTE_AUT_DET")
@Data
public class ComprobanteAutDet {
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "CTA_COMPROBANTE_AUT_DETSQ", name = "CTA_COMPROBANTE_AUT_DETSQ")
	@GeneratedValue(generator = "CTA_COMPROBANTE_AUT_DETSQ", strategy = GenerationType.SEQUENCE)
	private Long comprobanteAutDetId;
	@JoinColumn(name = "COMPROBANTE_AUT_CAB_ID", referencedColumnName = "comprobanteAutCabId")
	@ManyToOne(optional = false)
	private ComprobanteAutCab cabecera;
	@NotEmpty
	private String cuentaCod;
	@NotNull
	private BigDecimal debito;
	@NotNull
	private BigDecimal credito;
	private String trxOrigenDet;
	private String cuentaGrupoCod;
	private Long linea;
	private String observacion;
	private String traza;
}

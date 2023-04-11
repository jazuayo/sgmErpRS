package ec.sgm.cta.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 *
 * @author MP
 */
@Entity
@Table(name = "CTA_COMPROBANTE_AUT_CAB")
@Data
public class ComprobanteAutCab {
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "CTA_COMPROBANTE_AUT_CABSQ", name = "CTA_COMPROBANTE_AUT_CABSQ")
	@GeneratedValue(generator = "CTA_COMPROBANTE_AUT_CABSQ", strategy = GenerationType.SEQUENCE)
	private Long comprobanteAutCabId;
	@NotEmpty
	private String organizacionCod;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaTrx;
	@NotNull
	private String trxOrigen;
	private String concepto;
	@NotNull
	private String comprobanteAutCod;
	@NotEmpty
	private String traza;
	@Temporal(TemporalType.TIMESTAMP)
	private Date registroFecha;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "cabecera")
	private List<ComprobanteAutDet> detalles = new ArrayList<ComprobanteAutDet>();

}

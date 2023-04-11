package ec.sgm.fac.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ec.sgm.org.entity.Impuesto;
import lombok.Data;

@Entity
@Table(name = "FAC_RETENCION_DETALLE")
@Data
public class RetencionDetalle implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "FAC_RETENCION_DETALLESQ", name = "FAC_RETENCION_DETALLESQ")
	@GeneratedValue(generator = "FAC_RETENCION_DETALLESQ", strategy = GenerationType.SEQUENCE)
	private Long retencionDetalleId;
	private Long retencionId;
	@JoinColumn(name = "FACTURA_ID", referencedColumnName = "documentoId")
	@ManyToOne(optional = false)
	@NotNull
	private Factura factura;
	@JoinColumn(name = "IMPUESTO_COD", referencedColumnName = "impuestoCod")
	@ManyToOne(optional = false)
	@NotNull
	private Impuesto impuesto;
	@NotNull
	private Double baseImponible;
	@NotNull
	private Double valorRetenido;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

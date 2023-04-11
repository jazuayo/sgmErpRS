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
@Table(name = "FAC_FACTURA_DETALLE_IMP")
@Data
public class FacturaDetalleImpuesto implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "FAC_FACTURA_DETALLE_IMPSQ", name = "FAC_FACTURA_DETALLE_IMPSQ")
	@GeneratedValue(generator = "FAC_FACTURA_DETALLE_IMPSQ", strategy = GenerationType.SEQUENCE)
	private Long facturaDetalleImpId;
	private Long facturaDetalleId;
	@JoinColumn(name = "IMPUESTO_COD", referencedColumnName = "impuestoCod")
	@ManyToOne(optional = false)
	@NotNull
	private Impuesto impuesto;
	@NotNull
	private Double baseImponible;
	@NotNull
	private Double impuestoValor;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

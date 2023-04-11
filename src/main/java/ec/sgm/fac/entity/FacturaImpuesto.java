package ec.sgm.fac.entity;

import java.io.Serializable;
import java.util.Set;

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
@Table(name = "FAC_FACTURA_IMPUESTO")
@Data
public class FacturaImpuesto implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "FAC_FACTURA_IMPUESTOSQ", name = "FAC_FACTURA_IMPUESTOSQ")
	@GeneratedValue(generator = "FAC_FACTURA_IMPUESTOSQ", strategy = GenerationType.SEQUENCE)
	private Long facturaImpuestoId;
	private Long documentoId;
	@JoinColumn(name = "IMPUESTO_COD", referencedColumnName = "impuestoCod")
	@ManyToOne(optional = false)
	@NotNull
	private Impuesto impuesto;
	@NotNull
	private Double baseImponible;
	@NotNull
	private Double porcentaje = 0.0;
	@NotNull
	private Double descuentoValor = 0.0;
	@NotNull
	private Double impuestoValor;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isConImpuesto() {
		if (porcentaje == null) {
			return false;
		}
		if (porcentaje == 0.0) {
			return false;
		}
		return true;
	}

	public static Double sumaBaseImponible(Set<FacturaImpuesto> facturaImpuestos, Boolean conImpuestos) {
		Double baseImponibleSuma = 0.0;
		for (FacturaImpuesto facturaImpuesto : facturaImpuestos) {
			Double baseImponibleValor = facturaImpuesto.getBaseImponible();
			if (conImpuestos == null) {
				baseImponibleSuma = baseImponibleSuma + baseImponibleValor;
			} else {
				if (facturaImpuesto.isConImpuesto() == conImpuestos.booleanValue()) {
					baseImponibleSuma = baseImponibleSuma + baseImponibleValor;
				}
			}
		}
		return baseImponibleSuma;
	}

	public static Double sumaImpuestoValor(Set<FacturaImpuesto> facturaImpuestos) {
		Double impuestoValorSuma = 0.0;

		for (FacturaImpuesto facturaImpuesto : facturaImpuestos) {
			Double impuestoValorValor = facturaImpuesto.getImpuestoValor();
			impuestoValorSuma = impuestoValorSuma + impuestoValorValor;
		}
		return impuestoValorSuma;
	}

}

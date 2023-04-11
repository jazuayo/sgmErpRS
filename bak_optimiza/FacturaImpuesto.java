package ec.sgm.fac.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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
	public static final String IVA = "IVA";
	public static final String BASE_TOTAL = "BASE_TOTAL";
	public static final String BASE_CON_IMPUESTOS = "BASE_CON_IMPUESTOS";
	public static final String BASE_SIN_IMPUESTOS = "BASE_SIN_IMPUESTOS";
	Double baseImponibleConImpuestoSuma = 0.0;
	Double baseImponibleSinImpuestoSuma = 0.0;

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

	public static Map<String, Double> calculaTotales(Set<FacturaImpuesto> facturaImpuestos) {
		Map<String, Double> valores = new HashMap<>();
		Double impuestoValorSuma = 0.0;

		Double baseImponibleTotalSuma = 0.0;
		Double baseImponibleConImpuestoSuma = 0.0;
		Double baseImponibleSinImpuestoSuma = 0.0;
		for (FacturaImpuesto facturaImpuesto : facturaImpuestos) {
			Double impuestoValorValor = facturaImpuesto.getImpuestoValor();
			impuestoValorSuma = impuestoValorSuma + impuestoValorValor;

			Double baseImponibleValor = facturaImpuesto.getBaseImponible();
			baseImponibleTotalSuma = baseImponibleTotalSuma + baseImponibleValor;
			if (facturaImpuesto.isConImpuesto()) {
				baseImponibleConImpuestoSuma = baseImponibleConImpuestoSuma + baseImponibleValor;
			} else {
				baseImponibleSinImpuestoSuma = baseImponibleSinImpuestoSuma + baseImponibleValor;
			}
		}

		valores.put(IVA, impuestoValorSuma);
		valores.put(BASE_TOTAL, baseImponibleTotalSuma);
		valores.put(BASE_CON_IMPUESTOS, baseImponibleConImpuestoSuma);
		valores.put(BASE_SIN_IMPUESTOS, baseImponibleSinImpuestoSuma);

		System.out.println(valores.get(IVA));
		System.out.println(valores.get(BASE_TOTAL));

		return valores;
	}
}

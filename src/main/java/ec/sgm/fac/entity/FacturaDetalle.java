package ec.sgm.fac.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import lombok.Data;

@Entity
@Table(name = "FAC_FACTURA_DETALLE")
@Data
public class FacturaDetalle implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "FAC_FACTURA_DETALLESQ", name = "FAC_FACTURA_DETALLESQ")
	@GeneratedValue(generator = "FAC_FACTURA_DETALLESQ", strategy = GenerationType.SEQUENCE)
	private Long facturaDetalleId;
	private Long documentoId;
	@JoinColumn(name = "ITEM_ID", referencedColumnName = "ITEMID")
	@ManyToOne(optional = false)
	@NotNull
	private Item item;
	@NotEmpty
	private String descripcion;
	@NotNull
	private Double cantidad;
	@NotNull
	private Double precioUnitario;
	@NotNull
	private Double descuentoValor = 0.0;
	private String lote;
	private String loteCod;
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fechaVence;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "facturaDetalleId")
	private List<FacturaDetalleImpuesto> impuestos = new ArrayList<FacturaDetalleImpuesto>();
	@Transient
	private Double pvp;

	public Double precioTotalSinImpuesto() {
		return (cantidad * precioUnitario) - descuentoValor;
	}
}

package ec.sgm.fac.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Entity
@Table(name = "FAC_FACTURA")
@Data
public class Factura implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "FAC_FACTURASQ", name = "FAC_FACTURASQ")
	@GeneratedValue(generator = "FAC_FACTURASQ", strategy = GenerationType.SEQUENCE)
	private Long documentoId;
	@JoinColumn(name = "ORIGEN_COD", referencedColumnName = "CATEGORIACOD")
	@ManyToOne(optional = false)
	@NotNull
	private Categoria origen = new Categoria();
	@NotNull
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;
	@NotNull
	@JoinColumn(name = "PERSONA_ID", referencedColumnName = "PERSONAID")
	@ManyToOne(optional = false)
	private Persona persona;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fechaEmite;
	@JoinColumn(name = "DOCUMENTO_COD", referencedColumnName = "DOCUMENTOCOD")
	@ManyToOne(optional = false)
	@NotNull
	private Documento documento;
	@JoinColumn(name = "DOCUMENTO_TIPO_COD", referencedColumnName = "DOCUMENTOTIPOCOD")
	@ManyToOne(optional = false)
	@NotNull
	private DocumentoTipo documentoTipo;
	private String documentoNumero;
	@NotNull
	private Double documentoValor = 0.0;
	@NotEmpty
	private String autorizacionNumero = "0000000000";
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	@NotNull
	private Date autorizacionFecha = new Date();
	@JoinColumn(name = "ESTADO_COD", referencedColumnName = "ESTADOCOD")
	@ManyToOne(optional = false)
	@NotNull
	private Estado estado;
	@NotEmpty
	private String observaciones = ".";
	@NotNull
	private Double saldoPendiente = 0.0;
	@JoinColumn(name = "FORMA_PAGO_COD", referencedColumnName = "FORMAPAGOCOD")
	@ManyToOne(optional = false)
	@NotNull
	private FormaPago formaPago;
	@NotNull
	private Long plazoDias;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "documentoId")
	private Set<FacturaDetalle> detalles = new HashSet<FacturaDetalle>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "documentoId")
	private Set<FacturaImpuesto> impuestos = new HashSet<FacturaImpuesto>();

	@JoinColumn(name = "FACTURA_ID_MODIFICA", referencedColumnName = "DOCUMENTOID")
	@ManyToOne(optional = true)
	private Factura facturaModifica;
	@NotEmpty
	private String usuario = ".";

	private String sriEstado = ".";
	private String sriNota = ".";

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Entity
@Table(name = "FAC_RETENCION")
@Data
public class Retencion implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "FAC_RETENCIONSQ", name = "FAC_RETENCIONSQ")
	@GeneratedValue(generator = "FAC_RETENCIONSQ", strategy = GenerationType.SEQUENCE)
	private Long retencionId;
	@NotNull
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fechaEmite;
	@JoinColumn(name = "DOCUMENTO_COD", referencedColumnName = "DOCUMENTOCOD")
	@ManyToOne(optional = false)
	@NotNull
	private Documento documento;
	@NotEmpty
	private String documentoNumero;
	@NotEmpty
	private String autorizacionNumero;
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	@NotNull
	private Date autorizacionFecha;
	@JoinColumn(name = "ESTADO_COD", referencedColumnName = "ESTADOCOD")
	@ManyToOne(optional = false)
	@NotNull
	private Estado estado;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "retencionId")
	private List<RetencionDetalle> detalles = new ArrayList<RetencionDetalle>();
	private String usuario = ".";

	private String sriEstado;
	private String sriNota;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

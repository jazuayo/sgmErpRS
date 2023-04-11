package ec.sgm.org.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "ORG_DOCUMENTO")
@Data
public class Documento {
	@Id
	private String documentoCod;
	@NotEmpty
	private String documentoDes;
	@NotNull
	private Long secuencial;
	@NotNull
	private Long longitud;
	private String inicio;
	private Long orden;
	@JoinColumn(name = "ESTADO_COD", referencedColumnName = "ESTADOCOD")
	@ManyToOne(optional = false)
	private Estado estado = new Estado();
	@JoinColumn(name = "ORIGEN_COD", referencedColumnName = "CATEGORIACOD")
	@ManyToOne(optional = false)
	private Categoria origen = new Categoria();// mul
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;// mul

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "documentoCod")
	private List<DocumentoSerie> series = new ArrayList<DocumentoSerie>();

	private String usuario = ".";
	private String sriAts;
	private String sriCe;

	private Boolean ce; // si doc lleva comprobante electronico.
	private Boolean inventario;// si el documento controla el aumento de inventario
}

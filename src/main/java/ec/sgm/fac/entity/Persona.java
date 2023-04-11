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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import ec.sgm.org.entity.Lugar;
import ec.sgm.org.entity.Organizacion;
import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity(name = "FAC_PERSONA")
@Table(name = "FAC_PERSONA")
@Data
public class Persona implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "FAC_PERSONASQ", name = "FAC_PERSONASQ")
	@GeneratedValue(generator = "FAC_PERSONASQ", strategy = GenerationType.SEQUENCE)
	private Long personaId;
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;
	@JoinColumn(name = "PER_TIPO_COD", referencedColumnName = "PERTIPOCOD")
	@ManyToOne(optional = false)
	private PersonaTipo personaTipo;
	@NotEmpty
	@Size(min = 3, max = 100)
	private String numeroId;
	@NotEmpty
	@Size(min = 3, max = 100)
	private String nombre;
	@NotEmpty
	private String direccion;
	@NotEmpty
	private String telefono;
	@Email
	private String email;
	private String usuario = ".";
	private Boolean esCliente;
	private Boolean esProveedor;
	private String siglasProveedor;
	@JoinColumn(name = "LUGAR_ID", referencedColumnName = "LUGARID")
	@ManyToOne(optional = false)
	private Lugar lugar;

}

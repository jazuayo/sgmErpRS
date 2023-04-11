package ec.sgm.org.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "ORG_USUARIO")
@Data
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@NotEmpty
	@Size(min = 3, max = 100)
	private String usuarioCod;
	@NotEmpty
	@Size(min = 3, max = 100)
	private String nombre;
	@NotEmpty
	private String clave;
	@NotEmpty
	private String email;
	@NotNull
	private Boolean activo = Boolean.TRUE;
	@JoinTable(name = "ORG_USUARIO_ROL", joinColumns = @JoinColumn(name = "USUARIO_COD", nullable = false), inverseJoinColumns = @JoinColumn(name = "ROL_COD", nullable = false))
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Rol> roles;
	@JoinTable(name = "ORG_USUARIO_ROL", joinColumns = @JoinColumn(name = "USUARIO_COD", nullable = false), inverseJoinColumns = @JoinColumn(name = "ORGANIZACION_COD", nullable = false))
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Organizacion> organizaciones;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

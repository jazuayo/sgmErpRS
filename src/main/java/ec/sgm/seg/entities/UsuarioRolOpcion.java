package ec.sgm.seg.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ec.sgm.org.entity.Rol;
import lombok.Data;

/**
 * 
 * @author SIGMA - TL
 *
 */
@Entity
@Table(name = "USUARIO_ROL_OPCION")
@Data
public class UsuarioRolOpcion implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@NotNull
	@Column(name = "ROL_OPCION_ID")
	private Long rolOpcionId;
	@JoinColumn(name = "opcionId", referencedColumnName = "OPCION_ID")
	@ManyToOne(optional = true)
	private UsuarioOpcion opcion;
	@JoinColumn(name = "rolId", referencedColumnName = "rolCod")
	@ManyToOne(optional = true)
	private Rol rol;
}

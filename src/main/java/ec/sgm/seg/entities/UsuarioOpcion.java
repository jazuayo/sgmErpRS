package ec.sgm.seg.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 
 * @author SIGMA - TL
 *
 */
@Entity
@Table(name = "USUARIO_OPCION")
@Data
public class UsuarioOpcion implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@NotNull
	@Column(name = "OPCION_ID")
	private Long opcionId;
	@NotNull
	@Column(name = "MENU_COD")
	private String menuCod;
	@NotNull
	@Column(name = "OPCION_COD")
	private String opcionCod;
}

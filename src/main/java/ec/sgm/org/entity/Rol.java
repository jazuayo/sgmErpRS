package ec.sgm.org.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "ORG_ROL")
@Data
public class Rol implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@NotEmpty
	private String rolCod;
	@NotEmpty
	private String rolDes;

}

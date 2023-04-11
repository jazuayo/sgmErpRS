package ec.sgm.org.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "ORG_ORGANIZACION")
@Data
public class Organizacion {
	@Id
	private String organizacionCod;
	private String organizacionDes;

}

package ec.sgm.org.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ORG_ESTADO")
@Data
public class Estado {
	@Id
	private String estadoCod;
	private String estadoDes;

}

package ec.sgm.org.entity;

import javax.persistence.Table;

import lombok.Data;

@Table(name = "ORG_PROPIEDAD")
@Data
public class Propiedad {
	private String propiedadCod;
	private String propiedadDes;

}

package ec.sgm.org.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ORG_IMPUESTO_TIPO")
@Data
public class ImpuestoTipo {
	@Id
	private String impuestoTipoCod;
	private String impuestoTipoDes;
	private String impuestoTipoSri;

}

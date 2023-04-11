package ec.sgm.fac.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "FAC_PERSONA_TIPO")
@Data
public class PersonaTipo {
	@Id
	private String perTipoCod;
	private String perTipoDes;
	@JoinColumn(name = "TIPO_ID_COD", referencedColumnName = "TIPOIDCOD")
	@ManyToOne(optional = false)
	private TipoId tipoId;
	private Boolean ivaCompra;
	private Boolean ivaVenta;
}

package ec.sgm.cta.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "CTA_COMPROBANTE_AUTOMATICO")
@Data
public class ComprobanteAutomatico {
	@Id
	@NotEmpty
	private String comprobanteAutCod;
	@NotEmpty
	private String comprobanteAutDes;
	@NotEmpty
	private String concepto;
	@NotEmpty
	private String moduloCod;
	@NotEmpty
	private String documentoCod;
}

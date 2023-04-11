package ec.sgm.fac.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "FAC_DOCUMENTO_TIPO")
@Data
public class DocumentoTipo {
	@Id
	private String documentoTipoCod;
	private String documentoTipoDes;
	private String documentoTipoSri;

}

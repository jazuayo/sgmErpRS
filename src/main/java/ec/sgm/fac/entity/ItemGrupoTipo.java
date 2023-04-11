package ec.sgm.fac.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "FAC_ITEM_GRUPO_TIPO")
@Data
public class ItemGrupoTipo {
	@Id
	private String itemGrupoTipoCod;
	private String itemGrupoTipoDes;
}

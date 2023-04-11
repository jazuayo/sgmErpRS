package ec.sgm.fac.modelo;

import java.util.List;

import lombok.Data;

@Data
public class ItemGrupoReq {
	private String itemGrupoCod;
	private String itemGrupoDes;
	private String organizacionCod;
	// Tipo de item grupo
	private String itemGrupoTipoCod;
	// lista de impuestos
	private List<String> impuestoId;
}

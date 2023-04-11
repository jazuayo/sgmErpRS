package ec.sgm.fac.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "FAC_TIPO_ID")
@Data
public class TipoId {
	@Id
	private String tipoIdCod;
	private String tipoIdDes;

	public String getSriAtsCompra() {
		if (tipoIdCod.equalsIgnoreCase("C"))
			return "02";
		if (tipoIdCod.equalsIgnoreCase("R"))
			return "01";
		if (tipoIdCod.equalsIgnoreCase("P"))
			return "03";
		return tipoIdCod;
	}

	public String getSriCeVenta() {
		if (tipoIdCod.equalsIgnoreCase("C"))
			return "05";
		if (tipoIdCod.equalsIgnoreCase("R"))
			return "04";
		if (tipoIdCod.equalsIgnoreCase("P"))
			return "06";
		if (tipoIdCod.equalsIgnoreCase("F"))
			return "07";
		return tipoIdCod;
	}
}

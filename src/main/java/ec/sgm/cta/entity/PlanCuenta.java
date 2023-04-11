package ec.sgm.cta.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import ec.sgm.org.entity.Organizacion;
import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "CTA_PLAN_CUENTA")
@Data
public class PlanCuenta {
	@Id
	@Column(name = "CUENTA_COD")
	private String cuentaCod;
	@NotEmpty
	private String cuentaNum;
	@NotEmpty
	@Column(name = "CUENTA_DES")
	private String cuentaDes;
	@Column(name = "OBSERVACIONES")
	private String observaciones;
	@NotNull
	@Column(name = "ES_MOVIMIENTO")
	private Boolean movimiento = Boolean.FALSE;
	@Column(name = "NIVEL")
	private Integer nivel = 0;
	@NotNull
	@Column(name = "FECHA_DESDE")
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private LocalDate fechaDesde;
	@NotNull
	@Column(name = "FECHA_HASTA")
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private LocalDate fechaHasta;
	@NotNull
	@Column(name = "ES_OPERATIVA")
	private Boolean operativa = Boolean.FALSE;

	@JoinColumn(name = "PADRE", referencedColumnName = "CUENTA_COD")
	@ManyToOne
	private PlanCuenta ctaPlanCuentaPadre;

	@JoinColumn(name = "CUENTA_TIPO_COD", referencedColumnName = "CUENTA_TIPO_COD")
	@ManyToOne(optional = false)
	private PlanCuentaTipo planCuentaTipo = new PlanCuentaTipo();

	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;

	private String usuario = ".";

	@Column(name = "GRUPO_MOV")
	private String grupoMov = "M";

	public String getCuentaCodPresenta() {
		if (cuentaCod == null)
			return "";
		int pos = cuentaCod.indexOf("_");
		if (pos >= 0)
			return cuentaCod.substring(pos + 1);
		else
			return cuentaCod;
	}

	public String getPresenta() {
		return getCuentaCodPresenta() + ":" + cuentaDes;
	}
}

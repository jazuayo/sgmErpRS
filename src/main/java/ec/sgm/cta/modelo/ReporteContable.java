package ec.sgm.cta.modelo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.org.entity.Organizacion;

public class ReporteContable {

	private PlanCuenta cuentaInicial;
	private PlanCuenta cuentaFinal;

	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fechaInicial;

	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fechaFinal;
	private Organizacion organizacion;
//	private GenCategoria zona;
//	private GenCategoria linea;
	private String tipoReporte;
	private int nivel = 7;

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public PlanCuenta getCuentaInicial() {
		return cuentaInicial;
	}

	public void setCuentaInicial(PlanCuenta cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	public PlanCuenta getCuentaFinal() {
		return cuentaFinal;
	}

	public void setCuentaFinal(PlanCuenta cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/*
	 * public String getOrganizacion() { return organizacion; } public void
	 * setOrganizacion(String organizacion) { this.organizacion = organizacion; }
	 */

//	public GenCategoria getZona() {
//		return zona;
//	}
//	public void setZona(GenCategoria zona) {
//		this.zona = zona;
//	}
//	public GenCategoria getLinea() {
//		return linea;
//	}
//	public void setLinea(GenCategoria linea) {
//		this.linea = linea;
//	}
	public String getTipoReporte() {
		return tipoReporte;
	}

	public Organizacion getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(Organizacion organizacion) {
		this.organizacion = organizacion;
	}

	public void setTipoReporte(String tipoReporte) {
		this.tipoReporte = tipoReporte;
	}

}

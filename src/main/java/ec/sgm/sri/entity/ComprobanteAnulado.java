package ec.sgm.sri.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import ec.sgm.org.entity.Organizacion;

/**
 * 
 * @author MP
 *
 */
@Entity
@Table(name = "SRI_COMPROBANTE_ANULADO")
public class ComprobanteAnulado {
	@Id
	@SequenceGenerator(name = "atsComprobanteAnuladoSq", sequenceName = "ATS_COMPROBANTE_ANULADOSQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atsComprobanteAnuladoSq")
	private Long regId;
	@NotNull
	private Integer establecimiento;
	@NotNull
	private Integer puntoEmision;
	@NotNull
	private Integer secuencialInicio;
	@NotNull
	private Integer secuencialFin;
	@NotEmpty
	private String autorizacion;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fechaAnula;
	@JoinColumn(name = "COMPROBANTE_TIPO_COD", referencedColumnName = "COMPROBANTETIPOCOD")
	@ManyToOne(optional = false)
	private ComprobanteTipo comprobanteTipo;

	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;

	private String usuario = ".";

	public Long getRegId() {
		return regId;
	}

	public void setRegId(Long regId) {
		this.regId = regId;
	}

	public Integer getEstablecimiento() {
		return establecimiento;
	}

	public void setEstablecimiento(Integer establecimiento) {
		this.establecimiento = establecimiento;
	}

	public Integer getPuntoEmision() {
		return puntoEmision;
	}

	public void setPuntoEmision(Integer puntoEmision) {
		this.puntoEmision = puntoEmision;
	}

	public Integer getSecuencialInicio() {
		return secuencialInicio;
	}

	public void setSecuencialInicio(Integer secuencialInicio) {
		this.secuencialInicio = secuencialInicio;
	}

	public Integer getSecuencialFin() {
		return secuencialFin;
	}

	public void setSecuencialFin(Integer secuencialFin) {
		this.secuencialFin = secuencialFin;
	}

	public String getAutorizacion() {
		return autorizacion;
	}

	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}

	public Date getFechaAnula() {
		return fechaAnula;
	}

	public void setFechaAnula(Date fechaAnula) {
		this.fechaAnula = fechaAnula;
	}

	public ComprobanteTipo getComprobanteTipo() {
		return comprobanteTipo;
	}

	public void setComprobanteTipo(ComprobanteTipo comprobanteTipo) {
		this.comprobanteTipo = comprobanteTipo;
	}

	public Organizacion getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(Organizacion organizacion) {
		this.organizacion = organizacion;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "ComprobanteAnulado [regId=" + regId + ", establecimiento=" + establecimiento + ", puntoEmision="
				+ puntoEmision + ", secuencialInicio=" + secuencialInicio + ", secuencialFin=" + secuencialFin
				+ ", autorizacion=" + autorizacion + ", fechaAnula=" + fechaAnula + ", comprobanteTipo="
				+ comprobanteTipo + ", organizacion=" + organizacion + ", usuario=" + usuario + "]";
	}

}

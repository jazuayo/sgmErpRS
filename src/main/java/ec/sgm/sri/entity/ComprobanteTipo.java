package ec.sgm.sri.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @author MP
 *
 */
@Entity
@Table(name = "SRI_COMPROBANTE_TIPO")
public class ComprobanteTipo {
	@Id
	@NotNull
	private String comprobanteTipoCod;
	@Size(max = 100)
	private String comprobanteTipoDes;
//    @Size(max = 100)
//    @Column(name = "TRANSACCION_COD_SEQ")
//    private String transaccionCodSeq;
//    @Column(name = "FECHA_VIGENCIA")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date fechaVigencia;
//    @Size(max = 100)
//    @Column(name = "COMPROBANTE_SUSTENTO")
//    private String comprobanteSustento;

//    @JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
//	@ManyToOne(optional = false)
//	private Organizacion organizacion;

//	private String usuario = ".";

//    public SriComprobanteTipo() {
//    }

//    public SriComprobanteTipo(String comprobanteTipoCod) {
//        this.comprobanteTipoCod = comprobanteTipoCod;
//    }

	public String getComprobanteTipoCod() {
		return comprobanteTipoCod;
	}

	public void setComprobanteTipoCod(String comprobanteTipoCod) {
		this.comprobanteTipoCod = comprobanteTipoCod;
	}

	public String getComprobanteTipoDes() {
		return comprobanteTipoDes;
	}

	public void setComprobanteTipoDes(String comprobanteTipoDes) {
		this.comprobanteTipoDes = comprobanteTipoDes;
	}

	@Override
	public String toString() {
		return "SriComprobanteTipo [comprobanteTipoCod=" + comprobanteTipoCod + ", comprobanteTipoDes="
				+ comprobanteTipoDes + "]";
	}

//    public String getTransaccionCodSeq() {
//        return transaccionCodSeq;
//    }
//
//    public void setTransaccionCodSeq(String transaccionCodSeq) {
//        this.transaccionCodSeq = transaccionCodSeq;
//    }
//
//    public Date getFechaVigencia() {
//        return fechaVigencia;
//    }
//
//    public void setFechaVigencia(Date fechaVigencia) {
//        this.fechaVigencia = fechaVigencia;
//    }
//
//    public String getComprobanteSustento() {
//        return comprobanteSustento;
//    }
//
//    public void setComprobanteSustento(String comprobanteSustento) {
//        this.comprobanteSustento = comprobanteSustento;
//    }
//
//    public Organizacion getOrganizacion() {
//		return organizacion;
//	}
//
//	public void setOrganizacion(Organizacion organizacion) {
//		this.organizacion = organizacion;
//	}
//
//	public String getUsuario() {
//		return usuario;
//	}
//
//	public void setUsuario(String usuario) {
//		this.usuario = usuario;
//	}
//
//	@Override
//    public String toString() {
//        return "ec.sgm.ats.entity.SriComprobanteTipo[ comprobanteTipoCod=" + comprobanteTipoCod + " ]";
//    }

}

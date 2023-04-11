package ec.sgm.cta.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import ec.sgm.core.Constantes;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "CTA_COMPROBANTE")
@Data
@NoArgsConstructor
public class Comprobante {
	@Id
	@NotEmpty
	private String comprobanteCod;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fecha;
	private String concepto;
	private String fuente;
	private String deudorBeneficiario;
	@NotNull
	private boolean esAutomatico;
	@NotEmpty
	private String usuario;
	private BigInteger chequeNumero;

	private String periodoCod;

	@JoinColumn(name = "ESTADO_COD", referencedColumnName = "ESTADOCOD")
	@ManyToOne(optional = false)
	@NotNull
	private Estado estado;
	
	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "comprobante", orphanRemoval = true )
	private List<ComprobanteCuenta> detalles = new ArrayList<ComprobanteCuenta>();

	@JoinColumn(name = "DOCUMENTO_COD", referencedColumnName = "DOCUMENTOCOD")
	@ManyToOne(optional = false)
	@NotNull
	private Documento documento;

	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;

	private Integer compAutCabcod;

	@Transient
	private BigDecimal valorCheque = BigDecimal.ZERO;

	public BigDecimal getDebitos() {
		BigDecimal lbValor = BigDecimal.ZERO;
		for (ComprobanteCuenta c : this.detalles) {
			lbValor = lbValor.add(c.getDebito());
		}
		return lbValor;
	}

	public BigDecimal getCreditos() {
		BigDecimal lbValor = BigDecimal.ZERO;
		for (ComprobanteCuenta c : this.detalles) {
			lbValor = lbValor.add(c.getCredito());
		}
		return lbValor;
	}

	public Comprobante(String comprobanteCod, Date fecha, boolean esAutomatico, int usuarioId) {
		this.comprobanteCod = comprobanteCod;
//        this.organizacionCod = organizacionCod;
		this.fecha = fecha;
		this.esAutomatico = esAutomatico;
		// this.usuarioId = usuarioId;
	}

	@Override
	public String toString() {
		String cuentas = "";
		for (ComprobanteCuenta detalle : detalles) {
			cuentas += "\nCuenta=" + detalle.getCuenta().getCuentaCod() + ":" + detalle.getCuenta().getCuentaDes()
					+ " debito=" + detalle.getDebito() + " credito=" + detalle.getCredito();
		}

		return "Comprobante [comprobanteCod=" + comprobanteCod + ", fecha=" + fecha + ", concepto=" + concepto
				+ ", fuente=" + fuente + ", deudorBeneficiario=" + deudorBeneficiario + ", esAutomatico=" + esAutomatico
				+ ", usuario=" + usuario + ", chequeNumero=" + chequeNumero + ", periodoCod=" + periodoCod + ", estado="
				+ estado + ", documento=" + documento + ", organizacion=" + organizacion + ", compAutCabcod="
				+ compAutCabcod + ", valorCheque=" + valorCheque + "]" + cuentas;
	}

}

package ec.sgm.cta.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ec.sgm.core.Constantes;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Marco
 */
@NamedStoredProcedureQueries({
		@NamedStoredProcedureQuery(name = "pctaIntACGeneraComp", procedureName = "pcta_int_ac_genera_comp", parameters = {
				@StoredProcedureParameter(type = String.class, name = "iv_origen", mode = ParameterMode.IN),
				@StoredProcedureParameter(type = String.class, name = "ov_resultado", mode = ParameterMode.OUT) })//
})
@Entity
@Table(name = "CTA_COMPROBANTE_TMP")
@Data
@NoArgsConstructor
public class ComprobanteTmp {
	@Id
	@NotEmpty
	private String comprobanteCod;
	@NotNull
//	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fecha;
	private String concepto;
	private String fuente;
	private String deudorBeneficiario;
	@NotNull
	private Boolean esAutomatico;
	@NotEmpty
	private String usuario;
	private BigInteger chequeNumero;

	private String periodoCod;

	@Column(name = "ESTADO_COD")
	private String estadoCod;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "comprobante")
	@JsonManagedReference
	private List<ComprobanteTmpCuenta> detalles = new ArrayList<ComprobanteTmpCuenta>();

	@Column(name = "DOCUMENTO_COD")
	@NotNull
	private String documentoCod;

	@Column(name = "ORGANIZACION_COD")
	@NotNull
	private String organizacionCod;

	private Integer compAutCabcod;

	public BigDecimal getDebitos() {
		BigDecimal lbValor = BigDecimal.ZERO;
		for (ComprobanteTmpCuenta c : this.detalles) {
			lbValor = lbValor.add(c.getDebito());
		}
		return lbValor;
	}

	public BigDecimal getCreditos() {
		BigDecimal lbValor = BigDecimal.ZERO;
		for (ComprobanteTmpCuenta c : this.detalles) {
			lbValor = lbValor.add(c.getCredito());
		}
		return lbValor;
	}

}

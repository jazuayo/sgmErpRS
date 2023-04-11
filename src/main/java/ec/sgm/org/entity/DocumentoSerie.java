package ec.sgm.org.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import lombok.Data;

@Entity
@Table(name = "ORG_DOCUMENTO_SERIE")
@Data
public class DocumentoSerie {
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "ORG_DOCUMENTO_SERIESQ", name = "ORG_DOCUMENTO_SERIESQ")
	@GeneratedValue(generator = "ORG_DOCUMENTO_SERIESQ", strategy = GenerationType.SEQUENCE)
	private Long docSerieId;
	private String documentoCod;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fechaEmision;
	@NotNull
	private Long secuencialDesde;
	@NotNull
	private Long secuencialHasta;
	@NotEmpty
	private String autorizacion;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fechaCaduca;

}

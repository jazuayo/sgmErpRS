package ec.sgm.org.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import lombok.Data;

@Entity
@Table(name = "ORG_PARAMETRO")
@Data
public class Parametro {
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "ORG_PARAMETROSQ", name = "ORG_PARAMETROSQ")
	@GeneratedValue(generator = "ORG_PARAMETROSQ", strategy = GenerationType.SEQUENCE)
	private Integer parametroId;
	@NotNull
	private String parametroDes;
	@NotNull
	private String clave;
	@NotNull
	private String valor;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	//private LocalDateTime fechaDesde;
	private Date fechaDesde;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	//private LocalDateTime fechaHasta;
	private Date fechaHasta;
	@JoinColumn(name = "organizacionCod", referencedColumnName = "organizacionCod")
	@ManyToOne(optional = true)
	private Organizacion organizacion;
}

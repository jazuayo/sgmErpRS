package ec.sgm.cta.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ec.sgm.org.entity.Organizacion;
import lombok.Data;

/**
 *
 * @author Marco
 */
@Entity
@Table(name = "CTA_PERIODO")
@Data
public class Periodo {
	@Id
	@NotNull
	@Column(name = "PERIODO_COD")
	private Integer periodoCod;
	@NotEmpty
	@Column(name = "PERIODO_DES")
	private String periodoDes;
	@NotNull
	@Column(name = "FECHA_DESDE")
	@Temporal(TemporalType.DATE)
	private Date fechaDesde;
	@NotNull
	@Column(name = "FECHA_HASTA")
	@Temporal(TemporalType.DATE)
	private Date fechaHasta;
	@JoinTable(name = "CTA_PERIODO_ORGANIZACION", joinColumns = @JoinColumn(name = "PERIODO_COD", nullable = false), inverseJoinColumns = @JoinColumn(name = "ORGANIZACION_COD", nullable = false))
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Organizacion> organizaciones;

}

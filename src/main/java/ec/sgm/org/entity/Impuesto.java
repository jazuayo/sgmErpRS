package ec.sgm.org.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "ORG_IMPUESTO")
@Data
public class Impuesto {
	@Id
	private String impuestoCod;
	@NotNull
	private String impuestoDes;
	@JoinColumn(name = "IMPUESTO_TIPO_COD", referencedColumnName = "IMPUESTOTIPOCOD")
	@ManyToOne(optional = false)
	private ImpuestoTipo impuestoTipo;
	@NotNull
	private Double porcentaje;
	private String sriCodigo;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "impuestoCod")
	private List<ImpuestoCategoria> categorias = new ArrayList<ImpuestoCategoria>();

}

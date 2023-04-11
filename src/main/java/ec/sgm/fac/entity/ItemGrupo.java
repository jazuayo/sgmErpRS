package ec.sgm.fac.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ec.sgm.org.entity.Impuesto;
import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Entity
@Table(name = "FAC_ITEM_GRUPO")
@Data
public class ItemGrupo {
	@Id
	private String itemGrupoCod;
	@NotNull
	private String itemGrupoDes;
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = true)
	private Organizacion organizacion;
	@JoinTable(name = "FAC_ITEM_GRUPO_IMPUESTO", joinColumns = @JoinColumn(name = "ITEM_GRUPO_COD", nullable = false), inverseJoinColumns = @JoinColumn(name = "IMPUESTO_COD", nullable = false))
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private Set<Impuesto> impuestos = new HashSet<>();
	@JoinColumn(name = "ITEM_GRUPO_TIPO_COD", referencedColumnName = "ITEMGRUPOTIPOCOD")
	@ManyToOne(optional = true)
	private ItemGrupoTipo itemGrupoTipo;

}

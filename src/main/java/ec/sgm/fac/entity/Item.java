package ec.sgm.fac.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Entity
@Table(name = "FAC_ITEM")
@Data
public class Item {
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 100, sequenceName = "FAC_ITEMSQ", name = "FAC_ITEMSQ")
	@GeneratedValue(generator = "FAC_ITEMSQ", strategy = GenerationType.SEQUENCE)
	private Long itemId;
	private String itemDes;
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = false)
	private Organizacion organizacion;
	@JoinColumn(name = "ITEM_GRUPO_COD", referencedColumnName = "ITEMGRUPOCOD")
	@ManyToOne(optional = false)
	private ItemGrupo itemGrupo;
	private Boolean modificaPrecio = Boolean.TRUE;
	private Boolean permiteDetalle = Boolean.TRUE;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "itemId")
	private List<ItemCategoria> categorias = new ArrayList<ItemCategoria>();
	private String usuario;
	private Long secuenciaGenera = Long.valueOf(1);
	private String siglasItem;
	private Double precioVenta;
	private Double costoCompra;
}

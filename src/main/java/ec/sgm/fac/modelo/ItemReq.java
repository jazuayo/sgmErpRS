package ec.sgm.fac.modelo;

import java.util.List;

import ec.sgm.fac.entity.ItemCategoria;
import lombok.Data;

@Data
public class ItemReq {
	private Long itemId;
	private String itemDes;
	private String organizacionCod;
	private String itemGrupoCod;
	private Boolean modificaPrecio;
	private Boolean permiteDetalle;
	private List<ItemCategoria> categorias;
	private String usuario;
	private Long secuenciaGenera;
	private String siglasItem;
	private Double precioVenta;
	private Double costoCompra;

}

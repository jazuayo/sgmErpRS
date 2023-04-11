package ec.sgm.fac.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import lombok.Data;

@Entity
@Table(name = "FAC_ITEM_INV")
@Data
public class ItemInventario {
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "FAC_ITEMS_INVQ", name = "FAC_ITEMS_INVQ")
	@GeneratedValue(generator = "FAC_ITEMS_INVQ", strategy = GenerationType.SEQUENCE)
	private Long itemInvId;
	@Column(name = "ITEM_ID")
	private Long itemId;
	@Column(name = "LOTE")
	private String lote = Constantes.INVENTARIO_SIN_LOTE;
	@Column(name = "CANTIDAD")
	private Long cantidad = Long.valueOf(0);
	@Column(name = "FECHA_VENCE")
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private Date fechaVence;
}

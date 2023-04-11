package ec.sgm.seg.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import ec.sgm.core.Constantes;
import lombok.Data;

/**
 * 
 * @author SIGMA - TL
 *
 */
@Entity
@Table(name = "USUARIO_MENU")
@Data
public class UsuarioMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@NotNull
	@Column(name = "MENU_COD")
	private String menuCod;
	@NotNull
	@Column(name = "MENU_DES")
	private String menuDes;
	@NotNull
	@Column(name = "ICONO")
	private String icono;
	@NotNull
	@Column(name = "EJECUTA")
	private String ejecuta;
	@NotNull
	@Column(name = "ORDEN")
	private Long orden;
	@NotNull
	@Column(name = "MENU_PADRE")
	private String menuPadre;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private LocalDateTime fechaDesde;
	@NotNull
	@DateTimeFormat(pattern = Constantes.FORMATO_FECHA)
	private LocalDateTime fechaHasta;
}

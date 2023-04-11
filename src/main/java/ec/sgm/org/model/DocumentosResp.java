package ec.sgm.org.model;

import java.util.ArrayList;
import java.util.List;

import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.DocumentoSerie;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Data
public class DocumentosResp {
	private String documentoCod;
	private String documentoDes;
	private Long secuencial;
	private Long longitud;
	private String inicio;
	private Long orden;
	private Estado estado = new Estado();
	private Categoria origen = new Categoria();// mul
	private Organizacion organizacion;// mul

	private List<DocumentoSerie> series = new ArrayList<DocumentoSerie>();

	private String usuario = ".";
	private String sriAts;
	private String sriCe;

	private Boolean ce; // si doc lleva comprobante electronico.
	private Boolean inventario;// si el documento controla el aumento de inventario

	private String secuencia;
}

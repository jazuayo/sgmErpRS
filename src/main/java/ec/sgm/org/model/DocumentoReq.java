package ec.sgm.org.model;

import java.util.List;

import ec.sgm.org.entity.DocumentoSerie;
import lombok.Data;

@Data
public class DocumentoReq {
	private String documentoCod;
	private String documentoDes;
	private Long secuencial;
	private Long longitud;
	private String inicio;
	private Long orden;
	private String estadoCod;
	private String origenCod;
	private String organizacionCod;
	private String usuario;
	private Boolean ce;
	private List<DocumentoSerie> series;
}

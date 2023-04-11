package ec.sgm.ce.modelo;

import java.util.List;

import lombok.Data;

@Data
public class Email {
	private String para;
	private String asunto;
	private String contenido;
	private List<String> archivos;
}

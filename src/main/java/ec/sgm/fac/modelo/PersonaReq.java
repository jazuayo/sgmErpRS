package ec.sgm.fac.modelo;

import lombok.Data;

@Data
public class PersonaReq {
	private Long personaId;
	private String direccion;
	private String email;
	private String nombre;
	private String numeroId;
	private String telefono;
	private String usuario;
	private String organizacionCod;
	private String perTipoCod;
	private Boolean esCliente;
	private Boolean esProveedor;
	private String siglasProveedor;
	private Long lugarId;
}

package ec.sgm.seg.modelo;

import ec.sgm.org.entity.Organizacion;
import lombok.Data;

@Data
public class UsuarioReq {
	String email;
	String clave;
	Organizacion organizacion;
	
	// Cambio de contraseña
	String claveAnterior;
	String claveNueva;
	String codUsuario;
}

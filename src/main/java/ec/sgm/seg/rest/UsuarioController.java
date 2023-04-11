package ec.sgm.seg.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.entity.Usuario;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.UsuarioRepository;
import ec.sgm.seg.modelo.UsuarioReq;
import ec.sgm.seg.modelo.UsuarioResp;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
	private static final Logger LOGGER = LogManager.getLogger(UsuarioController.class);
	@Autowired
	private UsuarioRepository repository;

	/**
	 * Lista organizaciones por usuario
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "organizacion")
	public List<Organizacion> grabar(@RequestBody UsuarioReq registro) throws SigmaException {
		List<Organizacion> organizaciones = new ArrayList<Organizacion>();
		try {
			Usuario usuario = repository.findByEmailAndClaveAndActivo(registro.getEmail(), registro.getClave(), true);
			organizaciones = usuario.getOrganizaciones().stream().distinct().collect(Collectors.toList());
			return organizaciones;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al obtener las organizaciones", e);
		}
	}

	/**
	 * Listar los usuarios
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<Usuario> listar() throws SigmaException {
		try {
			List<Usuario> usuarios = repository.findAll();
			return usuarios;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error listar los usuarios", e);
		}
	}

	/**
	 * Obtener usuario por codigo
	 * 
	 * @param codigo
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "usuarioPorCodigo/{codigo}")
	public Usuario eliminar(@PathVariable("codigo") String codigo) throws SigmaException {
		try {
			Usuario usuario = repository.findById(codigo).orElse(null);
			return usuario;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al recuperar el usuario por el codigo.", e);
		}
	}

	/**
	 * Obtener opciones de botones por pantalla
	 * 
	 * @param rolCod
	 * @param menuCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "opcionesBotonesPorPantalla/{rolCod}/{menuCod}")
	public Object[] recuperaOpcionesBotonesPorPantalla(@PathVariable("rolCod") String rolCod,
			@PathVariable("menuCod") String menuCod) throws SigmaException {
		try {
			Object[] pantallas = repository.findOpcionesBotonesPorPantalla(rolCod, menuCod);
			return pantallas;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al recuperar los botones por pantalla.", e);
		}
	}

	/**
	 * Recupera el usuario por el email y la clave
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "login")
	public UsuarioResp recuperaUsuario(@RequestBody UsuarioReq registro) throws SigmaException {
		Usuario usuario = repository.findByEmailAndClaveAndActivo(registro.getEmail(), registro.getClave(), true);
		UsuarioResp usuarioResp = new UsuarioResp();
		if (usuario != null) {
			try {
				Object[] pantallas = repository.findOpcionesMenuByUsuarioCodAndOrganizacionCod(usuario.getUsuarioCod(),
						registro.getOrganizacion().getOrganizacionCod());
				if (pantallas.length == 0) {
					LOGGER.error("Usted no acceso a pantallas de la organización seleccionada.",
							"Usted no acceso a pantallas de la organización seleccionada.");
					throw new SigmaException("Usted no acceso a pantallas de la organización seleccionada.",
							"Usted no acceso a pantallas de la organización seleccionada.");
				}
				usuarioResp.setPantallas(pantallas);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				throw new SigmaException("Error al recuperar las opciones del menú.", e);
			}
			usuarioResp.setUsuarioCod(usuario.getUsuarioCod());
			usuarioResp.setNombre(usuario.getNombre());
			return usuarioResp;
		} else {
			LOGGER.error("Error: Revise las credenciales, usuario: " + registro.getEmail() + "Revise las credenciales");
			throw new SigmaException("Error: Revise las credenciales, usuario: " + registro.getEmail(),
					"Revise las credenciales");
		}

	}

	/**
	 * Cambiar la contraseña
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "cambiarClave")
	public HashMap<String, String> cambiarClave(@RequestBody UsuarioReq registro) throws SigmaException {
		Usuario usuario = repository.findById(registro.getCodUsuario()).orElse(null);
		if (usuario != null) {
			if (usuario.getClave().equals(registro.getClaveAnterior())) {
				usuario.setClave(registro.getClaveNueva());
				repository.save(usuario);
				return MensajeResponse.mensaje("Contraseña actualizada correctamente.");
			} else {
				LOGGER.error("Error: No coincide la clave anterior.", "No coincide la clave anterior.");
				throw new SigmaException("Error: No coincide la clave anterior.", "No coincide la clave anterior.");
			}
		} else {
			LOGGER.error("Error: Usuario no encontrado.", "Usuario no encontrado.");
			throw new SigmaException("Error: Usuario no encontrado.", "Usuario no encontrado.");
		}

	}
}

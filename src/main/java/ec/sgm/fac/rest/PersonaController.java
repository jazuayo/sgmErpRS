package ec.sgm.fac.rest;

import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

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
import ec.sgm.fac.entity.Persona;
import ec.sgm.fac.entity.PersonaTipo;
import ec.sgm.fac.modelo.PersonaReq;
import ec.sgm.fac.repository.PersonaRepository;
import ec.sgm.fac.repository.PersonaTipoRepository;
import ec.sgm.org.entity.Lugar;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.LugarRepository;
import ec.sgm.org.repository.OrganizacionRepository;

@RestController
@RequestMapping("/persona")
public class PersonaController {
	private static final Logger LOGGER = LogManager.getLogger(PersonaController.class);
	@Autowired
	private PersonaRepository repositoryPersona;
	@Autowired
	private PersonaTipoRepository repositoryPersonaTipo;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;
	@Autowired
	private LugarRepository repositoryLugar;

	/**
	 * listar todas las personas por organizacion
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/todas/{organizacionCod}")
	public List<Persona> listar(@PathVariable("organizacionCod") String organizacionCod) throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			List<Persona> persona = repositoryPersona.findByOrganizacionOrderByNombre(organizacion);
			return persona;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar personas", e);
		}
	}

	/**
	 * Solo clientes
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "cliente/{organizacionCod}")
	public List<Persona> listaClientes(@PathVariable("organizacionCod") String organizacionCod) throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			List<Persona> persona = repositoryPersona.personaEsClientePorOrganizacion(organizacion);
			return persona;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar personas tipo cliente", e);
		}
	}

	/**
	 * Solo proveedores
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "proveedor/{organizacionCod}")
	public List<Persona> listarProveedores(@PathVariable("organizacionCod") String organizacionCod)
			throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			return repositoryPersona.personaEsProveedorPorOrganizacion(organizacion);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar personas tipo proveedor", e);
		}
	}

	/**
	 * Guardar persona
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@Transactional
	@PostMapping
	public Persona grabar(@RequestBody PersonaReq registro) throws SigmaException {
		try {
			Persona persona = new Persona();
			if (registro.getPersonaId() != null && registro.getPersonaId().intValue() == 0) {
				registro.setPersonaId(null);
			}
			persona.setPersonaId(registro.getPersonaId());
			Organizacion organizacion = new Organizacion();
			organizacion = repositoryOrganizacion.findById(registro.getOrganizacionCod()).get();
			persona.setOrganizacion(organizacion);
			PersonaTipo personaTipo = repositoryPersonaTipo.findById(registro.getPerTipoCod()).get();
			persona.setPersonaTipo(personaTipo);
			persona.setNumeroId(registro.getNumeroId());
			persona.setNombre(registro.getNombre());
			persona.setDireccion(registro.getDireccion());
			persona.setTelefono(registro.getTelefono());
			persona.setUsuario(registro.getUsuario());
			persona.setEmail(registro.getEmail());
			persona.setEsCliente(registro.getEsCliente());
			persona.setEsProveedor(registro.getEsProveedor());
			if (registro.getEsProveedor() == true) {
				persona.setSiglasProveedor(registro.getSiglasProveedor().toUpperCase());
			}
			Lugar lugar = repositoryLugar.findById(registro.getLugarId()).get();
			persona.setLugar(lugar);
			repositoryPersona.save(persona);
			return persona;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al grabar persona.", e);
		}
	}

	/**
	 * Eliminar persona
	 * 
	 * @param id
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{id}")
	public HashMap<String, String> eliminar(@PathVariable("id") Long id) throws SigmaException {
		try {
			repositoryPersona.deleteById(id);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar persona.", e);
		}
	}

	/**
	 * Listar persona por cedula (numero) y organizacion
	 * 
	 * @param org
	 * @param numero
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "buscar/{organizacionCod}/{numero}")
	public Persona buscarCedulayOrg(@PathVariable("organizacionCod") String organizacionCod,
			@PathVariable("numero") String numero) throws SigmaException {
		try {
			Persona persona = new Persona();
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).orElse(null);
			List<Persona> personas = repositoryPersona.findByNumeroIdAndOrganizacion(numero, organizacion);
			if (personas.size() == 1) {
				persona = personas.get(0);
			}
			return persona;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al buscar persona persona.", e);
		}
	}
}

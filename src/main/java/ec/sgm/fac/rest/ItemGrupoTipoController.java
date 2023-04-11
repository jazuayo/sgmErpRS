package ec.sgm.fac.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.entity.ItemGrupoTipo;
import ec.sgm.fac.repository.ItemGrupoTipoRepository;
import ec.sgm.org.entity.Parametro;
import ec.sgm.org.repository.ParametroRepository;

@RestController
@RequestMapping("/itemGrupoTipo")
public class ItemGrupoTipoController {
	private static final Logger LOGGER = LogManager.getLogger(ItemGrupoTipoController.class);
	@Autowired
	private ItemGrupoTipoRepository repositoryItemGrupoTipo;
	@Autowired
	private ParametroRepository parametroRepository;

	@GetMapping(value = "/{organizacionCod}")
	public List<ItemGrupoTipo> listarTipoGrupoPorOrganizacion(@PathVariable("organizacionCod") String organizacionCod)
			throws SigmaException {
		try {
			String clave = "TipoProducto";
			List<ItemGrupoTipo> respuesta = new ArrayList<ItemGrupoTipo>();
			List<Parametro> parametros = parametroRepository.findByClaveAndOrganizacionCodOrderByParametroDes(clave,
					organizacionCod);
			for (Parametro parametro : parametros) {
				String valor = parametro.getValor();
				ItemGrupoTipo itemGrupoTipo = repositoryItemGrupoTipo.findById(valor).get();
				respuesta.add(itemGrupoTipo);
			}
			return respuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar los items grupo tipos por organizacion.", e);
		}

	}

	/**
	 * Listar todos los items_grupo_tipos. CT-SIGMA
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<ItemGrupoTipo> listar() throws SigmaException {
		try {
			return repositoryItemGrupoTipo.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar los items grupo tipos.", e);
		}
	}
}

package ec.sgm.fac.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
import ec.sgm.fac.entity.ItemGrupo;
import ec.sgm.fac.entity.ItemGrupoTipo;
import ec.sgm.fac.modelo.ItemGrupoReq;
import ec.sgm.fac.repository.ItemGrupoRepository;
import ec.sgm.fac.repository.ItemGrupoTipoRepository;
import ec.sgm.org.entity.Impuesto;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.ImpuestoRepository;
import ec.sgm.org.repository.OrganizacionRepository;

@RestController
@RequestMapping("/grupoItems")
public class ItemGrupoController {
	private static final Logger LOGGER = LogManager.getLogger(ItemGrupoController.class);
	@Autowired
	private ItemGrupoRepository repositoryItemGrupo;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;
	@Autowired
	private ImpuestoRepository repositoryImpuesto;
	@Autowired
	private ItemGrupoTipoRepository repositoryItemGrupoTipo;

	/**
	 * listar todo de la organizacion
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/{organizacionCod}")
	public List<ItemGrupo> listar(@PathVariable("organizacionCod") String organizacionCod) throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			List<ItemGrupo> itemsGrupo = repositoryItemGrupo.findByOrganizacionOrderByItemGrupoDes(organizacion);
			return itemsGrupo;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar grupo de items.", e);
		}
	}

	/**
	 * guardar nuevo grupo item o actualizar
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping
	public HashMap<String, String> grabar(@RequestBody ItemGrupoReq registro) throws SigmaException {
		try {
			if (registro.getItemGrupoCod().equals("")) {
				registro.setItemGrupoCod(null);
			}
			ItemGrupo itemGrupo = new ItemGrupo();
			Organizacion organizacion = repositoryOrganizacion.findById(registro.getOrganizacionCod()).get();
			itemGrupo.setItemGrupoCod(registro.getItemGrupoCod());
			itemGrupo.setItemGrupoDes(registro.getItemGrupoDes());
			itemGrupo.setOrganizacion(organizacion);
			List<Impuesto> impuestoGrupo = new ArrayList<>();
			for (String codigoImpuesto : registro.getImpuestoId()) {
				Impuesto impuesto = repositoryImpuesto.findById(codigoImpuesto).get();
				impuestoGrupo.add(impuesto);
			}
			itemGrupo.setImpuestos(new HashSet<>(impuestoGrupo));
			ItemGrupoTipo itemGrupoTipo = repositoryItemGrupoTipo.findById(registro.getItemGrupoTipoCod()).get();
			itemGrupo.setItemGrupoTipo(itemGrupoTipo);
			repositoryItemGrupo.save(itemGrupo);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al grabar grupo de items", e);
		}
	}

	/**
	 * Eliminar el grupo de item
	 * 
	 * @param itemGrupoCod
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{itemGrupoCod}")
	public HashMap<String, String> eliminar(@PathVariable("itemGrupoCod") String itemGrupoCod) throws SigmaException {
		try {
			repositoryItemGrupo.deleteById(itemGrupoCod);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en eliminar grupo.", e);
		}
	}

}

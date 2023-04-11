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
import ec.sgm.fac.entity.Item;
import ec.sgm.fac.entity.ItemCategoria;
import ec.sgm.fac.entity.ItemGrupo;
import ec.sgm.fac.modelo.ItemReq;
import ec.sgm.fac.repository.ItemCategoriaRepository;
import ec.sgm.fac.repository.ItemGrupoRepository;
import ec.sgm.fac.repository.ItemRepository;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.OrganizacionRepository;

@RestController
@RequestMapping("/item")
public class ItemController {
	private static final Logger LOGGER = LogManager.getLogger(ItemController.class);
	@Autowired
	private ItemRepository repositoryItem;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;
	@Autowired
	private ItemGrupoRepository repositoryItemGrupo;
	@Autowired
	private ItemCategoriaRepository repositoryItemCate;

	/**
	 * listar todos los items por organizacion
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/{organizacionCod}")
	public List<Item> listar(@PathVariable("organizacionCod") String organizacionCod) throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			List<Item> items = repositoryItem.findByOrganizacionOrderByItemDes(organizacion);
			return items;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException(e.getMessage(), e);
		}
	}

	/**
	 * Agregar item
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@Transactional
	@PostMapping
	public Item grabar(@RequestBody ItemReq registro) throws SigmaException {
		try {

			Item item = new Item();
			if (registro.getItemId() != null && registro.getItemId().intValue() == 0) {
				registro.setItemId(null);
			}
			item.setItemId(registro.getItemId());
			item.setPrecioVenta(registro.getPrecioVenta());
			item.setCostoCompra(registro.getCostoCompra());
			item.setItemDes(registro.getItemDes());
			item.setModificaPrecio(registro.getModificaPrecio());
			item.setPermiteDetalle(registro.getPermiteDetalle());
			ItemGrupo itemGrupo = repositoryItemGrupo.findById(registro.getItemGrupoCod()).get();
			item.setItemGrupo(itemGrupo);
			Organizacion organizacion = repositoryOrganizacion.findById(registro.getOrganizacionCod()).get();
			item.setOrganizacion(organizacion);
			item.setUsuario(registro.getUsuario());
			item.setSecuenciaGenera(registro.getSecuenciaGenera());
			item.setSiglasItem(registro.getSiglasItem().toUpperCase());
			Long itemId = repositoryItem.save(item).getItemId();
			// Registrar categoria, agregar itemId al que pertenece la categoria
			for (int i = 0; i <= registro.getCategorias().size() - 1; i++) {
				registro.getCategorias().get(i).setItemId(itemId);
			}
			item.setCategorias(registro.getCategorias());
			item.setItemId(itemId);
			repositoryItem.save(item);
			// Recuperar items categorias actualizados;
			List<ItemCategoria> itemsCategoriaActualizado = repositoryItemCate.findByItemId(itemId);
			item.setCategorias(itemsCategoriaActualizado);
			return item;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al guardar item.", e);
		}
	}

	/**
	 * eliminar
	 * 
	 * @param itemId
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{itemId}")
	public HashMap<String, String> eliminar(@PathVariable("itemId") Long itemId) throws SigmaException {
		try {
			repositoryItem.deleteById(itemId);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar item", e);
		}
	}

}

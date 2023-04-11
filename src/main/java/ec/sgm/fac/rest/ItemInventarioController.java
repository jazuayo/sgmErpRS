package ec.sgm.fac.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.entity.ItemInventario;
import ec.sgm.fac.repository.ItemInventarioRepository;

@RestController
@RequestMapping("/itemInventario")
public class ItemInventarioController {
	private static final Logger LOGGER = LogManager.getLogger(ItemInventarioController.class);
	@Autowired
	private ItemInventarioRepository repository;

	/**
	 * Recupera el inventario por el item Id
	 * 
	 * @param itemId
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/{itemId}")
	public List<ItemInventario> listar(@PathVariable("itemId") Long itemId) throws SigmaException {
		try {
			List<ItemInventario> itemsInv = repository.findByItemIdAndCantidad(itemId, Long.valueOf(0));
			return itemsInv;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar grupo de items del inventario.", e);
		}
	}

	/**
	 * Recuperar el valor del inventario segun el codigo del inventario
	 * 
	 * @param item_inv_id
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/inventario/{item_inv_id}")
	public ItemInventario buscarInventario(@PathVariable("item_inv_id") Long item_inv_id) throws SigmaException {
		try {
			ItemInventario itemInventario = repository.findById(item_inv_id).get();
			return itemInventario;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al obtener datos del inventario.", e);
		}
	}

}

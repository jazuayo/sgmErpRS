package ec.sgm.fac.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.fac.entity.ItemCategoria;
import ec.sgm.fac.repository.ItemCategoriaRepository;

@Service
public class ItemCategoriaService {
	private static final Logger LOGGER = LogManager.getLogger(ItemCategoriaService.class);
	@Autowired
	private ItemCategoriaRepository itemCategoriaRepository;

	public String findCuentaCodByItemIdAndCategoriaCod(Long itemId, String categoriaCod) throws SigmaException {
		List<ItemCategoria> categorias = itemCategoriaRepository.findByItemIdAndCategoriaCod(itemId, categoriaCod);
		for (ItemCategoria categoria : categorias) {
			return categoria.getCuenta().getCuentaCod();
		}
		LOGGER.error("Cuenta No Definida para el item:" + itemId + " categoria:" + categoriaCod);
		throw new SigmaException("Cuenta No Definida para el item:" + itemId + " categoria:" + categoriaCod);
	}

}
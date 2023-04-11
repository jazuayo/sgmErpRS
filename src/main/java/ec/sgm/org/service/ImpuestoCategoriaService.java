package ec.sgm.org.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.org.entity.ImpuestoCategoria;
import ec.sgm.org.repository.ImpuestoCategoriaRepository;

@Service
public class ImpuestoCategoriaService {
	private static final Logger LOGGER = LogManager.getLogger(ImpuestoCategoriaService.class);
	@Autowired
	private ImpuestoCategoriaRepository repositiryImpCate;

	/**
	 * Listar los codigos de los impuestos para esa cuenta y categoria
	 * 
	 * @param cuentaCod
	 * @param categoriaCod
	 * @return
	 * @throws SigmaException
	 */
	public List<String> recuperarCodigoImpuesto(String cuentaCod, String categoriaCod) throws SigmaException {
		try {
			List<String> impuestosCod = new ArrayList<String>();
			List<ImpuestoCategoria> impuestoCategorias = repositiryImpCate.findByCuentaCodAndCategoriaCod(cuentaCod,
					categoriaCod);
			for (ImpuestoCategoria impuestoCategoria : impuestoCategorias) {
				impuestosCod.add(impuestoCategoria.getImpuestoCod());
			}
			System.out.println("Num de impuestos: " + impuestosCod.size() + " para la cuenta: " + cuentaCod
					+ " y categoria: " + categoriaCod);
			System.out.println(impuestosCod);

			return impuestosCod;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al obtener lista de impuestos codigos.", e);
		}
	}
}

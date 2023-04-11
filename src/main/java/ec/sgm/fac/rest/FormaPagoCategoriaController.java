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
import ec.sgm.fac.entity.FormaPago;
import ec.sgm.fac.entity.FormaPagoCategoria;
import ec.sgm.fac.repository.CategoriaRepository;
import ec.sgm.fac.repository.FormaPagoCategoriaRepository;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.OrganizacionRepository;

@RestController
@RequestMapping("/formaPago/categoria")
public class FormaPagoCategoriaController {
	private static final Logger LOGGER = LogManager.getLogger(FormaPagoCategoriaController.class);
	@Autowired
	private FormaPagoCategoriaRepository repositoryFormaPagoCat;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;
	@Autowired
	private CategoriaRepository repositoryCategoria;

	/**
	 * Listar las formas de pago segun la categoria y la organizacion
	 * 
	 * @param organizacionCod
	 * @param categoriaCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/{organizacionCod}/{categoriaCod}")
	public List<FormaPago> listar(@PathVariable("organizacionCod") String organizacionCod,
			@PathVariable("categoriaCod") String categoriaCod) throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			Categoria origen = repositoryCategoria.findById(categoriaCod).get();
			List<FormaPago> respuesta = new ArrayList<FormaPago>();
			List<FormaPagoCategoria> formasPago = repositoryFormaPagoCat.findByOrigenAndOrganizacion(origen,
					organizacion);
			for (FormaPagoCategoria formaPago : formasPago) {
				respuesta.add(formaPago.getFormaPagoCod());
			}
			return respuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar las formas de pago segun la categoria y la organizacion", e);
		}

	}

}

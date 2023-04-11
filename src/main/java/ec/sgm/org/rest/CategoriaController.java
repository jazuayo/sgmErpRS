package ec.sgm.org.rest;

import java.util.HashMap;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.repository.CategoriaRepository;
import ec.sgm.fac.repository.CategoriaTipoRepository;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.CategoriaOrganizacion;
import ec.sgm.org.entity.CategoriaTipo;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.CategoriaReq;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.CategoriaOrganizacionRepository;
import ec.sgm.org.repository.OrganizacionRepository;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {
	private static final Logger LOGGER = LogManager.getLogger(CategoriaController.class);
	@Autowired
	private CategoriaRepository repository;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;
	@Autowired
	private CategoriaTipoRepository repositoryCategoriaTipo;
	@Autowired
	private CategoriaOrganizacionRepository repositoryCategoriaOrganizacion;

	/**
	 * Ingresa o actualiza una categoria
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@Transactional
	@PostMapping
	public HashMap<String, String> grabar(@RequestBody CategoriaReq registro) throws SigmaException {
		try {
			Categoria categoria = new Categoria();

			Organizacion organizacion = repositoryOrganizacion.findById(registro.getOrganizacionCod()).orElse(null);
			CategoriaTipo categoriaTipo = repositoryCategoriaTipo.findById(registro.getCategoriaTipoCod()).orElse(null);

			categoria.setCategoriaCod(registro.getCategoriaCod());
			categoria.setCategoriaDes(registro.getCategoriaDes());
			categoria.setCategoriaTipo(categoriaTipo);
			categoria.setUsuario(registro.getUsuario());

			categoria = repository.save(categoria);
			// Guardar org_categoria_organizacion
			CategoriaOrganizacion catOrganizacion = new CategoriaOrganizacion();
			if (registro.getCatOrgId() != null && registro.getCatOrgId().intValue() != 0) {
				catOrganizacion.setCatOrgId(registro.getCatOrgId());
			}
			catOrganizacion.setOrganizacion(organizacion);
			catOrganizacion.setCategoria(categoria);
			repositoryCategoriaOrganizacion.save(catOrganizacion);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al registrar la categoría.", e);
		}
	}
}

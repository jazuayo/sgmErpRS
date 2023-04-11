package ec.sgm.org.rest;

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
import ec.sgm.org.entity.CategoriaOrganizacion;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.CategoriaResp;
import ec.sgm.org.repository.CategoriaOrganizacionRepository;
import ec.sgm.org.repository.OrganizacionRepository;

@RestController
@RequestMapping("/categoriaOrganizacion")
public class CategoriaOrganizacionController {
	private static final Logger LOGGER = LogManager.getLogger(CategoriaOrganizacionController.class);
	@Autowired
	private CategoriaOrganizacionRepository repositoryCategoriaOrg;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;

	/**
	 * Recupera categorias por organización
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/{organizacionCod}")
	public List<CategoriaResp> listar(@PathVariable("organizacionCod") String organizacionCod) throws SigmaException {
		try {
			List<CategoriaResp> respuesta = new ArrayList<CategoriaResp>();
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).get();
			List<CategoriaOrganizacion> categoriasOrganizacion = repositoryCategoriaOrg
					.findByOrganizacion(organizacion);
			for (CategoriaOrganizacion cateOrganizacion : categoriasOrganizacion) {
				CategoriaResp categoriaResp = new CategoriaResp();
				categoriaResp.setCategoriaCod(cateOrganizacion.getCategoria().getCategoriaCod());
				categoriaResp.setCategoriaDes(cateOrganizacion.getCategoria().getCategoriaDes());
				categoriaResp.setCatOrgId(cateOrganizacion.getCatOrgId());
				categoriaResp
						.setCategoriaTipoDes(cateOrganizacion.getCategoria().getCategoriaTipo().getCategoriaTipoDes());
				categoriaResp
						.setCategoriaTipoCod(cateOrganizacion.getCategoria().getCategoriaTipo().getCategoriaTipoCod());
				categoriaResp.setOrganizacionDes(cateOrganizacion.getOrganizacion().getOrganizacionDes());
				categoriaResp.setOrganizacionCod(cateOrganizacion.getOrganizacion().getOrganizacionCod());
				respuesta.add(categoriaResp);
			}
			return respuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar las categorias de la organizacion", e);
		}
	}

}

package ec.sgm.org.rest;

import java.util.ArrayList;
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
import ec.sgm.org.entity.Impuesto;
import ec.sgm.org.entity.ImpuestoCategoria;
import ec.sgm.org.entity.ImpuestoTipo;
import ec.sgm.org.model.ImpuestoReq;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.ImpuestoRepository;
import ec.sgm.org.repository.ImpuestoTipoRepository;

@RestController
@RequestMapping("/impuesto")
public class ImpuestoController {
	private static final Logger LOGGER = LogManager.getLogger(ImpuestoController.class);
	@Autowired
	private ImpuestoRepository repositoryImpuesto;
	@Autowired
	private ImpuestoTipoRepository repositoryImpuestoTipo;

	/**
	 * Listar impuestos por impuesto tipo Cod
	 * 
	 * @param impuestoTipoCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/impuestoTipo/{impuestoTipoCod}")
	public List<Impuesto> listarPorImpuestoTipo(@PathVariable("impuestoTipoCod") String impuestoTipoCod)
			throws SigmaException {
		try {
			ImpuestoTipo impuestoTipo = repositoryImpuestoTipo.findById(impuestoTipoCod).get();
			return repositoryImpuesto.findByImpuestoTipo(impuestoTipo);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar impuesto por el tipo.", e);
		}
	}

	/**
	 * Listar todos los impuestos
	 * 
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping
	public List<Impuesto> listar() throws SigmaException {
		try {
			return repositoryImpuesto.findAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar los impuestos.", e);
		}
	}

	/**
	 * CT - Listar todos los impuestos con la categoria perteneciente a la Org
	 * 
	 * @param organizacionCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/{organizacionCod}")
	public List<Impuesto> listarImpPorOrganizacion(@PathVariable("organizacionCod") String organizacionCod)
			throws SigmaException {
		try {
			List<Impuesto> respuesta = new ArrayList<Impuesto>();
			List<Impuesto> impuestos = repositoryImpuesto.findAll();
			for (Impuesto impuesto : impuestos) {
				if (impuesto.getCategorias().size() != 0) {
					List<ImpuestoCategoria> categorias = new ArrayList<ImpuestoCategoria>();
					for (ImpuestoCategoria categoria : impuesto.getCategorias()) {
						if (categoria.getOrganizacion().getOrganizacionCod().equals(organizacionCod)) {
							categorias.add(categoria);
						}
					}
					impuesto.setCategorias(categorias);
				}
				respuesta.add(impuesto);
			}
			return respuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar los impuestos de la organizacion.", e);
		}
	}

	/**
	 * Agregar tipo de impuesto
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@Transactional
	@PostMapping
	public Impuesto grabar(@RequestBody ImpuestoReq registro) throws SigmaException {
		try {
			Impuesto impuesto = new Impuesto();
			impuesto.setImpuestoCod(registro.getImpuestoCod());
			impuesto.setImpuestoDes(registro.getImpuestoDes());
			ImpuestoTipo impuestoTipo = repositoryImpuestoTipo.findById(registro.getImpuestoTipoCod()).get();
			impuesto.setImpuestoTipo(impuestoTipo);
			impuesto.setPorcentaje(registro.getPorcentaje());
			impuesto.setSriCodigo(registro.getPorcentajeSri());
			impuesto.setCategorias(registro.getCategorias());
			impuesto.setCategorias(repositoryImpuesto.save(impuesto).getCategorias());
			return impuesto;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al guardar item", e);
		}
	}

	/**
	 * Eliminar el tipo de impuesto
	 * 
	 * @param id
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{id}")
	public HashMap<String, String> eliminar(@PathVariable("id") String id) throws SigmaException {
		try {
			repositoryImpuesto.deleteById(id);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar item", e);
		}
	}
}

package ec.sgm.fac.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.Retencion;
import ec.sgm.fac.entity.RetencionDetalle;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.fac.repository.RetencionDetalleRepository;
import ec.sgm.fac.repository.RetencionRepository;
import ec.sgm.org.model.MensajeResponse;

@RestController
@RequestMapping("/retencionDetalle")
public class RetencionDetalleController {
	private static final Logger LOGGER = LogManager.getLogger(RetencionDetalleController.class);
	@Autowired
	private RetencionDetalleRepository repository;

	@Autowired
	private FacturaRepository repositoryFactura;

	@Autowired
	private RetencionRepository repositoryRetencion;

	/**
	 * Recupera detalles de las retenciones por id de la retencion
	 * 
	 * @param retencionId
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/recuperaPorRetencion/{retencionId}")
	public List<RetencionDetalle> buscarPorOrganizacion(@PathVariable("retencionId") Long retencionId)
			throws SigmaException {
		try {
			List<RetencionDetalle> retencionesDetalle = repository
					.findByRetencionIdOrderByRetencionDetalleId(retencionId);
			return retencionesDetalle;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al listar los detalles de las retenciones por el id de la retencion.", e);
		}
	}

	/**
	 * Eliminar detalle de la retencio por id
	 * 
	 * @param id
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{id}")
	public HashMap<String, String> eliminar(@PathVariable("id") Long id) throws SigmaException {
		try {
			repository.deleteById(id);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar el detalle de la retencion.", e);
		}
	}

	/**
	 * Recuperar retención por el id de la factura
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/recuperaPorFactura/{documentoId}")
	public Retencion buscarPorFactura(@PathVariable("documentoId") Long documentoId) throws SigmaException {
		try {
			Retencion retencion = new Retencion();
			Factura factura = repositoryFactura.findById(documentoId).orElse(null);

			List<RetencionDetalle> retencionesDetalle = repository.findByFacturaOrderByRetencionDetalleId(factura);
			if (retencionesDetalle.size() != 0) {
				retencion = repositoryRetencion.findById(retencionesDetalle.get(0).getRetencionId()).orElse(null);
				List<RetencionDetalle> retencionesAux = new ArrayList<RetencionDetalle>();
				for (RetencionDetalle detalle : retencionesDetalle) {
					if (!retencionesAux.contains(detalle)) {
						retencionesAux.add(detalle);
					}
				}
				retencion.setDetalles(retencionesAux);
			}
			return retencion;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al recuperar la retención por la factura.", e);
		}
	}
}

package ec.sgm.fac.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.fac.entity.Retencion;
import ec.sgm.fac.entity.RetencionDetalle;
import ec.sgm.fac.repository.RetencionDetalleRepository;

@Service
public class RetencionService {
	private static final Logger LOGGER = LogManager.getLogger(RetencionService.class);
	@Autowired
	private RetencionDetalleRepository repositoryRetDetalle;

	/**
	 * Valor retenido por retenciones por el tipo de impuesto
	 * 
	 * @param retenciones
	 * @param impTipoRetencion
	 * @return
	 * @throws SigmaException
	 */
	public double valorRetenidoPorTipoImpuesto(List<Retencion> retenciones, String impTipoRetencion)
			throws SigmaException {
		try {

			Double valor = 0.0;
			for (Retencion retencion : retenciones) {
				List<RetencionDetalle> detalles = repositoryRetDetalle
						.findByRetencionIdOrderByRetencionDetalleId(retencion.getRetencionId());

				for (RetencionDetalle detalle : detalles) {
					String impuestoTipoCod = detalle.getImpuesto().getImpuestoTipo().getImpuestoTipoCod();
					if (impuestoTipoCod.equals(impTipoRetencion)) {

						valor += detalle.getValorRetenido();
					}
				}
			}

			return valor;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al calcular valor retenido por tipo Impuesto", e);
		}
	}

	/**
	 * Valor retenido por retenciones segun porcentaje
	 * 
	 * @param retenciones
	 * @param porcentaje
	 * @return
	 * @throws SigmaException
	 */
	public double valorRetenidoIVAPorcentaje(List<Retencion> retenciones, Double porcentaje) throws SigmaException {
		try {
			Double valor = 0.0;
			for (Retencion retencion : retenciones) {
				List<RetencionDetalle> detalles = repositoryRetDetalle
						.findByRetencionIdOrderByRetencionDetalleId(retencion.getRetencionId());

				for (RetencionDetalle detalle : detalles) {
					Double porcentajeValor = detalle.getImpuesto().getPorcentaje();
					if (porcentajeValor == porcentaje) {
						valor += detalle.getValorRetenido();
					}
				}
			}

			return valor;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al calcular valor retenido IVA porcentaje", e);
		}
	}

}

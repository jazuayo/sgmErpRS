package ec.sgm.fac.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.core.Numero;
import ec.sgm.cta.entity.Comprobante;
import ec.sgm.cta.entity.ComprobanteCuenta;
import ec.sgm.cta.service.ComprobanteService;
import ec.sgm.cta.service.PlanCuentaService;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.FacturaDetalle;
import ec.sgm.fac.entity.FacturaDetalleImpuesto;
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.repository.FacturaDetalleRepository;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.org.entity.Impuesto;
import ec.sgm.org.entity.ImpuestoCategoria;
import ec.sgm.org.repository.ImpuestoCategoriaRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class FacturaService {
	private static final Logger LOGGER = LogManager.getLogger(FacturaService.class);

	@Autowired
	private FacturaRepository facturaRepository;
	@Autowired
	private FacturaDetalleRepository facturaDetalleRepository;
	@Autowired
	private ImpuestoCategoriaRepository impuestoCategoriaRepository;
	@Autowired
	private ItemCategoriaService itemCategoriaService;
	@Autowired
	private PlanCuentaService planCuentaService;
	@Autowired
	private ComprobanteService comprobanteService;
	@Autowired
	private ParametroService parametroService;

	/**************** CALCULOS *********************/
	public List<FacturaImpuesto> calculaImpuestosFacturaConFacturaDetalleImpuesto(
			List<FacturaDetalleImpuesto> impuestosDetalles) {
		List<FacturaImpuesto> facturaImpuestos = new ArrayList<>();
		for (FacturaDetalleImpuesto impuestoDetalle : impuestosDetalles) {
			FacturaImpuesto facturaImpuesto = new FacturaImpuesto();
			facturaImpuesto.setBaseImponible(0.0);
			// veo si existe el impuesto previamente
			boolean agrega = true;
			for (FacturaImpuesto fi : facturaImpuestos) {
				if (facturaImpuesto.getImpuesto() != null && facturaImpuesto.getImpuesto().getImpuestoCod() != null) {
					if (facturaImpuesto.getImpuesto().getImpuestoCod()
							.equalsIgnoreCase(impuestoDetalle.getImpuesto().getImpuestoCod())) {
						facturaImpuesto = fi;
						agrega = false;
					}
				}
			}
			// actualizo los valores del impuesto
			facturaImpuesto.setDocumentoId(null);
			facturaImpuesto.setImpuesto(impuestoDetalle.getImpuesto());
			facturaImpuesto.setPorcentaje(impuestoDetalle.getImpuesto().getPorcentaje());
			Double baseImpobibleValor = facturaImpuesto.getBaseImponible() + impuestoDetalle.getBaseImponible();
			Double baseImponible = Math.round(baseImpobibleValor * 100.0) / 100.0;
			facturaImpuesto.setBaseImponible(baseImponible);
			Double impuestoValor = facturaImpuesto.getBaseImponible() * (facturaImpuesto.getPorcentaje() / 100);
			Double impuesto = Math.round(impuestoValor * 100.0) / 100.0;
			facturaImpuesto.setImpuestoValor(impuesto);
			if (agrega) {
				facturaImpuestos.add(facturaImpuesto);
			}

		}
		return facturaImpuestos;
	}

	public List<FacturaDetalleImpuesto> calculaImpuestosDetalle(Long documentoId) {
		List<FacturaDetalleImpuesto> impuestosDetalles = new ArrayList<>();
		boolean calculaImpuestos = false;
		Factura factura = facturaRepository.findById(documentoId).orElse(null);
		if (factura != null) {
			if (Constantes.ORIGEN_COMPRA_FACTURA.equals(factura.getOrigen().getCategoriaCod())
					|| Constantes.ORIGEN_COMPRA_NC.equals(factura.getOrigen().getCategoriaCod()))

				// if
				// (Constantes.ORIGEN_COMPRA_FACTURA.compareTo(factura.getOrigen().getCategoriaCod())
				// == 0)
				calculaImpuestos = factura.getPersona().getPersonaTipo().getIvaCompra().booleanValue();
			if (Constantes.ORIGEN_VENTA_FACTURA.equals(factura.getOrigen().getCategoriaCod())
					|| Constantes.ORIGEN_VENTA_NC.equals(factura.getOrigen().getCategoriaCod()))
				// if
				// (Constantes.ORIGEN_VENTA_FACTURA.compareTo(factura.getOrigen().getCategoriaCod())
				// == 0)
				calculaImpuestos = factura.getPersona().getPersonaTipo().getIvaVenta().booleanValue();
		}

		if (calculaImpuestos) {
			List<FacturaDetalle> detalles = facturaDetalleRepository
					.findByDocumentoIdOrderByFacturaDetalleId(documentoId);
			for (FacturaDetalle detalle : detalles) {
				Set<Impuesto> impuestos = detalle.getItem().getItemGrupo().getImpuestos();
				for (Impuesto impuesto : impuestos) {
					FacturaDetalleImpuesto facturaDetalleImpuesto = new FacturaDetalleImpuesto();
					facturaDetalleImpuesto.setFacturaDetalleId(detalle.getFacturaDetalleId());
					facturaDetalleImpuesto.setImpuesto(impuesto);
					facturaDetalleImpuesto.setBaseImponible(Numero.redondear(detalle.precioTotalSinImpuesto()));
					Double impuestoValor = detalle.precioTotalSinImpuesto() * (impuesto.getPorcentaje() / 100);
					facturaDetalleImpuesto.setImpuestoValor(Numero.redondear(impuestoValor));
					impuestosDetalles.add(facturaDetalleImpuesto);
				}
			}
		}
		return impuestosDetalles;
	}

	public List<FacturaImpuesto> calculaImpuestosFactura(Long documentoId) {
		// facturaImpuestoRepository.deleteByDocumentoId(documentoId);
		List<FacturaImpuesto> facturaImpuestos = new ArrayList<>();
		List<FacturaDetalleImpuesto> impuestosDetalles = calculaImpuestosDetalle(documentoId);

		for (FacturaDetalleImpuesto impuestoDetalle : impuestosDetalles) {
			FacturaImpuesto facturaImpuesto = new FacturaImpuesto();
			facturaImpuesto.setBaseImponible(0.0);
			// veo si existe el impuesto previamente
			boolean agrega = true;
			for (FacturaImpuesto fi : facturaImpuestos) {
				if (fi.getImpuesto().getImpuestoCod()
						.equalsIgnoreCase(impuestoDetalle.getImpuesto().getImpuestoCod())) {
					facturaImpuesto = fi;
					agrega = false;
				}
			}
			// actualizo los valores del impuesto
			facturaImpuesto.setDocumentoId(documentoId);
			facturaImpuesto.setImpuesto(impuestoDetalle.getImpuesto());
			facturaImpuesto.setPorcentaje(impuestoDetalle.getImpuesto().getPorcentaje());
			facturaImpuesto.setBaseImponible(facturaImpuesto.getBaseImponible() + impuestoDetalle.getBaseImponible());
			facturaImpuesto
					.setImpuestoValor(facturaImpuesto.getBaseImponible() * (facturaImpuesto.getPorcentaje() / 100));
			if (agrega) {
				facturaImpuestos.add(facturaImpuesto);
			}

		}
		return facturaImpuestos;
	}

	public List<FacturaDetalleImpuesto> calculaImpuestosDesdeDetalle(Set<FacturaDetalle> detalles) {
		List<FacturaDetalleImpuesto> impuestosDetalles = new ArrayList<>();
		for (FacturaDetalle detalle : detalles) {
			Set<Impuesto> impuestos = detalle.getItem().getItemGrupo().getImpuestos();
			for (Impuesto impuesto : impuestos) {
				FacturaDetalleImpuesto facturaDetalleImpuesto = new FacturaDetalleImpuesto();
				facturaDetalleImpuesto.setFacturaDetalleId(detalle.getFacturaDetalleId());
				facturaDetalleImpuesto.setImpuesto(impuesto);
				facturaDetalleImpuesto.setBaseImponible(detalle.precioTotalSinImpuesto());
				facturaDetalleImpuesto
						.setImpuestoValor(detalle.precioTotalSinImpuesto() * (impuesto.getPorcentaje() / 100));
				impuestosDetalles.add(facturaDetalleImpuesto);
			}
		}
		return impuestosDetalles;
	}

	public double ivaValorDesdeDetalleImpuestos(List<FacturaDetalleImpuesto> detalleImpuestos) {
		Double valor = 0.0;
		for (FacturaDetalleImpuesto detalle : detalleImpuestos) {
			if ("iva".compareTo(detalle.getImpuesto().getImpuestoTipo().getImpuestoTipoCod()) == 0) {
				valor += detalle.getImpuestoValor();
			}
		}
		return valor;
	}

	public double ivaBase0DesdeDetalleImpuestos(List<FacturaDetalleImpuesto> detalleImpuestos) {
		Double valor = 0.0;
		for (FacturaDetalleImpuesto detalle : detalleImpuestos) {
			if ("iva".compareTo(detalle.getImpuesto().getImpuestoTipo().getImpuestoTipoCod()) == 0
					&& detalle.getImpuesto().getPorcentaje() == 0.0) {
				valor += detalle.getBaseImponible();
			}
		}
		return valor;
	}

	public double ivaBaseDesdeDetalleImpuestos(List<FacturaDetalleImpuesto> detalleImpuestos) {
		Double valor = 0.0;
		for (FacturaDetalleImpuesto detalle : detalleImpuestos) {
			if ("iva".compareTo(detalle.getImpuesto().getImpuestoTipo().getImpuestoTipoCod()) == 0
					&& detalle.getImpuesto().getPorcentaje() > 0.0) {
				valor += detalle.getBaseImponible();
			}
		}
		return valor;
	}

	// Contabilizaciones
	public HashMap<String, Double> precioSinImpuestoPorCuentasDesdeDetalle(List<FacturaDetalle> detalles, String origen)
			throws SigmaException {
		HashMap<String, Double> cuentas = new HashMap<>();
		String cuenta;
		for (FacturaDetalle detalle : detalles) {
			cuenta = itemCategoriaService.findCuentaCodByItemIdAndCategoriaCod(detalle.getItem().getItemId(), origen);
			if (cuentas.get(cuenta) == null) {
				cuentas.put(cuenta, detalle.precioTotalSinImpuesto());
			} else {
				cuentas.put(cuenta, cuentas.get(cuenta) + detalle.precioTotalSinImpuesto());
			}
		}
		return cuentas;
	}

	public void contabilizaFactura(Long documentoId, String comprobanteCod) throws SigmaException {
		Comprobante comprobante = new Comprobante();
		ComprobanteCuenta comprobanteCuenta = new ComprobanteCuenta();
		double total = 0.0;
		Factura factura = facturaRepository.findById(documentoId).orElse(null);
		if (factura == null) {
			throw new SigmaException("No se ha encontrado la factura:" + documentoId);
		}
		String origen = factura.getDocumento().getOrigen().getCategoriaCod();
		String organizacionCod = factura.getOrganizacion().getOrganizacionCod();
		comprobante.setConcepto(factura.getPersona().getNombre() + ":" + factura.getDocumentoNumero());
		comprobante.setFuente(origen + ":" + factura.getDocumentoId());
		comprobante.setOrganizacion(factura.getOrganizacion());
		comprobante.setFecha(factura.getFechaEmite());
		comprobante.setDeudorBeneficiario(factura.getDocumentoNumero());
		comprobante.setEsAutomatico(true);
		List<FacturaDetalle> detalles = facturaDetalleRepository.findByDocumentoIdOrderByFacturaDetalleId(documentoId);
		HashMap<String, Double> cuentasSubtotales = precioSinImpuestoPorCuentasDesdeDetalle(detalles, origen);
		for (String key : cuentasSubtotales.keySet()) {
			comprobanteCuenta = new ComprobanteCuenta();
			comprobanteCuenta.setCuenta(planCuentaService.findByCuentaCod(key));
			comprobanteCuenta.setDebito(BigDecimal.valueOf(cuentasSubtotales.get(key)));
			comprobante.getDetalles().add(comprobanteCuenta);
			total += cuentasSubtotales.get(key);
		}

		List<FacturaImpuesto> impuestos = calculaImpuestosFactura(documentoId);
		for (FacturaImpuesto impuesto : impuestos) {
			List<ImpuestoCategoria> categorias = impuestoCategoriaRepository
					.findByImpuestoCodAndCategoriaCod(impuesto.getImpuesto().getImpuestoCod(), origen);
			for (ImpuestoCategoria categoria : categorias) {
				comprobanteCuenta = new ComprobanteCuenta();
				comprobanteCuenta.setCuenta(categoria.getCuenta());
				comprobanteCuenta.setDebito(BigDecimal.valueOf(impuesto.getImpuestoValor()));
				comprobante.getDetalles().add(comprobanteCuenta);
				total += impuesto.getImpuestoValor();
			}
		}
		comprobanteCuenta = new ComprobanteCuenta();
		comprobanteCuenta.setCuenta(planCuentaService
				.findByCuentaCod(parametroService.ctaCuentaParametrizada(origen + "_Tot", organizacionCod)));
		comprobanteCuenta.setCredito(BigDecimal.valueOf(total));
		comprobante.getDetalles().add(comprobanteCuenta);

		comprobante = comprobanteService.grabar(comprobante,
				parametroService.ctaDocumentoUtiliza(origen, organizacionCod), comprobanteCod);
//		System.out.println(comprobante);
	}

	@Transactional
	public void contabilizaComprasFacturasPorFecha(Date fechaDesde, Date fechaHasta, String organizacionCod)
			throws SigmaException {
		List<Factura> facturas = facturaRepository.findByfechasAndOrganizacionOrderByDocumentoId(
				Constantes.ORIGEN_COMPRA_FACTURA, Fecha.formatoXML(fechaDesde), Fecha.formatoXML(fechaHasta),
				organizacionCod);
		for (Factura factura : facturas) {
			Comprobante comprobante = comprobanteService
					.findByfuente(Constantes.ORIGEN_COMPRA_FACTURA + ":" + factura.getDocumentoId(), organizacionCod);
			String comprobanteCod = null;
			if (comprobante != null) {
				comprobanteCod = comprobante.getComprobanteCod();
				comprobanteService.delete(comprobante.getComprobanteCod());
			}
			contabilizaFactura(factura.getDocumentoId(), comprobanteCod);
		}

	}

}

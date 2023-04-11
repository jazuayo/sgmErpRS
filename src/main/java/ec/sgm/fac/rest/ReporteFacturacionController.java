package ec.sgm.fac.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.fac.modelo.ReporteFacturaReq;
import ec.sgm.fac.service.ReporteFacturasPDFService;
import ec.sgm.fac.service.ReporteFacturasXLSXService;
import ec.sgm.fac.service.ReporteInvMovimientoPDFService;
import ec.sgm.fac.service.ReporteInvMovimientoXLSXService;
import ec.sgm.fac.service.ReporteRetencionesPDFService;
import ec.sgm.fac.service.ReporteRetencionesXLSXService;

@RestController
@RequestMapping("/facturacion")
public class ReporteFacturacionController {
	private static final Logger LOGGER = LogManager.getLogger(ReporteFacturacionController.class);
	@Autowired
	private ReporteFacturasPDFService reporteFacturas;
	@Autowired
	private ReporteFacturasXLSXService reporteFacturasExcel;
	@Autowired
	private ReporteRetencionesPDFService reporteRet;
	@Autowired
	private ReporteRetencionesXLSXService reporteRetExcel;
	@Autowired
	private ReporteInvMovimientoPDFService reporteInvMovimientos;
	@Autowired
	private ReporteInvMovimientoXLSXService reporteInvMovimientosExcel;

	/**
	 * Generar reporte de facturas y/o retenciones generales
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/reporte/general", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] reporteGeneral(@RequestBody ReporteFacturaReq registro) throws SigmaException {
		try {
			System.out.println("Fecha desde: " + registro.getFechaDesde());
			System.out.println("Fecha hasta: " + registro.getFechaHasta());
			System.out.println("Categoria codigo: " + registro.getCategoriaCod());
			// Filtros
			String orgCod = registro.getOrgCod();
			String categoriaCod = registro.getCategoriaCod();
			String fechaDesde = Fecha.formatoFechaGuionSeparado(registro.getFechaDesde());
			String fechaHasta = Fecha.formatoFechaGuionSeparado(registro.getFechaHasta());
			// if para dividir si es una retencion
			switch (categoriaCod) {
			case Constantes.ORIGEN_COMPRA_RETENCION:
				String retCat = Constantes.ORIGEN_COMPRA_FACTURA;
				switch (registro.getFormatoReporte()) {
				case Constantes.FORMATO_TIPO_REPORTE_EXCEL:
					return reporteRetExcel.generarReportesRetencionesExcel(orgCod, retCat, fechaDesde, fechaHasta);
				case Constantes.FORMATO_TIPO_REPORTE_PDF:
					return reporteRet.generarReporteRetenciones(orgCod, retCat, fechaDesde, fechaHasta);
				default:
					return null;
				}
			case Constantes.ORIGEN_VENTA_RETENCION:
				String ventCat = Constantes.ORIGEN_VENTA_FACTURA;
				switch (registro.getFormatoReporte()) {
				case Constantes.FORMATO_TIPO_REPORTE_EXCEL:
					return reporteRetExcel.generarReportesRetencionesExcel(orgCod, ventCat, fechaDesde, fechaHasta);
				case Constantes.FORMATO_TIPO_REPORTE_PDF:
					return reporteRet.generarReporteRetenciones(orgCod, ventCat, fechaDesde, fechaHasta);
				default:
					return null;
				}
			case Constantes.CODIGO_REPORTE_INV:
				Long itemId = registro.getItemId();
				switch (registro.getFormatoReporte()) {
				case Constantes.FORMATO_TIPO_REPORTE_EXCEL:
					return reporteInvMovimientosExcel.generarReportesInvMovimientosExcel(orgCod, fechaDesde, fechaHasta,
							itemId);
				case Constantes.FORMATO_TIPO_REPORTE_PDF:
					return reporteInvMovimientos.generarReporteInvMovimientos(orgCod, fechaDesde, fechaHasta, itemId);
				default:
					return null;
				}
			default:
				switch (registro.getFormatoReporte()) {
				case Constantes.FORMATO_TIPO_REPORTE_EXCEL:
					return reporteFacturasExcel.generarReportesFacturasExcel(orgCod, categoriaCod, fechaDesde,
							fechaHasta);
				case Constantes.FORMATO_TIPO_REPORTE_PDF:
					return reporteFacturas.generarReporteFacturas(orgCod, categoriaCod, fechaDesde, fechaHasta);
				default:
					return null;
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en reporte facturas y/o retenciones generales", e);
		}
	}
}

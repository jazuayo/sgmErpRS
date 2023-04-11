package ec.sgm.fac.service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.ce.util.HtmlServices;
import ec.sgm.core.Fecha;
import ec.sgm.core.Numero;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.repository.CategoriaRepository;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.fac.repository.ReportesRepository;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.OrganizacionRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class ReporteFacturasPDFService {
	private static final Logger LOGGER = LogManager.getLogger(ReporteFacturasPDFService.class);
	@Autowired
	private HtmlServices htmlUtils;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private ReportesRepository repository;
	@Autowired
	private OrganizacionRepository repositoryOrg;
	@Autowired
	private CategoriaRepository repositoryCate;
	@Autowired
	private FacturaRepository repositoryFac;

	// Generar los reportes para facturas
	public byte[] generarReporteFacturas(String orgCod, String categoriaCod, String fechaDesde, String fechaHasta)
			throws SigmaException {
		try {
			// Carga de plantilla
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", orgCod);
			String htmlTemplate = pathArchivos + "repFacturas.html";
			String htmlString = htmlUtils.archivoHtmlaString(htmlTemplate);
			// Carga de datos
			htmlString = htmlString.replace("$fechaActual", Fecha.formatoReportes(new Date()));

			Organizacion org = repositoryOrg.findById(orgCod).orElse(null);
			htmlString = htmlString.replace("$orgDescripcion", org.getOrganizacionDes());

			htmlString = htmlString.replace("$fechaDesde", fechaDesde);
			htmlString = htmlString.replace("$fechaHasta", fechaHasta);

			Categoria cate = repositoryCate.findById(categoriaCod).orElse(null);
			htmlString = htmlString.replace("$categoriaDes", cate.getCategoriaDes());
			System.out.println("Buscando registros...");
			List<Object[]> facturas = repository.reporteFacturas(orgCod, categoriaCod, fechaDesde, fechaHasta);
			System.out.println("Numero de registros: " + facturas.size());
			System.out.println("Generando reporte...");
			htmlString = htmlString.replace("$numRegistros", String.valueOf(facturas.size()));
			String items = "";
			for (Object[] dato : facturas) {
				String documento_id = "<td>" + (BigInteger) dato[0] + "</td>";

				// Calculo de bases
				BigInteger docuId = (BigInteger) dato[0];
				Factura factura = repositoryFac.findById(docuId.longValue()).orElse(null);
				Set<FacturaImpuesto> facturaImpuestos = factura.getImpuestos();
				Double baseImponibleTotal = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, null);
				Double baseImponibleConImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, true);
				Double baseImponibleSinImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, false);
				Double impuestosValor = FacturaImpuesto.sumaImpuestoValor(facturaImpuestos);

				String tipo = "<td>" + (String) dato[1] + "</td>";
				String identificacion = "<td>" + (String) dato[2] + "</td>";
				String nombre_persona = "<td>" + htmlUtils.depurarDatos((String) dato[3]) + "</td>";
				String num_factura = "<td>" + (String) dato[4] + "</td>";
				String fecha_emision = "<td>" + (String) dato[5] + "</td>";
				// String base_sin_iva = "<td>" + (BigDecimal) dato[6] + "</td>";
				String base_sin_iva = "<td>" + Numero.aString(baseImponibleSinImp, 2) + "</td>";
				// String base_con_iva = "<td>" + (BigDecimal) dato[7] + "</td>";
				String base_con_iva = "<td>" + Numero.aString(baseImponibleConImp, 2) + "</td>";
				// String iva = "<td>" + (BigDecimal) dato[8] + "</td>";
				String iva = "<td>" + Numero.aString(impuestosValor, 2) + "</td>";
				// String total_factura = "<td>" + (BigDecimal) dato[9] + "</td>";
				String total_factura = "<td>" + Numero.aString(baseImponibleTotal, 2) + "</td>";
				String operacion = "<td>" + (String) dato[10] + "</td>";
				String estado = "<td>" + (String) dato[11] + "</td>";

				String itemData = documento_id + tipo + identificacion + nombre_persona + num_factura + fecha_emision
						+ base_sin_iva + base_con_iva + iva + total_factura + operacion + estado;

				items = items + "<tr style=\"font-size: 12px;\">" + itemData + "</tr>";
			}
			System.out.println("Fin carga");

			htmlString = htmlString.replace("<tr id=\"items\"></tr>", items);
			// Crear array PDF
			byte[] arrayBytePdf = htmlUtils.htmlAPdf(htmlString);
			String fechaReporte = Fecha.formatoReportesFechaHora(new Date()).toString().replaceAll("\\s", "_")
					.replaceAll(":", "-").replaceAll("/", "-");
			String nombrePdf = "reporte_" + categoriaCod + "_" + fechaReporte;
			System.out.println("Comprobante pdf generado en: " + pathArchivos + nombrePdf + ".pdf");
			htmlUtils.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar reporte pdf de facturas", e);
		}
	}
}

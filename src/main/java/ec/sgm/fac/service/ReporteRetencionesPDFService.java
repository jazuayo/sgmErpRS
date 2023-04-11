package ec.sgm.fac.service;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.ce.util.HtmlServices;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.fac.repository.CategoriaRepository;
import ec.sgm.fac.repository.ReportesRepository;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.OrganizacionRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class ReporteRetencionesPDFService {
	private static final Logger LOGGER = LogManager.getLogger(ReporteRetencionesPDFService.class);
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

	/**
	 * Generar Reporte de retenciones
	 * 
	 * @param orgCod
	 * @param categoriaCod
	 * @param fechaDesde
	 * @param fechaHasta
	 * @return
	 * @throws SigmaException
	 */
	public byte[] generarReporteRetenciones(String orgCod, String categoriaCod, String fechaDesde, String fechaHasta)
			throws SigmaException {
		try {
			// Carga de plantilla
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", orgCod);
			String htmlTemplate = pathArchivos + "repRetenciones.html";
			String htmlString = htmlUtils.archivoHtmlaString(htmlTemplate);
			// Carga de datos
			htmlString = htmlString.replace("$fechaActual", Fecha.formatoReportes(new Date()));

			Organizacion org = repositoryOrg.findById(orgCod).orElse(null);
			htmlString = htmlString.replace("$orgDescripcion", org.getOrganizacionDes());

			htmlString = htmlString.replace("$fechaDesde", fechaDesde);
			htmlString = htmlString.replace("$fechaHasta", fechaHasta);

			Categoria cate = repositoryCate.findById(categoriaCod).orElse(null);
			htmlString = htmlString.replace("$categoriaDes", "Retencion en " + cate.getCategoriaDes());
			System.out.println("Buscando registros...");
			List<Object[]> retenciones = repository.reporteRetenciones(orgCod, categoriaCod, fechaDesde, fechaHasta,
					Constantes.ESTADO_ANULADO);
			System.out.println("Numero de registros: " + retenciones.size());
			System.out.println("Generando reporte...");
			htmlString = htmlString.replace("$numRegistros", String.valueOf(retenciones.size()));
			String items = "";
			for (Object[] dato : retenciones) {

				String retencionId = "<td>" + dato[0] + "</td>";
				String fechaEmision = "<td>" + dato[1] + "</td>";
				String identificacion = "<td>" + dato[2] + "</td>";
				String clienteProveedor = "<td>" + htmlUtils.depurarDatos((String) dato[3]) + "</td>";
				String docNumero = "<td>" + dato[4] + "</td>";
				String baseRetencion = "<td>" + dato[5] + "</td>";
				String codRetencion = "<td>" + dato[6] + "</td>";
				String porRetencion = "<td>" + dato[7] + "</td>";
				String valorRetenido = "<td>" + dato[8] + "</td>";
				String docFactura = "<td>" + dato[9] + "</td>";
				String fechaEmisionFac = "<td>" + dato[10] + "</td>";
				String docuNumFac = "<td>" + dato[11] + "</td>";

				String itemData = retencionId + fechaEmision + identificacion + clienteProveedor + docNumero
						+ baseRetencion + codRetencion + porRetencion + valorRetenido + docFactura + fechaEmisionFac
						+ docuNumFac;

				items = items + "<tr style=\"font-size: 12px;\">" + itemData + "</tr>";
			}
			System.out.println("Fin carga");

			htmlString = htmlString.replace("<tr id=\"items\"></tr>", items);
			// Crear array PDF
			byte[] arrayBytePdf = htmlUtils.htmlAPdf(htmlString);
			String fechaReporte = Fecha.formatoReportesFechaHora(new Date()).toString().replaceAll("\\s", "_")
					.replaceAll(":", "-").replaceAll("/", "-");
			String nombrePdf = "reporte_ret_" + categoriaCod + "_" + fechaReporte;
			System.out.println("Comprobante pdf generado en: " + pathArchivos + nombrePdf + ".pdf");
			htmlUtils.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar reporte pdf de retenciones", e);
		}
	}
}

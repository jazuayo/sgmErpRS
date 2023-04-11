package ec.sgm.cta.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.ce.util.HtmlServices;
import ec.sgm.core.Fecha;
import ec.sgm.core.Numero;
import ec.sgm.core.Texto;
import ec.sgm.cta.repository.ReportesContRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class DiarioGeneralPDFService {
	private static final Logger LOGGER = LogManager.getLogger(DiarioGeneralPDFService.class);
	@Autowired
	private HtmlServices htmlUtils;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private ReportesContRepository repositoryReportes;

	/**
	 * Generar reporte de diario general
	 * 
	 * @param orgCod
	 * @param fechaDesde
	 * @param fechaHasta
	 * @return
	 * @throws SigmaException
	 */
	public byte[] generarReporteDiarioGeneral(String orgCod, String fechaDesde, String fechaHasta)
			throws SigmaException {
		try {
			LOGGER.info("Reporte Diario General");
			LOGGER.info("Facha desde: " + fechaDesde);
			LOGGER.info("Facha hasta: " + fechaHasta);
			// Cargar plantillas
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", orgCod);
			String htmlTemplate = pathArchivos + "estFinDiarioGeneral.html";
			String htmlString = htmlUtils.archivoHtmlaString(htmlTemplate);
			// Cargar datos
			LOGGER.info("Buscando registros...");

			List<Object[]> resultadoSql = repositoryReportes.diarioGeneral(orgCod, fechaDesde, fechaHasta);
			int indexInicialBody = htmlString.indexOf("<body>");
			int indexFinalBody = htmlString.indexOf("</body>");
			String htmlBody = htmlString.substring(indexInicialBody + 6, indexFinalBody);
			String htmlGeneral = "";
			StringBuffer bufferString = new StringBuffer(htmlString);
			// Armar datos a recorrer
			String divisor = "<div>&nbsp;</div><hr></hr>";// "<div>&nbsp;</div><div>&nbsp;</div><hr></hr>";
			// Separar datos por clave y anexar listas para esa clave
			HashMap<String, List<Object[]>> valoresHashMap = new HashMap<String, List<Object[]>>();

			for (Object[] dato : resultadoSql) {
				String key = (String) dato[1];
				if (!valoresHashMap.containsKey(key)) {
					List<Object[]> list = new ArrayList<Object[]>();
					list.add(dato);
					valoresHashMap.put(key, list);
				} else {
					valoresHashMap.get(key).add(dato);
				}
			}
			// Recorrer claves para generar reportes repetidos con diferente data
			List<String> clavesMap = new ArrayList<String>(valoresHashMap.keySet());
			for (String clave : clavesMap) {
				LOGGER.info("Codigo " + clave + " -> " + valoresHashMap.get(clave).size() + " registros");
				List<Object[]> datos = valoresHashMap.get(clave);
				htmlBody = htmlBody.replace("$documento", htmlUtils.depurarDatos((String) datos.get(0)[0]));
				htmlBody = htmlBody.replace("$comprobante_cod", (String) datos.get(0)[1]);
				Date fecha = Fecha.dateDesdeFront((String) datos.get(0)[5]);
				htmlBody = htmlBody.replace("$fecha", Fecha.fechaLetras(fecha));
				htmlBody = htmlBody.replace("$conceptocab", htmlUtils.depurarDatos((String) datos.get(0)[4]));
				htmlBody = htmlBody.replace("$fuente", htmlUtils.depurarDatos((String) datos.get(0)[3]));
				// Sumas debe y haber
				Double sumaDebe = 0.0;
				Double sumaHaber = 0.0;
				// cuentas lista
				String items = "";
				for (Object[] dato : datos) {
					String cuenta = "<td>" + Texto.cuentaFormato(htmlUtils.depurarDatos((String) dato[7])) + "</td>";
					String nombre = "<td>" + htmlUtils.depurarDatos((String) dato[11]) + "</td>";
					BigDecimal debeDato = (BigDecimal) dato[8];
					String debe = "<td align=\"right\">" + Numero.aString(debeDato.doubleValue(), 2) + "</td>";
					BigDecimal haberDato = (BigDecimal) dato[9];
					String haber = "<td align=\"right\">" + Numero.aString(haberDato.doubleValue(), 2) + "</td>";
					String concepto = "<td>" + htmlUtils.depurarDatos((String) dato[10]) + "</td>";

					String itemData = cuenta + nombre + debe + haber + concepto;

					items = items + "<tr style=\"font-size: 12px;\">" + itemData + "</tr>";
					// sumas

					sumaDebe = sumaDebe + debeDato.doubleValue();
					sumaHaber = sumaHaber + haberDato.doubleValue();
				}
				htmlBody = htmlBody.replace("$sumaDebe", Numero.aString(sumaDebe, 2));
				htmlBody = htmlBody.replace("$sumaHaber", Numero.aString(sumaHaber, 2));

				htmlBody = htmlBody.replace("<tr id=\"items\"></tr>", items);

				htmlGeneral = htmlGeneral + "<div>" + htmlBody + "</div>" + divisor;
				htmlBody = htmlString.substring(indexInicialBody + 6, indexFinalBody);
			}

			bufferString.replace(indexInicialBody, indexFinalBody, "<body>" + htmlGeneral);
			LOGGER.info("Generando pdf");
			// Crear array PDF
			byte[] arrayBytePdf = htmlUtils.htmlAPdf(bufferString.toString());
			// nombre pdf reporte
			String fechaReporte = Fecha.formatoReportesFechaHora(new Date()).toString().replaceAll("\\s", "_")
					.replaceAll(":", "-").replaceAll("/", "-");
			String nombrePdf = "DiarioGeneral_" + fechaReporte;
			LOGGER.info("Comprobante pdf generado en: " + pathArchivos + nombrePdf + ".pdf");
			htmlUtils.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar pdf de diario general", e);
		}
	}
}

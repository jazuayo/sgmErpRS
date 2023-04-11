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
public class MayorGeneralPDFService {
	private static final Logger LOGGER = LogManager.getLogger(MayorGeneralPDFService.class);
	@Autowired
	private HtmlServices htmlUtils;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private ReportesContRepository repositoryReportes;

	public byte[] generarReporteMayorGeneral(String organizacionCod, String usuarioCod, String cuentaDesde,
			String cuentaHasta, String fechaDesde, String fechaHasta) throws SigmaException {
		try {
			// Cargar plantillas
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			String htmlTemplate = pathArchivos + "estFinMayorGeneral.html";
			String htmlString = htmlUtils.archivoHtmlaString(htmlTemplate);
			// Cargar datos
			LOGGER.info("Buscando registros...");

			List<Object[]> resultadoSql = repositoryReportes.mayorGeneral(organizacionCod, cuentaDesde, cuentaHasta,
					fechaDesde, fechaHasta);
			LOGGER.info("Registros recuperados " + resultadoSql.size());
			// Datos cabezera
			htmlString = htmlString.replace("$FECHADESDE", fechaDesde);
			htmlString = htmlString.replace("$FECHAHASTA", fechaHasta);
			// Divide secciones a repetir de reporte
			int indexInicialBody = htmlString.indexOf("<body>");
			int indexFinalBody = htmlString.indexOf("</body>");
			String htmlBody = htmlString.substring(indexInicialBody + 6, indexFinalBody);
			String htmlGeneral = "";
			StringBuffer bufferString = new StringBuffer(htmlString);
			String divisor = "<div>&nbsp;</div><hr></hr>";

			// Agrupar datos por clave
			HashMap<String, List<Object[]>> valoresHashMap = new HashMap<String, List<Object[]>>();
			for (Object[] dato : resultadoSql) {
				String key = (String) dato[0]; // Agrupa por cuenta_cod
				if (!valoresHashMap.containsKey(key)) {
					List<Object[]> list = new ArrayList<Object[]>();
					list.add(dato);
					valoresHashMap.put(key, list);
				} else {
					valoresHashMap.get(key).add(dato);
				}
			}
			LOGGER.info("Generando reporte...");
			List<String> clavesMap = new ArrayList<String>(valoresHashMap.keySet());

			for (String clave : clavesMap) {
				LOGGER.info("Cuenta_codigo " + clave + " -> " + valoresHashMap.get(clave).size() + " registros");
				List<Object[]> datos = valoresHashMap.get(clave);

				htmlBody = htmlBody.replace("$cuenta_cod", Texto.cuentaFormato((String) datos.get(0)[0]));
				htmlBody = htmlBody.replace("$cuenta_des", (String) datos.get(0)[1]);
				// valores suma
				Double sumaDeb = 0.0;
				Double sumaCred = 0.0;
				// Genera los datos de la lista
				String items = "";
				for (Object[] dato : datos) {
					BigDecimal debitoVal = (BigDecimal) dato[5];
					BigDecimal creditoVal = (BigDecimal) dato[6];
					// Genera data intermedia
					String fecha = "<td>" + dato[2].toString() + "</td>";
					String NoComprobante = "<td>" + dato[3].toString() + "</td>";
					String debito = "<td align=\"right\">" + Numero.aString(debitoVal.doubleValue(), 2) + "</td>";
					String credito = "<td align=\"right\">" + Numero.aString(creditoVal.doubleValue(), 2) + "</td>";
					BigDecimal saldoVal = (BigDecimal) dato[8];
					String saldo = "<td align=\"right\">" + Numero.aString(saldoVal.doubleValue(), 2) + "</td>";
					String concepto = "<td>" + htmlUtils.depurarDatos(dato[9].toString()) + "</td>";

					String itemData = fecha + NoComprobante + debito + credito + saldo + concepto;

					items = items + "<tr style=\"font-size: 12px;\">" + itemData + "</tr>";

					// sumas

					sumaDeb = sumaDeb + debitoVal.doubleValue();
					sumaCred = sumaCred + creditoVal.doubleValue();
				}
				htmlBody = htmlBody.replace("$sumaDeb", Numero.aString(sumaDeb, 2));
				htmlBody = htmlBody.replace("$sumaCred", Numero.aString(sumaCred, 2));

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
			String nombrePdf = "MayorGeneral_" + fechaReporte;
			LOGGER.info("Comprobante pdf generado en: " + pathArchivos + nombrePdf + ".pdf");
			htmlUtils.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar pdf de mayor general", e);
		}
	}
}

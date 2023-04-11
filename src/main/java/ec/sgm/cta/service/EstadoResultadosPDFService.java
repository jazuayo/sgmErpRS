package ec.sgm.cta.service;

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
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.OrganizacionRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class EstadoResultadosPDFService {
	private static final Logger LOGGER = LogManager.getLogger(EstadoResultadosPDFService.class);
	@Autowired
	private HtmlServices htmlUtils;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private ReportesContRepository repositoryReportes;
	@Autowired
	private OrganizacionRepository repositoryOrg;

	public byte[] generarReporteEstadoResultados(String organizacionCod, String usuarioCod, Integer nivel)
			throws SigmaException {
		try {
			// Cargar plantillas
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			String htmlTemplate = pathArchivos + "estFinEstadoResultados.html";
			String htmlString = htmlUtils.archivoHtmlaString(htmlTemplate);
			// Cargar datos
			LOGGER.info("Buscando registros...");
			List<Object[]> registros = repositoryReportes.estadoResultados(organizacionCod, usuarioCod);
			if (registros.size() == 0) {
				LOGGER.error("No hay registros para el reporte de estado de resultados.");
				return null;
			}
			LOGGER.info("Numero de registros: " + registros.size());
			// Resumen de valores
			LOGGER.info("Calculando resumen de valores...");
			List<Object[]> sumaValores = repositoryReportes.sumaValoresPlanSaldo(organizacionCod, usuarioCod, nivel);

			// Datos de cabecera
			htmlString = htmlString.replace("$fechaActual", Fecha.formatoReportesFechaHora(new Date()));
			Organizacion org = repositoryOrg.findById(organizacionCod).orElse(null);
			htmlString = htmlString.replace("$orgDescripcion", org.getOrganizacionDes());

			String tituloReporte = "Estado de Resultados al "
					+ Fecha.fechaLetras(Fecha.dateDesdeFront((String) registros.get(0)[9]));
			htmlString = htmlString.replace("$fecha_corte_letra", tituloReporte);
			// Datos de tabla
			// Agrupar datos por clave
			HashMap<String, List<Object[]>> valoresHashMap = new HashMap<String, List<Object[]>>();

			for (Object[] dato : registros) {
				String key = (String) dato[0]; // Agrupa por cuenta_tipo_cod
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
			String items = "";
			for (String clave : clavesMap) {
				LOGGER.info("Cuenta_codigo " + clave + " -> " + valoresHashMap.get(clave).size() + " registros");
				List<Object[]> datos = valoresHashMap.get(clave);
				String cuentaFormato = "<td><b>" + datos.get(0)[2] + "</b></td>";
				String titulo = datos.get(0)[1].toString().toUpperCase();
				String cuentaTipoDes = "<td align=\"center\"><b>" + titulo + "</b></td>";
				String valor = "<td><b>" + "$sumaTipo" + "</b></td>";
				String itemData = cuentaFormato + cuentaTipoDes + valor;
				items = items + "<tr style=\"font-size: 14px;\">" + itemData + "</tr>";

				for (Object[] dato : datos) {
					cuentaFormato = "<td>" + Texto.cuentaFormato((String) dato[4]) + "</td>";
					cuentaTipoDes = "<td>" + htmlUtils.depurarDatos((String) dato[5]) + "</td>";
					valor = "<td align=\"right\">" + Numero.aString((double) dato[6], 2) + "</td>";
					itemData = cuentaFormato + cuentaTipoDes + valor;
					items = items + "<tr style=\"font-size: 12px;\">" + itemData + "</tr>";
				}
				switch (clave) {
				case "I":
					items = items.replace("$sumaTipo", Numero.aString((double) sumaValores.get(0)[3], 2));
					break;
				case "C":
					items = items.replace("$sumaTipo", Numero.aString((double) sumaValores.get(0)[8], 2));
					break;
				case "G":
					items = items.replace("$sumaTipo", Numero.aString((double) sumaValores.get(0)[4], 2));
					break;
				case "N":
					items = items.replace("$sumaTipo", Numero.aString((double) sumaValores.get(0)[9], 2));
					break;
				}

			}
			htmlString = htmlString.replace("<tr id=\"items\"></tr>", items);
			// Actualizar resumen de costos

			htmlString = htmlString.replace("$total_ingresos", Numero.aString((double) sumaValores.get(0)[3], 2));

			htmlString = htmlString.replace("$costo_gasto", Numero.aString((double) sumaValores.get(0)[6], 2));

			htmlString = htmlString.replace("$resultadoOperativo", Numero.aString((double) sumaValores.get(0)[7], 2));

			// Crear array PDF
			byte[] arrayBytePdf = htmlUtils.htmlAPdf(htmlString);
			String fechaReporte = Fecha.formatoReportesFechaHora(new Date()).toString().replaceAll("\\s", "_")
					.replaceAll(":", "-").replaceAll("/", "-");
			String nombrePdf = "EstadoResultado_" + fechaReporte;
			LOGGER.info("Comprobante pdf generado en: " + pathArchivos + nombrePdf + ".pdf");
			htmlUtils.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar pdf de estado de resultados", e);
		}
	}
}

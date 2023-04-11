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
public class BalanceGeneralPDFService {
	private static final Logger LOGGER = LogManager.getLogger(BalanceGeneralPDFService.class);
	@Autowired
	private HtmlServices htmlUtils;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private ReportesContRepository repositoryReportes;
	@Autowired
	private OrganizacionRepository repositoryOrg;

	public byte[] generarReporteBalanceGeneral(String organizacionCod, String usuarioCod,Integer nivel) throws SigmaException {
		try {
			// Cargar plantillas
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			String htmlTemplate = pathArchivos + "estFinBalanceGeneral.html";
			String htmlString = htmlUtils.archivoHtmlaString(htmlTemplate);
			// Cargar datos
			LOGGER.info("Buscando registros...");
			List<Object[]> registros = repositoryReportes.balanceGeneral(organizacionCod, usuarioCod);
			LOGGER.info("Numero de registros: " + registros.size());
			if (registros.size() == 0) {
				LOGGER.error("No hay registros para el reporte balance general.");
				return null;
			}
			// Resumen de valores
			LOGGER.info("Calculando resumen de valores...");
			List<Object[]> sumaValores = repositoryReportes.sumaValoresPlanSaldo(organizacionCod, usuarioCod,nivel);

			// Datos de cabecera
			htmlString = htmlString.replace("$fechaActual", Fecha.formatoReportesFechaHora(new Date()));
			Organizacion org = repositoryOrg.findById(organizacionCod).orElse(null);
			htmlString = htmlString.replace("$orgDescripcion", org.getOrganizacionDes());

			String tituloReporte = "Balance General al "
					+ Fecha.fechaLetras(Fecha.dateDesdeFront((String) registros.get(0)[9]));
			htmlString = htmlString.replace("$fecha_corte_letra", tituloReporte);

			// Datos de tabla
			// Agrupar datos por tipo de cuenta
			HashMap<String, List<Object[]>> valoresHashMap = new HashMap<String, List<Object[]>>();

			for (Object[] dato : registros) {
				Integer orden = (Integer) dato[2];
				String key = orden.toString(); // Agrupa por cuenta_tipo_cod
				if (!valoresHashMap.containsKey(key)) {
					List<Object[]> list = new ArrayList<Object[]>();
					list.add(dato);
					valoresHashMap.put(key, list);
				} else {
					valoresHashMap.get(key).add(dato);
				}
			}
			LOGGER.info("Generando reporte...");
			// Recorrer claves para generar reportes repetidos con diferente data
			List<String> clavesMap = new ArrayList<String>(valoresHashMap.keySet());
			String items = "";
			for (String clave : clavesMap) {
				List<Object[]> datos = valoresHashMap.get(clave);
				String cuentaFormato = "<td><b>" + datos.get(0)[2] + "</b></td>";
				String titulo = datos.get(0)[1].toString().toUpperCase();
				LOGGER.info("Clave " + titulo + " -> " + valoresHashMap.get(clave).size() + " registros");
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
				case "1":
					items = items.replace("$sumaTipo", Numero.aString((double) sumaValores.get(0)[0], 2));
					break;
				case "2":
					items = items.replace("$sumaTipo", Numero.aString((double) sumaValores.get(0)[1], 2));
					break;
				case "3":
					items = items.replace("$sumaTipo", Numero.aString((double) sumaValores.get(0)[2], 2));
					break;
				}

			}
			htmlString = htmlString.replace("<tr id=\"items\"></tr>", items);
			// Actualizar resumen de costos

			htmlString = htmlString.replace("$total_Activos", Numero.aString((double) sumaValores.get(0)[0], 2));

			htmlString = htmlString.replace("$pasivo_patrimonio", Numero.aString((double) sumaValores.get(0)[5], 2));

			htmlString = htmlString.replace("$resultadoOperativo", Numero.aString((double) sumaValores.get(0)[7], 2));

			// Crear array PDF
			byte[] arrayBytePdf = htmlUtils.htmlAPdf(htmlString);
			String fechaReporte = Fecha.formatoReportesFechaHora(new Date()).toString().replaceAll("\\s", "_")
					.replaceAll(":", "-").replaceAll("/", "-");
			String nombrePdf = "BalanceGeneral_" + fechaReporte;
			LOGGER.info("Comprobante pdf generado en: " + pathArchivos + nombrePdf + ".pdf");
			htmlUtils.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar pdf de balance general", e);
		}
	}
}

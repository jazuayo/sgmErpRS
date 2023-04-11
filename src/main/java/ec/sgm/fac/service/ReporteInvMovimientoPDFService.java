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
import ec.sgm.core.Numero;
import ec.sgm.fac.entity.Item;
import ec.sgm.fac.repository.ItemRepository;
import ec.sgm.fac.repository.ReportesRepository;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.OrganizacionRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class ReporteInvMovimientoPDFService {
	private static final Logger LOGGER = LogManager.getLogger(ReporteInvMovimientoPDFService.class);
	@Autowired
	private HtmlServices htmlUtils;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private ReportesRepository repository;
	@Autowired
	private OrganizacionRepository repositoryOrg;
	@Autowired
	private ItemRepository repositoryItem;

	public byte[] generarReporteInvMovimientos(String orgCod, String fechaDesde, String fechaHasta, Long itemId)
			throws SigmaException {
		try {
			// Carga de plantilla
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", orgCod);
			String htmlTemplate = pathArchivos + "repInventarioMov.html";
			String htmlString = htmlUtils.archivoHtmlaString(htmlTemplate);
			// Carga de datos
			Item item = repositoryItem.findById(itemId).orElse(null);
			htmlString = htmlString.replace("$itemDes", item.getItemDes());
			htmlString = htmlString.replace("$itemGrupo", item.getItemGrupo().getItemGrupoDes());

			htmlString = htmlString.replace("$fechaActual", Fecha.formatoReportes(new Date()));

			Organizacion org = repositoryOrg.findById(orgCod).orElse(null);
			htmlString = htmlString.replace("$orgDescripcion", org.getOrganizacionDes());

			htmlString = htmlString.replace("$fechaDesde", fechaDesde);
			htmlString = htmlString.replace("$fechaHasta", fechaHasta);
			// detalles
			System.out.println("Buscando registros...");
			List<Object[]> movimientos = repository.reporteInvMovimiento(orgCod, fechaDesde, fechaHasta, itemId,
					Constantes.ESTADO_ANULADO);
			System.out.println("Numero de registros: " + movimientos.size());
			System.out.println("Generando reporte...");
			htmlString = htmlString.replace("$numRegistros", String.valueOf(movimientos.size()));
			String items = "";
			for (Object[] dato : movimientos) {

				String fechaEmite = "<td>" + dato[1] + "</td>";
				String documentoDes = "<td>" + dato[2] + "</td>";
				String cantidad = "<td>" + Numero.aString((Double) dato[3], 0) + "</td>";
				String precioUnitario = "<td>" + Numero.aString((Double) dato[4], 2) + "</td>";
				String precioTotal = "<td>" + Numero.aString((Double) dato[5], 2) + "</td>";

				String itemData = fechaEmite + documentoDes + cantidad + precioUnitario + precioTotal;

				items = items + "<tr style=\"font-size: 12px;\">" + itemData + "</tr>";
			}
			System.out.println("Fin carga");

			htmlString = htmlString.replace("<tr id=\"items\"></tr>", items);

			// Crear array PDF
			byte[] arrayBytePdf = htmlUtils.htmlAPdf(htmlString);
			String fechaReporte = Fecha.formatoReportesFechaHora(new Date()).toString().replaceAll("\\s", "_")
					.replaceAll(":", "-").replaceAll("/", "-");
			String nombrePdf = "reporte_" + Constantes.CODIGO_REPORTE_INV + "_" + fechaReporte;
			System.out.println("Comprobante pdf generado en: " + pathArchivos + nombrePdf + ".pdf");
			htmlUtils.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar reporte pdf de movimientos de inventario", e);
		}
	}
}

package ec.sgm.cta.service;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.ce.util.HtmlServices;
import ec.sgm.core.Fecha;
import ec.sgm.core.Numero;
import ec.sgm.core.Texto;
import ec.sgm.cta.entity.Comprobante;
import ec.sgm.cta.entity.ComprobanteCuenta;
import ec.sgm.cta.repository.ComprobanteRepository;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.service.ParametroService;

@Service
public class ComprobantePDFService {
	private static final Logger LOGGER = LogManager.getLogger(ComprobantePDFService.class);
	@Autowired
	private HtmlServices htmlUtils;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private ComprobanteRepository repositoryComprobante;

	public byte[] generarComprobantePDF(String comprobanteCod) throws SigmaException {
		try {
			LOGGER.info("Generando informacion de " + comprobanteCod);
			Comprobante comprobante = repositoryComprobante.findById(comprobanteCod).orElse(null);
			Organizacion organizacion = comprobante.getOrganizacion();
			// Cargar plantillas
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", organizacion.getOrganizacionCod());
			String htmlTemplate = pathArchivos + "estFinDiarioGeneral.html";
			String htmlString = htmlUtils.archivoHtmlaString(htmlTemplate);
			// Arma cabecera
			htmlString = htmlString.replace("$documento",
					htmlUtils.depurarDatos(comprobante.getDocumento().getDocumentoDes()));
			htmlString = htmlString.replace("$comprobante_cod", comprobanteCod);
			htmlString = htmlString.replace("$fecha", Fecha.fechaLetras(comprobante.getFecha()));
			htmlString = htmlString.replace("$conceptocab", htmlUtils.depurarDatos(comprobante.getConcepto()));
			htmlString = htmlString.replace("$fuente", htmlUtils.depurarDatos(comprobante.getFuente()));
			// Sumas debe y haber
			Double sumaDebe = 0.0;
			Double sumaHaber = 0.0;
			// cuentas lista
			String items = "";
			for (ComprobanteCuenta comCuenta : comprobante.getDetalles()) {
				String cuenta = "<td>"
						+ Texto.cuentaFormato(htmlUtils.depurarDatos(comCuenta.getCuenta().getCuentaCod())) + "</td>";
				String nombre = "<td>" + htmlUtils.depurarDatos(comCuenta.getCuenta().getCuentaDes()) + "</td>";
				BigDecimal debeDato = comCuenta.getDebito();
				String debe = "<td align=\"right\">" + Numero.aString(debeDato.doubleValue(), 2) + "</td>";
				BigDecimal haberDato = comCuenta.getCredito();
				String haber = "<td align=\"right\">" + Numero.aString(haberDato.doubleValue(), 2) + "</td>";
				String concepto = "<td>" + htmlUtils.depurarDatos(comCuenta.getConcepto()) + "</td>";

				String itemData = cuenta + nombre + debe + haber + concepto;

				items = items + "<tr style=\"font-size: 12px;\">" + itemData + "</tr>";
				// sumas

				sumaDebe = sumaDebe + debeDato.doubleValue();
				sumaHaber = sumaHaber + haberDato.doubleValue();
			}
			htmlString = htmlString.replace("$sumaDebe", Numero.aString(sumaDebe, 2));
			htmlString = htmlString.replace("$sumaHaber", Numero.aString(sumaHaber, 2));

			htmlString = htmlString.replace("<tr id=\"items\"></tr>", items);

			LOGGER.info("Generando pdf ");

			byte[] arrayBytePdf = htmlUtils.htmlAPdf(htmlString);
			// guardar archivo generado
			String nombrePdf = "Comprobante-" + comprobanteCod;
			htmlUtils.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			LOGGER.info("Comprobante pdf guardado en :" + pathArchivos + nombrePdf + ".pdf");

			return arrayBytePdf;

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar pdf de comprobante", e);
		}
	}
}

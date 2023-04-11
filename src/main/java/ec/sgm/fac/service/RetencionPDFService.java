package ec.sgm.fac.service;

import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.ce.util.HtmlServices;
import ec.sgm.core.Fecha;
import ec.sgm.core.Numero;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.Retencion;
import ec.sgm.fac.entity.RetencionDetalle;
import ec.sgm.fac.repository.RetencionRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class RetencionPDFService {
	private static final Logger LOGGER = LogManager.getLogger(RetencionPDFService.class);
	@Autowired
	private RetencionRepository repositoryRetencion;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private HtmlServices serviceHtml;

	public byte[] generarPdfRetencion(Long retencionId) throws SigmaException {
		try {
			Retencion retencion = repositoryRetencion.findById(retencionId).orElse(null);
			if (retencion == null) {
				throw new SigmaException("Retencion:" + retencionId + " no encontrada.");
			}
			String organizacionCod = retencion.getOrganizacion().getOrganizacionCod();
			// ------ Cargar el template html ------
			String htmlTemplate = "";

			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			htmlTemplate = pathArchivos + "templateRetencion.html";
			String htmlString = serviceHtml.archivoHtmlaString(htmlTemplate);
			// Datos de cabecera
			String orgLogo = serviceParametros.findValorByClave("orgLogo", organizacionCod);
			String logo = "";
			if (orgLogo == null || orgLogo.equals("")) {
				logo = "";
			} else {
				BufferedImage logoBuffer = serviceHtml.archivoImgsBuffer(orgLogo);
				logo = serviceHtml.imagenBufferABase64(logoBuffer);
			}
			htmlString = htmlString.replace("$logoOrg", logo);

			String sriRuc = serviceParametros.findValorByClave("sriRuc", organizacionCod);
			htmlString = htmlString.replace("$sriRuc", sriRuc);

			String documentoNumero = retencion.getDocumentoNumero();
			htmlString = htmlString.replace("$documentoNumero", documentoNumero);

			String autorizacionNumero = retencion.getAutorizacionNumero();
			htmlString = htmlString.replace("$autorizacionNumero", autorizacionNumero);

			String autorizacionFecha = Fecha.formatoReportesFechaHora(retencion.getAutorizacionFecha());
			htmlString = htmlString.replace("$autorizacionFecha", autorizacionFecha);

			String sriCeAmbiente = serviceParametros.findValorByClave("sriCeAmbiente", organizacionCod);
			String ambiente = "PRUEBAS";
			if (!sriCeAmbiente.equals("1")) {
				ambiente = "PRODUCCION";
			}
			htmlString = htmlString.replace("$sriCeAmbiente", ambiente);

			BufferedImage bufferClave = serviceHtml.generarCodigoBarras(autorizacionNumero);
			String CodBarClaveAcceso = serviceHtml.imagenBufferABase64(bufferClave);
			htmlString = htmlString.replace("$CodBarClaveAcceso", CodBarClaveAcceso);

			// Datos persona vendedor

			String sriRazonSocial = serviceParametros.findValorByClave("sriRazonSocial", organizacionCod);
			htmlString = htmlString.replace("$sriRazonSocial", sriRazonSocial);

			String sriMatrizDir = serviceParametros.findValorByClave("sriMatrizDir", organizacionCod);
			htmlString = htmlString.replace("$sriMatrizDir", sriMatrizDir);

			String sriEstablecimientoDir = serviceParametros.findValorByClave("sriEstablecimientoDir", organizacionCod);
			htmlString = htmlString.replace("$sriEstablecimientoDir", sriEstablecimientoDir);

			String sriContribuyenteEspecial = serviceParametros.findValorByClave("sriContribuyenteEspecial",
					organizacionCod);
			htmlString = htmlString.replace("$sriContribuyenteEspecial", sriContribuyenteEspecial);

			String sriObligadoContabilidad = serviceParametros.findValorByClave("sriObligadoContabilidad",
					organizacionCod);
			htmlString = htmlString.replace("$sriObligadoContabilidad", sriObligadoContabilidad);

			// Datos persona comprador
			Factura factura = retencion.getDetalles().get(0).getFactura();

			String nombre = factura.getPersona().getNombre();
			htmlString = htmlString.replace("$nombre", serviceHtml.depurarDatos(nombre));

			String numeroId = factura.getPersona().getNumeroId();
			htmlString = htmlString.replace("$numeroId", numeroId);

			String fechaEmite = Fecha.formatoReportes(retencion.getFechaEmite());
			htmlString = htmlString.replace("$fechaEmite", fechaEmite);

			// Tabla de detalles
			String items = "";
			for (RetencionDetalle detalle : retencion.getDetalles()) {
				Factura facturaSustento = detalle.getFactura();
				String codDocSustento = "<td>" + facturaSustento.getDocumentoId() + "</td>";
				String numDocSustento = "<td>" + facturaSustento.getDocumentoNumero() + "</td>";
				String fechaEmisionDoc = "<td>" + Fecha.formatoReportes(facturaSustento.getFechaEmite()) + "</td>";
				String ejecicioFiscal = "<td>" + Fecha.getYear(retencion.getFechaEmite()) + "</td>";
				String baseImponible = "<td>" + Numero.aString(detalle.getBaseImponible(), 2) + "</td>";
				String adicionalTipoImp = "<td>" + detalle.getImpuesto().getSriCodigo() + "</td>";
				String porcentajeRetener = "<td>" + detalle.getImpuesto().getPorcentaje() + "</td>";
				String valorRetenido = "<td>" + Numero.aString(detalle.getValorRetenido(), 2) + "</td>";
				String itemData = codDocSustento + numDocSustento + fechaEmisionDoc + ejecicioFiscal + baseImponible
						+ adicionalTipoImp + porcentajeRetener + valorRetenido;
				items = items + "<tr>" + itemData + "</tr>";
			}
			htmlString = htmlString.replace("<tr id=\"items\"></tr>", items);

			// Pasar a pdf
			byte[] arrayBytePdf = serviceHtml.htmlAPdf(htmlString);
			// guardar archivo generado
			String nombrePdf = "retencion_" + retencion.getRetencionId();
			String ruta = serviceHtml.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			System.out.println("Comprobante retencion pdf generado en: " + ruta);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar pdf de retencion", e);
		}
	}
}

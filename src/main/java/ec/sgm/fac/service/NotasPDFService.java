package ec.sgm.fac.service;

import java.awt.image.BufferedImage;
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
import ec.sgm.fac.entity.FacturaDetalle;
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class NotasPDFService {
	private static final Logger LOGGER = LogManager.getLogger(NotasPDFService.class);
	@Autowired
	private FacturaRepository repositoryFactura;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private HtmlServices serviceHtml;

	public byte[] generarNotaPDF(Long documentoId) throws SigmaException {
		try {
			// Org
			Factura factura = repositoryFactura.findById(documentoId).get();
			String organizacionCod = factura.getOrganizacion().getOrganizacionCod();
			// Cargar plantilla
			String htmlTemplate = "htmlTemplates/templateNotas.html";
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			htmlTemplate = pathArchivos + "templateNotas.html";
			String htmlString = serviceHtml.archivoHtmlaString(htmlTemplate);

			// ------ Llena los valores del html con los respectivos datos ------
			// -- logo
			String orgLogo = serviceParametros.findValorByClave("orgLogo", organizacionCod);
			String logo = "";
			if (orgLogo == null || orgLogo.equals("")) {
				logo = "";
			} else {
				BufferedImage logoBuffer = serviceHtml.archivoImgsBuffer(orgLogo);
				logo = serviceHtml.imagenBufferABase64(logoBuffer);
			}
			htmlString = htmlString.replace("$logoOrg", logo);
			// -- fin logo
			// ------------------------------------------------- datos cabezera
			// ---- id = factura
			String sriRuc = serviceParametros.findValorByClave("sriRuc", organizacionCod);
			htmlString = htmlString.replace("$sriRuc", sriRuc);

			String notaDes = factura.getOrigen().getCategoriaDes();
			htmlString = htmlString.replace("$notaDes", notaDes);

			String documentoNumero = factura.getDocumentoNumero();
			htmlString = htmlString.replace("$documentoNumero", documentoNumero);

			String autorizacionNumero = factura.getAutorizacionNumero();
			htmlString = htmlString.replace("$autorizacionNumero", autorizacionNumero);

			String autorizacionFecha = Fecha.formatoReportesFechaHora(factura.getAutorizacionFecha());
			htmlString = htmlString.replace("$autorizacionFecha", autorizacionFecha);

			String sriCeAmbiente = serviceParametros.findValorByClave("sriCeAmbiente", organizacionCod);
			String ambiente = "PRUEBAS";
			if (!sriCeAmbiente.equals("1")) {
				ambiente = "PRODUCCION";
			}
			htmlString = htmlString.replace("$sriCeAmbiente", ambiente);

			String emisionValor = "NORMAL";
			if (factura.getAutorizacionFecha() == null) {
				emisionValor = "CONTIGENCIA";
			}
			htmlString = htmlString.replace("$notaEmision", emisionValor);

			BufferedImage bufferClave = serviceHtml.generarCodigoBarras(autorizacionNumero);
			String CodBarClaveAcceso = serviceHtml.imagenBufferABase64(bufferClave);
			htmlString = htmlString.replace("$CodBarClaveAcceso", CodBarClaveAcceso);

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

			// ----- fin id factura
			// ----- id datosCliente
			String nombre = factura.getPersona().getNombre();
			htmlString = htmlString.replace("$nombre", serviceHtml.depurarDatos(nombre));

			String fechaAdicion = Fecha.formatoReportes(factura.getFechaEmite());
			htmlString = htmlString.replace("$fechaAdicion", fechaAdicion);

			String numeroId = factura.getPersona().getNumeroId();
			htmlString = htmlString.replace("$numeroId", numeroId);

			Factura facturaModifica = factura.getFacturaModifica();

			String doc_numero_modifica = facturaModifica.getDocumentoNumero();
			htmlString = htmlString.replace("$DOC_NUMERO_MODIFICA", doc_numero_modifica);

			String fechaEmisionMod = Fecha.formatoReportes(facturaModifica.getFechaEmite());
			htmlString = htmlString.replace("$DOC_FECHA_EMISION_MODIFICA", fechaEmisionMod);

			// ----- fin id datosCliente
			// ----- id detalles
			String items = "";
			Double sumaDesc = 0.00;
			for (FacturaDetalle detalle : factura.getDetalles()) {
				sumaDesc = sumaDesc + detalle.getDescuentoValor();
				String razonMod = "<td>" + detalle.getDescripcion() + "</td>";
				String valorMod = "<td>" + Numero.aString(detalle.getCantidad(), 0) + "</td>";
				String itemData = razonMod + valorMod;
				items = items + "<tr id=\"items\">" + itemData + "</tr>";
			}
			htmlString = htmlString.replace("<tr id=\"items\"></tr>", items);

			// ----- fin id detalles
			// ----- id resumen
			Set<FacturaImpuesto> facturaImpuestos = factura.getImpuestos();
			Double baseImponibleTotal = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, null);
			Double baseImponibleConImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, true);
			Double baseImponibleSinImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, false);
			Double impuestosValor = FacturaImpuesto.sumaImpuestoValor(facturaImpuestos);

			htmlString = htmlString.replace("$sub12", Numero.aString(baseImponibleConImp, 2));

			htmlString = htmlString.replace("$sub0", Numero.aString(baseImponibleTotal, 2));

			htmlString = htmlString.replace("$subSinImp", Numero.aString(baseImponibleTotal, 2));

			htmlString = htmlString.replace("$desc", Numero.aString(sumaDesc, 2));

			htmlString = htmlString.replace("$impuestosValor", Numero.aString(impuestosValor, 2));

			htmlString = htmlString.replace("$propina", Numero.aString(0.00, 2));

			Double total = baseImponibleTotal + impuestosValor;
			htmlString = htmlString.replace("$vTotal", Numero.aString(total, 2));

			// ----- fin id resumen

			// Pasar a pdf
			byte[] arrayBytePdf = serviceHtml.htmlAPdf(htmlString);
			// guardar archivo generado

			String nombrePdf = "nota_" + notaDes + "_" + documentoId.toString();
			String ruta = serviceHtml.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			System.out.println("Comprobante nota de " + notaDes + " pdf generado en: " + ruta);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar pdf nota", e);
		}
	}
}

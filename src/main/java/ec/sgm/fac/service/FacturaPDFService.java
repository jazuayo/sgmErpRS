package ec.sgm.fac.service;

import java.awt.image.BufferedImage;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.ce.util.HtmlServices;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.core.Numero;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.FacturaDetalle;
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class FacturaPDFService {
	private static final Logger LOGGER = LogManager.getLogger(FacturaPDFService.class);
	@Autowired
	private FacturaRepository repositoryFactura;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private HtmlServices serviceHtml;

	/**
	 * Cargar html plantilla y generar pdf + datos
	 * 
	 * @param documentoId
	 * @throws SigmaException
	 */
	public byte[] generarFacturaPDF(Long documentoId) throws SigmaException {
		try {
			// Cargar plantilla
			String htmlTemplate = "htmlTemplates/templateFactura.html";

			Factura factura = repositoryFactura.findById(documentoId).get();
			String organizacionCod = factura.getOrganizacion().getOrganizacionCod();
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);

			htmlTemplate = pathArchivos + "templateFactura.html";

			// ------ Cargar el template html ------
			String htmlString = serviceHtml.archivoHtmlaString(htmlTemplate);

			// ------ Llena los valores del html con los respectivos datos ------
			String orgLogo = serviceParametros.findValorByClave("orgLogo", organizacionCod);

			String logo = "";
			if (orgLogo == null || orgLogo.equals("")) {
				logo = "";
			} else {
				BufferedImage logoBuffer = serviceHtml.archivoImgsBuffer(orgLogo);
				logo = serviceHtml.imagenBufferABase64(logoBuffer);
			}
			htmlString = htmlString.replace("$logoOrg", logo);

			String sriNombreComercial = serviceParametros.findValorByClave("sriNombreComercial", organizacionCod);
			htmlString = htmlString.replace("$sriNombreComercial", sriNombreComercial);

			String sriMatrizDir = serviceParametros.findValorByClave("sriMatrizDir", organizacionCod);
			htmlString = htmlString.replace("$sriMatrizDir", sriMatrizDir);

			String sriEstablecimientoDir = serviceParametros.findValorByClave("sriEstablecimientoDir", organizacionCod);
			htmlString = htmlString.replace("$sriEstablecimientoDir", sriEstablecimientoDir);

			String sriObligadoContabilidad = serviceParametros.findValorByClave("sriObligadoContabilidad",
					organizacionCod);
			htmlString = htmlString.replace("$sriObligadoContabilidad", sriObligadoContabilidad);

			String sriRuc = serviceParametros.findValorByClave("sriRuc", organizacionCod);
			htmlString = htmlString.replace("$sriRuc", sriRuc);

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

			BufferedImage bufferClave = serviceHtml.generarCodigoBarras(autorizacionNumero);
			String CodBarClaveAcceso = serviceHtml.imagenBufferABase64(bufferClave);
			htmlString = htmlString.replace("$CodBarClaveAcceso", CodBarClaveAcceso);

			htmlString = htmlString.replace("$claveAcceso", autorizacionNumero);

			// Datos persona
			String nombre = factura.getPersona().getNombre();
			htmlString = htmlString.replace("$nombre", serviceHtml.depurarDatos(nombre));

			String direccion = factura.getPersona().getDireccion();
			htmlString = htmlString.replace("$direccion", direccion);

			String numeroId = factura.getPersona().getNumeroId();
			htmlString = htmlString.replace("$numeroId", numeroId);

			String fechaEmite = Fecha.formatoReportes(factura.getFechaEmite());
			htmlString = htmlString.replace("$fechaEmite", fechaEmite);

			// Detalles de los items
			String items = "";
			Double sumaDesc = 0.00;
			for (FacturaDetalle detalle : factura.getDetalles()) {
				sumaDesc = sumaDesc + detalle.getDescuentoValor();
				String codPrin = "<td>" + detalle.getItem().getItemId().toString() + "</td>";
				String cantidad = "<td>" + Numero.aString(detalle.getCantidad(), 0) + "</td>";
				String descripcion = "<td>" + detalle.getDescripcion() + "</td>";
				String precioUnitario = "<td align=\"right\">" + Numero.aString(detalle.getPrecioUnitario(), 2)
						+ "</td>";
				String descuentoValor = "<td align=\"right\">" + Numero.aString(detalle.getDescuentoValor(), 2)
						+ "</td>";
				String precioTotalSinImpuesto = "<td align=\"right\">"
						+ Numero.aString(detalle.precioTotalSinImpuesto(), 2) + "</td>";
				String itemData = codPrin + cantidad + descripcion + precioUnitario + descuentoValor
						+ precioTotalSinImpuesto;
				items = items + "<tr id=\"items\">" + itemData + "</tr>";
			}

			htmlString = htmlString.replace("<tr id=\"items\"></tr>", items);
			// --------- valores extras -------
			String email = factura.getPersona().getEmail();
			String datoEmail = "";
			if (email == null || email.equals("")) {
				datoEmail = "&nbsp;";
			} else {
				datoEmail = email;
			}
			htmlString = htmlString.replace("$email", datoEmail);

			String telefono = factura.getPersona().getTelefono();
			htmlString = htmlString.replace("$telefono", telefono);

			String formaPago = factura.getFormaPago().getFormaPagoDes();
			htmlString = htmlString.replace("$formaPago", formaPago);
			// ------------ resumen de valores de factura
			Set<FacturaImpuesto> facturaImpuestos = factura.getImpuestos();
			Double baseImponibleTotal = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, null);
			Double baseImponibleConImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, true);
			Double baseImponibleSinImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, false);
			Double impuestosValor = FacturaImpuesto.sumaImpuestoValor(facturaImpuestos);
			/*
			 * System.out.println(baseImponibleTotal);
			 * System.out.println(baseImponibleConImp);
			 * System.out.println(baseImponibleSinImp); System.out.println(impuestosValor);
			 */
			htmlString = htmlString.replace("$sub12", Numero.aString(baseImponibleConImp, 2));

			htmlString = htmlString.replace("$sub0", Numero.aString(0.0, 2));

			htmlString = htmlString.replace("$subSinImp", Numero.aString(baseImponibleSinImp, 2));

			htmlString = htmlString.replace("$desc", Numero.aString(sumaDesc, 2));

			htmlString = htmlString.replace("$impuestosValor", Numero.aString(impuestosValor, 2));

			Double total = baseImponibleTotal + impuestosValor;
			htmlString = htmlString.replace("$vTotal", Numero.aString(total, 2));
			// Pasar a pdf
			byte[] arrayBytePdf = serviceHtml.htmlAPdf(htmlString);
			// guardar archivo generado
			String nombrePdf = "factura_" + factura.getDocumentoNumero();
			System.out.println("Comprobante factura pdf a generar en: " + pathArchivos + nombrePdf
					+ Constantes.FORMATO_TIPO_REPORTE_PDF);
			String ruta = serviceHtml.guardarArchivo(pathArchivos, arrayBytePdf, nombrePdf);
			return arrayBytePdf;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar pdf de factura", e);
		}
	}

}

package ec.sgm.fac.service;

import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ec.sgm.SigmaException;
import ec.sgm.ce.service.SigneService;
import ec.sgm.ce.util.XMLServices;
import ec.sgm.core.Fecha;
import ec.sgm.core.Numero;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.FacturaDetalle;
import ec.sgm.fac.entity.FacturaDetalleImpuesto;
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.org.service.ParametroService;

/**
 * 
 * @author SIGMA-CT
 *
 */
@Service
public class FirmaXMLService {
	private static final Logger LOGGER = LogManager.getLogger(FirmaXMLService.class);
	private String tipoEmision = "1"; // Emision normal
	private String ambiente = "2";
	// ***** fin valores estaticos ****
	@Autowired
	private FacturaRepository repositoryFactura;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private XMLServices serviceXML;

	/**
	 * CREAR XML Y FIRMAR
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	public String generaArchivosXml(Long documentoId) throws SigmaException {
		try {
			Factura facturaData = repositoryFactura.findByIdWithDetalles(documentoId).orElse(null);
			if (facturaData == null) {
				throw new SigmaException("Factura:" + documentoId + " no encontrada:");
			}
			String organizacionCod = facturaData.getOrganizacion().getOrganizacionCod();
			String sriCeDocPath = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);

			// ****** OBTENER XML TIPO DOCUMENT PARA FIRMAR DESDE BASE DATOS ***********
			Document doc = generarXML(documentoId);

			String claveAcceso = doc.getElementsByTagName("claveAcceso").item(0).getTextContent();
			// ********* GENERAR XML NO FIRMADO ************
			serviceXML.guardarXML(sriCeDocPath + claveAcceso + ".xml", doc);
			// ****** FIRMA XML *******
			// TODO temporal
			int numeroCertificado = 0;
			String numeroCertificadoPar = serviceParametros.findValorByClaveOpcional("sriCeCertNumero",
					organizacionCod);
			if (numeroCertificadoPar != null) {
				numeroCertificado = Integer.valueOf(numeroCertificadoPar);
			}

			String resultado = SigneService.firma(serviceParametros.findValorByClave("sriCeCertPath", organizacionCod),
					serviceParametros.findValorByClave("sriCeCertClave", organizacionCod), numeroCertificado,
					sriCeDocPath + claveAcceso + ".xml");
			return resultado;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException(e.getMessage(), e);
		}
	}

	/**
	 * GENERAR EL ARCHIVO XML
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	public Document generarXML(Long documentoId) throws SigmaException {
		try {
			// ****** PARAMETROS DE ORG *****
			Factura facturaData = repositoryFactura.findByIdWithDetalles(documentoId).orElse(null);
			String organizacionCod = facturaData.getOrganizacion().getOrganizacionCod();
			String razonSocial = serviceParametros.findValorByClave("sriRazonSocial", organizacionCod);
			String nombreComercial = serviceParametros.findValorByClave("sriNombreComercial", organizacionCod);
			String ruc = serviceParametros.findValorByClave("sriRuc", organizacionCod);
			String dirMatriz = serviceParametros.findValorByClave("sriMatrizDir", organizacionCod);
			String dirEstablecimiento = serviceParametros.findValorByClave("sriEstablecimientoDir", organizacionCod);
			String contribuyenteEspecial = serviceParametros.findValorByClave("sriContribuyenteEspecial",
					organizacionCod);
			String obligadoContabilidad = serviceParametros.findValorByClave("sriObligadoContabilidad",
					organizacionCod);
			ambiente = serviceParametros.findValorByClave("sriCeAmbiente", organizacionCod);

			// ********* CLAVE ACCESO ******

			String fechaEmision = Fecha.formatoXML(facturaData.getFechaEmite());
			String codDoc = facturaData.getDocumentoTipo().getDocumentoTipoSri();
			String documentoNumero = facturaData.getDocumentoNumero();
			String autorizacionNumero = facturaData.getAutorizacionNumero();

			// ********* XML *********

			// Builder
			DocumentBuilderFactory facturaFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder facturaBuilder = facturaFactory.newDocumentBuilder();
			// root elements
			Document factura = facturaBuilder.newDocument();
			Element rootElement = factura.createElement("factura");
			factura.appendChild(rootElement);
			// Atributos de root
			Attr id = factura.createAttribute("id");
			Attr version = factura.createAttribute("version");
			version.setValue("1.1.0");
			id.setValue("comprobante");
			rootElement.setAttributeNode(version);
			rootElement.setAttributeNode(id);
			// Informacion tributaria
			Element infoTributaria = factura.createElement("infoTributaria");
			rootElement.appendChild(infoTributaria);

			Element elementAmbiente = factura.createElement("ambiente");
			infoTributaria.appendChild(elementAmbiente);
			elementAmbiente.appendChild(factura.createTextNode(ambiente));

			Element elementTipoEmision = factura.createElement("tipoEmision");
			infoTributaria.appendChild(elementTipoEmision);
			elementTipoEmision.appendChild(factura.createTextNode(tipoEmision));

			Element elementRazonSocial = factura.createElement("razonSocial");
			infoTributaria.appendChild(elementRazonSocial);
			elementRazonSocial.appendChild(factura.createTextNode(razonSocial));

			Element elementNombreComercial = factura.createElement("nombreComercial");
			infoTributaria.appendChild(elementNombreComercial);
			elementNombreComercial.appendChild(factura.createTextNode(nombreComercial));

			Element elementRuc = factura.createElement("ruc");
			infoTributaria.appendChild(elementRuc);
			elementRuc.appendChild(factura.createTextNode(ruc));

			Element elementClaveAcceso = factura.createElement("claveAcceso");
			infoTributaria.appendChild(elementClaveAcceso);
			elementClaveAcceso.appendChild(factura.createTextNode(facturaData.getAutorizacionNumero()));

			Element elementCodDoc = factura.createElement("codDoc");
			infoTributaria.appendChild(elementCodDoc);
			elementCodDoc.appendChild(factura.createTextNode(codDoc));

			Element elementEstab = factura.createElement("estab");
			infoTributaria.appendChild(elementEstab);
			String estab = facturaData.getDocumentoNumero().substring(0, 3);
			elementEstab.appendChild(factura.createTextNode(estab));

			Element elementPtoEmi = factura.createElement("ptoEmi");
			infoTributaria.appendChild(elementPtoEmi);
			String ptoEmi = facturaData.getDocumentoNumero().substring(4, 7);
			elementPtoEmi.appendChild(factura.createTextNode(ptoEmi));

			Element elementSecuencial = factura.createElement("secuencial");
			infoTributaria.appendChild(elementSecuencial);
			String secuencia = facturaData.getDocumentoNumero().substring(8);
			elementSecuencial.appendChild(factura.createTextNode(secuencia));

			Element elementDirMatriz = factura.createElement("dirMatriz");
			infoTributaria.appendChild(elementDirMatriz);
			elementDirMatriz.appendChild(factura.createTextNode(dirMatriz));

			// TODO temporal
			String sriAgenteRetRes = serviceParametros.findValorByClaveOpcional("sriAgenteRetRes", organizacionCod);
			if (sriAgenteRetRes != null) {
				Element agenteRetencion = factura.createElement("agenteRetencion");
				infoTributaria.appendChild(agenteRetencion);
				agenteRetencion.appendChild(factura.createTextNode(sriAgenteRetRes));
			}
			String sriRegimen = serviceParametros.findValorByClaveOpcional("sriRegimen", organizacionCod);
			if (sriRegimen != null) {
				Element contribuyenteRimpe = factura.createElement("contribuyenteRimpe");
				infoTributaria.appendChild(contribuyenteRimpe);
				contribuyenteRimpe.appendChild(factura.createTextNode(sriRegimen));

			}

			// Informacion de factura

			Element infoFactura = factura.createElement("infoFactura");
			rootElement.appendChild(infoFactura);

			Element elementFechaEmision = factura.createElement("fechaEmision");
			infoFactura.appendChild(elementFechaEmision);
			String fechaEmite = Fecha.formatoReportes(facturaData.getFechaEmite());
			elementFechaEmision.appendChild(factura.createTextNode(fechaEmite));

			Element elementDirEstablecimiento = factura.createElement("dirEstablecimiento");
			infoFactura.appendChild(elementDirEstablecimiento);
			elementDirEstablecimiento.appendChild(factura.createTextNode(dirEstablecimiento));

			Element elementObligadoContabilidad = factura.createElement("obligadoContabilidad");
			infoFactura.appendChild(elementObligadoContabilidad);
			elementObligadoContabilidad.appendChild(factura.createTextNode(obligadoContabilidad));

			if (!contribuyenteEspecial.equals("NO")) {
				Element elementContribuyenteEspecial = factura.createElement("contribuyenteEspecial");
				infoFactura.appendChild(elementContribuyenteEspecial);
				elementContribuyenteEspecial.appendChild(factura.createTextNode(contribuyenteEspecial));
			}

			Element elementTipoIdentificacionComprador = factura.createElement("tipoIdentificacionComprador");
			infoFactura.appendChild(elementTipoIdentificacionComprador);
			String tipoIdentificacionComprador = facturaData.getPersona().getPersonaTipo().getTipoId().getSriCeVenta();
			elementTipoIdentificacionComprador.appendChild(factura.createTextNode(tipoIdentificacionComprador));

			Element elementRazonSocialComprador = factura.createElement("razonSocialComprador");
			infoFactura.appendChild(elementRazonSocialComprador);
			String razonSocialComprador = facturaData.getPersona().getNombre();
			elementRazonSocialComprador.appendChild(factura.createTextNode(razonSocialComprador));

			Element elementIdentificacionComprador = factura.createElement("identificacionComprador");
			infoFactura.appendChild(elementIdentificacionComprador);
			String identificacionComprador = facturaData.getPersona().getNumeroId();
			elementIdentificacionComprador.appendChild(factura.createTextNode(identificacionComprador));

			Element elementDireccionComprador = factura.createElement("direccionComprador");
			infoFactura.appendChild(elementDireccionComprador);
			String direccionComprador = facturaData.getPersona().getDireccion();
			elementDireccionComprador.appendChild(factura.createTextNode(direccionComprador));

			// Totales de factura Base y valor
			Set<FacturaImpuesto> facturaImpuestos = facturaData.getImpuestos();

			Double descuentoValorSuma = 0.0;
			for (FacturaDetalle facturaDetalle : facturaData.getDetalles()) {
				Double descuentoValor = facturaDetalle.getDescuentoValor();
				descuentoValorSuma = descuentoValorSuma + descuentoValor;
			}

			Double baseImponibleTotal = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, null);
			Double baseImponibleConImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, true);
			Double baseImponibleSinImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, false);
			Double impuestosValor = FacturaImpuesto.sumaImpuestoValor(facturaImpuestos);

			Element elementTotalSinImpuestos = factura.createElement("totalSinImpuestos");
			infoFactura.appendChild(elementTotalSinImpuestos);
			elementTotalSinImpuestos.appendChild(factura.createTextNode(Numero.aString(baseImponibleTotal, 2)));

			Element elementTotalDescuento = factura.createElement("totalDescuento");
			infoFactura.appendChild(elementTotalDescuento);
			elementTotalDescuento.appendChild(factura.createTextNode(Numero.aString(descuentoValorSuma, 2)));

			Element totalConImpuestos = factura.createElement("totalConImpuestos");
			infoFactura.appendChild(totalConImpuestos);

			for (FacturaImpuesto facturaImpuesto : facturaImpuestos) {
				Element totalImpuesto = factura.createElement("totalImpuesto");
				totalConImpuestos.appendChild(totalImpuesto);

				Element codigo = factura.createElement("codigo");
				totalImpuesto.appendChild(codigo);
				codigo.appendChild(
						factura.createTextNode(facturaImpuesto.getImpuesto().getImpuestoTipo().getImpuestoTipoSri()));

				Element codigoPorcentaje = factura.createElement("codigoPorcentaje");
				totalImpuesto.appendChild(codigoPorcentaje);
				codigoPorcentaje.appendChild(factura.createTextNode(facturaImpuesto.getImpuesto().getSriCodigo()));

				Element baseImponible = factura.createElement("baseImponible");
				totalImpuesto.appendChild(baseImponible);
				baseImponible
						.appendChild(factura.createTextNode(Numero.aString(facturaImpuesto.getBaseImponible(), 2)));

				Element valor = factura.createElement("valor");
				totalImpuesto.appendChild(valor);
				valor.appendChild(factura.createTextNode(Numero.aString(facturaImpuesto.getImpuestoValor(), 2)));
			}

			Element elementPropina = factura.createElement("propina");
			infoFactura.appendChild(elementPropina);
			elementPropina.appendChild(factura.createTextNode("0"));

			Element elementImporteTotal = factura.createElement("importeTotal");
			infoFactura.appendChild(elementImporteTotal);
			elementImporteTotal
					.appendChild(factura.createTextNode(Numero.aString(baseImponibleTotal + impuestosValor, 2)));

			Element moneda = factura.createElement("moneda");
			infoFactura.appendChild(moneda);
			moneda.appendChild(factura.createTextNode("DOLAR"));

			Element pagos = factura.createElement("pagos");
			infoFactura.appendChild(pagos);

			Element pago = factura.createElement("pago");
			pagos.appendChild(pago);

			Element formaPago = factura.createElement("formaPago");
			pago.appendChild(formaPago);
			formaPago.appendChild(factura.createTextNode(facturaData.getFormaPago().getFormaPagoSri()));

			Element total = factura.createElement("total");
			pago.appendChild(total);
			total.appendChild(factura.createTextNode(Numero.aString(baseImponibleTotal + impuestosValor, 2)));

			Element plazo = factura.createElement("plazo");
			pago.appendChild(plazo);
			String plazoValor = facturaData.getPlazoDias().toString();
			plazo.appendChild(factura.createTextNode(plazoValor));

			Element unidadTiempo = factura.createElement("unidadTiempo");
			pago.appendChild(unidadTiempo);
			String unidadTiempoValor = "dias";
			unidadTiempo.appendChild(factura.createTextNode(unidadTiempoValor));

			// Detalles
			Element detalles = factura.createElement("detalles");
			rootElement.appendChild(detalles);

			for (FacturaDetalle facturaDetalle : facturaData.getDetalles()) {
				Element detalle = factura.createElement("detalle");
				detalles.appendChild(detalle);

				Element codigoPrincipal = factura.createElement("codigoPrincipal");
				detalle.appendChild(codigoPrincipal);
				String codigoPrincipalValor = facturaDetalle.getItem().getItemId().toString();
				codigoPrincipal.appendChild(factura.createTextNode(codigoPrincipalValor));

				Element codigoAuxiliar = factura.createElement("codigoAuxiliar");
				detalle.appendChild(codigoAuxiliar);
				String codigoAuxiliarValor = facturaDetalle.getFacturaDetalleId().toString();
				codigoAuxiliar.appendChild(factura.createTextNode(codigoAuxiliarValor));

				Element descripcion = factura.createElement("descripcion");
				detalle.appendChild(descripcion);
				String descripcionValor = facturaDetalle.getDescripcion();
				descripcion.appendChild(factura.createTextNode(descripcionValor));

				Element cantidad = factura.createElement("cantidad");
				detalle.appendChild(cantidad);
				String cantidadValor = facturaDetalle.getCantidad().toString();
				cantidad.appendChild(factura.createTextNode(cantidadValor));

				Element precioUnitario = factura.createElement("precioUnitario");
				detalle.appendChild(precioUnitario);
				String precioUnitarioValor = facturaDetalle.getPrecioUnitario().toString();
				precioUnitario.appendChild(factura.createTextNode(precioUnitarioValor));

				Element descuento = factura.createElement("descuento");
				detalle.appendChild(descuento);
				String descuentoValor = facturaDetalle.getDescuentoValor().toString();
				descuento.appendChild(factura.createTextNode(descuentoValor));

				Element precioTotalSinImpuesto = factura.createElement("precioTotalSinImpuesto");
				detalle.appendChild(precioTotalSinImpuesto);
				String precioTotalSinImpuestoValor = facturaDetalle.precioTotalSinImpuesto().toString();
				precioTotalSinImpuesto.appendChild(factura.createTextNode(precioTotalSinImpuestoValor));

				// Impuestos de cada detalle de la factura
				Element impuestos = factura.createElement("impuestos");
				detalle.appendChild(impuestos);

				List<FacturaDetalleImpuesto> facturaDetalleImpuesto = facturaDetalle.getImpuestos();
				for (int k = 0; k <= facturaDetalleImpuesto.size() - 1; k++) {
					FacturaDetalleImpuesto impuestoDetalle = facturaDetalleImpuesto.get(k);
					Element impuesto = factura.createElement("impuesto");
					impuestos.appendChild(impuesto);

					Element codigoImp = factura.createElement("codigo");
					impuesto.appendChild(codigoImp);
					String codigoImpValor = impuestoDetalle.getImpuesto().getImpuestoTipo().getImpuestoTipoSri();
					codigoImp.appendChild(factura.createTextNode(codigoImpValor));

					Element codigoPorcentajeImp = factura.createElement("codigoPorcentaje");
					impuesto.appendChild(codigoPorcentajeImp);
					codigoPorcentajeImp
							.appendChild(factura.createTextNode(impuestoDetalle.getImpuesto().getSriCodigo()));

					Element tarifaImp = factura.createElement("tarifa");
					impuesto.appendChild(tarifaImp);
					tarifaImp.appendChild(
							factura.createTextNode(Numero.aString(impuestoDetalle.getImpuesto().getPorcentaje(), 2)));

					Element baseImponibleImp = factura.createElement("baseImponible");
					impuesto.appendChild(baseImponibleImp);
					baseImponibleImp
							.appendChild(factura.createTextNode(Numero.aString(impuestoDetalle.getBaseImponible(), 2)));

					Element valorImp = factura.createElement("valor");
					impuesto.appendChild(valorImp);
					valorImp.appendChild(factura.createTextNode(Numero.aString(impuestoDetalle.getImpuestoValor(), 2)));
				}

			}

			// Informacion adicional
			Element infoAdicional = factura.createElement("infoAdicional");
			rootElement.appendChild(infoAdicional);

			Element campoAdicionalTeléfono = factura.createElement("campoAdicional");
			infoAdicional.appendChild(campoAdicionalTeléfono);
			Attr nombreTeléfono = factura.createAttribute("nombre");
			nombreTeléfono.setValue("Teléfono:");
			campoAdicionalTeléfono.setAttributeNode(nombreTeléfono);
			String telefono = facturaData.getPersona().getTelefono();
			campoAdicionalTeléfono.appendChild(factura.createTextNode(telefono));

			String email = facturaData.getPersona().getEmail();
			if (email != null && !email.equals("")) {
				Element campoAdicionalCorreo = factura.createElement("campoAdicional");
				infoAdicional.appendChild(campoAdicionalCorreo);
				Attr nombreCorreo = factura.createAttribute("nombre");
				nombreCorreo.setValue("Correo Electrónico:");
				campoAdicionalCorreo.setAttributeNode(nombreCorreo);
				campoAdicionalCorreo.appendChild(factura.createTextNode(email));

			}
			// ********* RETURN XML TIPO DOCUMENT ***********
			return factura;

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException(e.getMessage(), e);
		}
	}

}

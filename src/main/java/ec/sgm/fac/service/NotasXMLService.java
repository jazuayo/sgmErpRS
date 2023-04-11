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

@Service
public class NotasXMLService {
	private static final Logger LOGGER = LogManager.getLogger(NotasXMLService.class);
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private XMLServices serviceXML;
	@Autowired
	private FacturaRepository repositoryFactura;
	private String tipoEmision = "1"; // Emision normal

	/**
	 * Procesar el XML de nota
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	public String generaArchivoXml(Long documentoId) throws SigmaException {
		try {
			Factura nota = repositoryFactura.findByIdWithDetalles(documentoId).orElse(null);
			if (nota == null) {
				throw new SigmaException("Nota de credito: " + documentoId + " no encontrada.");
			}
			String organizacionCod = nota.getOrganizacion().getOrganizacionCod();
			String sriCeDocPath = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			// Generar XML
			Document doc = generarNotaXML(documentoId);
			String claveAcceso = doc.getElementsByTagName("claveAcceso").item(0).getTextContent();
			// Guardar XML no firmado
			serviceXML.guardarXML(sriCeDocPath + claveAcceso + ".xml", doc);
			// Firmar
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
			throw new SigmaException("Error en proceso XML de nota.", e);
		}
	}

	public Document generarNotaXML(Long documentoId) throws SigmaException {
		try {
			System.out.println("\nGenerando XML...");
			Factura notaCreditoData = repositoryFactura.findByIdWithDetalles(documentoId).orElse(null);
			String organizacionCod = notaCreditoData.getOrganizacion().getOrganizacionCod();
			Set<FacturaImpuesto> notaImpuestos = notaCreditoData.getImpuestos();
			Factura facturaModificada = notaCreditoData.getFacturaModifica();
			// XML
			DocumentBuilderFactory Factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder notaBuilder = Factory.newDocumentBuilder();

			// root elements
			Document docNota = notaBuilder.newDocument();
			Element notaElement = docNota.createElement("notaCredito");
			docNota.appendChild(notaElement);
			Attr id = docNota.createAttribute("id");
			id.setValue("comprobante");
			notaElement.setAttributeNode(id);
			Attr version = docNota.createAttribute("version");
			version.setValue("1.0.0");
			notaElement.setAttributeNode(version);

			// Info tributaria
			Element infoTributaria = docNota.createElement("infoTributaria");
			notaElement.appendChild(infoTributaria);

			Element elementAmbiente = docNota.createElement("ambiente");
			infoTributaria.appendChild(elementAmbiente);
			String ambiente = serviceParametros.findValorByClave("sriCeAmbiente", organizacionCod);
			elementAmbiente.appendChild(docNota.createTextNode(ambiente));

			Element elementTipoEmision = docNota.createElement("tipoEmision");
			infoTributaria.appendChild(elementTipoEmision);
			elementTipoEmision.appendChild(docNota.createTextNode(tipoEmision));

			Element elementRazonSocial = docNota.createElement("razonSocial");
			infoTributaria.appendChild(elementRazonSocial);
			String razonSocial = serviceParametros.findValorByClave("sriRazonSocial", organizacionCod);
			elementRazonSocial.appendChild(docNota.createTextNode(razonSocial));

			Element elementNombreComercial = docNota.createElement("nombreComercial");
			infoTributaria.appendChild(elementNombreComercial);
			String nombreComercial = serviceParametros.findValorByClave("sriNombreComercial", organizacionCod);
			elementNombreComercial.appendChild(docNota.createTextNode(nombreComercial));

			Element elementRuc = docNota.createElement("ruc");
			infoTributaria.appendChild(elementRuc);
			String ruc = serviceParametros.findValorByClave("sriRuc", organizacionCod);
			elementRuc.appendChild(docNota.createTextNode(ruc));

			Element elementClaveAcceso = docNota.createElement("claveAcceso");
			infoTributaria.appendChild(elementClaveAcceso);
			elementClaveAcceso.appendChild(docNota.createTextNode(notaCreditoData.getAutorizacionNumero()));

			Element elementCodDoc = docNota.createElement("codDoc");
			infoTributaria.appendChild(elementCodDoc);
			String codDoc = notaCreditoData.getDocumento().getSriCe();
			elementCodDoc.appendChild(docNota.createTextNode(codDoc));

			Element elementEstab = docNota.createElement("estab");
			infoTributaria.appendChild(elementEstab);
			String estab = notaCreditoData.getDocumentoNumero().substring(0, 3);
			elementEstab.appendChild(docNota.createTextNode(estab));

			Element elementPtoEmi = docNota.createElement("ptoEmi");
			infoTributaria.appendChild(elementPtoEmi);
			String ptoEmi = notaCreditoData.getDocumentoNumero().substring(4, 7);
			elementPtoEmi.appendChild(docNota.createTextNode(ptoEmi));

			Element elementSecuencial = docNota.createElement("secuencial");
			infoTributaria.appendChild(elementSecuencial);
			String secuencia = notaCreditoData.getDocumentoNumero().substring(8);
			elementSecuencial.appendChild(docNota.createTextNode(secuencia));

			Element elementDirMatriz = docNota.createElement("dirMatriz");
			infoTributaria.appendChild(elementDirMatriz);
			String dirMatriz = serviceParametros.findValorByClave("sriMatrizDir", organizacionCod);
			elementDirMatriz.appendChild(docNota.createTextNode(dirMatriz));

			// TODO temporal
			String sriAgenteRetRes = serviceParametros.findValorByClaveOpcional("sriAgenteRetRes", organizacionCod);
			if (sriAgenteRetRes != null) {
				Element agenteRetencion = docNota.createElement("agenteRetencion");
				infoTributaria.appendChild(agenteRetencion);
				agenteRetencion.appendChild(docNota.createTextNode(sriAgenteRetRes));
			}
			String sriRegimen = serviceParametros.findValorByClaveOpcional("sriRegimen", organizacionCod);
			if (sriRegimen != null) {
				Element contribuyenteRimpe = docNota.createElement("contribuyenteRimpe");
				infoTributaria.appendChild(contribuyenteRimpe);
				contribuyenteRimpe.appendChild(docNota.createTextNode(sriRegimen));
			}

			// Informacion de nota de credito
			Element infoNotaCredito = docNota.createElement("infoNotaCredito");
			notaElement.appendChild(infoNotaCredito);

			Element elementFechaEmision = docNota.createElement("fechaEmision");
			infoNotaCredito.appendChild(elementFechaEmision);
			String fechaEmite = Fecha.formatoReportes(notaCreditoData.getFechaEmite());
			elementFechaEmision.appendChild(docNota.createTextNode(fechaEmite));

			Element elementDirEstablecimiento = docNota.createElement("dirEstablecimiento");
			infoNotaCredito.appendChild(elementDirEstablecimiento);
			String dirEstablecimiento = serviceParametros.findValorByClave("sriEstablecimientoDir", organizacionCod);
			elementDirEstablecimiento.appendChild(docNota.createTextNode(dirEstablecimiento));

			Element elementTipoIdentificacionComprador = docNota.createElement("tipoIdentificacionComprador");
			infoNotaCredito.appendChild(elementTipoIdentificacionComprador);
			String tipoIdentificacionComprador = notaCreditoData.getPersona().getPersonaTipo().getTipoId()
					.getSriCeVenta();
			elementTipoIdentificacionComprador.appendChild(docNota.createTextNode(tipoIdentificacionComprador));

			Element elementRazonSocialComprador = docNota.createElement("razonSocialComprador");
			infoNotaCredito.appendChild(elementRazonSocialComprador);
			String razonSocialComprador = notaCreditoData.getPersona().getNombre();
			elementRazonSocialComprador.appendChild(docNota.createTextNode(razonSocialComprador));

			Element elementIdentificacionComprador = docNota.createElement("identificacionComprador");
			infoNotaCredito.appendChild(elementIdentificacionComprador);
			String identificacionComprador = notaCreditoData.getPersona().getNumeroId();
			elementIdentificacionComprador.appendChild(docNota.createTextNode(identificacionComprador));

			String contriEspecial = serviceParametros.findValorByClave("sriContribuyenteEspecial", organizacionCod);
			if (!contriEspecial.equals("NO")) {
				Element elementContribuyenteEspecial = docNota.createElement("contribuyenteEspecial");
				infoNotaCredito.appendChild(elementContribuyenteEspecial);
				elementContribuyenteEspecial.appendChild(docNota.createTextNode(contriEspecial));
			}

			Element elementObligadoContabilidad = docNota.createElement("obligadoContabilidad");
			infoNotaCredito.appendChild(elementObligadoContabilidad);
			String obligadoConta = serviceParametros.findValorByClave("sriObligadoContabilidad", organizacionCod);
			elementObligadoContabilidad.appendChild(docNota.createTextNode(obligadoConta));

			Element codDocModificado = docNota.createElement("codDocModificado");
			infoNotaCredito.appendChild(codDocModificado);
			String documentoIdMod = facturaModificada.getDocumento().getSriCe();
			codDocModificado.appendChild(docNota.createTextNode(documentoIdMod));

			Element numDocModificado = docNota.createElement("numDocModificado");
			infoNotaCredito.appendChild(numDocModificado);
			numDocModificado.appendChild(docNota.createTextNode(facturaModificada.getDocumentoNumero()));

			Element fechaEmisionDocSustento = docNota.createElement("fechaEmisionDocSustento");
			infoNotaCredito.appendChild(fechaEmisionDocSustento);
			String fechaEmiteSust = Fecha.formatoReportes(facturaModificada.getFechaEmite());
			fechaEmisionDocSustento.appendChild(docNota.createTextNode(fechaEmiteSust));

			Double baseImponibleTotal = FacturaImpuesto.sumaBaseImponible(notaImpuestos, null);
			Double impuestosValor = FacturaImpuesto.sumaImpuestoValor(notaImpuestos);

			Element elementTotalSinImpuestos = docNota.createElement("totalSinImpuestos");
			infoNotaCredito.appendChild(elementTotalSinImpuestos);
			elementTotalSinImpuestos.appendChild(docNota.createTextNode(Numero.aString(baseImponibleTotal, 2)));

			Element valorModificacion = docNota.createElement("valorModificacion");
			infoNotaCredito.appendChild(valorModificacion);
			valorModificacion
					.appendChild(docNota.createTextNode(Numero.aString(baseImponibleTotal + impuestosValor, 2)));

			Element moneda = docNota.createElement("moneda");
			infoNotaCredito.appendChild(moneda);
			moneda.appendChild(docNota.createTextNode("DOLAR"));

			Element totalConImpuestos = docNota.createElement("totalConImpuestos");
			infoNotaCredito.appendChild(totalConImpuestos);

			for (FacturaImpuesto facturaImpuesto : notaImpuestos) {
				Element totalImpuesto = docNota.createElement("totalImpuesto");
				totalConImpuestos.appendChild(totalImpuesto);

				Element codigo = docNota.createElement("codigo");
				totalImpuesto.appendChild(codigo);
				codigo.appendChild(
						docNota.createTextNode(facturaImpuesto.getImpuesto().getImpuestoTipo().getImpuestoTipoSri()));

				Element codigoPorcentaje = docNota.createElement("codigoPorcentaje");
				totalImpuesto.appendChild(codigoPorcentaje);
				codigoPorcentaje.appendChild(docNota.createTextNode(facturaImpuesto.getImpuesto().getSriCodigo()));

				Element baseImponible = docNota.createElement("baseImponible");
				totalImpuesto.appendChild(baseImponible);
				baseImponible
						.appendChild(docNota.createTextNode(Numero.aString(facturaImpuesto.getBaseImponible(), 2)));

				Element valor = docNota.createElement("valor");
				totalImpuesto.appendChild(valor);
				valor.appendChild(docNota.createTextNode(Numero.aString(facturaImpuesto.getImpuestoValor(), 2)));

			}
			Element motivo = docNota.createElement("motivo");
			infoNotaCredito.appendChild(motivo);
			motivo.appendChild(docNota.createTextNode("."));

			// Detalles
			Element detalles = docNota.createElement("detalles");
			notaElement.appendChild(detalles);

			for (FacturaDetalle facturaDetalle : notaCreditoData.getDetalles()) {
				Element detalle = docNota.createElement("detalle");
				detalles.appendChild(detalle);

				Element codigoInterno = docNota.createElement("codigoInterno");
				detalle.appendChild(codigoInterno);
				String codigoInternoValor = facturaDetalle.getItem().getItemId().toString();
				codigoInterno.appendChild(docNota.createTextNode(codigoInternoValor));

				Element codigoAdicional = docNota.createElement("codigoAdicional");
				detalle.appendChild(codigoAdicional);
				String codigoAdicionalValor = facturaDetalle.getFacturaDetalleId().toString();
				codigoAdicional.appendChild(docNota.createTextNode(codigoAdicionalValor));

				Element descripcion = docNota.createElement("descripcion");
				detalle.appendChild(descripcion);
				String descripcionValor = facturaDetalle.getDescripcion();
				descripcion.appendChild(docNota.createTextNode(descripcionValor));

				Element cantidad = docNota.createElement("cantidad");
				detalle.appendChild(cantidad);
				String cantidadValor = facturaDetalle.getCantidad().toString();
				cantidad.appendChild(docNota.createTextNode(cantidadValor));

				Element precioUnitario = docNota.createElement("precioUnitario");
				detalle.appendChild(precioUnitario);
				String precioUnitarioValor = facturaDetalle.getPrecioUnitario().toString();
				precioUnitario.appendChild(docNota.createTextNode(precioUnitarioValor));

				Element descuento = docNota.createElement("descuento");
				detalle.appendChild(descuento);
				String descuentoValor = facturaDetalle.getDescuentoValor().toString();
				descuento.appendChild(docNota.createTextNode(descuentoValor));

				Element precioTotalSinImpuesto = docNota.createElement("precioTotalSinImpuesto");
				detalle.appendChild(precioTotalSinImpuesto);
				String precioTotalSinImpuestoValor = facturaDetalle.precioTotalSinImpuesto().toString();
				precioTotalSinImpuesto.appendChild(docNota.createTextNode(precioTotalSinImpuestoValor));

				// Impuestos de cada detalle de la factura
				Element impuestos = docNota.createElement("impuestos");
				detalle.appendChild(impuestos);

				List<FacturaDetalleImpuesto> facturaDetalleImpuesto = facturaDetalle.getImpuestos();
				for (FacturaDetalleImpuesto impuestoDetalle : facturaDetalleImpuesto) {
					Element impuesto = docNota.createElement("impuesto");
					impuestos.appendChild(impuesto);

					Element codigoImp = docNota.createElement("codigo");
					impuesto.appendChild(codigoImp);
					String codigoImpValor = impuestoDetalle.getImpuesto().getImpuestoTipo().getImpuestoTipoSri();
					codigoImp.appendChild(docNota.createTextNode(codigoImpValor));

					Element codigoPorcentajeImp = docNota.createElement("codigoPorcentaje");
					impuesto.appendChild(codigoPorcentajeImp);
					codigoPorcentajeImp
							.appendChild(docNota.createTextNode(impuestoDetalle.getImpuesto().getSriCodigo()));

					Element tarifaImp = docNota.createElement("tarifa");
					impuesto.appendChild(tarifaImp);
					tarifaImp.appendChild(
							docNota.createTextNode(Numero.aString(impuestoDetalle.getImpuesto().getPorcentaje(), 2)));

					Element baseImponibleImp = docNota.createElement("baseImponible");
					impuesto.appendChild(baseImponibleImp);
					baseImponibleImp
							.appendChild(docNota.createTextNode(Numero.aString(impuestoDetalle.getBaseImponible(), 2)));

					Element valorImp = docNota.createElement("valor");
					impuesto.appendChild(valorImp);
					valorImp.appendChild(docNota.createTextNode(Numero.aString(impuestoDetalle.getImpuestoValor(), 2)));
				}

			}

			return docNota;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en generar XML de nota.", e);
		}
	}
}

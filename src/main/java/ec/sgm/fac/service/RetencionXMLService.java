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
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.entity.Retencion;
import ec.sgm.fac.entity.RetencionDetalle;
import ec.sgm.fac.repository.RetencionRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class RetencionXMLService {
	private static final Logger LOGGER = LogManager.getLogger(RetencionXMLService.class);
	@Autowired
	private RetencionRepository retencionRepository;
	@Autowired
	private XMLServices servicesXML;
	@Autowired
	private ParametroService serviceParametros;

	public String generaArchivosXml(Long documentoId) throws SigmaException {
		try {
			Retencion retencion = retencionRepository.findById(documentoId).orElse(null);
			if (retencion == null) {
				throw new SigmaException("Retencion:" + documentoId + " no encontrada:");
			}
			String organizacionCod = retencion.getOrganizacion().getOrganizacionCod();
			String pathArchivoXml = crearArchivoRetencionXML(documentoId);
			String resultado = SigneService.firma(serviceParametros.findValorByClave("sriCeCertPath", organizacionCod),
					serviceParametros.findValorByClave("sriCeCertClave", organizacionCod), 0, pathArchivoXml);
			return resultado;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException(e.getMessage(), e);
		}
	}

	public String crearArchivoRetencionXML(Long retencionId) throws SigmaException {
		try {
			Retencion retencion = retencionRepository.findById(retencionId).orElse(null);
			if (retencion == null) {
				throw new SigmaException("Retencion:" + retencionId + " no encontrada.");
			}
			List<RetencionDetalle> retencionDetalle = retencion.getDetalles();
			if (retencionDetalle.size() == 0) {
				throw new SigmaException("Retencion:" + retencionId + " sin lista de detalles.");
			}
			Factura facturaRet = retencionDetalle.get(0).getFactura();
			String organizacionCod = retencion.getOrganizacion().getOrganizacionCod();
			// ********* XML *********

			// Builder
			DocumentBuilderFactory retencionFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder retencionBuilder = retencionFactory.newDocumentBuilder();
			// root elements
			Document retencionDoc = retencionBuilder.newDocument();
			Element rootElement = retencionDoc.createElement("comprobanteRetencion");
			retencionDoc.appendChild(rootElement);
			// Atributos de root
			Attr id = retencionDoc.createAttribute("id");
			id.setValue("comprobante");
			rootElement.setAttributeNode(id);
			Attr version = retencionDoc.createAttribute("version");
			version.setValue("2.0.0");
			rootElement.setAttributeNode(version);
			// Informacion tributaria
			Element infoTributaria = retencionDoc.createElement("infoTributaria");
			rootElement.appendChild(infoTributaria);

			Element elementAmbiente = retencionDoc.createElement("ambiente");
			infoTributaria.appendChild(elementAmbiente);
			String sriCeAmbiente = serviceParametros.findValorByClave("sriCeAmbiente", organizacionCod);
			elementAmbiente.appendChild(retencionDoc.createTextNode(sriCeAmbiente));

			Element elementTipoEmision = retencionDoc.createElement("tipoEmision");
			infoTributaria.appendChild(elementTipoEmision);
			String tipoEmision = "1";
			elementTipoEmision.appendChild(retencionDoc.createTextNode(tipoEmision));

			Element elementRazonSocial = retencionDoc.createElement("razonSocial");
			infoTributaria.appendChild(elementRazonSocial);
			String sriRazonSocial = serviceParametros.findValorByClave("sriRazonSocial", organizacionCod);
			elementRazonSocial.appendChild(retencionDoc.createTextNode(sriRazonSocial));

			Element elementNombreComercial = retencionDoc.createElement("nombreComercial");
			infoTributaria.appendChild(elementNombreComercial);
			String sriNombreComercial = serviceParametros.findValorByClave("sriNombreComercial", organizacionCod);
			elementNombreComercial.appendChild(retencionDoc.createTextNode(sriNombreComercial));

			Element elementRuc = retencionDoc.createElement("ruc");
			infoTributaria.appendChild(elementRuc);
			String sriRuc = serviceParametros.findValorByClave("sriRuc", organizacionCod);
			elementRuc.appendChild(retencionDoc.createTextNode(sriRuc));

			Element elementClaveAcceso = retencionDoc.createElement("claveAcceso");
			infoTributaria.appendChild(elementClaveAcceso);
			elementClaveAcceso.appendChild(retencionDoc.createTextNode(retencion.getAutorizacionNumero()));

			Element elementCodDoc = retencionDoc.createElement("codDoc");
			infoTributaria.appendChild(elementCodDoc);
			elementCodDoc.appendChild(retencionDoc.createTextNode(retencion.getDocumento().getSriCe()));

			// ---- Documento numero
			String documentoNumeroRet = retencion.getDocumentoNumero();

			if (documentoNumeroRet.length() != 17) {
				throw new SigmaException("La longitud del documento de la retencion debe ser 17 en lugar de:"
						+ documentoNumeroRet.length());
			}

			Element elementEstab = retencionDoc.createElement("estab");
			infoTributaria.appendChild(elementEstab);
			String estab = documentoNumeroRet.substring(0, 3);
			elementEstab.appendChild(retencionDoc.createTextNode(estab));

			Element elementPtoEmi = retencionDoc.createElement("ptoEmi");
			infoTributaria.appendChild(elementPtoEmi);
			String ptoEmi = documentoNumeroRet.substring(4, 7);
			elementPtoEmi.appendChild(retencionDoc.createTextNode(ptoEmi));

			Element elementSecuencial = retencionDoc.createElement("secuencial");
			infoTributaria.appendChild(elementSecuencial);
			String secuencia = documentoNumeroRet.substring(8);
			elementSecuencial.appendChild(retencionDoc.createTextNode(secuencia));

			Element elementDirMatriz = retencionDoc.createElement("dirMatriz");
			infoTributaria.appendChild(elementDirMatriz);
			String sriMatrizDir = serviceParametros.findValorByClave("sriMatrizDir", organizacionCod);
			elementDirMatriz.appendChild(retencionDoc.createTextNode(sriMatrizDir));

			// TODO temporal
			String sriAgenteRetRes = serviceParametros.findValorByClave("sriAgenteRetRes", organizacionCod);
			if (sriAgenteRetRes != null) {
				Element agenteRetencion = retencionDoc.createElement("agenteRetencion");
				infoTributaria.appendChild(agenteRetencion);
				agenteRetencion.appendChild(retencionDoc.createTextNode(sriAgenteRetRes));
			}
			String sriRegimen = serviceParametros.findValorByClaveOpcional("sriRegimen", organizacionCod);
			if (sriRegimen != null) {
				Element contribuyenteRimpe = retencionDoc.createElement("contribuyenteRimpe");
				infoTributaria.appendChild(contribuyenteRimpe);
				contribuyenteRimpe.appendChild(retencionDoc.createTextNode(sriRegimen));
			}

			// infoCompRetencion

			Element infoCompRetencion = retencionDoc.createElement("infoCompRetencion");
			rootElement.appendChild(infoCompRetencion);

			Element elementFechaEmision = retencionDoc.createElement("fechaEmision");
			infoCompRetencion.appendChild(elementFechaEmision);
			String fechaEmision = Fecha.formatoReportes(retencion.getFechaEmite());
			elementFechaEmision.appendChild(retencionDoc.createTextNode(fechaEmision));

			Element elementDirEstablecimiento = retencionDoc.createElement("dirEstablecimiento");
			infoCompRetencion.appendChild(elementDirEstablecimiento);
			String sriEstablecimientoDir = serviceParametros.findValorByClave("sriEstablecimientoDir", organizacionCod);
			elementDirEstablecimiento.appendChild(retencionDoc.createTextNode(sriEstablecimientoDir));

			Element elementObligadoContabilidad = retencionDoc.createElement("obligadoContabilidad");
			infoCompRetencion.appendChild(elementObligadoContabilidad);
			String sriObligadoContabilidad = serviceParametros.findValorByClave("sriObligadoContabilidad",
					organizacionCod);
			elementObligadoContabilidad.appendChild(retencionDoc.createTextNode(sriObligadoContabilidad));

			Element elementTipoIdentificacionComprador = retencionDoc.createElement("tipoIdentificacionSujetoRetenido");
			infoCompRetencion.appendChild(elementTipoIdentificacionComprador);
			String tipoIdentificacionSujetoRetenido = facturaRet.getPersona().getPersonaTipo().getTipoId()
					.getSriCeVenta();
			elementTipoIdentificacionComprador
					.appendChild(retencionDoc.createTextNode(tipoIdentificacionSujetoRetenido));

			Element elementParteRel = retencionDoc.createElement("parteRel");
			infoCompRetencion.appendChild(elementParteRel);
			elementParteRel.appendChild(retencionDoc.createTextNode("NO"));

			Element elementRazonSocialSujetoRetenido = retencionDoc.createElement("razonSocialSujetoRetenido");
			infoCompRetencion.appendChild(elementRazonSocialSujetoRetenido);
			String razonSocialSujetoRetenido = facturaRet.getPersona().getNombre();
			elementRazonSocialSujetoRetenido.appendChild(retencionDoc.createTextNode(razonSocialSujetoRetenido));

			Element elementIdentificacionSujetoRetenido = retencionDoc.createElement("identificacionSujetoRetenido");
			infoCompRetencion.appendChild(elementIdentificacionSujetoRetenido);
			String identificacionSujetoRetenido = facturaRet.getPersona().getNumeroId();
			elementIdentificacionSujetoRetenido.appendChild(retencionDoc.createTextNode(identificacionSujetoRetenido));

			Element elementPeriodoFiscal = retencionDoc.createElement("periodoFiscal");
			infoCompRetencion.appendChild(elementPeriodoFiscal);
			elementPeriodoFiscal
					.appendChild(retencionDoc.createTextNode(Fecha.periodoRetencion(facturaRet.getFechaEmite())));

			// docsSustento --- detalles

			Set<FacturaImpuesto> facturaRetImpuestos = facturaRet.getImpuestos();
			Double baseImponibleTotal = FacturaImpuesto.sumaBaseImponible(facturaRetImpuestos, null);
			Double baseImponibleConImp = FacturaImpuesto.sumaBaseImponible(facturaRetImpuestos, true);
			Double baseImponibleSinImp = FacturaImpuesto.sumaBaseImponible(facturaRetImpuestos, false);
			Double impuestosValor = FacturaImpuesto.sumaImpuestoValor(facturaRetImpuestos);

			Element docsSustento = retencionDoc.createElement("docsSustento");
			rootElement.appendChild(docsSustento);

			Element docsSustentoDet = retencionDoc.createElement("docSustento");
			docsSustento.appendChild(docsSustentoDet);

			Element elementCodSustento = retencionDoc.createElement("codSustento");
			docsSustentoDet.appendChild(elementCodSustento);
			elementCodSustento.appendChild(retencionDoc.createTextNode("02"));

			Element elementcodDocSustento = retencionDoc.createElement("codDocSustento");
			docsSustentoDet.appendChild(elementcodDocSustento);
			elementcodDocSustento.appendChild(retencionDoc
					.createTextNode(facturaRet.getPersona().getPersonaTipo().getTipoId().getSriAtsCompra()));

			Element elementNumDocSustento = retencionDoc.createElement("numDocSustento");
			docsSustentoDet.appendChild(elementNumDocSustento);
			String numDocSustento = facturaRet.getDocumentoNumero().replace("-", "");
			elementNumDocSustento.appendChild(retencionDoc.createTextNode(numDocSustento));

			Element elementFechaEmisionDocSustento = retencionDoc.createElement("fechaEmisionDocSustento");
			docsSustentoDet.appendChild(elementFechaEmisionDocSustento);
			String fechaEmisionDocSustento = Fecha.formatoReportes(facturaRet.getFechaEmite());
			elementFechaEmisionDocSustento.appendChild(retencionDoc.createTextNode(fechaEmisionDocSustento));

			Element elementPagoLocExt = retencionDoc.createElement("pagoLocExt");
			docsSustentoDet.appendChild(elementPagoLocExt);
			elementPagoLocExt.appendChild(retencionDoc.createTextNode("01"));

			Element elementTotalSinImpuestos = retencionDoc.createElement("totalSinImpuestos");
			docsSustentoDet.appendChild(elementTotalSinImpuestos);
			elementTotalSinImpuestos.appendChild(retencionDoc.createTextNode(Numero.aString(baseImponibleSinImp, 2)));

			Element elementImporteTotal = retencionDoc.createElement("importeTotal");
			docsSustentoDet.appendChild(elementImporteTotal);
			elementImporteTotal.appendChild(retencionDoc.createTextNode(Numero.aString(baseImponibleTotal, 2)));

			// -- impuestosDocSustento

			Element impuestosDocSustentoDet = retencionDoc.createElement("impuestosDocSustento");
			docsSustentoDet.appendChild(impuestosDocSustentoDet);

			for (FacturaImpuesto impuestoFac : facturaRet.getImpuestos()) {

				Element impuestoDocSustentoDet = retencionDoc.createElement("impuestoDocSustento");
				impuestosDocSustentoDet.appendChild(impuestoDocSustentoDet);

				Element elementCodImpuestoDocSustento = retencionDoc.createElement("codImpuestoDocSustento");
				impuestoDocSustentoDet.appendChild(elementCodImpuestoDocSustento);
				elementCodImpuestoDocSustento.appendChild(
						retencionDoc.createTextNode(impuestoFac.getImpuesto().getImpuestoTipo().getImpuestoTipoSri()));

				Element elementCodigoPorcentaje = retencionDoc.createElement("codigoPorcentaje");
				impuestoDocSustentoDet.appendChild(elementCodigoPorcentaje);
				String codigoPorcentaje = impuestoFac.getImpuesto().getSriCodigo();
				elementCodigoPorcentaje.appendChild(retencionDoc.createTextNode(codigoPorcentaje));

				Element elementBaseImponible = retencionDoc.createElement("baseImponible");
				impuestoDocSustentoDet.appendChild(elementBaseImponible);
				elementBaseImponible
						.appendChild(retencionDoc.createTextNode(Numero.aString(impuestoFac.getBaseImponible(), 2)));

				Element elementTarifa = retencionDoc.createElement("tarifa");
				impuestoDocSustentoDet.appendChild(elementTarifa);
				elementTarifa.appendChild(retencionDoc.createTextNode(impuestoFac.getPorcentaje().toString()));

				Element elementValorImpuesto = retencionDoc.createElement("valorImpuesto");
				impuestoDocSustentoDet.appendChild(elementValorImpuesto);
				elementValorImpuesto
						.appendChild(retencionDoc.createTextNode(Numero.aString(impuestoFac.getImpuestoValor(), 2)));
			}

			Element retenciones = retencionDoc.createElement("retenciones");
			docsSustentoDet.appendChild(retenciones);

			for (RetencionDetalle retDetalle : retencionDetalle) {

				Element elementRetencion = retencionDoc.createElement("retencion");
				retenciones.appendChild(elementRetencion);

				Element elementCodigo = retencionDoc.createElement("codigo");
				elementRetencion.appendChild(elementCodigo);
				elementCodigo.appendChild(
						retencionDoc.createTextNode(retDetalle.getImpuesto().getImpuestoTipo().getImpuestoTipoSri()));

				Element elementCodigoRetencion = retencionDoc.createElement("codigoRetencion");
				elementRetencion.appendChild(elementCodigoRetencion);
				elementCodigoRetencion
						.appendChild(retencionDoc.createTextNode(retDetalle.getImpuesto().getSriCodigo()));

				Element elementBaseImponible = retencionDoc.createElement("baseImponible");
				elementRetencion.appendChild(elementBaseImponible);
				elementBaseImponible
						.appendChild(retencionDoc.createTextNode(Numero.aString(retDetalle.getBaseImponible(), 2)));

				Element elementPorcentajeRetener = retencionDoc.createElement("porcentajeRetener");
				elementRetencion.appendChild(elementPorcentajeRetener);
				elementPorcentajeRetener
						.appendChild(retencionDoc.createTextNode(retDetalle.getImpuesto().getPorcentaje().toString()));

				Element elementValorRetenido = retencionDoc.createElement("valorRetenido");
				elementRetencion.appendChild(elementValorRetenido);
				elementValorRetenido
						.appendChild(retencionDoc.createTextNode(Numero.aString(retDetalle.getValorRetenido(), 2)));

			}

			// docSustento -- pagos
			Element pagos = retencionDoc.createElement("pagos");
			docsSustentoDet.appendChild(pagos);

			Element elementPagos = retencionDoc.createElement("pago");
			pagos.appendChild(elementPagos);

			Element elementFormaPago = retencionDoc.createElement("formaPago");
			elementPagos.appendChild(elementFormaPago);
			elementFormaPago.appendChild(retencionDoc.createTextNode(facturaRet.getFormaPago().getFormaPagoSri()));

			Element elementTotal = retencionDoc.createElement("total");
			elementPagos.appendChild(elementTotal);
			elementTotal.appendChild(retencionDoc.createTextNode(Numero.aString(baseImponibleTotal, 2)));

			// Guardar xml generado
			String sriCeDocPath = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			String nombreXML = sriCeDocPath + retencion.getAutorizacionNumero() + ".xml";
			System.out.println("Comprobante retencion en: " + nombreXML);
			return servicesXML.guardarXML(nombreXML, retencionDoc);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al crear xml retencion.", e);
		}
	}
}

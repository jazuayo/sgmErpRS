package ec.sgm.fac.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ec.sgm.SigmaException;
import ec.sgm.ce.util.XMLServices;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.core.Numero;
import ec.sgm.core.Texto;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.FacturaDetalleImpuesto;
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.entity.Retencion;
import ec.sgm.fac.entity.RetencionDetalle;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.fac.repository.RetencionRepository;
import ec.sgm.org.service.ParametroService;
import ec.sgm.sri.entity.ComprobanteAnulado;
import ec.sgm.sri.repository.ComprobanteAnuladoRepository;

@Service
public class AtsService {
	private static final Logger LOGGER = LogManager.getLogger(AtsService.class);
	@Autowired
	private XMLServices serviceXML;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private FacturaRepository facturaRepository;
	@Autowired
	private FacturaService facturaService;
	@Autowired
	private RetencionRepository retencionRepository;
	@Autowired
	private ComprobanteAnuladoRepository comprobanteAnuladoRepository;
	@Autowired
	private RetencionService retencionService;
	// Numero de decimales
	private Integer decimales = 2;

	// calcula desde registros
	String numEstabRuc = "0";

	/**
	 * Genera y guarda el XML de ATS
	 * 
	 * @param fechaGenera
	 * @throws SigmaException
	 */
	public byte[] generaATS(Date fechaGenera, String organizacionCod) throws SigmaException {
		try {
			System.out.println("\n\nCreando ATS");
			System.out.println("Fecha genera-final: " + fechaGenera);
			System.out.println("Organizacion cod: " + organizacionCod);
			// Generar XML
			Document doc = generarXML(fechaGenera, organizacionCod);
			// Guardar XML
			String sriCeDocPath = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			String nombre = "ATS_" + organizacionCod + "_" + Fecha.formatoFechaGuionSeparado(fechaGenera) + ".xml";
			String ruta = serviceXML.guardarXML(sriCeDocPath + nombre, doc);
			System.out.println("XML_ATS generado en: " + ruta);
			// Creando arrayByte para mandar a front
			byte[] arrayByte = Files.readAllBytes(Paths.get(ruta));
			return arrayByte;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en ATS", e);
		}
	}

	// generar el xml
	private Document generarXML(Date fechaGenera, String organizacionCod) throws SigmaException {
		try {
			Date fechaIniciaMes = Fecha.fechaIniciaMes(fechaGenera);

			// ********* XML *********

			// Builder
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			// Root elements
			Document document = docBuilder.newDocument();
			Element rootElement = document.createElement("iva");
			document.appendChild(rootElement);
			// Varios atributos de informante
			Element eleTipoIDInformante = document.createElement("TipoIDInformante");
			rootElement.appendChild(eleTipoIDInformante);
			eleTipoIDInformante.appendChild(document.createTextNode("R"));

			Element eleIdInformante = document.createElement("IdInformante");
			rootElement.appendChild(eleIdInformante);
			String idInformante = serviceParametros.findValorByClave("sriRuc", organizacionCod);
			eleIdInformante.appendChild(document.createTextNode(idInformante));

			Element eleRazonSocial = document.createElement("razonSocial");
			rootElement.appendChild(eleRazonSocial);
			String razonSocial = serviceParametros.findValorByClave("sriRazonSocial", organizacionCod);
			eleRazonSocial.appendChild(document.createTextNode(razonSocial));

			Element eleAnio = document.createElement("Anio");
			rootElement.appendChild(eleAnio);
			eleAnio.appendChild(document.createTextNode(Integer.toString(Fecha.getYear(fechaGenera))));

			Element eleMes = document.createElement("Mes");
			rootElement.appendChild(eleMes);
			eleMes.appendChild(document.createTextNode(Fecha.getMonthMM(fechaGenera)));
			// Elementos
			Element eleNumEstabRuc = document.createElement("numEstabRuc");
			rootElement.appendChild(eleNumEstabRuc);

			Element eleTotalVentas = document.createElement("totalVentas");
			rootElement.appendChild(eleTotalVentas);

			// Fin elementos
			Element eleCodigoOperativo = document.createElement("codigoOperativo");
			rootElement.appendChild(eleCodigoOperativo);
			eleCodigoOperativo.appendChild(document.createTextNode("IVA"));

			// Fechas
			System.out.println("Desde " + fechaIniciaMes);
			System.out.println("Hasta " + fechaGenera);
			// LLamado a etiqueta compras

			document = generaCompras(document, fechaIniciaMes, fechaGenera, organizacionCod, rootElement);

			// LLamado a etiqueta ventas y ventas establecimiento

			double totalVentas = generarVentas(document, fechaIniciaMes, fechaGenera, organizacionCod, rootElement);

			// Llamado a etiqueta genera anulados

			document = generaAnulados(document, organizacionCod, fechaIniciaMes, fechaGenera, rootElement);

			// Fin llamado a listas--actualiza valores de venta y numEstabRuc

			numEstabRuc = ("000" + String.valueOf(numEstabRuc)).trim();
			numEstabRuc = numEstabRuc.substring(numEstabRuc.length() - 3);
			eleNumEstabRuc.appendChild(document.createTextNode(numEstabRuc));

			eleTotalVentas.appendChild(document.createTextNode(Numero.aString(totalVentas, decimales)));

			return document;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al armar archivo XML", e);
		}
	}

	/**
	 * Genera etiquetas de anulados
	 * 
	 * @param document
	 * @param organizacion
	 * @param fechaDesde
	 * @param fechaHasta
	 * @return
	 * @throws SigmaException
	 */
	private Document generaAnulados(Document document, String organizacionCod, Date fechaDesde, Date fechaHasta,
			Element rootElement) throws SigmaException {
		System.out.println("Generando etiquetas de ANULADOS...");
		try {
			List<ComprobanteAnulado> anulados = comprobanteAnuladoRepository.findbyOrganizacionFechas(fechaDesde,
					fechaHasta, organizacionCod/* organizacion */);

			Element eleAnulados = document.createElement("anulados");
			rootElement.appendChild(eleAnulados);

			for (ComprobanteAnulado anulado : anulados) {

				Element detalleAnulados = document.createElement("detalleAnulados");
				eleAnulados.appendChild(detalleAnulados);

				Element tipoComprobante = document.createElement("tipoComprobante");
				detalleAnulados.appendChild(tipoComprobante);
				tipoComprobante
						.appendChild(document.createTextNode(anulado.getComprobanteTipo().getComprobanteTipoCod()));

				Element establecimiento = document.createElement("establecimiento");
				detalleAnulados.appendChild(establecimiento);
				establecimiento.appendChild(document.createTextNode(Texto.lpad(anulado.getEstablecimiento(), 3, "0")));

				Element puntoEmision = document.createElement("puntoEmision");
				detalleAnulados.appendChild(puntoEmision);
				puntoEmision.appendChild(document.createTextNode(Texto.lpad(anulado.getPuntoEmision(), 3, "0")));

				Element secuencialInicio = document.createElement("secuencialInicio");
				detalleAnulados.appendChild(secuencialInicio);
				secuencialInicio.appendChild(document.createTextNode(String.valueOf(anulado.getSecuencialInicio())));

				Element secuencialFin = document.createElement("secuencialFin");
				detalleAnulados.appendChild(secuencialFin);
				secuencialFin.appendChild(document.createTextNode(String.valueOf(anulado.getSecuencialFin())));

				Element autorizacion = document.createElement("autorizacion");
				detalleAnulados.appendChild(autorizacion);
				autorizacion.appendChild(document.createTextNode(anulado.getAutorizacion()));
			}

			return document;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar etiqueta anulados", e);
		}
	}

	/**
	 * Generar etiqueta de Ventas
	 * 
	 * @param document
	 * @param fechaDesde
	 * @param fechaHasta
	 * @param organizacionCod
	 * @param rootElement
	 * @return
	 * @throws SigmaException
	 */
	private double generarVentas(Document document, Date fechaDesde, Date fechaHasta, String organizacionCod,
			Element rootElement) throws SigmaException {
		System.out.println("Generando etiquetas de VENTA...");
		try {

			List<Factura> facturas = facturaRepository.findFacturasPorFechas(Constantes.ORIGEN_VENTA_FACTURA,
					Constantes.ORIGEN_VENTA_NC, Fecha.formatoXML(fechaDesde), Fecha.formatoXML(fechaHasta),
					organizacionCod);
			// agrupo las factura
//			Map<Object, List<Factura>> facturasGrupo = facturas.stream()
//					.collect(Collectors.groupingBy(w -> w.getPersona()));
//			System.out.println(facturasGrupo);
			// agrupo las facturas por persona y tipo de documento
			Map<String, List<Factura>> facturasMap = new HashMap<String, List<Factura>>();
			for (Factura factura : facturas) {
				// key: la persona - tipo de documento - logitud(clave)=>para fisica o
				// electronica
				String key = factura.getPersona().getPersonaId() + "#"
						+ factura.getDocumento().getSriAts()/* factura.getDocumento().getAtsCodigo() */ + "#"
						+ factura.getAutorizacionNumero().length();
				if (facturasMap.containsKey(key)) {
					facturasMap.get(key).add(factura);
				} else {
					List<Factura> list = new ArrayList<Factura>();
					list.add(factura);
					facturasMap.put(key, list);
				}

			}
			// proceso con las facturas agrupadas
			Factura primeraFactura;
			List<Retencion> retenciones;
			List<FacturaDetalleImpuesto> detalleImpuestos;

			Element ventas = document.createElement("ventas");
			rootElement.appendChild(ventas);
			double ventasSumatoria = 0.0;
			for (Map.Entry<String, List<Factura>> entry : facturasMap.entrySet()) {
				primeraFactura = (Factura) entry.getValue().get(0);

				Element detalleVentas = document.createElement("detalleVentas");
				ventas.appendChild(detalleVentas);

				Element tpIdCliente = document.createElement("tpIdCliente");
				detalleVentas.appendChild(tpIdCliente);
				String tpIdClienteAux = primeraFactura.getPersona().getPersonaTipo().getTipoId().getSriCeVenta();
				tpIdCliente.appendChild(document.createTextNode(tpIdClienteAux));

				Element idCliente = document.createElement("idCliente");
				detalleVentas.appendChild(idCliente);
				String idClienteAux = primeraFactura.getPersona().getNumeroId();
				idCliente.appendChild(document.createTextNode(idClienteAux));

				Element parteRelVtas = document.createElement("parteRelVtas");
				detalleVentas.appendChild(parteRelVtas);
				parteRelVtas.appendChild(document.createTextNode("NO"));

				Element tipoComprobante = document.createElement("tipoComprobante");
				detalleVentas.appendChild(tipoComprobante);
				String tipoComprobanteAux = primeraFactura.getDocumento().getSriAts();
				tipoComprobante.appendChild(document.createTextNode(tipoComprobanteAux));

				Element tipoEmision = document.createElement("tipoEmision");
				detalleVentas.appendChild(tipoEmision);
				String tipoEmisionAux = "";
				if (primeraFactura.getAutorizacionNumero().length() == 10) {
					tipoEmisionAux = "F";
				} else {
					tipoEmisionAux = "E";
				}
				tipoEmision.appendChild(document.createTextNode(tipoEmisionAux));

				double baseImponibleIvaCero = 0.0;
				double baseImpGrav = 0.0;
				double montoIva = 0.0;
				double valorRetIva = 0.0;
				double valorRetRenta = 0.0;
				for (Factura factura : entry.getValue()) {
					retenciones = retencionRepository.findFullByFactura(factura);

					Set<FacturaImpuesto> facturaRetImpuestos = factura.getImpuestos();
					Double baseImponibleConImp = FacturaImpuesto.sumaBaseImponible(facturaRetImpuestos, true);
					Double baseImponibleSinImp = FacturaImpuesto.sumaBaseImponible(facturaRetImpuestos, false);
					Double impuestosValor = FacturaImpuesto.sumaImpuestoValor(facturaRetImpuestos);

					baseImponibleIvaCero += baseImponibleSinImp;
					baseImpGrav += baseImponibleConImp;
					montoIva += impuestosValor;
					valorRetIva += retencionService.valorRetenidoPorTipoImpuesto(retenciones,
							Constantes.IMPUESTO_TIPO_IVA_RETENCION);
					valorRetRenta += retencionService.valorRetenidoPorTipoImpuesto(retenciones,
							Constantes.IMPUESTO_TIPO_RENTA_RETENCION);
				}

				Element numeroComprobantes = document.createElement("numeroComprobantes");
				detalleVentas.appendChild(numeroComprobantes);
				String numComprobantes = String.valueOf(entry.getValue().size());
				numeroComprobantes.appendChild(document.createTextNode(numComprobantes));

				Element baseNoGraIva = document.createElement("baseNoGraIva");
				detalleVentas.appendChild(baseNoGraIva);
				baseNoGraIva.appendChild(document.createTextNode(Numero.aString(0.00, decimales)));

				Element baseImponible = document.createElement("baseImponible");
				detalleVentas.appendChild(baseImponible);
				baseImponible.appendChild(document.createTextNode(Numero.aString(baseImponibleIvaCero, decimales)));

				Element eleBaseImpGrav = document.createElement("baseImpGrav");
				detalleVentas.appendChild(eleBaseImpGrav);
				eleBaseImpGrav.appendChild(document.createTextNode(Numero.aString(baseImpGrav, decimales)));

				ventasSumatoria = ventasSumatoria + baseImpGrav + baseImponibleIvaCero;

				Element eleMontoIva = document.createElement("montoIva");
				detalleVentas.appendChild(eleMontoIva);
				String eleMontoIvaAux = Numero.aString(montoIva, decimales);
				eleMontoIva.appendChild(document.createTextNode(eleMontoIvaAux));

				Element montoIce = document.createElement("montoIce");
				detalleVentas.appendChild(montoIce);
				montoIce.appendChild(document.createTextNode(Numero.aString(0.00, decimales)));

				Element eleValorRetIva = document.createElement("valorRetIva");
				detalleVentas.appendChild(eleValorRetIva);
				String valorRetIvaAux = Numero.aString(valorRetIva, decimales);
				eleValorRetIva.appendChild(document.createTextNode(valorRetIvaAux));

				Element eleValorRetRenta = document.createElement("valorRetRenta");
				detalleVentas.appendChild(eleValorRetRenta);
				String valorRerRentaAux = Numero.aString(valorRetRenta, decimales);
				eleValorRetRenta.appendChild(document.createTextNode(valorRerRentaAux));
			}
			System.out.println("Ventas sumatoria " + ventasSumatoria);
			// generacion de datos de los establecimiento
			Map<String, List<Factura>> facturasEstablecimientoMap = new HashMap<String, List<Factura>>();
			for (Factura factura : facturas) {
				String key = factura.getDocumentoNumero().substring(0, 3);
				if (facturasEstablecimientoMap.containsKey(key)) {
					facturasEstablecimientoMap.get(key).add(factura);
				} else {
					List<Factura> list = new ArrayList<Factura>();
					list.add(factura);
					facturasEstablecimientoMap.put(key, list);
				}
			}
			// ventas establicimiento
			System.out.println("Generando etiquetas de VENTA ESTABLECIMIENTO...");
			Element ventasEstablecimiento = document.createElement("ventasEstablecimiento");
			rootElement.appendChild(ventasEstablecimiento);
			for (Map.Entry<String, List<Factura>> entry : facturasEstablecimientoMap.entrySet()) {

				Element ventaEst = document.createElement("ventaEst");
				ventasEstablecimiento.appendChild(ventaEst);

				// for intermedio para calculos
				double baseImponibleIvaCero = 0.0;
				double baseImpGrav = 0.0;
				// double montoIva = 0.0;
				for (Factura factura : entry.getValue()) {
					detalleImpuestos = facturaService.calculaImpuestosDesdeDetalle(factura.getDetalles());
					// si es factura incremento el valor de las ventas
					// de lo contrario considero nota de credito por lo tanto resto
					if (Constantes.ORIGEN_VENTA_FACTURA.compareTo(factura.getOrigen().getCategoriaCod()) == 0) {
						baseImponibleIvaCero += facturaService.ivaBase0DesdeDetalleImpuestos(detalleImpuestos);
						baseImpGrav += facturaService.ivaBaseDesdeDetalleImpuestos(detalleImpuestos);
					} else {
						baseImponibleIvaCero -= facturaService.ivaBase0DesdeDetalleImpuestos(detalleImpuestos);
						baseImpGrav -= facturaService.ivaBaseDesdeDetalleImpuestos(detalleImpuestos);
					}
					// montoIva += facturaService.ivaValorDesdeDetalleImpuestos(detalleImpuestos);
				}

				// fin for
				Element codEstab = document.createElement("codEstab");
				ventaEst.appendChild(codEstab);
				codEstab.appendChild(document.createTextNode(entry.getKey()));

				Element ventasEstab = document.createElement("ventasEstab");
				ventaEst.appendChild(ventasEstab);
				ventasEstab.appendChild(
						document.createTextNode(Numero.aString(baseImponibleIvaCero + baseImpGrav, decimales)));

				Element ivaComp = document.createElement("ivaComp");
				ventaEst.appendChild(ivaComp);
				ivaComp.appendChild(document.createTextNode(Numero.aString(0.00, decimales)));

			}

			numEstabRuc = String.valueOf(facturasEstablecimientoMap.entrySet().size()).trim();

			return ventasSumatoria;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar etiqueta ventas", e);
		}
	}

	/**
	 * Generar etiqueta de compras
	 * 
	 * @param document
	 * @param fechaDesde
	 * @param fechaHasta
	 * @param organizacionCod
	 * @param rootElement
	 * @return
	 * @throws SigmaException
	 */
	private Document generaCompras(Document document, Date fechaDesde, Date fechaHasta, String organizacionCod,
			Element rootElement) throws SigmaException {
		System.out.println("Generando etiquetas de COMPRA...");
		try {
			List<Factura> facturas = facturaRepository.findFacturasPorFechas(Constantes.ORIGEN_COMPRA_FACTURA,
					Constantes.ORIGEN_COMPRA_NC, Fecha.formatoXML(fechaDesde), Fecha.formatoXML(fechaHasta),
					organizacionCod);
			List<Retencion> retenciones;
			List<FacturaDetalleImpuesto> detalleImpuestos;

			Element eleCompras = document.createElement("compras");
			rootElement.appendChild(eleCompras);

			for (Factura factura : facturas) {

				retenciones = retencionRepository.findFullByFactura(factura);
				detalleImpuestos = facturaService.calculaImpuestosDesdeDetalle(factura.getDetalles());

				Element eleDetalleCompras = document.createElement("detalleCompras");
				eleCompras.appendChild(eleDetalleCompras);

				Element codSustento = document.createElement("codSustento");
				eleDetalleCompras.appendChild(codSustento);
				codSustento.appendChild(document.createTextNode("01")); // Agregar

				Element tpIdProv = document.createElement("tpIdProv");
				eleDetalleCompras.appendChild(tpIdProv);
				String tpIdProvAux = factura.getPersona().getPersonaTipo().getTipoId().getSriAtsCompra();
				tpIdProv.appendChild(document.createTextNode(tpIdProvAux));

				Element idProv = document.createElement("idProv");
				eleDetalleCompras.appendChild(idProv);
				String idProvAux = factura.getPersona().getNumeroId();
				idProv.appendChild(document.createTextNode(idProvAux));

				// si es compra colo el tipo factura nota liquidacion
				Element tipoComprobante = document.createElement("tipoComprobante");
				eleDetalleCompras.appendChild(tipoComprobante);
				String tipoComprobanteAux = "";
				if (Constantes.ORIGEN_COMPRA_FACTURA.compareTo(factura.getOrigen().getCategoriaCod()) == 0) {
					tipoComprobanteAux = factura.getDocumentoTipo().getDocumentoTipoSri();
				} else {
					// si es nota credito recupero por el documento
					tipoComprobanteAux = factura.getDocumento().getSriAts();
				}
				tipoComprobante.appendChild(document.createTextNode(tipoComprobanteAux));

				if ("F".compareTo(factura.getPersona().getPersonaTipo().getTipoId().getTipoIdCod()) != 0) {
					Element parteRel = document.createElement("parteRel");
					eleDetalleCompras.appendChild(parteRel);
					parteRel.appendChild(document.createTextNode("NO"));
				}

				Element fechaRegistro = document.createElement("fechaRegistro");
				eleDetalleCompras.appendChild(fechaRegistro);
				String fechaRegistroAux = Fecha.formatoReportes(factura.getFechaEmite());
				fechaRegistro.appendChild(document.createTextNode(fechaRegistroAux));

				Element establecimiento = document.createElement("establecimiento");
				eleDetalleCompras.appendChild(establecimiento);
				String establecimientoAux = factura.getDocumentoNumero().substring(0, 3);
				establecimiento.appendChild(document.createTextNode(establecimientoAux));

				Element puntoEmision = document.createElement("puntoEmision");
				eleDetalleCompras.appendChild(puntoEmision);
				String puntoEmisionAux = factura.getDocumentoNumero().substring(3, 6);
				puntoEmision.appendChild(document.createTextNode(puntoEmisionAux));

				Element secuencial = document.createElement("secuencial");
				eleDetalleCompras.appendChild(secuencial);
				String secuencialAux = factura.getDocumentoNumero().substring(6);
				secuencial.appendChild(document.createTextNode(secuencialAux));

				Element fechaEmision = document.createElement("fechaEmision");
				eleDetalleCompras.appendChild(fechaEmision);
				String fechaEmisionAux = Fecha.formatoReportes(factura.getFechaEmite());
				fechaEmision.appendChild(document.createTextNode(fechaEmisionAux));

				Set<FacturaImpuesto> facturaRetImpuestos = factura.getImpuestos();
				Double baseImponibleTotal = FacturaImpuesto.sumaBaseImponible(facturaRetImpuestos, null);
				Double baseImponibleConImp = FacturaImpuesto.sumaBaseImponible(facturaRetImpuestos, true);
				Double baseImponibleSinImp = FacturaImpuesto.sumaBaseImponible(facturaRetImpuestos, false);
				Double impuestosValor = FacturaImpuesto.sumaImpuestoValor(facturaRetImpuestos);

				Element autorizacion = document.createElement("autorizacion");
				eleDetalleCompras.appendChild(autorizacion);
				String autorizacionAux = factura.getAutorizacionNumero();
				autorizacion.appendChild(document.createTextNode(autorizacionAux));

				Element baseNoGraIva = document.createElement("baseNoGraIva");
				eleDetalleCompras.appendChild(baseNoGraIva);
				baseNoGraIva.appendChild(document.createTextNode(Numero.aString(0.00, decimales)));

				Element baseImponible = document.createElement("baseImponible");
				eleDetalleCompras.appendChild(baseImponible);
				String baseImpAux = Numero.aString(baseImponibleSinImp, decimales);
				baseImponible.appendChild(document.createTextNode(baseImpAux));

				Element baseImpGrav = document.createElement("baseImpGrav");
				eleDetalleCompras.appendChild(baseImpGrav);
				String basImpGravAux = Numero.aString(baseImponibleConImp, decimales);
				baseImpGrav.appendChild(document.createTextNode(basImpGravAux));

				Element baseImpExe = document.createElement("baseImpExe");
				eleDetalleCompras.appendChild(baseImpExe);
				baseImpExe.appendChild(document.createTextNode(Numero.aString(0.00, decimales)));

				Element montoIce = document.createElement("montoIce");
				eleDetalleCompras.appendChild(montoIce);
				montoIce.appendChild(document.createTextNode(Numero.aString(0.00, decimales)));

				Element montoIva = document.createElement("montoIva");
				eleDetalleCompras.appendChild(montoIva);
				String montoIvaAux = Numero.aString(impuestosValor, decimales);
				montoIva.appendChild(document.createTextNode(montoIvaAux));

				Element valRetBien10 = document.createElement("valRetBien10");
				eleDetalleCompras.appendChild(valRetBien10);
				String valRetServ10Aux = Numero
						.aString(retencionService.valorRetenidoIVAPorcentaje(retenciones, (double) 10), decimales);
				valRetBien10.appendChild(document.createTextNode(valRetServ10Aux));

				Element valRetServ20 = document.createElement("valRetServ20");
				eleDetalleCompras.appendChild(valRetServ20);
				String valRetServ20Aux = Numero
						.aString(retencionService.valorRetenidoIVAPorcentaje(retenciones, (double) 20), decimales);
				valRetServ20.appendChild(document.createTextNode(valRetServ20Aux));

				Element valorRetBienes = document.createElement("valorRetBienes");
				eleDetalleCompras.appendChild(valorRetBienes);
				String valorRetBienesAux = Numero
						.aString(retencionService.valorRetenidoIVAPorcentaje(retenciones, (double) 30), decimales);
				valorRetBienes.appendChild(document.createTextNode(valorRetBienesAux));

				Element valRetServ50 = document.createElement("valRetServ50");
				eleDetalleCompras.appendChild(valRetServ50);
				String valRetServ50Aux = Numero
						.aString(retencionService.valorRetenidoIVAPorcentaje(retenciones, (double) 50), decimales);
				valRetServ50.appendChild(document.createTextNode(valRetServ50Aux));

				Element valorRetServicios = document.createElement("valorRetServicios");
				eleDetalleCompras.appendChild(valorRetServicios);
				String valorRetServiciosAux = Numero
						.aString(retencionService.valorRetenidoIVAPorcentaje(retenciones, (double) 70), decimales);
				valorRetServicios.appendChild(document.createTextNode(valorRetServiciosAux));

				Element valRetServ100 = document.createElement("valRetServ100");
				eleDetalleCompras.appendChild(valRetServ100);
				String valRetServ100Aux = Numero
						.aString(retencionService.valorRetenidoIVAPorcentaje(retenciones, (double) 100), decimales);
				valRetServ100.appendChild(document.createTextNode(valRetServ100Aux));

				// Element valorRetencionNc = document.createElement("valorRetencionNc");
				// eleDetalleCompras.appendChild(valorRetencionNc);
				// valorRetencionNc.appendChild(document.createTextNode("0.00"));

				Element totbasesImpReemb = document.createElement("totbasesImpReemb");
				eleDetalleCompras.appendChild(totbasesImpReemb);
				totbasesImpReemb.appendChild(document.createTextNode(Numero.aString(0.00, decimales)));
				// PAGO EXTERIOR

				Element pagoExterior = document.createElement("pagoExterior");
				eleDetalleCompras.appendChild(pagoExterior);

				Element pagoLocExt = document.createElement("pagoLocExt");
				pagoExterior.appendChild(pagoLocExt);
				pagoLocExt.appendChild(document.createTextNode("01"));

				Element paisEfecPago = document.createElement("paisEfecPago");
				pagoExterior.appendChild(paisEfecPago);
				paisEfecPago.appendChild(document.createTextNode("NA"));

				Element aplicConvDobTrib = document.createElement("aplicConvDobTrib");
				pagoExterior.appendChild(aplicConvDobTrib);
				aplicConvDobTrib.appendChild(document.createTextNode("NA"));

				Element pagExtSujRetNorLeg = document.createElement("pagExtSujRetNorLeg");
				pagoExterior.appendChild(pagExtSujRetNorLeg);
				pagExtSujRetNorLeg.appendChild(document.createTextNode("NA"));

				if (baseImponibleTotal + impuestosValor >= 1000.0) {
					Element formasDePago = document.createElement("formasDePago");
					eleDetalleCompras.appendChild(formasDePago);

					Element formaPago = document.createElement("formaPago");
					formasDePago.appendChild(formaPago);
					formaPago.appendChild(document.createTextNode(factura.getFormaPago().getFormaPagoSri()));
				}
				// AIR
				Element air = document.createElement("air");
				eleDetalleCompras.appendChild(air);

				boolean registroRetencion = false;

				for (Retencion retencion : retenciones) {
					for (RetencionDetalle detalle : retencion.getDetalles()) {
						String impuestoTipoCod = detalle.getImpuesto().getImpuestoTipo().getImpuestoTipoCod();
						if (Constantes.IMPUESTO_TIPO_RENTA_RETENCION.equalsIgnoreCase(impuestoTipoCod)) {
							Element detalleAir = document.createElement("detalleAir");
							air.appendChild(detalleAir);

							Element codRetAir = document.createElement("codRetAir");
							detalleAir.appendChild(codRetAir);
							// detalle.getImpuesto().getPorcentajeSri()
							String codRetAirAux = detalle.getImpuesto().getSriCodigo();
							codRetAir.appendChild(document.createTextNode(codRetAirAux));

							Element baseImpAir = document.createElement("baseImpAir");
							detalleAir.appendChild(baseImpAir);
							String baseImpAirAux = Numero.aString(detalle.getBaseImponible(), decimales);
							baseImpAir.appendChild(document.createTextNode(baseImpAirAux));

							Element porcentajeAir = document.createElement("porcentajeAir");
							detalleAir.appendChild(porcentajeAir);
							String porcentajeAirAux = Numero.aString(detalle.getImpuesto().getPorcentaje(), decimales);
							porcentajeAir.appendChild(document.createTextNode(porcentajeAirAux));

							Element valRetAir = document.createElement("valRetAir");
							detalleAir.appendChild(valRetAir);
							String valRetAirAux = Numero.aString(detalle.getValorRetenido(), decimales);
							valRetAir.appendChild(document.createTextNode(valRetAirAux));

							registroRetencion = true;
						}
					}
				}
				// si no se ha registrado una retencion manual registro una con el concepto 332
				if (!registroRetencion) {
					Element detalleAir = document.createElement("detalleAir");
					air.appendChild(detalleAir);

					Element codRetAir = document.createElement("codRetAir");
					detalleAir.appendChild(codRetAir);
					codRetAir.appendChild(document.createTextNode("332"));

					Element baseImpAir = document.createElement("baseImpAir");
					detalleAir.appendChild(baseImpAir);
					String baseImpAirAux = Numero.aString(facturaService.ivaBase0DesdeDetalleImpuestos(detalleImpuestos)
							+ facturaService.ivaBaseDesdeDetalleImpuestos(detalleImpuestos), decimales);
					baseImpAir.appendChild(document.createTextNode(baseImpAirAux));

					Element porcentajeAir = document.createElement("porcentajeAir");
					detalleAir.appendChild(porcentajeAir);
					porcentajeAir.appendChild(document.createTextNode(Numero.aString(0.00, decimales)));

					Element valRetAir = document.createElement("valRetAir");
					detalleAir.appendChild(valRetAir);
					valRetAir.appendChild(document.createTextNode(Numero.aString(0.00, decimales)));
				}

			}

			return document;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar etiqueta compras", e);

		}
	}

}

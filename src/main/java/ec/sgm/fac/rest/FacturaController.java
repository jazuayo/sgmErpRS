package ec.sgm.fac.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaDateMidnight;
import ec.sgm.SigmaException;
import ec.sgm.ce.modelo.ClaveAcceso;
import ec.sgm.ce.service.SriService;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.fac.entity.DocumentoTipo;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.FacturaDetalle;
import ec.sgm.fac.entity.FacturaDetalleImpuesto;
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.entity.FormaPago;
import ec.sgm.fac.entity.Item;
import ec.sgm.fac.entity.ItemInventario;
import ec.sgm.fac.entity.Persona;
import ec.sgm.fac.modelo.FacturaConsultaReq;
import ec.sgm.fac.modelo.FacturaReq;
import ec.sgm.fac.repository.CategoriaRepository;
import ec.sgm.fac.repository.DocumentoTipoRepository;
import ec.sgm.fac.repository.FacturaDetalleImpuestoRepository;
import ec.sgm.fac.repository.FacturaDetalleRepository;
import ec.sgm.fac.repository.FacturaImpuestoRepository;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.fac.repository.FormaPagoRepository;
import ec.sgm.fac.repository.ItemInventarioRepository;
import ec.sgm.fac.repository.ItemRepository;
import ec.sgm.fac.repository.PersonaRepository;
import ec.sgm.fac.service.FacturaPDFService;
import ec.sgm.fac.service.FacturaService;
import ec.sgm.fac.service.FirmaXMLService;
import ec.sgm.fac.service.NotasPDFService;
import ec.sgm.fac.service.NotasXMLService;
import ec.sgm.fac.service.NotificaDocumentos;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.DocumentoRepository;
import ec.sgm.org.repository.EstadoRepository;
import ec.sgm.org.repository.OrganizacionRepository;
import ec.sgm.org.service.DocumentoService;
import ec.sgm.org.service.ParametroService;

@RestController
@RequestMapping("/factura")
public class FacturaController {
	private static final Logger LOGGER = LogManager.getLogger(FacturaController.class);
	@Autowired
	private FacturaRepository repositoryFactura;
	@Autowired
	private ItemInventarioRepository repositoryItemInv;
	@Autowired
	private PersonaRepository repositoryPersona;
	@Autowired
	private ItemRepository repositoryItem;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;
	@Autowired
	private EstadoRepository repositoryEstado;
	@Autowired
	private DocumentoRepository repositoryDocumento;
	@Autowired
	private DocumentoTipoRepository repositoryDocumentoTipo;
	@Autowired
	private FormaPagoRepository repositoryFormaPago;
	@Autowired
	private CategoriaRepository repositoryCategoria;
	@Autowired
	private FacturaDetalleImpuestoRepository repositoryDetalleImp;
	@Autowired
	private FacturaImpuestoRepository facturaImpuestoRepository;
	@Autowired
	private FacturaService facturaService;
	@Autowired
	private FacturaDetalleRepository facturaDetalleRepository;
	@Autowired
	private FirmaXMLService xmlService;
	@Autowired
	private ParametroService parametroService;
	@Autowired
	private DocumentoService documentoService;
	@Autowired
	private FacturaPDFService pdfService;
	@Autowired
	private NotasPDFService pdfNotaService;
	@Autowired
	private NotasXMLService xmlNotasService;
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private NotificaDocumentos envioComprobante;
	@Autowired
	private SigmaDateMidnight serviceFecha;

	/**
	 * Generar clave Acceso
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	public String claveAcceso(Factura registro) throws SigmaException {
		try {
			String organizacionCod = registro.getOrganizacion().getOrganizacionCod();
			ClaveAcceso claveAcceso = new ClaveAcceso();
			claveAcceso.setFechaEmision(registro.getFechaEmite());
			claveAcceso.setTipoComprobante(registro.getDocumento().getSriCe());
			claveAcceso.setNumeroRuc(parametroService.findValorByClave("sriRuc", organizacionCod));
			claveAcceso.setTipoAmbiente(parametroService.findValorByClave("sriCeAmbiente", organizacionCod));
			claveAcceso.setDocumentoNumero(registro.getDocumentoNumero());
			claveAcceso.setCodigoNumerico(parametroService.findValorByClave("sriCeComplemento", organizacionCod));
			return SriService.generaClaveAcceso(claveAcceso);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar claveAcceso.", e);
		}
	}

	/**
	 * Generar documento XML para procesar notas de credito
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	@Transactional
	@PostMapping(value = "/procesar/nota/xml/{documentoId}")
	public HashMap<String, String> procesarNota(@PathVariable("documentoId") Long documentoId) throws SigmaException {
		try {
			Factura notaCredito = repositoryFactura.findById(documentoId).get();
			Boolean esElectronico = notaCredito.getDocumento().getCe();
			impuestosFactura(documentoId);
			if (esElectronico == null || !esElectronico) {
				System.out.println("No es comprobante electronico");
				return MensajeResponse.ok();
			}
			String organizacionCod = notaCredito.getOrganizacion().getOrganizacionCod();
			boolean esProduccion = false;
			String pathArchivoFirmado = null;

			if (notaCredito.getSriEstado() == null || notaCredito.getSriEstado().trim().length() < 2) {
				notaCredito.setAutorizacionNumero(claveAcceso(notaCredito));
				repositoryFactura.save(notaCredito);
				impuestosFactura(documentoId);
				pathArchivoFirmado = xmlNotasService.generaArchivoXml(documentoId);
				System.out.println("Archivo firmado: " + pathArchivoFirmado);
			}

			try {
				if ("2".equalsIgnoreCase(parametroService.findValorByClave("sriCeAmbiente", organizacionCod))) {
					esProduccion = true;
				}
				HashMap<String, Object> resultado = SriService.procesar(notaCredito.getSriEstado(),
						notaCredito.getAutorizacionNumero(), pathArchivoFirmado, esProduccion);
				if (resultado != null && resultado.containsKey(SriService.ESTADO_KEY)) {
					notaCredito.setSriEstado((String) resultado.get(SriService.ESTADO_KEY));
					notaCredito.setSriNota((String) resultado.get("nota"));
					if (resultado.containsKey("fecha")) {
						notaCredito.setAutorizacionFecha((Date) resultado.get("fecha"));
					}
					Estado estado = estadoRepository.findById("AUT").orElse(null);
					notaCredito.setEstado(estado);
					repositoryFactura.save(notaCredito);
				} else {
					LOGGER.error("Procesando SRI: No se ha recuperado un estado");
					return MensajeResponse.error("No se ha recuperado un estado");
				}
			} catch (Exception e) {
				LOGGER.error("Procesando SRI:" + e.getMessage());
				return MensajeResponse.error(e.getMessage());
			}

			// envioComprobante.notificaEmailDocumentosFac(notaCredito);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar documento XML nota de credito.", e);
		}
	}

	/**
	 * Generar XML y firmarlo - FACTURAS COMPRA Y VENTA
	 * 
	 * @param id
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/procesar/{documentoId}")
	@Transactional
	public HashMap<String, String> procesar(@PathVariable("documentoId") Long documentoId) {
		try {
			Factura factura = repositoryFactura.findById(documentoId).get();
			Boolean esElectronico = factura.getDocumento().getCe();
			impuestosFactura(documentoId);

			if (esElectronico == null || !esElectronico) {
				System.out.println("No es comprobante electronico");
				return MensajeResponse.ok();
			}
			String organizacionCod = factura.getOrganizacion().getOrganizacionCod();
			boolean esProduccion = false;
			String pathArchivoFirmado = null;

			if (factura.getSriEstado() == null || factura.getSriEstado().trim().length() < 2) {
				factura.setAutorizacionNumero(claveAcceso(factura));
				repositoryFactura.save(factura);
				pathArchivoFirmado = xmlService.generaArchivosXml(documentoId);
				System.out.println("archivo firmado:" + pathArchivoFirmado);
			}
			try {
				if ("2".equalsIgnoreCase(parametroService.findValorByClave("sriCeAmbiente", organizacionCod))) {
					esProduccion = true;
				}
				HashMap<String, Object> resultado = SriService.procesar(factura.getSriEstado(),
						factura.getAutorizacionNumero(), pathArchivoFirmado, esProduccion);
				if (resultado != null && resultado.containsKey(SriService.ESTADO_KEY)) {
					factura.setSriEstado((String) resultado.get(SriService.ESTADO_KEY));
					factura.setSriNota((String) resultado.get("nota"));
					if (resultado.containsKey("fecha")) {
						factura.setAutorizacionFecha((Date) resultado.get("fecha"));
					}
					Estado estado = estadoRepository.findById("AUT").orElse(null);
					factura.setEstado(estado);
					repositoryFactura.save(factura);
				} else {
					LOGGER.error("procesando SRI: No se ha recuperado un estado");
					return MensajeResponse.error("No se ha recuperado un estado");
				}
			} catch (Exception e) {
				LOGGER.error("procesando SRI:" + e.getMessage());
				return MensajeResponse.error(e.getMessage());
			}
			// envioComprobante.notificaEmailDocumentosFac(factura);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error("Error al procesar:" + e.getMessage());
			return MensajeResponse.error(e.getMessage());
		}
	}

	/**
	 * Eliminar factura, cambia estado en la Base a anulado
	 * 
	 * @param id
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{documentoId}")
	public HashMap<String, String> eliminar(@PathVariable("documentoId") Long documentoId) throws SigmaException {
		try {
			Factura registro = repositoryFactura.findById(documentoId).orElse(null);
			if (registro == null) {
				LOGGER.error("Registro no encontrado en DB");
				throw new SigmaException("Registro no encontrado.");
			}
			Estado estadoAnulado = repositoryEstado.findById(Constantes.ESTADO_ANULADO).orElse(null);
			registro.setEstado(estadoAnulado);
			repositoryFactura.save(registro);
			// repositoryFactura.deleteById(documentoId);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar documento.", e);
		}
	}

	/**
	 * Listar facturas por filtros
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/busqueda")
	public List<Factura> listarFacturas(@RequestBody FacturaConsultaReq registro) throws SigmaException {
		try {
			List<Factura> respuestaFacturas = new ArrayList<Factura>();
			String categoriaCod = null;
			switch (registro.getCategoriaCod()) {
			case Constantes.ORIGEN_COMPRA_FACTURA:
				categoriaCod = Constantes.ORIGEN_COMPRA_FACTURA;
				break;
			case Constantes.ORIGEN_VENTA_FACTURA:
				categoriaCod = Constantes.ORIGEN_VENTA_FACTURA;
				break;
			default:
				break;
			}

			if (registro.getPersonaNombre() == "") {
				respuestaFacturas = repositoryFactura.findByfechasAndOrganizacionOrderByDocumentoId(categoriaCod,
						Fecha.formatoXML(registro.getFechaDesde()), Fecha.formatoXML(registro.getFechaHasta()),
						registro.getOrganizacionCod());
			} else {
				List<Persona> personas = repositoryPersona
						.findByCoincidencia(registro.getPersonaNombre().toLowerCase());
				for (Persona persona : personas) {
					List<Factura> facturaAux = repositoryFactura
							.findByOrigenAndOrganizacionAndPersonaAndFechaEmiteBetweenOrderByDocumentoId(categoriaCod,
									registro.getOrganizacionCod(), persona.getPersonaId(),
									Fecha.formatoXML(registro.getFechaDesde()),
									Fecha.formatoXML(registro.getFechaHasta()));
					respuestaFacturas = Stream.concat(respuestaFacturas.stream(), facturaAux.stream())
							.collect(Collectors.toList());
				}
			}
			return respuestaFacturas;
		} catch (Exception e) {
			throw new SigmaException("Error al realizar busqueda de facturas.", e);
		}

	}

	/**
	 * Calcula impuesto de factura por detalle, sin guardar datos en la base
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@Transactional
	@PostMapping("/impuesto")
	public List<FacturaImpuesto> calcularDesdeDetalles(@RequestBody FacturaReq registro) throws SigmaException {
		try {
			List<FacturaImpuesto> respuestaImpuesto = new ArrayList<FacturaImpuesto>();
			boolean calculaImpuestos = false;
			if (registro.getCategoriaCod().equals(Constantes.ORIGEN_COMPRA_FACTURA)
					|| registro.getCategoriaCod().equals(Constantes.ORIGEN_COMPRA_NC)) {
				calculaImpuestos = repositoryPersona.findById(registro.getPersonaId()).get().getPersonaTipo()
						.getIvaCompra().booleanValue();
			}
			if (registro.getCategoriaCod().equals(Constantes.ORIGEN_VENTA_FACTURA)
					|| registro.getCategoriaCod().equals(Constantes.ORIGEN_VENTA_NC)) {
				calculaImpuestos = repositoryPersona.findById(registro.getPersonaId()).get().getPersonaTipo()
						.getIvaCompra().booleanValue();
			}
			if (calculaImpuestos) {

				// Asignar completamente el item para q funcione el servicio
				for (FacturaDetalle detalle : registro.getDetalles()) {
					Item item = repositoryItem.findByIdFull(detalle.getItem().getItemId()).get();
					detalle.setItem(item);
				}

				List<FacturaDetalleImpuesto> impuestosDetalles = facturaService
						.calculaImpuestosDesdeDetalle(registro.getDetalles());
				respuestaImpuesto = facturaService.calculaImpuestosFacturaConFacturaDetalleImpuesto(impuestosDetalles);
			}
			return respuestaImpuesto;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al calcular impuesto.", e);
		}
	}

	/**
	 * Grabar factura y notas de creditos
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@Transactional
	@PostMapping
	public Factura grabar(@RequestBody FacturaReq registro) throws SigmaException {
		try {
			Factura factura = new Factura();
			// Datos extras
			factura.setUsuario(registro.getUsuario());
			// Modifica factura -- nota de credito
			if (Constantes.ORIGEN_VENTA_NC.equals(registro.getCategoriaCod())
					|| Constantes.ORIGEN_COMPRA_NC.equals(registro.getCategoriaCod())) {
				Factura factModifica = repositoryFactura.findById(registro.getFacturaModifica()).get();
				factura.setFacturaModifica(factModifica);
			}
			if (registro.getDocumentoId() != null && registro.getDocumentoId().intValue() == 0) {
				registro.setDocumentoId(null);
			}
			factura.setDocumentoId(registro.getDocumentoId());
			// Datos de cabezera
			Organizacion organizacion = repositoryOrganizacion.findById(registro.getOrganizacionCod()).get();
			factura.setOrganizacion(organizacion);
			Estado estado = repositoryEstado.findById(registro.getEstadoCod()).get();
			factura.setEstado(estado);
			factura.setAutorizacionNumero(registro.getAutorizacionNumero());
			factura.setDocumentoNumero(registro.getDocumentoNumero());
			Persona persona = repositoryPersona.findById(registro.getPersonaId()).get();
			factura.setPersona(persona);
			Documento documento = repositoryDocumento.findById(registro.getDocumentoCod()).get();
			factura.setDocumento(documento);
			DocumentoTipo documentoTipo = repositoryDocumentoTipo.findById(registro.getDocumentoTipoCod()).get();
			factura.setDocumentoTipo(documentoTipo);
			factura.setObservaciones(registro.getObservaciones());

			// if (Constantes.ORIGEN_VENTA_FACTURA.compareTo(registro.getCategoriaCod()) ==
			// 0
			// && factura.getDocumentoId() == null && documento.getCe().booleanValue() ==
			// true) {
			if (factura.getDocumentoId() == null && documento.getCe().booleanValue() == true) {
				// if (factura.getDocumentoNumero().indexOf("-") <= 0) {
				System.out.println("Nuevo Registro y CE true");
				factura.setDocumentoNumero(documentoService.recuperaIncrementaSecuencia(registro.getDocumentoCod()));
				// }
			}
			// Fechas

			factura.setAutorizacionFecha(serviceFecha.DateMidnight(registro.getAutorizacionFecha()));
			factura.setFechaEmite(serviceFecha.DateMidnight(registro.getFechaEmite()));

			factura.setPlazoDias(registro.getPlazoDias());
			FormaPago formaPago = repositoryFormaPago.findById(registro.getFormaPagoCod()).get();
			factura.setFormaPago(formaPago);

			Long documentoId = null;

			factura.setOrigen(repositoryCategoria.findById(registro.getCategoriaCod()).get());

			switch (registro.getCategoriaCod()) {
			case Constantes.ORIGEN_COMPRA_FACTURA:
			case Constantes.ORIGEN_COMPRA_NC:
				documentoId = repositoryFactura.save(factura).getDocumentoId();
				for (FacturaDetalle detalle : registro.getDetalles()) {
					detalle.setDocumentoId(documentoId);
					// Integer cantidadItems = detalle.getCantidad().intValue();
					Item item = repositoryItem.findById(detalle.getItem().getItemId()).get();
					if (detalle.getDescripcion().equals("")) {
						String itemDes = detalle.getItem().getItemDes();
						detalle.setDescripcion(itemDes);
					}

					// Si el docuemento permite actualizar inventario
					if (documento.getInventario().booleanValue() == true) {

						String loteNombre = detalle.getLote();
						List<ItemInventario> inventario = repositoryItemInv.findByLoteAndItemId(loteNombre,
								item.getItemId());

						ItemInventario itemInv = new ItemInventario();
						itemInv.setItemId(item.getItemId());
						itemInv.setLote(loteNombre);
						for (ItemInventario lote : inventario) {
							itemInv = lote;
						}
						itemInv.setFechaVence(detalle.getFechaVence());
						itemInv.setCantidad(itemInv.getCantidad() + detalle.getCantidad().longValue());
						repositoryItemInv.save(itemInv);
					}

					// actualiza el precio del item si me permite el documento
					if (documento.getInventario().booleanValue() == true) {
						item.setPrecioVenta(detalle.getPvp());
						item.setCostoCompra(detalle.getPrecioUnitario());
						repositoryItem.save(item);
					}

				}
				factura.setDetalles(registro.getDetalles());
				factura.setDocumentoId(documentoId);
				repositoryFactura.save(factura);
				impuestosFactura(documentoId);
				break;
			case Constantes.ORIGEN_VENTA_FACTURA:
			case Constantes.ORIGEN_VENTA_NC:
				documentoId = repositoryFactura.save(factura).getDocumentoId();
				for (FacturaDetalle detalle : registro.getDetalles()) {
					detalle.setDocumentoId(documentoId);
					Item item = repositoryItem.findById(detalle.getItem().getItemId()).get();
					if (detalle.getDescripcion().equals("")) {
						String itemDes = detalle.getItem().getItemDes();
						detalle.setDescripcion(itemDes);
					}

					// resto del inventario
					ItemInventario itemInventario = repositoryItemInv.findById(Long.valueOf(detalle.getLote()))
							.orElse(null);
					itemInventario.setCantidad(itemInventario.getCantidad() - detalle.getCantidad().longValue());
					repositoryItemInv.save(itemInventario);

				}
				factura.setDetalles(registro.getDetalles());
				factura.setDocumentoId(documentoId);
				repositoryFactura.save(factura);
				impuestosFactura(documentoId);

				break;
			default:
				break;
			}
			List<FacturaDetalle> detallesActualizados = facturaDetalleRepository
					.findByDocumentoIdOrderByFacturaDetalleId(documentoId);
			factura.setDetalles(new HashSet<>(detallesActualizados));
			Factura resp = repositoryFactura.findById(documentoId).get();
			return resp;
		} catch (

		Exception e) {
			System.out.println(e.getMessage());
			throw new SigmaException("Error al grabar documento.", e);
		}

	}

	public void impuestosFactura(Long documentoId) throws SigmaException {
		try {
			repositoryDetalleImp.deleteByFacturaId(documentoId);
			facturaImpuestoRepository.deleteByDocumentoId(documentoId);
			// Calcula y guarda cada impuesto de cada detalle
			List<FacturaDetalleImpuesto> detalleImpuesto = facturaService.calculaImpuestosDetalle(documentoId);
			for (FacturaDetalleImpuesto facturaDetalleImpuesto : detalleImpuesto) {
				repositoryDetalleImp.save(facturaDetalleImpuesto);
			}
			// calcular los impuestos de la factura total
			List<FacturaImpuesto> facturaImpuestos = facturaService.calculaImpuestosFactura(documentoId);
			for (FacturaImpuesto facturaImpuesto : facturaImpuestos) {
				facturaImpuestoRepository.save(facturaImpuesto);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new SigmaException("Error impuestos del documento.", e);
		}
	}

	/**
	 * Generar pdf - CT
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/procesar/pdf/{documentoId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] procesarPdf(@PathVariable("documentoId") Long documentoId) throws SigmaException {
		try {
			return pdfService.generarFacturaPDF(documentoId);
		} catch (Exception e) {
			LOGGER.error("Error al procesar el documento pdf." + e.getMessage());
			throw new SigmaException("Error al procesar el documento, generar pdf", e);
		}
	}

	/**
	 * Generar pdf de nota de credito/debito CT
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/procesar/nota/pdf/{documentoId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] procesarNotaPdf(@PathVariable("documentoId") Long documentoId) throws SigmaException {
		try {
			return pdfNotaService.generarNotaPDF(documentoId);
		} catch (Exception e) {
			LOGGER.error("Error al procesar la nota pdf." + e.getMessage());
			throw new SigmaException("Error al procesar el documento, generar nota pdf.", e);
		}
	}

	/**
	 * Recuperar nota por documentoId
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/recuperar/nota/{documentoId}")
	public Factura recuperarNotaPorDocumentoId(@PathVariable("documentoId") Long documentoId) throws SigmaException {
		try {
			Factura factura = repositoryFactura.findById(documentoId).orElse(null);
			List<Factura> facturaExiste = repositoryFactura.findByFacturaModificaOrderByDocumentoId(factura);
			Factura respuesta = new Factura();
			if (facturaExiste.size() == 1) {
				respuesta = facturaExiste.get(0);
			}
			return respuesta;
		} catch (Exception e) {
			LOGGER.error("Error al recuperar documento: " + e.getMessage());
			throw new SigmaException("Error al recuperar documento.", e);
		}
	}

	/**
	 * Consultar registro factura por organizacion y observacion
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/validar/registro")
	public Boolean validarRegistroPorObservacion(@RequestBody FacturaReq registro) throws SigmaException {
		try {
			Organizacion organizacion = repositoryOrganizacion.findById(registro.getOrganizacionCod()).orElse(null);
			List<Factura> facturas = repositoryFactura.findByOrganizacionAndObservaciones(organizacion,
					registro.getObservaciones().trim());
			return !facturas.isEmpty();
		} catch (Exception e) {
			LOGGER.error("Error al validar por observacion registro: " + e.getMessage());
			throw new SigmaException("Error al validar por observacion registro.", e);
		}
	}

	/**
	 * Recuperar la factura o nota por el id
	 * 
	 * @param documentoId
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/recuperar/registro/{documentoId}")
	public Factura recuperarRegistroPorId(@PathVariable("documentoId") Long documentoId) throws SigmaException {
		try {
			Factura factura = repositoryFactura.findById(documentoId).orElse(null);
			return factura;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al recuperar datos por documentoId", e);
		}
	}

}

package ec.sgm.cta.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import ec.sgm.SigmaException;
import ec.sgm.core.Fecha;
import ec.sgm.cta.entity.Comprobante;
import ec.sgm.cta.entity.ComprobanteCuenta;
import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.cta.modelo.ComprobanteConsultaReq;
import ec.sgm.cta.modelo.ComprobanteReq;
import ec.sgm.cta.repository.ComprobanteRepository;
import ec.sgm.cta.service.ComprobantePDFService;
import ec.sgm.cta.service.ComprobanteService;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.model.MensajeResponse;
import ec.sgm.org.repository.DocumentoRepository;
import ec.sgm.org.repository.EstadoRepository;
import ec.sgm.org.repository.OrganizacionRepository;

/**
 * 
 * @author SIGMA - TL
 *
 */
@RestController
@RequestMapping("/comprobante")
public class ComprobanteController {
	private static final Logger LOGGER = LogManager.getLogger(ComprobanteController.class);
	@Autowired
	private ComprobanteRepository repository;
	@Autowired
	private ComprobanteService comprobanteService;
	@Autowired
	private DocumentoRepository documentoRepository;
	@Autowired
	private OrganizacionRepository organizacionRepository;
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private ComprobantePDFService servicePdf;

	/**
	 * Inserta nuevo o actualiza el comprobante
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping
	public Comprobante grabar(@RequestBody ComprobanteReq registro) throws SigmaException {

		try {
			Documento documento = documentoRepository.findById(registro.getDocumentoCod()).orElse(null);
			Organizacion organizacion = organizacionRepository.findById(registro.getOrganizacionCod()).orElse(null);
			Estado estado = estadoRepository.findById(registro.getEstadoCod()).orElse(null);
			Comprobante comprobante = new Comprobante();
			comprobante.setComprobanteCod(registro.getComprobanteCod());
			comprobante.setFecha(registro.getFecha());
			comprobante.setConcepto(registro.getConcepto());
			comprobante.setFuente(registro.getFuente());
			comprobante.setDeudorBeneficiario(registro.getDeudorBeneficiario());
			comprobante.setEsAutomatico(registro.isEsAutomatico());
			comprobante.setUsuario(registro.getUsuario());
			comprobante.setChequeNumero(registro.getChequeNumero());
			List<ComprobanteCuenta> cuentas = new ArrayList<>();
			for (ComprobanteCuenta cuenta : registro.getDetalles()) {
				boolean equals = cuenta.getCuenta().getCuentaCod().equals("Totales");
				if (!equals) {
					cuentas.add(cuenta);
				}
			}
			comprobante.setDetalles(cuentas);
			comprobante.setDocumento(documento);
			comprobante.setOrganizacion(organizacion);
			comprobante.setCompAutCabcod(registro.getCompAutCabcod());
			comprobante.setEstado(estado);
			comprobante = comprobanteService.grabar(comprobante, registro.getDocumentoCod(),
					registro.getComprobanteCod());
			return comprobante;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al registrar el comprobante.", e);
		}
	}

	/**
	 * Listar comprobantes segun filtro - CT
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping("/busqueda")
	public List<Comprobante> listarComprobantes(@RequestBody ComprobanteConsultaReq registro) throws SigmaException {
		try {
			List<Comprobante> comprobanteRespuesta = new ArrayList<Comprobante>();
			Organizacion organizacion = organizacionRepository.findById(registro.getOrganizacionCod()).orElse(null);
			// Fecha + comprobante busqueda
			if (registro.getComprobanteCod() != "" && registro.getConcepto() == "") {
				comprobanteRespuesta = repository.findByOrganizacionAndComprobanteCodLikeAndFechaBetween(organizacion,
						registro.getComprobanteCod(), Fecha.formatoXML(registro.getFechaDesde()),
						Fecha.formatoXML(registro.getFechaHasta()));
			}
			// Fecha + concepto busqueda
			if (registro.getComprobanteCod() == "" && registro.getConcepto() != "") {
				comprobanteRespuesta = repository.findByOrganizacionAndConceptoContainingIgnoreCaseAndFechaBetween(
						organizacion, registro.getConcepto(), Fecha.formatoXML(registro.getFechaDesde()),
						Fecha.formatoXML(registro.getFechaHasta()));
			}
			// Fecha + concepto + comprobante
			if (registro.getComprobanteCod() != "" && registro.getConcepto() != "") {
				comprobanteRespuesta = repository
						.findByOrganizacionAndConceptoContainingIgnoreCaseAndComprobanteCodContainingIgnoreCaseAndFechaBetween(
								organizacion, registro.getConcepto(), registro.getComprobanteCod(),
								Fecha.formatoXML(registro.getFechaDesde()), Fecha.formatoXML(registro.getFechaHasta()));
			}
			// Realizar la suma para el total del costo
			for (Comprobante comprobante : comprobanteRespuesta) {
				ComprobanteReq comprobanteReq = new ComprobanteReq();
				comprobanteReq.setDetalles(comprobante.getDetalles());
				List<ComprobanteCuenta> detallesSumados = sumaDebitosCreditos(comprobanteReq);
				comprobante.setDetalles(detallesSumados);
			}

			return comprobanteRespuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en listar comprobantes.", e);
		}
	}

	/**
	 * sumal el total de debitos y creditos del comprobante - CT
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping("/totales")
	public List<ComprobanteCuenta> sumaDebitosCreditos(@RequestBody ComprobanteReq registro) throws SigmaException {
		try {
			List<ComprobanteCuenta> respuesta = new ArrayList<>();
			BigDecimal sumaDebitos = new BigDecimal(0.0);
			BigDecimal sumaCreditos = new BigDecimal(0.0);
			for (ComprobanteCuenta compCuenta : registro.getDetalles()) {
				boolean equals = compCuenta.getCuenta().getCuentaCod().equals("Totales");
				if (!equals) {
					sumaDebitos = sumaDebitos.add(compCuenta.getDebito());
					sumaCreditos = sumaCreditos.add(compCuenta.getCredito());
					respuesta.add(compCuenta);
				}
			}
			// Armar la suma
			ComprobanteCuenta objetoSuma = new ComprobanteCuenta();
			objetoSuma.setCredito(sumaCreditos);
			objetoSuma.setDebito(sumaDebitos);
			objetoSuma.setConcepto(sumaDebitos.subtract(sumaCreditos).toString());
			PlanCuenta planCuenta = new PlanCuenta();
			planCuenta.setCuentaCod("Totales");
			planCuenta.setCuentaDes("");
			objetoSuma.setCuenta(planCuenta);

			respuesta.add(objetoSuma);
			return respuesta;
		} catch (

		Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en operar valores debito credito.", e);
		}
	}

	/**
	 * Elimina el comprobante
	 * 
	 * @param id
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/{id}")
	public HashMap<String, String> eliminar(@PathVariable("id") String id) throws SigmaException {
		try {
			comprobanteService.delete(id);
			return MensajeResponse.ok();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al eliminar el comprobante.", e);
		}
	}

	/**
	 * Generar comprobante en pdf
	 * 
	 * @param comprobanteCod
	 * @return
	 * @throws SigmaException
	 */
	@GetMapping(value = "/imprimir/{comprobanteCod}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] imprimir(@PathVariable("comprobanteCod") String comprobanteCod) throws SigmaException {
		try {
			return servicePdf.generarComprobantePDF(comprobanteCod);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en generar comprobante pdf", e);
		}
	}

}

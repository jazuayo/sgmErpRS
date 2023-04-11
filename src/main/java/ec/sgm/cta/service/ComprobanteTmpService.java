package ec.sgm.cta.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.cta.entity.Comprobante;
import ec.sgm.cta.entity.ComprobanteCuenta;
import ec.sgm.cta.entity.ComprobanteTmp;
import ec.sgm.cta.entity.ComprobanteTmpCuenta;
import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.cta.repository.ComprobanteRepository;
import ec.sgm.cta.repository.ComprobanteTmpRepository;
import ec.sgm.cta.repository.PlanCuentaRepository;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.DocumentoRepository;
import ec.sgm.org.repository.EstadoRepository;
import ec.sgm.org.repository.OrganizacionRepository;

@Service
public class ComprobanteTmpService {
	private static final Logger LOGGER = LogManager.getLogger(ComprobanteTmpService.class);
	@Autowired
	private ComprobanteTmpRepository comprobanteTmpRepository;
	@Autowired
	private DocumentoRepository repositoryDocumento;
	@Autowired
	private PlanCuentaRepository repositoryPlanCuenta;
	@Autowired
	private ComprobanteRepository repositoryComprobante;
	@Autowired
	private EstadoRepository repositoryEstado;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;

	@Transactional
	public int grabar(List<ComprobanteTmp> comprobantes) throws Exception {
		int registros = 0;
		String lsComprobante = "";
		try {
			for (ComprobanteTmp comprobante : comprobantes) {
				registros++;
				lsComprobante = comprobante.getComprobanteCod();
				for (ComprobanteTmpCuenta comprobanteDet : comprobante.getDetalles()) {
					comprobanteDet.setComprobante(comprobante);
				}
				comprobanteTmpRepository.save(comprobante);
			}

		} catch (Exception e) {
			LOGGER.error("Error en registro:" + registros + ":" + lsComprobante + ":" + e.getMessage());
			throw new Exception("Error en registro:" + registros + ":" + lsComprobante + ":" + e.getMessage());
		}
		return registros;
	}

	public String pctaIntACGeneraComp(String origen) throws SigmaException {
		try {
			return comprobanteTmpRepository.pctaIntACGeneraComp(origen);
		} catch (Exception e) {
			LOGGER.error("Error pctaIntACGeneraComp:" + e.getMessage());
			throw new SigmaException("Error pctaIntACGeneraComp:" + e.getMessage());
		}
	}

	/**
	 * CT - Validad que existan todas las cuentas y tipos de documento
	 * 
	 * @param organizacionCod
	 * @param fecha
	 * @throws SigmaException
	 */
	public void pctaSyncValidaReferencias(List<ComprobanteTmp> comprobantes) throws SigmaException {
		List<String> documentosCod = new ArrayList<String>();
		List<String> cuentasCod = new ArrayList<String>();
		for (ComprobanteTmp comprobanteTemp : comprobantes) {
			if (!documentosCod.contains(comprobanteTemp.getDocumentoCod())) {
				Documento documento = repositoryDocumento.findById(comprobanteTemp.getDocumentoCod()).orElse(null);
				if (documento == null) {
					String mensaje = "Error pctaSyncComprobanteInicia: No existe DocumentoCod "
							+ comprobanteTemp.getDocumentoCod();
					LOGGER.error(mensaje);
					throw new SigmaException(mensaje);
				} else {
					documentosCod.add(documento.getDocumentoCod());
				}
			}
			// Valida existencia de cuentas
			for (ComprobanteTmpCuenta detalle : comprobanteTemp.getDetalles()) {
				if (!cuentasCod.contains(detalle.getCuentaCod())) {
					PlanCuenta cuenta = repositoryPlanCuenta.findById(detalle.getCuentaCod()).orElse(null);
					if (cuenta == null) {
						String mensaje = "Error pctaSyncComprobanteInicia: No existe CuentaCod "
								+ detalle.getCuentaCod();
						LOGGER.error(mensaje);
						throw new SigmaException(mensaje);
					} else {
						cuentasCod.add(cuenta.getCuentaCod());
					}
				}

			}

		}
	}

	/**
	 * CT - borrar los comprobantes de una organizacion y una fecha
	 * 
	 * @param organizacionCod
	 * @param fecha
	 * @throws SigmaException
	 */
	public void eliminaMigrados(String organizacionCod, Date fecha) throws SigmaException {
		try {
			List<Object[]> codigos = repositoryComprobante.findByOrganizacionCodAndEstadoCodAndFecha(organizacionCod,
					Constantes.ESTADO_MIGRADO, Fecha.formatoXML(fecha));

			for (Object[] dato : codigos) {
				repositoryComprobante.deleteById(dato[0].toString());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException(e.getMessage(), e);
		}
	}

	/**
	 * CT - borrar los comprobantes de la fecha que se pasaron en el cta_comprobante
	 * y comprobante cuenta e inserta los nuevos desde la temporal
	 * 
	 * @param organizacionCod
	 * @param fecha
	 * @throws SigmaException
	 */
	public void pctaSyncComprobanteFinaliza(String organizacionCod, Date fecha) throws SigmaException {
		try {

			// buscar los codigos segun criterios y los elimina
			List<Object[]> codigos = repositoryComprobante.findByOrganizacionCodAndEstadoCodAndFecha(organizacionCod,
					Constantes.ESTADO_MIGRADO, Fecha.formatoXML(fecha));

			for (Object[] dato : codigos) {
				repositoryComprobante.deleteById(dato[0].toString());
			}
			// Cambia de temporal a principal
			Organizacion organizacion = repositoryOrganizacion.findById(organizacionCod).orElse(null);
			Estado estadoMigrado = repositoryEstado.findById(Constantes.ESTADO_MIGRADO).orElse(null);
			List<ComprobanteTmp> comprobantesTmp = comprobanteTmpRepository
					.findByOrganizacionCodAndFecha(organizacionCod, Fecha.formatoXML(fecha));
			for (ComprobanteTmp comprobanteTmp : comprobantesTmp) {
				Comprobante comprobante = new Comprobante();
				comprobante.setComprobanteCod(comprobanteTmp.getComprobanteCod());
				comprobante.setFecha(comprobanteTmp.getFecha());
				comprobante.setConcepto(comprobanteTmp.getConcepto());
				comprobante.setFuente(comprobanteTmp.getFuente());
				comprobante.setDeudorBeneficiario(comprobanteTmp.getDeudorBeneficiario());
				comprobante.setEsAutomatico(comprobanteTmp.getEsAutomatico());
				comprobante.setUsuario(comprobanteTmp.getUsuario());
				comprobante.setChequeNumero(comprobanteTmp.getChequeNumero());
				comprobante.setPeriodoCod(comprobanteTmp.getPeriodoCod());
				comprobante.setEstado(repositoryEstado.findById(comprobanteTmp.getEstadoCod()).orElse(null));
				// comprobante.setEstado(estadoMigrado);
				comprobante.setDocumento(repositoryDocumento.findById(comprobanteTmp.getDocumentoCod()).orElse(null));
				comprobante.setOrganizacion(organizacion);
				comprobante.setCompAutCabcod(comprobanteTmp.getCompAutCabcod());

				List<ComprobanteCuenta> cuentas = new ArrayList<ComprobanteCuenta>();
				for (ComprobanteTmpCuenta tmpCuenta : comprobanteTmp.getDetalles()) {
					ComprobanteCuenta comprobanteCuenta = new ComprobanteCuenta();
					comprobanteCuenta.setIdReg(null);
					comprobanteCuenta.setLinea(tmpCuenta.getLinea());
					comprobanteCuenta.setDebito(tmpCuenta.getDebito());
					comprobanteCuenta.setCredito(tmpCuenta.getCredito());
					comprobanteCuenta.setConcepto(tmpCuenta.getConcepto());
					comprobanteCuenta.setComprobante(comprobante);
					comprobanteCuenta.setCuenta(repositoryPlanCuenta.findById(tmpCuenta.getCuentaCod()).orElse(null));
					comprobanteCuenta.setDocumentoId(tmpCuenta.getDocumentoId());
					comprobanteCuenta.setPersonaId(tmpCuenta.getPersonaId());
					comprobanteCuenta.setCentroCod(tmpCuenta.getCentroCod());
					cuentas.add(comprobanteCuenta);
				}
				comprobante.setDetalles(cuentas);
				repositoryComprobante.save(comprobante);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException(e.getMessage(), e);
		}
	}
}

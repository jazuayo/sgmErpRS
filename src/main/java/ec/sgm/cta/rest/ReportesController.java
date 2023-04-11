package ec.sgm.cta.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.cta.modelo.ReportesReq;
import ec.sgm.cta.service.BalanceGeneralPDFService;
import ec.sgm.cta.service.DiarioGeneralPDFService;
import ec.sgm.cta.service.EstadoResultadosPDFService;
import ec.sgm.cta.service.MayorGeneralPDFService;
import ec.sgm.cta.service.PlanCuentaSaldoService;

/**
 * Genera los reportes del apartado de contabilidad
 * 
 * @author CT
 *
 */
@RestController
@RequestMapping("/reporte")
public class ReportesController {
	private static final Logger LOGGER = LogManager.getLogger(ReportesController.class);
	@Autowired
	private DiarioGeneralPDFService diarioGeneral;
	@Autowired
	private MayorGeneralPDFService mayorGeneral;
	@Autowired
	private BalanceGeneralPDFService balanceGeneral;
	@Autowired
	private EstadoResultadosPDFService estadoResultados;
	@Autowired
	private PlanCuentaSaldoService saldosPlanCuenta;

	/**
	 * Reporte para diario general
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */

	@PostMapping(value = "/diarioGeneral", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] diarioGeneral(@RequestBody ReportesReq registro) throws SigmaException {
		try {
			String orgCod = registro.getOrganizacionCod();
			String id_fecha_inicialAux = "";
			if (registro.getFechaDesde() != null) {
				id_fecha_inicialAux = Fecha.formatoReportes(registro.getFechaDesde());
			}
			String id_fecha_finalAux = "";
			if (registro.getFechaHasta() != null) {
				id_fecha_finalAux = Fecha.formatoReportes(registro.getFechaHasta());
			}
			return diarioGeneral.generarReporteDiarioGeneral(orgCod, id_fecha_inicialAux, id_fecha_finalAux);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en reporte diario general", e);
		}
	}

	/**
	 * Reporte para balance general
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/balanceGeneral", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] balanceGeneral(@RequestBody ReportesReq registro) throws SigmaException {
		try {
			// Valores a definir en interfaz
			Integer in_solo_con_saldo = 0;
			Integer in_no_actualiza_signo = 0;

			saldosPlanCuenta.generaSaldosMayorizaEnBaseDatos(Constantes.COD_REPORTE_BALANCE_GENERAL,
					registro.getOrganizacionCod(), null, null, registro.getFechaDesde(), registro.getFechaHasta(),
					Integer.valueOf(registro.getNivel()), registro.getUsuarioCod(), in_solo_con_saldo,
					in_no_actualiza_signo);

			return balanceGeneral.generarReporteBalanceGeneral(registro.getOrganizacionCod(), registro.getUsuarioCod(),
					Integer.valueOf(registro.getNivel()));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en reporte balance general", e);
		}
	}

	/**
	 * Reporte para estado resultado
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/estadoResultado", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] estadoResultado(@RequestBody ReportesReq registro) throws SigmaException {
		try {
			Integer in_solo_con_saldo = 0;
			Integer in_no_actualiza_signo = 0;

			saldosPlanCuenta.generaSaldosMayorizaEnBaseDatos(Constantes.COD_REPORTE_ESTADO_RESULTADOS,
					registro.getOrganizacionCod(), null, null, registro.getFechaDesde(), registro.getFechaHasta(),
					Integer.valueOf(registro.getNivel()), registro.getUsuarioCod(), in_solo_con_saldo,
					in_no_actualiza_signo);

			return estadoResultados.generarReporteEstadoResultados(registro.getOrganizacionCod(),
					registro.getUsuarioCod(), Integer.valueOf(registro.getNivel()));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en reporte de estado resultado", e);
		}
	}

	/**
	 * Reporte para mayor general
	 * 
	 * @param registro
	 * @return
	 * @throws SigmaException
	 */
	@PostMapping(value = "/mayorGeneral", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] mayorGeneral(@RequestBody ReportesReq registro) throws SigmaException {
		try {
			String organizacionCod = registro.getOrganizacionCod();
			String cuentaCodDesde = registro.getCuentaIniCod();
			String cuentaCodHasta = registro.getCuentaFinCod();

			String id_fecha_inicialAux = Fecha.formatoReportes(registro.getFechaDesde());

			String id_fecha_finalAux = Fecha.formatoReportes(registro.getFechaHasta());

			return mayorGeneral.generarReporteMayorGeneral(organizacionCod, registro.getUsuarioCod(), cuentaCodDesde,
					cuentaCodHasta, id_fecha_inicialAux, id_fecha_finalAux);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en reporte mayor general", e);
		}
	}

}

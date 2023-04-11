package ec.sgm.cta.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.core.Fecha;
import ec.sgm.cta.entity.PlanCuentaSaldo;

@Service
public class PlanCuentaSaldoService {
	private static final Logger LOGGER = LogManager.getLogger(PlanCuentaSaldoService.class);
	@Autowired
	private PlanSaldoMayorizaService planSaldoMayoriza;

	public List<PlanCuentaSaldo> recuperaSaldos(String origen, String organizacionCod, Date fechaInicial,
			Date fechaFinal) throws SigmaException {
		List<PlanCuentaSaldo> saldos = new ArrayList<>();
		return saldos;
	}

	public void generaSaldosMayorizaEnBaseDatos(String iv_tipo_reporte, String iv_organizacion_cod,
			String iv_cuenta_cod_desde, String iv_cuenta_cod_hasta, Date id_fecha_inicialAux, Date id_fecha_finalAux,
			Integer in_nivel, String iv_usuario_cod, Integer in_solo_con_saldo, Integer in_no_actualiza_signo)
			throws SigmaException {
		try {
			String fechaDesde = "";
			if (id_fecha_inicialAux != null) {
				fechaDesde = Fecha.formatoReportes(id_fecha_inicialAux);
			}
			String fechaHasta = "";
			if (id_fecha_finalAux != null) {
				fechaHasta = Fecha.formatoReportes(id_fecha_finalAux);
			}
			Integer in_considera_fech_ini = 1;// falso/no = 0, verdadero/si = 1
			if (fechaDesde.equals("")) {
				in_considera_fech_ini = 0;
			}
			LOGGER.info("\nGenerando valores en la base de datos.");
			// Llamada a metodo
			planSaldoMayoriza.pcta_plan_cuenta_mayoriza(iv_tipo_reporte, iv_organizacion_cod, iv_cuenta_cod_desde,
					iv_cuenta_cod_hasta, fechaDesde, fechaHasta, in_nivel, in_solo_con_saldo, iv_usuario_cod,
					in_no_actualiza_signo, in_considera_fech_ini);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar datos en tabla cal_cta_plan_saldo", e);
		}
	}
}

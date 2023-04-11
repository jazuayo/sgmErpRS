package ec.sgm.cta.service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.core.Fecha;
import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.cta.entity.PlanCuentaTipoOrg;
import ec.sgm.cta.entity.PlanSaldo;
import ec.sgm.cta.modelo.PlanSaldoConsultaResp;
import ec.sgm.cta.repository.PlanCuentaRepository;
import ec.sgm.cta.repository.PlanCuentaTipoOrgRepository;
import ec.sgm.cta.repository.PlanSaldoRepository;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.OrganizacionRepository;

@Service
public class PlanSaldoMayorizaService {
	private static final Logger LOGGER = LogManager.getLogger(PlanSaldoMayorizaService.class);
	// LLamada a servicios
	@Autowired
	private FechaDesdeHastaService FechaDesdeHasta;
	@Autowired
	private PlanCuentaDesdeHastaService CuentaDesdeHasta;
	// Repositorios
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;
	@Autowired
	private PlanSaldoRepository repositoryPlanSaldo;
	@Autowired
	private PlanCuentaRepository repositoryPlanCuenta;
	@Autowired
	private PlanCuentaTipoOrgRepository repositoryPlanCuentaTipoOrg;

	/**
	 * CT -- Calcula el saldo de todas las cuentas en base a los parametros
	 * entregados y si es necesario las mayoriza
	 * 
	 * @param iv_tipo_reporte
	 * @param iv_organizacion_cod
	 * @param iv_cuenta_cod_desde
	 * @param iv_cuenta_cod_hasta
	 * @param id_fecha_inicialAux
	 * @param id_fecha_finalAux
	 * @param in_nivel
	 * @param in_solo_con_saldo
	 * @param iv_usuario_cod
	 * @param in_no_actualiza_signo
	 * @param in_considera_fech_ini
	 * @throws SigmaException
	 * @throws ParseException
	 */
	@Transactional
	public void pcta_plan_cuenta_mayoriza(String iv_tipo_reporte, String iv_organizacion_cod,
			String iv_cuenta_cod_desde, String iv_cuenta_cod_hasta, String id_fecha_inicialAux,
			String id_fecha_finalAux, Integer in_nivel, Integer in_solo_con_saldo, String iv_usuario_cod,
			Integer in_no_actualiza_signo, Integer in_considera_fech_ini) throws SigmaException, ParseException {
		String lv_cuenta_cod_desde = iv_cuenta_cod_desde;
		String lv_cuenta_cod_hasta = iv_cuenta_cod_hasta;
		if (iv_organizacion_cod == null || iv_organizacion_cod == "") {
			throw new SigmaException("Defina la organizacion a mayorizar.");
		}
		// Recupera las fechas del periodo
		HashMap<String, String> fechaDesdeHasta = FechaDesdeHasta.pcta_rep_fecha_desde_hasta(iv_organizacion_cod,
				id_fecha_inicialAux, id_fecha_finalAux);
		Date ld_comprobante_filtro_inicial = Fecha.stringToDate(fechaDesdeHasta.get("fechaInicial"));
		Date ld_comprobante_filtro_final = Fecha.stringToDate(fechaDesdeHasta.get("fechaFinal"));
		// fecha de considera movimientos dentro del periodo, para comprobacion
		Date ld_comprobante_rango_inicial = ld_comprobante_filtro_inicial;

		Date id_fecha_inicial = null;
		if (id_fecha_inicialAux != null && id_fecha_inicialAux != "") {
			id_fecha_inicial = Fecha.stringToDate(id_fecha_inicialAux);
		}
		if (id_fecha_inicial != null) {
			if (id_fecha_inicial.before(ld_comprobante_filtro_inicial)) {
				LOGGER.error("Fecha inicial menor al periodo.");
				throw new SigmaException("Fecha inicial menor al periodo.");
			}
			ld_comprobante_rango_inicial = id_fecha_inicial;
		}
		// Metodo para obtener rango de cuentas
		HashMap<String, String> cuentaDesdeHasta = CuentaDesdeHasta.pcta_rep_cuenta_desde_hasta(iv_tipo_reporte,
				iv_organizacion_cod, iv_cuenta_cod_desde, iv_cuenta_cod_hasta);
		lv_cuenta_cod_desde = cuentaDesdeHasta.get("ov_cuenta_cod_desde");
		lv_cuenta_cod_hasta = cuentaDesdeHasta.get("ov_cuenta_cod_desde");

		String lv_org_comodin = iv_organizacion_cod + "_";
		// Colocar como consolidado
		// Busqueda de organizaciones, if organizaciones existen entonces ln_consolida =
		// 1 , else = 0
		List<Organizacion> organizaciones = repositoryOrganizacion.findAll();
		Integer ln_consolida;
		if (organizaciones.size() != 0) {
			ln_consolida = 1;
		} else {
			ln_consolida = 0;
		}
		// --Elimino los datos de los calculos anteriores del usuario
		repositoryPlanSaldo.deleteAll();
		/*
		 * --0 recupero los totales de debitos y creditos para cada cuenta contable --si
		 * se esta consolidado cambiara todos los codigos de la sucursales consolidadas
		 * por lel codigo de la sucursal consolidadora
		 */
		Organizacion organizacion = repositoryOrganizacion.findById(iv_organizacion_cod).get();
		List<PlanSaldoConsultaResp> comprobantes = repositoryPlanSaldo.totalesCreditoDebitoDeCuentasContables(
				organizacion, ld_comprobante_filtro_inicial, ld_comprobante_filtro_final, ld_comprobante_rango_inicial);
		/*
		 * Crear datos en cal_cta_plan_saldo (etapa, usuario_cod, fecha,
		 * organizacion_cod, cuenta_cod, debito_inicial, credito_inicial, debitos,
		 * creditos)
		 */
		int idPlanSaldoAux = 0;
		for (PlanSaldoConsultaResp planSaldoCon : comprobantes) {

			PlanSaldo planSaldoAux = new PlanSaldo();
			planSaldoAux.setPlanSaldoId((long) idPlanSaldoAux);
			idPlanSaldoAux = idPlanSaldoAux + 1;
			planSaldoAux.setEtapa(0);
			String organizacionCodAux = null;
			if (ln_consolida == 1) {
				organizacionCodAux = iv_organizacion_cod;
			} else {
				organizacionCodAux = planSaldoCon.getOrganizacion();
			}
			planSaldoAux.setOrganizacionCod(organizacionCodAux);
			planSaldoAux.setCuentaCod(planSaldoCon.getCuenta());
			planSaldoAux.setFecha(ld_comprobante_filtro_final);
			Double debitoInicial = planSaldoCon.getDebitoInicial();
			planSaldoAux.setDebitoInicial(debitoInicial);
			Double creditoInicial = planSaldoCon.getCreditoInicial();
			planSaldoAux.setCreditoInicial(creditoInicial);
			Double saldoInicial = debitoInicial - creditoInicial;
			planSaldoAux.setSaldoInicial(saldoInicial);
			Double debitos = planSaldoCon.getDebitos();
			planSaldoAux.setDebitos(debitos);
			Double creditos = planSaldoCon.getCredito();
			planSaldoAux.setCreditos(creditos);
			Double saldo = (debitoInicial - creditoInicial) + (debitos - creditos);
			planSaldoAux.setSaldo(saldo);
			PlanCuenta cuentaAux = repositoryPlanCuenta.findById(planSaldoCon.getCuenta()).get();
			// Cuenta formato = cuenta cod
			planSaldoAux.setCuentaFormato(planSaldoCon.getCuenta());
			planSaldoAux.setCuentaDes(cuentaAux.getCuentaDes());
			planSaldoAux.setNivel(cuentaAux.getNivel());
			planSaldoAux.setCuentaTipoCod(cuentaAux.getPlanCuentaTipo().getCuentaTipoCod());
			planSaldoAux.setCuentaTipoDes(cuentaAux.getPlanCuentaTipo().getCuentaTipoDes());
			planSaldoAux.setUsuarioCod(iv_usuario_cod);
			planSaldoAux.setOrden(cuentaAux.getPlanCuentaTipo().getOrden());
			repositoryPlanSaldo.save(planSaldoAux);
		}

		LOGGER.info("Se recupero: " + comprobantes.size() + " registros para procesar");

		if (in_nivel == 0) {
			/*
			 * Si se tiene que presentar unicamente las cuentas de movimiento coloca a las
			 * cuentas anteriores como las cuentas que se tienen que presentar y actulizo
			 * los dato requeridos
			 */
			repositoryPlanSaldo.updateNivel_0();
		} else {
			// --recupero las cuentas de las cuentas que tienen que ser presentadas
			List<PlanCuenta> cuentasRecuperadas = repositoryPlanCuenta.cuentasPresentadas(organizacion, in_nivel);
			for (PlanCuenta planCuentaAux : cuentasRecuperadas) {
				// --mayorizo los debito y los credito
				List<PlanSaldoConsultaResp> existeMayorizado = repositoryPlanSaldo
						.mayorizarDebitoYCredito(planCuentaAux.getCuentaCod());
				PlanSaldoConsultaResp mayorizado = new PlanSaldoConsultaResp();
				if (existeMayorizado.size() != 0) {
					mayorizado = existeMayorizado.get(0);
				}
				Double ln_debitos = mayorizado.getDebitos();
				Double ln_creditos = mayorizado.getCredito();
				Double ln_valor_inicial_debitos = mayorizado.getDebitoInicial();
				Double ln_valor_inicial_creditos = mayorizado.getCreditoInicial();
				// --ingreso los valores calculados
				PlanSaldo planSaldoAux = new PlanSaldo();
				planSaldoAux.setPlanSaldoId((long) idPlanSaldoAux);
				idPlanSaldoAux = idPlanSaldoAux + 1;
				planSaldoAux.setEtapa(1);
				planSaldoAux.setUsuarioCod(iv_usuario_cod);
				planSaldoAux.setOrganizacionCod(iv_organizacion_cod);
				planSaldoAux.setFecha(ld_comprobante_filtro_final);
				planSaldoAux.setCuentaCod(planCuentaAux.getCuentaCod());
				if (ln_valor_inicial_debitos == null) {
					ln_valor_inicial_debitos = 0.0;
				}
				planSaldoAux.setDebitoInicial(ln_valor_inicial_debitos);
				if (ln_valor_inicial_creditos == null) {
					ln_valor_inicial_creditos = 0.0;
				}
				planSaldoAux.setCreditoInicial(ln_valor_inicial_creditos);
				Double saldoInicialAux = ln_valor_inicial_debitos - ln_valor_inicial_creditos;
				planSaldoAux.setSaldoInicial(saldoInicialAux);
				if (ln_debitos == null) {
					ln_debitos = 0.0;
				}
				planSaldoAux.setDebitos(ln_debitos);
				if (ln_creditos == null) {
					ln_creditos = 0.0;
				}
				planSaldoAux.setCreditos(ln_creditos);
				Double saldo = (ln_valor_inicial_debitos - ln_valor_inicial_creditos) + (ln_debitos - ln_creditos);
				planSaldoAux.setSaldo(saldo);
				planSaldoAux.setCuentaDes(planCuentaAux.getCuentaDes());
				planSaldoAux.setCuentaTipoCod(planCuentaAux.getPlanCuentaTipo().getCuentaTipoCod());
				planSaldoAux.setNivel(planCuentaAux.getNivel());
				planSaldoAux.setCuentaFormato(planCuentaAux.getCuentaCod());
				planSaldoAux.setCuentaTipoDes(planCuentaAux.getPlanCuentaTipo().getCuentaTipoDes());
				planSaldoAux.setOrden(planCuentaAux.getPlanCuentaTipo().getOrden());
				repositoryPlanSaldo.save(planSaldoAux);
			}
		}
		// si debo de actualizar el signo cambio los signos de las cuentas
		if (in_no_actualiza_signo == null) {
			in_no_actualiza_signo = 0;
		}
		if (in_no_actualiza_signo == 0) {
			List<PlanCuentaTipoOrg> cuentasSignoNegativo = repositoryPlanCuentaTipoOrg
					.cuentasSignoNegativo(organizacion);
			for (PlanCuentaTipoOrg planCuentaTipo : cuentasSignoNegativo) {
				String cuentaCod = planCuentaTipo.getCuentaCod();
				int modificadas = repositoryPlanSaldo.cuentaSaldoSignoNegativo(cuentaCod);
				LOGGER.info("Actualizo negativos: " + cuentaCod + " : " + modificadas);
			}
		}

		if (in_solo_con_saldo == 1) {
			repositoryPlanSaldo.deleteBySaldo(0.0);
		}

	}
}

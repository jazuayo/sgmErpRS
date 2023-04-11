package ec.sgm.cta.service;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.core.Constantes;
import ec.sgm.cta.repository.PlanCuentaRepository;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.OrganizacionRepository;

@Service
public class PlanCuentaDesdeHastaService {
	private static final Logger LOGGER = LogManager.getLogger(PlanCuentaDesdeHastaService.class);
	@Autowired
	private PlanCuentaRepository repositoryPlanCuenta;
	@Autowired
	private OrganizacionRepository repositoryOrganizacion;

	/**
	 * * CT -- Define la cuenta inicial y final para la generacion de reportes
	 * 
	 * @param iv_tipo_reporte
	 * @param iv_organizacion_cod
	 * @param iv_cuenta_cod_desde
	 * @param iv_cuenta_cod_hasta
	 * @return
	 * @throws SigmaException
	 */
	public HashMap<String, String> pcta_rep_cuenta_desde_hasta(String iv_tipo_reporte, String iv_organizacion_cod,
			String iv_cuenta_cod_desde, String iv_cuenta_cod_hasta) throws SigmaException {
		try {

			// Valores de salida
			HashMap<String, String> cuentaDesdeHasta = new HashMap<String, String>();
			// Begin
			// Recupera las cuentas de la organizacion
			Organizacion organizacion = repositoryOrganizacion.findById(iv_organizacion_cod).get();
			String lv_cuenta_cod_desde = repositoryPlanCuenta.minCuentaCod(organizacion);
			String lv_cuenta_cod_hasta = repositoryPlanCuenta.maxCuentaCod(organizacion);
			// Inicialmente recupera todas las cuentas
			String ov_cuenta_cod_desde = lv_cuenta_cod_desde;
			String ov_cuenta_cod_hasta = lv_cuenta_cod_hasta;
			// Si tiene definido una cuenta inicial la coloco
			if (iv_cuenta_cod_desde != null) {
				ov_cuenta_cod_desde = iv_cuenta_cod_desde;
			}
			// Si tiene definido una cuenta final la coloco
			if (iv_cuenta_cod_hasta != null) {
				ov_cuenta_cod_hasta = iv_cuenta_cod_hasta;
			}
			// El balance y el estado de resultados siempre recuperan todas las cuentas
			if (iv_tipo_reporte.equals(Constantes.COD_REPORTE_BALANCE_GENERAL)
					|| iv_tipo_reporte.equals(Constantes.COD_REPORTE_ESTADO_RESULTADOS)) {
				ov_cuenta_cod_desde = lv_cuenta_cod_desde;
				ov_cuenta_cod_hasta = lv_cuenta_cod_hasta;
			}
			// Respuesta
			cuentaDesdeHasta.put("ov_cuenta_cod_desde", ov_cuenta_cod_desde);
			cuentaDesdeHasta.put("ov_cuenta_cod_hasta", ov_cuenta_cod_hasta);

			return cuentaDesdeHasta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en metodo cuenta desde hasta.", e);
		}
	}
}

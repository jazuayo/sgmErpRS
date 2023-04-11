package ec.sgm.cta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ec.sgm.cta.entity.PlanCuenta;

@Repository
@Transactional
public interface ReportesContRepository extends JpaRepository<PlanCuenta, String> {
	@Query(value = ""
			//+ "-- CONSULTA SQL DIARIO GENERAL POSTGRES"
			+ ""
			+ "SELECT "
			//+ "--fgen_parametro_texto('orgRuc', og.organizacion_cod) ruc_empresa,"
			//+ "--fgen_parametro_texto('orgRazonSocial', og.organizacion_cod) organizacion,"
			//+ "--fgen_parametro_texto('orgDirMatriz', og.organizacion_cod) direccion,"
			//+ "--TO_CHAR(SYSDATE, 'dd/mm/yyyy') fechaactual,"
			//+ "--TO_CHAR(SYSDATE, 'HH:MI:SS') horaactual,"
			//+ ""
			//+ " TO_CHAR(current_date, 'dd/mm/yyyy') fechaactual,"
			//+ " TO_CHAR(current_date, 'HH:MI:SS') horaactual,"
			+ ""
			+ " doc.documento_des documento,"//0
			+ " comcab.comprobante_cod,"//1
			+ " comcab.documento_cod,"//2
			//+ " comcab.fuente,"
			+ " (CASE WHEN comcab.fuente IS NULL THEN ' ' ELSE comcab.fuente END) fuente,"//3
			+ " (CASE WHEN comcab.concepto IS NULL THEN ' ' ELSE comcab.concepto END) conceptocab,"//4
			//+ " TO_CHAR(comcab.fecha,'TMDay dd \"de\" TMMonth \"del\" yyyy') fecha,"
			+ " TO_CHAR(comcab.fecha, 'yyyy-MM-dd') fecha,"//5
			//+ " --fgen_fecha_letras(comcab.fecha) fecha,"
			+ " comcab.periodo_cod," //6
			+ " (CASE WHEN comcue.cuenta_cod IS NULL THEN ' ' ELSE comcue.cuenta_cod END) , " //7
			+ " (CASE WHEN comcue.debito IS NULL THEN 0.00 ELSE comcue.debito END) DEBITO , " //8
			+ " (CASE WHEN comcue.credito IS NULL THEN 0.00 ELSE comcue.credito END) , " //9
			//+ " --NVL(comcue.concepto, comcab.concepto) conceptodet,"
			+ " (CASE WHEN comcue.concepto IS NULL THEN (CASE WHEN comcab.concepto IS NULL THEN ' ' ELSE comcab.concepto END) ELSE comcue.concepto END) conceptodet,"//10
			+ " (CASE WHEN cue.cuenta_des IS NULL THEN ' ' ELSE cue.cuenta_des END) "//11
			+ " FROM org_organizacion org, org_documento doc, cta_comprobante comcab, cta_comprobante_cuenta comcue, cta_plan_cuenta cue"
			+ " WHERE org.organizacion_cod = comcab.organizacion_cod"
			+ "   AND doc.documento_cod = comcab.documento_cod"
			+ "   AND comcab.comprobante_cod = comcue.comprobante_cod"
			+ "   AND comcue.cuenta_cod = cue.cuenta_cod"
			+ "   AND doc.organizacion_cod = ?1"
			+ "   "
			+ "  and date(comcab.fecha) between to_date(?2,'dd/mm/yyyy') and to_date(?3,'dd/mm/yyyy') "
			+ "   "
//			+ "   --AND TO_CHAR(comcab.fecha, 'dd/mm/yyyy') >= $P{psFechaDesde}"
//			+ "   --AND TO_CHAR(comcab.fecha, 'dd/mm/yyyy') <= $P{psFechaHasta}"
			+ "ORDER BY comcab.fecha;",nativeQuery = true)
	
		List<Object[]> diarioGeneral(String orgCod,String fechaDesde,String fechaHasta);
	
		@Query(value = ""
				+ "SELECT s.cuenta_tipo_cod,"
				+ "             s.cuenta_tipo_des,"
				+ "             s.orden,"
				+ "             s.cuenta_cod,"
				//+ "             --fcta_rep_cuenta(s.cuenta_cod) cuenta_formato,"
				+ "             s.cuenta_cod cuentaFormato,"
				+ "             s.cuenta_des,"
				+ "             s.saldo saldo,"
				+ "             s.nivel,"
				//+ "             --'Balance General al ' || fgen_fecha_letras(id_fecha_final) fecha_corte_letra,"
				//+ "              TO_CHAR(current_date, 'dd/mm/yyyy') fechaActual,"
				//+ "             --frep_usr_organizacion_cod(iv_usuario_cod) organizacion_cod"
				+ "             organizacion_cod, "
				
				+ " 			TO_CHAR(s.fecha, 'yyyy-MM-dd') fecha "			
				+ "        FROM cal_cta_plan_saldo s"
				+ "       WHERE usuario_cod = ?2"
				+ "       	 AND organizacion_cod =?1"
				+ "         AND etapa = 1"
				+ "         AND cuenta_tipo_cod IN ('A', 'P', 'T')"
				+ "       ORDER BY s.orden, s.cuenta_cod;"
				+ "",nativeQuery = true)

		List<Object[]> balanceGeneral(String orgCod,String usuarioCod);
		
		@Query(value = ""
				+ " select "
				+ "total_activos, " //0
				+ "total_pasivos, "//1
				+ "total_patrimonio, "//2
				+ "total_ingresos, "//3
				+ "total_gastos, "//4
				+ "total_pasivos + total_patrimonio pasivo_patrimonio, "//5
				+ "total_costos + total_gastos + total_gastos_deduc costo_gasto, "//6
				+ "total_ingresos - total_costos - total_gastos - total_gastos_deduc resultado_operativo, "//7
				+ "total_costos, "//8
				+ "total_gastos_deduc "//9
				+ "from "
				+ "( "
				+ "  select "
				+ "  sum(case when cuenta_tipo_cod='A' then saldo else 0 end) total_activos, "
				+ "  sum(case when cuenta_tipo_cod='P' then saldo else 0 end) total_pasivos, "
				+ "  sum(case when cuenta_tipo_cod='T' then saldo else 0 end) total_patrimonio, "
				+ "  sum(case when cuenta_tipo_cod='I' then saldo else 0 end) total_ingresos, "
				+ "  sum(case when cuenta_tipo_cod='G' then saldo else 0 end) total_gastos, "
				+ "  sum(case when cuenta_tipo_cod='C' then saldo else 0 end) total_costos, "
				+ "  sum(case when cuenta_tipo_cod='N' then saldo else 0 end) total_gastos_deduc "
				+ "  from CAL_CTA_PLAN_SALDO "
				+ "  where usuario_cod = ?2 "
				+ "  and organizacion_cod =?1 "
				
				+ "  and etapa=1 "
				+ "  and nivel <= ?3 "
				+ ") AS suma;"
				+ "",nativeQuery = true)
		List<Object[]> sumaValoresPlanSaldo(String organizacionCod,String usuarioCod,Integer nivel);
		
		@Query(value = ""
				+ "SELECT s.cuenta_tipo_cod, "
				+ "             s.cuenta_tipo_des, "
				+ "             s.orden, "
				+ "             s.cuenta_cod cuenta_cod, "
				//+ "             --fcta_rep_cuenta(s.cuenta_cod) cuenta_formato, "
				+ "             s.cuenta_formato , "
				+ "             s.cuenta_des, "
				+ "             s.saldo saldo, "
				+ "             s.nivel, "
				//+ "             --'Estado de resultados  al ' || fgen_fecha_letras(id_fecha_final) fecha_corte_letra, "
				//+ "             --frep_usr_organizacion_cod(iv_usuario_cod) organizacion_cod "
				+ "             s.organizacion_cod , "
				+ "             TO_CHAR(s.fecha, 'yyyy-MM-dd') fecha  "
				+ "        FROM cal_cta_plan_saldo s "
				+ "       WHERE usuario_cod = ?2 "
				+ "        and organizacion_cod = ?1  "
				+ "       	AND etapa = 1 "
				+ "         AND cuenta_tipo_cod IN ('I', 'C', 'G', 'N') "
				+ "       ORDER BY s.orden, s.cuenta_cod;"
				+ "",nativeQuery = true)
		List<Object[]> estadoResultados(String organizacionCod,String usuarioCod);
		
		
		@Query(value = ""
				+ "SELECT CUENTA_COD, "
				+ "               CUENTA_DES, "
				+ "				  TO_CHAR(FECHA,'dd/mm/yyyy') FECHA, "
				+ "               COMPROBANTE_COD, "
				+ "               OBSERVACIONES, "
				+ "               DEBITO, "
				+ "               CREDITO, "
				+ "               SALDO_LINEA, "
				+ "			   SUM(SALDO_LINEA) OVER(PARTITION BY CUENTA_COD ORDER BY CASE WHEN SUBSTRING(COMPROBANTE_COD, 1, 2) = 'SI' THEN 0 ELSE 1 END,FECHA, COMPROBANTE_COD ROWS UNBOUNDED PRECEDING) SALDO, "
				+ "               CONCEPTO "
				+ "          FROM (SELECT CTA.CUENTA_COD, "
				+ "                        CTA.CUENTA_DES, "
				+ "                        CB.FECHA, "
				+ "                        CB.COMPROBANTE_COD, "
				+ "                        CB.DEUDOR_BENEFICIARIO OBSERVACIONES, "
				+ "                        CBC.DEBITO, "
				+ "                        CBC.CREDITO, "
				+ "						   ((CASE WHEN CBC.DEBITO IS NULL THEN 0 ELSE CBC.DEBITO END)-(CASE WHEN CBC.CREDITO IS NULL THEN 0 ELSE CBC.CREDITO END)) SALDO_LINEA, "
				+ "                        CASE WHEN CBC.CONCEPTO IS NULL THEN CB.CONCEPTO ELSE CBC.CONCEPTO END || ' ' ||  CASE WHEN CB.FUENTE IS NULL THEN ' ' ELSE CB.FUENTE END CONCEPTO "
				+ "                  FROM CTA_COMPROBANTE CB, CTA_COMPROBANTE_CUENTA CBC, CTA_PLAN_CUENTA CTA "
				+ "                 WHERE CB.COMPROBANTE_COD = CBC.COMPROBANTE_COD "
				+ "                   AND CBC.CUENTA_COD = CTA.CUENTA_COD "
				+ "                   AND CB.ESTADO_COD NOT IN ('ANU') "
				+ "                   AND CB.ORGANIZACION_COD = ?1 "
				+ "                   AND CTA.CUENTA_COD >= ?2 "
				+ "                   AND CTA.CUENTA_COD <= ?3 "
				+ " 				  AND date(CB.FECHA) between to_date(?4,'dd/mm/yyyy') and to_date(?5,'dd/mm/yyyy') "
				//+ "				      AND TO_CHAR(CB.FECHA,'dd/mm/yyyy') >= ?4 "
				//+ "                   AND TO_CHAR(CB.FECHA,'dd/mm/yyyy') <= ?5 "
				+ "                UNION ALL "
				+ "                SELECT CTA.CUENTA_COD, "
				+ "                        MAX(CTA.CUENTA_DES) CUENTA_DES, "
				+ "                        MAX(CB.FECHA) FECHA, "
				+ "                        ' ' COMPROBANTE_COD, "
				+ "                        ' ' OBSERVACIONES, "
				+ "                        SUM(CBC.DEBITO) DEBITO, "
				+ "                        SUM(CBC.DEBITO) CREDITO, "
				+ "						SUM((CASE WHEN CBC.DEBITO IS NULL THEN 0 ELSE CBC.DEBITO END)-(CASE WHEN CBC.CREDITO IS NULL THEN 0 ELSE CBC.CREDITO END)) SALDO_LINEA, "
				+ "                        'Saldo Anterior' CONCEPTO "
				+ "                  FROM CTA_COMPROBANTE CB, CTA_COMPROBANTE_CUENTA CBC, CTA_PLAN_CUENTA CTA "
				+ "                 WHERE CB.COMPROBANTE_COD = CBC.COMPROBANTE_COD "
				+ "                   AND CBC.CUENTA_COD = CTA.CUENTA_COD "
				+ " and CB.ESTADO_COD not in ('ANU') "
				+ " and CB.ORGANIZACION_COD = ?1  "
				+ " and CTA.CUENTA_COD >= ?2 "
				+ " and CTA.CUENTA_COD <= ?3 "
				+ " and date(CB.FECHA) between to_date(?4,'dd/mm/yyyy') and to_date(?5,'dd/mm/yyyy') "
				//+ " and TO_CHAR(CB.FECHA, 'dd/mm/yyyy') >= ?4 "
				//+ " and TO_CHAR(CB.FECHA, 'dd/mm/yyyy') < ?5 "
				+ " group by CTA.CUENTA_COD) as c;"
				+ "",nativeQuery = true)
		List<Object[]> mayorGeneral(String organizacionCod,String cuentaCodDesde,String cuentaCodHasta,String fechaDesde,String fechaHasta);
}

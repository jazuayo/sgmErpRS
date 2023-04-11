package ec.sgm.cta.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ec.sgm.cta.entity.PlanSaldo;
import ec.sgm.cta.modelo.PlanSaldoConsultaResp;
import ec.sgm.org.entity.Organizacion;

/**
 * 
 * @author CT Sigma
 *
 */
@Repository
@Transactional
public interface PlanSaldoRepository extends JpaRepository<PlanSaldo, Long> {

	/**
	 * 
	 * @param organizacion
	 * @param ld_comprobante_filtro_inicial
	 * @param ld_comprobante_filtro_final
	 * @param ld_comprobante_rango_inicial
	 * @return
	 */
	@Query("SELECT new ec.sgm.cta.modelo.PlanSaldoConsultaResp(" + "c.organizacion.organizacionCod,"
			+ "d.cuenta.cuentaCod,"
			+ "SUM(CASE WHEN c.fecha < ?4 THEN CASE WHEN d.debito IS null THEN 0.0 ELSE d.debito END ELSE 0.0 END), "
			+ "SUM(CASE WHEN c.fecha < ?4 THEN CASE WHEN d.credito IS null THEN 0.0 ELSE d.credito END ELSE 0.0 END), "
			+ "SUM(CASE WHEN c.fecha >= ?4 THEN CASE WHEN d.debito IS null THEN 0.0 ELSE d.debito END ELSE 0.0 END), "
			+ "SUM(CASE WHEN c.fecha >= ?4 THEN CASE WHEN d.credito IS null THEN 0.0 ELSE d.credito END ELSE 0.0 END) "
			+ ")  " + "FROM Comprobante c, ComprobanteCuenta d " + "WHERE c.comprobanteCod = d.comprobante "
			+ "AND c.organizacion = ?1 " + "AND c.fecha >= ?2 " + "AND c.fecha <= ?3 " + "AND c.estado != ('ANU') "
			+ "GROUP BY c.organizacion,d.cuenta")
	List<PlanSaldoConsultaResp> totalesCreditoDebitoDeCuentasContables(Organizacion organizacion,
			Date ld_comprobante_filtro_inicial, Date ld_comprobante_filtro_final, Date ld_comprobante_rango_inicial);

	/**
	 * Actualizar la tabla temporal de saldos si el nivel es igual a 0
	 */
	@Modifying
	@Query("UPDATE PlanSaldo p SET "
			+ "p.cuentaDes = (SELECT c.cuentaDes FROM PlanCuenta c WHERE c.cuentaCod = p.cuentaCod), "
			+ "p.cuentaTipoCod = (SELECT c.planCuentaTipo.cuentaTipoCod FROM PlanCuenta c WHERE c.cuentaCod = p.cuentaCod), "
			+ "p.cuentaTipoDes = (SELECT c.planCuentaTipo.cuentaTipoDes FROM PlanCuenta c WHERE c.cuentaCod = p.cuentaCod), "
			+ "p.orden = (SELECT c.planCuentaTipo.orden FROM PlanCuenta c WHERE c.cuentaCod = p.cuentaCod), "
			+ "p.saldoInicial = "
			+ "(CASE WHEN p.debitoInicial IS NULL THEN 0.0 ELSE p.debitoInicial END) - (CASE WHEN p.creditoInicial IS NULL THEN 0.0 ELSE p.creditoInicial END), "
			+ "p.saldo ="
			+ "(CASE WHEN p.debitoInicial IS NULL THEN 0.0 ELSE p.debitoInicial END) -  (CASE WHEN p.creditoInicial IS NULL THEN 0.0 ELSE p.creditoInicial END) "
			+ "+ (CASE WHEN p.debitos IS NULL THEN 0.0 ELSE p.debitos END) - (CASE WHEN p.creditos IS NULL THEN 0.0 ELSE p.creditos END), "
			+ "p.etapa = 1," + "p.nivel = 1," + "p.cuentaFormato = p.cuentaCod")

	void updateNivel_0();

	@Modifying
	@Query("UPDATE PlanSaldo p SET p.saldo = -p.saldo WHERE p.cuentaCod = ?1 ")
	int cuentaSaldoSignoNegativo(String cuentaCod);

	/**
	 * Mayorizar
	 * 
	 * @param cuentaCod
	 * @return
	 */
	@Query("SELECT new ec.sgm.cta.modelo.PlanSaldoConsultaResp(p.organizacionCod,p.cuentaCod,"
			+ "SUM(CASE WHEN p.debitoInicial IS NULL THEN 0.0 ELSE p.debitoInicial END), "
			+ "SUM(CASE WHEN p.creditoInicial IS NULL THEN 0.0 ELSE p.creditoInicial END), "
			+ "SUM(CASE WHEN p.debitos IS NULL THEN 0.0 ELSE p.debitos END), "
			+ "SUM(CASE WHEN p.creditos IS NULL THEN 0.0 ELSE p.creditos END) " + ") FROM PlanSaldo p WHERE "
			+ "p.cuentaCod = ?1 AND p.etapa = 0 GROUP BY p.organizacionCod,p.cuentaCod")
	List<PlanSaldoConsultaResp> mayorizarDebitoYCredito(String cuentaCod);

	void deleteBySaldo(Double saldo);

}

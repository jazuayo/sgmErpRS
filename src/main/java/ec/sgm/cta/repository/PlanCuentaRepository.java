package ec.sgm.cta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface PlanCuentaRepository extends JpaRepository<PlanCuenta, String> {

	List<PlanCuenta> findByCuentaNumAndOrganizacion(String cuentaNum, Organizacion organizacion);

	List<PlanCuenta> findByOrganizacionOrderByCuentaCod(Organizacion organizacion);

	List<PlanCuenta> findByCuentaCod(String cuentaCod);

	@Query("SELECT MIN(c.cuentaCod) FROM PlanCuenta c WHERE c.organizacion = ?1")
	String minCuentaCod(Organizacion organizacionCod);

	@Query("SELECT MAX(c.cuentaCod) FROM PlanCuenta c WHERE c.organizacion = ?1")
	String maxCuentaCod(Organizacion organizacionCod);

	/**
	 * recupero las cuentas de las cuentas que tienen que ser presentadas
	 * 
	 * @param organizacionCod
	 * @param nivel
	 * @return
	 */
	@Query("SELECT p FROM PlanCuenta p WHERE p.organizacion = ?1 AND p.nivel <= ?2  ORDER BY p.cuentaCod")
	List<PlanCuenta> cuentasPresentadas(Organizacion organizacion, Integer nivel);

	@Query("SELECT  cpc FROM PlanCuenta cpc WHERE cpc.organizacion = ?1 AND cpc.movimiento = TRUE ORDER BY cpc.cuentaCod")
	List<PlanCuenta> cuentaEsMovimientoPorOrganizacion(Organizacion organizacion);

	@Query("SELECT  cpc FROM PlanCuenta cpc WHERE cpc.organizacion = ?1 AND cpc.operativa = TRUE ORDER BY cpc.cuentaCod")
	List<PlanCuenta> cuentaEsOperativaPorOrganizacion(Organizacion organizacion);

}

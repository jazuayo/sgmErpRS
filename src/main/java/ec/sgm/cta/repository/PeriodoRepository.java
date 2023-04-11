package ec.sgm.cta.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.Periodo;

/**
 *
 * @author Marco
 */
@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Integer> {
	@Query("select c from Periodo c join c.organizaciones o where o.organizacionCod=?1 order by c.periodoCod desc")
	List<Periodo> recuperaPorOrganizacionCod(String organizacionCod);

	/**
	 * CT - Recuperar periodo entre fechas
	 * 
	 * @param id_fecha_final
	 * @return
	 */
	@Query("SELECT c FROM Periodo c WHERE ?1 BETWEEN c.fechaDesde AND c.fechaHasta")
	List<Periodo> recuperarPeriodoEntreFechas(Date id_fecha_final);

}

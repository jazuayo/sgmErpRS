package ec.sgm.cta.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.Comprobante;
import ec.sgm.cta.entity.ComprobanteTmp;

/**
 *
 * @author Marco
 */
@Repository
public interface ComprobanteTmpRepository extends JpaRepository<ComprobanteTmp, String> {
	@Query("select cc from ComprobanteTmp cc where cc.organizacionCod = ?1 and date(cc.fecha) = to_date(?2,'ddMMyyyy')")
	List<ComprobanteTmp> findByOrganizacionCodAndFecha(String orgCod, String fecha);

	List<ComprobanteTmp> findByOrganizacionCod(String organizacionCod);

	List<Comprobante> findByOrganizacionCodOrderByComprobanteCod(String organizacionCod);

	@Procedure(name = "pctaIntACGeneraComp")
	String pctaIntACGeneraComp(@Param("iv_origen") String origen);

	@Query(value = " select distinct (to_date(cast(cct.fecha as text), 'YYYY-MM-DD')) fecha from "
			+ "	cta_comprobante_tmp cct where organizacion_cod = ?1 order by fecha asc ", nativeQuery = true)
	List<Date> fechasComprobantesTmp(String organizacionCod);
}

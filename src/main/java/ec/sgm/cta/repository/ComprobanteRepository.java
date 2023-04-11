package ec.sgm.cta.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.Comprobante;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, String> {
	@Query(value = "select cc.comprobante_cod from cta_comprobante cc where cc.organizacion_cod = ?1 and cc.estado_cod = ?2 and date(cc.fecha) = to_date(?3,'ddMMyyyy')", nativeQuery = true)
	public List<Object[]> findByOrganizacionCodAndEstadoCodAndFecha(String orgCod, String estadoCod, String fecha);

	public List<Comprobante> findByOrganizacionOrderByComprobanteCod(Organizacion organizacion);

	@Query("select c from Comprobante c where c.organizacion=?1 and trunc(c.fecha) between trunc(?2) and trunc(?3) and c.comprobanteCod like ?4"
			+ " order by trunc(c.fecha),comprobanteCod")
	public List<Comprobante> recuperaPorFechasComprobante(Organizacion organizacion, Date fechaDesde, Date fechaHasta,
			String comprobante);

	@Query("select c from Comprobante c where c.fuente=?1 and c.organizacion.organizacionCod = ?2"
			+ " order by comprobanteCod desc")
	public List<Comprobante> findByfuente(String fuente, String organizacionCod);

	
	@Query("select c from Comprobante c where c.organizacion=?1 and date(c.fecha) between to_date(?3,'ddmmyyyy') and to_date(?4,'ddmmyyyy') and c.comprobanteCod like %?2%"
			+ " order by date(c.fecha)")
	public List<Comprobante> findByOrganizacionAndComprobanteCodLikeAndFechaBetween(Organizacion organizacion,
			String comprobante, String fechaDesde, String fechaHasta);

	@Query("select c from Comprobante c where c.organizacion=?1 and date(c.fecha) between to_date(?3,'ddmmyyyy') and to_date(?4,'ddmmyyyy') and lower(c.concepto) like %?2%"
			+ " order by date(c.fecha)")
	public List<Comprobante> findByOrganizacionAndConceptoContainingIgnoreCaseAndFechaBetween(Organizacion organizacion,
			String concepto, String fechaDesde, String fechaHasta);

	@Query("select c from Comprobante c where c.organizacion=?1 and date(c.fecha) between to_date(?4,'ddmmyyyy') and to_date(?5,'ddmmyyyy') and lower(c.concepto) like %?2%"
			+ " and c.comprobanteCod like %?3% order by date(c.fecha)")
	public List<Comprobante> findByOrganizacionAndConceptoContainingIgnoreCaseAndComprobanteCodContainingIgnoreCaseAndFechaBetween(
			Organizacion organizacion, String concepto, String comprobante, String fechaDesde, String fechaHasta);
}

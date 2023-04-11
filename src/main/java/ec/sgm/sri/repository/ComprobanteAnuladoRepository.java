package ec.sgm.sri.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Organizacion;
import ec.sgm.sri.entity.ComprobanteAnulado;

/**
 * 
 * @author MP
 *
 */
@Repository
public interface ComprobanteAnuladoRepository extends JpaRepository<ComprobanteAnulado, Long> {
	List<ComprobanteAnulado> findByOrganizacionOrderByRegId(Organizacion organizacion);

	@Query("select c from ComprobanteAnulado c"// where c.organizacion=?1 "
			//+ " where trunc(c.fechaAnula) between trunc(?1) and trunc(?2) "
			+ " where c.fechaAnula between ?1 and ?2 "
			+ " and c.organizacion.organizacionCod = ?3 "
			+ " order by c.comprobanteTipo,c.fechaAnula")
	List<ComprobanteAnulado> findbyOrganizacionFechas(Date fechaDesde, Date fechaHasta,String organizacionCod/* Organizacion organizacion, */);

}

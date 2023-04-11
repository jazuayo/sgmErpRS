package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.FacturaImpuesto;

/**
 *
 * @author Marco
 */
@Repository
public interface FacturaImpuestoRepository extends JpaRepository<FacturaImpuesto, Long> {
	List<FacturaImpuesto> findByDocumentoIdOrderByFacturaImpuestoId(Long documentoId);

	@Modifying
	@Query(value = "delete from FacturaImpuesto c where c.documentoId=?1")
	void deleteByDocumentoId(Long documentoId);
}

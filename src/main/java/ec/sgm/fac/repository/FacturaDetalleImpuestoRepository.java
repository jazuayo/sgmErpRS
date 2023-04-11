package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.FacturaDetalleImpuesto;

/**
 *
 * @author Marco
 */
@Repository
public interface FacturaDetalleImpuestoRepository extends JpaRepository<FacturaDetalleImpuesto, Long> {
	List<FacturaDetalleImpuesto> findByFacturaDetalleIdOrderByFacturaDetalleImpId(Long facturaDetalleId);

	void deleteByFacturaDetalleId(Long facturaDetalleId);

	@Modifying
	@Query(value = "delete from FacturaDetalleImpuesto c where c.facturaDetalleId IN "
			+ "(select fd.facturaDetalleId from FacturaDetalle fd where fd.documentoId=?1)")
	void deleteByFacturaId(Long facturaId);
}

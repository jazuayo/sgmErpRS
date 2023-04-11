package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.FacturaDetalle;

/**
 *
 * @author Marco
 */
@Repository
public interface FacturaDetalleRepository extends JpaRepository<FacturaDetalle, Long> {
	List<FacturaDetalle> findByDocumentoIdOrderByFacturaDetalleId(Long facturaId);
}

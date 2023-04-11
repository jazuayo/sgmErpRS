package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.RetencionDetalle;

/**
 *
 * @author Marco
 */
@Repository
public interface RetencionDetalleRepository extends JpaRepository<RetencionDetalle, Long> {
	List<RetencionDetalle> findByRetencionIdOrderByRetencionDetalleId(Long RetencionId);

	List<RetencionDetalle> findByFacturaOrderByRetencionDetalleId(Factura factura);
}

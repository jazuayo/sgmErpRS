package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.Retencion;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface RetencionRepository extends JpaRepository<Retencion, Long> {
	List<Retencion> findByOrganizacionOrderByRetencionId(Organizacion organizacion);

	@Query("select distinct r from Retencion r join fetch r.documento d join fetch r.organizacion join fetch r.estado join fetch r.detalles d"
			+ " where d.factura=?1 and r.estado.estadoCod <> ec.sgm.core.Constantes.ESTADO_ANULADO order by r.retencionId")
	List<Retencion> findFullByFactura(Factura factura);
}

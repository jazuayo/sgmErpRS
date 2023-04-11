package ec.sgm.cta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.ComprobanteAutDet;

/**
 *
 * @author Marco
 */
@Repository
public interface ComprobanteAutDetRepository extends JpaRepository<ComprobanteAutDet, Long> {

}

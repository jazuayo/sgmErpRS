package ec.sgm.cta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.ComprobanteAutCab;

/**
 *
 * @author Marco
 */
@Repository
public interface ComprobanteAutCabRepository extends JpaRepository<ComprobanteAutCab, Long> {

}

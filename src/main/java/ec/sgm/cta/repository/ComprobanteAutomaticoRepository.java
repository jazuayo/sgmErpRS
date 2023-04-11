package ec.sgm.cta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.ComprobanteAutomatico;

/**
 *
 * @author Marco
 */
@Repository
public interface ComprobanteAutomaticoRepository extends JpaRepository<ComprobanteAutomatico, String> {

}

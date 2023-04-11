package ec.sgm.cta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.PlanCuentaTipo;


/**
 *
 * @author Marco
 */
@Repository
public interface PlanCuentaTipoRepository extends JpaRepository<PlanCuentaTipo, String> {
}

package ec.sgm.cta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.PlanCuentaSaldo;

/**
 *
 * @author Marco
 */
@Repository
public interface PlanCuentaSaldoRepository extends JpaRepository<PlanCuentaSaldo, String> {
		
}

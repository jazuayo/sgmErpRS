package ec.sgm.cta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.PlanCuentaTipoOrg;
import ec.sgm.org.entity.Organizacion;

@Repository
public interface PlanCuentaTipoOrgRepository extends JpaRepository<PlanCuentaTipoOrg, Long> {

	@Query("SELECT p FROM PlanCuentaTipoOrg p WHERE p.organizacion = ?1 AND p.signo = -1")
	List<PlanCuentaTipoOrg> cuentasSignoNegativo(Organizacion organizacion);
}

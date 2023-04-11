package ec.sgm.fac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.CategoriaTipo;

/**
 *
 * @author Marco
 */
@Repository
public interface CategoriaTipoRepository extends JpaRepository<CategoriaTipo, String> {
}

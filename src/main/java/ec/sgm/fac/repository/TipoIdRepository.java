package ec.sgm.fac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.TipoId;

/**
 *
 * @author Marco
 */
@Repository
public interface TipoIdRepository extends JpaRepository<TipoId, String> {
}

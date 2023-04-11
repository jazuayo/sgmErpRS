package ec.sgm.fac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.PersonaTipo;

/**
 *
 * @author Marco
 */
@Repository
public interface PersonaTipoRepository extends JpaRepository<PersonaTipo, String> {

}

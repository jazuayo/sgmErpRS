package ec.sgm.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, String> {

}

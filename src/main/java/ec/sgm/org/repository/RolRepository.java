package ec.sgm.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Rol;

/**
 *
 * @author Marco
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, String> {
	// List<Usuario> findByOrganizacionOrderByNombre(Organizacion organizacion);
}

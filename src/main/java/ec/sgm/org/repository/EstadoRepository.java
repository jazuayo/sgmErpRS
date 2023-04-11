package ec.sgm.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Estado;

/**
 *
 * @author Marco
 */
@Repository
public interface EstadoRepository extends JpaRepository<Estado, String> {
	Estado findByEstadoCod(String estadoCod);
}

package ec.sgm.fac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Categoria;

/**
 *
 * @author Marco
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, String> {
}

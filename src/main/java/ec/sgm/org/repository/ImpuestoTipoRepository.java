package ec.sgm.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.ImpuestoTipo;

/**
 *
 * @author Marco
 */
@Repository
public interface ImpuestoTipoRepository extends JpaRepository<ImpuestoTipo, String> {

}

package ec.sgm.seg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.seg.entities.UsuarioRolOpcion;

/**
 * 
 * @author SIGMA - TL
 *
 */
@Repository
public interface UsuarioRolOpcionRepository extends JpaRepository<UsuarioRolOpcion, Long> {

}

package ec.sgm.seg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.seg.entities.UsuarioOpcion;


/**
 * 
 * @author SIGMA - TL
 *
 */
@Repository
public interface UsuarioOpcionRepository extends JpaRepository<UsuarioOpcion, Long>{

}

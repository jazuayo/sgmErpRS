package ec.sgm.seg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.seg.entities.UsuarioMenu;

/**
 * 
 * @author SIGMA - TL
 *
 */
@Repository
public interface UsuarioMenuRepository extends JpaRepository<UsuarioMenu, String> {

}

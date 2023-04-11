package ec.sgm.fac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.ItemGrupoTipo;

@Repository
public interface ItemGrupoTipoRepository extends JpaRepository<ItemGrupoTipo, String> {

}

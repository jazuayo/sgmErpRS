package ec.sgm.fac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.ItemGrupo;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface ItemGrupoRepository extends JpaRepository<ItemGrupo, String> {
	List<ItemGrupo> findByOrganizacionOrderByItemGrupoDes(Organizacion organizacion);

	@Query("select c from ItemGrupo c left join fetch c.impuestos where c.itemGrupoCod=?1")
	Optional<ItemGrupo> findByIdFull(String id);

	@Query(value = "select fig.item_grupo_cod from  fac_item_grupo fig  inner join fac_item_grupo_impuesto figi "
			+ "on fig.item_grupo_cod = figi.item_grupo_cod "
			+ "where figi.impuesto_cod = ?1 and  fig.organizacion_cod = ?2 ", nativeQuery = true)
	List<Object[]> itemGrupoPorImpuestoAndOrganizacion(String impuestoCod, String organizacionCod);

}

package ec.sgm.fac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.Item;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	List<Item> findByOrganizacionOrderByItemDes(Organizacion organizacion);

	@Query("select c from Item c left outer join c.categorias where c.itemId=?1")
	Optional<Item> findByIdFull(Long itemId);
}

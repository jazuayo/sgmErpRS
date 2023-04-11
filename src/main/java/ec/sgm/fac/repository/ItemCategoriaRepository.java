package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.ItemCategoria;

@Repository
public interface ItemCategoriaRepository extends JpaRepository<ItemCategoria, Long> {

	@Query("select c from ItemCategoria c WHERE c.itemId = ?1 AND c.categoria.categoriaCod = ?2")
	List<ItemCategoria> findByItemIdAndCategoriaCod(Long itemId, String categoriaCod);

	List<ItemCategoria> findByItemId(Long itemId);

	void deleteByItemId(Long itemId);

}

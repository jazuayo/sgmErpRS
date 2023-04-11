package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.ItemInventario;

@Repository
public interface ItemInventarioRepository extends JpaRepository<ItemInventario, Long> {

	@Query("select c from ItemInventario c where c.itemId=?1 and c.cantidad > ?2")
	List<ItemInventario> findByItemIdAndCantidad(Long itemId, Long cantidad);

	List<ItemInventario> findByLoteAndItemId(String lote, Long itemid);
}

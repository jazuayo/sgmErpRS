package ec.sgm.org.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Impuesto;
import ec.sgm.org.entity.ImpuestoTipo;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface ImpuestoRepository extends JpaRepository<Impuesto, String> {

	@Query("select c from Impuesto c join c.categorias cat where c.impuestoTipo.impuestoTipoCod=?1 and cat.organizacion=?2 order by c.impuestoDes")
	List<Impuesto> recuperaPorImpuestoTipoCodAndOrganizacionOrderByImpuestoDes(String impuestoTipoCod,
			Organizacion organizacion);

	@Query("select c from Impuesto c left outer join c.categorias where c.impuestoCod=?1")
	Optional<Impuesto> findByIdFull(String impuestoCod);
	
	List<Impuesto> findByImpuestoTipo(ImpuestoTipo impuestoTipo);

}

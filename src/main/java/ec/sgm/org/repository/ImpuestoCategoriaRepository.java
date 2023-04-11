package ec.sgm.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.org.entity.ImpuestoCategoria;

@Repository
public interface ImpuestoCategoriaRepository extends JpaRepository<ImpuestoCategoria, Long> {

	@Query("select c from ImpuestoCategoria c WHERE c.cuenta.cuentaCod = ?1 AND c.categoria.categoriaCod = ?2")
	List<ImpuestoCategoria> findByCuentaCodAndCategoriaCod(String cuentaCod, String categoriaCod);

	List<ImpuestoCategoria> findByCuenta(PlanCuenta cuenta);

	@Query("select c from ImpuestoCategoria c WHERE c.impuestoCod = ?1 AND c.categoria.categoriaCod = ?2")
	List<ImpuestoCategoria> findByImpuestoCodAndCategoriaCod(String impuestoCod, String categoriaCod);

	@Query("select c from ImpuestoCategoria c WHERE c.impuestoCod = ?1 AND c.organizacion.organizacionCod = ?2")
	List<ImpuestoCategoria> findByImpuestoCodAndOrganizacion(String impuestoCod, String organizacion);

	void deleteByImpuestoCod(String impuestoCod);

}

package ec.sgm.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Parametro;

@Repository
public interface ParametroRepository extends JpaRepository<Parametro, Integer> {
	@Query(value = "select c from Parametro c where c.clave =?1 and c.organizacion.organizacionCod =?2")
	List<Parametro> findByClaveAndOrganizacionCodOrderByParametroDes(String clave, String organizacionCod);
}

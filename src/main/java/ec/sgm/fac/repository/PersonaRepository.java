package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.Persona;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
	List<Persona> findByOrganizacionOrderByNombre(Organizacion organizacion);

	@Query("SELECT p FROM FAC_PERSONA p WHERE lower(p.nombre) LIKE %:personaNombre%")
	List<Persona> findByCoincidencia(@Param("personaNombre") String personaNombre);

	@Query("SELECT p FROM FAC_PERSONA p WHERE p.organizacion = ?1 AND p.esCliente = TRUE ORDER BY p.nombre ASC")
	List<Persona> personaEsClientePorOrganizacion(Organizacion organizacion);

	@Query("SELECT p FROM FAC_PERSONA p WHERE p.organizacion = ?1 AND p.esProveedor = TRUE ORDER BY p.nombre ASC")
	List<Persona> personaEsProveedorPorOrganizacion(Organizacion organizacion);

	List<Persona> findByNumeroIdAndOrganizacion(String numeroId, Organizacion organizacion);

}

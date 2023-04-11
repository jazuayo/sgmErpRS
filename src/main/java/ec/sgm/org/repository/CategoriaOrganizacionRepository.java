package ec.sgm.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.CategoriaOrganizacion;
import ec.sgm.org.entity.Organizacion;

@Repository
public interface CategoriaOrganizacionRepository extends JpaRepository<CategoriaOrganizacion, Long> {
	List<CategoriaOrganizacion> findByOrganizacion(Organizacion organizacion);
}

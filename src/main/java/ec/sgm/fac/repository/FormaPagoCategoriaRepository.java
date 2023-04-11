package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.FormaPagoCategoria;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Organizacion;

@Repository
public interface FormaPagoCategoriaRepository extends JpaRepository<FormaPagoCategoria, Long> {
	List<FormaPagoCategoria> findByOrigenAndOrganizacion(Categoria origen, Organizacion organizacion);
}

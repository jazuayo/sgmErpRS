package ec.sgm.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface DocumentoRepository extends JpaRepository<Documento, String> {
	@Query("select c from Documento c where c.organizacion=?1 and c.origen.categoriaCod=?2 order by c.orden")
	List<Documento> findByOrganizacionAndOrigenCodOrderByOrden(Organizacion organizacion, String origenCod);

	List<Documento> findByOrganizacionAndOrigenOrderByOrden(Organizacion organizacion, Categoria origenCod);

	List<Documento> findByOrganizacionOrderByDocumentoDes(Organizacion organizacion);

	List<Documento> findByDocumentoCod(String documentoCod);
}

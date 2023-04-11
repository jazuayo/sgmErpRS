package ec.sgm.org.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.DocumentoSerie;

/**
 *
 * @author Marco
 */
@Repository
public interface DocumentoSerieRepository extends JpaRepository<DocumentoSerie, Long> {
	List<DocumentoSerie> findByDocumentoCodOrderByDocSerieId(String documentoCod);

	@Query("select c from DocumentoSerie c where c.documentoCod=?1 and trunc(?2) between trunc(c.fechaEmision) and trunc(c.fechaCaduca) order by c.fechaCaduca")
	List<DocumentoSerie> findByDocumentoCodAndFecha(String documentoCod, Date fechaDocumento);
}

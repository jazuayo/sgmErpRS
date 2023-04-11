package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.DocumentoTipo;

/**
 *
 * @author Marco
 */
@Repository
public interface DocumentoTipoRepository extends JpaRepository<DocumentoTipo, String> {
	List<DocumentoTipo> findByDocumentoTipoCod(String documentoTipoCod);
}

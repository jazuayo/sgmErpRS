package ec.sgm.sri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.sri.entity.ComprobanteTipo;

/**
 * 
 * @author MP
 *
 */
@Repository
public interface ComprobanteTipoRepository extends JpaRepository<ComprobanteTipo, String> {
}

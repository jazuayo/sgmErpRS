package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.FormaPago;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, String> {
	List<FormaPago> findByOrganizacion(Organizacion organizacion);
	
	List<FormaPago> findByFormaPagoCod(String formaPagoCod);
}

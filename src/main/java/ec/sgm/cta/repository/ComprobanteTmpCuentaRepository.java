package ec.sgm.cta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.ComprobanteTmpCuenta;

@Repository
public interface ComprobanteTmpCuentaRepository extends JpaRepository<ComprobanteTmpCuenta, Long> {

}

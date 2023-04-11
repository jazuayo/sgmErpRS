package ec.sgm.cta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.sgm.cta.entity.Comprobante;
import ec.sgm.cta.entity.ComprobanteCuenta;

@Repository
public interface ComprobanteCuentaRepository extends JpaRepository<ComprobanteCuenta, Long> {
	List<ComprobanteCuenta> findByComprobante(Comprobante comprobante);
}

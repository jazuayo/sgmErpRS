package ec.sgm.fac.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.Persona;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;

/**
 *
 * @author Marco
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
	List<Factura> findByOrganizacionAndOrigenOrderByDocumentoId(Organizacion organizacion, Categoria origen);

	@Query("select f from Factura f join fetch f.documento d join fetch f.organizacion join fetch f.estado"
			+ " join fetch f.persona p join fetch f.documentoTipo dt join fetch f.formaPago fp"
			+ " where f.persona=?1 and f.origen=?2")
	List<Factura> findByPersonaAndOrigenOrderByDocumentoId(Persona persona, Categoria origen);

	@Query("select f from Factura f join fetch f.documento d join fetch f.organizacion join fetch f.estado"
			+ " join fetch f.persona p join fetch f.documentoTipo dt join fetch f.formaPago fp"
			+ " left join fetch f.detalles d left join fetch f.impuestos i where f.documentoId=?1")
	Optional<Factura> findByIdWithDetalles(Long documentoId);

	List<Factura> findByEstadoOrderByDocumentoId(Estado estado);

	List<Factura> findByEstadoAndOrganizacionOrderByDocumentoId(Estado estado, Organizacion organizacion);

	@Query("select f from Factura f where f.documento.origen.categoriaCod=?1 and f.organizacion.organizacionCod=?4"
			+ " and date(f.fechaEmite) between to_date(?2,'ddmmyyyy') and to_date(?3,'ddmmyyyy')  order by f.documentoId")
	List<Factura> findByfechasAndOrganizacionOrderByDocumentoId(String origenCod, String fechaDesde, String fechaHasta,
			String organizacionCod);

	List<Factura> findByFacturaModificaOrderByDocumentoId(Factura facturaOrigen);

	@Query("select f from Factura f where (f.origen.categoriaCod=?1 or f.origen.categoriaCod=?2) "
			// + " and trunc(f.fechaEmite) between trunc(?3) and trunc(?4) "
			+ " and date(f.fechaEmite) between to_date(?3,'ddmmyyyy') and to_date(?4,'ddmmyyyy') "
			+ " and f.estado.estadoCod <> ec.sgm.core.Constantes.ESTADO_ANULADO "
			+ " and f.organizacion.organizacionCod = ?5 " + " order by f.origen.categoriaCod, f.fechaEmite")
	List<Factura> findFacturasPorFechas(String origenCodFactura, String origenCodNotaCredito, String fechaDesde,
			String fechaHasta, String organizacionCod);

	@Query("select f from Factura f where f.documento.origen.categoriaCod=?1 and f.organizacion.organizacionCod=?2"
			+ " and f.persona.personaId = ?3 "
			+ " and date(f.fechaEmite) between to_date(?4,'ddmmyyyy') and to_date(?5,'ddmmyyyy')  order by f.documentoId")
	List<Factura> findByOrigenAndOrganizacionAndPersonaAndFechaEmiteBetweenOrderByDocumentoId(String origen,
			String organizacion, Long persona, String fechaDesde, String fechaHasta);

	List<Factura> findByDocumentoId(Long documentoId);

	List<Factura> findByOrganizacionAndObservaciones(Organizacion organizacion, String observaciones);

	List<Factura> findByOrganizacionAndOrigenAndEstadoIsNot(Organizacion organizacion, Categoria origen, Estado estado);

}

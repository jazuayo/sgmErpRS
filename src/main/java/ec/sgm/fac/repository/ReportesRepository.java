package ec.sgm.fac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ec.sgm.cta.entity.Periodo;



/**
 * Consultas nativas select a la base.
 * 
 * Base: POSTGRES
 * 
 * @author CT
 *
 */
@Repository
@Transactional
public interface ReportesRepository extends JpaRepository<Periodo, Integer> {

	@Query(value = "SELECT documento_id,"
			+ "           documento_des tipo,"
			+ "           numero_id identificacion,"
			+ "           nombre nombre_persona,"
			+ "           documento_numero num_factura,"
			+ "           TO_CHAR(fecha_emite, 'dd-mm-yyyy') fecha_emision,"
		
			+ "           (CASE WHEN subtotal_iva0 IS NULL THEN 0.0 ELSE subtotal_iva0 END) base_sin_iva,"
			
			+ "           (CASE WHEN subtotal_iva IS NULL THEN 0.0 ELSE subtotal_iva END) base_con_iva,"
			
			+ "           (CASE WHEN iva_valor IS NULL THEN 0.0 ELSE iva_valor END) iva,"
			
			+ "           (CASE WHEN subtotal_iva0 IS NULL THEN 0.0 ELSE subtotal_iva0 END)+"
			+ "           (CASE WHEN subtotal_iva IS NULL THEN 0.0 ELSE subtotal_iva END)+"
			+ "           (CASE WHEN iva_valor IS NULL THEN 0.0 ELSE iva_valor END) total_factura,"
			+ "           operacion,"
			+ "           estado"
			+ "      FROM (SELECT f.documento_id,"
			+ "                   god.documento_des,"
			+ "                   p.persona_id,"
			+ "                   p.numero_id,"
			+ "                   p.nombre,"
			+ "                   f.documento_numero,"
			+ "                   f.fecha_emite,"
			
			+ "                   0.0 subtotal_iva0,"
			
			+ "                   0.0 subtotal_iva,"
			
			+ "                   0.0 iva_valor,"
			+ "                   f.documento_valor,"
			+ "                   m.categoria_des  operacion,"
			+ "                   f.estado_cod estado"
			+ "              FROM fac_factura f, fac_persona p, org_documento god, org_categoria m"
			+ "             WHERE f.persona_id = p.persona_id"
			+ "               AND f.documento_cod = god.documento_cod"
			+ "               AND god.origen_cod  = m.categoria_cod "
			
			+ "               AND f.organizacion_cod = ?1"
			+ "               AND m.categoria_cod  = (CASE WHEN ?2 IS NULL THEN m.categoria_cod ELSE ?2 END) "
			+ "               AND date(f.fecha_emite) between to_date(?3,'dd-mm-yyyy') AND to_date(?4,'dd-mm-yyyy') "	
			//+ "				  AND to_char(f.fecha_emite, 'dd-mm-yyyy') >= to_char( CASE WHEN to_date(?3,'dd-mm-yyyy') IS NULL THEN f.fecha_emite ELSE to_date(?3,'dd-mm-yyyy') end, 'dd-mm-yyyy')"
			//+ "				  AND to_char(f.fecha_emite, 'dd-mm-yyyy') <= to_char( CASE WHEN to_date(?4,'dd-mm-yyyy') IS NULL THEN f.fecha_emite ELSE to_date(?4,'dd-mm-yyyy') end, 'dd-mm-yyyy')"
			+ "               ) as foo"
			+ "               ORDER BY documento_numero ;",nativeQuery = true)
	List<Object[]> reporteFacturas(String org,String categoriaCod,String fechaDesde,String fechaHasta);
	
	@Query(value = ""
			+ "select"
			+ "	R.RETENCION_ID,"
			+ "	TO_CHAR(R.FECHA_EMITE, 'dd-mm-yyyy') FECHA_EMISION,"
			+ "	P.PERSONA_ID IDENTIFICACION,"
			+ "	P.NOMBRE CLIENTE_PROVEEDOR,"
			+ "	R.DOCUMENTO_NUMERO,"
			+ "	RD.BASE_IMPONIBLE BASE_RETENCION,"
			+ "	I.sri_codigo  CODIGO_RETENCION,"
			+ "	I.PORCENTAJE PORCENTAJE_RETENCION,"
			+ "	RD.VALOR_RETENIDO VALOR_RETENIDO,"
			+ "	D.DOCUMENTO_DES DOCUMETO_FAC,"
			+ "	TO_CHAR(F.FECHA_EMITE, 'dd-mm-yyyy') FECHA_EMISION_FACT,"
			+ "	F.DOCUMENTO_NUMERO DOCUMENTO_NUMERO_FACT"
			+ " from"
			+ "	FAC_RETENCION R,"
			+ "	FAC_RETENCION_DETALLE RD,"
			+ "	FAC_FACTURA F,"
			+ "	ORG_DOCUMENTO D,"
			+ "	FAC_PERSONA P,"
			+ "	ORG_IMPUESTO I,"
			+ "	org_categoria O"
			+ " where"
			+ "	R.RETENCION_ID = RD.RETENCION_ID"
			+ "	and RD.FACTURA_ID = F.DOCUMENTO_ID"
			+ "	and RD.IMPUESTO_COD = I.IMPUESTO_COD"
			+ "	and F.PERSONA_ID = P.PERSONA_ID"
			+ "	and F.DOCUMENTO_COD = D.DOCUMENTO_COD"
			
			+ "	and F.origen_cod = O.categoria_cod"
			
            + " AND date(R.fecha_emite) between to_date(?3,'dd-mm-yyyy') AND to_date(?4,'dd-mm-yyyy') "	
			//+ "	AND to_char(R.fecha_emite, 'dd-mm-yyyy') >= to_char( CASE WHEN to_date(?3,'dd-mm-yyyy') IS NULL THEN R.fecha_emite ELSE to_date(?3,'dd-mm-yyyy') END, 'dd-mm-yyyy')"
			
			//+ "	AND to_char(R.fecha_emite, 'dd-mm-yyyy') <= to_char( CASE WHEN to_date(?4,'dd-mm-yyyy') IS NULL THEN R.fecha_emite ELSE to_date(?4,'dd-mm-yyyy') end, 'dd-mm-yyyy')"
			+ "	and F.ORGANIZACION_COD = ?1"
			+ "	and O.categoria_cod  = (CASE WHEN ?2 IS NULL THEN O.categoria_cod ELSE ?2 END)"
			+ " and R.estado_cod <> ?5 "
			+ " order by"
			+ "	R.FECHA_EMITE,"
			+ "	R.RETENCION_ID;",nativeQuery = true)
	List<Object[]> reporteRetenciones(String org,String categoriaCod,String fechaDesde,String fechaHasta,String estadoCod);
	
	
	
			@Query(value = ""
					+ "select "
					//+ "--ffd.item_id, fi.item_des ,"
					+ " od.documento_cod, "
					+ " TO_CHAR(ff.fecha_emite, 'dd-mm-yyyy'),  " 
					+ " od.documento_des, "
					+ " ffd.cantidad, "
					+ " ffd.precio_unitario, "
					+ " ffd.cantidad * ffd.precio_unitario precio_total,"
					+ " case when od.origen_cod='ComFac' then 1 else -1 end signo "
					+ " from fac_factura_detalle ffd,  fac_factura ff ,org_documento od"
					//+ "--, fac_item fi "
					+ " where ffd.documento_id =ff.documento_id "
					+ " and ff.documento_cod = od.documento_cod "
					//+ "--and ffd .item_id = fi.item_id "
					+ " and ff.organizacion_cod = ?1 "
					+ " and ffd.item_id = ?4 "
					//+ "--and od.inventario = true"
					+ " and ff.estado_cod <> ?5 "
					+ " AND date(ff.fecha_emite) between to_date(?2,'dd-mm-yyyy') AND to_date(?3,'dd-mm-yyyy') "	
					//+ " and to_char(ff.fecha_emite,'dd-mm-yyyy') >= to_char( CASE WHEN to_date(?2,'dd-mm-yyyy') IS NULL THEN ff.fecha_emite ELSE to_date(?2,'dd-mm-yyyy') end, 'dd-mm-yyyy')"
					//+ " and to_char(ff.fecha_emite,'dd-mm-yyyy') <= to_char( CASE WHEN to_date(?3,'dd-mm-yyyy') IS NULL THEN ff.fecha_emite ELSE to_date(?3,'dd-mm-yyyy') end, 'dd-mm-yyyy')  "
					+ " order by ff.fecha_emite , ff.documento_id",nativeQuery = true)
			List<Object[]> reporteInvMovimiento(String org,String fechaDesde,String fechaHasta,Long itemId, String estado);
	
	
	
	
}

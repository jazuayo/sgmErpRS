package ec.sgm.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ec.sgm.org.entity.Usuario;

/**
 *
 * @author Marco
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
	// List<Usuario> findByOrganizacionOrderByNombre(Organizacion organizacion);

	@Query(value = "select DISTINCT(um.ejecuta) from usuario_rol_opcion uro, usuario_opcion uo, usuario_menu um where uro.rol_id = ?1 and uro.opcion_id = uo.opcion_id and uo.menu_cod = um.menu_cod", nativeQuery = true)
	Object[] findOpcionesMenu(String codigoRol);
	
	@Query(value = "select DISTINCT(um.ejecuta) from usuario_rol_opcion uro, usuario_opcion uo, usuario_menu um where uro.rol_id in (select our.rol_cod from org_usuario_rol our where our.usuario_cod = ?1 and our.organizacion_cod = ?2) and uro.opcion_id = uo.opcion_id and uo.menu_cod = um.menu_cod", nativeQuery = true)
	Object[] findOpcionesMenuByUsuarioCodAndOrganizacionCod(String usuarioCod, String organizacionCod);

	@Query(value = "select uo.opcion_cod from usuario_rol_opcion uro, usuario_opcion uo, usuario_menu um where uro.rol_id = ?1 and uro.opcion_id = uo.opcion_id and uo.menu_cod = um.menu_cod and um.menu_cod =?2", nativeQuery = true)
	Object[] findOpcionesBotonesPorPantalla(String codigoRol, String codigoMenu);
	
	Usuario findByEmailAndClaveAndActivo(String email, String clave, Boolean activo);
}

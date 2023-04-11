package ec.sgm.core;

/**
 *
 * @author Marco
 */
public class Constantes {

	public final static String PAGINA_LISTA = "lista";
	public final static String PAGINA_REGISTRO = "registro";
	public final static String PAGINA_IMPRIME = "imprime";
	public final static String PAGINA_ACTUALIZA_LISTA = "actLista";
	public final static String MAPPING_LISTA = "/lista";
	public final static String MAPPING_REGISTRO = "/registro";
	public final static String MAPPING_EDITA = "/edita";
	public final static String MAPPING_ELIMINA = "/elimina";
	public final static String MAPPING_IMPRIME = "/imprime";
//public final static String MAPPING_IMPRIME = "/imprime/{id}";

	public final static String ATRIBUTO_TITULO = "titulo";
	public final static String ATRIBUTO_BOTONES = "botones";
	public final static String ATRIBUTO_LISTADO = "listado";
	public final static String ATRIBUTO_REGISTRO = "registro";
	public final static String ATRIBUTO_GRABAR_TEXTO = "grabarTexto";
	public final static String TITULO_LISTA = "Listado de Registros";
	public final static String TITULO_CREA = "Creando Registro";
	public final static String TITULO_EDITA = "Editando Registro";
	public final static String TITULO_CREA_ERROR = "Creando Registro(Revise)";
	public final static String TITULO_EDITA_ERROR = "Editando Registro(Revise)";

	public final static String ESTADO_INGRESANDO = "ING";
	public final static String ESTADO_MIGRADO = "MIG";
	// public final static String ESTADO_EDITADO = "EDI";
	public final static String ESTADO_REGISTRADO = "REG";
	public final static String ESTADO_ANULADO = "ANU";
	public final static String FORMATO_FECHA = "yyyy-MM-dd";

	public final static String ORIGEN_VENTA_FACTURA = "VenFac";
	public final static String ORIGEN_VENTA_RETENCION = "VenRet";
	public final static String ORIGEN_VENTA_NC = "VenNC";
	public final static String ORIGEN_COMPRA_FACTURA = "ComFac";
	public final static String ORIGEN_COMPRA_RETENCION = "ComRet";
	public final static String ORIGEN_COMPRA_NC = "ComNC";
	public final static String ORIGEN_COMPROBANTE_CONTABLE = "Cta";

	public final static String FLASH_ERROR = "error";
	public final static String FLASH_INFO = "info";
	public final static String FLASH_MENSAJE_OK = "Datos procesados correctamente";
	public final static String FLASH_MENSAJE_REVISE = "Revise sus datos";
	public final static String FLASH_MENSAJE_ELIMINADO = "Registro eliminado con exito";
	public final static String FLASH_MENSAJE_NO_ENCONTRADO = "El registro no se ha encontrado:";

	public final static String IMPUESTO_TIPO_IVA = "iva";
	public final static String IMPUESTO_TIPO_IVA_RETENCION = "ivaRet";
	public final static String IMPUESTO_TIPO_RENTA_RETENCION = "rentaRet";

	public final static String CATEGORIA_TIPO_ORIGEN = "origen";

	public final static String SERVER_REPORTE = "http://localhost:8087/sgmGR/";

	public final static String INVENTARIO_SIN_LOTE = "SinLote";

	public final static String CODIGO_REPORTE_INV = "invRep";
	// Tipo de reportes
	public final static String FORMATO_TIPO_REPORTE_PDF = ".pdf";
	// Codigos reportes contabilidad
	public final static String COD_REPORTE_BALANCE_GENERAL = "BG";
	public final static String COD_REPORTE_ESTADO_RESULTADOS = "ER";
}

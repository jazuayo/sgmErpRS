package ec.sgm.cta.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.sgm.SigmaException;
import ec.sgm.core.Constantes;
import ec.sgm.core.Numero;
import ec.sgm.cta.entity.PlanCuenta;
import ec.sgm.cta.service.PlanCuentaService;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.FacturaDetalle;
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.entity.Item;
import ec.sgm.fac.entity.ItemGrupo;
import ec.sgm.fac.repository.CategoriaRepository;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.fac.repository.ItemGrupoRepository;
import ec.sgm.org.entity.Categoria;
import ec.sgm.org.entity.Estado;
import ec.sgm.org.entity.Organizacion;
import ec.sgm.org.repository.EstadoRepository;
import ec.sgm.org.repository.OrganizacionRepository;
import ec.sgm.org.service.ImpuestoCategoriaService;

@RestController
@RequestMapping("/contables")
public class ContablesController {

	private static final Logger LOGGER = LogManager.getLogger(ContablesController.class);
	@Autowired
	private OrganizacionRepository organizacionRepository;
	@Autowired
	private FacturaRepository facRepository;
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private ItemGrupoRepository repositoryItemGrupo;
	@Autowired
	private PlanCuentaService serviceCuenta;
	@Autowired
	private ImpuestoCategoriaService serviceImpCategorias;
	@Autowired
	private CategoriaRepository repositoryCategoria;

	// variables

	private final String organizacionCod = "AC";
	private String cuentaNum = "";
	private Double valor = 0.0;
	private PlanCuenta cuenta = new PlanCuenta();
	private List<String> impCodigos = new ArrayList<String>();
	private List<Factura> facturas = new ArrayList<Factura>();
	private Categoria cate = new Categoria();

	// Metodos

	@GetMapping
	public void generarContables() throws SigmaException {
		try {
			// DEBE
			Double debe = 0.0;
			debe += generarContableCompras();
			debe += generarContablesRetCompras();
			debe += generarContablesNotaCredCompras();
			// HABER
			Double haber = 0.0;
			haber += generarContablesRetVentas();
			haber += generarContablesNotaCredVentas();
			// Resumen de valores
			System.out.println("\nDEBE: " + Numero.redondear(debe));
			System.out.println("HABER: " + Numero.redondear(haber));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar contables", e);
		}
	}

	/**
	 * Generar contable compra
	 * 
	 * @throws SigmaException
	 */
	private Double generarContableCompras() throws SigmaException {
		try {
			System.out.println("\nContable compras...");
			Double suma = 0.0;
			Organizacion organizacion = organizacionRepository.findById(organizacionCod).orElse(null);
			Estado estadoANU = estadoRepository.findById("ANU").orElse(null);

			// ----- 5 -------
			cuentaNum = "5";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			// ------------Suma de valores subtotal
			// Lista con los grupo de items que tengan ese valor de impuesto de la cuenta
			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_COMPRA_FACTURA);
			List<ItemGrupo> itemGrupos = new ArrayList<ItemGrupo>();
			for (String impuestoCod : impCodigos) {
				List<Object[]> grupos = repositoryItemGrupo.itemGrupoPorImpuestoAndOrganizacion(impuestoCod,
						organizacionCod);
				for (Object[] dato : grupos) {
					ItemGrupo itemGrupo = repositoryItemGrupo.findById((String) dato[0]).orElse(null);
					if (!itemGrupos.contains(itemGrupo)) {
						itemGrupos.add(itemGrupo);
					}
				}
			}
			valor = 0.0;

			// Busco las facturas a analizar
			cate = repositoryCategoria.findById(Constantes.ORIGEN_COMPRA_FACTURA).orElse(null);
			facturas = facRepository.findByOrganizacionAndOrigenAndEstadoIsNot(organizacion, cate, estadoANU);
			// Recorro cada item de las facturas y veo si pertenece al grupo obtenido previo
			for (Factura factura : facturas) {
				Set<FacturaDetalle> facturaDetalles = factura.getDetalles();
				for (FacturaDetalle detalle : facturaDetalles) {
					Item item = detalle.getItem();
					if (itemGrupos.contains(item.getItemGrupo())) {
						Double subTotal = Numero.redondear(
								(detalle.getCantidad() * detalle.getPrecioUnitario()) - detalle.getDescuentoValor());
						valor += subTotal;
					}
				}
			}

			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));

			// ----- 1901005013 -------
			cuentaNum = "1901005013";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			// Suma de valores
			valor = 0.0;
			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_COMPRA_FACTURA);
			cate = repositoryCategoria.findById(Constantes.ORIGEN_COMPRA_FACTURA).orElse(null);
			facturas = facRepository.findByOrganizacionAndOrigenAndEstadoIsNot(organizacion, cate, estadoANU);

			for (Factura factura : facturas) {
				Set<FacturaImpuesto> facturaImpuestos = factura.getImpuestos();
				for (FacturaImpuesto aux : facturaImpuestos) {
					if (impCodigos.contains(aux.getImpuesto().getImpuestoCod())) {
						valor += aux.getImpuestoValor();
					}
				}
			}
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));
			// ----- 2506 ------- REVISAR
			cuentaNum = "2506";
			valor = 0.0;
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_COMPRA_FACTURA);
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));

			return suma;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar contable compra", e);
		}
	}

	private Double generarContablesRetCompras() throws SigmaException {
		try {
			System.out.println("\nContable Retenciones de compras...");
			Double suma = 0.0;
			// ----- 2506 -------- REVISAR
			valor = 0.0;
			cuentaNum = "2506";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_COMPRA_RETENCION);
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));
			// ----- 25040505 -------
			valor = 0.0;
			cuentaNum = "25040505";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_COMPRA_RETENCION);
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));
			// ----- 25040510 -------
			valor = 0.0;
			cuentaNum = "25040510";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_COMPRA_RETENCION);
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));

			return suma;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar contables", e);
		}
	}

	private Double generarContablesNotaCredCompras() throws SigmaException {
		try {
			System.out.println("\nContable Nota de credito compras...");
			Double suma = 0.0;
			Organizacion organizacion = organizacionRepository.findById(organizacionCod).orElse(null);
			Estado estadoANU = estadoRepository.findById("ANU").orElse(null);
			// ----- 2506 --------
			valor = 0.0;
			cuentaNum = "2506";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_COMPRA_NC);
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));
			// ----- 5 -------
			valor = 0.0;
			cuentaNum = "5";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_COMPRA_NC);

			// Lista con los grupo de items que tengan ese valor de impuesto de la cuenta
			List<ItemGrupo> itemGrupos = new ArrayList<ItemGrupo>();
			for (String impuestoCod : impCodigos) {
				List<Object[]> grupos = repositoryItemGrupo.itemGrupoPorImpuestoAndOrganizacion(impuestoCod,
						organizacionCod);
				for (Object[] dato : grupos) {
					ItemGrupo itemGrupo = repositoryItemGrupo.findById((String) dato[0]).orElse(null);
					if (!itemGrupos.contains(itemGrupo)) {
						itemGrupos.add(itemGrupo);
					}
				}
			}
			valor = 0.0;

			// Busco las facturas a analizar
			cate = repositoryCategoria.findById(Constantes.ORIGEN_COMPRA_NC).orElse(null);
			facturas = facRepository.findByOrganizacionAndOrigenAndEstadoIsNot(organizacion, cate, estadoANU);
			// Recorro cada item de las facturas y veo si pertenece al grupo obtenido previo
			for (Factura factura : facturas) {
				Set<FacturaDetalle> facturaDetalles = factura.getDetalles();
				for (FacturaDetalle detalle : facturaDetalles) {
					Item item = detalle.getItem();
					if (itemGrupos.contains(item.getItemGrupo())) {
						Double subTotal = Numero.redondear(
								(detalle.getCantidad() * detalle.getPrecioUnitario()) - detalle.getDescuentoValor());
						valor += subTotal;
					}
				}
			}

			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));
			// ----- 1901005013 -------
			valor = 0.0;
			cuentaNum = "1901005013";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_COMPRA_NC);

			cate = repositoryCategoria.findById(Constantes.ORIGEN_COMPRA_NC).orElse(null);
			facturas = facRepository.findByOrganizacionAndOrigenAndEstadoIsNot(organizacion, cate, estadoANU);

			for (Factura factura : facturas) {
				Set<FacturaImpuesto> facturaImpuestos = factura.getImpuestos();
				for (FacturaImpuesto aux : facturaImpuestos) {
					if (impCodigos.contains(aux.getImpuesto().getImpuestoCod())) {
						valor += aux.getImpuestoValor();
					}
				}
			}

			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));

			return suma;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar contables", e);
		}
	}

	private Double generarContablesRetVentas() throws SigmaException {
		try {
			System.out.println("\nContable Retenciones de ventas...");
			Double suma = 0.0;
			// ----- 1901005011 --------
			valor = 0.0;
			cuentaNum = "1901005011";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));
			// ----- 1901005012 -------
			valor = 0.0;
			cuentaNum = "1901005012";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));
			// ----- 2608090056 -------
			valor = 0.0;
			cuentaNum = "2608090056";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));

			return suma;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar contables", e);
		}
	}

	private Double generarContablesNotaCredVentas() throws SigmaException {
		try {
			System.out.println("\nContable Nota de credito ventas...");
			Double suma = 0.0;
			Organizacion organizacion = organizacionRepository.findById(organizacionCod).orElse(null);
			Estado estadoANU = estadoRepository.findById("ANU").orElse(null);
			// ----- 4104015 --------
			valor = 0.0;
			cuentaNum = "4104015";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);

			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_VENTA_NC);

			// Lista con los grupo de items que tengan ese valor de impuesto de la cuenta
			List<ItemGrupo> itemGrupos = new ArrayList<ItemGrupo>();
			for (String impuestoCod : impCodigos) {
				List<Object[]> grupos = repositoryItemGrupo.itemGrupoPorImpuestoAndOrganizacion(impuestoCod,
						organizacionCod);
				for (Object[] dato : grupos) {
					ItemGrupo itemGrupo = repositoryItemGrupo.findById((String) dato[0]).orElse(null);
					if (!itemGrupos.contains(itemGrupo)) {
						itemGrupos.add(itemGrupo);
					}
				}
			}
			valor = 0.0;

			// Busco las facturas a analizar
			cate = repositoryCategoria.findById(Constantes.ORIGEN_VENTA_NC).orElse(null);
			facturas = facRepository.findByOrganizacionAndOrigenAndEstadoIsNot(organizacion, cate, estadoANU);
			// Recorro cada item de las facturas y veo si pertenece al grupo obtenido previo
			for (Factura factura : facturas) {
				Set<FacturaDetalle> facturaDetalles = factura.getDetalles();
				for (FacturaDetalle detalle : facturaDetalles) {
					Item item = detalle.getItem();
					if (itemGrupos.contains(item.getItemGrupo())) {
						Double subTotal = Numero.redondear(
								(detalle.getCantidad() * detalle.getPrecioUnitario()) - detalle.getDescuentoValor());
						valor += subTotal;
					}
				}
			}

			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));
			// ----- 2602015 -------
			valor = 0.0;
			cuentaNum = "2602015";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);

			impCodigos = serviceImpCategorias.recuperarCodigoImpuesto(cuenta.getCuentaCod(),
					Constantes.ORIGEN_VENTA_NC);

			cate = repositoryCategoria.findById(Constantes.ORIGEN_VENTA_NC).orElse(null);
			facturas = facRepository.findByOrganizacionAndOrigenAndEstadoIsNot(organizacion, cate, estadoANU);

			for (Factura factura : facturas) {
				Set<FacturaImpuesto> facturaImpuestos = factura.getImpuestos();
				for (FacturaImpuesto aux : facturaImpuestos) {
					if (impCodigos.contains(aux.getImpuesto().getImpuestoCod())) {
						valor += aux.getImpuestoValor();
					}
				}
			}

			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));
			// ----- 2608090055 -------
			valor = 0.0;
			cuentaNum = "2608090055";
			cuenta = serviceCuenta.findByNumCuentaAndOrganizacion(cuentaNum, organizacionCod);
			suma += valor;
			System.out.println(cuentaNum + " -> " + cuenta.getCuentaDes() + " -> " + Numero.redondear(valor));

			return suma;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar contables", e);
		}
	}

}

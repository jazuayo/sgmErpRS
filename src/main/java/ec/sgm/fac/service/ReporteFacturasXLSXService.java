package ec.sgm.fac.service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.ce.util.XLSXServices;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.core.Numero;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.FacturaImpuesto;
import ec.sgm.fac.repository.FacturaRepository;
import ec.sgm.fac.repository.ReportesRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class ReporteFacturasXLSXService {
	private static final Logger LOGGER = LogManager.getLogger(ReporteFacturasXLSXService.class);
	@Autowired
	private XLSXServices exelServices;
	@Autowired
	private ReportesRepository repository;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private FacturaRepository repositoryFac;

	public byte[] generarReportesFacturasExcel(String orgCod, String categoriaCod, String fechaDesde, String fechaHasta)
			throws SigmaException {
		try {
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", orgCod);
			System.out.println("Buscando registros...");

			List<Object[]> registros = repository.reporteFacturas(orgCod, categoriaCod, fechaDesde, fechaHasta);
			System.out.println("Numero de registros: " + registros.size());
			// Generando excel
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Registros");

			// Cabecera del excel
			List<String> headers = Arrays.asList("ID", "Tipo", "Identificacion", "Persona", "Num. Factura",
					"Fecha Emision", "Base sin IVA", "Base con IVA", "IVA", "Total Factura", "Operacion", "Estado");
			Row header = sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();

			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 12);
			font.setBold(true);
			headerStyle.setFont(font);

			Integer siceColumn = 4000;
			for (int i = 0; i <= headers.size() - 1; i++) {
				sheet.setColumnWidth(i, siceColumn);
				Cell headerCell = header.createCell(i);
				headerCell.setCellValue(headers.get(i));
				headerCell.setCellStyle(headerStyle);
			}

			// Datos de la tabla de excel

			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);

			for (int i = 0; i <= registros.size() - 1; i++) {

				Object[] dato = registros.get(i);

				BigInteger docuId = (BigInteger) dato[0];
				Factura factura = repositoryFac.findById(docuId.longValue()).orElse(null);
				Set<FacturaImpuesto> facturaImpuestos = factura.getImpuestos();
				Double baseImponibleTotal = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, null);
				Double baseImponibleConImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, true);
				Double baseImponibleSinImp = FacturaImpuesto.sumaBaseImponible(facturaImpuestos, false);
				Double impuestosValor = FacturaImpuesto.sumaImpuestoValor(facturaImpuestos);

				Row fila = sheet.createRow(i + 1);

				Cell celda = fila.createCell(0);
				celda.setCellValue(docuId.toString());

				celda = fila.createCell(1);
				celda.setCellValue((String) dato[1]);

				celda = fila.createCell(2);
				celda.setCellValue((String) dato[2]);

				celda = fila.createCell(3);
				celda.setCellValue(exelServices.depurarDatos((String) dato[3]));

				celda = fila.createCell(4);
				celda.setCellValue((String) dato[4]);

				celda = fila.createCell(5);
				celda.setCellValue((String) dato[5]);

				celda = fila.createCell(6);
				celda.setCellValue(Numero.aString(baseImponibleSinImp, 2));

				celda = fila.createCell(7);
				celda.setCellValue(Numero.aString(baseImponibleConImp, 2));

				celda = fila.createCell(8);
				celda.setCellValue(Numero.aString(impuestosValor, 2));

				celda = fila.createCell(9);
				celda.setCellValue(Numero.aString(baseImponibleTotal, 2));

				celda = fila.createCell(10);
				celda.setCellValue((String) dato[10]);

				celda = fila.createCell(11);
				celda.setCellValue((String) dato[11]);

				celda.setCellStyle(style);

			}

			// Guardar archivo
			byte[] arrayByte = exelServices.generarArrayByte(workbook);

			// Nombre
			String fechaReporte = Fecha.formatoReportesFechaHora(new Date()).toString().replaceAll("\\s", "_")
					.replaceAll(":", "-").replaceAll("/", "-");
			String nombrePdf = "reporte_" + categoriaCod + "_" + fechaReporte;
			exelServices.guardarArchivo(pathArchivos, nombrePdf, arrayByte);
			System.out.println(
					"Comprobante pdf generado en: " + pathArchivos + nombrePdf + Constantes.FORMATO_TIPO_REPORTE_EXCEL);
			workbook.close();
			return arrayByte;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar reporte excel de facturas", e);
		}
	}
}

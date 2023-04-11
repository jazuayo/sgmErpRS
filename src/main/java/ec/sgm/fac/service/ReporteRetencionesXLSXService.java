package ec.sgm.fac.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import ec.sgm.fac.repository.ReportesRepository;
import ec.sgm.org.service.ParametroService;

@Service
public class ReporteRetencionesXLSXService {
	private static final Logger LOGGER = LogManager.getLogger(ReporteRetencionesXLSXService.class);
	@Autowired
	private XLSXServices exelServices;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private ReportesRepository repository;

	/**
	 * Generar reporte de retenciones excel
	 * 
	 * @param orgCod
	 * @param categoriaCod
	 * @param fechaDesde
	 * @param fechaHasta
	 * @return
	 * @throws SigmaException
	 */
	public byte[] generarReportesRetencionesExcel(String orgCod, String categoriaCod, String fechaDesde,
			String fechaHasta) throws SigmaException {
		try {
			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", orgCod);
			System.out.println("Buscando registros...");
			List<Object[]> registros = repository.reporteRetenciones(orgCod, categoriaCod, fechaDesde, fechaHasta,
					Constantes.ESTADO_ANULADO);
			System.out.println("Numero de registros: " + registros.size());
			System.out.println("Generando reporte...");
			// Generando excel
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Registros");

			// Cabecera del excel
			List<String> headers = Arrays.asList("Retencion Id", "Fecha Emision", "Identificacion", "Cliente Proveedor",
					"Num. Documento", "Base Retencion", "Cod. Retencion", "% Retencion", "Valor Retenido",
					"Documento Fac", "Fecha Emision Fac", "Documento Numero Fac");
			Row header = sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();

			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 12);
			font.setBold(true);
			headerStyle.setFont(font);

			Integer siceColumn = 5000;
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
				Row fila = sheet.createRow(i + 1);

				for (int j = 0; j <= headers.size() - 1; j++) {
					Cell celda = fila.createCell(j);
					celda.setCellValue(exelServices.depurarDatos(dato[j].toString()));

					celda.setCellStyle(style);
				}

			}
			// Guardar archivo
			byte[] arrayByte = exelServices.generarArrayByte(workbook);

			// Nombre
			String fechaReporte = Fecha.formatoReportesFechaHora(new Date()).toString().replaceAll("\\s", "_")
					.replaceAll(":", "-").replaceAll("/", "-");
			String nombre = "reporte_ret_" + categoriaCod + "_" + fechaReporte;
			exelServices.guardarArchivo(pathArchivos, nombre, arrayByte);
			System.out.println(
					"Comprobante generado en: " + pathArchivos + nombre + Constantes.FORMATO_TIPO_REPORTE_EXCEL);
			workbook.close();
			return arrayByte;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar reporte excel de retenciones", e);
		}
	}
}
package ec.sgm.ce.util;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.core.Constantes;

/**
 * Crear las funciones para procesar EXCEL
 * 
 * @author CT
 *
 */
@Service
public class XLSXServices {
	private static final Logger LOGGER = LogManager.getLogger(XLSXServices.class);

	/**
	 * Generar arrayByte para exportar a front
	 * 
	 * @param workbook
	 * @return
	 * @throws SigmaException
	 */
	public byte[] generarArrayByte(Workbook workbook) throws SigmaException {
		try {
			ByteArrayOutputStream arrayByte = new ByteArrayOutputStream();
			workbook.write(arrayByte);
			return arrayByte.toByteArray();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar arrayByte.", e);
		}
	}

	/**
	 * Guardar archivo excel
	 * 
	 * @param ubicacionPath
	 * @param nombre
	 * @param arrayByte
	 * @return
	 * @throws SigmaException
	 */
	public String guardarArchivo(String ubicacionPath, String nombre, byte[] arrayByte) throws SigmaException {
		try {
			Path path = Paths.get(ubicacionPath + nombre + Constantes.FORMATO_TIPO_REPORTE_EXCEL);
			Files.write(path, arrayByte);
			return path.toString();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException(
					"Error al guardar archivo:" + ubicacionPath + nombre + Constantes.FORMATO_TIPO_REPORTE_EXCEL, e);
		}
	}

	/**
	 * Depurar datos
	 * 
	 * @param dataDepurada
	 * @return
	 * @throws SigmaException
	 */
	public String depurarDatos(String dataDepurada) throws SigmaException {
		try {
			dataDepurada = dataDepurada.replace('Ñ', 'N');
			dataDepurada = dataDepurada.replace('Á', 'A');
			dataDepurada = dataDepurada.replace('É', 'E');
			dataDepurada = dataDepurada.replace('Í', 'I');
			dataDepurada = dataDepurada.replace('Ó', 'O');
			dataDepurada = dataDepurada.replace('Ú', 'O');
			dataDepurada = dataDepurada.replace('ñ', 'n');
			dataDepurada = dataDepurada.replace('á', 'a');
			dataDepurada = dataDepurada.replace('é', 'e');
			dataDepurada = dataDepurada.replace('í', 'i');
			dataDepurada = dataDepurada.replace('ó', 'o');
			dataDepurada = dataDepurada.replace('ú', 'u');
			dataDepurada = dataDepurada.replace('&', 'Y');
			dataDepurada = dataDepurada.replace("Yamp;", "Y");
			return dataDepurada;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al depurar datos.", e);
		}
	}
}

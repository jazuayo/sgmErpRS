package ec.sgm.ce.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.pdf.Barcode128;

import ec.sgm.SigmaException;

@Service
public class HtmlServices {
	private static final Logger LOGGER = LogManager.getLogger(HtmlServices.class);

	/**
	 * Pasar de archivo a buffer imagen
	 * 
	 * @param pathArchivo
	 * @return
	 * @throws SigmaException
	 */
	public BufferedImage archivoImgsBuffer(String pathArchivo) throws SigmaException {
		BufferedImage bufferImage = null;
		try {
			File archivoLogo = new File(pathArchivo);
			bufferImage = ImageIO.read(archivoLogo);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al pasar imagen" + pathArchivo + " a buffer.", e);
		}
		return bufferImage;
	}

	/**
	 * Pasar archivo html a string para poder editar
	 * 
	 * @param pathHtml
	 * @return
	 * @throws SigmaException
	 */
	public String archivoHtmlaString(String pathHtml) throws SigmaException {
		String htmlString = null;
		try {
//			File file = ResourceUtils.getFile("classpath:" + pathHtml);
			File file = new File(pathHtml);
			htmlString = new String(Files.readAllBytes(file.toPath()));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al pasar plantilla html a string.", e);
		}
		return htmlString;
	}

	/**
	 * Generar codigo de barras 128
	 * 
	 * @param codigo
	 * @return
	 * @throws SigmaException
	 */
	public BufferedImage generarCodigoBarras(String codigo) throws SigmaException {
		BufferedImage imgBuffered = null;
		try {
			Barcode128 barcode = new Barcode128();
			barcode.setCode(codigo);
			Image image = barcode.createAwtImage(Color.BLACK, Color.WHITE);
			imgBuffered = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics g = imgBuffered.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar codigo de barras", e);
		}
		return imgBuffered;
	}

	/**
	 * Pasar el buffer de una imagen a base 64 para agregar a templateHtml
	 * 
	 * CT
	 * 
	 * @param bufferedImage
	 * @return
	 * @throws SigmaException
	 */
	public String imagenBufferABase64(BufferedImage bufferedImage) throws SigmaException {
		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", byteArray);
			String data = DatatypeConverter.printBase64Binary(byteArray.toByteArray());
			String imagenString = "data:image/png;base64," + data;
			return imagenString;
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al convertir buffer imagen a base64", e);
		}

	}

	/**
	 * Pasar de String html a pdf buffer arrayByte
	 *
	 * CT
	 * 
	 * @param htmlString
	 * @return
	 * @throws SigmaException
	 */
	public byte[] htmlAPdf(String htmlString) throws SigmaException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(htmlString);
			renderer.layout();
			renderer.createPDF(outputStream, false);
			renderer.finishPDF();
			return outputStream.toByteArray();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al generar pdf desde string html", e);
		}
	}

	/**
	 * Guardar archivo desde un array de bytes (pdf o el html)
	 * 
	 * CT
	 * 
	 * @param ubicacionPath
	 * @param arrayByte
	 * @param nombre
	 * @param tipoArchivo
	 * @throws SigmaException
	 */
	public String guardarArchivo(String ubicacionPath, byte[] arrayByte, String nombre) throws SigmaException {
		try {
			Path path = Paths.get(ubicacionPath + nombre + ".pdf");
			Files.write(path, arrayByte);
			return path.toString();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al guardar archivo:" + ubicacionPath + nombre + ".pdf", e);
		}

	}

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

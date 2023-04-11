package ec.sgm.ce.util;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ec.sgm.SigmaException;

/**
 * Crear las funciones para procesar XML
 * 
 * @author CT
 *
 */
@Service
public class XMLServices {
	private static final Logger LOGGER = LogManager.getLogger(XMLServices.class);
	/**
	 * GUARDAR LOS ARCHIVOS XML
	 * 
	 * @param path
	 * @param nombre
	 * @param doc
	 * @throws SigmaException
	 */
	public String guardarXML(String archivoPath, Document doc) throws SigmaException {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult streamResult = new StreamResult(new File(archivoPath));
			// Depurar datos
//			Document docDepurado = depurarDatos(doc);
//			DOMSource domSource = new DOMSource(docDepurado);
			DOMSource domSource = new DOMSource(doc);
			transformer.transform(domSource, streamResult);
			return archivoPath;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al guardar XML.", e);
		}
	}

	/**
	 * Depurar datos de xml, remplazo de caracteres
	 * 
	 * @param doc
	 * @return
	 * @throws SigmaException
	 */
	private Document depurarDatos(Document doc) throws SigmaException {
		try {
			String dataDepurada = stringDesdeDocument(doc);

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
			dataDepurada = dataDepurada.replace("&", "Y");
			dataDepurada = dataDepurada.replace("Yamp;", "Y");

			return documentDesdeString(dataDepurada);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al depurar datos XML.", e);
		}
	}

	/**
	 * Pasar de tipo Document a String
	 * 
	 * @param doc
	 * @return
	 * @throws SigmaException
	 */
	private String stringDesdeDocument(Document doc) throws SigmaException {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al convertir a string desde Document", e);
		}
	}

	/**
	 * Pasar de tipo String a Document
	 * 
	 * @param xmlStr
	 * @return
	 */
	private static Document documentDesdeString(String xmlStr) throws SigmaException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
			return doc;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al convertir a Document desde String", e);
		}

	}

}

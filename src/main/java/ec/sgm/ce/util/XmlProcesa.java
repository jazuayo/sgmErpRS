package ec.sgm.ce.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlProcesa {
	public static Document DocumentDesdeString(String xml) {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource source = new InputSource();
			source.setCharacterStream(new StringReader(xml));

			Document document = builder.parse(source);
			return document;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("Error al convertir el documento:" + e.getMessage());
			System.err.println(xml);
			throw new RuntimeException("Error al convertir el documento:" + e.getMessage());
		}
	}

	public static String getValorPorTagName(String xml, String tagName)
			throws ParserConfigurationException, SAXException, IOException {
		Document document = DocumentDesdeString(xml);
		return document.getElementsByTagName(tagName).item(0).getTextContent();

	}

	public static Element getElementPorTagName(Document document, String tagName) {
		Element element = (Element) document.getElementsByTagName(tagName).item(0);
		return element;

	}

	public static Element getElementPorTagName(Element padre, String tagName) {
		Element element = (Element) padre.getElementsByTagName(tagName).item(0);
		return element;

	}

	public static Element getElementPorTagNames(Element padre, List<String> tagNames) {
		Element element = padre;
		for (String tagName : tagNames) {
			element = (Element) element.getElementsByTagName(tagName).item(0);
		}
		return element;

	}

	public static Element getElementPorTagNames(Document document, List<String> tagNames) {
		Element element = (Element) document.getElementsByTagName(tagNames.get(0)).item(0);
		boolean primero = true;
		for (String tagName : tagNames) {
			if (!primero) {
				element = (Element) element.getElementsByTagName(tagName).item(0);
			}
			primero = false;
		}
		return element;

	}
}

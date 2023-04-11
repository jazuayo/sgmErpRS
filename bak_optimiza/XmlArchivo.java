package ec.sgm.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlArchivo {

	public void agregaDouble2D(Document document, Element elementPadre, String etiqueta, double valor) {
		Element elementHijo = document.createElement(etiqueta);
		elementPadre.appendChild(elementHijo);
		elementHijo.appendChild(document.createTextNode(Numero.aString(valor, 2)));
	}

}

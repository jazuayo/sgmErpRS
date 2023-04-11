package ec.sgm.ce.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ec.sgm.ce.modelo.ClaveAcceso;
import ec.sgm.ce.util.Archivo;
import ec.sgm.ce.util.ServicioWeb;
import ec.sgm.ce.util.ServicioWebResponse;
import ec.sgm.ce.util.XmlProcesa;

public class SriService {
	public final static String URL_RECEPCION_PRODUCCION = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
	public final static String URL_RECEPCION_PRUEBAS = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
	public final static String URL_AUTORIZACION_PRODUCCION = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
	public final static String URL_AUTORIZACION_PRUEBAS = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";
	public final static String ESTADO_KEY = "estado";
	public final static String ESTADO_RECEPCION_OK = "RECIBIDA";
	public final static String ESTADO_AUTORIZACION_OK = "AUTORIZADO";

	public static String generaClaveAcceso(ClaveAcceso claveAcceso) {
		if (claveAcceso.getSerie().length() != 6)
			throw new RuntimeException("La serie debe de tener una longitud de 6");
		if (claveAcceso.getSecuencial().length() != 9)
			throw new RuntimeException("El secuencial debe de tener una longitud de 9");

		String claveGenerada = claveAcceso.getFechaEmision() // 'ddmmyyyy'
				+ claveAcceso.getTipoComprobante() //
				+ claveAcceso.getNumeroRuc() //
				+ claveAcceso.getTipoAmbiente() //
				+ claveAcceso.getSerie().substring(0, 3) + claveAcceso.getSerie().substring(3)
				+ claveAcceso.getSecuencial() //
				+ claveAcceso.getCodigoNumerico() //
				+ claveAcceso.getTipoEmision();

		if (claveGenerada.length() != 48)
			throw new RuntimeException("La longitud de la clave debe ser 48");

		int suma = 0;
		int factor = 2;
		for (int i = 1; i <= 48; i++) {
			int digitoPos = claveGenerada.length() - i;
			String digito = claveGenerada.substring(digitoPos, digitoPos + 1);

			if (factor == 8)
				factor = 2;
			int ln_aplica = Integer.valueOf(digito) * factor;
			suma = suma + (ln_aplica);
			factor = factor + 1;

		}
		factor = suma % 11;
		factor = 11 - factor;

		if (factor == 11)
			factor = 0;

		if (factor == 10)
			factor = 1;

		claveGenerada = claveGenerada + factor;

		return claveGenerada;

	}

	public static String enviarRecepcion(String pathDocumento, String urlServicio) {
		if (pathDocumento == null) {
			return "defina el path del documento a firmar";
		}

		if (urlServicio == null) {
			return "defina el url del servcio";
		}

		// recuperar archivo firmado
		byte[] bytes;
		String archivoCodificado;
		try {
			bytes = Archivo.recuperaEnBytes(pathDocumento);
			archivoCodificado = Base64.getEncoder().encodeToString(bytes);
		} catch (Exception e) {
			return "Error al recuperar el archivo:" + pathDocumento + "," + e.getMessage();
		}
		// armo la peticion
		List<String> textosPeticion = new ArrayList<>();
		textosPeticion.add(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ec=\"http://ec.gob.sri.ws.recepcion\">"
						+ "<soapenv:Header/><soapenv:Body><ec:validarComprobante><xml>");
		textosPeticion.add(archivoCodificado);
		textosPeticion.add("</xml></ec:validarComprobante></soapenv:Body></soapenv:Envelope>");

		// enviar al servicio
		ServicioWebResponse servicioWebResponse = ServicioWeb.peticionSri(urlServicio, textosPeticion);
		if (servicioWebResponse.getError()) {
			System.err.println("Error en el envio a recepcion:" + pathDocumento);
			System.err.println(servicioWebResponse.getErrores());
			throw new RuntimeException("Error en el envio a recepcion:" + servicioWebResponse.getErrores());
		}
		Document document = XmlProcesa.DocumentDesdeString(servicioWebResponse.getRespuesta());
		Element element = XmlProcesa.getElementPorTagName(document, "RespuestaRecepcionComprobante");
		Element estado = XmlProcesa.getElementPorTagName(element, "estado");
		String estadoStr = estado.getTextContent();
		System.out.println("Estado del envio a recepcion SRI:" + estadoStr);

		if (ESTADO_RECEPCION_OK.compareTo(estadoStr) == 0) {
			return estadoStr;
		} else {
			Element mensajes = XmlProcesa.getElementPorTagName(element, "mensajes");
			Element mensaje = (Element) mensajes.getElementsByTagName("mensaje").item(0);
			System.out.println(XmlProcesa.getElementPorTagName(mensaje, "identificador").getTextContent());
			System.out.println(XmlProcesa.getElementPorTagName(mensaje, "mensaje").getTextContent());
			String informacionAdicional = "";
			try {
				informacionAdicional = XmlProcesa.getElementPorTagName(mensaje, "informacionAdicional")
						.getTextContent();
			} catch (Exception e) {
				informacionAdicional = "";
			}
			return estadoStr + ":" + XmlProcesa.getElementPorTagName(mensaje, "mensaje").getTextContent() + " => "
					+ informacionAdicional;
		}
	}

	public static String verificaAutorizacion(String claveAcceso, String urlServicio) {
		// armo la peticion
		List<String> textosPeticion = new ArrayList<>();
		textosPeticion.add(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ec=\"http://ec.gob.sri.ws.autorizacion\">"
						+ "<soapenv:Header/><soapenv:Body><ec:autorizacionComprobante><claveAccesoComprobante>"
						+ claveAcceso
						+ "</claveAccesoComprobante></ec:autorizacionComprobante></soapenv:Body></soapenv:Envelope>");

		// enviar al servicio
		ServicioWebResponse servicioWebResponse = ServicioWeb.peticionSri(urlServicio, textosPeticion);
		if (!servicioWebResponse.getError()) {
			try {
				Document document = XmlProcesa.DocumentDesdeString(servicioWebResponse.getRespuesta());

				Element autorizacion = XmlProcesa.getElementPorTagName(document, "autorizacion");
				if (autorizacion == null) {
					System.err.println("No se ha recuperado una autorizacion para la clave");
					return null;
				}
				Element estado = XmlProcesa.getElementPorTagName(autorizacion, "estado");
				Element fechaAutorizacion = XmlProcesa.getElementPorTagName(autorizacion, "fechaAutorizacion");
				Element mensaje = XmlProcesa.getElementPorTagName(autorizacion, "mensaje");
				System.out.println("Estado de la verificacion de la autorizacion SRI:" + estado.getTextContent());

				if (ESTADO_AUTORIZACION_OK.compareTo(estado.getTextContent()) == 0) {
					System.out.println("fechaAutorizacion:" + fechaAutorizacion.getTextContent());
					return ESTADO_AUTORIZACION_OK + ":" + fechaAutorizacion.getTextContent();
				} else {
					System.out.println("tipo:" + XmlProcesa.getElementPorTagName(mensaje, "tipo").getTextContent());
					System.out.println("mensaje:" //
							+ XmlProcesa.getElementPorTagName(mensaje, "mensaje").getTextContent());
					System.out.println("informacionAdicional:"
							+ XmlProcesa.getElementPorTagName(mensaje, "informacionAdicional").getTextContent());
					return XmlProcesa.getElementPorTagName(mensaje, "tipo").getTextContent() + ":"
							+ XmlProcesa.getElementPorTagName(mensaje, "mensaje").getTextContent() + " => "
							+ XmlProcesa.getElementPorTagName(mensaje, "informacionAdicional").getTextContent();
				}

			} catch (Exception e) {
				System.err.println("Error al procesar verificaAutorizacion:" + e.getMessage());
			}
		}
		return null;
	}

	public static HashMap<String, Object> procesar(String estadoActual, String claveAcceso, String pathArchivoFirmado,
			boolean isProduccion) {
		String urlAutorizacion = SriService.URL_AUTORIZACION_PRUEBAS;
		String urlRecepcion = SriService.URL_RECEPCION_PRUEBAS;
		if (isProduccion) {
			System.out.println("**procesa en produccion**");
			urlAutorizacion = SriService.URL_AUTORIZACION_PRODUCCION;
			urlRecepcion = SriService.URL_RECEPCION_PRODUCCION;
		}

		String resultado = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String fechaActual = dateFormat.format(new Date());

		HashMap<String, Object> respuesta = new HashMap<>();

		if (estadoActual != null && SriService.ESTADO_RECEPCION_OK.equalsIgnoreCase(estadoActual)) {
			resultado = SriService.verificaAutorizacion(claveAcceso, urlAutorizacion);
			System.out.println("estado autoriza:" + resultado);
			if (resultado != null) {
				if (resultado.startsWith(SriService.ESTADO_AUTORIZACION_OK + ":")) {
					respuesta.put(ESTADO_KEY, SriService.ESTADO_AUTORIZACION_OK);
					respuesta.put("nota", resultado + " <> " + fechaActual);
					int fechaInicia = resultado.indexOf(":") + 1;
					String fechaString = resultado.substring(fechaInicia, fechaInicia + 19);
					Date fecha;
					try {
						fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fechaString.replace('T', ' '));
					} catch (ParseException e) {
						throw new RuntimeException("Error al recuperar la fecha de autorizacion");
					}
					respuesta.put("fecha", fecha);
				} else {
					respuesta.put(ESTADO_KEY, SriService.ESTADO_AUTORIZACION_OK + "=>ERROR" + " <> " + fechaActual);
					respuesta.put("nota", resultado.length() < 250 ? resultado : resultado.substring(0, 250));
				}
				return respuesta;
			}
		}

		if (estadoActual == null || estadoActual.trim().length() < 2) {
			if (pathArchivoFirmado == null || pathArchivoFirmado.trim().length() < 5) {
				throw new RuntimeException("Nombre de archivo no valido");
			}
			resultado = SriService.enviarRecepcion(pathArchivoFirmado, urlRecepcion);
			System.out.println("estado envio:" + resultado);
			if (SriService.ESTADO_RECEPCION_OK.equalsIgnoreCase(resultado)) {
				respuesta.put(ESTADO_KEY, SriService.ESTADO_RECEPCION_OK);
				respuesta.put("nota", SriService.ESTADO_RECEPCION_OK + " <> " + fechaActual);
			} else {
				respuesta.put(ESTADO_KEY, SriService.ESTADO_RECEPCION_OK + "=>ERROR" + " <> " + fechaActual);
				respuesta.put("nota", resultado.length() < 250 ? resultado : resultado.substring(0, 250));
			}
			return respuesta;
		}
		return null;

	}

}

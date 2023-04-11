package ec.sgm.fac.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import ec.sgm.SigmaException;
import ec.sgm.ce.modelo.Email;
import ec.sgm.ce.util.BundleApp;
import ec.sgm.core.Constantes;
import ec.sgm.fac.entity.Factura;
import ec.sgm.fac.entity.Persona;
import ec.sgm.fac.entity.Retencion;
import ec.sgm.org.service.ParametroService;

@Component
public class NotificaDocumentos {
	private static final Logger LOGGER = LogManager.getLogger(NotificaDocumentos.class);
	@Autowired
	private FacturaPDFService archivoFacturaNota;
	@Autowired
	private ParametroService serviceParametros;
	@Autowired
	private RetencionPDFService archivoRetencion;

	private final String urlEnviarEmail = BundleApp.getString(BundleApp.URL_ENVIAR_EMAIL) + "/correo/envia";

	/**
	 * Genera datos para enviar los valores de retenciones
	 * 
	 * @param retencion
	 * @throws SigmaException
	 */
	public void notificaEmailDocRetenciones(Retencion retencion) throws SigmaException {
		try {
			Factura factura = retencion.getDetalles().get(0).getFactura();
			Persona persona = factura.getPersona();
			String organizacionCod = factura.getOrganizacion().getOrganizacionCod();
			if (persona.getEmail() == null || persona.getEmail() == "") {
				LOGGER.error("Email no registrado para: " + persona.getNombre());
				throw new SigmaException("Email no registrado para: " + persona.getNombre());
			}
			String nombreComercial = serviceParametros.findValorByClave("sriNombreComercial", organizacionCod);

			String asunto = "Documentos electronicos de " + nombreComercial;
			String contenido = "Estimado(a) <b> " + persona.getNombre() + "</b>"
					+ "<br> Usted ha recibido su documento electrónico a través de este medio."
					+ "<br> Adjunto sírvase encontrar el detalle de su documento. <br> Atentamente, <br> "
					+ nombreComercial + "<br> POR FAVOR NO RESPONDER A ESTE MENSAJE";
			// Archivos
			List<String> archivos = new ArrayList<String>();
			String sriCeDocPath = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			archivoRetencion.generarPdfRetencion(retencion.getRetencionId());
			String archivoPdf = sriCeDocPath + "retencion_" + retencion.getRetencionId() + ".pdf";
			archivos.add(archivoPdf);
			String archivoXML = sriCeDocPath + retencion.getAutorizacionNumero() + ".xml";
			archivos.add(archivoXML);
			// Datos a enviar
			Email email = new Email();
			email.setPara(persona.getEmail());
			email.setAsunto(asunto);
			email.setContenido(contenido);
			email.setArchivos(archivos);
			System.out.println("Data -> " + email);
			// Notifica por servicio
			post(new Gson().toJson(email), urlEnviarEmail);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en generar datos para notificar retencion", e);
		}
	}

	/**
	 * Genera datos para enviar las notas y/o facturas
	 * 
	 * @param data
	 * @throws SigmaException
	 */
	public void notificaEmailDocumentosFac(Factura data) throws SigmaException {
		try {
			String organizacionCod = data.getOrganizacion().getOrganizacionCod();
			Persona persona = data.getPersona();
			if (persona.getEmail() == null || persona.getEmail().trim() == "") {
				LOGGER.error("Email no registrado para: " + persona.getNombre());
				throw new SigmaException("Email no registrado para: " + persona.getNombre());
			}
			String nombreComercial = serviceParametros.findValorByClave("sriNombreComercial", organizacionCod);

			String asunto = "Documentos electronicos de " + nombreComercial;
			String contenido = "Estimado(a) <b> " + persona.getNombre() + "</b>"
					+ "<br> Usted ha recibido su documento electrónico a través de este medio."
					+ "<br> Documento numero: <b>" + data.getDocumentoNumero() + "</b>"
					+ "<br> Adjunto sírvase encontrar el detalle de su documento. <br> Atentamente, <br> "
					+ nombreComercial + "<br> POR FAVOR NO RESPONDER A ESTE MENSAJE";

			// Parametros para archivos
			List<String> archivos = new ArrayList<String>();

			String pathArchivos = serviceParametros.findValorByClave("sriCeDocPath", organizacionCod);
			String nombre = "";
			switch (data.getOrigen().getCategoriaCod()) {
			case Constantes.ORIGEN_COMPRA_FACTURA:
			case Constantes.ORIGEN_VENTA_FACTURA:
				nombre = "factura_";
				break;
			case Constantes.ORIGEN_COMPRA_NC:
			case Constantes.ORIGEN_VENTA_NC:
				nombre = "nota_";
				break;
			}

			archivoFacturaNota.generarFacturaPDF(data.getDocumentoId());
			String archivoFactura = pathArchivos + nombre + data.getDocumentoNumero() + ".pdf";
			archivos.add(archivoFactura);
			String archivoXML = pathArchivos + data.getAutorizacionNumero() + ".xml";
			archivos.add(archivoXML);
			// Datos a enviar
			Email email = new Email();
			email.setPara(persona.getEmail());
			email.setAsunto(asunto);
			email.setContenido(contenido);
			email.setArchivos(archivos);
			System.out.println("Data -> " + email);
			// Notifica por servicio
			post(new Gson().toJson(email), urlEnviarEmail);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en generar datos para notificar documento", e);
		}

	}

	/**
	 * POST
	 * 
	 * @param data
	 * @param url
	 * @throws SigmaException
	 */
	public void post(String data, String url) throws SigmaException {
		try {
			System.out.println("\n\nURI POST: " + url);

			StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);

			HttpResponse response = httpClient.execute(request);

			System.out.println("Estatus POST: " + response.getStatusLine().getStatusCode() + ".\n\n");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error en consumir servicio", e);
		}

	}

}

package ec.sgm.ce.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ServicioWeb {
	public static ServicioWebResponse peticionSri(String urlServicio, List<String> textosPeticion) {
		ServicioWebResponse servicioWebResponse = new ServicioWebResponse();
		try {
			URL url = new URL(urlServicio);
			HttpURLConnection connection = null;
			if (url.openConnection() instanceof HttpURLConnection) {
				connection = (HttpURLConnection) url.openConnection();
			}
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "text/html");
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			for (String textoPeticion : textosPeticion)
				wr.writeBytes(textoPeticion);
			wr.flush();
			wr.close();

			servicioWebResponse.setCodigo(connection.getResponseCode());
			if (servicioWebResponse.getCodigo() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine = in.readLine();
				Integer tamanioBuffer = 10;
				StringBuffer response = new StringBuffer(tamanioBuffer);
				while (inputLine != null) {
					response.append(inputLine);
					inputLine = in.readLine();
				}
				in.close();
				servicioWebResponse.setRespuesta(response.toString());
			} else {
				System.err.println("codigo de Respuesta de la peticion al SRI:" + connection.getResponseCode());
				servicioWebResponse
						.agregaError("codigo de respuesta:" + connection.getResponseCode() + "desde el servicio");
			}
		} catch (Exception e) {
			System.err.println("error en la peticion al SRI:" + urlServicio);
			for (String textoPeticion : textosPeticion) {
				System.err.println("textosPeticion:" + textoPeticion);
			}

			System.err.println("error en la peticion al SRI:" + e.getMessage());
			servicioWebResponse.agregaError("Exception:" + e.getMessage());
		}
		return servicioWebResponse;
	}
}

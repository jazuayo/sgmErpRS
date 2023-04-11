package ec.sgm.ce.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Archivo {
	public static byte[] recuperaEnBytes(String pathArchivo) throws IOException {
		InputStream inputStream = new FileInputStream(pathArchivo);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final int SIZE = 1024;
		byte[] bites = new byte[SIZE];
		int longitud;
		while ((longitud = inputStream.read(bites, 0, SIZE)) != -1)
			byteArrayOutputStream.write(bites, 0, longitud);
		bites = byteArrayOutputStream.toByteArray();
		inputStream.close();
		return bites;
	}

}

package ec.sgm.ce.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BundleBase {
	private static final Logger LOGGER = LogManager.getLogger(BundleBase.class);

	private static ResourceBundle bundle = null;
	public static String ERROR_BUNDLE = "error_BUNDLE_no_ENCONTRADO";
	public static String ERROR_KEY = "error_KEY_no_ENCONTRADO";
	public static String PING = "ping";

	public static void cargaBundle() {
		try {
			File file = new File("/opt/sgm/config");
			URL[] urls = { file.toURI().toURL() };
			ClassLoader loader = new URLClassLoader(urls);
			bundle = ResourceBundle.getBundle("sgmJEE", Locale.getDefault(), loader);
		} catch (Exception ex) {
			bundle = null;
			LOGGER.error("BundleBase.cargaBundle:Locale.getDefault():" + Locale.getDefault());
			LOGGER.error("BundleBase.cargaBundle:" + ex.getMessage());
		}
	}

	public static boolean tieneError(String valor) {
		return ((valor.compareTo(ERROR_BUNDLE) == 0) || (valor.compareTo(ERROR_KEY) == 0));
	}

	public static String getString(String key) {
		if (bundle == null) {
			cargaBundle();
		}
		if (bundle == null) {   
			return ERROR_BUNDLE;
		}
		String text;
		try {
			text = bundle.getString(key);
		} catch (Exception ex) {
			text = ERROR_KEY;
			LOGGER.error("BundleBase.getString: Error al recuperar la clave:" + key);
			LOGGER.error("BundleBase.getString: " + ex.getMessage());
		}
		return text;
	}

}

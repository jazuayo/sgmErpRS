package ec.sgm.core;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author Marco
 */
public class Numero {

	public static String aString(Double valor, Integer decimales) {
		if (valor == null)
			return null;

		String fmtDecimales = "";
		for (int d = 0; d < decimales; d++)
			fmtDecimales = fmtDecimales + "0";
		String fmtNumero = "0";
		if (fmtDecimales.compareTo("") != 0)
			fmtNumero = "0." + fmtDecimales;

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');

		DecimalFormat decimalFormat = new DecimalFormat(fmtNumero, decimalFormatSymbols);
		String numeroString = decimalFormat.format(valor);
		return numeroString;
	}

	public static Double redondear(Double valor) {
		if (valor == null)
			return null;
		return Math.round(valor * 100.0) / 100.0;
	}

}

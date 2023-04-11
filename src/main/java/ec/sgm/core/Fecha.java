package ec.sgm.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.sgm.SigmaException;

public class Fecha {
	private static final Logger LOGGER = LogManager.getLogger(Fecha.class);

	public static String anio(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		return format.format(fecha);
	}

	public static String periodoRetencion(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
		return format.format(fecha);
	}

	public static String formatoXML(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		return format.format(fecha);
	}

	public static Date stringToDate(String fecha) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.parse(fecha);
	}

	public static String formatoReportes(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(fecha);
	}

	public static String formatoReportesFechaHora(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return format.format(fecha);
	}

	public static String formatoFechaGuionSeparado(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		return format.format(fecha);
	}

	public static Calendar calendarDesdeDate(Date fecha) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		return calendar;
	}

	public static int getYear(Date fecha) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		return calendar.get(Calendar.YEAR);
	}

	public static int getMonth(Date fecha) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		return calendar.get(Calendar.MONTH) + 1;
	}

	public static String getMonthMM(Date fecha) {
		String mes = "0".concat(Integer.toString(getMonth(fecha)));
		return mes.substring(mes.length() - 2);
	}

	public static Date dateDesdeFront(String fecha) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(fecha);
	}

	public static Date fechaIniciaMes(Date fecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	public static String stringDesdeDateExcel(Date valor) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return simpleDateFormat.format(valor).trim();
	}

	public static String sqlOracleFechaHora(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return "TO_DATE('" + format.format(fecha) + "','dd/mm/yyyy hh24:mi:ss')";
	}

	public static String fechaLetras(Date fecha) throws SigmaException {
		try {
			List<String> mes = Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto",
					"Septiembre", "Octubre", "Noviembre", "Diciembre");
			List<String> diaSemana = Arrays.asList("Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes",
					"Sabado");
			Calendar calendario = Calendar.getInstance();
			calendario.setTime(fecha);
			// Mes - dia letras
			String diaLetras = diaSemana.get(calendario.get(Calendar.DAY_OF_WEEK) - 1);
			String mesLetras = mes.get(calendario.get(Calendar.MONTH));
			// respuesta
			String respuesta = diaLetras + " " + calendario.get(Calendar.DAY_OF_MONTH) + " de " + mesLetras + " del "
					+ calendario.get(Calendar.YEAR);
			return respuesta;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SigmaException("Error al pasar fecha a letras", e);
		}

	}

}

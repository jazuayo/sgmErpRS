package ec.sgm.cta.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.cta.entity.Periodo;
import ec.sgm.cta.repository.PeriodoRepository;

@Service
public class FechaDesdeHastaService {
	private static final Logger LOGGER = LogManager.getLogger(FechaDesdeHastaService.class);
	@Autowired
	private PeriodoRepository repositoryPeriodo;

	/**
	 * CT -- Define la fecha inicial y final para la generacion de reportes
	 * 
	 * @param iv_tipo_reporte
	 * @param iv_organizacion_cod
	 * @param id_fecha_inicialAux
	 * @param id_fecha_finalAux
	 * @return
	 * @throws SigmaException
	 * @throws ParseException
	 */
	public HashMap<String, String> pcta_rep_fecha_desde_hasta(String iv_organizacion_cod, String id_fecha_inicialAux,
			String id_fecha_finalAux) throws SigmaException, ParseException {

		// valores de salida
		HashMap<String, String> fechaDesdeHasta = new HashMap<String, String>();

		// Begin
		if (iv_organizacion_cod == null || iv_organizacion_cod == "") {
			LOGGER.error("Defina la organizacion a mayorizar.");
			throw new SigmaException("Defina la organizacion a mayorizar.");
		}
		// Defina la id_fecha_final si es null entonces la fecha del sistema
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaActual = new Date();
		Date id_fecha_final = formato.parse(formato.format(fechaActual).toString());
		if (id_fecha_finalAux != null && id_fecha_finalAux != "") {
			id_fecha_final = formato.parse(id_fecha_finalAux);
		}
		Date id_fecha_inicial = null;
		if (id_fecha_inicialAux != null && id_fecha_inicialAux != "") {
			id_fecha_inicial = formato.parse(id_fecha_inicialAux);
		}
		// Recupera periodos
		List<Periodo> periodos = repositoryPeriodo.recuperarPeriodoEntreFechas(id_fecha_final);
		if (periodos.size() == 0) {
			LOGGER.error("No se ha encontrado periodo");
			throw new SigmaException("No se ha encontrado periodo");
		}
		// Valido los valores de fechas
		Date ld_periodo_fecha_inicial = null;
		Date ld_periodo_fecha_final = null;
		for (int i = 0; i <= periodos.size() - 1; i++) {
			Periodo periodo = periodos.get(i);
			ld_periodo_fecha_inicial = periodo.getFechaDesde();
			ld_periodo_fecha_final = periodo.getFechaHasta();
			Integer ln_periodo_cod = periodo.getPeriodoCod();
			if (ld_periodo_fecha_inicial == null) {
				LOGGER.error("defina la fecha inicial del periodo: " + ln_periodo_cod);
				throw new SigmaException("defina la fecha inicial del periodo: " + ln_periodo_cod);
			}
			if (ld_periodo_fecha_final == null) {
				LOGGER.error("defina la fecha final del periodo: " + ln_periodo_cod);
				throw new SigmaException("defina la fecha final del periodo: " + ln_periodo_cod);
			}
		}
		// Recupero el rango de fechas del periodo y filtra por la fechas del periodo
		ld_periodo_fecha_inicial = periodos.get(0).getFechaDesde();
		ld_periodo_fecha_final = periodos.get(0).getFechaHasta();
		for (int i = 0; i <= periodos.size() - 1; i++) {
			Periodo periodo = periodos.get(i);
			if (periodo.getFechaDesde().before(ld_periodo_fecha_inicial)) {
				ld_periodo_fecha_inicial = periodo.getFechaDesde();
			}
			if (periodo.getFechaHasta().after(ld_periodo_fecha_final)) {
				ld_periodo_fecha_final = periodo.getFechaHasta();
			}
		}

		Date od_fecha_inicial = ld_periodo_fecha_inicial;
		Date od_fecha_final = ld_periodo_fecha_final;
		// --si no se especifica fechas para los calculos, tomo las del perido contable
		if (id_fecha_inicial != null) {
			if (id_fecha_inicial.before(ld_periodo_fecha_inicial)) {
				String fecha_inicial_periodo = new SimpleDateFormat("dd/MM/yyyy").format(ld_periodo_fecha_inicial)
						.toString();
				LOGGER.error("La fecha inicial no puede ser menor a la del periodo " + fecha_inicial_periodo);
				throw new SigmaException(
						"La fecha inicial no puede ser menor a la del periodo " + fecha_inicial_periodo);
			} else {
				od_fecha_inicial = id_fecha_inicial;
			}
		}
		if (id_fecha_final != null) {
			if (id_fecha_final.after(ld_periodo_fecha_final)) {
				String fecha_final_periodo = new SimpleDateFormat("dd/MM/yyyy").format(ld_periodo_fecha_final)
						.toString();
				LOGGER.error("La fecha final no puede ser mayor a la del periodo " + fecha_final_periodo);
				throw new SigmaException("La fecha final no puede ser mayor a la del periodo " + fecha_final_periodo);
			} else {
				od_fecha_final = id_fecha_final;
			}
		}
		// --valido la consistencia de las fechas
		if (od_fecha_inicial.after(od_fecha_final)) {
			LOGGER.error("La fecha inicial no puede ser mayor a la final ");
			throw new SigmaException("La fecha inicial no puede ser mayor a la final ");
		}
		// Respuesta
		String od_inicial = new SimpleDateFormat("dd/MM/yyyy").format(od_fecha_inicial).toString();
		String od_final = new SimpleDateFormat("dd/MM/yyyy").format(od_fecha_final).toString();
		fechaDesdeHasta.put("fechaInicial", od_inicial);
		fechaDesdeHasta.put("fechaFinal", od_final);
		return fechaDesdeHasta;

	}

}

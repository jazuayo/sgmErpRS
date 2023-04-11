package ec.sgm;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.stereotype.Service;

@Service
public class SigmaDateMidnight {
	private static final Logger LOGGER = LogManager.getLogger(SigmaDateMidnight.class);

	/**
	 * Fecha a con horas en cero para cualquier zona horaria
	 * 
	 * @param date
	 * @return
	 * @throws SigmaException
	 */
	public Date DateMidnight(Date date) throws SigmaException {
		try {
			Instant inst = date.toInstant();
			LocalDate localDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();
			Instant dayInst = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
			return Date.from(dayInst);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return date;
		}
	}
}

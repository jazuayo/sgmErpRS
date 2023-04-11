package ec.sgm.cta.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.SigmaException;
import ec.sgm.core.Constantes;
import ec.sgm.core.Fecha;
import ec.sgm.cta.entity.Comprobante;
import ec.sgm.cta.entity.ComprobanteCuenta;
import ec.sgm.cta.repository.ComprobanteRepository;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.repository.DocumentoRepository;
import ec.sgm.org.repository.EstadoRepository;
import ec.sgm.org.service.DocumentoService;

@Service
public class ComprobanteService {
	private static final Logger LOGGER = LogManager.getLogger(ComprobanteService.class);

	@Autowired
	private ComprobanteRepository comprobanteRepository;
	@Autowired
	private DocumentoRepository documentoRepository;
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private DocumentoService documentoService;

	@Transactional
	public Comprobante grabar(Comprobante comprobante, String documentoCod, String comprobanteCod)
			throws SigmaException {
		if (documentoCod != null) {
			Documento documento = documentoRepository.findById(documentoCod).orElse(null);
			if (documento == null) {
				throw new SigmaException("No se ha encontrado el documento:" + documentoCod);
			}
			comprobante.setEstado(estadoRepository.findByEstadoCod(Constantes.ESTADO_INGRESANDO));

			comprobante.setDocumento(documento);
			if (comprobanteCod == null) {
				comprobante.setComprobanteCod(comprobante.getOrganizacion().getOrganizacionCod() + "_"
						+ documentoService.recuperaIncrementaSecuencia(documento.getDocumentoCod(), true));
			} else {
				comprobante.setComprobanteCod(comprobanteCod);
			}
		}
		comprobante.setPeriodoCod("" + Fecha.getYear(comprobante.getFecha()));
		if (comprobante.getUsuario() == null) {
			comprobante.setUsuario("PRC_AUT");
		}
		int linea = 5001;
		for (ComprobanteCuenta cuenta : comprobante.getDetalles()) {
			cuenta.setComprobante(comprobante);
			if (cuenta.getLinea() == null || cuenta.getLinea() == 0) {
				cuenta.setLinea(linea++);
			}
			if (cuenta.getConcepto() == null) {
				cuenta.setConcepto(comprobante.getConcepto());
			}
		}
		comprobanteRepository.save(comprobante);
		return comprobante;
	}

	public Comprobante findByfuente(String fuente, String organizacionCod) throws SigmaException {
		List<Comprobante> comprobantes = comprobanteRepository.findByfuente(fuente, organizacionCod);
		for (Comprobante comprobante : comprobantes) {
			return comprobante;
		}
		return null;
	}

	public Comprobante delete(String comprobanteCod) throws SigmaException {
		Comprobante comprobante = comprobanteRepository.findById(comprobanteCod).orElse(null);
		if (comprobante == null) {
			LOGGER.error("No se ha encontrado el comprobante:" + comprobanteCod);
			throw new SigmaException("No se ha encontrado el comprobante:" + comprobanteCod);
		}
		comprobanteRepository.delete(comprobante);
		return comprobante;
	}
}

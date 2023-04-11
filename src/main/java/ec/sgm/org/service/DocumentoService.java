package ec.sgm.org.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.sgm.core.Texto;
import ec.sgm.org.entity.Documento;
import ec.sgm.org.entity.DocumentoSerie;
import ec.sgm.org.repository.DocumentoRepository;
import ec.sgm.org.repository.DocumentoSerieRepository;

@Service
public class DocumentoService {

	@Autowired
	private DocumentoRepository documentoRepository;
	@Autowired
	private DocumentoSerieRepository documentoSerieRepository;

	private String generaSecuencia(Documento documento) {
		if (documento == null) {
			return null;
		}
		String secuencial = documento.getSecuencial().toString();
		int longitudInicial = documento.getInicio().length();
		int longitudRestante = documento.getLongitud().intValue() - longitudInicial;
		if (longitudRestante > secuencial.length()) {
			return documento.getInicio() + Texto.lpad(secuencial, longitudRestante, "0");
		} else {
			return documento.getInicio() + secuencial;
		}
	}

	public String recuperaIncrementaSecuencia(String documentoCod, boolean incrementa) {
		Documento documento = documentoRepository.findById(documentoCod).orElse(null);
		if (documento == null) {
			return null;
		}
		String secuencial = generaSecuencia(documento);
		if (incrementa) {
			documento.setSecuencial(documento.getSecuencial() + 1);
			System.out.println("\n\n" + documento.getSecuencial());
			documentoRepository.save(documento);
		}
		return secuencial;
	}

	public String recuperaIncrementaSecuencia(String documentoCod) {
		return recuperaIncrementaSecuencia(documentoCod, true);
	}

	public String recuperaSecuencia(String documentoCod) {
		return recuperaIncrementaSecuencia(documentoCod, false);
	}

	public DocumentoSerie recuperaSerie(String documentoCod, Date fechaDocumento) {
		DocumentoSerie documentoSerie = null;
		if (fechaDocumento != null) {
			List<DocumentoSerie> series = documentoSerieRepository.findByDocumentoCodAndFecha(documentoCod,
					fechaDocumento);
			for (DocumentoSerie serie : series) {
				documentoSerie = serie;
			}
		}
		return documentoSerie;
	}
}

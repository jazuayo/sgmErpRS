package ec.sgm.cta.modelo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ec.sgm.cta.entity.ComprobanteCuenta;
import lombok.Data;

@Data
public class ComprobanteReq {
	private String comprobanteCod;
	private Date fecha;
	private String concepto;
	private String fuente;
	private String deudorBeneficiario;
	private boolean esAutomatico;
	private String usuario;
	private BigInteger chequeNumero;
	private List<ComprobanteCuenta> detalles = new ArrayList<ComprobanteCuenta>();
	private String documentoCod;
	private String organizacionCod;
	private Integer compAutCabcod;
	private String estadoCod;
}

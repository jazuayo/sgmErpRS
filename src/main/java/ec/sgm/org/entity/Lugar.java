package ec.sgm.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ORG_LUGAR")
@Data
public class Lugar {
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "ORG_LUGARESQ", name = "ORG_LUGARESQ")
	@GeneratedValue(generator = "ORG_LUGARESQ", strategy = GenerationType.SEQUENCE)
	private Long lugarId;
	@Column(name = "nombre")
	private String nombre;
	@JoinColumn(name = "lugar_padre_id", referencedColumnName = "LUGARID")
	@ManyToOne(optional = true)
	private Lugar lugar;
}

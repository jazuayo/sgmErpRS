package ec.sgm.org.entity;

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
@Table(name = "ORG_CATEGORIA_ORGANIZACION")
@Data
public class CategoriaOrganizacion {
	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "ORG_CATEGORIA_ORGANIZACIONESQ", name = "ORG_CATEGORIA_ORGANIZACIONESQ")
	@GeneratedValue(generator = "ORG_CATEGORIA_ORGANIZACIONESQ", strategy = GenerationType.SEQUENCE)
	private Long catOrgId;
	@JoinColumn(name = "ORGANIZACION_COD", referencedColumnName = "ORGANIZACIONCOD")
	@ManyToOne(optional = true)
	private Organizacion organizacion;
	@JoinColumn(name = "CATEGORIA_COD", referencedColumnName = "CATEGORIACOD")
	@ManyToOne(optional = true)
	private Categoria categoria;

}

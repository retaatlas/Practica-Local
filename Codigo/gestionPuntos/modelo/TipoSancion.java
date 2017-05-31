package gestionPuntos.modelo;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@NamedQueries({
	//@NamedQuery(name="TipoSancion.PorDescripcion",query="SELECT ts FROM Tipo_sancion ts WHERE ( (ts.descripcion= :clave_candidata) )")
		
})
@Entity
@Table(name="TIPO_SANCION")
public class TipoSancion {

	@Id
	@Column(nullable=false,name="ID_TIPO_SANCION")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorTipoSancion")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorTipoSancion", sequenceName = "sequence")
	protected Integer id_tipo_sancion;
	@Column(nullable=false,length=255,name="DESCRIPCION")
	protected String descripcion;
	@Column(nullable=false,name="DIAS_SANCION")
	protected Integer dias_sancion;
	
	@OneToMany(mappedBy="tipo_sancion", cascade=CascadeType.PERSIST)
	protected List<Sancion> sanciones;
	
	public TipoSancion(){
		
	}

	public Integer getId() {
		return id_tipo_sancion;
	}

	public void setId(Integer id) {
		this.id_tipo_sancion = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getDias_sancion() {
		return dias_sancion;
	}

	public void setDias_sancion(Integer dias_sancion) {
		this.dias_sancion = dias_sancion;
	}

	public List<Sancion> getSanciones() {
		return sanciones;
	}

	public void setSanciones(List<Sancion> sanciones) {
		this.sanciones = sanciones;
	}
}

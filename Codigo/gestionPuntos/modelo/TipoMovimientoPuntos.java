package gestionPuntos.modelo;

import java.util.ArrayList;
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

import org.json.simple.JSONObject;

import otros.JSONable;

@NamedQueries({
	@NamedQuery(name="TipoMovimientoPuntos.PorDescripcion",query="SELECT tmp FROM TipoMovimientoPuntos tmp WHERE tmp.descripcion LIKE :descripcion")
		
})
@Entity
@Table(name="TIPO_MOV_PUNTOS")
public class TipoMovimientoPuntos implements JSONable{
	
	@Id
	@Column(nullable=false,name="ID_TIPO_MOV_PUNTOS")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorTipoMovPuntos")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorTipoMovPuntos", sequenceName = "sequence")
	protected Integer id_tipo_mov_puntos;
	@Column(nullable=false,length=255,name="DESCRIPCION")
	protected String descripcion;
	
	@OneToMany(mappedBy="tipo_mov_puntos",cascade=CascadeType.PERSIST)
	protected List<MovimientoPuntos> movimientos= new ArrayList<MovimientoPuntos>();

	public Integer getId_tipo_mov_puntos() {
		return id_tipo_mov_puntos;
	}

	public void setId_tipo_mov_puntos(Integer id_tipo_mov_puntos) {
		this.id_tipo_mov_puntos = id_tipo_mov_puntos;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<MovimientoPuntos> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<MovimientoPuntos> movimientos) {
		this.movimientos = movimientos;
	}

	@Override
	public void SetJSONObject(JSONObject json) {		
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", this.id_tipo_mov_puntos);
		json.put("descripcion", this.descripcion);
		return json;
	}

	@Override
	public Object getPrimaryKey() {
		return null;
	}
}

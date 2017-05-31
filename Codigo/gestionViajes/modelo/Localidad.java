package gestionViajes.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import otros.JSONable;

@Entity
@Table(name="localidad")
@NamedQueries({
	@NamedQuery(name="Localidad.autocompletar",query="SELECT l FROM Localidad l WHERE l.nombre LIKE :busqueda AND l.clasificacion='P'")
})
public class Localidad implements JSONable {

	@Id
	protected Integer id_localidad;
	@Column(nullable=false)
	protected Double lat;
	@Column(nullable=false)
	protected Double lng;
	@Column(nullable=false,length=200)
	protected String nombre;
	@Column(nullable=true,length=200)
	protected String nombre_ascii;
	@Column(nullable=true)
	protected Character clasificacion;
	@Column(nullable=true)
	protected Integer admin1;
	
	public Localidad(){
		
	}
	
	public Integer getId() {
		return id_localidad;
	}

	public void setId(Integer id) {
		this.id_localidad = id;
	}

	public Double getLatitud() {
		return lat;
	}

	public void setLatitud(Double latitud) {
		this.lat = latitud;
	}

	public Double getLongitud() {
		return lng;
	}

	public void setLongitud(Double longitud) {
		this.lng = longitud;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombreAscii() {
		return nombre_ascii;
	}

	public void setNombreAscii(String nombre_ascii) {
		this.nombre_ascii = nombre_ascii;
	}

	public Character getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(Character clasificacion) {
		this.clasificacion = clasificacion;
	}

	@Override
	public void SetJSONObject(JSONObject json) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", this.getId());
		json.put("nombre", this.getNombre());
		json.put("lat", this.getLatitud());
		json.put("lng", this.getLongitud());
		return json;
	}

	@Override
	public Object getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNombreProvincia() {
		//Esto es un parche, en realidad deberia buscarse desde la base de datos.
		// select admin1, nombre from localidad where clase_punto='ADM1';
		switch (this.admin1) {
			case 1 : return "Buenos Aires";
			case 2 : return "Catamarca";
			case 3 : return "Chaco";
			case 4 : return "Chubut";
			case 5 : return "Cordoba";
			case 6 : return "Corrientes";
			case 7 : return "Ciudad Autonoma de Buenos Aires";
			case 8 : return "Entre Rios";
			case 9 : return "Formosa";
			case 10 : return "Jujuy";
			case 11 : return "La Pampa";
			case 12 : return "La Rioja";
			case 13 : return "Mendoza";
			case 14 : return "Misiones";
			case 15 : return "Neuquen";
			case 16 : return "Rio Negro";
			case 17 : return "Salta";
			case 18 : return "San Juan";
			case 19 : return "San Luis";
			case 20 : return "Santa Cruz";
			case 21 : return "Santa Fe";
			case 22 : return "Santiago del Estero";
			case 23 : return "Tierra del Fuego";
			case 24 : return "Tucuman";
			default: return "";
		}
	}
}

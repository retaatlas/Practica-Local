package modelojpa;

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

@NamedQueries({
	@NamedQuery(name="Permiso.todos",query="SELECT p FROM Permiso p"),
	@NamedQuery(name="Permiso.porNombre",query="SELECT p FROM Permiso p	WHERE p.nombre_permiso LIKE :nombre_permiso"),
	@NamedQuery(name="Permiso.SearchById",query="SELECT p FROM Permiso p WHERE p.id_permiso = :id"),
	@NamedQuery(name="Permiso.porFuncinalidad",query="SELECT p FROM Permiso p WHERE p.funcionalidad LIKE :funcionalidad"),
	@NamedQuery(name="Permiso.buscarPorClaveCandidata",query="SELECT p FROM Permiso p WHERE p.nombre_permiso = :clave_candidata"),
})
@Entity
@Table
public class Permiso implements JSONable {
	
	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorPermiso")
	@SequenceGenerator(allocationSize=1, schema="pruebajpa",  name="MySequenceGeneratorPermiso", sequenceName = "sequence")
	protected int id_permiso;
	@Column(nullable=false,length=50)
	protected String nombre_permiso;
	@Column(nullable=false,length=200)
	protected String funcionalidad;
	@Column(nullable=true)
	protected String descripcion;
	@Column(nullable=false,length=1)
	protected Character estado;
	@OneToMany(mappedBy="permiso",cascade=CascadeType.PERSIST)
	private List<PermisoRol> roles= new ArrayList<PermisoRol>();
	
	public Permiso(){
		this.id_permiso=2;
		this.nombre_permiso="primer permiso";
		this.funcionalidad="funcionalidad 1";
		this.descripcion="este es el 1er permiso q creo";
		this.estado='A';
	}
	
	public Permiso(JSONObject json){
		this.id_permiso = (int) json.get("id_permiso");
		this.nombre_permiso = (String) json.get("nombre_permiso");
		this.funcionalidad = (String) json.get("funcionalidad");
		this.descripcion = (String) json.get("descripcion");
		this.estado = json.get("estado").toString().charAt(0);
	}
	
	public List<PermisoRol> getRoles() {
		return roles;
	}

	public void setRoles(List<PermisoRol> roles) {
		this.roles = roles;
	}

	public int getId_permiso() {
		return id_permiso;
	}

	public void setId_permiso(int id_permiso) {
		this.id_permiso = id_permiso;
	}

	public String getNombre_permiso() {
		return nombre_permiso;
	}

	public void setNombre_permiso(String nombre_permiso) {
		this.nombre_permiso = nombre_permiso;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Character getEstado() {
		return estado;
	}

	public void setEstado(Character estado) {
		this.estado = estado;
	}

	public String getFuncionalidad() {
		return funcionalidad;
	}

	public void setFuncionalidad(String funcionalidad) {
		this.funcionalidad = funcionalidad;
	}
	
	@Override
	public String toString(){
		return "Permiso: ["+this.id_permiso+" , "+this.nombre_permiso+" , "+this.descripcion+" , "+this.funcionalidad+" ]";
	}
	
	@SuppressWarnings("unchecked")  //esta anotacion es para q no rompa las bolas con los warnings
	@Override
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		json.put("id_permiso", this.id_permiso);
		json.put("nombre_permiso", this.nombre_permiso);
		json.put("descripcion", this.descripcion);
		json.put("funcionalidad", this.funcionalidad);
		json.put("estado", this.estado.toString());
		return json;
	}
	
	@Override
	public void SetJSONObject(JSONObject json) {
		this.id_permiso = (int) json.get("id_permiso");
		this.nombre_permiso = (String) json.get("nombre_permiso");
		this.funcionalidad = (String) json.get("funcionalidad");
		this.descripcion = (String) json.get("descripcion");
		this.estado = json.get("estado").toString().charAt(0);
	}

	@Override
	public Object getPrimaryKey() {
		return this.id_permiso;
	}
	
}

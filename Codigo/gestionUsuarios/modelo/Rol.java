package gestionUsuarios.modelo;

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

import org.json.simple.JSONArray;


@Table(name="rol")

/*
 *			las querys que tengan atributos :algo, estos son parametros que se deben pasar al objeto query antes de ejecutarlo
 *			se pasan con  .setParameter("id_rol", 3);  "id_rol" es el nombre del campo, y el otro es un objeto que por el q se busca
 */

@NamedQueries({
	@NamedQuery(name="Rol.todos",query="SELECT r FROM Rol r"),
	@NamedQuery(name="Rol.porNombre",query="SELECT r FROM Rol r	WHERE r.nombre_rol = :nombre"),
	@NamedQuery(name="Rol.SearchById",query="SELECT r FROM Rol r WHERE r.id_rol = :id"),
	@NamedQuery(name="Rol.porNombreAmigable",query="SELECT r FROM Rol r WHERE r.nombre_amigable LIKE :nombre_amigable"),
	@NamedQuery(name="Rol.buscarPorClaveCandidata",query="SELECT r FROM Rol r WHERE r.nombre_rol = :clave_candidata")
	
})
@Entity
public class Rol implements JSONable {
	
	@Id
	@Column(nullable=false)
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorRol")
	@SequenceGenerator(allocationSize=1, schema="pruebajpa",  name="MySequenceGeneratorRol", sequenceName = "sequence")
	//@GeneratedValue(strategy = GenerationType.AUTO) //este no hacia que siga la regla del autoincrement de la BD
	protected Integer id_rol;
	@Column(nullable=false,length=30)
	protected String nombre_rol;
	@Column(nullable=false,length=30)
	protected String nombre_amigable;
	protected String descripcion;

	@Column(nullable=false,length=1)
	protected Character estado;
	@OneToMany(mappedBy="rol", cascade=CascadeType.PERSIST)
	private List<PermisoRol> permisos= new ArrayList<PermisoRol>();
	@OneToMany(mappedBy="rol", cascade=CascadeType.PERSIST)
	protected List<UsuarioRol> usuarios= new ArrayList<UsuarioRol>();
	
	
	public Rol(){
		
	}
	
	public Rol(JSONObject json){
		this.id_rol=Integer.parseInt(json.get("id_rol").toString());
		this.nombre_rol=(String)json.get("nombre_rol");
		this.nombre_amigable=(String)json.get("nombre_amigable");
		this.descripcion=(String)json.get("descripcion");
		this.estado=json.get("estado").toString().charAt(0);		
	}
	
	public void AsignarPermiso(Permiso p){
		//creo entidad para la tabla intermedia
		PermisoRol pr = new PermisoRol(p,this);
		permisos.add(pr);
	}
	
	public List<UsuarioRol> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<UsuarioRol> usuarios) {
		this.usuarios = usuarios;
	}
	
	public List<PermisoRol> getPermisos() {
		return permisos;
	}

	public void setPermisos(List<PermisoRol> permisos) {
		this.permisos = permisos;
	}

	public Integer getId_rol() {
		return id_rol;
	}

	public void setId_rol(Integer id_rol) {
		this.id_rol = id_rol;
	}

	public String getNombre_rol() {
		return nombre_rol;
	}

	public void setNombre_rol(String nombre_rol) {
		this.nombre_rol = nombre_rol;
	}

	public String getNombre_amigable() {
		return nombre_amigable;
	}

	public void setNombre_amigable(String nombre_amigable) {
		this.nombre_amigable = nombre_amigable;
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

	@Override
	  public String toString() {
	      return "Rol [id= " + this.id_rol + ", nombre=" + this.nombre_rol + ", nombre_amigable=" + this.nombre_amigable +
	    		  ", descr=" + this.descripcion + ", estado: "+this.estado+ "]";
	   }
	
	@SuppressWarnings("unchecked")  //esta anotacion es para q no rompa las bolas con los warnings
	@Override
	public JSONObject toJSON(){
		JSONArray idpermisos;

		JSONObject json = new JSONObject();
		json.put("id_rol", this.id_rol);
		json.put("nombre_rol", this.nombre_rol);
		json.put("nombre_amigable", this.nombre_amigable);
		json.put("descripcion", this.descripcion);
		json.put("estado", this.estado.toString());

		idpermisos = new JSONArray();
		List<PermisoRol> lista = this.getPermisos();
		for (PermisoRol permiso: lista){
			idpermisos.add(permiso.getPermiso().getId_permiso());
		}
		json.put("permisos", idpermisos);

		return json;
	}

	@Override
	public void SetJSONObject(JSONObject json) {
		this.nombre_rol=(String)json.get("nombre_rol");
		this.nombre_amigable=(String)json.get("nombre_amigable");
		this.descripcion=(String)json.get("descripcion");
		this.estado=json.get("estado").toString().charAt(0);
	}

	@Override
	public Object getPrimaryKey() {
		return this.id_rol;
	}
	
	
}

package modelojpa;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Entity
@Table(name="usuario")
@NamedQueries({
	@NamedQuery(name="Usuario.todos",query="SELECT u FROM Usuario u"),
	@NamedQuery(name="Usuario.porNombre",query="SELECT u FROM Usuario u	WHERE u.nombre_usuario LIKE :nombre"),
	@NamedQuery(name="Usuario.porNombreExacto",query="SELECT u FROM Usuario u	WHERE u.nombre_usuario = :nombre"),
	@NamedQuery(name="Usuario.SearchById",query="SELECT u FROM Usuario u WHERE u.id_usuario = :id"),
	@NamedQuery(name="Usuario.porEmail",query="SELECT u FROM Usuario u WHERE u.email = :email"),
	@NamedQuery(name="Usuario.porEstado",query="SELECT u FROM Usuario u WHERE u.estado = :estado"),
	@NamedQuery(name="Usuario.buscarPorClaveCandidata",query="SELECT u FROM Usuario u WHERE u.nombre_usuario = :clave_candidata")
	
})
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="Tipo", discriminatorType=DiscriminatorType.STRING,length=20)
@DiscriminatorValue("U")
public class Usuario implements JSONable {
	
	@Id
	@Column(nullable=false)
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorUsuario")
	@SequenceGenerator(allocationSize=1, schema="pruebajpa",  name="MySequenceGeneratorUsuario", sequenceName = "sequence")
	protected Integer id_usuario;
	
	@Column(nullable=false,length=30)
	protected String nombre_usuario;
	@Column(nullable=false,length=32)
	protected String password;
	@Column(nullable=false,length=40)
	protected String email;
	@Column(nullable=true)
	protected String descripcion;
	@Column(nullable=false,length=1)
	protected Character estado;
	
	@Column(nullable=false,length=1)
	protected Character tipo;			//necesaria para que la herencia de JPA ande. aca va el tipo de "hijo" que tiene las misma caracteristicas
	
	@OneToMany(mappedBy="usuario", cascade=CascadeType.PERSIST)
	protected List<UsuarioRol> roles;
	
	

	//@Column(nullable=false, name="id_persona")
	@ManyToOne
	@JoinColumn(name="id_persona")
	protected Persona persona;
	
	
	public Usuario(){
		this.descripcion="user lucas";
		this.email="l.mufato@gmail.com";
		this.estado='A';
		this.id_usuario=1;
		this.nombre_usuario="lucasmufato";
		this.password="masterkey";
		this.roles=new ArrayList<UsuarioRol>();
	}
	
	public Usuario (JSONObject json){
		this.id_usuario=(Integer) json.get("id_usuario");
		this.nombre_usuario= (String) json.get("nombre_usuario");
		this.password= (String) json.get("password");
		this.email= (String) json.get("email");
		this.descripcion= (String) json.get("descripcion");
		this.estado= json.get("estado").toString().charAt(0);
	}
	
	public void asignarRol(Rol r){
		UsuarioRol ur= new UsuarioRol(this,r);
		this.roles.add(ur);
	}

	public void desasignarRol(Rol r){
		this.roles.contains(r);
	}
	
	public List<UsuarioRol> getRoles() {
		return roles;
	}

	public void setRoles(List<UsuarioRol> roles) {
		this.roles = roles;
	}
	
	public Integer getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(Integer id_usuario) {
		this.id_usuario = id_usuario;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getNombre_usuario() {
		return nombre_usuario;
	}

	public void setNombre_usuario(String nombre_usuario) {
		this.nombre_usuario = nombre_usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	public String toString(){
		return "Usuario: [ID:"+this.id_usuario+" , "+this.nombre_usuario+" , "+this.password+" , "+this.descripcion+" , "+this.email+" , "+
					this.estado+" ,ID_Persona "+this.persona.getId_persona()+",tipo: "+this.tipo+" ]";
	}
	
	@SuppressWarnings("unchecked")  //esta anotacion es para q no rompa las bolas con los warnings
	@Override
	public JSONObject toJSON(){
		JSONArray idroles;
		List<UsuarioRol> listaroles;

		JSONObject json= new JSONObject();
		json.put("id_usuario", this.id_usuario);
		json.put("nombre_usuario", this.nombre_usuario);
		json.put("password", this.password);
		json.put("descripcion", this.descripcion);
		json.put("email", this.email);
		json.put("estado", this.estado.toString());
		if (this.tipo != null) {
			json.put("tipo", this.tipo.toString());
		}
		//envio el id de la persona con la q esta relacionada
		if(this.persona!=null){
			json.put("id_persona", this.persona.getId_persona());
		}else{
			json.put("id_persona", -1);
		}

		idroles = new JSONArray ();
		listaroles = this.getRoles();
		if (listaroles != null) {
			for (Object rol: listaroles) {
				idroles.add (((UsuarioRol)rol).getRol().getId_rol());
			}
		}
		json.put("roles", idroles);
		
		
		return json;
	}
	
	@Override
	public void SetJSONObject(JSONObject json) {
		this.id_usuario=(Integer) json.get("id_usuario");
		this.nombre_usuario= (String) json.get("nombre_usuario");
		this.password= (String) json.get("password");
		this.email= (String) json.get("email");
		this.descripcion= (String) json.get("descripcion");
		String estado=(String) json.get("estado");
		if(estado!=null){
			this.estado= json.get("estado").toString().charAt(0);
		}else{
			this.estado=null;
		}
		this.tipo= json.get("tipo").toString().charAt(0);
	}

	@Override
	public Object getPrimaryKey() {
		return this.id_usuario;
	}
	
}

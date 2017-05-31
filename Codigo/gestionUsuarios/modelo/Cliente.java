package gestionUsuarios.modelo;

import java.util.ArrayList;
import java.util.List;

import gestionViajes.modelo.*;

import javax.persistence.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import otros.JSONable;

	@Entity
	@Table(name="cliente")
	@NamedQueries({
		@NamedQuery(name="Cliente.buscarPorClaveCandidata",query="SELECT c FROM Cliente c WHERE c.nombre_usuario = :clave_candidata"),
		@NamedQuery(name="Cliente.autocompletar",query="SELECT c FROM Cliente c WHERE CONCAT(c.nombre_usuario , \" \", c.email) LIKE :busqueda")

	})

	@DiscriminatorValue("C")		//valor que va en la tabla de usuario, por el cual JPA distingue que tipo de clase hijo es
public class Cliente extends Usuario implements JSONable {
	@Column(nullable=true)
	protected Integer reputacion;
	@Column(nullable=true)
	protected Integer puntos;
	@Column(nullable=true,length=120)
	protected String foto_registro;
	@Column(nullable=true,length=120)
	protected String foto;
	@Column(nullable=true)
	protected float saldo;
	
	
	@OneToMany(mappedBy="cliente", cascade=CascadeType.PERSIST)
	protected List<Maneja> vehiculos= new ArrayList<Maneja>();
	
	@OneToMany(mappedBy="cliente", cascade=CascadeType.PERSIST)
	protected List<Notificacion> notificaciones= new ArrayList<Notificacion>();
	
	public Cliente(){
		super();
		this.reputacion=7;
		this.puntos=7;
		this.foto_registro="no tiene";
		this.foto="no tiene";
	}

	public Cliente(JSONObject cliente) {
		super(cliente);
		this.reputacion =(Integer) cliente.get("reputacion");
		this.puntos= (Integer) cliente.get("puntos");
		this.foto_registro=(String) cliente.get("foto_registro");
		this.foto=(String) cliente.get("foto");
		this.tipo='C';
		
	}

	
	public void setSaldo(float saldo){
		this.saldo=saldo;
	}
	
	public float getSaldo(){
		return saldo;
	}
	
	
	public Integer getReputacion() {
		return reputacion;
	}

	public void setReputacion(Integer reputacion) {
		this.reputacion = reputacion;
	}

	public Integer getPuntos() {
		return puntos;
	}

	public void setPuntos(Integer puntos) {
		this.puntos = puntos;
	}
	public String getFoto_registro() {
		return foto_registro;
	}

	public void setFoto_registro(String foto_registro) {
		this.foto_registro = foto_registro;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	public List<Maneja> getVehiculos() {
		return vehiculos;
	}

	public void setVehiculos(List<Maneja> vehiculos) {
		this.vehiculos = vehiculos;
	}

	@SuppressWarnings("unchecked")
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
		json.put("tipo", this.tipo.toString());
		json.put("puntos", this.puntos);
		json.put("reputacion", this.reputacion);
		json.put("foto", this.foto);
		json.put("foto_registro", this.foto_registro);
		
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
				idroles.add( ((UsuarioRol)rol).getRol().getId_rol() );
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
		Integer reputacion = (Integer) json.get("reputacion");
		if (reputacion!=null){
			this.reputacion =reputacion;
		}
		Integer puntos = (Integer) json.get("puntos");
		if (puntos != null){
			this.puntos= puntos;
		}
		String foto = (String) json.get("foto");
		if (foto != null){
			this.foto= foto;
		}
		String foto_registro = (String) json.get("foto_registro");
		if (foto_registro != null){
			this.foto_registro= foto;
		}
		this.tipo='C';
	}

	@Override
	public Object getPrimaryKey() {
		return this.id_usuario;
	}
	
	@Override
	public String toString(){
		return "CLiente: [ID:"+this.id_usuario+" , "+this.nombre_usuario+" , "+this.password+" , "+this.descripcion+" , "+this.email+" , "
				+this.estado+" ,ID_Persona "+this.persona.getId_persona()+",tipo: "+this.tipo+" ,reputacion: "+this.reputacion+" ,puntos: "+this.puntos+
				", "+this.foto+", "+this.foto_registro+" ]";
	}

	public boolean asignarVehiculo(Vehiculo vehiculo) {
		Maneja maneja= new Maneja(this,vehiculo);
		vehiculo.getConductores().add(maneja);
		this.vehiculos.add(maneja);		
		return true;
	}
	
	//by mufa
	public boolean puedeManejar(Vehiculo vehiculo) {
		for(Maneja m: this.vehiculos){
			//si es el vehiculo q busco
			if(vehiculo.getId() == m.getVehiculo().getId()){
				//si todavia puede manejarlo
				if(m.getFecha_fin()==null){
					return true;
				}
			}
		}
		//si llego aca es por q no podia manejar un vehiculo con ese id
		return false;
	}

	public List<Notificacion> getNotificaciones() {
		return notificaciones;
	}

	public void setNotificaciones(List<Notificacion> notificaciones) {
		this.notificaciones = notificaciones;
	}
	
	public void enviarNotificacion(Notificacion notificacion){
		this.notificaciones.add(notificacion);
	}

	//by mufa
	//metodo que devuelve los vehiculos que puede manjejar el cliente
	public List<Vehiculo> getVehiculosQueManeja() {
		ArrayList<Vehiculo> v=new ArrayList<Vehiculo>();
		for(Maneja m: this.vehiculos){
			if(m.getFecha_fin()==null){
				v.add(m.getVehiculo());
			}
		}
		return v;
	}

	//by juan
	// Para saber si dos clientes son el mismo (llamado por List<Cliente> en metodo contains)
    @Override
    public boolean equals(Object object){
        boolean igual = false;

        if (object != null && object instanceof Cliente){
            igual = this.getId_usuario() == ((Cliente) object).getId_usuario() 
            		&& this.getNombre_usuario().equals(((Cliente) object).getNombre_usuario());
        }

        return igual;
    }

	public void sumarPuntos(Integer puntos) {
		this.puntos+=puntos;
	}
	
}

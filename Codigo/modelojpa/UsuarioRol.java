package modelojpa;

import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
	@NamedQuery(name="UsuarioRol.todos",query="SELECT ur FROM UsuarioRol ur"),
	
})
@Table(name="usuario_rol")
@IdClass(UsuarioRolID.class)

public class UsuarioRol {	
	
	@Id 
	@JoinColumn(name="id_rol")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Rol rol;
	
	@Id 
	@JoinColumn(name="id_usuario")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Usuario usuario;
	
	@Column(nullable=false)
	protected Date fecha_modificacion;
	
	public UsuarioRol(){
		
	}
	
	
	public UsuarioRol(Usuario usuario, Rol rol) {
		this.usuario=usuario;
		this.rol=rol;
		this.fecha_modificacion=new Date((new java.util.Date()).getTime());
	}


	public Date getFecha_modificacion() {
		return fecha_modificacion;
	}

	public void setFecha_modificacion(Date fecha_modificacion) {
		this.fecha_modificacion = fecha_modificacion;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public String toString(){
		return "USUARIO-ROL:[ u:"+this.usuario.getId_usuario()+" ,r: "+this.rol.getId_rol()+" ,fecha: "+this.fecha_modificacion.toString()+"]";
	}
	
}

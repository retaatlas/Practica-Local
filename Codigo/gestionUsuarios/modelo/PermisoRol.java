package gestionUsuarios.modelo;

import java.sql.Date;
import java.sql.Timestamp;

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
	@NamedQuery(name="PermisoRol.todos",query="SELECT pr FROM PermisoRol pr"),
	
})
@Table(name="PERMISO_ROL")
@IdClass(PermisoRolID.class)
public class PermisoRol {
	@Id 
	@JoinColumn(name="id_rol")
	@ManyToOne(cascade=CascadeType.PERSIST)
	  private Rol rol;
	
	@Id
	@JoinColumn(name="id_permiso")
	@ManyToOne(cascade=CascadeType.PERSIST)
	  private Permiso permiso;

	@Column(nullable=false)
	protected Timestamp fecha_modificacion;

	  public PermisoRol(){
		  //no hago nada
	  }
	  
	  public PermisoRol(Permiso p, Rol r){
		  this.permiso=p;
		  this.rol=r;
		  this.fecha_modificacion=new Timestamp((new java.util.Date()).getTime());
	  }
	  
		public Timestamp getFecha_modificacion() {
			return fecha_modificacion;
		}
		public void setFecha_modificacion(Timestamp fecha_modificacion) {
			this.fecha_modificacion = fecha_modificacion;
		}
		public Permiso getPermiso() {
			return permiso;
		}
		public void setPermiso(Permiso permiso) {
			this.permiso = permiso;
		}
		public Rol getRol() {
			return rol;
		}
		public void setRol(Rol rol) {
			this.rol = rol;
		}
		
		public String toString(){
			return "PERMISO-ROL:[ p:"+this.permiso.getId_permiso()+" ,r: "+this.rol.getId_rol()+" , fecha:"+this.fecha_modificacion.toString()+"]";
		}
}

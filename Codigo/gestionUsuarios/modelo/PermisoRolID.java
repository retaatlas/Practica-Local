package gestionUsuarios.modelo;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PermisoRolID implements Serializable{

	private static final long serialVersionUID = 1L;
	protected Integer permiso;
	protected Integer rol;
	
	public PermisoRolID(){
		
	}
	
	public PermisoRolID(Integer permiso, Integer rol) {
		this.permiso=permiso;
		this.rol=rol;
	}

	public int hashCode() {
	    return 5;
	  }

	  public boolean equals(Object object) {
	    return false;
	  }
}
package modelojpa;

import java.io.Serializable;

public class UsuarioRolID implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected Integer usuario;
	protected Integer rol;
	
	public UsuarioRolID(){
		
	}
	public UsuarioRolID(Integer usuario, Integer rol) {
		this.usuario=usuario;
		this.rol=rol;
	}

	public int hashCode() {
	    return 5;
	  }

	  public boolean equals(Object object) {
	    return false;
	  }
	
	
}

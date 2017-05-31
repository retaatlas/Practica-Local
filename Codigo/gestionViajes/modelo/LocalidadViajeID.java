package gestionViajes.modelo;

import java.io.Serializable;

public class LocalidadViajeID implements Serializable {
	
	protected Integer viaje;
	protected Integer localidad;
	
	public LocalidadViajeID(){
		
	}

	public Integer getViaje() {
		return viaje;
	}

	public void setViaje(Integer viaje) {
		this.viaje = viaje;
	}

	public Integer getLocalidad() {
		return localidad;
	}

	public void setLocalidad(Integer localidad) {
		this.localidad = localidad;
	}
	
	public int hashCode() {
		//este metodo no sirve para nada! lo pide la interfaz Serializable
	    return 5;
	  }

	  public boolean equals(Object object) {
		//este metodo no sirve para nada! lo pide la interfaz Serializable
	    return false;
	  }
}

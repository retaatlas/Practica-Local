package gestionViajes.modelo;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class ManejaID implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer cliente;
	private Integer vehiculo;
	private Timestamp fecha_inicio;
	
	public ManejaID(){
		
	}
	
	public Timestamp getFecha_inicio() {
		return fecha_inicio;
	}

	public void setFecha_inicio(Timestamp fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}
	
	public ManejaID(Integer id_cliente, Integer id_vehiculo){
		this.cliente=id_cliente;
		this.vehiculo=id_vehiculo;
	}
	public Integer getCliente() {
		return cliente;
	}

	public void setCliente(Integer cliente) {
		this.cliente = cliente;
	}

	public Integer getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(Integer vehiculo) {
		this.vehiculo = vehiculo;
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

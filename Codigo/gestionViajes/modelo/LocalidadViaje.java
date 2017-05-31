package gestionViajes.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import otros.JSONable;

@Entity
@Table(name="localidad_viaje")
@IdClass(LocalidadViajeID.class)
public class LocalidadViaje implements JSONable{

	@Id
	@JoinColumn(name="id_viaje")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Viaje viaje;
	@Id
	@JoinColumn(name="ID_LOCALIDAD")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Localidad localidad;
	@Column(nullable=false,name="CANTIDAD_PASAJEROS")
	protected Integer cantidad_pasajeros;
	@Column(nullable=false,name="ORDINAL")
	protected Integer ordinal;
	@Column(nullable=false,name="KMS_A_LOCALIDAD_SIGUIENTE")
	protected Double kms_a_localidad_siguiente;
	
	@OneToMany(mappedBy="localidad_bajada", cascade=CascadeType.PERSIST)
	protected List<PasajeroViaje> pasajeros_bajaron= new ArrayList<PasajeroViaje>();
	
	@OneToMany(mappedBy="localidad_subida", cascade=CascadeType.PERSIST)
	protected List<PasajeroViaje> pasajeros_subieron= new ArrayList<PasajeroViaje>();
	
	public LocalidadViaje(){
		
	}

	public LocalidadViaje(Viaje viaje, Localidad localidad) {
		this.localidad=localidad;
		this.viaje=viaje;
		this.cantidad_pasajeros=0;
	}

	public Viaje getViaje() {
		return viaje;
	}

	public void setViaje(Viaje viaje) {
		this.viaje = viaje;
	}

	public Localidad getLocalidad() {
		return localidad;
	}

	public void setLocalidad(Localidad localidad) {
		this.localidad = localidad;
	}

	public Integer getCantidad_pasajeros() {
		return cantidad_pasajeros;
	}

	public void setCantidad_pasajeros(Integer cantidad_pasajeros) {
		this.cantidad_pasajeros = cantidad_pasajeros;
	}

	public Double getKms_a_localidad_siguiente() {
		return kms_a_localidad_siguiente;
	}

	public void setKms_a_localidad_siguiente(Double kms_a_localidad_siguiente) {
		this.kms_a_localidad_siguiente = kms_a_localidad_siguiente;
	}

	public List<PasajeroViaje> getPasajeros_bajaron() {
		return pasajeros_bajaron;
	}

	public void setPasajeros_bajaron(List<PasajeroViaje> pasajeros_bajaron) {
		this.pasajeros_bajaron = pasajeros_bajaron;
	}

	public List<PasajeroViaje> getPasajeros_subieron() {
		return pasajeros_subieron;
	}

	public void setPasajeros_subieron(List<PasajeroViaje> pasajeros_subieron) {
		this.pasajeros_subieron = pasajeros_subieron;
	}

	public Integer getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}

	@Override
	public void SetJSONObject(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONObject toJSON() {
		JSONObject localidad = new JSONObject();
		localidad.put("cantidad_pasajeros", this.getCantidad_pasajeros());
		localidad.put("kms_a_localidad_siguiente", this.getKms_a_localidad_siguiente());
		localidad.put("id_localidad", this.getLocalidad().getId());
		localidad.put("nombre_localidad", this.getLocalidad().getNombre());
		localidad.put("ordinal", this.getOrdinal());
		return localidad;
	}

	@Override
	public Object getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

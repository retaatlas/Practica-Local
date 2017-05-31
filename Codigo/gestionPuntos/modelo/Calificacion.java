package gestionPuntos.modelo;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import gestionUsuarios.modelo.Cliente;
import gestionViajes.modelo.PasajeroViaje;
import otros.JSONable;

@NamedQueries({
	@NamedQuery(name="Calificacion.todos",query="SELECT c FROM Calificacion c"),
	@NamedQuery(name="Calificacion.SearchById",query="SELECT c FROM Calificacion c WHERE c.id_calificacion= :id"),
	@NamedQuery(name="Calificacion.ClaveCandidateCompuesta",query="SELECT c FROM Calificacion c WHERE c.pasajero_viaje= :cc1 AND c.conductor= :cc2"),
})
@Entity
@Table(name="calificacion")
public class Calificacion implements JSONable {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	@Column(name="ID_CALIFICACION")
	protected Integer id_calificacion;
	
	@Column(name="CALIFICACION_PARA_CONDUCTOR",nullable=true)
	protected Integer calificacion_para_conductor;
	
	@Column(name="CALIFICACION_PARA_PASAJERO",nullable=true)
	protected Integer calificacion_para_pasajero;
	
	//@Column(name="PARTICIPO",nullable=false)
	//protected EstadoClasificacion participo;
	
	@Column(name="PARTICIPO_CONDUCTOR",nullable=true)
	protected Character participo_conductor;
	
	@Column(name="PARTICIPO_PASAJERO",nullable=true)
	protected Character participo_pasajero;
	
	@Column(name="COMENTARIO_CONDUCTOR",nullable=true)
	protected String comentario_conductor;
	
	@Column(name="COMENTARIO_PASAJERO",nullable=true)
	protected String comentario_pasajero;
	
	@JoinColumn(name="ID_PASAJERO_VIAJE")
	@OneToOne(cascade=CascadeType.PERSIST)
	protected PasajeroViaje pasajero_viaje;
	
	@JoinColumn(name="ID_CONDUCTOR")
	@OneToOne(cascade=CascadeType.PERSIST)
	protected Cliente conductor;
	
	@JoinColumn(name="ID_MOVIMIENTO_PUNTOS_CHOFER")
	@OneToOne(cascade=CascadeType.PERSIST)
	protected MovimientoPuntos movimiento_puntos_chofer;
	
	@JoinColumn(name="ID_MOVIMIENTO_PUNTOS_PASAJERO")
	@OneToOne(cascade=CascadeType.PERSIST)
	protected MovimientoPuntos movimiento_puntos_pasajero;
	
	
	public Calificacion(){
		
	}
	
	
	public Integer getId_calificacion() {
		return id_calificacion;
	}


	public void setId_calificacion(Integer id_calificacion) {
		this.id_calificacion = id_calificacion;
	}


	public Integer getCalificacion_para_conductor() {
		return calificacion_para_conductor;
	}


	public void setCalificacion_para_conductor(Integer calificacion_para_conductor) {
		this.calificacion_para_conductor = calificacion_para_conductor;
	}


	public Integer getCalificacion_para_pasajero() {
		return calificacion_para_pasajero;
	}


	public void setCalificacion_para_pasajero(Integer calificacion_para_pasajero) {
		this.calificacion_para_pasajero = calificacion_para_pasajero;
	}


	public Integer getId() {
		return id_calificacion;
	}


	public void setId(Integer id) {
		this.id_calificacion = id;
	}


	public PasajeroViaje getPasajero_viaje() {
		return pasajero_viaje;
	}


	public void setPasajero_viaje(PasajeroViaje pasajero_viaje) {
		this.pasajero_viaje = pasajero_viaje;
	}

	public Character getParticipo_conductor() {
		return participo_conductor;
	}


	public void setParticipo_conductor(Character participo_conductor) {
		this.participo_conductor = participo_conductor;
	}


	public Character getParticipo_pasajero() {
		return participo_pasajero;
	}


	public void setParticipo_pasajero(Character participo_pasajero) {
		this.participo_pasajero = participo_pasajero;
	}


	public String getComentario_conductor() {
		return comentario_conductor;
	}


	public void setComentario_conductor(String comentario_conductor) {
		this.comentario_conductor = comentario_conductor;
	}


	public String getComentario_pasajero() {
		return comentario_pasajero;
	}


	public void setComentario_pasajero(String comentario_pasajero) {
		this.comentario_pasajero = comentario_pasajero;
	}


	public MovimientoPuntos getMovimiento_puntos_chofer() {
		return movimiento_puntos_chofer;
	}


	public void setMovimiento_puntos_chofer(MovimientoPuntos movimiento_puntos_chofer) {
		this.movimiento_puntos_chofer = movimiento_puntos_chofer;
	}


	public MovimientoPuntos getMovimiento_puntos_pasajero() {
		return movimiento_puntos_pasajero;
	}


	public void setMovimiento_puntos_pasajero(MovimientoPuntos movimiento_puntos_pasajero) {
		this.movimiento_puntos_pasajero = movimiento_puntos_pasajero;
	}


	public Cliente getConductor() {
		return conductor;
	}


	public void setConductor(Cliente conductor) {
		this.conductor = conductor;
	}


	@Override
	public void SetJSONObject(JSONObject json) {
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrimaryKey() {
		return null;
	}

}

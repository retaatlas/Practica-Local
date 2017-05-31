package gestionViajes.modelo;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import gestionComisiones.modelo.ComisionCobrada;
import gestionPuntos.modelo.Calificacion;
import gestionUsuarios.modelo.Cliente;
import otros.JSONable;

@NamedQueries({
	@NamedQuery(name="PasajeroViaje.buscarPorViajeCliente",query="SELECT pv FROM PasajeroViaje pv WHERE pv.viaje = :viaje AND pv.cliente = :cliente")
})
@Entity
@Table(name="pasajero_viaje")
public class PasajeroViaje implements JSONable {

	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorPermiso")
	@SequenceGenerator(allocationSize=1, schema="pruebajpa",  name="MySequenceGeneratorPasajeroViaje", sequenceName = "sequence")
	protected Integer id_pasajero_viaje;
	@Column(nullable=true,name="KILOMETROS")
	protected Double kilometros;

	@Column(nullable=false,name="ESTADO")
	protected EstadoPasajeroViaje estado;
	
	@Column(nullable=false,name="NRO_ASIENTOS")
	protected Integer nro_asientos;
	
	@JoinColumn(name="ID_CALIFICACION")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Calificacion calificacion;
	
	@JoinColumn(name="ID_CLIENTE")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Cliente cliente;
	
	@JoinColumn(name="ID_COMISION_COBRADA")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected ComisionCobrada comision;
	
	@JoinColumns({
		@JoinColumn(name="ID_LOCALIDAD_BAJADA", referencedColumnName="id_localidad"),
		@JoinColumn(name="ID_VIAJE", referencedColumnName="id_viaje", updatable=false, insertable=false)
	})
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected LocalidadViaje localidad_bajada;
	
	@JoinColumns({
		@JoinColumn(name="ID_LOCALIDAD_SUBIDA", referencedColumnName="id_localidad"),
		@JoinColumn(name="ID_VIAJE", referencedColumnName="id_viaje", updatable=false, insertable=false)
	})
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected LocalidadViaje localidad_subida;
	
	@JoinColumn(name="ID_VIAJE")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Viaje viaje;
	
	public PasajeroViaje(){

	}

	public Double getKilometros() {
		return kilometros;
	}

	public void setKilometros(Double kilometros) {
		this.kilometros = kilometros;
	}

	public Calificacion getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(Calificacion calificacion) {
		this.calificacion = calificacion;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public ComisionCobrada getComision() {
		return comision;
	}

	public void setComision(ComisionCobrada comision) {
		this.comision = comision;
	}

	public Viaje getViaje() {
		return viaje;
	}

	public void setViaje(Viaje viaje) {
		this.viaje = viaje;
	}

	public LocalidadViaje getLocalidad_bajada() {
		return localidad_bajada;
	}

	public void setLocalidad_bajada(LocalidadViaje localidad_bajada) {
		this.localidad_bajada = localidad_bajada;
	}

	public LocalidadViaje getLocalidad_subida() {
		return localidad_subida;
	}

	public void setLocalidad_subida(LocalidadViaje localidad_subida) {
		this.localidad_subida = localidad_subida;
	}

	public Integer getId_pasajero_viaje() {
		return id_pasajero_viaje;
	}

	public void setId_pasajero_viaje(Integer id_pasajero_viaje) {
		this.id_pasajero_viaje = id_pasajero_viaje;
	}

	public EstadoPasajeroViaje getEstado() {
		return estado;
	}

	public void setEstado(EstadoPasajeroViaje estado) {
		this.estado = estado;
	}

	public Integer getNro_asientos() {
		return nro_asientos;
	}

	public void setNro_asientos(Integer nro_asientos) {
		this.nro_asientos = nro_asientos;
	}

	@Override
	public void SetJSONObject(JSONObject json) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}

	// Para saber si dos pasajeros de un mismo viaje son el mismo (llamado por List<PasajeroViaje> en metodo contains)
    @Override
    public boolean equals(Object object){
        boolean igual = false;

        if (object != null && object instanceof PasajeroViaje){
            igual = this.getCliente().getId_usuario() == ((PasajeroViaje) object).getCliente().getId_usuario() 
            		&& this.getViaje().getId_viaje() == ((PasajeroViaje) object).getViaje().getId_viaje();
        }

        return igual;
    }
}

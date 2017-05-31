package gestionComisiones.modelo;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import gestionViajes.modelo.PasajeroViaje;
import java.sql.Timestamp;
import otros.JSONable;

@NamedQueries({
	@NamedQuery(name="ComisionCobrada.todos",query="SELECT n FROM ComisionCobrada n"),
	@NamedQuery(name="ComisionCobrada.porUsuario",query="SELECT n FROM ComisionCobrada n where n.pasajero_viaje= :id_cliente"),
	@NamedQuery(name="ComisionCobrada.pagas",query="SELECT n FROM ComisionCobrada n where n.estado=gestionComisiones.modelo.EstadoComisionCobrada.pagado"),
})
@Entity
@Table(name="comision_cobrada")
public class ComisionCobrada implements JSONable {
	
	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorComisionCobrada")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorComisionCobrada", sequenceName = "sequence")
	@Column(name="ID_COMISION_COBRADA")
	protected Integer id_comision_cobrada;
	@Column(name="MONTO",nullable=false)
	protected float monto;
	@Column(name="ESTADO",nullable=false)
	protected EstadoComisionCobrada estado;
	@Column(name="FECHA",nullable=false)
	protected Timestamp fecha;
	@JoinColumn(name="ID_COMISION")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Comision comision;
	@JoinColumn(name="ID_MOVIMIENTO_SALDO")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected MovimientoSaldo movimiento_saldo;
	@JoinColumn(name="ID_PASAJERO_VIAJE")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected PasajeroViaje pasajero_viaje;
	
	public Integer getId_comision_cobrada() {
		return id_comision_cobrada;
	}

	public void setId_comision_cobrada(Integer id_comision_cobrada) {
		this.id_comision_cobrada = id_comision_cobrada;
	}

	public EstadoComisionCobrada getEstado() {
		return estado;
	}

	public void setEstado(EstadoComisionCobrada estado) {
		this.estado = estado;
	}

	public ComisionCobrada(){
		
	}

	public Integer getId() {
		return id_comision_cobrada;
	}

	public void setId(Integer id) {
		this.id_comision_cobrada = id;
	}

	public float getMonto() {
		return monto;
	}

	public void setMonto(float monto) {
		this.monto = monto;
	}

	public Comision getComision() {
		return comision;
	}

	public void setComision(Comision comision) {
		this.comision = comision;
	}

	public MovimientoSaldo getMovimiento_saldo() {
		return movimiento_saldo;
	}

	public void setMovimiento_saldo(MovimientoSaldo movimiento_saldo) {
		this.movimiento_saldo = movimiento_saldo;
	}

	public PasajeroViaje getPasajero_viaje() {
		return pasajero_viaje;
	}

	public void setPasajero_viaje(PasajeroViaje pasajero_viaje) {
		this.pasajero_viaje = pasajero_viaje;
	}

        
        public Timestamp getFecha() {
		return fecha;
	}

	public void setfecha(Timestamp fecha) {
		this.fecha = fecha;
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

}

package gestionPuntos.modelo;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import gestionUsuarios.modelo.Cliente;
import otros.JSONable;

import javax.persistence.NamedQuery;

@NamedQueries({
	@NamedQuery(name="MovimientoPuntos.PorCiente",query="SELECT mp FROM MovimientoPuntos mp WHERE mp.cliente= :cliente ORDER BY mp.fecha DESC")

})
@Entity
@Table(name="movimiento_puntos")

public class MovimientoPuntos implements JSONable{
	
	@Id
	@Column(nullable=false,name="ID_MOVIMIENTOS_PUNTOS")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorMovimientoPuntos")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorMovimientoPuntos", sequenceName = "sequence")
	protected Integer id_movimiento_puntos;
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ID_CLIENTE")
	protected Cliente cliente;
	@Column(nullable=false,name="FECHA")
	protected Timestamp fecha;
	@Column(nullable=false,name="MONTO")
	protected Integer monto;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="TIPO")
	protected TipoMovimientoPuntos tipo_mov_puntos;
	
	public MovimientoPuntos(){
		
	}

	public Integer getId() {
		return id_movimiento_puntos;
	}

	public void setId(Integer id) {
		this.id_movimiento_puntos = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Timestamp getFecha() {
		return fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public Integer getMonto() {
		return monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
	}

	public Integer getId_movimiento_puntos() {
		return id_movimiento_puntos;
	}

	public void setId_movimiento_puntos(Integer id_movimiento_puntos) {
		this.id_movimiento_puntos = id_movimiento_puntos;
	}

	public TipoMovimientoPuntos getTipo_mov_puntos() {
		return tipo_mov_puntos;
	}

	public void setTipo_mov_puntos(TipoMovimientoPuntos tipo_mov_puntos) {
		this.tipo_mov_puntos = tipo_mov_puntos;
	}

	@Override
	public void SetJSONObject(JSONObject json) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", this.id_movimiento_puntos);
		json.put("monto", this.monto);
		json.put("cliente", this.cliente.getNombre_usuario());
		json.put("fecha", (this.fecha != null)? this.fecha.toString().toString(): null);
		json.put("tipo_mov_punto", this.tipo_mov_puntos.getId_tipo_mov_puntos());
		json.put("descripcion", this.tipo_mov_puntos.getDescripcion());
		return json;
	}

	@Override
	public Object getPrimaryKey() {
		return null;
	}
	
}

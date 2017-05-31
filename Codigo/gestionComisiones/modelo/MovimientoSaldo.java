package gestionComisiones.modelo;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gestionPuntos.modelo.TipoSancion;
import gestionUsuarios.modelo.Cliente;

import javax.persistence.NamedQuery;

import org.json.simple.JSONObject;

import otros.JSONable;

@NamedQueries({
    	@NamedQuery(name="MovSaldo.PorCliente",query="SELECT ms FROM MovimientoSaldo ms WHERE  ms.cliente= :cliente ORDER BY ms.fecha DESC"),//agregada por fede

})
@Entity
@Table(name="MOVIMIENTO_SALDO")
public class MovimientoSaldo implements JSONable{
	
	@Id
	@Column(nullable=false,name="ID_MOVIMIENTO_SALDO")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorMovimientoSaldo")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorMovimientoSaldo", sequenceName = "sequence")
	protected Integer id_movimiento_saldo;
	
	@Column(nullable=false,name="FECHA")
	protected Date fecha;
	
	@Column(nullable=false,name="MONTO")
	protected float monto;
	
	@JoinColumn(name="ID_PAGO")
	@OneToOne(cascade=CascadeType.PERSIST)
	protected Pago pago;
	
	@JoinColumn(name="ID_COMISION_COBRADA")
	@OneToOne(cascade=CascadeType.MERGE)
	protected ComisionCobrada comision_cobrada;
        
	@JoinColumn(name="TIPO")
	@ManyToOne(cascade=CascadeType.PERSIST)	
	protected TipoMovSaldo tipo;
	
        @JoinColumn(name="ID_CLIENTE")
	@ManyToOne(cascade=CascadeType.PERSIST)	
	protected Cliente cliente;
        
	public MovimientoSaldo(){
		
	}

	public Integer getId() {
		return id_movimiento_saldo;
	}

	public void setId(Integer id) {
		this.id_movimiento_saldo = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha2) {
		this.fecha = fecha2;
	}

	public float getMonto() {
		return monto;
	}

	public void setMonto(float monto) {
		this.monto = monto;
	}

	public Pago getPago() {
		return pago;
	}

	public void setPago(Pago pago) {
		this.pago = pago;
	}

	public ComisionCobrada getComision_cobrada() {
		return comision_cobrada;
	}

	public void setComision_cobrada(ComisionCobrada comision_cobrada) {
		this.comision_cobrada = comision_cobrada;
	}
	
	public TipoMovSaldo getTipo_mov_saldo() {
		return tipo;
	}

	public void setTipo_mov_saldo(TipoMovSaldo tipo_mov_saldo) {
		this.tipo = tipo_mov_saldo;
	}
	
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@Override
	public void SetJSONObject(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("fecha", (this.getFecha() != null)? this.getFecha().toString().toString(): null);
		json.put("tipo", this.getTipo_mov_saldo().descripcion);
		json.put("monto", this.getMonto());
		return json;
	}

	@Override
	public Object getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}
}

package gestionComisiones.modelo;

import java.sql.Date;

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

import gestionUsuarios.modelo.Cliente;

@NamedQueries({
	
})
@Entity
@Table(name="PAGO")
public class Pago {
	
	@Id
	@Column(nullable=false,name="ID_PAGO")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorPago")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorPago", sequenceName = "sequence")
	protected Integer id_pago;
	@Column(nullable=false,name="FECHA")
	protected Date fecha;
	@Column(nullable=false,name="MONTO")
	protected float monto;
	
	@JoinColumn(name="ID_CLIENTE")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Cliente cliente;
	
	//@JoinColumn(name="ID_MOVIMIENTO_SALDO")
	//@ManyToOne(cascade=CascadeType.PERSIST)
	//protected MovimientoSaldo movimiento_saldo;
	
	public Pago(){
		
	}

	public Integer getId() {
		return id_pago;
	}

	public void setId(Integer id) {
		this.id_pago = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public float getMonto() {
		return monto;
	}

	public void setMonto(float monto) {
		this.monto = monto;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

        /*
	public MovimientoSaldo getMovimiento_saldo() {
		return movimiento_saldo;
	}

	public void setMovimiento_saldo(MovimientoSaldo movimiento_saldo) {
		this.movimiento_saldo = movimiento_saldo;
	}
*/
}

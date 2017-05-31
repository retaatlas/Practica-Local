package gestionPuntos.modelo;

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

import org.json.simple.JSONObject;

import otros.JSONable;
@NamedQueries({
	
})
@Entity
@Table(name="CUPON")
public class Cupon implements JSONable {
	
	@Id
	@Column(nullable=false,name="ID_CUPON")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorCupon")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorCupon", sequenceName = "sequence")
	protected Integer id_cupon;
	
	protected EstadoCupon estado;
	@Column(nullable=false,name="FECHA_CADUCA")
	protected Date fecha_caduca;
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ID_BENEFICIO")
	protected Beneficio beneficio;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ID_MOVIMIENTO_PUNTOS")
	protected MovimientoPuntos movimiento_puntos;
	
	public Cupon(){
		
	}
	
	public Integer getId() {
		return id_cupon;
	}

	public void setId(Integer id) {
		this.id_cupon = id;
	}

	public EstadoCupon getEstado() {
		return estado;
	}

	public void setEstado(EstadoCupon estado) {
		this.estado = estado;
	}

	public Date getFecha_caduca() {
		return fecha_caduca;
	}

	public void setFecha_caduca(Date fecha_caduca) {
		this.fecha_caduca = fecha_caduca;
	}

	public Beneficio getBeneficio() {
		return beneficio;
	}

	public void setBeneficio(Beneficio beneficio) {
		this.beneficio = beneficio;
	}

	public MovimientoPuntos getMovimiento_puntos() {
		return movimiento_puntos;
	}

	public void setMovimiento_puntos(MovimientoPuntos movimiento_puntos) {
		this.movimiento_puntos = movimiento_puntos;
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

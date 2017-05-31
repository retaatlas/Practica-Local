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

import gestionUsuarios.modelo.Cliente;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.NamedQuery;


@Entity
@Table(name="SANCION")
@NamedQuery(name="Sancion.PorIDCliente",query="SELECT s FROM Sancion s WHERE s.cliente = :id_cliente")    


public class Sancion {
	
	@Id
	@Column(nullable=false,name="ID_SANCION")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorSancion")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorSancion", sequenceName = "sequence")
	protected Integer id_sancion;
	@Column(nullable=false,name="FECHA_INICIO")
	protected Date fecha_inicio;
	@Column(nullable=false,name="FECHA_FIN")
	protected Date fecha_fin;
	

	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ID_CLIENTE")
	protected Cliente cliente;

	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ID_MOVIMIENTO_PUNTOS")
	protected MovimientoPuntos movimiento_puntos;

	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="ID_TIPO_SANCION")
	protected TipoSancion tipo_sancion;
	
	@Column(nullable=false,name="ESTADO")
	protected EstadoSancion estado;
	
	public Sancion(){
		
	}

	public Integer getId() {
		return id_sancion;
	}

	public void setId(Integer id) {
		this.id_sancion = id;
	}

	public Date getFecha_inicio() {
		return fecha_inicio;
	}

	public void setFecha_inicio(Date fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}

	public Date getFecha_fin() {
		return fecha_fin;
	}

	public void setFecha_fin(Date fecha_fin) {
		this.fecha_fin = fecha_fin;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public MovimientoPuntos getMovimiento_puntos() {
		return movimiento_puntos;
	}

	public void setMovimiento_puntos(MovimientoPuntos movimiento_puntos) {
		this.movimiento_puntos = movimiento_puntos;
	}

	public TipoSancion getTipo_sancion() {
		return tipo_sancion;
	}

	public void setTipo_sancion(TipoSancion tipo_sancion) {
		this.tipo_sancion = tipo_sancion;
	}
	
	public void setEstado(EstadoSancion estado){
            this.estado = estado;
        }
	
        public EstadoSancion getEstado(){
            return estado;
        }
	
}

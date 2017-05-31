package gestionUsuarios.modelo;

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
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import otros.JSONable;

@NamedQueries({
	@NamedQuery(name="Notificacion.todos",query="SELECT n FROM Notificacion n ORDER BY n.fecha DESC"),
	@NamedQuery(name="Notificacion.porUsuario",query="SELECT n FROM Notificacion n where n.cliente= :id_cliente ORDER BY n.fecha DESC"),
	@NamedQuery(name="Notificacion.NoLeidasPorUsuario",query="SELECT n FROM Notificacion n where (n.cliente= :id_cliente) AND (n.estado= gestionUsuarios.modelo.EstadoNotificacion.no_leido) ORDER BY n.fecha DESC"),
	@NamedQuery(name="Notificacion.cantidadNoLeidaPorUsuario",query="SELECT COUNT(n.id_notificacion) FROM Notificacion n where (n.cliente= :id_cliente) AND (n.estado= gestionUsuarios.modelo.EstadoNotificacion.no_leido)"),
	@NamedQuery(name="Notificacion.SearchById",query="SELECT n FROM Notificacion n WHERE n.id_notificacion = :id"),
})

@Entity
@Table(name="notificacion")
public class Notificacion implements JSONable {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)		//pruebo como es el tema con generationType.auto
	@Column(name="ID_NOTIFICACION")
	protected Integer id_notificacion;
	@Column(name="FECHA",nullable=false)
	protected Timestamp fecha;
	@Column(name="TEXTO",nullable=false)
	protected String texto;
	@Column(name="LINK",nullable=true)
	protected String link;
	@Column(name="ESTADO",nullable=false,length=1)
	protected EstadoNotificacion estado;		
	
	@JoinColumn(name="ID_CLIENTE")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Cliente cliente;
	
	public Notificacion(){
		
	}

	public Integer getId_notificacion() {
		return id_notificacion;
	}

	public void setId_notificacion(Integer id_notificacion) {
		this.id_notificacion = id_notificacion;
	}

	public Timestamp getFecha() {
		return fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public EstadoNotificacion getEstado() {
		return estado;
	}

	public void setEstado(EstadoNotificacion estado) {
		this.estado = estado;
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
		json.put("id_notificacion", this.id_notificacion);
		json.put("fecha",(this.fecha!=null)? this.fecha.toString(): null);
		json.put("texto", this.texto);
		json.put("link", this.link);
		json.put("estado", this.estado.toString());
		return json;
	}

	@Override
	public Object getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

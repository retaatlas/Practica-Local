package gestionComisiones.modelo;

import java.sql.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import otros.JSONable;

@NamedQueries({
	@NamedQuery(name="Comision.todos",query="SELECT n FROM Comision n"),
	@NamedQuery(name="Comision.porKm",query="SELECT n FROM Comision n WHERE n.limite_inferior <= :km AND n.limite_superior > :km"),
	@NamedQuery(name="Comision.PrecioPorKM",query="SELECT n FROM Comision n WHERE n.limite_inferior <= :km AND n.limite_superior > :km AND n.fecha_fin is NULL"),
	@NamedQuery(name="Comision.SearchById",query="SELECT c FROM Comision c WHERE c.id_comision = :id"),
	@NamedQuery(name="Comision.vigentes",query="SELECT c FROM Comision c WHERE c.fecha_fin is NULL ORDER BY c.id_comision"),
	@NamedQuery(name="Comision.NOvigentes",query="SELECT c FROM Comision c WHERE c.fecha_fin is NOT NULL ORDER BY c.id_comision"),
})
@Entity
@Table(name="COMISION")
public class Comision implements JSONable {

	@Id
	@Column(nullable=false,name="ID_COMISION")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorComision")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorComision", sequenceName = "sequence")
	protected Integer id_comision;
	@Column(nullable=false,name="LIMITE_SUPERIOR")
	protected Integer limite_superior;
	@Column(nullable=false,name="LIMITE_INFERIOR")
	protected Integer limite_inferior;
	@Column(nullable=false,name="PRECIO")
	protected Float precio;
	@Column(nullable=false,name="FECHA_INICIO")
	protected Date fecha_inicio;
	@Column(nullable=false,name="FECHA_FIN")
	protected Date fecha_fin;
	
	/*
	@JoinColumn(name="PRECIO_COMISION")
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected PrecioComision precio_comision;
	*/
	@OneToMany(mappedBy="comision", cascade=CascadeType.PERSIST)
	protected List<ComisionCobrada> comisiones_cobradas;
	
	public Comision(){
		
	}
	
	public Integer getId() {
		return id_comision;
	}

	public void setId(Integer id_comision) {
		this.id_comision = id_comision;
	}

	public Integer getLimite_superior() {
		return limite_superior;
	}

	public void setLimite_superior(Integer limite_superior) {
		this.limite_superior = limite_superior;
	}

	public Integer getLimite_inferior() {
		return limite_inferior;
	}

	public void setLimite_inferior(Integer limite_inferior) {
		this.limite_inferior = limite_inferior;
	}

	public Float getPrecio() {
		return precio;
	}

	public void setPrecio(Float precio) {
		this.precio = precio;
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

	public Integer getId_comision() {
		return id_comision;
	}

	public void setId_comision(Integer id_comision) {
		this.id_comision = id_comision;
	}

	public List<ComisionCobrada> getComisiones_cobradas() {
		return comisiones_cobradas;
	}

	public void setComisiones_cobradas(List<ComisionCobrada> comisiones_cobradas) {
		this.comisiones_cobradas = comisiones_cobradas;
	}

	@Override
	public void SetJSONObject(JSONObject json) {
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id_comision", this.id_comision);
		json.put("limite_inferior", this.limite_inferior);
		json.put("limite_superior", this.limite_superior);
		json.put("precio", this.precio);
		json.put("fecha_inicio", ((this.fecha_inicio != null)? this.fecha_inicio.toString(): null));
		json.put("fecha_fin", ((this.fecha_fin != null)? this.fecha_fin.toString(): null));
		return json;
	}

	@Override
	public Object getPrimaryKey() {
		return null;
	}
}

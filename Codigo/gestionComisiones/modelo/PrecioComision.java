package gestionComisiones.modelo;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import otros.JSONable;
@NamedQueries({
	
})
@Entity
@Table(name="PRECIO_COMISION")
public class PrecioComision implements JSONable {

	/*
	 * 		ESTA CLASE DESAPARECE
	 * 		ESTA CLASE DESAPARECE
	 * 		ESTA CLASE DESAPARECE
	 * 		ESTA CLASE DESAPARECE
	 */
	
	@Id
	@Column(nullable=false,name="ID_COMISION")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorPrecioComision")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorPrecioComision", sequenceName = "sequence")
	protected Integer id_comision;
	@Column(nullable=false,name="FECHA_DESDE")
	protected Date fecha_desde;
	@Column(nullable=false,name="FECHA_HASTA")
	protected Date fecha_hasta;
	@Column(nullable=false,name="MONTO")
	protected float Monto;
	//@OneToMany(mappedBy="precio_comision", cascade=CascadeType.PERSIST)
	//protected List<Comision> comision;
	
	public PrecioComision(){
		
	}
	
	@Override
	public void SetJSONObject(JSONObject json) {

	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public Object getPrimaryKey() {
		return null;
	}

	public float getMonto(){
		return this.Monto;
	}
}

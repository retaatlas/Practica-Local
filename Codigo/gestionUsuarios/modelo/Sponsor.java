package gestionUsuarios.modelo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import otros.JSONable;

@Entity
@Table(name="sponsor")
@NamedQuery(name="Sponsor.buscarPorClaveCandidata",query="SELECT s FROM Sponsor s WHERE s.nombre_usuario = :clave_candidata")
@DiscriminatorValue("S")
public class Sponsor extends Usuario implements JSONable {
	
	@Column(nullable=false)
	protected Integer cuit;
	@Column(nullable=false,length=120)
	protected String rubro;
	
	
	public Sponsor(){
		
	}
	
	public Sponsor (JSONObject sponsor) {
		super(sponsor);
                this.cuit = (Integer) sponsor.get("CUIT");
		this.rubro = (String) sponsor.get("rubro");
		this.tipo='S';
		
	}

 
	public Integer getCuit() {
		return cuit;
	}


	public void setCuit(Integer cuit) {
		this.cuit = cuit;
	}


	public String getRubro() {
		return rubro;
	}


	public void setRubro(String rubro) {
		this.rubro = rubro;
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

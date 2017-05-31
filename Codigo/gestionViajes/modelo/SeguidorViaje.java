package gestionViajes.modelo;

import gestionUsuarios.modelo.Cliente;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import otros.JSONable;

@NamedQueries({
	    @NamedQuery(name="SeguidorViaje.BuscarPorViaje", query="SELECT s FROM SeguidorViaje s WHERE s.viaje= :viaje")
})
@Entity
@Table(name="seguidor_viaje")
public class SeguidorViaje implements JSONable {
	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorSeguidorViaje")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorSeguidorViaje", sequenceName = "sequence")
	@Column(name="ID_SEGUIDOR_VIAJE")
	protected Integer id_seguidor_viaje;
	 
	 @Column(nullable=false,name="fecha")
	protected Timestamp fecha;
	 
	 @ManyToOne(cascade=CascadeType.PERSIST)
	 @JoinColumn(name="id_cliente")
	 protected Cliente cliente;
	 
	 @ManyToOne(cascade=CascadeType.PERSIST)
	 @JoinColumn(name="id_viaje")
	 protected Viaje viaje;
	 
	 @Column(nullable=false,name="estado")
	 protected Character estado;
     
	public Integer getId_seguidor_viaje() {
		return id_seguidor_viaje;
	}

	public void setId_seguidor_viaje(Integer id_seguidor_viaje) {
		this.id_seguidor_viaje = id_seguidor_viaje;
	}

	public Timestamp getFecha() {
		return fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Viaje getViaje() {
		return viaje;
	}

	public void setViaje(Viaje viaje) {
		this.viaje = viaje;
	}

	public Character getEstado() {
		return estado;
	}

	public void setEstado(Character estado) {
		this.estado = estado;
	}

	public boolean isActivo() {
		return this.getEstado().equals("A".charAt(0));
	}
	
	@Override
	public void SetJSONObject(JSONObject json) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject toJSON() {
		JSONObject seguidor = new JSONObject();
		seguidor.put("estado", this.getEstado().toString());
		seguidor.put("fecha", (this.getFecha() != null)? this.getFecha().toString() : null);
		seguidor.put("nombre_usuario", this.getCliente().getNombre_usuario());
		return seguidor;
	}

	@Override
	public Object getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//by juan
	// Para saber si dos seguidores de u viaje son el mismo (llamado por List<SeguidorViaje> en metodo contains)
    @Override
    public boolean equals(Object object){
        boolean igual = false;

        if (object != null && object instanceof SeguidorViaje){
            igual = this.getViaje().getId_viaje() == ((SeguidorViaje) object).getViaje().getId_viaje() 
            		&& this.getCliente().getId_usuario() == ((SeguidorViaje) object).getCliente().getId_usuario(); 
        }

        return igual;
    }
}

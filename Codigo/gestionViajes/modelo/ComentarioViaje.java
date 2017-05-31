/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionViajes.modelo;

import gestionUsuarios.modelo.Cliente;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

/**
 *
 * @author Fede
 */



@NamedQueries({
	    @NamedQuery(name="Comentarios.BuscarPorViaje", query="SELECT c FROM ComentarioViaje c WHERE c.viaje= :viaje ORDER BY c.fecha DESC")
})
@Entity
@Table(name="comentario_viaje")
public class ComentarioViaje implements JSONable {
        @Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorComentario")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorComentario", sequenceName = "sequence")
	@Column(name="ID_COMENTARIO_VIAJE")
	protected Integer id_comentario_viaje;
        
        @Column(nullable=false,name="fecha")
	protected Timestamp fecha;
        
        @ManyToOne(cascade=CascadeType.PERSIST)
        @JoinColumn(name="id_cliente")
        protected Cliente cliente;
        
        @ManyToOne(cascade=CascadeType.PERSIST)
        @JoinColumn(name="id_viaje")
        protected Viaje viaje;
        
        @Column(nullable=false,name="texto")
        protected String texto;
        
        
        
        
        public void setId_comentario_viaje(int id){
            this.id_comentario_viaje=id;
        }
        public int getId_comentario_viaje(){
            return id_comentario_viaje;
        }
        public void setFecha(Timestamp fecha){
            this.fecha=fecha;
        }
        public Timestamp getFecha(){
            return fecha;
        }
        public void setCliente(Cliente cliente){
            this.cliente=cliente;
        }
        public Cliente getCliente(){
            return cliente;
        }
        public void setViaje(Viaje viaje){
            this.viaje=viaje;
        }
        public Viaje getViaje(){
            return viaje;
        }
        public void setTexto(String texto){
            this.texto=texto;
        }
        public String getTexto(){
            return texto;
        }
        
        
        
        
        
        
        
        
        
        
        

    @Override
    public void SetJSONObject(JSONObject json) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject toJSON() {
		JSONObject comentario = new JSONObject();
		comentario.put("comentario", this.getTexto());
		comentario.put("fecha", (this.getFecha() != null)? this.getFecha().toString() : null);
		comentario.put("nombre_usuario", this.getCliente().getNombre_usuario());
		comentario.put("foto", this.getCliente().getFoto());
		return comentario;
    }

    @Override
    public Object getPrimaryKey() {
        return null;
    }
        
}

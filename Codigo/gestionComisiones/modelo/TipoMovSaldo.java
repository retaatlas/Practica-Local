package gestionComisiones.modelo;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gestionPuntos.modelo.Sancion;


@NamedQueries({
	@NamedQuery(name="TipoMovSaldo.SearchById",query="SELECT c FROM TipoMovSaldo c WHERE c.id_tipo_mov_saldo = :id"),		
})
@Entity
@Table(name="TIPO_MOV_SALDO")
public class TipoMovSaldo {

	@Id
	@Column(nullable=false,name="ID_TIPO_MOV_SALDO")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorTipoMovSaldo")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorTipoMovSaldo", sequenceName = "sequence")
	protected Integer id_tipo_mov_saldo;
	@Column(nullable=false,length=255,name="DESCRIPCION")
	protected String descripcion;
	
        
	
	public TipoMovSaldo(){
		
	}
	
	public Integer getId() {
		return id_tipo_mov_saldo;
	}

	public void setId(Integer id) {
		this.id_tipo_mov_saldo = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	
	
}
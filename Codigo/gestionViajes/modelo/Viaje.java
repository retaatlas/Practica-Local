package gestionViajes.modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import gestionUsuarios.modelo.Cliente;

import javax.persistence.NamedQuery;

import otros.ExceptionViajesCompartidos;
import otros.JSONable;

@NamedQueries({
	@NamedQuery(name="Viaje.SearchById",query="SELECT v FROM Viaje v WHERE v.id_viaje= :id"),//agregada por fede
	@NamedQuery(name="Viaje.SearchByConductor",query="SELECT v FROM Viaje v WHERE EXISTS (SELECT m FROM Maneja m WHERE v.conductor_vehiculo=m AND m.cliente= :conductor) ORDER BY v.fecha_inicio DESC"),
	@NamedQuery(name="Viaje.SearchByPasajero",query="SELECT v FROM Viaje v WHERE EXISTS (SELECT p FROM PasajeroViaje p WHERE p.viaje=v AND p.cliente= :pasajero) ORDER BY v.fecha_inicio DESC"),
	@NamedQuery(name="Viaje.SearchByCliente",query="SELECT v FROM Viaje v WHERE EXISTS (SELECT m FROM Maneja m WHERE v.conductor_vehiculo=m AND m.cliente= :cliente) OR EXISTS (SELECT p FROM PasajeroViaje p WHERE p.viaje=v AND p.cliente= :cliente) ORDER BY v.fecha_inicio DESC"),
	@NamedQuery(name="Viaje.todos",query="SELECT v FROM Viaje v"),
	@NamedQuery(name="Viaje.noIniciadosAtrasados",query="SELECT v FROM Viaje v WHERE v.estado=gestionViajes.modelo.EstadoViaje.no_iniciado AND v.fecha_inicio <= CURRENT_TIMESTAMP"),
	@NamedQuery(name="Viaje.inicianAntes",query="SELECT v FROM Viaje v WHERE v.estado=gestionViajes.modelo.EstadoViaje.no_iniciado AND v.fecha_inicio <= :tiempo"),
        @NamedQuery(name="Viaje.PorVehiculo",query="SELECT vj FROM Viaje vj WHERE EXISTS (SELECT m FROM Maneja m WHERE EXISTS (SELECT ve FROM Vehiculo ve WHERE (m.viajes= vj AND m.vehiculo=ve AND ve.id_vehiculo=:id_vehiculo)))"),
	@NamedQuery(name="Viaje.IniciadosAntes",query="SELECT v FROM Viaje v WHERE v.estado=gestionViajes.modelo.EstadoViaje.iniciado AND v.fecha_inicio <= :tiempo")

})

@Entity
@Table(name="viaje")
public class Viaje implements JSONable {

	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGeneratorViaje")
	@SequenceGenerator(allocationSize=1, schema="seminario",  name="MySequenceGeneratorViaje", sequenceName = "sequence")
	@Column(name="ID_VIAJE")
	protected Integer id_viaje;
	@Column(nullable=false,length=30,name="NOMBRE_AMIGABLE")
	protected String nombre_amigable;
	@Column(nullable=false,name="ASIENTOS_DISPONIBLES")
	protected Integer asientos_disponibles;
	@Column(nullable=false,name="ESTADO")
	protected EstadoViaje estado;
	@Column(nullable=false,name="FECHA_INICIO")
	protected Timestamp fecha_inicio;
	@Column(nullable=false,name="FECHA_ALTA")
	protected Timestamp fecha_alta;
	@Column(nullable=true,name="FECHA_FINALIZACION")
	protected Timestamp fecha_finalizacion;
	@Column(nullable=true,name="FECHA_CANCELACION")
	protected Timestamp fecha_cancelacion;
	@Column(nullable=true,name="PRECIO")
	protected Float precio;
	@JoinColumns ({
		@JoinColumn(name="id_cliente", referencedColumnName="id_cliente"),
		@JoinColumn(name="id_vehiculo", referencedColumnName="id_vehiculo"),
		@JoinColumn(name="fecha_inicio_maneja", referencedColumnName="fecha_inicio"),
	})
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected Maneja conductor_vehiculo;
	@OneToMany(mappedBy="viaje", cascade=CascadeType.PERSIST)
	@OrderBy("ordinal ASC")
	protected List<LocalidadViaje> localidades= new ArrayList<LocalidadViaje>();
	@OneToMany(mappedBy="viaje", cascade=CascadeType.PERSIST)
	protected List<PasajeroViaje> pasajeros= new ArrayList<PasajeroViaje>();
	@JoinColumn(name="viaje_complementario", referencedColumnName = "id_viaje", nullable = true)
	@OneToOne(cascade=CascadeType.PERSIST)
	protected Viaje viaje_complementario;
	
	//by mufa
	public boolean crearRecorrido(List<Localidad> arreglo_de_localidades){
		this.localidades.clear();
		Integer ordinal=1;
		for(Localidad l: arreglo_de_localidades){
			LocalidadViaje lv = new LocalidadViaje(this,l);
			lv.setOrdinal(ordinal);
			this.localidades.add(lv);
			ordinal++;
		}
		return true;
	}
	
	//by mufa
	//juan 26/05/2016: ahora verifica que ese cliente ya sea pasajero, si es asi lo saca y pone el nuevo
	public boolean aniadir_pasajeroViaje (PasajeroViaje cliente, Localidad subida, Localidad bajada) throws ExceptionViajesCompartidos{
		if (this.pasajeros.contains(cliente)){
			this.pasajeros.remove(this.pasajeros.indexOf(cliente));
		}
		this.pasajeros.add(cliente);
		cliente.setLocalidad_bajada(this.contiene_localidad(bajada));
		cliente.setLocalidad_subida(this.contiene_localidad(subida));
		cliente.setViaje(this);
		return true;
	}
	
	//by mufa
	public LocalidadViaje contiene_localidad(Localidad localidad){
		//recorro la lista de localidadesViaje, comparando por id, si tiene la localidad, te devuelve LocalidadViaje
		Integer cantidad_iteraciones= this.localidades.size();
		Integer index=0;
		while(index<cantidad_iteraciones){
			if( this.localidades.get(index).getLocalidad().getId() == localidad.getId() ){
				return this.localidades.get(index);
			}
			index++;
		}
		return null;
	}
	
	//by mufa
	//metodo que devuelve la cantidad de KM o metros, que tiene una parte o todo un recorrido,
	public Double calcularKM(Localidad localidad_subida, Localidad localidad_bajada) throws ExceptionViajesCompartidos {
		if( !this.contiene_localidades_en_orden(localidad_subida, localidad_bajada) ){
			throw new ExceptionViajesCompartidos("ERROR: LA LOCALIDAD DE SUBIDA NO ES ANTERIOR A LA DE BAJADA");
		}
		
		boolean encontrado=false;
		boolean entremedio=false;
		int index=0;
		Double distancia=0.0;
		
		while(!encontrado){
			if ( this.localidades.get(index).getLocalidad().getId()==localidad_subida.getId() ){
				entremedio=true;
			}
			if(entremedio){	//si estoy entre la localidad de subida y la de bajada cuento los KMs
				distancia+=this.localidades.get(index).kms_a_localidad_siguiente;
			}
			
			if (this.localidades.get(index).getLocalidad().getId() == localidad_bajada.getId()){
				entremedio=false;
				encontrado=true;
			}
			index++;
		}
		
		return distancia;
	}
	
	//by mufa
	public List<Localidad> recuperarOrigenYDestino(){
		List<Localidad> origenDestino = new ArrayList<Localidad>();
		origenDestino.add( this.localidades.get(0).getLocalidad() );
		int ultima_localidad=( this.localidades.size() -1);
		origenDestino.add( this.localidades.get(ultima_localidad).getLocalidad() );
		return origenDestino;
	}
	
	public Viaje(){
		
	}
	
	public Integer getId_viaje() {
		return id_viaje;
	}

	public void setId_viaje(Integer id) {
		this.id_viaje = id;
	}

	public Timestamp getFecha_inicio() {
		return fecha_inicio;
	}

	public void setFecha_inicio(Timestamp fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}

	// Despues de esta fecha se considera el viaje como finalizado, aunque el conductor no lo indique asi
	public Timestamp getFecha_autofinalizar() {
		long t_inicio = this.fecha_inicio.getTime();
		double distancia_km;
		double velocidad_kmh = 60d; // suponemos que no viaja muy rapido
		
		try {
			distancia_km = calcularKM (this.getOrigen(), this.getDestino());
		} catch (ExceptionViajesCompartidos e) {
			distancia_km = 0d;
		}

		double t_recorrido_h = ((distancia_km/velocidad_kmh));

		if(t_recorrido_h > 8) {
			// Si el viaje es mas largo que 8 horas, suponemos que viajan solo 12 horas por dia
			t_recorrido_h = t_recorrido_h*2;
		} else if(t_recorrido_h > 3.2) {
			// Si el viaje es mas largo que 3.2 horas, suponemos que ocasionalmente paran a descansar
			t_recorrido_h = t_recorrido_h*1.25d;
		} else {
			// Dejamos 4 horas para viajes cortos 
			t_recorrido_h = 4;
		}

		long t_recorrido = (long)(t_recorrido_h * 3600000d);

		return new Timestamp(t_inicio + t_recorrido);
	}

	public List<PasajeroViaje> getPasajeros() {
		return pasajeros;
	}

	// calificables son los aceptados/finalizados/ausentes que califican/son calificados
	public List<PasajeroViaje> getPasajerosCalificables() {
		List<PasajeroViaje> lista = new ArrayList<PasajeroViaje>();
		for(PasajeroViaje pas: pasajeros) {
			if (pas.estado == EstadoPasajeroViaje.aceptado || pas.estado == EstadoPasajeroViaje.finalizo_viaje || pas.estado == EstadoPasajeroViaje.ausente){
				lista.add(pas);
			}
		}
		return lista;
	}
	public List<Cliente> getPasajerosCalificablesComoListCliente() {
		List<Cliente> lista = new ArrayList<Cliente>();
		List<PasajeroViaje> calificables = this.getPasajerosCalificables();
		for(PasajeroViaje pas: calificables) {
			lista.add(pas.getCliente());
		}
		return lista;
	}
	public List<Cliente> getPasajerosTodosComoListCliente() {
		List<Cliente> lista = new ArrayList<Cliente>();
		for(PasajeroViaje pas: pasajeros) {
			lista.add(pas.getCliente());
		}
		return lista;
	}
	
	public List<Cliente> getPasajerosPostuladosComoListCliente() {
		List<Cliente> lista = new ArrayList<Cliente>();
		for(PasajeroViaje pas: pasajeros) {
			if (pas.getEstado().equals(EstadoPasajeroViaje.postulado)){
				lista.add(pas.getCliente());
			}
		}
		return lista;
	}

	public List<Cliente> getPasajerosAceptadosComoListCliente() {
		List<Cliente> lista = new ArrayList<Cliente>();
		for(PasajeroViaje pas: pasajeros) {
			if (pas.getEstado().equals(EstadoPasajeroViaje.aceptado)){
				lista.add(pas.getCliente());
			}
		}
		return lista;
	}
	
	public List<Cliente> getPasajerosRechazadosComoListCliente() {
		List<Cliente> lista = new ArrayList<Cliente>();
		for(PasajeroViaje pas: pasajeros) {
			if (pas.getEstado().equals(EstadoPasajeroViaje.rechazado)){
				lista.add(pas.getCliente());
			}
		}
		return lista;
	}
	
	
	public List<Cliente> getPasajerosFinalizadosComoListCliente() {
		List<Cliente> lista = new ArrayList<Cliente>();
		for(PasajeroViaje pas: pasajeros) {
			if (pas.getEstado().equals(EstadoPasajeroViaje.finalizo_viaje)){
				lista.add(pas.getCliente());
			}
		}
		return lista;
	}
	
	//by mufa
	public PasajeroViaje recuperar_pasajeroViaje_por_cliente(Cliente cliente){
		for(PasajeroViaje pv: this.pasajeros){
			if(pv.getCliente().getId_usuario()==cliente.getId_usuario()){
				return pv;
			}
		}
		return null;
	}

	public void setPasajeros(List<PasajeroViaje> pasajeros) {
		this.pasajeros = pasajeros;
	}

	public String getNombre_amigable() {
		return nombre_amigable;
	}

	public void setNombre_amigable(String nombre_amigable) {
		this.nombre_amigable = nombre_amigable;
	}

	public Integer getAsientos_disponibles() {
		return asientos_disponibles;
	}

	public void setAsientos_disponibles(Integer asientos_disponibles) {
		this.asientos_disponibles = asientos_disponibles;
	}

	public Timestamp getFecha_alta() {
		return fecha_alta;
	}

	public void setFecha_alta(Timestamp fecha_alta) {
		this.fecha_alta = fecha_alta;
	}

	public Timestamp getFecha_finalizacion() {
		return fecha_finalizacion;
	}

	public EstadoViaje getEstado() {
		return estado;
	}

	public void setEstado(EstadoViaje estado) {
		this.estado = estado;
	}

	public Float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public void setFecha_finalizacion(Timestamp fecha_finalizacion) {
		this.fecha_finalizacion = fecha_finalizacion;
	}

	public Timestamp getFecha_cancelacion() {
		return fecha_cancelacion;
	}

	public void setFecha_cancelacion(Timestamp fecha_cancelacion) {
		this.fecha_cancelacion = fecha_cancelacion;
	}

	public Viaje getViaje_complementario() {
		return viaje_complementario;
	}

	public void setViaje_complementario(Viaje viaje_complementario) {
		this.viaje_complementario = viaje_complementario;
	}

	public Maneja getConductor_vehiculo() {
		return conductor_vehiculo;
	}

	public Cliente getConductor() {
		return this.conductor_vehiculo.getCliente();
	}
	
	public Vehiculo getVehiculo() {
		return this.conductor_vehiculo.getVehiculo();
	}

	public void setConductor_vehiculo(Maneja conductor_vehiculo) {
		this.conductor_vehiculo = conductor_vehiculo;
	}

	public List<LocalidadViaje> getLocalidades() {
		return localidades;
	}

	public void setLocalidades(List<LocalidadViaje> localidades) {
		this.localidades = localidades;
	}
	
	public List<Localidad> getLocalidadesComoListLocalidad() { // Nombre confuso
		List<Localidad> lista = new ArrayList<Localidad>();
		for(LocalidadViaje locv: this.localidades) {
			lista.add(locv.getLocalidad());
		}
		return lista;
	}

	public Localidad getOrigen() {
		try {
			return localidades.get(0).getLocalidad();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Localidad getDestino() {
		try {
			return localidades.get(localidades.size()-1).getLocalidad();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean actualizarEstado() {
		Timestamp now = new Timestamp((new java.util.Date()).getTime());
		if (
			(this.estado == EstadoViaje.iniciado || this.estado == EstadoViaje.no_iniciado ) 
			&& (this.fecha_finalizacion == null)
			&& (this.getFecha_autofinalizar().compareTo(now) <= 0)
		) {
			this.setEstado(EstadoViaje.finalizado);
			return true;
		}  else if (
			(this.estado == EstadoViaje.no_iniciado) 
			&& (this.fecha_inicio != null)
			&& (this.fecha_inicio.compareTo(now) <= 0)
		) {
			this.setEstado(EstadoViaje.iniciado);
			return true;
		}


		return false;
	}
	
	public JSONArray getLocalidadesViajeJson(){
		List<LocalidadViaje> locs = this.getLocalidades();
		JSONArray recorrido = new JSONArray();
		for (LocalidadViaje locviaje: locs) {
			recorrido.add (locviaje.toJSON());
		}
		return recorrido;
	}
	
	@Override
	public void SetJSONObject(JSONObject json) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON() {
		JSONObject json_viaje = new JSONObject();
		json_viaje.put("id", this.getId_viaje());
		json_viaje.put("nombre_amigable", this.getNombre_amigable());
		json_viaje.put("estado", (this.getEstado().toString()!= null)? this.getEstado().toString(): null);
		if (this.getViaje_complementario() == null) {
			json_viaje.put("tipo", "ida");
		} else {
			json_viaje.put("tipo", "ida_vuelta");
			json_viaje.put("id_viaje_complementario", this.getViaje_complementario().getId_viaje());
		}
		List<LocalidadViaje> locs = this.getLocalidades();
		JSONArray recorrido = new JSONArray();
		JSONArray disponibilidad_asientos = new JSONArray();
		for (LocalidadViaje locviaje: locs) {
			recorrido.add (locviaje.getLocalidad().getId());
			disponibilidad_asientos.add (this.getAsientos_disponibles() - locviaje.getCantidad_pasajeros());
		}

		// WARNING: recorrido debe tener al menos dos puntos o va a fallar
		json_viaje.put("recorrido", recorrido);
		json_viaje.put("disponibilidad_asientos", disponibilidad_asientos);
		json_viaje.put("origen", recorrido.get(0));
		json_viaje.put("destino", recorrido.get(recorrido.size()-1));
		json_viaje.put("fecha_inicio", (this.getFecha_inicio() != null)? this.getFecha_inicio().toString().toString(): null);
		json_viaje.put("fecha_alta", (this.getFecha_alta() != null)? this.getFecha_alta().toString(): null);
		json_viaje.put("fecha_cancelacion", (this.getFecha_cancelacion() != null)? this.getFecha_cancelacion().toString(): null);
		json_viaje.put("fecha_finalizacion", (this.getFecha_finalizacion() != null)? this.getFecha_finalizacion().toString(): null);
		json_viaje.put("precio",this.getPrecio());
		json_viaje.put("asientos_disponibles", this.getAsientos_disponibles());
		json_viaje.put("cantidad_pasajeros_calificables",this.getPasajerosCalificables().size());
		json_viaje.put("cantidad_pasajeros_postulados", this.getPasajerosPostuladosComoListCliente().size());
		/* NOTAS: 
			//TODO hora debe estar dentro de fecha
		*/
		return json_viaje;
	}

	@Override
	public Object getPrimaryKey() {
		return null;
	}
	
	//by mufa
	public Viaje crearTuVuelta(JSONObject vuelta) throws ExceptionViajesCompartidos {
		Viaje mi_vuelta = new Viaje();
		//le pongo al viaje los datos que llegan desde el JSON
		Integer a=(Integer) vuelta.get("cantidad_asientos");
		if(a!=null){
			mi_vuelta.setAsientos_disponibles(a);
		}else{
			mi_vuelta.setAsientos_disponibles(this.asientos_disponibles);
		}
		String n= (String) vuelta.get("nombre_amigable");
		if(n!=null){
			mi_vuelta.setNombre_amigable(n);
		}else{
			if(this.nombre_amigable.length()<=23){
				mi_vuelta.setNombre_amigable(this.nombre_amigable+"_vuelta");
			}else if(this.nombre_amigable.length()<=28){
				mi_vuelta.setNombre_amigable(this.nombre_amigable+"_V");
			}else{
				mi_vuelta.setNombre_amigable(this.nombre_amigable);
			}	
		}
		Timestamp f= (Timestamp) vuelta.get("fecha_inicio");
		if(f!=null){
			mi_vuelta.setFecha_inicio(f);
		}else{
			throw new ExceptionViajesCompartidos("ERROR: FALTA LA FECHA DE INICIO DEL VIAJE DE VUELTA");
		}
		mi_vuelta.setFecha_alta(new Timestamp((new java.util.Date()).getTime()));
		// le pongo al viaje los datos propios que se repiten
		
		mi_vuelta.setConductor_vehiculo(this.getConductor_vehiculo());
		mi_vuelta.setEstado(EstadoViaje.no_iniciado);
		mi_vuelta.setFecha_cancelacion(null);
		mi_vuelta.setFecha_finalizacion(null);
		mi_vuelta.setPrecio(this.precio);
		//hago la relacion con sigo mismo
		this.setViaje_complementario(mi_vuelta);
		mi_vuelta.setViaje_complementario(this);
		
		//hago el recorrido invertido, para eso creo la nueva lista, y recorro la viaje de atras para adelante
		//agregando las localidades
		ArrayList<Localidad> nuevo_recorrido= new ArrayList<Localidad>();
		int index=this.localidades.size();
		index--;
		for(;index>=0;index--){
			nuevo_recorrido.add(this.localidades.get(index).getLocalidad());
		}
		mi_vuelta.crearRecorrido(nuevo_recorrido);
		
		//agrego los valores de KM de que tengo en el viaje y se los pongo a la vuelta, en sentido inverso
		List<LocalidadViaje> lista_localidad_viaje=mi_vuelta.getLocalidades();
		int j=0;
		for(int i=(lista_localidad_viaje.size()-1);i>=0;i--){
			lista_localidad_viaje.get(j).setKms_a_localidad_siguiente( this.localidades.get(i).getKms_a_localidad_siguiente() );
			j++;
		}
		Integer ultimo=lista_localidad_viaje.size();
		//lista_localidad_viaje.get(ultimo-1).setKms_a_localidad_siguiente(0.0);		//a la ultima localidadViaje le pongo distancia 0
		
		return mi_vuelta;
		
	}
	
	//by mufa
	public boolean contiene_localidades_en_orden(Localidad primer_localidad, Localidad segunda_localidad) {
		//si el primero == segundo, va a decir q estan en orden
		boolean encontre_primero=false;
		Integer cantidad_iteraciones= this.localidades.size();
		Integer index=0;
		
		while(index<cantidad_iteraciones){
			if( this.localidades.get(index).getLocalidad().getId() == primer_localidad.getId() ){
				//cuando encuentro el primero seteo la bandera
				encontre_primero=true;
			}else{
				if( this.localidades.get(index).getLocalidad().getId() == segunda_localidad.getId() ){
					//cuando encuentro el segundo pregunte si ya habia encontrado al primer
					if(encontre_primero){
						//si lo habia encontrado al primero significa q estan en orden
						return true;
					}else{
						//si no lo habia encontrado, entonces encontre al segundo antes, y no estan en orden
						return false;
					}
				}
			}
			
			index++;
		}
		return false;
	}

}

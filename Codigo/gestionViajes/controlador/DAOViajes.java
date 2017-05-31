package gestionViajes.controlador;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gestionComisiones.controlador.DAOComisiones;
import gestionComisiones.modelo.Comision;
import gestionComisiones.modelo.ComisionCobrada;
import gestionComisiones.modelo.EstadoComisionCobrada;
import gestionPuntos.controlador.DAOPuntos;
import gestionPuntos.modelo.Calificacion;
import gestionUsuarios.controlador.DAOAdministracionUsuarios;
import gestionUsuarios.controlador.DAONotificaciones;
import gestionUsuarios.modelo.*;
import gestionViajes.modelo.*;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import javax.persistence.Query;
import javax.persistence.RollbackException;

import otros.DataAccesObject;
import otros.ExceptionViajesCompartidos;
import otros.ManejadorErrores;

/*import com.google.maps.GeoApiContext;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;*/

public class DAOViajes extends DataAccesObject {
	static PlanificadorEstadoViaje planifEstadoViaje = null;
	static DAOViajes instance = null;

    public DAOViajes(){		//lo cambio para poder hacer una prueba(Lucas)
    	super();
    	
    	synchronized (DAOViajes.class) {
			if (DAOViajes.planifEstadoViaje == null) {
				DAOViajes.planifEstadoViaje = new PlanificadorEstadoViaje (this);
				DAOViajes.planifEstadoViaje.iniciar();
			}
		}
		
	}

	public static DAOViajes getInstance () {
		if(DAOViajes.instance == null) {
			DAOViajes.instance = new DAOViajes();
		}
		return DAOViajes.instance;
	}
    
    //by mufa
    public synchronized boolean NuevoVehiculo(JSONObject datos) throws ExceptionViajesCompartidos{	//tiene tests
    	
    	/* estructura del JSON datos:
    	 * { "CONDUCTOR": ID_USUARIO,
    	 * "VEHICULO":{FOTO,MARCA,MODELO, PATENTE,ANIO,AIRE_ACOND,SEGURO,foto,seguro, aire, asientos}
    	 * }
    	 */
    	
    	Integer id_cliente=(Integer) datos.get("conductor");
    	Cliente cliente=(Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
    	if(cliente==null){
    		throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE!");
    	}
    	JSONObject datos_vehiculo= (JSONObject) datos.get("vehiculo");
    	String patente= (String) datos_vehiculo.get("patente");
    	Vehiculo vehiculo = (Vehiculo) this.buscarPorClaveCandidata("Vehiculo", patente);
    	if(vehiculo!=null){
    		throw new ExceptionViajesCompartidos("ERROR: EXISTE UN VEHICULO CON ESA PATENTE");
    	}
    	
		this.iniciarTransaccion();
    	vehiculo = new Vehiculo();
    	Integer anio= (Integer)datos_vehiculo.get("anio");
    	if(anio==null){
    		throw new ExceptionViajesCompartidos("ERROR: DEBE INGRESAR EL AÑO DEL VEHICULO");
    	}else{
    		System.out.println(Calendar.getInstance().get(Calendar.YEAR));
    		if(anio>(Calendar.getInstance().get(Calendar.YEAR))){
    			throw new ExceptionViajesCompartidos("ERROR: El AÑO DEL VEHICULO NO PUEDE SER MAYOR AL AÑO ACTUAL");
    		}
    	}
    	vehiculo.setAnio(anio);
    	vehiculo.setFecha_verificacion(null);
    	//para todos los string uso la misma variable para ahorrar :P
    	String dato= (String)datos_vehiculo.get("marca");
    	if(dato==null){
    		throw new ExceptionViajesCompartidos("ERROR: DEBE COMPLETAR EL CAMPO 'MARCA'");
    	}
    	vehiculo.setMarca( dato );
    	dato = (String)datos_vehiculo.get("modelo") ;
    	if(dato==null){
    		throw new ExceptionViajesCompartidos("ERROR: DEBE COMPLETAR EL CAMPO 'MARCA'");
    	}
    	vehiculo.setModelo( dato );
    	dato = (String)datos_vehiculo.get("patente") ;
    	if(dato==null){
    		throw new ExceptionViajesCompartidos("ERROR: DEBE COMPLETAR EL CAMPO 'MARCA'");
    	}
    	vehiculo.setPatente(dato);
    	vehiculo.setAire_acondicionado( (Character) datos_vehiculo.get("aire"));
    	vehiculo.setColor((String) datos_vehiculo.get("color"));
    	Integer asientos=(Integer) datos_vehiculo.get("asientos");
    	if(asientos==null){
    		throw new ExceptionViajesCompartidos("ERROR: DEBE COMPLETAR EL CAMPO 'cantidad de asientos'");
    	}else{
    		if(asientos<=0){
    			throw new ExceptionViajesCompartidos("ERROR: EL VEHICULO DEBE TENER UN ASIENTOS DISPONIBLE COMO MINIMO");
    		}
    	}
    	vehiculo.setCantidad_asientos(asientos);
    	vehiculo.setSeguro( (Character) datos_vehiculo.get("seguro"));
    	String foto = (String) datos_vehiculo.get("foto");
    	if (foto != null) {
    		vehiculo.setFoto(foto);
    	}
    	
    	vehiculo.setEstado('A');
    	vehiculo.setVerificado('N');
    	
    	cliente.asignarVehiculo(vehiculo); 
    	try{
    		entitymanager.getTransaction( ).commit( );	
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
    	
    	return true;
    }
	
	public Vehiculo getAutoViaje(Integer id_viaje) {
                //agregado de fede
                Cliente conductor = new Cliente();
                Viaje v = new Viaje();
                Vehiculo veh = new Vehiculo();
                Query qry = entitymanager.createNamedQuery("Viaje.SearchById");
    		qry.setParameter("id_viaje", id_viaje);
    		v =(Viaje)qry.getSingleResult();
                Maneja conductor_maneja = v.getConductor_vehiculo();
                veh= conductor_maneja.getVehiculo();
                
		return veh;
                //fin agregado fede
		
	}
	
    //by juan
	//practicamente un copiar y pegar de nuevoViaje
	public synchronized void modificarViaje(JSONObject datos) throws ExceptionViajesCompartidos {
		JSONObject viajeJson= (JSONObject) datos.get("viaje");
		Integer id_cliente= (Integer) datos.get("cliente");
		Cliente cliente= (Cliente)this.buscarPorPrimaryKey(new Cliente(), id_cliente);
		if (cliente==null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
		}
		Integer id_viaje= (Integer) viajeJson.get("id_viaje");
		Viaje viaje= (Viaje)this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if (viaje==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO EXISTE");
		}
		if (viaje.getPasajerosCalificablesComoListCliente().size()>0){
			throw new ExceptionViajesCompartidos("No se permite modificar este viaje ya que existen pasajeros que han sido aceptados");
		}
		String patente = (String) datos.get("vehiculo");
		Vehiculo vehiculo = (Vehiculo) this.buscarPorClaveCandidata("Vehiculo", patente);
		if(vehiculo==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VEHICULO NO EXISTE");
		}
		//METODO QUE VERIFICA SI UN CLIENTE TIENE UNA RELACION ACTUAL CON ESE VEHICULO
		if( cliente.puedeManejar(vehiculo)==false ){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO MANEJA ESE VEHICULO");
		}
		
		this.iniciarTransaccion();
		//pongo quien es el conductor del viaje y en q vehiculo
		Maneja maneja= this.getManejaActivoPorVehiculoConductor(vehiculo, cliente);
		viaje.setConductor_vehiculo(maneja);
		//seteo los otros datos
		Timestamp fecha_inicio= (Timestamp) viajeJson.get("fecha_inicio");
		if(fecha_inicio.before(new Timestamp((new java.util.Date()).getTime()))){
			throw new ExceptionViajesCompartidos("ERROR:LA FECHA DE INICIO NO PUEDE SER ANTERIOR A LA ACTUAL");
		}
		if(fecha_inicio==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO TIENE FECHA DE INICIO");
		}
		viaje.setFecha_inicio(fecha_inicio);
		viaje.setFecha_alta(new Timestamp((new java.util.Date()).getTime()));
		viaje.setFecha_cancelacion(null);
		viaje.setFecha_finalizacion(null);
		
		Integer cantidad_asientos = (Integer) viajeJson.get("cantidad_asientos");
		if(cantidad_asientos==null){
			throw new ExceptionViajesCompartidos("ERROR: NO INGRESO LA CANTIDAD DE ASIENTOS DISPONIBLES");
		}
		if (cantidad_asientos+1>maneja.getVehiculo().getCantidad_asientos()){
			throw new ExceptionViajesCompartidos("ERROR: INGRESO MAS ASIENTOS DISPONIBLES QUE LA CANTIDAD DE ASIENTOS DEL VEHICULO");
		}
		viaje.setAsientos_disponibles(cantidad_asientos);
		String nombre_amigable = (String) viajeJson.get("nombre_amigable");
		if(nombre_amigable==null){
			nombre_amigable="Viaje sin nombre "+fecha_inicio.toString();
		}else if (nombre_amigable.length()>30){
			throw new ExceptionViajesCompartidos("ERROR: NOMBRE DE VIAJE DEMASIADO LARGO");
		}
		viaje.setNombre_amigable(nombre_amigable);

		Float precio=(Float) viajeJson.get("precio");
		if(precio==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO TIENE PRECIO");
		}
		if(precio<0){
			throw new ExceptionViajesCompartidos("ERROR: EL PRECIO NO PUEDE SER NEGATIVO");
		}
		viaje.setPrecio(precio);

		//creo el recorrido (lista de localidades) que tiene el viaje
		// le JSON datos tiene un JSON que se llama localidades, q tiene origen, destino y todos los puntos intermedios
		JSONObject localidades =(JSONObject) datos.get("localidades");
		if(localidades==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTAN TODAS LAS LOCALIDADES DEL VIAJE");
		}
		Integer id_origen = (Integer) localidades.get("origen");
		if(id_origen==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL ORIGEN DEL VIAJE");
		}
		Integer id_destino = (Integer) localidades.get("destino");
		if(id_destino==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL DESTINO DEL VIAJE");
		}
		JSONArray intermedios = (JSONArray) localidades.get("intermedios");
		ArrayList<Localidad> recorrido= new ArrayList<Localidad>();
		//armo la lista de localidades en ORDEN, el ordinal lo pone el viaje
		Localidad origen= (Localidad) this.buscarPorPrimaryKey(new Localidad(), id_origen);
		if(origen==null){
			throw new ExceptionViajesCompartidos("ERROR: EL ORIGEN NO EXISTE EN EL SISTEMA");
		}
		recorrido.add(origen);
		if(intermedios!=null){
			for(Object o: intermedios){
				Localidad l =(Localidad) this.buscarPorPrimaryKey(new Localidad(), o);
				if(l==null){
					throw new ExceptionViajesCompartidos("ERROR: UNO DE LOS PUNTOS INTERMEDIOS INGRESADOS NO EXISTE EN EL SISTEMA");
				}
				recorrido.add( l );			
			}
		}
		Localidad destino=(Localidad) this.buscarPorPrimaryKey(new Localidad(), id_destino); //cambio el origen por el destino, mantengo la variable
		if(destino==null){
			throw new ExceptionViajesCompartidos("ERROR: EL DESTINO NO EXISTE EN EL SISTEMA");
		}
		recorrido.add(destino);

		//Verifico si el nuevo recorrido dado es igual al anterior
		boolean esMismoRecorrido = true;
		List<LocalidadViaje> viejasLoc = viaje.getLocalidades();
		for (LocalidadViaje viejaLoc : viejasLoc){
			int ordinal= viejaLoc.getOrdinal();
			esMismoRecorrido = ordinal<=recorrido.size() 
					&& viejaLoc.getLocalidad().getId() == recorrido.get(ordinal-1).getId();
			if (!esMismoRecorrido) break;			
		}
		
		if (!esMismoRecorrido){
			//Hay pasajeros de cualquier tipo relacionado con localidadviaje?
			boolean hayPasajeros = viaje.getPasajeros().size()>0;
			if (hayPasajeros){
				throw new ExceptionViajesCompartidos("Usted ya no tiene permitido modificar el recorrido de este viaje: existen clientes asociados al viaje.");
			}
			
			// No hay pasajeros entonces puedo eliminar el anterior recorrido sin que estalle la bd 
			//borro anterior recorrido
			for (LocalidadViaje lv : viejasLoc){
				this.entitymanager.merge(lv);
				this.entitymanager.remove(lv);
			}
			viaje.setLocalidades(new ArrayList<LocalidadViaje>());
			
			try{
	    		this.entitymanager.getTransaction( ).commit( );	
	    		this.planifEstadoViaje.nuevaTarea (new TareaIniciarViaje (this, viaje));
	    	}catch(RollbackException e){
	    		String error= ManejadorErrores.parsearRollback(e);
	    		throw new ExceptionViajesCompartidos("ERROR: "+error);
	    	}
			//
			//creo recorrido nuevo
			iniciarTransaccion();

			viaje.crearRecorrido(recorrido);
			
			List<LocalidadViaje> lista_localidad_viaje=viaje.getLocalidades();
			for(int i=0;i<(lista_localidad_viaje.size()-1);i++){
				Double distancia = this.distanciaEntreLocalidades(lista_localidad_viaje.get(i).getLocalidad(),lista_localidad_viaje.get(i+1).getLocalidad());
		        lista_localidad_viaje.get(i).setKms_a_localidad_siguiente(distancia);
			}
			Integer ultimo=lista_localidad_viaje.size();
			lista_localidad_viaje.get(ultimo-1).setKms_a_localidad_siguiente(0.0);		//a la ultima localidadViaje le pongo distancia 0
		}

		try{
		this.entitymanager.getTransaction( ).commit( );
		this.planifEstadoViaje.nuevaTarea (new TareaIniciarViaje (this, viaje));
		this.notificarSeguidores(viaje.getId_viaje(), "modificado");
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
	}
	
	//by mufa
	@SuppressWarnings("unused")
	public synchronized boolean nuevoViaje(JSONObject datos) throws ExceptionViajesCompartidos {		//tiene tests
		/*
		EL JSON QUE RECIBE EL METODO TENDRIA LA SIGUIENTE FORMA:
		 { "LOCALIDADES": {"ORIGEN":"ID_LOCALIDAD","INTERMEDIO":ID_LOCALIDAD,.....,"DESTINO":ID_LOCALIDAD},
		 "VEHICULO": ID_VEHICULO,
		 "VIAJE": {FECHA_SALIDA, NOMBRE_AMIGABLE, COSTO_VIAJE},
		 "VUELTA": {FECHA_SALIDA},
		 "CLIENTE":ID_CLIENTE,
		 "cantidad_asientos"=asientos,
		 }
		*/
		JSONObject d_viaje= (JSONObject) datos.get("viaje");
		Integer id_cliente= (Integer) datos.get("cliente");
		Cliente cliente= (Cliente)this.buscarPorPrimaryKey(new Cliente(), id_cliente);
		if (cliente==null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
		}
		//Por las dudas, para que refresque el saldo
		this.refresh(cliente);
		String id_vehiculo = (String) datos.get("vehiculo");
		Vehiculo vehiculo = (Vehiculo) this.buscarPorClaveCandidata("Vehiculo", id_vehiculo);
		if(vehiculo==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VEHICULO NO EXISTE");
		}
		//METODO QUE VERIFICA SI UN CLIENTE TIENE UNA RELACION ACTUAL CON ESE VEHICULO
		if( cliente.puedeManejar(vehiculo)==false ){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO MANEJA ESE VEHICULO");
		}
		
		this.iniciarTransaccion();
		//pongo quien es el conductor del viaje y en q vehiculo
		Maneja maneja= this.getManejaActivoPorVehiculoConductor(vehiculo, cliente);
		Viaje viaje= new Viaje();
		viaje.setConductor_vehiculo(maneja);
		//seteo los otros datos
		Timestamp fecha_inicio= (Timestamp) d_viaje.get("fecha_inicio");
		if(fecha_inicio.before(new Timestamp((new java.util.Date()).getTime()))){
			throw new ExceptionViajesCompartidos("ERROR:LA FECHA DE INICIO NO PUEDE SER ANTERIOR A LA ACTUAL");
		}
		if(fecha_inicio==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO TIENE FECHA DE INICIO");
		}
		viaje.setFecha_inicio(fecha_inicio);
		viaje.setFecha_alta(new Timestamp((new java.util.Date()).getTime()));
		viaje.setFecha_cancelacion(null);
		viaje.setFecha_finalizacion(null);
		viaje.setEstado(EstadoViaje.no_iniciado);
		
		Integer cantidad_asientos = (Integer) d_viaje.get("cantidad_asientos");
		if(cantidad_asientos==null){
			throw new ExceptionViajesCompartidos("ERROR: NO INGRESO LA CANTIDAD DE ASIENTOS DISPONIBLES");
		}
		if (cantidad_asientos+1>maneja.getVehiculo().getCantidad_asientos()){
			throw new ExceptionViajesCompartidos("ERROR: INGRESO MAS ASIENTOS DISPONIBLES QUE LA CANTIDAD DE ASIENTOS DEL VEHICULO");
		}
		viaje.setAsientos_disponibles(cantidad_asientos);
		String nombre_amigable = (String) d_viaje.get("nombre_amigable");
		if(nombre_amigable==null){
			nombre_amigable="Viaje sin nombre "+fecha_inicio.toString();
		}else{
			if(nombre_amigable.length()>30){
				throw new ExceptionViajesCompartidos("ERROR: EL NOMBRE DEL VIAJE NO PUEDE TENER MAS DE 30 CARACTERES");
			}
		}
		viaje.setNombre_amigable(nombre_amigable);
		
		Float precio=(Float) d_viaje.get("precio");
		if(precio==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO TIENE PRECIO");
		}
		if(precio<0){
			throw new ExceptionViajesCompartidos("ERROR: EL PRECIO NO PUEDE SER NEGATIVO");
		}
		viaje.setPrecio(precio);
		
		// chequeo los datos de la vuelta
		Timestamp fecha_vuelta = null;
		Integer cantidad_asientos_vuelta=0;
		Float precio_vuelta=0f;
		
		JSONObject vuelta=(JSONObject) datos.get("vuelta");
		if(vuelta!=null){
			fecha_vuelta= (Timestamp) vuelta.get("fecha_inicio");
			if(fecha_vuelta.before(fecha_inicio)){
				throw new ExceptionViajesCompartidos("ERROR:LA FECHA DE INICIO DEL VIAJE DE VUELTA NO PUEDE SER ANTERIOR A LA FECHA DEL VIAJE DE IDA");
			}
			if(fecha_vuelta==null){
				throw new ExceptionViajesCompartidos("ERROR: EL VIAJE DE VUELTA NO TIENE FECHA DE INICIO");
			}
			cantidad_asientos_vuelta = (Integer) vuelta.get("cantidad_asientos");
			if(cantidad_asientos_vuelta==null){
				throw new ExceptionViajesCompartidos("ERROR: NO INGRESO LA CANTIDAD DE ASIENTOS DISPONIBLES PARA LA VUELTA");
			}

			if (cantidad_asientos_vuelta>maneja.getVehiculo().getCantidad_asientos()){
				throw new ExceptionViajesCompartidos("ERROR: INGRESO MAS ASIENTOS DISPONIBLES QUE LA CANTIDAD DE ASIENTOS DEL VEHICULO PARA LA VUELTA");
			}
			precio_vuelta=(Float) vuelta.get("precio");
			if(precio_vuelta == null) {
				throw new ExceptionViajesCompartidos("ERROR: NO INGRESO PRECIO PARA LA VUELTA");
			}
		}
		
		//creo el recorrido (lista de localidades) que tiene el viaje
		// le JSON datos tiene un JSON que se llama localidades, q tiene origen, destino y todos los puntos intermedios
		JSONObject localidades =(JSONObject) datos.get("localidades");
		if(localidades==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTAN TODAS LAS LOCALIDADES DEL VIAJE");
		}
		Integer id_origen = (Integer) localidades.get("origen");
		if(id_origen==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL ORIGEN DEL VIAJE");
		}
		Integer id_destino = (Integer) localidades.get("destino");
		if(id_destino==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL DESTINO DEL VIAJE");
		}
		JSONArray intermedios = (JSONArray) localidades.get("intermedios");
		ArrayList<Localidad> recorrido= new ArrayList<Localidad>();
		//armo la lista de localidades en ORDEN, el ordinal lo pone el viaje
		Localidad origen= (Localidad) this.buscarPorPrimaryKey(new Localidad(), id_origen);
		if(origen==null){
			throw new ExceptionViajesCompartidos("ERROR: EL ORIGEN NO EXISTE EN EL SISTEMA");
		}
		recorrido.add(origen);
		if(intermedios!=null){
			for(Object o: intermedios){
				origen =(Localidad) this.buscarPorPrimaryKey(new Localidad(), o);
				if(origen==null){
					throw new ExceptionViajesCompartidos("ERROR: UN PUNTO INTERMEDIO NO EXISTE EN EL SISTEMA (NOSE CUAL :-( )");
				}
				recorrido.add( origen );			
			}
		}
		origen=(Localidad) this.buscarPorPrimaryKey(new Localidad(), id_destino); //cambio el origen por el destino, mantengo la variable
		if(origen==null){
			throw new ExceptionViajesCompartidos("ERROR: EL DESTINO NO EXISTE EN EL SISTEMA");
		}
		recorrido.add(origen);
		viaje.crearRecorrido(recorrido);
		
		List<LocalidadViaje> lista_localidad_viaje=viaje.getLocalidades();
		for(int i=0;i<(lista_localidad_viaje.size()-1);i++){
			Double distancia = this.distanciaEntreLocalidades(lista_localidad_viaje.get(i).getLocalidad(),lista_localidad_viaje.get(i+1).getLocalidad()); 
            lista_localidad_viaje.get(i).setKms_a_localidad_siguiente(distancia);
		}
		Integer ultimo=lista_localidad_viaje.size();
		lista_localidad_viaje.get(ultimo-1).setKms_a_localidad_siguiente(0.0);		//a la ultima localidadViaje le pongo distancia 0
		/*
		//TODO verificar que el cliente tenga salgo para hacer crear el viaje
		//calculo saldo necesario (by fede)
		Double distancia_origen_primerpunto = lista_localidad_viaje.get(0).getKms_a_localidad_siguiente() ;
		DAOComisiones daocomisiones = DAOComisiones.getInstance();
		ComisionCobrada cc = daocomisiones.nuevaComisionCobrada(distancia_origen_primerpunto);
		float saldo_necesario = cc.getMonto();
		float saldo_cliente = cliente.getSaldo();
                //daocomisiones.cerrarConexiones();		//habia que cerrar el dao. Ahora ya no, el dao es singleton
                daocomisiones=null;
		if(saldo_necesario>saldo_cliente){
			throw new ExceptionViajesCompartidos("ERROR: USTED NO TIENE SALDO SUFICIENTE PARA CREAR EL VIAJE)");
		}
		   //fin saldo
		   */ //comentado lo de saldo
                double distancia_origen_destino = this.distanciaEntreLocalidades(viaje.getOrigen(), viaje.getDestino());
                this.autoTieneViaje(maneja, viaje.getFecha_inicio(), distancia_origen_destino);
             
		//si el viaje tiene marcado que es de ida y vuelta, le digo al viaje q cree la vuelta y le paso los datos de la misma
		vuelta=(JSONObject) datos.get("vuelta");
		if(vuelta!=null){ 
			
			try{	//SI EL VIAJE TIENE VUELTA, GUARDO EL PRIMER VIAJE EN LA BD
				this.entitymanager.persist(viaje);
	    		entitymanager.getTransaction( ).commit( );	
				this.planifEstadoViaje.nuevaTarea(new TareaIniciarViaje(this, viaje));
	    	}catch(RollbackException e){
	    		String error= ManejadorErrores.parsearRollback(e);
	    		throw new ExceptionViajesCompartidos("ERROR: "+error);
	    	}
			//HAGO OTRA TRANSACCION, Y AHI LE DIGO AL VIAJE Q CREE SU VUELTA, Y LO GUARDO EN LA BD (EL NUEVO VIAJE SE GUARDA POR PERSIST EN CASCADA)
			iniciarTransaccion();
			Viaje viaje_vuelta = viaje.crearTuVuelta(vuelta);
			viaje_vuelta.setPrecio(precio_vuelta);
			viaje_vuelta.setAsientos_disponibles(cantidad_asientos_vuelta);
			try{
	    		entitymanager.getTransaction( ).commit( );	
				this.planifEstadoViaje.nuevaTarea(new TareaIniciarViaje(this,viaje_vuelta));
	    	}catch(RollbackException e){
	    		String error= ManejadorErrores.parsearRollback(e);
	    		throw new ExceptionViajesCompartidos("ERROR: "+error);
	    	}
		}else{		//SI EL VIAJE NO TIENE VUELTA, LA SETEO COMO NULA, Y GUARDO EL VIAJE
			viaje.setViaje_complementario(null);
			this.entitymanager.persist(viaje);
			try{
	    		entitymanager.getTransaction( ).commit( );	
				this.planifEstadoViaje.nuevaTarea(new TareaIniciarViaje(this, viaje));
	    	}catch(RollbackException e){
	    		String error= ManejadorErrores.parsearRollback(e);
	    		throw new ExceptionViajesCompartidos("ERROR: "+error);
	    	}
		}
		return true;
	}
	
	//by pablo
	protected Double distanciaEntreLocalidades(Localidad localidad1, Localidad localidad2){
	/* 
		GeoApiContext context = new GeoApiContext ();

		// La apikey no deberia estar hardcodeada
		//juan: creo que en java se guardan en un objeto property o algo por el estilo
		//en node.js yo lo metia en un archivo txt y lo cargaba con un modulo standard pero en Java ni idea
		String claveJuan = "AIzaSyC83dfqf-uzt4fyBckfpNa51sToN6t7kZA";
		String clavePablo = "AIzaSyCu2P6zmQmOyESf872DSdZgYam9PMJnzwg";
		context.setApiKey(clavePablo);

		// TODO: permitir configurar proxy 
		//context.setProxy(proxy)

		LatLng origen = new LatLng(localidad1.getLatitud(), localidad1.getLongitud());
		LatLng destino = new LatLng(localidad2.getLatitud(), localidad2.getLongitud());

		DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest (context);
		request.origins(origen);
		request.destinations(destino);
		request.mode(TravelMode.DRIVING);

		long mts = -1;
		
		try {
			long distance;
			DistanceMatrix result = request.await();
			for (DistanceMatrixElement element: result.rows[0].elements) {
					distance = element.distance.inMeters;
				if ((element.status == DistanceMatrixElementStatus.OK) && (mts == -1 || distance < mts)) {
					mts = distance;
				}
			}
		} catch (Exception e) {
			//throw new ExceptionViajesCompartidos ("No se pudo calcular la distancia entre las localidades");
		}

		if (mts == -1) {
			//throw new ExceptionViajesCompartidos ("No se pudo calcular la distancia entre las localidades");
		}

		double kms = (double) mts / 1000d;
		return kms;
		*/
		return this.distanceCalculation(localidad1.getLatitud(), localidad1.getLongitud(), localidad2.getLatitud(), localidad2.getLongitud());
	}
	
	//by pablo
	protected double distanceCalculation(double point1_lat, double point1_long, double point2_lat, double point2_long) {
	/*
	DescripciÃ³n: CÃ¡lculo de la distancia entre 2 puntos en funciÃ³n de su latitud/longitud
	Autor: Rajesh Singh (2014)
	Sito web: AssemblySys.com
	*/
	// CÃ¡lculo de la distancia en grados
	double degrees = Math.toDegrees(Math.acos((Math.sin(Math.toRadians(point1_lat))*Math.sin(Math.toRadians(point2_lat))) + (Math.cos(Math.toRadians(point1_lat))*Math.cos(Math.toRadians(point2_lat))*Math.cos(Math.toRadians(point1_long-point2_long)))));
 
	double distance = degrees * 111.13384; // 1 grado = 111.13384 km, basÃ¡ndose en el diametro promedio de la Tierra (12.735 km)
	return distance;
}
	
	//by mufa
	public Cliente getConductorViaje(Integer id_viaje){		//tiene test
		Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(viaje==null){
			return null;		// o puede tirar una exception
		}else{
			return viaje.getConductor_vehiculo().getCliente();
		}	
	}
	
	//by mufa
	public Vehiculo getVehiculoViaje(Integer id_viaje){		//tiene test
		Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(viaje==null){
			return null;		// o puede tirar una exception
		}else{
			return viaje.getConductor_vehiculo().getVehiculo();
		}
	}
	
	//by mufa
	public synchronized boolean Cliente_se_postula_en_viaje(JSONObject json) throws ExceptionViajesCompartidos{// tiene test
		/*
		 * JSON{
		 * "CLIENTE":ID_CLIENTE,
		 * "VIAJE":ID_VIAJE,
		 * "LOCALIDAD_SUBIDA":ID_LOCALIDAD,
		 * "LOCALIDAD_BAJADA": ID_LOCALIDAD,
		 * "NRO_ASIENTOS": integer
		 * } 
		 */
		
		Integer id_cliente= (Integer) json.get("cliente");
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
		if(cliente==null){
			throw new ExceptionViajesCompartidos("ERROR: CLIENTE NO ENCONTRADO");
		}
		Integer id_viaje = (Integer) json.get("viaje");
		Viaje viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(viaje==null){
			throw new ExceptionViajesCompartidos("ERROR: VIAJE NO ENCONTRADO");
		}
			
		Integer id_subida= (Integer) json.get("localidad_subida");
		Localidad localidad_subida= (Localidad) this.buscarPorPrimaryKey(new Localidad(), id_subida);
		if(localidad_subida==null){
			throw new ExceptionViajesCompartidos("ERROR: LA LOCALIDAD DE SUBIDA NO EXISTE");
		}
		Integer id_bajada= (Integer) json.get("localidad_bajada");
		Localidad localidad_bajada= (Localidad) this.buscarPorPrimaryKey(new Localidad(), id_bajada);
		if(localidad_bajada==null){
			throw new ExceptionViajesCompartidos("ERROR: LA LOCALIDAD DE BAJADA NO EXISTE");
		}
		if(viaje.getEstado()==EstadoViaje.cancelado || viaje.getEstado()==EstadoViaje.finalizado){
			throw new ExceptionViajesCompartidos("ERROR: NO PUEDE POSTULARSE A UN VIAJE FINALIZADO/CANCELADO");
		}
		if( cliente.getId_usuario()==viaje.getConductor_vehiculo().getCliente().getId_usuario() ){
			throw new ExceptionViajesCompartidos("ERROR: NO PUEDES UNIRTE COMO PASAJERO A UN VIAJE QUE VOS MISMO CREASTE");
		}
		if (!viaje.contiene_localidades_en_orden(localidad_subida,localidad_bajada)){
			throw new ExceptionViajesCompartidos("ERROR: LA LOCALIDAD DE SUBIDA ESTA DESPUES QUE LA DE BAJADA");
		}
		
		this.iniciarTransaccion();
	
		PasajeroViaje pasajero=(PasajeroViaje) viaje.recuperar_pasajeroViaje_por_cliente(cliente);
		//si el pasajero no estaba en el viaje lo creo, si estaba lo modifico a menos q este en aceptado/finalizo/ausente
		if (pasajero == null){
			pasajero = new PasajeroViaje();
		}else{
			if(pasajero.getEstado()==EstadoPasajeroViaje.aceptado){
				throw new ExceptionViajesCompartidos("ERROR: YA ESTAS ACEPTADO EN EL VIAJE");
			}
			if(pasajero.getEstado()==EstadoPasajeroViaje.finalizo_viaje){
				throw new ExceptionViajesCompartidos("ERROR: YA FINALIZASTE EL VIAJE!");
			}
			if(pasajero.getEstado()==EstadoPasajeroViaje.ausente){
				throw new ExceptionViajesCompartidos("ERROR: NO PARTICIPASTE DEL VIAJE! (FUISTE CALIFICADO COMO AUSENTE)");
			}
		}
		
		pasajero.setCalificacion(null);
		pasajero.setCliente(cliente);
		pasajero.setEstado(EstadoPasajeroViaje.postulado);
		Integer nro_asientos =(Integer) json.get("nro_asientos");
		if(nro_asientos==null){
			nro_asientos=1;
		}else{
			if(nro_asientos<1 || nro_asientos>viaje.getAsientos_disponibles()){
				throw new ExceptionViajesCompartidos("ERROR: LA CANTIDAD DE ASIENTOS NO PUEDE SER MAYOR A LA DEL VEHICULO");
			}
		}
		pasajero.setNro_asientos(nro_asientos);
		
		//Double km = viaje.calcularKM(localidad_subida,localidad_bajada);
		Double km = this.distanciaEntreLocalidades(localidad_subida, localidad_bajada);
		pasajero.setKilometros(km);
		pasajero.setComision(null);

		viaje.aniadir_pasajeroViaje(pasajero, localidad_subida, localidad_bajada);
		
		DAOComisiones daocomision= DAOComisiones.getInstance();
		ComisionCobrada comisionCobrada =this.entitymanager.merge( daocomision.nuevaComisionCobrada(km) );	
		//daocomision.cerrarConexiones(); El dao ahora es singleton
		daocomision=null;
		
		if(comisionCobrada==null){
			throw new ExceptionViajesCompartidos("ERROR: NO SE PUDO RECUPERAR LA COMISION A COBRAR");
		}
		Float monto_nuevo = (comisionCobrada.getMonto()) * ((Integer)(pasajero.getNro_asientos())).floatValue();
                comisionCobrada.setMonto(monto_nuevo);
		comisionCobrada.setMovimiento_saldo(null);
		comisionCobrada.setPasajero_viaje(null);
		comisionCobrada.setEstado(EstadoComisionCobrada.informativa);		//significa que se guarda solo para saber cuanto le dijimos q le ibamos a cobrar cuando se postulo
		comisionCobrada.setfecha(new Timestamp((new java.util.Date()).getTime()));
		//le asigno la comision al pasajero
		pasajero.setComision(comisionCobrada);
		this.entitymanager.persist(comisionCobrada);
		
		this.entitymanager.persist(pasajero);
		try{
    		entitymanager.getTransaction( ).commit( );	
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
		//hago un guardado anterior por que no puedo vincular doblemente al pasajero con la calificacion y a la calificacion con el pasajero
		this.iniciarTransaccion();
		
		//creo la notificacion que le va a llegar al conductor de ese viaje, informandole que tiene un postulante
		Notificacion notificacion= new Notificacion();
		notificacion.setCliente(viaje.getConductor());
		notificacion.setEstado(EstadoNotificacion.no_leido);
		notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) ); 
		notificacion.setTexto("El usuario: "+cliente.getNombre_usuario()+
				" se ha postulado para participar en tu viaje: "+viaje.getNombre_amigable());
		notificacion.setLink("/listado_postulantes.html?id="+viaje.getId_viaje());
		DAONotificaciones daonotif = DAONotificaciones.getInstance();
                daonotif.enviarNotificacionPorMail(notificacion);
                
                this.entitymanager.persist(notificacion);
                
                
                
		//ahora que ya tengo guardado el pasajero en la BD, la calificacion y la comision tiene una entidad a la cual apuntar
		comisionCobrada.setPasajero_viaje(pasajero);
		try{
    		entitymanager.getTransaction( ).commit( );	
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
		return true;
	}

	//by mufa
	public synchronized float comision_por_recorrido(Localidad inicio, Localidad destino, Integer id_viaje) throws ExceptionViajesCompartidos{	//tiene test
		Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(viaje==null){
			throw new ExceptionViajesCompartidos("ERROR: VIAJE NO ENCONTRADO");
		}
		if (!viaje.contiene_localidades_en_orden(inicio, destino) ){
			throw new ExceptionViajesCompartidos("ERROR: LAS LOCALIDADES NO EXISTEN O NO ESTAN EN ORDEN");
		}
		Double km = viaje.calcularKM(inicio,destino);
		DAOComisiones daocomision= DAOComisiones.getInstance();
		ComisionCobrada comisionCobrada = daocomision.nuevaComisionCobrada(km);
		//daocomision.cerrarConexiones(); Esto ya no es necesario, ahora el DAO es singleton
		//daocomision=null;
		return comisionCobrada.getMonto();
	}
	
	public Viaje getViajeById(Integer id_viaje) {
		Viaje viaje;
		viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		return viaje;
	}
	
	//by pablo y lucas
	public List<Viaje> buscarViajes(JSONObject busqueda) throws ExceptionViajesCompartidos{
		//crear query (campos obligatorios: origen, destino,fecha_desde)
		// otros campos: fecha hasta, conductor, estado_viaje
		
		Integer id_origen = (Integer) busqueda.get("origen");
		if(id_origen==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL ORIGEN");
		}
		Localidad origen = (Localidad) this.buscarPorPrimaryKey(new Localidad(), id_origen);
		if(origen==null){
			throw new ExceptionViajesCompartidos("ERROR: El ORIGEN NO SE ENCUENTRA EN EL SISTEMA");
		}
		Integer id_destino = (Integer) busqueda.get("destino");
		if(id_origen==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL DESTINO");
		}
		Localidad destino = (Localidad) this.buscarPorPrimaryKey(new Localidad(), id_destino);
		if(destino==null){
			throw new ExceptionViajesCompartidos("ERROR: El DESTINO NO SE ENCUENTRA EN EL SISTEMA");
		}
		Timestamp fecha_desde = (Timestamp) busqueda.get("fecha_desde");
		if(fecha_desde==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA LA FECHA DESDE");
		}else{
			if(fecha_desde.before( new Timestamp((new java.util.Date()).getTime()) ) ){
				//throw new ExceptionViajesCompartidos("ERROR: FECHA DESDE NO PUEDE SER ANTERIOR A LA FECHA ACTUAL");
			}
		}
		
		Timestamp fecha_hasta =(Timestamp) busqueda.get("fecha_hasta");
		boolean b_fecha_hasta=false;
		if(fecha_hasta!=null){
			if(fecha_hasta.before( new Timestamp((new java.util.Date()).getTime()) ) ){
				throw new ExceptionViajesCompartidos("ERROR: FECHA HASTA NO PUEDE SER ANTERIOR A LA FECHA ACTUAL");
			}
			b_fecha_hasta=true;
		}
		
		String conductor = (String) busqueda.get("conductor");
		boolean b_conductor=false;
		if ( (conductor!=null) && !(conductor.equals("")) ){
			b_conductor=true;
		}
		
		String estado = (String) busqueda.get("estado");
		if(estado==null){
			estado="ambas";
		}
		if( !(estado.equals("ambas") ||estado.equals("iniciado") || estado.equals("no_iniciado")) ){
			//si el estado no es iniciado, ambas o no iniciado tiro error
			throw new ExceptionViajesCompartidos("ERROR: ESTADO DEL VIAJE INCORRECTO (ESTADOS POSIBLES: INICIADO, NO INICIADO, AMBOS)");
		}
		// esta es la query base

		String query="SELECT v from Viaje v "
			+"WHERE :fecha_desde < v.fecha_inicio "
			+"AND EXISTS (SELECT l1 FROM LocalidadViaje l1 WHERE l1.viaje=v AND l1.localidad=:origen "
			+"AND EXISTS (SELECT l2 FROM LocalidadViaje l2 WHERE l2.viaje=v AND l2.localidad=:destino "
			+"AND l1.ordinal < l2.ordinal)) ";
		//ahora le agrego a la query los otros campos de busqueda a medida q los hay
		if(b_fecha_hasta){
			query=query.concat("AND v.fecha_inicio < :fecha_hasta ");
		}
		if(b_conductor){
			//query=query.concat("AND v LIKE :nombre_usuario_conductor ");
		}
		if(estado.equals("ambas")){
			query=query.concat("AND (v.estado=:estado1 OR v.estado=:estado2) ");
		}else{
			query=query.concat("AND v.estado= :estado1");
		}
		
		//armo la query y pongo los parametros minimos
		Query q=this.entitymanager.createQuery(query);
		q.setParameter("fecha_desde", fecha_desde);
		q.setParameter("origen", origen);
		q.setParameter("destino", destino);
		
		//ahora agrego el resto de los parametros
		if(b_fecha_hasta){
			q.setParameter("fecha_hasta",fecha_hasta);
		}
		if(b_conductor){
			//query.setParameter("nombre_usuario_conductor",conductor);
		}
		if(estado.equals("ambas")){
			q.setParameter("estado1", EstadoViaje.iniciado);
			q.setParameter("estado2", EstadoViaje.no_iniciado);
		}else{
			if(estado.equals("iniciado")){
				q.setParameter("estado1", EstadoViaje.iniciado);
			}else{
				q.setParameter("estado1", EstadoViaje.no_iniciado);
			}
		}
		//agregado fede -> si puso conductor solo le devuelvo esos
                 
                 List<Viaje> viajes= q.getResultList();
                 if(b_conductor){
                       
                        DAOAdministracionUsuarios daousr = DAOAdministracionUsuarios.getInstance();
                        Cliente cliente_conductor = (Cliente) daousr.buscarUsuarioPorNombre(conductor);
                        if(cliente_conductor==null){
                            throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
                        }
						for(int i=0;i<viajes.size();i++){
                            if(viajes.get(i).getConductor().getId_usuario()!=cliente_conductor.getId_usuario()){
                                viajes.remove(i);
                            }
                        }
                        //daousr.cerrarConexiones(); El dao ahora es singleton
                        daousr=null;
                        
		}
		
		return viajes;
	}

	//by pablo
	public synchronized boolean actualizarEstadoViaje(Integer id_viaje) {

		boolean actualizado = false;
		Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);

		if (viaje == null) {	//TODO chequeo agregado por lucas, por si entran al detalle viaje desde el url que no explote el metodo
			return false;
		}
		this.iniciarTransaccion();
		actualizado = viaje.actualizarEstado();
		EstadoViaje estadoActual = viaje.getEstado();
		
		if (actualizado && estadoActual == EstadoViaje.iniciado) {		//si le cambie el estado al viaje y lo pase a iniciado
			// Notificar al conductor 
                        System.out.println(viaje.getNombre_amigable());
                        String nombre_destino = viaje.getDestino().getNombre();
                        System.out.println(viaje.getDestino().getNombre());
			Notificacion notificacion = new Notificacion();
			notificacion.setCliente(viaje.getConductor());
			notificacion.setEstado(EstadoNotificacion.no_leido);
			notificacion.setFecha(viaje.getFecha_inicio()); 
			notificacion.setTexto ("Ha llegado el momento de iniciar tu viaje <<" + viaje.getNombre_amigable() + ">> con destino a " + viaje.getDestino().getNombre());
			notificacion.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
			this.entitymanager.persist(notificacion);
			List<PasajeroViaje> pasajeros = viaje.getPasajeros();
			for(PasajeroViaje pasajero: pasajeros) {
				if(pasajero.getEstado() == EstadoPasajeroViaje.aceptado) {
					notificacion = new Notificacion();
					notificacion.setCliente(pasajero.getCliente());
					notificacion.setEstado(EstadoNotificacion.no_leido);
					notificacion.setFecha(new Timestamp((new java.util.Date()).getTime())); 
					notificacion.setTexto ("Ha llegado la hora de inicio del viaje <<" + viaje.getNombre_amigable() + ">> con destino a " + viaje.getDestino().getNombre() + " en el cual esta inscripto como pasajero");
					notificacion.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
					this.entitymanager.persist(notificacion);
                                        DAONotificaciones daonotif = DAONotificaciones.getInstance();
                                        daonotif.enviarNotificacionPorMail(notificacion);
				}
			}
	
		} else if (actualizado && estadoActual == EstadoViaje.finalizado) {		//si le cambie el estado al viaje y lo pase a finalizado
			Notificacion notificacion = new Notificacion();
			notificacion.setCliente(viaje.getConductor());
			notificacion.setEstado(EstadoNotificacion.no_leido);
			notificacion.setFecha(new Timestamp((new java.util.Date()).getTime())); 
			notificacion.setTexto ("Su viaje <<" + viaje.getNombre_amigable() + ">> con destino a " + viaje.getDestino().getNombre()+" se ha marcado como finalizado.");
			notificacion.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
			this.entitymanager.persist(notificacion);
                        DAONotificaciones daonotif = DAONotificaciones.getInstance();
                        daonotif.enviarNotificacionPorMail(notificacion);
		}
		
		this.entitymanager.getTransaction().commit();
		return actualizado;
	}

	public List<PasajeroViaje> listarPasajerosPorViaje(Integer id_viaje) {
		Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		return viaje.getPasajeros();
	}
	
	@SuppressWarnings("unchecked")
	public List<Viaje> listarViajesPorCliente(Integer id_cliente) {
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey (new Cliente(), id_cliente);
		Query qry = entitymanager.createNamedQuery("Viaje.SearchByCliente");
    	qry.setParameter("cliente", cliente);
		return (List<Viaje>)qry.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Viaje> listarViajesPorConductor(Integer id_conductor) {
		Cliente conductor = (Cliente) this.buscarPorPrimaryKey (new Cliente(), id_conductor);
		Query qry = entitymanager.createNamedQuery("Viaje.SearchByConductor");
    	qry.setParameter("conductor", conductor);
		return (List<Viaje>)qry.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Viaje> listarViajesPorPasajero(Integer id_pasajero) {
		Cliente pasajero = (Cliente) this.buscarPorPrimaryKey (new Cliente(), id_pasajero);
		Query qry = entitymanager.createNamedQuery("Viaje.SearchByPasajero");
    	qry.setParameter("pasajero", pasajero);
		return (List<Viaje>)qry.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Cliente> listarConductoresPorVehiculo(Integer idVehiculo) {
		Vehiculo v = (Vehiculo) this.buscarPorPrimaryKey (new Vehiculo(), idVehiculo);
		Query qry = entitymanager.createNamedQuery("Maneja.SearchByVehiculo");
    	qry.setParameter("vehiculo", v);
    	List<Maneja> lista = qry.getResultList();
    	List<Cliente> listaCliente = new ArrayList<Cliente>();;
    	for (Maneja m : lista){
    		if (m.getFecha_fin() == null){
        		listaCliente.add(m.getCliente());
    		}
    	}
		return listaCliente;
	}
          
	//by mufa
	public String nombreAmigablePorViaje(Integer id_viaje) {
		Viaje v=(Viaje)this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(v==null){
			return null;
		}else{
			return  v.getNombre_amigable();
		}
	}

	//by pablo
	public List<Viaje> listarViajesNoIniciadosAtrasados() {
		Query qry = entitymanager.createNamedQuery("Viaje.noIniciadosAtrasados");
		return (List<Viaje>)qry.getResultList();
	}
	
	//by pablo
	public List<Viaje> listarViajesProximosAIniciar(long tiempo) {
		Query qry = entitymanager.createNamedQuery("Viaje.inicianAntes");
		qry.setParameter("tiempo", new Timestamp(new java.util.Date().getTime() + tiempo));
		return (List<Viaje>)qry.getResultList();
	}

	//by pablo
	public List<Viaje> listarViajesNoFinalizadosAtrasados() {
		return listarViajesProximosAFinalizar(0);
	}

	//by pablo
	public List<Viaje> listarViajesProximosAFinalizar(long tiempo){
		Query qry = entitymanager.createNamedQuery("Viaje.IniciadosAntes");
		List<Viaje> resultado_busqueda;
		List<Viaje> atrasados = new ArrayList<Viaje>();
		Timestamp limite = new Timestamp (new java.util.Date().getTime() + tiempo);

		/* Buscamos viajes iniciados hace mas de 4 horas */
		qry.setParameter("tiempo", new Timestamp (new java.util.Date().getTime() - 4*60*60*1000));
		resultado_busqueda=(List<Viaje>)qry.getResultList();
		for (Viaje viaje: resultado_busqueda) {
			if(viaje.getFecha_autofinalizar().compareTo(limite) <= 0) {
				atrasados.add (viaje);
			}
		}
		return atrasados;
	}


	//by jasmin y luz
	public synchronized boolean aceptarPasajero(Integer id_cliente_postulante, Integer id_viaje) throws ExceptionViajesCompartidos {
		Viaje viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if (viaje == null) {
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO EXISTE");
		}
		if(viaje.getEstado() == EstadoViaje.cancelado || viaje.getEstado() == EstadoViaje.finalizado){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE ESTA CANCELADO O YA FINALIZO");
		}
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente_postulante);
		if(cliente == null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
		}
		PasajeroViaje pasajero = viaje.recuperar_pasajeroViaje_por_cliente(cliente);
		if (pasajero == null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO PARTICIPA DE ESE VIAJE");
		}
		if(pasajero.getEstado() != EstadoPasajeroViaje.postulado){
			throw new ExceptionViajesCompartidos("ERROR: SOLO PODES ACEPTAR A UN PASAJERO CUYO ESTADO SEA POSTULADO");
		}
		Integer asientos = viaje.getAsientos_disponibles();
		LocalidadViaje bajada = pasajero.getLocalidad_bajada();
		LocalidadViaje subida = pasajero.getLocalidad_subida();
		List<LocalidadViaje> lista = viaje.getLocalidades();
		Integer i = 0;
		while (lista.get(i) != subida) {//WHILE HASTA QUE ENCUENTRA LA LOCALIDAD DE SUBIDA Y TENGO LA POSICION CON I
			i++;
		}
		while (lista.get(i) != bajada) { //WHILE PARA RECORRER DESDE SUBIDA HASTA QUE SEA BAJADA
			if ((asientos - lista.get(i).getCantidad_pasajeros()) < pasajero.getNro_asientos()) { // SI NO HAY ASIENTOS DISPONIBLES
				//NO SE LO PUEDE ACEPTAR 
				throw new ExceptionViajesCompartidos("ERROR: NO HAY SUFICIENTES ASIENTOS DISPONIBLES PARA EL TRAMO");
			}
			i++;
		} 
		//COMENTO PORQUE CREO QUE COMO EN LA BAJADA NO SE SUMA PASAJERO NO HACE FALTA CHEQUEARLA
		/*if ((asientos - lista.get(i).getCantidad_pasajeros()) == 0) { //SE VERIFICAN LOS ASIENTOS EN LA BAJADA
			//NO SE LO PUEDE ACEPTAR
			throw new ExceptionViajesCompartidos("ERROR: NO HAY SUFICIENTES ASIENTOS DISPONIBLES PARA EL TRAMO");
		}*/ 
		//ACEPTAR
		this.iniciarTransaccion();
		pasajero.setEstado(EstadoPasajeroViaje.aceptado);
		i = 0;
		while (lista.get(i) != subida) {//WHILE HASTA QUE ENCUENTRA LA LOCALIDAD DE SUBIDA Y TENGO LA POSICION CON I
			i++;
		}
		while (lista.get(i) != bajada) { //WHILE PARA RECORRER DESDE SUBIDA HASTA QUE SEA BAJADA
			Integer c=lista.get(i).getCantidad_pasajeros();
			c+=pasajero.getNro_asientos();
			lista.get(i).setCantidad_pasajeros(c);
			i++;
		} 
		
		Calificacion calificacion = (Calificacion) this.buscarPorClaveCandidataCompuesta("Calificacion",pasajero,viaje.getConductor());
		if(calificacion==null){
			calificacion= new Calificacion();
		}
		calificacion.setCalificacion_para_conductor(null);
		calificacion.setCalificacion_para_pasajero(null);
		calificacion.setComentario_conductor(null);
		calificacion.setComentario_pasajero(null);
		calificacion.setParticipo_pasajero(null);
		calificacion.setParticipo_conductor(null);
		calificacion.setPasajero_viaje(pasajero);
		calificacion.setConductor(viaje.getConductor());
		
		pasajero.getComision().setEstado(EstadoComisionCobrada.pendiente);
                pasajero.getComision().setfecha(new Timestamp((new java.util.Date()).getTime()));
		//SE CREA LA NOTIFICACION QUE LE VA A LLEGAR AL PASAJERO, SOBRE QUE FUE ACEPTADO
		Notificacion notificacion= new Notificacion();
		notificacion.setCliente(cliente);
		notificacion.setEstado(EstadoNotificacion.no_leido);
		notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) ); 
		notificacion.setTexto("El conductor: "+ viaje.getConductor().getNombre_usuario() +
				" lo ha aceptado al viaje: "+viaje.getNombre_amigable());
		notificacion.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
		this.entitymanager.persist(notificacion);
                DAONotificaciones daonotif = DAONotificaciones.getInstance();
                daonotif.enviarNotificacionPorMail(notificacion);
		try{
		 	entitymanager.getTransaction( ).commit( );	
		}catch(RollbackException e){
		  	String error= ManejadorErrores.parsearRollback(e);
		 	throw new ExceptionViajesCompartidos("ERROR: "+error);
		}
		
		this.iniciarTransaccion();
		pasajero.setCalificacion(calificacion);
		this.entitymanager.persist(calificacion);
		try{
		 	entitymanager.getTransaction( ).commit( );	
		}catch(RollbackException e){
		  	String error= ManejadorErrores.parsearRollback(e);
		 	throw new ExceptionViajesCompartidos("ERROR: "+error);
		}
        
		//ahora hago que dejar de seguir seguidor del viaje
		SeguidorViaje seguidor = new SeguidorViaje();
		List<SeguidorViaje> lista_seguidores = this.getSeguidoresViaje(id_viaje);
		if(lista_seguidores.size()!=0){ // si el viaje tiene seguidores
			for(int j=0;j<lista_seguidores.size();j++){ //reviso si es seguidor el que acepte
				seguidor = lista_seguidores.get(j);
				if(seguidor.getCliente().getId_usuario() == id_cliente_postulante){
					this.iniciarTransaccion();
					seguidor.setEstado("I".charAt(0));
					this.entitymanager.getTransaction().commit();
				}// deja de ser seguidor
			}
		}
        
		return true;         
	}

	//by mufa
	public synchronized boolean rechazarPasajero(Integer id_cliente_postulante, Integer id_viaje) throws ExceptionViajesCompartidos {
		Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(viaje==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO EXISTE");
		}
		if(viaje.getEstado()==EstadoViaje.cancelado || viaje.getEstado()==EstadoViaje.finalizado){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE ESTA CANCELADO O YA FINALIZO");
		}
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente_postulante);
		if(cliente==null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
		}
		PasajeroViaje pasajero = viaje.recuperar_pasajeroViaje_por_cliente(cliente);
		if (pasajero==null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO PARTICIPA DE ESE VIAJE");
		}
		if(pasajero.getEstado()!=EstadoPasajeroViaje.postulado){	//no podria rechazar a un cliente que ya acepte
			throw new ExceptionViajesCompartidos("ERROR: SOLO PODES RECHAZAR A UN PASAJERO CUYO ESTADO SEA POSTULADO");
		}
		this.iniciarTransaccion();
		pasajero.setEstado(EstadoPasajeroViaje.rechazado);
		pasajero.getComision().setEstado(EstadoComisionCobrada.desestimada);
                pasajero.getComision().setfecha(new Timestamp((new java.util.Date()).getTime())); 
		//SE CREA LA NOTIFICACION QUE LE VA A LLEGAR AL PASAJERO, SOBRE QUE FUE RECHAZADO
		Notificacion notificacion= new Notificacion();
		notificacion.setCliente(cliente);
		notificacion.setEstado(EstadoNotificacion.no_leido);
		notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) ); 
		notificacion.setTexto("El conductor: "+ viaje.getConductor().getNombre_usuario() +
				" ha rechazado su participacion en el viaje: "+viaje.getNombre_amigable());
		notificacion.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
		this.entitymanager.persist(notificacion);
                DAONotificaciones daonotif = DAONotificaciones.getInstance();
                daonotif.enviarNotificacionPorMail(notificacion);
		try{
    		entitymanager.getTransaction( ).commit( );	
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
        
		//ahora hago que dejar de seguir seguidor del viaje
		SeguidorViaje seguidor = new SeguidorViaje();
		List<SeguidorViaje> lista_seguidores = this.getSeguidoresViaje(id_viaje);
		if(lista_seguidores.size()!=0){ // si el viaje tiene seguidores
			for(int j=0;j<lista_seguidores.size();j++){ //reviso si es seguidor el que acepte
				seguidor = lista_seguidores.get(j);
				if(seguidor.getCliente().getId_usuario() == id_cliente_postulante){
					this.iniciarTransaccion();
					seguidor.setEstado("I".charAt(0));
					this.entitymanager.getTransaction().commit();
				}// deja de ser seguidor
			}    
		}
		return true;
	}
	
	// by mufa
	public synchronized boolean clienteNoManejaVehiculo(Integer id_cliente, Integer id_vehiculo) throws ExceptionViajesCompartidos{
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
		if(cliente==null){
			throw new ExceptionViajesCompartidos("ERROR: NO EXISTE EL CLIENTE");
		}
		Vehiculo vehiculo = (Vehiculo) this.buscarPorPrimaryKey(new Vehiculo(), id_vehiculo);
		if(vehiculo==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VEHICULO NO EXISTE");
		}
		Maneja maneja = this.getManejaActivoPorVehiculoConductor(vehiculo, cliente);
		if(maneja==null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO ESTA RELACIONADO CON LE VEHICULO");
		}
		if(maneja.getFecha_fin()!=null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO PODIA MANEJAR EL VEHICULO");
		}
		this.iniciarTransaccion();
		maneja.setFecha_fin( new Timestamp((new java.util.Date()).getTime()) );
		try{
    		entitymanager.getTransaction( ).commit( );	
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
		return true;
	}
	
	//by mufa
	@SuppressWarnings("unchecked")
	public List<Vehiculo> getVehiculosPorCliente(Integer id_cliente) throws ExceptionViajesCompartidos{
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
		if(cliente==null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE EN EL SISTEMA");
		}
		Query q= this.entitymanager.createQuery("SELECT v FROM Vehiculo v JOIN v.conductores maneja"
				+ " JOIN maneja.cliente cliente WHERE maneja.fecha_fin = null AND cliente=:cliente ");
		q.setParameter("cliente", cliente);
		return q.getResultList();
	}
	
	//by mufa
	public List<Localidad> getOrigenYDestinoDeViaje(Integer id_viaje){
		Viaje v=(Viaje)this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(v==null){
			return null;
		}else{
			return  v.recuperarOrigenYDestino();
		}
	}
	
	//by mufa
	public synchronized boolean finalizarViaje(Integer id_cliente, Integer id_viaje) throws ExceptionViajesCompartidos{
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
		if(cliente==null){
			throw new ExceptionViajesCompartidos("ERROR: NO EXISTE EL CLIENTE");
		}
		Viaje viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(viaje==null){
			throw new ExceptionViajesCompartidos("ERROR: NO EXISTE EL VIAJE");
		}
		PasajeroViaje pv= viaje.recuperar_pasajeroViaje_por_cliente(cliente);
		if(pv==null){
			//si el cliente no esta en el viaje, pregunto si es el conductor
			if(viaje.getConductor().getId_usuario()!=cliente.getId_usuario()){
				throw new ExceptionViajesCompartidos("ERROR: NO PARTICIPAS DEL VIAJE!");
			}else{
				//si soy el chofer hago voy a este metodo y termino
				return this.finalizarViaje(viaje);
			}
		}else{
			if(pv.getEstado()!=EstadoPasajeroViaje.aceptado){
				throw new ExceptionViajesCompartidos("ERROR: NO ESTAS ACEPTADO EN EL VIAJE");
			}
		}

		if(viaje.getEstado()!=EstadoViaje.iniciado && viaje.getEstado()!=EstadoViaje.finalizado ){
			throw new ExceptionViajesCompartidos("ERROR: NO PUEDES FINALIZAR TU PARTICIPACION EN UN VIAJE QUE NO ESTA INICIADO O FINALIZADO");
		}
		
		//GUARDO EL CAMBIO DE ESTADO
		this.iniciarTransaccion();
		pv.setEstado(EstadoPasajeroViaje.finalizo_viaje);
		try{
    		entitymanager.getTransaction( ).commit( );	
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
		this.notificarSeguidores(viaje.getId_viaje(), "finalizado");
		return true;
	}

	private synchronized boolean finalizarViaje(Viaje viaje) throws ExceptionViajesCompartidos {
		//entro a este metodo solo si quiero finalizar un viaje y soy el conductor
		//este metodo es llamado por el otro finalizar viaje
		
		this.iniciarTransaccion();
		viaje.setEstado(EstadoViaje.finalizado);
		viaje.setFecha_finalizacion(new Timestamp((new java.util.Date()).getTime()) );
		
		//a todos los pasajeros que no esten aceptados los rechazo
		for(PasajeroViaje pv: viaje.getPasajeros()){
			if(pv.getEstado()==EstadoPasajeroViaje.postulado){
				pv.setEstado(EstadoPasajeroViaje.rechazado);
                pv.getComision().setEstado(EstadoComisionCobrada.desestimada);
                pv.getComision().setfecha(new Timestamp((new java.util.Date()).getTime()));
			}
		}
		try{
    		entitymanager.getTransaction( ).commit( );	
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
		//cobro la comision de cada pasajero
		DAOComisiones daocomisiones = DAOComisiones.getInstance();
		for(PasajeroViaje pv: viaje.getPasajeros()){
			if(pv.getEstado()==EstadoPasajeroViaje.aceptado || pv.getEstado()==EstadoPasajeroViaje.finalizo_viaje){
				daocomisiones.cobrarComision(pv);
			}
		}
		//daocomisiones.cerrarConexiones(); el dao ahora es singleton
		daocomisiones=null;
		return true;
	}       
        
        //by fede
    public synchronized boolean cancelarParticipacionEnViaje(Integer id_viaje,Integer id_cliente ) throws ExceptionViajesCompartidos {
            //Verificaciones varias
        Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(viaje==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO EXISTE");
		}
		if(viaje.getEstado()==EstadoViaje.cancelado || viaje.getEstado()==EstadoViaje.finalizado){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE ESTA CANCELADO O YA FINALIZO");
		}
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
		if(cliente==null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
		}
		PasajeroViaje pasajero = viaje.recuperar_pasajeroViaje_por_cliente(cliente);
		if (pasajero==null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO PARTICIPA DE ESE VIAJE");
		}
		if(pasajero.getEstado()==EstadoPasajeroViaje.cancelado){	//no podria rechazar a un cliente que ya acepte
			throw new ExceptionViajesCompartidos("ERROR: USTED YA CANCELO SU PARTICIPACION EN ESTE VIAJE");
		}
		if(pasajero.getEstado()==EstadoPasajeroViaje.rechazado){	//no podria rechazar a un cliente que ya acepte
			throw new ExceptionViajesCompartidos("ERROR: USTED FUE RECHAZADO POR EL CHOFER");
		}
                //Fin verificaciones. Ahora busco el tramo.
                this.iniciarTransaccion();
                LocalidadViaje subida = pasajero.getLocalidad_subida();
                LocalidadViaje bajada = pasajero.getLocalidad_bajada();
                List<LocalidadViaje> lista = viaje.getLocalidades();
                Integer i = 0;
                
                while (lista.get(i) != subida) {//WHILE HASTA QUE ENCUENTRA LA LOCALIDAD DE SUBIDA Y TENGO LA POSICION CON I
                    i++;
		}
		while (lista.get(i) != bajada) { //WHILE PARA RECORRER DESDE SUBIDA HASTA QUE SEA BAJADA
                    Integer c = lista.get(i).getCantidad_pasajeros();
                    c-=pasajero.getNro_asientos();
                    lista.get(i).setCantidad_pasajeros(c);
                    i++;
		} 
		
		pasajero.setEstado(EstadoPasajeroViaje.cancelado);
		pasajero.getComision().setEstado(EstadoComisionCobrada.desestimada);
                pasajero.getComision().setfecha(new Timestamp((new java.util.Date()).getTime()));
		//SE CREA LA NOTIFICACION QUE LE VA A LLEGAR AL CONDUCTOR, SOBRE QUE EL PASAJERO CANCELO SU PARTICIPACION
		Notificacion notificacion= new Notificacion();
		notificacion.setCliente(viaje.getConductor());
		notificacion.setEstado(EstadoNotificacion.no_leido);
		notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) ); 
		notificacion.setTexto("El pasajero: "+ pasajero.getCliente().getNombre_usuario() +
				" ha cancelado su participacion su viaje <<"+viaje.getNombre_amigable()+">> con destino a "+viaje.getDestino().getNombre());
		notificacion.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
		this.entitymanager.persist(notificacion);
                DAONotificaciones daonotif = DAONotificaciones.getInstance();
                daonotif.enviarNotificacionPorMail(notificacion);
		try{
                    entitymanager.getTransaction( ).commit( );
                    DAOPuntos daopuntos = DAOPuntos.getInstance();
                    boolean bandera= false;
                    Calendar calendar = Calendar.getInstance();
                    Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
                    bandera = daopuntos.evaluarSancion(id_cliente, id_viaje, currentTimestamp);
                }catch(RollbackException e){
                    String error= ManejadorErrores.parsearRollback(e);
                    throw new ExceptionViajesCompartidos("ERROR: "+error);
                }
		return true;
        
        }
        
        //by juan
    	public synchronized boolean subirFotoVehiculo(JSONObject foto) throws ExceptionViajesCompartidos {  
    		int idVehiculo;
    		try{
    			idVehiculo = Integer.parseInt(foto.get("vehiculo").toString());
    		}catch(Exception e){
    			throw new ExceptionViajesCompartidos("El vehiculo no es vï¿½lido");
    		}
        	Vehiculo v = (Vehiculo) this.buscarPorPrimaryKey(new Vehiculo(), idVehiculo);
        	if (v==null){
    			throw new ExceptionViajesCompartidos("El vehiculo no existe en el sistema");
        	}
        	
    		//GUARDO EL CAMBIO DE FOTO
    		this.iniciarTransaccion();
			v.setFoto(foto.get("imagen").toString());
			this.entitymanager.persist(v);
    		try{
        		entitymanager.getTransaction( ).commit( );	
        	}catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
        	}
                
                //notifico a todos los que manejan el auto que fue modificado por este cliente
    		int id_conductor_modifica = (int) foto.get("conductor_modifica");
                Cliente conductor_modifica = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_conductor_modifica);
                List <Maneja> lista_conductores = this.getManejaPorVehiculo(v);
                for(int i=0;i<lista_conductores.size();i++){
                    Cliente conductor = lista_conductores.get(i).getCliente();
                    if(conductor.getId_usuario() != id_conductor_modifica){ //si no es quien modifico, notifico
                        
                        try{ //lo notifico uno por uno
                        
                            this.iniciarTransaccion();
                            Notificacion notificacion = new Notificacion();
                            notificacion.setTexto("El usuario <<"+conductor_modifica.getNombre_usuario()+">> ha modificado la foto del vehículo con patente: <<"+v.getPatente()+">>");
                            notificacion.setEstado(EstadoNotificacion.no_leido);
                            notificacion.setCliente(conductor);
                            notificacion.setLink("/ver_vehiculo.html?id="+v.getId_vehiculo());
                            notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()));
                            this.entitymanager.persist(notificacion);
                            
                            this.entitymanager.getTransaction().commit();
                            DAONotificaciones daonotif = DAONotificaciones.getInstance();
                            daonotif.enviarNotificacionPorMail(notificacion);

                        }catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
                        }
                    }
                }
    		return true;
    	}
    	
    	//by juan
		public synchronized boolean modificarVehiculo(JSONObject json) throws ExceptionViajesCompartidos {  
    		int idVehiculo;
    		try{
    			idVehiculo = Integer.parseInt(json.get("id").toString());
    		}catch(Exception e){
    			throw new ExceptionViajesCompartidos("El vehiculo no es vï¿½lido");
    		}
        	Vehiculo v = (Vehiculo) this.buscarPorPrimaryKey(new Vehiculo(), idVehiculo);
        	if (v==null){
    			throw new ExceptionViajesCompartidos("El vehiculo no existe en el sistema");
        	}
        	
    		this.iniciarTransaccion();
			v.setAire_acondicionado(json.get("aire").toString().charAt(0));
			v.setAnio(Integer.parseInt(json.get("anio").toString()));
			v.setCantidad_asientos(Integer.parseInt(json.get("asientos").toString()));
			v.setColor(json.get("color").toString());
			v.setSeguro(json.get("seguro").toString().charAt(0));
			this.entitymanager.persist(v);
    		try{
        		entitymanager.getTransaction( ).commit( );	
        	}catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
        	}
                //notifico a todos los que manejan el auto que fue modificado por este cliente
    		int id_conductor_modifica = (int) json.get("conductor_modifica");
                Cliente conductor_modifica = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_conductor_modifica);
                List <Maneja> lista_conductores = this.getManejaPorVehiculo(v);
                for(int i=0;i<lista_conductores.size();i++){
                    
                    if(lista_conductores.get(i).getFecha_fin()==null){
                        Cliente conductor = lista_conductores.get(i).getCliente();
                        if(conductor.getId_usuario() != id_conductor_modifica){ //si no es quien modifico, notifico

                            try{ //lo notifico uno por uno

                                this.iniciarTransaccion();
                                Notificacion notificacion = new Notificacion();
                                notificacion.setTexto("El usuario <<"+conductor_modifica.getNombre_usuario()+">> ha modificado el vehículo con patente: <<"+v.getPatente()+">>");
                                notificacion.setEstado(EstadoNotificacion.no_leido);
                                notificacion.setCliente(conductor);
                                notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()));
                                notificacion.setLink("/ver_vehiculo.html?id="+v.getId_vehiculo()); 
                                this.entitymanager.persist(notificacion);
                                this.entitymanager.getTransaction().commit();
                                DAONotificaciones daonotif = DAONotificaciones.getInstance();
                                daonotif.enviarNotificacionPorMail(notificacion);
                            }catch(RollbackException e){
                            String error= ManejadorErrores.parsearRollback(e);
                            throw new ExceptionViajesCompartidos("ERROR: "+error);
                            }
                        }
                    }
                }
                
                
    		return true;
		}

		//by juan
		//Funcion que pone el estado del vehiculo en inactivo
		public synchronized boolean desactivarVehiculo(Vehiculo v, int id_conductor_modifica) throws ExceptionViajesCompartidos {
			
    		
    		this.iniciarTransaccion();
			v.setEstado("I".charAt(0));
			this.entitymanager.persist(v);
    		try{
        		entitymanager.getTransaction( ).commit( );	
        	}catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
        	}
                
    		// Tomo los conductores activos de ese vehiculo
        	List<Cliente> conductoresActivos = v.getConductoresActivos();
                // traigo a quien lo modifico
                Cliente cliente_modifico = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_conductor_modifica);
                
                
        	
        	// notifico uno por uno (si esta activo) 
        	for (int i=0;i< conductoresActivos.size();i++){
        		if ( conductoresActivos.get(i).isActivo() ){
        			this.iniciarTransaccion();            			
                                Notificacion notificacion = new Notificacion();
                                notificacion.setTexto("El vehículo con patente <<"+v.getPatente()+" >> ha sido desactivado por <<"+cliente_modifico.getNombre_usuario()+" >>");
                                notificacion.setCliente(conductoresActivos.get(i));
                                notificacion.setEstado(EstadoNotificacion.no_leido);
                                notificacion.setLink("/mis_vehiculos.html");
                                notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) );
                                try{
                                    this.entitymanager.persist(notificacion);
                                    this.entitymanager.getTransaction().commit();
                                    DAONotificaciones daonotif = DAONotificaciones.getInstance();
                                    daonotif.enviarNotificacionPorMail(notificacion);
                                }catch(RollbackException e){
                                    String error= ManejadorErrores.parsearRollback(e);
                                    throw new ExceptionViajesCompartidos("ERROR: "+error);
                                }
        		}
        	}//fin de notificarlos
    		return true;
		}

		// by juan
		//Funcion que desactiva todas las relaciones de maneja existentes segun el vehiculo dado
		public synchronized boolean desactivarManeja(Vehiculo v) throws ExceptionViajesCompartidos {
    		this.iniciarTransaccion();

			List<Maneja> lista = this.getManejaPorVehiculo(v);
			for (Maneja m : lista){
				m.setFecha_fin(new Timestamp((new java.util.Date()).getTime()));
				this.entitymanager.persist(m);
			}
    		try{
        		entitymanager.getTransaction( ).commit( );	
        	}catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
        	}
			
			return true;
		}

		//by juan
		//Funcion que en base a un vehiculo y un conductor devuelve todas sus relaciones de maneja
		@SuppressWarnings("unchecked")
		public List<Maneja> getManejaPorVehiculoConductor(Vehiculo v, Cliente c) {
			Query q=this.entitymanager.createNamedQuery("Maneja.ListarConductorVehiculo");//nombre de query confuso
			q.setParameter("conductor", c);
			q.setParameter("vehiculo", v);
			return q.getResultList();
		}
		
		//by juan
		//Funcion que en base a un vehiculo y un conductor devuelve la primer relacion maneja que este activa
		public Maneja getManejaActivoPorVehiculoConductor(Vehiculo v, Cliente c) {
			List<Maneja> lista = this.getManejaPorVehiculoConductor(v,c);
			for (Maneja m:lista){
				if (m.getFecha_fin() == null){
					return m;
				}
			}
			return null;
		}
		
		//by juan
		//Funcion que en base a un vehiculo devuelve todas las relaciones de maneja
		@SuppressWarnings("unchecked")
		public List<Maneja> getManejaPorVehiculo(Vehiculo v) {
			String query="SELECT m from Maneja m "
					+"WHERE m.vehiculo = :id_vehiculo";
			Query q=this.entitymanager.createQuery(query);
			q.setParameter("id_vehiculo", v);
			return q.getResultList();
		}

		//by juan
		// funcion que en base a un maneja verifica si tiene viajes
		public boolean manejaTieneViajesActivos(Maneja m) {
			String query="SELECT v from Viaje v "
					+"WHERE (v.conductor_vehiculo = :conductor_vehiculo) and"
					+" (v.estado = :iniciado or v.estado = :no_iniciado)";
			Query q=this.entitymanager.createQuery(query);
			q.setParameter("conductor_vehiculo", m);
			q.setParameter("iniciado", EstadoViaje.iniciado);
			q.setParameter("no_iniciado", EstadoViaje.no_iniciado);
			return !q.getResultList().isEmpty();
		}	
		
		// by juan
		// Funcion que en base a un vehiculo verifica si tiene viajes
		public boolean vehiculoTieneViajes(Vehiculo v) {
			List<Maneja> listaManeja = this.getManejaPorVehiculo(v);
			for (Maneja m : listaManeja){
				if (this.manejaTieneViajesActivos(m)){
					return true;
				}
			}
			return false;
		}
		
		//by juan
		// recibe una lista de id de conductores y el id de un vehiculo.
		//Se le asignan esos conductores al vehiculo
		public synchronized boolean asignarConductoresVehiculo(int idVehiculo,
				ArrayList<Cliente> listaConductores) throws ExceptionViajesCompartidos {
			
			// verifico que vehiculo existe en sistema
			Vehiculo v = (Vehiculo) this.buscarPorPrimaryKey(new Vehiculo(), idVehiculo);
        	if (v==null){
    			throw new ExceptionViajesCompartidos("El vehiculo no existe en el sistema");
        	}
			
    		this.iniciarTransaccion();
        	
        	// Tomo los conductores activos de ese vehiculo
        	List<Cliente> conductoresActivos = v.getConductoresActivos();
        	
        	// Si conductor no maneja este vehiculo, lo asigno. 
        	for (Cliente conductorNuevo : listaConductores){
        		if (conductorNuevo.isActivo() && !conductoresActivos.contains(conductorNuevo)){
        			if (v.asignarConductor(conductorNuevo)){
            			this.entitymanager.persist(conductorNuevo);
            			this.entitymanager.persist(v);
                                Notificacion notificacion = new Notificacion();
                                notificacion.setTexto("Usted ha sido designado como conductor del vehículo con patente: "+ v.getPatente());
                                notificacion.setCliente(conductorNuevo);
                                notificacion.setEstado(EstadoNotificacion.no_leido);
                                 notificacion.setLink("/ver_vehiculo.html?id="+v.getId_vehiculo());
                                notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) );
                                this.entitymanager.persist(notificacion);
                                DAONotificaciones daonotif = DAONotificaciones.getInstance();
                                daonotif.enviarNotificacionPorMail(notificacion);
        			}
        		}
        	}
        	
    		try{
        		entitymanager.getTransaction( ).commit( );	
        	}catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
        	}
			
			return true;
		}
		

        //es el mismo metodo que usa juan pero con unas correcciones
        public synchronized boolean asignarConductoresVehiculo2(int idVehiculo,
				int id_conductor_modifica, String[] conductores) throws ExceptionViajesCompartidos {
			
			// verifico que vehiculo existe en sistema
			Vehiculo v = (Vehiculo) this.buscarPorPrimaryKey(new Vehiculo(), idVehiculo);
        	if (v==null){
    			throw new ExceptionViajesCompartidos("El vehiculo no existe en el sistema");
        	}
			
    		this.iniciarTransaccion();
    		
        	ArrayList<Cliente> listaConductores = new ArrayList<Cliente>();
    		if (conductores != null) {
				for (String c: conductores) {
					Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), Integer.parseInt(c));
					if (cliente==null){
						throw new ExceptionViajesCompartidos("El cliente no existe en el sistema");
					}
					listaConductores.add(cliente);
				}
			}
    		Cliente conductor_modifica = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_conductor_modifica);
                
        	// Tomo los conductores activos de ese vehiculo
        	List<Cliente> conductoresActivos = v.getConductoresActivos();
        	
        	// Si conductor no maneja este vehiculo, lo asigno. 
        	for (Cliente conductorNuevo : listaConductores){
        		if (conductorNuevo.isActivo() && !conductoresActivos.contains(conductorNuevo)){
        			conductorNuevo.asignarVehiculo(v);
                                Notificacion notificacion = new Notificacion();
                                notificacion.setTexto("Usted ha sido designado como conductor del vehículo con patente: <<"+ v.getPatente()+">> por el usuario: <<"+conductor_modifica.getNombre_usuario()+">>");
                                notificacion.setCliente(conductorNuevo);
                                notificacion.setEstado(EstadoNotificacion.no_leido);
                                notificacion.setLink("/ver_vehiculo.html?id="+v.getId_vehiculo());
                                notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) );
                                this.entitymanager.persist(notificacion);
                                DAONotificaciones daonotif = DAONotificaciones.getInstance();
                                daonotif.enviarNotificacionPorMail(notificacion);
        		}
        	}
        	
    		try{
        		entitymanager.getTransaction( ).commit( );	
        	}catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
        	}
			
			return true;
		}         
                          
		
		//by juan
		// desactiva conductor a vehiculo (agarro el maneja y le mando una fecha al null de fechafin)
		public synchronized boolean desasignarConductor(int idVehiculo, int idConductor, int id_conductor_modifica) throws ExceptionViajesCompartidos {
			Vehiculo v = (Vehiculo) this.buscarPorPrimaryKey(new Vehiculo(), idVehiculo);
			if (v==null){
        		throw new ExceptionViajesCompartidos("El vehiculo no se encuentra en el sistema");
			}
			Cliente c = (Cliente) this.buscarPorPrimaryKey(new Cliente(), idConductor);
			if (v==null){
        		throw new ExceptionViajesCompartidos("El Conductor no se encuentra en el sistema");
			}
			if (!c.puedeManejar(v)){
        		throw new ExceptionViajesCompartidos("El Conductor no tiene asignado este vehï¿½culo");
			}
						
    		this.iniciarTransaccion();
			Cliente conductor_modifica = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_conductor_modifica);
			List<Maneja> listaManeja = this.getManejaPorVehiculoConductor(v, c);
			for (Maneja m : listaManeja) {
				if (m.getFecha_fin() == null){
					// chequeo que vehiculo no tenga viaje pendiente
					if (this.manejaTieneViajesActivos(m)){
		        		throw new ExceptionViajesCompartidos("El Conductor tiene viajes pendientes con este vehiculo");
					}
					m.desactivar();
					this.entitymanager.persist(m);
                                        Notificacion notificacion = new Notificacion();
                                        notificacion.setTexto("Usted fue eliminado como conductor del vehículo con patente: <<"+ v.getPatente()+">> por el usuario <<"+conductor_modifica.getNombre_usuario()+" >> y ya no podrá realizar viajes con el mismo");
                                        notificacion.setCliente(m.getCliente());
                                        notificacion.setEstado(EstadoNotificacion.no_leido);
                                        notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) );
                                         notificacion.setLink("/mis_vehiculos.html");
                                        this.entitymanager.persist(notificacion);
                                        DAONotificaciones daonotif = DAONotificaciones.getInstance();
                                        daonotif.enviarNotificacionPorMail(notificacion);
				}
			}
			
    		try{
        		entitymanager.getTransaction( ).commit( );	
				this.refresh(c);
				this.refresh(v);
        	}catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
        	}
    		
			return true;
		}	
                
        //by fede
        public synchronized boolean cancelarViaje(Integer id_viaje,Integer id_cliente ) throws ExceptionViajesCompartidos {
        	this.limpiarTransacciones();
            //Verificaciones varias
            Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
		if(viaje==null){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO EXISTE");
		}
		if(viaje.getEstado()==EstadoViaje.cancelado || viaje.getEstado()==EstadoViaje.finalizado){
			throw new ExceptionViajesCompartidos("ERROR: EL VIAJE ESTA CANCELADO O YA FINALIZO");
		}
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
		if(cliente==null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
		}
                Cliente chofer = viaje.getConductor();
                if(chofer.equals(cliente)){ //si es el chofer quien cancela
                    this.iniciarTransaccion();
                    viaje.setEstado(EstadoViaje.cancelado);
                    Calendar calendar = Calendar.getInstance();
                    Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
                    viaje.setFecha_cancelacion(currentTimestamp);
                    DAOPuntos daopuntos = DAOPuntos.getInstance();
                    try{
                        entitymanager.getTransaction( ).commit( );
                        //Ya cancele el viaje, ahora cancelo a los Pasajeros sin sancionarlos
                        List<PasajeroViaje> lista = viaje.getPasajeros(); 
                        int aceptados = 0 ;
                        for(int j=0;j<lista.size();j++){//me fijo si alguno tenia estado aceptado
                            PasajeroViaje pasajero = lista.get(j);
                            if(pasajero.getEstado().equals(EstadoPasajeroViaje.aceptado)){
                                aceptados++;                   
                            }
                        }
                        id_viaje = viaje.getId_viaje();
                        for(int i=0; i<lista.size();i++){
                            this.iniciarTransaccion();
                            boolean bandera= false;
                            currentTimestamp.setYear(1000);//seteo aÃ±o para que no los sancione
                            PasajeroViaje pasajero = lista.get(i);
                            pasajero.setEstado(EstadoPasajeroViaje.cancelado);
                            //this.entitymanager.getTransaction( ).commit( );
                            int id_cliente_pas = pasajero.getCliente().getId_usuario();
                            //bandera = daopuntos.evaluarSancion(id_cliente_pas, id_viaje, currentTimestamp);
                            //SE CREA LA NOTIFICACION QUE LE VA A LLEGAR AL PASAJERO, SOBRE QUE FUE Cancelado
                            //this.iniciarTransaccion();
                            Notificacion notificacion= new Notificacion();
                            notificacion.setCliente(pasajero.getCliente()); 
                            notificacion.setEstado(EstadoNotificacion.no_leido);
                            notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) ); 
                            notificacion.setTexto("El conductor: "+ viaje.getConductor().getNombre_usuario() +
                                            " ha cancelado el viaje: "+viaje.getNombre_amigable());
                            notificacion.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
                            this.entitymanager.persist(notificacion);
                            this.entitymanager.getTransaction( ).commit( );
                            DAONotificaciones daonotif = DAONotificaciones.getInstance();
                            daonotif.enviarNotificacionPorMail(notificacion);
                            //fin notificar pasajero viaje
                        }
                        //Ahora sanciono al chofer si corresponde
                        int id_chofer = chofer.getId_usuario();
                                            
                        boolean bandera = false;
                        calendar = Calendar.getInstance();
                        currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
                                          
                        
                        if(aceptados>0){//lo sanciono si tenia pasajeros
                            bandera = daopuntos.sancionarChofer(id_viaje,id_chofer,aceptados);
                        }
                        this.notificarSeguidores(viaje.getId_viaje(), "cancelado");
                    }catch(RollbackException e){
                        String error= ManejadorErrores.parsearRollback(e);
                        throw new ExceptionViajesCompartidos("ERROR: "+error);
                    }
                
                }else{//no era el chofer
                    throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO ES EL CONDUCTOR");               
                
                }	
		return true;
        }
        
        //by fede
        public synchronized boolean dejarComentarioEnViaje(JSONObject json) throws ExceptionViajesCompartidos{
           this.limpiarTransacciones();
            
            
            ComentarioViaje cv = new ComentarioViaje();
            cv.setTexto((String) json.get("texto"));
            int id_cliente = (int) json.get("id_cliente");
            Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
            if(cliente==null){
                throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE!");
            }
            cv.setCliente(cliente);
            Viaje viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), json.get("id_viaje"));
            if(viaje==null){
                throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO EXISTE!");
            }
            Cliente conductor = viaje.getConductor();
            cv.setViaje(viaje);    		
            java.util.Date utilDate = new java.util.Date();
            java.sql.Timestamp fecha = new Timestamp(utilDate.getTime());
            cv.setFecha(fecha);
            this.iniciarTransaccion();

            Notificacion notif = new Notificacion();
            if(cliente.getId_usuario()!=conductor.getId_usuario()){ //si no es el conductor, lo notifico al conductor
                //notificacion para el chofer
                
                    notif.setCliente(conductor);
                    notif.setEstado(EstadoNotificacion.no_leido);
                    notif.setFecha(new Timestamp((new java.util.Date()).getTime()));
                    //notif.setId_notificacion();
                    notif.setTexto(cliente.getNombre_usuario()+" ha realizado un comentario en su viaje "+viaje.getNombre_amigable());
                    notif.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
                    this.entitymanager.persist(cv);
                    this.entitymanager.persist(notif);
                    this.entitymanager.getTransaction().commit();
                    DAONotificaciones daonotif = DAONotificaciones.getInstance();
                    daonotif.enviarNotificacionPorMail(notif);
                    //TO DO Set LINK!!
                //fin notif
            }else{
                if(cliente.getId_usuario()==conductor.getId_usuario()){ //si comenta el conductor, le aviso a los pasajeros
                    try{
                        this.entitymanager.persist(cv); //persisto el comentario y los notifico a todos
                        this.entitymanager.getTransaction().commit();
                        List<ComentarioViaje> lista = this.getComentariosViaje(viaje.getId_viaje());
                        List<Cliente> notificados = new ArrayList<Cliente>();
                        notificados.add(conductor); //le agrego el conductor asi no se notifica de su comentario
                        for(int i=0;i<lista.size();i++){ //tengo que notificar a todos pero solo una vez
                            Cliente clienteANotificar = lista.get(i).getCliente();
                            
                                if( (!notificados.contains(clienteANotificar))){ //si no lo notifique, lo notifico ahora   
                                    Notificacion notificacion = new Notificacion(); 
                                    this.iniciarTransaccion();
                                                                       
                                    notificacion.setEstado(EstadoNotificacion.no_leido);
                                    notificacion.setCliente(clienteANotificar);
                                    notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()));
                                    
                                    notificacion.setTexto("El conductor del viaje "+viaje.getNombre_amigable()+" ha realizado un comentario en el mismo.");
                                    notificacion.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
                                    this.entitymanager.persist(notificacion);
                                    this.entitymanager.getTransaction().commit();
                                    notificados.add(clienteANotificar); //lo agrego para no notificarlo mas
                                    DAONotificaciones daonotif = DAONotificaciones.getInstance();
                                    daonotif.enviarNotificacionPorMail(notificacion);
                                }
                            
                        }
                        
                    }catch(RollbackException e){
                        String error= ManejadorErrores.parsearRollback(e);
                        throw new ExceptionViajesCompartidos("ERROR: "+error);
                    }
                }
            }
            return true;
        }
        
        public synchronized List<ComentarioViaje> getComentariosViaje(int id_viaje) throws ExceptionViajesCompartidos{
            List<ComentarioViaje> lista = new ArrayList<ComentarioViaje>();
            Viaje viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
            if(viaje==null){
                throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO EXISTE!");
            }
            Query qry = entitymanager.createNamedQuery("Comentarios.BuscarPorViaje");
            qry.setParameter("viaje", viaje);
            lista = qry.getResultList(); 
            return lista;
        }
		
        public synchronized boolean seguirViaje(int idViaje, int idUsuario) throws ExceptionViajesCompartidos {
            this.limpiarTransacciones();
            
            Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), idUsuario);
            if(cliente==null){
                throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE!");
            }
            
            Viaje viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), idViaje);
            if(viaje==null){
                throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO EXISTE!");
            }
            List<SeguidorViaje> listaSeguidores = this.getSeguidoresViaje(viaje.getId_viaje());
            SeguidorViaje sv = null;
            for (SeguidorViaje seguidor : listaSeguidores){
            	if (seguidor.getCliente().getId_usuario() == idUsuario){
            		sv = seguidor;
            	}
        	}
            if (sv==null){
            	sv = new SeguidorViaje();
            }else if (sv.isActivo()){
                throw new ExceptionViajesCompartidos("Usted ya es seguidor de este viaje");
            }

            this.iniciarTransaccion();
            sv.setCliente(cliente);
            sv.setEstado("A".charAt(0));
            sv.setViaje(viaje);    		
            java.util.Date utilDate = new java.util.Date();
            java.sql.Timestamp fecha = new Timestamp(utilDate.getTime());
            sv.setFecha(fecha);

            Notificacion notifSeguidor = new Notificacion();
            Notificacion notifConductor = new Notificacion();
            Cliente conductor = viaje.getConductor();
            notifConductor.setCliente(conductor);
            notifConductor.setEstado(EstadoNotificacion.no_leido);
            notifConductor.setFecha(new Timestamp((new java.util.Date()).getTime()));
            notifConductor.setTexto("El usuario "+cliente.getNombre_usuario()+" está siguiendo el viaje "+viaje.getNombre_amigable()+" en el cual usted es conductor");
            notifConductor.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
            notifSeguidor.setCliente(cliente);
            notifSeguidor.setEstado(EstadoNotificacion.no_leido);
            notifSeguidor.setFecha(new Timestamp((new java.util.Date()).getTime()));
            notifSeguidor.setTexto("Usted está siguiendo el viaje "+viaje.getNombre_amigable());
            notifSeguidor.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
            
    		try{
                this.entitymanager.persist(sv);
                this.entitymanager.persist(notifConductor);
                this.entitymanager.persist(notifSeguidor);
        		entitymanager.getTransaction( ).commit( );
                        DAONotificaciones daonotif = DAONotificaciones.getInstance();
                        daonotif.enviarNotificacionPorMail(notifSeguidor);
                        daonotif.enviarNotificacionPorMail(notifConductor);
        	}catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
        	}
 
            return true;
			
		}
		public synchronized boolean dejarDeSeguirViaje(int idViaje, int idUsuario) throws ExceptionViajesCompartidos {
            this.limpiarTransacciones();
            
            Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), idUsuario);
            if(cliente==null){
                throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE!");
            }
            
            Viaje viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), idViaje);
            if(viaje==null){
                throw new ExceptionViajesCompartidos("ERROR: EL VIAJE NO EXISTE!");
            }
            List<SeguidorViaje> listaSeguidores = this.getSeguidoresViaje(viaje.getId_viaje());
            SeguidorViaje sv = null; 
            for (SeguidorViaje seguidor : listaSeguidores){
	            if (seguidor.getCliente().getId_usuario() == idUsuario){
	            	sv = seguidor;
	            	break;
	            }
        	}
            
            if (sv == null || !sv.isActivo()) throw new ExceptionViajesCompartidos("ERROR: Usted no es seguidor de este viaje");
            
            this.iniciarTransaccion();
            sv.setEstado("I".charAt(0));
            java.util.Date utilDate = new java.util.Date();
            java.sql.Timestamp fecha = new Timestamp(utilDate.getTime());
            sv.setFecha(fecha);

            Notificacion notifSeguidor = new Notificacion();
            Notificacion notifConductor = new Notificacion();
            Cliente conductor = viaje.getConductor();
            notifConductor.setCliente(conductor);
            notifConductor.setEstado(EstadoNotificacion.no_leido);
            notifConductor.setFecha(new Timestamp((new java.util.Date()).getTime()));
            notifConductor.setTexto("El usuario "+cliente.getNombre_usuario()+" ha dejado de seguir el viaje "+viaje.getNombre_amigable()+" en el cual usted es conductor");
            notifConductor.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
            notifSeguidor.setCliente(cliente);
            notifSeguidor.setEstado(EstadoNotificacion.no_leido);
            notifSeguidor.setFecha(new Timestamp((new java.util.Date()).getTime()));
            notifSeguidor.setTexto("Usted ha dejado de seguir el viaje "+viaje.getNombre_amigable());
            notifSeguidor.setLink ("/detalle_viaje.html?id=" + viaje.getId_viaje());
            
    		try{
                this.entitymanager.persist(notifConductor);
                this.entitymanager.persist(notifSeguidor);
        		entitymanager.getTransaction( ).commit( );
                        DAONotificaciones daonotif = DAONotificaciones.getInstance();
                        daonotif.enviarNotificacionPorMail(notifSeguidor);
                        daonotif.enviarNotificacionPorMail(notifConductor);
        	}catch(RollbackException e){
        		String error= ManejadorErrores.parsearRollback(e);
        		throw new ExceptionViajesCompartidos("ERROR: "+error);
        	}
 
            return true;
		}
        public List<SeguidorViaje> getSeguidoresViaje(int id_viaje){
            List<SeguidorViaje> lista = new ArrayList<SeguidorViaje>();
            Viaje viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
            Query qry = entitymanager.createNamedQuery("SeguidorViaje.BuscarPorViaje");
            qry.setParameter("viaje", viaje);
            lista = (qry.getResultList() == null)? null : qry.getResultList(); 
            return lista;
        }
        public boolean esSeguidor(int idCliente, int idViaje){
    		List<SeguidorViaje> listaSeguidores = this.getSeguidoresViaje(idViaje);
    		boolean esSeguidor = false;
    		for (SeguidorViaje sv : listaSeguidores){
    			esSeguidor = sv.getCliente().getId_usuario() == idCliente
    					&& sv.isActivo();
    		}
    		return esSeguidor;
        }
        
        
        public synchronized boolean notificarSeguidores (int id_viaje, String motivo) throws ExceptionViajesCompartidos{
            this.limpiarTransacciones();
            
            String motivo_notificacion = null;
            if(motivo.equals("cancelado")){
                motivo_notificacion = "ha sido cancelado";
            }else{
                if(motivo.equals("modificado")){
                    motivo_notificacion = "ha sido modificado";
                }else{
                    if(motivo.equals("iniciado")){
                        motivo_notificacion = "ha iniciado";
                    }else{
                        if(motivo.equals("finalizado")){
                            motivo_notificacion = "ha finalizado";              
                        }
                    }
                }
            }//fin setear motivo   
            Viaje viaje = (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
            String texto_notificacion = "El viaje <<"+viaje.getNombre_amigable()+">> que usted seguía "+motivo_notificacion;
            List<SeguidorViaje> lista_seguidores = this.getSeguidoresViaje(id_viaje);
            
            //ahora notifico a los seguidores
            for(int i=0;i<lista_seguidores.size();i++){
                
                if(lista_seguidores.get(i).isActivo()){
                    Cliente cliente_notificarlo = lista_seguidores.get(i).getCliente();
                    this.iniciarTransaccion();
                    Notificacion notificacion = new Notificacion();
                    notificacion.setEstado(EstadoNotificacion.no_leido);
                    notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()));
                    notificacion.setLink("/detalle_viaje.html?id="+viaje.getId_viaje());

                    notificacion.setTexto(texto_notificacion);
                    notificacion.setCliente(cliente_notificarlo);
                    try{                
                    this.entitymanager.persist(notificacion);
                    this.entitymanager.getTransaction( ).commit( );	
                    DAONotificaciones daonotif = DAONotificaciones.getInstance();
                    daonotif.enviarNotificacionPorMail(notificacion);
                    }catch(RollbackException e){
                            String error= ManejadorErrores.parsearRollback(e);
                            throw new ExceptionViajesCompartidos("ERROR: "+error);
                    }
                }
            }//fin for
            return true;
        }
		public JSONArray comisionPorRecorrido(ArrayList<Integer> localidades) {
			Localidad l1,l2 = null;
			JSONArray recorrido = new JSONArray();
			for (int i=0; i<localidades.size()-1;i++){
				JSONObject tramo = new JSONObject();
				l1=(Localidad) this.buscarPorPrimaryKey(new Localidad(), localidades.get(i));
				l2=(Localidad) this.buscarPorPrimaryKey(new Localidad(), localidades.get(i+1));
				tramo.put("origen", l1.toJSON());
				tramo.put("destino", l2.toJSON());
				tramo.put("comision", this.informarPrecioTramo(l1.getId(), l2.getId()));
				recorrido.add(tramo);
			}
			return recorrido;
		}
        public float informarPrecioTramo (int id_localidad1, int id_localidad2){
            Localidad localidad1 = (Localidad) this.buscarPorPrimaryKey(new Localidad(), id_localidad1);
            Localidad localidad2 = (Localidad) this.buscarPorPrimaryKey(new Localidad(), id_localidad2);
            
            Double distancia = this.distanciaEntreLocalidades(localidad1, localidad2);
            
            Query qry2 = this.entitymanager.createNamedQuery("Comision.PrecioPorKM");
            qry2.setParameter("km", distancia);
            Comision comision = (Comision) qry2.getSingleResult();
            Float monto = comision.getPrecio() * distancia.floatValue();	//retocado por lucas TODO 
            monto = (float) (Math.round(monto * 100.0) / 100.0);	
                //if(comision==null){
                //    throw new ExceptionViajesCompartidos("ERROR: NO SE PUDO RECUPERAR LA COMISION");
		//}
            return monto;
        }
        
        //by fede
          public List<PasajeroViaje> getPasajerosOrdenadosPorReputacion(int idViaje){
            List<PasajeroViaje> lista_ordenada = new ArrayList<PasajeroViaje>();
            lista_ordenada = this.listarPasajerosPorViaje(idViaje);
            Collections.sort(lista_ordenada, new Comparator<PasajeroViaje>(){

			@Override
			public int compare(PasajeroViaje pv1, PasajeroViaje pv2) {
				return pv2.getCliente().getReputacion().compareTo(pv1.getCliente().getReputacion());
			}
            });
            return lista_ordenada;        
          }
               
          
          //by fede
          public boolean autoTieneViaje(Maneja maneja, Timestamp fecha_hora_viaje, double kms_viaje) throws ExceptionViajesCompartidos{
                DAOPuntos daopuntos = DAOPuntos.getInstance();
                Vehiculo vehiculo =  maneja.getVehiculo();

                Query qry = this.entitymanager.createNamedQuery("Viaje.PorVehiculo");
                qry.setParameter("id_vehiculo", vehiculo.getId_vehiculo());
                List<Viaje> lista_viajes = qry.getResultList();
                for(int i=0;i<lista_viajes.size();i++){
                	EstadoViaje estadoviaje=lista_viajes.get(i).getEstado();
                    if(! (estadoviaje.equals(EstadoViaje.cancelado) || estadoviaje.equals(EstadoViaje.finalizado)) ){
                    Timestamp fecha_hora_viaje_recuperado = lista_viajes.get(i).getFecha_inicio();
                    double diferencia = daopuntos.diferenciaTimestampsParaVehiculos(fecha_hora_viaje, fecha_hora_viaje_recuperado);
                        if(diferencia>=0){//si el viaje a crear es posterior al que recupero
                          double kms_viaje_recuperado = this.distanciaEntreLocalidades(lista_viajes.get(i).getDestino(),lista_viajes.get(i).getOrigen());
                          if(diferencia < (kms_viaje_recuperado/100) ){ //supongo
                              throw new ExceptionViajesCompartidos("ERROR: EL VEHICULO TIENE VIAJES EN SIMULTANEO (O CERCANO) AL VIAJE QUE INTENTA CREAR");
                          }                   

                        }else{ //si el viaje que creo es anterior al que miro

                            if(Math.abs(diferencia) < (kms_viaje/100) ){ //supongo
                              throw new ExceptionViajesCompartidos("ERROR: EL VEHICULO TIENE VIAJES EN SIMULTANEO (O CERCANO) AL VIAJE QUE INTENTA CREAR");
                            }  

                        }
                        
                    }
                }   
                  
              
              return false;
          }
}

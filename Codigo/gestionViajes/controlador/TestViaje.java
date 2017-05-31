package gestionViajes.controlador;

import gestionViajes.controlador.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import gestionComisiones.modelo.EstadoComisionCobrada;
import gestionUsuarios.modelo.Cliente;
import gestionViajes.modelo.*;
import junit.framework.TestCase;
import otros.ExceptionViajesCompartidos;

public class TestViaje extends TestCase {

	protected DAOViajes daoviajes = DAOViajes.getInstance();
        	
	//ESTOY SIGUIENDO EL TUTORIAL DE http://www.tutorialspoint.com/junit/index.htm
	//VAN A TENER Q AGREGAR LA LIBRERIA JUint 4, que contiene:
	//junit.jar
	//org.hamcrest.core_1.3.0.v201303031735

	@Before
	public void setUp() throws Exception {
		//este metodo se ejecuta antes de cada "parte" del test, osea antes de cada metodo
		//sirve para inicializar variables asi todos los test arrancan en el mismo entorno 
		
		//esto q sigue es codigo para vaciar la BD y que todas las pruebas corran en el mismo entorno
		this.daoviajes.vaciarTabla("ComentarioViaje");
		this.daoviajes.vaciarTabla("MovimientoSaldo");
		this.daoviajes.vaciarTabla("Sancion");
        this.daoviajes.vaciarTabla("MovimientoPuntos");
        this.daoviajes.vaciarTabla("PasajeroViaje");
        this.daoviajes.vaciarTabla("Calificacion");
		this.daoviajes.vaciarTabla("ComisionCobrada");
		this.daoviajes.vaciarTabla("LocalidadViaje");
		this.daoviajes.borrarRelacionesEntreViajes();
        this.daoviajes.vaciarTabla("SeguidorViaje");
		this.daoviajes.vaciarTabla("Viaje");
		this.daoviajes.vaciarTabla("Maneja");
		this.daoviajes.vaciarTabla("Vehiculo");
        this.daoviajes.vaciarTabla("Notificacion");
	}
	
	@Test
	public void testquery() {
		String query = "";
		//pruena para probar querys
	}
	
	@Test
	public void testNuevoViajeCorrectoSINVUELTA() {
		//test q envie un json correcto y tendria q andar bien
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
	}
        
        @Test
	public void testNuevoViajeCorrectoSINSALDO() {
		//test q envie un json correcto y tendria q andar bien
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo2();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json_sin_saldo = TestViaje.crearViajeSinSaldo();
		try {
			boolean bandera = this.daoviajes.nuevoViaje(json_sin_saldo) ;
                        if(bandera){
                            fail("lo creó sin error");
                        }
		} catch (ExceptionViajesCompartidos e) {
			String msj_error=e.getMessage();
                        boolean bandera2 = false;
			if(msj_error.contains("USTED NO TIENE SALDO")){
                             bandera2 = true;      
			}else{
                             bandera2=false;				
			}
                        //assertTrue(bandera2);
		}
	}
	
	@Test
	@SuppressWarnings({ "unchecked" })
	public void testNuevoViajeCorrectoCONVUELTA() {
		//test q envie un json correcto y tendria q andar bien
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		JSONObject vuelta = new JSONObject();
		Timestamp fecha= new Timestamp((new java.util.Date()).getTime());
		fecha.setMonth(12);
		vuelta.put("fecha_inicio", fecha);
		vuelta.put("cantidad_asientos", 2);
		vuelta.put("nombre_amigable", "la vuelta de prueba viaje");
		vuelta.put("precio", 48f);
		json2.put("vuelta", vuelta);
		
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@SuppressWarnings({ "unchecked" })
	public void testBuscarViajeCorrecto1() {
		//test q busca un viaje recien creado que vaya desde el primero punto del viaje hasta el ultimo
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		JSONObject vuelta = new JSONObject();
		Timestamp fecha = new Timestamp((new java.util.Date()).getTime());
		fecha.setMonth(12);
		vuelta.put("fecha_inicio",fecha);
		vuelta.put("cantidad_asientos", 2);
		vuelta.put("precio", 24f);
		json2.put("vuelta", vuelta);
		
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject buscar = new JSONObject();
		buscar.put("origen", 3427200);
		buscar.put("destino", 3427205);
		buscar.put("fecha_desde", new Timestamp((new java.util.Date()).getTime()) );
		buscar.put("estado", "no_iniciado");
		try {
			List<Viaje> l=this.daoviajes.buscarViajes(buscar);
			if(l.size()!=1){
				fail("hay 2 viajes creado y tenia q devolver solo uno! y devolvi: "+l.size());
			}
			for(Viaje v: l){
				System.out.println("Viaje: "+v.getNombre_amigable());
			}
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testNuevoViajeINcorrectoSINVUELTA(){
		//el viaje se crea sin localidades
		boolean bandera=false;
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}
		JSONObject json2 = TestViaje.crearViaje();
		json2.remove("localidades");
		try {
			this.daoviajes.nuevoViaje(json2);
			fail("no tiro exception");
		} catch (ExceptionViajesCompartidos e) {
			bandera=true;
		}
		assertTrue(bandera);
	}
	
	@Test
	public void testNuevoViajeINcorrectoSINVUELTA3(){
		//el viaje se crea sin destino
		boolean bandera=false;
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}
		JSONObject json2 = TestViaje.crearViaje();
		JSONObject json3= (JSONObject) json2.get("localidades");
		json3.remove("destino");
		try {
			this.daoviajes.nuevoViaje(json2);
			fail("no tiro exception");
		} catch (ExceptionViajesCompartidos e) {
			String msj_error=e.getMessage();
			if(msj_error.contains("error no parseado")){
				fail("no se parseo bien el error");
			}else{
				bandera=true;				
			}
			
		}
		assertTrue(bandera);
	}
	
	@Test
	public void testNuevoViajeINcorrectoSINVUELTA2(){
		//el viaje se crea con id_destino= 1 (no existe esa localidad) 
		boolean bandera=false;
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}
		JSONObject json2 = TestViaje.crearViaje();
		JSONObject json3= (JSONObject) json2.get("localidades");
		json3.remove("destino");
		json3.put("destino", 1);
		try {
			this.daoviajes.nuevoViaje(json2);
			fail("no tiro exception");
		} catch (ExceptionViajesCompartidos e) {
			String msj_error=e.getMessage();
			if(msj_error.contains("error no parseado")){
				fail("no se parseo bien el error");
			}else{
				bandera=true;				
			}
			
		}
		assertTrue(bandera);
	}
	
	@Test
	public void testgetConductorYVehiculoViajeCorrecto() {
		//teste que crear un vehiculo, lo asocia a un viaje, y despues obtiene los datos del cliente y vehiculo de ese viaje
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}
		JSONObject json_viaje = TestViaje.crearViaje();		
		try {
			assertTrue( this.daoviajes.nuevoViaje(json_viaje) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		//se que en cada etapa del test hay un solo viaje, asi q recupero solo ese viaje
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		
		assertNotNull(this.daoviajes.getVehiculoViaje(viaje.getId_viaje()));
		assertNotNull(this.daoviajes.getConductorViaje(viaje.getId_viaje()));
	}
	
	@Test
	public void testgetConductorYVehiculoViajeINCorrecto() {
		//teste que trata de obtener los datos de un viaje q no existe
		
		assertNull(this.daoviajes.getVehiculoViaje(1));
		assertNull(this.daoviajes.getConductorViaje(1));
		assertNull(this.daoviajes.getVehiculoViaje(null));
		assertNull(this.daoviajes.getConductorViaje(null));
	}
	
	@Test
	public void testbuscarManejar() {	//test q buscar en la tabla Maneja
		//json con datos de vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
			//recupero el vehiculo
			Cliente c= (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 2);
			Vehiculo auto=(Vehiculo)this.daoviajes.buscarPorClaveCandidata("Vehiculo", "abd123");
			//ahora pruebo el metodo 
			Maneja maneja=(Maneja)this.daoviajes.buscarPorIDCompuesta("Maneja", c, auto);
			assertNotNull(maneja);
			System.out.println(maneja);
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@SuppressWarnings({ "unchecked" })
	public void testgetVehiculosPorCliente() {
		//test q les crea 2 vehiculos a un usuario, le saca uno y comprueba que solo puede manejar el otro
		//json con datos de vehiculo
		JSONObject json= crearVehiculo();
		JSONObject json2= crearVehiculo();
		JSONObject vj=(JSONObject) json2.get("vehiculo");
		vj.remove("patente");
		vj.put("patente", "eer999");
		try {
			//pruebo que el metodo devuelva true
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
			assertTrue(this.daoviajes.NuevoVehiculo(json2) );
			Vehiculo v=(Vehiculo)this.daoviajes.buscarPorClaveCandidata("Vehiculo", "eer999");
			this.daoviajes.clienteNoManejaVehiculo(2, v.getId());
			List<Vehiculo> l=this.daoviajes.getVehiculosPorCliente(2);
			assertEquals(l.size(),1);
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testNuevoAutoCorrecto() {	//test q envie un usuario q existe, y vehiculo con datos bien.
		
		//json con datos de vehiculo
		JSONObject json= crearVehiculo();
		
		try {
			//pruebo que el metodo devuelva true
			assertTrue(this.daoviajes.NuevoVehiculo(json) );	
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testasignarConductoresVehiculo2() {		
		//json con datos de vehiculo
		JSONObject json= crearVehiculo();
		try {
			//el cliente con id=2
			assertTrue(this.daoviajes.NuevoVehiculo(json) );	
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		String[] conductores= {"3","4"};
		Vehiculo v=(Vehiculo)this.daoviajes.buscarPorClaveCandidata("Vehiculo", "abd123");
		try {
			assertTrue (this.daoviajes.asignarConductoresVehiculo2(v.getId(),2 ,conductores) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		Cliente c=(Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 3);
		if(! c.getVehiculosQueManeja().contains(v) ){
			fail("no maneja el vehiculo que le acabo de asignar");
		}
		c=(Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 4);
		if(! c.getVehiculosQueManeja().contains(v) ){
			fail("no maneja el vehiculo que le acabo de asignar");
		}
		assertEquals(v.getConductoresActivos().size(),3);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNuevoAutoINCorrecto1() {	
		//test q envia 2 veces un mismo auto (pantente repetida)
		//el sistema debe responder con una exceptcion propia		
		boolean bandera= false;
		JSONObject json= new JSONObject();
		json.put("conductor", 2);
		JSONObject vehiculo= crearVehiculo();
		json.put("vehiculo", vehiculo);
		
		try {
			this.daoviajes.NuevoVehiculo(json);
			this.daoviajes.NuevoVehiculo(json);	//esta vez tiene q excplotar por patente duplicada
			fail("no tiro la exceptio de viajes compartidos");
		} catch (ExceptionViajesCompartidos e) {
			bandera=true;
		}
		assertTrue(bandera);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNuevoAutoINCorrecto2() {	
		//test q envia un usuarios q no existe
		boolean bandera=false;
		JSONObject json= crearVehiculo();
		json.remove("conductor");
		json.put("conductor", -1);
		
		try {
			this.daoviajes.NuevoVehiculo(json);
			fail("no tiro la exceptio de viajes compartidos");
		} catch (ExceptionViajesCompartidos e) {
			bandera=true;
		}
		assertTrue(bandera);
	}
	
	@Test
	public void testNuevoPasajeroViajeCorrecto() {
		//test q envie un json correcto y tendria q andar bien
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		
		try {
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		int i=0;
		i++;
	}
	
	//by jasmin y luz
		@Test
		public void testAceptarPostulanteCorrecto() {
			//CANTIDAD DE ASIENTOS DEL AUTO 1
			//SE POSTULA Y ACEPTA A 1 PASAJERO CORRECTAMENTE
			
			//datos del vehiculo y cliente, para crear el vehiculo
			JSONObject json= crearVehiculo();
			try {
				//creo los datos en la tabla maneja
				assertTrue(this.daoviajes.NuevoVehiculo(json) );
			}catch(ExceptionViajesCompartidos E){
				fail(E.getMessage());
			}
				JSONObject json2 = TestViaje.crearViaje2();
			try {
				assertTrue( this.daoviajes.nuevoViaje(json2) );
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			JSONObject json3= this.crearPostulante();
			
			try {
				assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			List viajes=this.daoviajes.selectAll("Viaje");
			Viaje viaje=(Viaje) viajes.get(0);
			try {
				assertTrue( this.daoviajes.aceptarPasajero(3, viaje.getId_viaje()) );
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 3);
			PasajeroViaje pv=viaje.recuperar_pasajeroViaje_por_cliente(cliente);
			assertEquals(pv.getEstado(),EstadoPasajeroViaje.aceptado);
			assertEquals(pv.getComision().getEstado(),EstadoComisionCobrada.pendiente);
		}
		
		//by jasmin y luz
		@Test
		public void testAceptarPostulanteCorrecto1(){
			// CANTIDAD DE ASIENTOS DEL AUTO ES 1
			// LAS SUBIDAS Y BAJADAS DE LOS DOS PASAJEROS NO SE SUPORPONEN
			//LOS DOS SON ACEPTADOS.
			
			//datos del vehiculo y cliente, para crear el vehiculo
			JSONObject json= crearVehiculo();
			try {
				//creo los datos en la tabla maneja
				assertTrue(this.daoviajes.NuevoVehiculo(json) );
			}catch(ExceptionViajesCompartidos E){
				fail(E.getMessage());
			}
				JSONObject json2 = TestViaje.crearViaje2();
			try {
				assertTrue( this.daoviajes.nuevoViaje(json2) );
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			JSONObject json3= this.crearPostulante3();
			JSONObject json4= this.crearPostulante4();
			
			try {
				assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
				assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json4) );
				
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			List viajes=this.daoviajes.selectAll("Viaje");
			Viaje viaje=(Viaje) viajes.get(0);
			try {
				assertTrue( this.daoviajes.aceptarPasajero(4, viaje.getId_viaje()));
				assertTrue( this.daoviajes.aceptarPasajero(7, viaje.getId_viaje()));
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			//compruebo estados
			Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 4);
			PasajeroViaje pv=viaje.recuperar_pasajeroViaje_por_cliente(cliente);
			assertEquals(pv.getEstado(),EstadoPasajeroViaje.aceptado);
			assertEquals(pv.getComision().getEstado(),EstadoComisionCobrada.pendiente);
				
			Cliente cliente1 = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 7);
			PasajeroViaje pv1=viaje.recuperar_pasajeroViaje_por_cliente(cliente1);
			assertEquals(pv1.getEstado(),EstadoPasajeroViaje.aceptado);
			assertEquals(pv1.getComision().getEstado(),EstadoComisionCobrada.pendiente);
			
		}
		
		//by jasmin y luz
		@Test
		public void testAceptarPostulanteCorrecto2(){
			// CANTIDAD DE ASIENTOS DEL AUTO ES 1
			// LAS SUBIDAS Y BAJADAS DE LOS DOS PASAJEROS SE SUPORPONEN PERO
			//COINCIDE LA BAJADA DE UNO CON LA SUBIDA DE OTRO
			//LOS DOS SON ACEPTADOS.
			
			//datos del vehiculo y cliente, para crear el vehiculo
			JSONObject json= crearVehiculo();
			try {
				//creo los datos en la tabla maneja
				assertTrue(this.daoviajes.NuevoVehiculo(json) );
			}catch(ExceptionViajesCompartidos E){
				fail(E.getMessage());
			}
				JSONObject json2 = TestViaje.crearViaje2();
			try {
				assertTrue( this.daoviajes.nuevoViaje(json2) );
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			JSONObject json3= this.crearPostulante5();
			JSONObject json4= this.crearPostulante6();
			
			try {
				assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
				assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json4) );
				
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			List viajes=this.daoviajes.selectAll("Viaje");
			Viaje viaje=(Viaje) viajes.get(0);
			try {
				assertTrue( this.daoviajes.aceptarPasajero(8, viaje.getId_viaje()));
				assertTrue( this.daoviajes.aceptarPasajero(9, viaje.getId_viaje()));
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			//compruebo estados
			Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 8);
			PasajeroViaje pv=viaje.recuperar_pasajeroViaje_por_cliente(cliente);
			assertEquals(pv.getEstado(),EstadoPasajeroViaje.aceptado);
			assertEquals(pv.getComision().getEstado(),EstadoComisionCobrada.pendiente);
				
			Cliente cliente1 = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 9);
			PasajeroViaje pv1=viaje.recuperar_pasajeroViaje_por_cliente(cliente1);
			assertEquals(pv1.getEstado(),EstadoPasajeroViaje.aceptado);
			assertEquals(pv1.getComision().getEstado(),EstadoComisionCobrada.pendiente);
			
		}
	
	//by jasmin y luz
	@Test
	public void testAceptarPostulanteIncorrecto(){
		// CANTIDAD DE ASIENTOS DEL AUTO ES 1
		// LAS SUBIDAS Y BAJADAS DE LOS DOS PASAJEROS SE SUPORPONEN
		//EL SEGUNDO NO ES ACEPTADO POR LA SUPERPOSICION.
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje2();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante1();
		JSONObject json4= this.crearPostulante2();
		
		try {
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json4) );
			
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		boolean bandera = false;
		try {
			assertTrue( this.daoviajes.aceptarPasajero(5, viaje.getId_viaje()));
			assertTrue( this.daoviajes.aceptarPasajero(6, viaje.getId_viaje()));
		} catch (ExceptionViajesCompartidos e) {
			if(e.toString().contains("ERROR: NO HAY SUFICIENTES ASIENTOS DISPONIBLES PARA EL TRAMO")){
				bandera=true;
			}else{
				fail("no tiro el error de asientos insuficientes");
			}
		}
		assertTrue(bandera);
		//compruebo estados
		Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 5);
		PasajeroViaje pv=viaje.recuperar_pasajeroViaje_por_cliente(cliente);
		assertEquals(pv.getEstado(),EstadoPasajeroViaje.aceptado);
		assertEquals(pv.getComision().getEstado(),EstadoComisionCobrada.pendiente);
		
		Cliente cliente1 = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 6);
		PasajeroViaje pv1=viaje.recuperar_pasajeroViaje_por_cliente(cliente1);
		assertEquals(pv1.getEstado(),EstadoPasajeroViaje.postulado);
		assertEquals(pv1.getComision().getEstado(),EstadoComisionCobrada.informativa);
		
	}
	
	@Test
	public void testRechazarPostulante1Correcto() {
		//test q envie un json correcto y tendria q andar bien
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		
		try {
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		try {
			assertTrue( this.daoviajes.rechazarPasajero(3, viaje.getId_viaje()) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 3);
		PasajeroViaje pv=viaje.recuperar_pasajeroViaje_por_cliente(cliente);
		assertEquals(pv.getEstado(),EstadoPasajeroViaje.rechazado);
		assertEquals(pv.getComision().getEstado(),EstadoComisionCobrada.desestimada);
	}
	
	@Test
	@SuppressWarnings({ "rawtypes" })
	public void testComisionPorRecorridoCorrecto() {
		//test q compara el metodo con el monto calculado en el viaje, tendrian que ser iguales
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		
		try {
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		PasajeroViaje pv=viaje.getPasajeros().get(0);
		float monto_cobrado=pv.getComision().getMonto();
		List <LocalidadViaje> lv=viaje.getLocalidades();
		Localidad inicio=lv.get(0).getLocalidad();
		Localidad destino= lv.get( (lv.size()-1) ).getLocalidad();
		float monto_a_cobrar=0;		
		try {
			monto_a_cobrar = this.daoviajes.comision_por_recorrido(inicio, destino, viaje.getId_viaje());
		} catch (ExceptionViajesCompartidos e) {
			fail();
			e.printStackTrace();
		}
		assertEquals(monto_cobrado,monto_a_cobrar);
	}
	
	@Test
	@SuppressWarnings({ "rawtypes" })
	public void testComisionPorRecorridoINCorrecto1() {
		//test q envia un id de viaje inexistente
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		
		try {
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		List <LocalidadViaje> lv=viaje.getLocalidades();
		Localidad inicio=lv.get(0).getLocalidad();
		Localidad destino= lv.get( (lv.size()-1) ).getLocalidad();

		boolean bandera=false;
		try {
			this.daoviajes.comision_por_recorrido(inicio, destino, 1);
			fail();
		} catch (ExceptionViajesCompartidos e) {
			bandera=true;
		}
		assertTrue(bandera);
	}
	
	@Test
	@SuppressWarnings({ "rawtypes" })
	public void testComisionPorRecorridoINCorrecto2() {
		//test q envia las localidades al reves (= que en desorden)
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		
		try {
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		PasajeroViaje pv=viaje.getPasajeros().get(0);
		float monto_cobrado=pv.getComision().getMonto();
		List <LocalidadViaje> lv=viaje.getLocalidades();
		Localidad inicio=lv.get(0).getLocalidad();
		Localidad destino= lv.get( (lv.size()-1) ).getLocalidad();
		float monto_a_cobrar=0;	
		boolean bandera=false;
		try {
			monto_a_cobrar = this.daoviajes.comision_por_recorrido(destino, inicio, viaje.getId_viaje());
			fail();
		} catch (ExceptionViajesCompartidos e) {
			bandera=true;
		}
		assertTrue(bandera);
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testNuevoPasajeroViajeCorrecto2() {
		//test q hace que 2 pasajeros se postulen al mismo viaje
		// tambien verifica que el metodo listarPasajerosPorViaje funque bien
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		JSONObject json4= this.crearPostulante();
		json4.remove("cliente");
		json4.put("cliente",4);
		try {
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json4) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		List<PasajeroViaje>l=this.daoviajes.listarPasajerosPorViaje(viaje.getId_viaje());
		assertEquals(l.size(),2);
	}
	
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testNuevoVariosPasajerosViajeCorrecto0() {
		//test que envia un pasajeroViaje que pide 2 asientos en un viaje
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		json3.put("nro_asientos", 2);
		
		try {
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		try {
			assertTrue( this.daoviajes.aceptarPasajero(3, viaje.getId_viaje()) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 3);
		PasajeroViaje pv=viaje.recuperar_pasajeroViaje_por_cliente(cliente);
		assertEquals(pv.getEstado(),EstadoPasajeroViaje.aceptado);
		assertEquals(pv.getComision().getEstado(),EstadoComisionCobrada.pendiente);
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testNuevoVariosPasajerosViajeINCorrecto0() {
		//test que envia un pasajeroViaje que pide 2 asientos en un viaje, pero ya no quedan esa cantidad de asientos
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		
		JSONObject json4= this.crearPostulante(); 		//pasajero que no puede ser aceptado
		json4.remove("cliente");
		json4.put("cliente", 4);
		json4.put("nro_asientos", 2);
		try {
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json4) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		
		try {
			assertTrue( this.daoviajes.aceptarPasajero(3, viaje.getId_viaje()) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		boolean bandera=false;
		try {
			assertTrue( this.daoviajes.aceptarPasajero(4, viaje.getId_viaje()) );
			fail("tenia q tirar errore de que no hay lugares suficientes");
		} catch (ExceptionViajesCompartidos e) {
			if(!e.getMessage().contains("NO HAY SUFICIENTES ASIENTOS DISPONIBLES")){
				fail("tenia que tirar el error: 'ERROR: NO HAY SUFICIENTES ASIENTOS DISPONIBLES PARA EL TRAMO' y mostro el msj: "+e.getMessage());
			}
			bandera=true;
		}
		assertTrue(bandera);
	}
	
	@Test
	@SuppressWarnings({ "unchecked" })
	public void testNuevoPasajeroViajeINCorrecto2() {
		//test que envia postula al conductor del viaje como pasajero
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		json3.remove("cliente");
		json3.put("cliente", 2);
		boolean bandera=false;
		try {
			this.daoviajes.Cliente_se_postula_en_viaje(json3);
			fail();
		} catch (ExceptionViajesCompartidos e) {
			bandera=true;
		}
		assertTrue(bandera);
	}
	
	@Test
	public void testNuevoPasajeroViajeINCorrecto3() {
		//test que se postula a un viaje q no existe
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		json3.remove("viaje");
		boolean bandera=false;
		try {
			this.daoviajes.Cliente_se_postula_en_viaje(json3);
			fail();
		} catch (ExceptionViajesCompartidos e) {
			bandera=true;
			assertTrue(e.toString(),bandera);
		}
		//assertTrue("",bandera);
	}
        
        //by fede
	@SuppressWarnings({ "unchecked", "rawtypes" })
         public void testCancelarParticipacionTodoElViajeCorrecto() {
		//test cancelar participacion
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
                Integer id_viaje = viaje.getId_viaje();
		JSONObject json3= this.crearPostulante();
                json3.remove("cliente");
		json3.put("cliente",5);
		JSONObject json4= this.crearPostulante();
		json4.remove("cliente");
		json4.put("cliente",4);
		try { 
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json4) );
                        assertTrue( this.daoviajes.aceptarPasajero(4, id_viaje ));
                        assertTrue( this.daoviajes.aceptarPasajero(5, id_viaje));
                        assertTrue ( this.daoviajes.cancelarParticipacionEnViaje(id_viaje, 4));
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		List viajes2=this.daoviajes.selectAll("Viaje");
		Viaje viaje2=(Viaje) viajes2.get(0);
		List<PasajeroViaje>l=this.daoviajes.listarPasajerosPorViaje(viaje2.getId_viaje());
                 int cancelados = 0;
                for (int j=0; j<l.size();j++){
                   PasajeroViaje pasaj = l.get(j);
                   if( pasaj.getEstado() == EstadoPasajeroViaje.cancelado ){
                        cancelados++;
                   }
                }
		assertEquals(cancelados,1);
	}
         
         
         //by fede
         @SuppressWarnings({ "unchecked", "rawtypes" })
         public void testCancelarParticipacionIntermedioCorrecto() {
		//test cancelar participacion
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViajeQueFaltaMucho();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
                Integer id_viaje = viaje.getId_viaje();
		JSONObject json3= this.crearPostulante();
                json3.remove("cliente");
		json3.put("cliente",5);
		JSONObject json4= this.crearPostulante();
		json4.remove("cliente");
		json4.put("cliente",4);
                json4.remove("localidad_subida");
                json4.remove("localidad_bajada");
                json4.put("localidad_subida",3427202);
                json4.put("localidad_bajada",3427203);
		try { 
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json4) );
                        assertTrue( this.daoviajes.aceptarPasajero(4, id_viaje ));
                        assertTrue( this.daoviajes.aceptarPasajero(5, id_viaje));
                        assertTrue ( this.daoviajes.cancelarParticipacionEnViaje(id_viaje, 4));
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		List viajes2=this.daoviajes.selectAll("Viaje");
		Viaje viaje2=(Viaje) viajes2.get(0);
		List<PasajeroViaje>l=this.daoviajes.listarPasajerosPorViaje(viaje2.getId_viaje());
                 int cancelados = 0;
                for (int j=0; j<l.size();j++){
                   PasajeroViaje pasaj = l.get(j);
                   if( pasaj.getEstado() == EstadoPasajeroViaje.cancelado ){
                        cancelados++;
                   }
                }
		assertEquals(cancelados,1);
	}
         
         
         //by fede
         @SuppressWarnings({ "unchecked", "rawtypes" })
		public void testCancelarViajeSinSancion() {
		//test cancelar viaje
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViajeQueFaltaMucho();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
                Integer id_viaje = viaje.getId_viaje();
                int id_chofer = viaje.getConductor().getId_usuario();
		JSONObject json3= this.crearPostulante();
                json3.remove("cliente");
		json3.put("cliente",5);
		JSONObject json4= this.crearPostulante();
		json4.remove("cliente");
		json4.put("cliente",4);
                json4.remove("localidad_subida");
                json4.remove("localidad_bajada");
                json4.put("localidad_subida",3427202);
                json4.put("localidad_bajada",3427203);
		try { 
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json4) );
                      
                        assertTrue ( this.daoviajes.cancelarViaje(id_viaje, id_chofer) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		Viaje viaje2 = (Viaje) this.daoviajes.buscarPorPrimaryKey(viaje, id_viaje);
		assertEquals(viaje2.getEstado(),EstadoViaje.cancelado);
	}
         
         //by fede
         @SuppressWarnings({ "unchecked", "rawtypes" })
		public void testCancelarViajeConSancion() {
		//test cancelar viaje
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
                Integer id_viaje = viaje.getId_viaje();
                int id_chofer = viaje.getConductor().getId_usuario();
		JSONObject json3= this.crearPostulante();
                json3.remove("cliente");
		json3.put("cliente",5);
		JSONObject json4= this.crearPostulante();
		json4.remove("cliente");
		json4.put("cliente",4);
                json4.remove("localidad_subida");
                json4.remove("localidad_bajada");
                json4.put("localidad_subida",3427202);
                json4.put("localidad_bajada",3427203);
		try { 
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json4) );
                        assertTrue( this.daoviajes.aceptarPasajero(4, id_viaje ));
                        assertTrue( this.daoviajes.aceptarPasajero(5, id_viaje));
                        assertTrue ( this.daoviajes.cancelarViaje(id_viaje, id_chofer) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		Viaje viaje2 = (Viaje) this.daoviajes.buscarPorPrimaryKey(viaje, id_viaje);
		assertEquals(viaje2.getEstado(),EstadoViaje.cancelado);
	}
                
               
        @Test
        public void testDejarComentario() throws ExceptionViajesCompartidos{
            
            
            String texto = "Este es un comentario de prueba para ver si persiste y ademas puede guardar muchos caracteres porque le puse string y no text y blablablblablabllasjdalsjdasjdkasjdkasdksajdkasjdkajskd";
            Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 3);
            
            JSONObject json2 = TestViaje.crearViaje();
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}
            
            
            try {	
                    assertTrue( this.daoviajes.nuevoViaje(json2) );
                } catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
            
                 List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
                Integer id_viaje = viaje.getId_viaje();
                JSONObject json_comentario = new JSONObject();
                json_comentario.put("id_viaje", viaje.getId_viaje());
                json_comentario.put("id_cliente", cliente.getId_usuario());
                json_comentario.put("texto", texto);
                try {	
                    assertTrue( this.daoviajes.dejarComentarioEnViaje(json_comentario) );
                } catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
                
                
                
                
        }
        
        @Test
        public void testDejarComentarioINCorrecto() throws ExceptionViajesCompartidos{
            
            
            String texto = "Este es un comentario de prueba para ver si persiste y ademas puede guardar muchos caracteres porque le puse string y no text y blablablblablabllasjdalsjdasjdkasjdkasdksajdkasjdkajskd";
            Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 3);
            
            JSONObject json2 = TestViaje.crearViaje();
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}
            
            
            try {	
                    assertTrue( this.daoviajes.nuevoViaje(json2) );
                } catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
            
                 List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
                Integer id_viaje = viaje.getId_viaje();
                JSONObject json_comentario = new JSONObject();
                json_comentario.put("id_viaje", viaje.getId_viaje());
                json_comentario.put("id_cliente", -1);
                json_comentario.put("texto", texto);
                boolean bandera = false;
                try {
                    bandera =false;
                    bandera = this.daoviajes.dejarComentarioEnViaje(json_comentario) ;
                } catch (ExceptionViajesCompartidos e) {
			if(!e.getMessage().contains("CLIENTE NO EXISTE")){
				fail("No tiro excepcion que no existe cliente");
			}
			bandera=true;
		}
		assertTrue(bandera);
	}
         
                
                
                
        
        
          @Test
        public void testgetComentarios() throws ExceptionViajesCompartidos{
                        
            String texto = "Este es un comentario de prueba para ver si persiste y ademas puede guardar muchos caracteres porque le puse string y no text y blablablblablabllasjdalsjdasjdkasjdkasdksajdkasjdkajskd";
            Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 3);
                JSONObject json2 = TestViaje.crearViaje();
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}           
            
            try {	
                    assertTrue( this.daoviajes.nuevoViaje(json2) );
                } catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
            
                 List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
                Integer id_viaje = viaje.getId_viaje();
                JSONObject json_comentario = new JSONObject();
                json_comentario.put("id_viaje", id_viaje);
                json_comentario.put("id_cliente", cliente.getId_usuario());
                json_comentario.put("texto", texto);
                try {	
                    assertTrue( this.daoviajes.dejarComentarioEnViaje(json_comentario) );
                } catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
                //2do comentario
                String texto2 = "blablablblablabllasjdalsjdasjdkasjdkasdksajdkasjdkajskd";
                Cliente cliente2 = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 6);
                int id_cliente= cliente2.getId_usuario();
                JSONObject json_comentario2 = new JSONObject();
                json_comentario2.put("id_viaje", id_viaje);
                json_comentario2.put("id_cliente", cliente2.getId_usuario());
                json_comentario2.put("texto", texto2);
                try {	
                    assertTrue( this.daoviajes.dejarComentarioEnViaje(json_comentario2) );
                } catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
                //3er comentario
                String texto3 = "comentario de chofer";
                Cliente cliente3 = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 2);       
                		
            
                
                JSONObject json_comentario3 = new JSONObject();
                json_comentario3.put("id_viaje", id_viaje);
                json_comentario3.put("id_cliente", cliente3.getId_usuario());
                json_comentario3.put("texto", texto3);
                try {	
                    assertTrue( this.daoviajes.dejarComentarioEnViaje(json_comentario3) );
                } catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
                
                List <ComentarioViaje> lista = null;
                lista = this.daoviajes.getComentariosViaje(id_viaje);                
                assertEquals(lista.size(), 3);
                
                
        }
        
        //by fede
         @SuppressWarnings({ "unchecked", "rawtypes" })
		public void testCancelarViajeYNotificarSeguidores() {
		//test cancelar viaje
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViajeQueFaltaMucho();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
                Integer id_viaje = viaje.getId_viaje();
                int id_chofer = viaje.getConductor().getId_usuario();
		JSONObject json3= this.crearPostulante();
                json3.remove("cliente");
		json3.put("cliente",5);
		JSONObject json4= this.crearPostulante();
		json4.remove("cliente");
		json4.put("cliente",4);
                json4.remove("localidad_subida");
                json4.remove("localidad_bajada");
                json4.put("localidad_subida",3427202);
                json4.put("localidad_bajada",3427203);
		try { 
			assertTrue( this.daoviajes.seguirViaje(viaje.getId_viaje(),5) );
			assertTrue( this.daoviajes.seguirViaje(viaje.getId_viaje(),4) );
 
                      
                        assertTrue ( this.daoviajes.cancelarViaje(id_viaje, id_chofer) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		Viaje viaje2 = (Viaje) this.daoviajes.buscarPorPrimaryKey(viaje, id_viaje);
		assertEquals(viaje2.getEstado(),EstadoViaje.cancelado);
	}
        
        
        //by fede
		@Test
		public void testAceptarPostulanteCorrectoYDejaDeSeguirViaje() {
			//CANTIDAD DE ASIENTOS DEL AUTO 1
			//SE POSTULA Y ACEPTA A 1 PASAJERO CORRECTAMENTE
			
			//datos del vehiculo y cliente, para crear el vehiculo
			JSONObject json= crearVehiculo();
			try {
				//creo los datos en la tabla maneja
				assertTrue(this.daoviajes.NuevoVehiculo(json) );
			}catch(ExceptionViajesCompartidos E){
				fail(E.getMessage());
			}
				JSONObject json2 = TestViaje.crearViaje2();
			try {
				assertTrue( this.daoviajes.nuevoViaje(json2) );
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			JSONObject json3= this.crearPostulante();
			List viajes=this.daoviajes.selectAll("Viaje");
			Viaje viaje=(Viaje) viajes.get(0);
                        
			try {
                                assertTrue(this.daoviajes.seguirViaje(viaje.getId_viaje(), 3));
				assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			
			try {
				assertTrue( this.daoviajes.aceptarPasajero(3, viaje.getId_viaje()) );
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 3);
			PasajeroViaje pv=viaje.recuperar_pasajeroViaje_por_cliente(cliente);
			assertEquals(pv.getEstado(),EstadoPasajeroViaje.aceptado);
			assertEquals(pv.getComision().getEstado(),EstadoComisionCobrada.pendiente);
		}
                
                @Test
	public void testRechazarPostulante1CorrectoYDejaDeSerSeguidor() {
		//test q envie un json correcto y tendria q andar bien
		
		//datos del vehiculo y cliente, para crear el vehiculo
		JSONObject json= crearVehiculo();
		try {
			//creo los datos en la tabla maneja
			assertTrue(this.daoviajes.NuevoVehiculo(json) );
		}catch(ExceptionViajesCompartidos E){
			fail(E.getMessage());
		}

		JSONObject json2 = TestViaje.crearViaje();
		try {
			assertTrue( this.daoviajes.nuevoViaje(json2) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		JSONObject json3= this.crearPostulante();
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
                
		try {
                        assertTrue(this.daoviajes.seguirViaje(viaje.getId_viaje(), 3));
			assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		
		
		try {
			assertTrue( this.daoviajes.rechazarPasajero(3, viaje.getId_viaje()) );
		} catch (ExceptionViajesCompartidos e) {
			fail(e.getMessage());
		}
		Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 3);
		PasajeroViaje pv=viaje.recuperar_pasajeroViaje_por_cliente(cliente);
		assertEquals(pv.getEstado(),EstadoPasajeroViaje.rechazado);
		assertEquals(pv.getComision().getEstado(),EstadoComisionCobrada.desestimada);
	}
        
        
         @SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject crearPostulante() {
		/*
		 * JSON{
		 * "CLIENTE":ID_CLIENTE,
		 * "VIAJE":ID_VIAJE,
		 * "LOCALIDAD_SUBIDA":ID_LOCALIDAD,
		 * "LOCALIDAD_BAJADA": ID_LOCALIDAD
		 * } 
		 */
		JSONObject json =new JSONObject();
		json.put("cliente", 3);
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		json.put("viaje", viaje.getId_viaje());
		json.put("localidad_subida", 3427200);
		json.put("localidad_bajada", 3427205);
		return json;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject crearPostulante1() {
		JSONObject json =new JSONObject();
		json.put("cliente", 5);
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		json.put("viaje", viaje.getId_viaje());
		json.put("localidad_subida", 3427201);
		json.put("localidad_bajada", 3427203);
		return json;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject crearPostulante2() {
		JSONObject json =new JSONObject();
		json.put("cliente", 6);
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		json.put("viaje", viaje.getId_viaje());
		json.put("localidad_subida", 3427202);
		json.put("localidad_bajada", 3427204);
		return json;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject crearPostulante3() {
		JSONObject json =new JSONObject();
		json.put("cliente", 4);
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		json.put("viaje", viaje.getId_viaje());
		json.put("localidad_subida", 3427200);
		json.put("localidad_bajada", 3427202);
		return json;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject crearPostulante4() {
		JSONObject json =new JSONObject();
		json.put("cliente", 7);
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		json.put("viaje", viaje.getId_viaje());
		json.put("localidad_subida", 3427204);
		json.put("localidad_bajada", 3427205);
		return json;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject crearPostulante5() {
		JSONObject json =new JSONObject();
		json.put("cliente", 8);
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		json.put("viaje", viaje.getId_viaje());
		json.put("localidad_subida", 3427204);
		json.put("localidad_bajada", 3427205);
		return json;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JSONObject crearPostulante6() {
		JSONObject json =new JSONObject();
		json.put("cliente", 9);
		List viajes=this.daoviajes.selectAll("Viaje");
		Viaje viaje=(Viaje) viajes.get(0);
		json.put("viaje", viaje.getId_viaje());
		json.put("localidad_subida", 3427202);
		json.put("localidad_bajada", 3427204);
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject crearVehiculo(){
		JSONObject json= new JSONObject();
		json.put("conductor", 2);
		JSONObject vehiculo= new JSONObject();
		vehiculo.put("patente", "abd123");
		vehiculo.put("anio", 1992);
		vehiculo.put("modelo", "viejo");
		vehiculo.put("marca", "mondeo");
		vehiculo.put("seguro", 'C');
		vehiculo.put("color","arcoiris");
		vehiculo.put("aire", 'S');
		vehiculo.put("asientos",5);
		json.put("vehiculo", vehiculo);
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject crearViaje(){
		/*
		{ "LOCALIDADES": {"ORIGEN":"ID_LOCALIDAD","INTERMEDIO":ID_LOCALIDAD,.....,"DESTINO":ID_LOCALIDAD},
			 "VEHICULO": ID_VEHICULO,
			 "VIAJE": {FECHA_inicio, HS_SALIDA, CANTIDAD_ASIENTOS, NOMBRE_AMIGABLE, COSTO_VIAJE},
			 "VUELTA": {FECHA_SALIDA,HS_SALIDA,CANTIDAD_ASIENTOS, NOMBRE_AMIGABLE},
			 "CLIENTE":ID_CLIENTE
			 }
		*/
		JSONObject viaje = new JSONObject();
		JSONObject json2 = new JSONObject();
		json2.put("vehiculo", "abd123");
		json2.put("cliente", 2);
		Timestamp fecha = new Timestamp((new java.util.Date()).getTime());
		fecha.setMinutes(59);
		viaje.put("fecha_inicio", fecha);
		viaje.put("cantidad_asientos", 2);
		viaje.put("precio", new Float(50.0));
		viaje.put("nombre_amigable", "prueba viaje");
		json2.put("viaje", viaje);
		JSONObject localidades= new JSONObject();
		localidades.put("origen",3427200 );
		localidades.put("destino",3427205 );
		JSONArray intermedio= new JSONArray();
		intermedio.add(3427201);
		intermedio.add(3427202);
		intermedio.add(3427203);
		intermedio.add(3427204);
		localidades.put("intermedios", intermedio);
		json2.put("localidades", localidades);
		return json2;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject crearViaje2(){
		/*
		{ "LOCALIDADES": {"ORIGEN":"ID_LOCALIDAD","INTERMEDIO":ID_LOCALIDAD,.....,"DESTINO":ID_LOCALIDAD},
			 "VEHICULO": ID_VEHICULO,
			 "VIAJE": {FECHA_inicio, HS_SALIDA, CANTIDAD_ASIENTOS, NOMBRE_AMIGABLE, COSTO_VIAJE},
			 "VUELTA": {FECHA_SALIDA,HS_SALIDA,CANTIDAD_ASIENTOS, NOMBRE_AMIGABLE},
			 "CLIENTE":ID_CLIENTE
			 }
		*/
		JSONObject viaje = new JSONObject();
		JSONObject json2 = new JSONObject();
		json2.put("vehiculo", "abd123");
		json2.put("cliente", 2);
		Timestamp fecha = new Timestamp((new java.util.Date()).getTime());
		fecha.setMonth(11);
		viaje.put("fecha_inicio", fecha);
		viaje.put("cantidad_asientos", 1);
		viaje.put("precio", new Float(50.0));
		viaje.put("nombre_amigable", "prueba viaje");
		json2.put("viaje", viaje);
		JSONObject localidades= new JSONObject();
		localidades.put("origen",3427200 );
		localidades.put("destino",3427205 );
		JSONArray intermedio= new JSONArray();
		intermedio.add(3427201);
		intermedio.add(3427202);
		intermedio.add(3427203);
		intermedio.add(3427204);
		localidades.put("intermedios", intermedio);
		json2.put("localidades", localidades);
		return json2;
	}
        
        @SuppressWarnings("unchecked")
	public static JSONObject crearViajeQueFaltaMucho(){
		/*
		{ "LOCALIDADES": {"ORIGEN":"ID_LOCALIDAD","INTERMEDIO":ID_LOCALIDAD,.....,"DESTINO":ID_LOCALIDAD},
			 "VEHICULO": ID_VEHICULO,
			 "VIAJE": {FECHA_inicio, HS_SALIDA, CANTIDAD_ASIENTOS, NOMBRE_AMIGABLE, COSTO_VIAJE},
			 "VUELTA": {FECHA_SALIDA,HS_SALIDA,CANTIDAD_ASIENTOS, NOMBRE_AMIGABLE},
			 "CLIENTE":ID_CLIENTE
			 }
		*/
		JSONObject viaje = new JSONObject();
		JSONObject json2 = new JSONObject();
		json2.put("vehiculo", "abd123");
		json2.put("cliente", 2);
		Timestamp fecha = new Timestamp((new java.util.Date()).getTime());
		fecha.setMonth(11);
		viaje.put("fecha_inicio", fecha);
		viaje.put("cantidad_asientos", 2);
		viaje.put("precio", new Float(50.0));
		viaje.put("nombre_amigable", "viaje Tardio");
		json2.put("viaje", viaje);
		JSONObject localidades= new JSONObject();
		localidades.put("origen",3427200 );
		localidades.put("destino",3427205 );
		JSONArray intermedio= new JSONArray();
		intermedio.add(3427201);
		intermedio.add(3427202);
		intermedio.add(3427203);
		intermedio.add(3427204);
		localidades.put("intermedios", intermedio);
		json2.put("localidades", localidades);
		return json2;
	}
	@SuppressWarnings("unchecked")
	public static JSONObject crearViajeSinSaldo(){
		
		JSONObject viaje = new JSONObject();
		JSONObject json2 = new JSONObject();
		json2.put("vehiculo", "abd123");
		json2.put("cliente", 3);
		Timestamp fecha = new Timestamp((new java.util.Date()).getTime());
		
		viaje.put("fecha_inicio", fecha);
		viaje.put("cantidad_asientos", 1);
		viaje.put("precio", new Float(50.0));
		viaje.put("nombre_amigable", "prueba viaje");
		json2.put("viaje", viaje);
		JSONObject localidades= new JSONObject();
		localidades.put("origen",3427200 );
		localidades.put("destino",3427205 );
		JSONArray intermedio= new JSONArray();
		intermedio.add(3427201);
		intermedio.add(3427202);
		intermedio.add(3427203);
		intermedio.add(3427204);
		localidades.put("intermedios", intermedio);
		json2.put("localidades", localidades);
		return json2;
	}
        
        @SuppressWarnings("unchecked")
	public static JSONObject crearVehiculo2(){
		JSONObject json= new JSONObject();
		json.put("conductor", 3);
		JSONObject vehiculo= new JSONObject();
		vehiculo.put("patente", "abd123");
		vehiculo.put("anio", 1992);
		vehiculo.put("modelo", "viejo");
		vehiculo.put("marca", "mondeo");
		vehiculo.put("seguro", 'C');
		vehiculo.put("color","arcoiris");
		vehiculo.put("aire", 'S');
		vehiculo.put("asientos",5);
		json.put("vehiculo", vehiculo);
		return json;
	}
}

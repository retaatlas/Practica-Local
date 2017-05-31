package gestionComisiones.controlador;

import gestionComisiones.modelo.Comision;
import gestionComisiones.modelo.EstadoComisionCobrada;
import gestionComisiones.modelo.Pago;
import gestionUsuarios.controlador.DAOAdministracionUsuarios;
import gestionUsuarios.modelo.Cliente;
import gestionUsuarios.modelo.Persona;
import gestionViajes.controlador.DAOViajes;
import gestionViajes.controlador.TestViaje;
import static gestionViajes.controlador.TestViaje.crearVehiculo;
import gestionViajes.modelo.EstadoPasajeroViaje;
import gestionViajes.modelo.PasajeroViaje;
import gestionViajes.modelo.Viaje;
import java.sql.Timestamp;
import junit.framework.TestCase;
import otros.ExceptionViajesCompartidos;
import java.util.List;
import javax.persistence.EntityManager;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;


public class TestComision extends TestCase{
    
    protected DAOViajes daoviajes = DAOViajes.getInstance();
	protected DAOComisiones daocomisiones = DAOComisiones.getInstance();
	
		
            
	@Before
	public void setUp() throws Exception {
		//este metodo se ejecuta antes de cada "parte" del test, osea antes de cada metodo
		//sirve para inicializar variables asi todos los test arrancan en el mismo entorno 
		
		//esto q sigue es codigo para vaciar la BD y que todas las pruebas corran en el mismo entorno
		this.daoviajes.vaciarTabla("Calificacion");
		this.daoviajes.vaciarTabla("Sancion");
                this.daoviajes.vaciarTabla("MovimientoPuntos");
                this.daoviajes.vaciarTabla("PasajeroViaje");
		this.daoviajes.vaciarTabla("ComisionCobrada");
		this.daoviajes.vaciarTabla("LocalidadViaje");
		//this.daoviajes.borrarRelacionesEntreViajes();
		this.daoviajes.vaciarTabla("Viaje");
		this.daoviajes.vaciarTabla("Maneja");
		this.daoviajes.vaciarTabla("Vehiculo");
    }
	
		@Test
		public void test1NuevaComision() {
			
			JSONObject datos_comision = new JSONObject();
			datos_comision.put("limite_inferior", 10000);
			datos_comision.put("limite_superior", 20000);
			datos_comision.put("precio", 200);
			try {
				this.daocomisiones.nuevaComision(datos_comision);
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			List<Comision> lista=this.daocomisiones.getComisionesVigentes();
			//busco la q acabo de agregar
			boolean encontrado=false;
			for(Comision c: lista){
				if(c.getLimite_inferior()==10000 && c.getLimite_superior()==20000 && c.getPrecio()==200){
					encontrado=true;
				}
			}
			assertTrue(encontrado);
		}
		
		@Test
		public void test2ModificarComision() {
			this.test1NuevaComision();
			List<Comision> lista=this.daocomisiones.getComisionesVigentes();
			Integer index=null;
			//busco la q acabo de agregar
			for(Comision c: lista){
				if(c.getLimite_inferior()==10000 && c.getLimite_superior()==20000 && c.getPrecio()==200){
					index=c.getId();
				}
			}
			if(index==null){
				fail("no se encontro la comision a modificar");
			}
			
			JSONObject datos_comision = new JSONObject();
			datos_comision.put("comision", index);
			datos_comision.put("limite_inferior", 15000);
			datos_comision.put("limite_superior", 25000);
			datos_comision.put("precio", 250);
			try {
				this.daocomisiones.modificarComision(datos_comision);
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			Comision comision = (Comision) this.daocomisiones.buscarPorPrimaryKey(new Comision(), index);
			if(comision==null){
				fail("no se encontro la comision modificada");
			}
			assertEquals((Integer)15000,comision.getLimite_inferior());
			assertEquals((Integer)25000,comision.getLimite_superior());
			//assertEquals(250.0,comision.getPrecio());
			try {
				this.daocomisiones.deletePorPrimaryKey(new Comision(), index);
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
		}
		
		@Test
		public void test3FinalizarComision() {
			List<Comision> lista=this.daocomisiones.getComisionesVigentes();
			Integer index=null;
			//busco la q acabo de agregar
			for(Comision c: lista){
				if(c.getLimite_inferior()==10000 && c.getLimite_superior()==20000 && c.getPrecio()==200){
					index=c.getId();
				}
			}
			if(index==null){
				fail("no se encontro la comision a modificar");
			}
			
			try {
				this.daocomisiones.FinalizarComision(index);
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			Comision comision=(Comision)this.daocomisiones.buscarPorPrimaryKey(new Comision(), index);
			assertNotNull(comision.getFecha_fin());
			try {
				this.daocomisiones.deletePorPrimaryKey(new Comision(), index);
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
		}
	
        @Test
		public void testCobrarComisionCorrecto() throws ExceptionViajesCompartidos{
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
	
			
			try {
				//ACA YA LE SETTEE LA COMISION AL PASAJERO
				assertTrue( this.daoviajes.Cliente_se_postula_en_viaje(json3) );
				
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			
			List viajes=this.daoviajes.selectAll("Viaje");
			Viaje viaje=(Viaje) viajes.get(0);
			try { 
				assertTrue( this.daoviajes.aceptarPasajero(8, viaje.getId_viaje()));
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
			//compruebo estados
			Cliente cliente = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 8);
			PasajeroViaje pv = viaje.recuperar_pasajeroViaje_por_cliente(cliente);
			assertEquals(pv.getEstado(),EstadoPasajeroViaje.aceptado);
			assertEquals(pv.getComision().getEstado(),EstadoComisionCobrada.pendiente);
			boolean bandera = false ;
                        //verifico si le cobré la comision
                        if( (pv.getEstado().equals(EstadoPasajeroViaje.aceptado))	&& (pv.getComision().getEstado().equals(EstadoComisionCobrada.pendiente)) ){
                            this.daocomisiones.cobrarComision(pv);
                            
                            
                         if(pv.getComision().getEstado().equals(EstadoComisionCobrada.pagado)){
                                 bandera = true;
                         }else{ bandera= false;}
                        }
            assertTrue(bandera);
		}
		
    
   
    @Test
		public void testAcreditarPago(){
                  		Cliente cliente_busco= (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 2);
                        assertTrue(daocomisiones.sumarSaldo(cliente_busco.getId_usuario(), 200));
                        //recupero con saldo nuevo
                        //Cliente cliente_actualizado = (Cliente) this.daoviajes.buscarPorPrimaryKey(new Cliente(), 2);
                        //float nuevo_saldo= cliente_actualizado.getSaldo();
                        //assertEquals(nuevo_saldo, 105);
		}
       
        
        
        
        
        
        
        //---------------------------------------------------------------------------//

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
}


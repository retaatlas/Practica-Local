package gestionUsuarios.controlador;

import gestionUsuarios.controlador.*;
import gestionUsuarios.controlador.*;
import gestionUsuarios.controlador.*;
import gestionUsuarios.controlador.*;
import gestionUsuarios.controlador.*;
import gestionUsuarios.controlador.*;
import java.util.ArrayList;
import java.util.List;
import gestionUsuarios.modelo.*;
import org.junit.Test;

import gestionUsuarios.modelo.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import junit.framework.TestCase;
import org.json.simple.JSONObject;
import org.junit.Before;
import otros.ExceptionViajesCompartidos;

public class TestAdministracionUsuarios extends TestCase {
	protected DAONotificaciones dao = DAONotificaciones.getInstance();
	protected DAOAdministracionUsuarios daoAdmUsu = DAOAdministracionUsuarios.getInstance();
        
        
	
        
        
	@Test
	public void testGetNotificaciones() throws ExceptionViajesCompartidos {
		List<Notificacion> notificaciones=this.dao.getNotificaciones(2);		//2=id_cliente
		assertEquals(notificaciones.size(),3);		//3 = cantidad de notificaciones que deberia tener
	}
	
	@Test
	public void testGetNotificacionesNoLeidas() throws ExceptionViajesCompartidos {
		List<Notificacion> notificaciones=this.dao.getNotificacionesNoLeidas(2);
		assertEquals(notificaciones.size(),1);
	}
	
	@Test
	public void testgetCantidadNotificacionesNoLeidas() throws ExceptionViajesCompartidos {
		Integer cant_not=this.dao.getCantidadNotificacionesNoLeidas(2);
		System.out.println("cant_not =" +cant_not);
		//assertEquals(cant_not,2);
	}
	
	@Test
	public void testSetNotificacionesLeidas() throws ExceptionViajesCompartidos {
		List<Integer> lista= new ArrayList<Integer>();
		lista.add(53);
		lista.add(103);
		boolean b=this.dao.setNotificacionesLeidas(2,lista);
		assertTrue(b);
	}
	
	@Test
        public void testNuevoSponsor() throws ExceptionViajesCompartidos{
            
            //Lo malo es que no puedo hacer BORRAR TABLAS porque sino borro usuarios!!!
            //Por FK no puedo vaciar sponsor.
            
            
            Persona p = (Persona) this.daoAdmUsu.buscarPorPrimaryKey(new Persona(), 2);
            JSONObject persona = new JSONObject();
            persona.put("id_persona", p.getId_persona());
            persona.put("nombres", p.getNombres());
            persona.put("apellidos", p.getApellidos());
            persona.put("tipo_doc", p.getTipo_doc());
            persona.put("nro_doc", p.getNro_doc());
            
            Date fn=   p.getFecha_nacimiento();
            String fn_str = String.valueOf(fn);
            persona.put("fecha_nacimiento", fn_str);
            persona.put("sexo", "M");            
            persona.put("domicilio", "aaaaaa 1111");
            persona.put("telefono", "99999");
            persona.put("descripcion", "descripcion!!!");
            persona.put("estado", "activo");
	
            Sponsor sponsor = new Sponsor();
            sponsor.setCuit(355436607);
            sponsor.setRubro("Informática");
            sponsor.setNombre_usuario("juan_cardona_sponsor");
            sponsor.setPassword("sponsorpass");
            sponsor.setEmail("juancardona@informatica.com");
            sponsor.setId_usuario(Integer.MIN_VALUE);
            
            JSONObject sp = new JSONObject();
            sp.put("rubro", sponsor.getRubro());
            sp.put("CUIT", sponsor.getCuit());
            sp.put("estado", "activo");
            sp.put("email", sponsor.getEmail());
            sp.put("nombre_usuario", sponsor.getNombre_usuario());
            sp.put("password", sponsor.getPassword());
            sp.put("id_usuario", sponsor.getId_usuario());
            
            
            JSONObject json = new JSONObject();
            json.put("persona",persona );
            json.put("sponsor",sp);
            
            
            try {
				this.daoAdmUsu.nuevoSponsor(json);
			} catch (ExceptionViajesCompartidos e) {
				fail(e.getMessage());
			}
            boolean bandera = false;
            Sponsor sp_recuperado = (Sponsor) this.daoAdmUsu.buscarPorClaveCandidata("Sponsor", "juan_cardona_sponsor");
            if ( (sp_recuperado.getNombre_usuario().equals("juan_cardona_sponsor")) && (sp_recuperado.getRubro().equals("Informática")) ){
                bandera = true;
            }
            assertTrue(bandera);
        }
}

package gestionUsuarios.controlador;

import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.jpa.JpaHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gestionUsuarios.modelo.*;
import otros.*;

@SuppressWarnings("unused")
public class DAOAdministracioUsuarios extends DataAccesObject {
    
    public DAOAdministracioUsuarios(){
    	super();
    	emfactory= Persistence.createEntityManagerFactory( "Viajes Compartidos" ); 
    	entitymanager = emfactory.createEntityManager( );
    }
    
    //-------------------------------------------------------------para la parte de administracion de usuarios------------------------------   
    
    //Metodo que devuelvo un JSONArray con:
    //permiso1, permiso3, permiso4... cada permiso en JSON
    @SuppressWarnings("unchecked")
	public JSONArray NombrePermisosDeUnUsuario(String nombre_usuario){
    	JSONArray jarray= new JSONArray();
    	Usuario lu= this.buscarUsuarioPorNombre(nombre_usuario);
		List<UsuarioRol>Userroles=lu.getRoles();
		for(UsuarioRol ur: Userroles){
			List<PermisoRol>Permroles = ur.getRol().getPermisos();
			for(PermisoRol pr: Permroles){
				jarray.add(pr.getPermiso().toJSON());
			}
		}
		return jarray;
    }
    
    //metodod que comprueba que el dado nombre de usuario y su password existan y sean el correcto.
    public boolean isUsuarioPass(String user, String pass) {
    	try{
			Usuario u=this.buscarUsuarioPorNombre(user);
			String p= u.getPassword();
			if(p.equals(pass)){
				return true;
			}else{
				return false;
			}
    	}catch(Exception e){
    		return false;
    	}
	}
    
    
    //devuele un JSONArray con:
    // 	rol1, rol2, rol3..
    @SuppressWarnings("unchecked")
	public JSONArray NombreRolUsuario(String nombre_usuario) {
    	JSONArray jarray= new JSONArray();
    	Usuario lu= this.buscarUsuarioPorNombre(nombre_usuario);
		List<UsuarioRol>roles=lu.getRoles();
		for(UsuarioRol ur: roles){
			//ur.getRol().toJSON();
			jarray.add(ur.getRol().toJSON());
		}
		
		return jarray;
	}
    

	public Usuario buscarUsuarioPorNombre(String nombre_usuario) {
    	try{
    		Query qry = entitymanager.createNamedQuery("Usuario.porNombreExacto");
    		qry.setParameter("nombre", nombre_usuario);
    		return (Usuario)qry.getSingleResult();
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
	}

	public boolean desasignarPermisoARol(int id_permiso, int id_rol) {
    	try{
			 this.deletePorPrimaryKey(new PermisoRol(), new PermisoRolID(id_permiso, id_rol));
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
		}
		return true;
	}
    
	public boolean desasignarRolAUsuario(int id_rol, int id_usuario) {
		try{
			this.deletePorPrimaryKey(new UsuarioRol(), new UsuarioRolID(id_usuario, id_rol));
		}catch(Exception e){
    		e.printStackTrace();
    		return false;
		}
		return true;
	}
    
    public boolean asignarPermisoARol(Integer id_rol,Integer id_permiso){
	    try{
		    //busco el rol y el permiso
		   Rol r =(Rol) this.buscarPorPrimaryKey(new Rol(), id_rol);
		   Permiso p= (Permiso) this.buscarPorPrimaryKey(new Permiso(), id_permiso);
	    	//chequeo q sus estados sean = 'a', osea q se les pueda asignar permisos
		    if(r.getEstado()!='A'){
		    	return false;
		    }
		    if(p.getEstado()!='A'){
		    	return false;
		    }
		    //al rol le asigo el permiso
		    //r.AsignarPermiso(p);
		    entitymanager.getTransaction( ).begin( );
		    r.AsignarPermiso(p);
		    //guardo la nueva info
		    entitymanager.getTransaction( ).commit( );	
	    }catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
	    return true;
	}
    
    public boolean asignarRolAUsuario(Integer id_rol,Integer id_usuario){
    	try{
	    	//busca el usuario y el rol
		    Rol r =(Rol) this.buscarPorPrimaryKey(new Rol(), id_rol);
		   // Usuario u= (Usuario) this.buscarPorPrimaryKey(new Usuario(), id_usuario);
		    Usuario u1=new Usuario();
		    u1.setId_usuario(id_usuario);
		    Usuario u= (Usuario) searchById(u1);
		  //chequeo q sus estados sean = 'a', osea q se les pueda asignar rol
		    if(r.getEstado()!='A'){
		    	return false;
		    }
		    if(u.getEstado()!='A'){
		    	return false;
		    }
		    entitymanager.getTransaction( ).begin( );
		    //el usuario le asigna el rol
	    	u.asignarRol(r);
	    	//guardo la nueva info
	       	entitymanager.getTransaction( ).commit( );
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    //el metodo devuelve true aunque el estado ya estubiera dado de baja
    @SuppressWarnings("unchecked")
	public boolean darDeBaja(Integer id,String clase) {
		entitymanager.getTransaction( ).begin( );
		try{
			//busco la clase a dar de baja usando la namedquery que tendria q tener
		    Query q2 = entitymanager.createNamedQuery(clase+".SearchById");
		    //la busco por su ID
		    q2.setParameter("id",id);
		    //obtengo el resultado y lo asigno a una clase
		    JSONable original=(JSONable)q2.getSingleResult();
		    //de esa clase saco el JSON original y lo guardo en otra clase
		    JSONObject j=original.toJSON();
		    //al json original le cambio la variable estado
		    j.remove("estado");
		    j.put("estado", 'B');
		    //y ahora al original le pongo el json cambiado, lo unico distinto que tiene es q ahora tiene un estado dado de baja
		    original.SetJSONObject(j);
		    //hago el commit del cambio
		    entitymanager.getTransaction( ).commit( );
		}catch(Exception e){
			entitymanager.getTransaction( ).rollback();
			e.printStackTrace();
			return false;
		}
		return true;
	}
    
    //la persona ya debe estar persistida, sino explota
    public boolean persistirUsuarioConPersona(Usuario u, Integer id_persona){
    	try{
    		entitymanager.getTransaction( ).begin( );
    		Persona p=(Persona) this.buscarPorPrimaryKey(new Persona(), id_persona);	//obtengo la persona	
    		u.setPersona(p);															//se la asigno al usuario
			entitymanager.persist(u);													//guardo el usuario
			entitymanager.getTransaction( ).commit( );	
    		
    	}catch(Exception e){
    		return false;
    	}
    	return true;
    }
    
    //metodo que le pasas el nombre de la clase y te devuelve un arreglo de JSONObjects con todas las tuplas de la BD
    @SuppressWarnings("unchecked")
	public JSONObject[] selectAllJSON(String clase){
    	JSONObject[] json=null;
    	try{
	    	List<JSONable> lista= selectAll(clase);
	    	Integer size= lista.size();
			json=new JSONObject[size];
			Integer index=0;
			for(JSONable j: lista){
				//System.out.println(j.toString());
				json[index]= j.toJSON();
				index++;
			}
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
		return json;		
    }
    
	//-------------------------------------------fin de la parte de administracion de usuarios----------------------------------------------
	//-------------------------------------------Registro Clientes-------------------------------------------------------------------------
	
    public Boolean nuevoCliente(JSONObject persona,JSONObject cliente) {
    	//creo los objectos a partir de los JSON recibidos
    	Persona p= new Persona(persona);
    	Cliente c= new Cliente(cliente);
    	try{
			 entitymanager.getTransaction( ).begin( );
			 c.setPersona(p);
			 System.out.println("id persona antes de insert: "+p.getId_persona());
			 entitymanager.persist(p);
			 System.out.println("id persona despues de insert: "+p.getId_persona());
			 System.out.println("id cliente antes de insert: "+c.getId_usuario());
			 entitymanager.persist(c);
			 System.out.println("id cliente despues de insert: "+c.getId_usuario());
			 Query qry = entitymanager.createNamedQuery("Rol.porNombre");
	    	 qry.setParameter("nombre", "cliente");
			 Rol r= (Rol) qry.getSingleResult();
			 c.asignarRol(r);
			 entitymanager.getTransaction( ).commit( );	
			 
			 System.out.println("id persona despues de commit: "+p.getId_persona());
			 System.out.println("id cliente despues de commit: "+c.getId_usuario());
			 Rol rol_recien_asignado= c.getRoles().get(0).getRol();
			 System.out.println("nombre del rol recien asignado: "+rol_recien_asignado.getNombre_rol());
			 
			 return true;
		}catch(Exception e){

			e.printStackTrace();
			return false;
		}    	
	}
    
	public boolean subirFotoRegistro(JSONObject foto) {
		Cliente c = this.clientePorNombre(foto.get("usuario").toString());
		if (c!=null){
			try{
				 entitymanager.getTransaction( ).begin( );
				 c.setFoto_registro(foto.get("imagen").toString());
				 entitymanager.persist(c);
				 entitymanager.getTransaction( ).commit( );	
				 return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}    	
		}
		return false;
	}	
    
	public boolean subirFotoCliente(JSONObject foto) {
    	Cliente c = this.clientePorNombre(foto.get("usuario").toString());
		if (c!=null){
			try{
			 entitymanager.getTransaction( ).begin( );
			 c.setFoto(foto.get("imagen").toString());
			 entitymanager.persist(c);
			 entitymanager.getTransaction( ).commit( );	
			 return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}    
		}
		return false;
	}	
	
	public Cliente clientePorNombre(String nombre){
    	try{
    		Query qry = entitymanager.createNamedQuery("Cliente.buscarPorClaveCandidata");
    		qry.setParameter("clave_candidata", nombre);
    		return (Cliente) qry.getSingleResult();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
    public boolean mailExiste(String mail){
    	try{
    		Query qry = entitymanager.createNamedQuery("Usuario.porEmail");
    		qry.setParameter("email", mail);
    		return !qry.getResultList().isEmpty();
    		}catch(Exception e){
    			e.printStackTrace();
    			return false;
    		}
    }
    
    public boolean documentoExiste(Integer tipo, BigInteger numero){
    	try{
    		Query qry = entitymanager.createNamedQuery("Persona.porNroYTipoDeDocumento");
    		qry.setParameter("nro_doc", numero);
    		qry.setParameter("tipo_doc", tipo);
    		return !qry.getResultList().isEmpty();
    		}catch(Exception e){
    			e.printStackTrace();
    			return false;
    		}
    }
    
	//-------------------------------------------fin de la parte de registro de clientes----------------------------------------------
	//-------------------------------------------parte generalizada-------------------------------------------------------------------------
							//se paso esta parte a la clase DataAccesObject

}

package otros;

import org.json.simple.JSONObject;

import gestionViajes.controlador.*;
import gestionPuntos.controlador.*;
import gestionUsuarios.controlador.*;
import gestionComisiones.controlador.*;
import gestionViajes.modelo.*;
import gestionComisiones.modelo.*;
import gestionConfiguracion.ConfiguracionDB;
import gestionConfiguracion.EjecutadorQuery;
import gestionUsuarios.modelo.*;


public class Probador {
	
	protected DAOAdministracionUsuarios dao= null;//DAOAdministracionUsuarios.getInstance();
	
	public static void main(String[] args) {
		/*
		Probador p = new Probador();
		p.menu();
		//para que cree los .class
		ControladorLogin cl= new ControladorLogin();
		AccessManager am= new AccessManager();
		Registro r11 = new Registro();
		AdministracionUsuarios au= new AdministracionUsuarios();
		
		Persona per= new Persona();
		Usuario u= new Usuario();
		Rol r= new Rol();
		UsuarioRol ur= new UsuarioRol();
		Permiso permiso= new Permiso();
		PermisoRol pr= new PermisoRol();
		Cliente c= new Cliente();
		*/
		/*
		EjecutadorQuery exe = new EjecutadorQuery();
		ConfiguracionDB conf = new ConfiguracionDB();
		//creo la configuracion
		//jdbc:mysql://localhost:3306/seminario
		conf.host="localhost";
		conf.dbname="seminario";
		conf.port=3306;
		conf.password="root";
		conf.username="root";
		exe.setConfiguracion(conf);
		if(!exe.conectarse()){
			System.out.println("no me pude conectar!");
		}
		if(!exe.ejecutarArchivo("DDL.sql")){
			System.out.println("no pude ejecutar el archivo");
		}
		if(!exe.ejecutarArchivo("DML.sql")){
			System.out.println("no pude ejecutar el archivo");
		}
		if(!exe.ejecutarArchivo("DML_geonames.sql")){
			System.out.println("no pude ejecutar el archivo");
		}
		
		if(!exe.desconectarse()){
			System.out.println("no me pude desconectar");
		}
	*/
		
		DAOViajes daov = new DAOViajes();
		//System.out.println( daov.actualizarEstadoViaje(1) );	//viaje iniciado
		System.out.println( daov.actualizarEstadoViaje(4) );	//viaje no iniciado (en mi caso)
		//ServletViaje sv = new ServletViaje();
		System.out.println("inicie el servlet");
	}	
	
	public void menu(){

		//para usar inicializar es conveninte haberle hecho truncate a toda la BD
		//inicializar sirve para crear un par de personas, usuarios,clientes, roles y permisos y asignarlos
		//inicializar();			//funca
	
		//probarDAOUsuarioConPersona();		//funca 
		//probarDAOselectAll();		//funca, hay q pasarle el .class.getSimpleName() y te tira devuelve la lista con los resultados
		//probarDAOselectAllJSON();	//funca, al contrario que el metodo anterior, este devuelve un arreglo de JSONObject
		//DAOpersistir();		//funca con todos, por ahi no con usuario a menos que ya tenga una persona asociada
		//DAOActualizar();		//funcaS seguro con clases q implementan JSONable
		//DAOasignarRolAUsuario(); //funca, tiene q ser un ROL y un USUARIO que estan con estado='a'
		//DAOEliminarClaveCompuesta();	//funciona para PermisoRol y UsuarioRol
		//DAOAsignarPermisoARol();	//funca, ROl y permiso tiene q tener estado 'a'
		//DAOdesasignarPermisoARol();		//funca
		//DAOdesasignarRolAUsuario();		//funca
		//DAObuscarUsuarioPorNombre();		//funca
		//DAONombreRolUsuario();		//funca
		//DAONombrePermisosDeUnUsuario();		//funca
		//DAOisUsuarioPass(); 		//funca
		//DAOCliente();			//se puede cargar una entidad con un json vacio o incompleto, los datos quedan en null
		//ConstruirClienteConJSONVacio();		//funca
		//DAOnuevoCliente();		//funca
		//DAOBuscarPorClaveCandidata();		//funca
	}
	public void DAOBuscarPorClaveCandidata(){
		//el test esta hecho para q funque con los datos creado por el inicializador
		
		//no busco por persona por que tiene una CC compuesta, despues hare ese metodo
		Object o=dao.buscarPorClaveCandidata("Usuario", "lucasmufato");
		System.out.println(o.toString());
		o=dao.buscarPorClaveCandidata("Cliente", "segundo_user");
		System.out.println(o.toString());
		o= dao.buscarPorClaveCandidata("Rol", "super_usuario");
		System.out.println(o.toString());
		o= dao.buscarPorClaveCandidata("Permiso", "administrar_usuarios");
		System.out.println(o.toString());
	}
	public void DAOnuevoCliente(){
		JSONObject persona, cliente;
		Persona p= new Persona();
		Cliente c= new Cliente();
		//los paso a JSON para emular que al DAO le llegan desde un servlet
		persona= p.toJSON();
		persona.remove("fecha_nacimiento");
		persona.put("fecha_nacimiento", "19920411");	//hago una fecha del tipo String, que es como le debe llegar al contructor de Persona
		cliente= c.toJSON();
		boolean b;
		try {
			b = dao.nuevoCliente(persona,cliente);
		} catch (ExceptionViajesCompartidos e) {
			e.printStackTrace();
		}
	}
	
	private void ConstruirClienteConJSONVacio(){
		Cliente c= new Cliente();
		JSONObject json= new JSONObject();
		c.SetJSONObject(json);
	}
	
	public void DAOCliente() throws ExceptionViajesCompartidos{
		System.out.println("creando cliente");
		Cliente c= new Cliente();
		c.setNombre_usuario("CLiente");
		c.setEmail("cliente@prueba.com");
		c.setPassword("jajaja");
		c.setPuntos(5);
		c.setReputacion(5);
		try {
			dao.persistirUsuarioConPersona(c,1);
		} catch (ExceptionViajesCompartidos e) {
			e.printStackTrace();
		} //se puede usar este metodo por que el cliente es un usuario (cliente hereda de usuario)
	}
	
	@SuppressWarnings("unused")
	private void inicializar() throws ExceptionViajesCompartidos{
		//este metodo sirve para llenar la BD de instancias!
		//ejecutar una sola ves por q va a explotar diciendo q ya existen las clases
		
		
		//hacer 2 personas y 2 usuarios
		Persona p1= new Persona();
		System.out.println(	dao.persistir(p1)	);
		Usuario u1 = new Usuario();
		System.out.println(	dao.persistirUsuarioConPersona(u1, p1.getId_persona())	);
		Persona p2= new Persona();
		p2.setTipo_doc(2);						//cambio el tipo de documento, por q el tipo y nro de doc son clave unica.
		System.out.println(	dao.persistir(p2)	);
		Cliente u2 = new Cliente();
		u2.setNombre_usuario("segundo_user");	//cambio el username por q es clave unica.
		u2.setEmail("segundo@mail.com");
		System.out.println(	dao.persistirUsuarioConPersona(u2, p2.getId_persona())	);
		
		//hacer varios roles y permisos
		Rol r1= new Rol();
		r1.setNombre_rol("rol1");
		System.out.println(	dao.persistir(r1)	);
		Rol r2 = new Rol();
		r2.setNombre_rol("rol2");
		System.out.println(	dao.persistir(r2)	);
		Rol r3 = new Rol();
		r3.setNombre_rol("rol3");
		System.out.println(	dao.persistir(r3)	);
		
		Permiso pem1= new Permiso();
		pem1.setNombre_permiso("permiso1");
		System.out.println(	dao.persistir(pem1)	);
		Permiso pem2= new Permiso();
		pem2.setNombre_permiso("permiso2");
		System.out.println(	dao.persistir(pem2)	);
		Permiso pem3= new Permiso();
		pem3.setNombre_permiso("permiso3");
		System.out.println(	dao.persistir(pem3)	);
		
		//dar roles a los usuarios y permisos a los roles
							//(rol, usuario)
		
		System.out.println(	dao.asignarRolAUsuario(1, 2)	);
		System.out.println(	dao.asignarRolAUsuario(2, 2)	);
		System.out.println(	dao.asignarRolAUsuario(3, 2)	);
	
		
							//(rol, permiso)
		
		System.out.println(	dao.asignarPermisoARol(1, 3)	);
		System.out.println(	dao.asignarPermisoARol(2, 2)	);
		System.out.println(	dao.asignarPermisoARol(3, 3)	);
		
		
		//ya tendria que quedar la BD con suficientes datos como para hacer prubas y visualizar
		
	}
	
	@SuppressWarnings("unused")
	private void DAONombrePermisosDeUnUsuario() {
		System.out.println(dao.NombrePermisosDeUnUsuario("lucasmufato").toJSONString());
		
	}

	@SuppressWarnings("unused")
	private void DAOisUsuarioPass() {
		System.out.println(dao.isUsuarioPass("lucasmufato", "masterkey"));
		
	}

	@SuppressWarnings("unused")
	private void DAONombreRolUsuario() {
		System.out.println(
				dao.NombreRolUsuario("lucasmufato").toString()
				);
		
	}

	@SuppressWarnings("unused")
	private void DAObuscarUsuarioPorNombre() {
		Usuario u= dao.buscarUsuarioPorNombre("lucasmufato");
		System.out.println("usuarios recuperados");
		System.out.println(u.toString());
	}

	@SuppressWarnings("unused")
	private void DAOdesasignarRolAUsuario() {
								//(rol,usuario)
		 System.out.println(dao.desasignarRolAUsuario(4, 1));
		
	}

	@SuppressWarnings("unused")
	private void DAOdesasignarPermisoARol() {
								//(permiso,rol)
		 System.out.println(dao.desasignarPermisoARol(2, 4));
		
	}

	public void DAOAsignarPermisoARol() throws ExceptionViajesCompartidos{
												//id_rol, id_permiso
	    try {
			System.out.println(dao.asignarPermisoARol(1,1));
		} catch (ExceptionViajesCompartidos e) {
			e.printStackTrace();
		}
	}
	
	public void DAOasignarRolAUsuario() throws ExceptionViajesCompartidos{

	    try {
			System.out.println(dao.asignarRolAUsuario(4, 1));
		} catch (ExceptionViajesCompartidos e) {
			e.printStackTrace();
		}
	}
	
	public void DAOEliminarClaveCompuesta() throws ExceptionViajesCompartidos{
		//para borrar un registro de la tabla intermedia entre usuario y rol
		dao.deletePorPrimaryKey(new UsuarioRol(), new UsuarioRolID(1,1));
		//para borrar un registro de la tabla intermedia entre permiso y rol
		//dao.deletePorPrimaryKey(new PermisoRol(), new PermisoRolID(1,2));		//funciona!
		
	}
	
	@SuppressWarnings("unused")
	private void probarDAOselectAllJSON(){
		JSONObject[] j=dao.selectAllJSON(Persona.class.getSimpleName());
		for(JSONObject json: j){
			System.out.println(json.toJSONString());
		}
	}
	
	@SuppressWarnings("unused")
	private void probarDAOselectAll() {
		dao.selectAll(Rol.class.getSimpleName());
		
	}

	@SuppressWarnings("unused")
	private void DAOActualizar() throws ExceptionViajesCompartidos{ //si actualizas un registro y queda igual q antes, no lo guarda
		Rol r= new Rol();
		r.setId_rol(2);
		r.setDescripcion("mica");
		r.setEstado('b');
		r.setNombre_amigable("mica");
		r.setNombre_rol("micaela guerrero");
		dao.actualizar(r);
	}
	
	@SuppressWarnings("unused")
	private void DAOpersistir() throws ExceptionViajesCompartidos{
		System.out.println("voy a persistir una entidad");
		Permiso p = new Permiso();
		p.setDescripcion("administrar_usuarios");
		p.setFuncionalidad("administrar_usuarios");
		p.setNombre_permiso("administrar_usuarios");
		dao.persistir(p);
	}	
	
	@SuppressWarnings("unused")
	private void probarDAOUsuarioConPersona() throws ExceptionViajesCompartidos{
		Usuario u= new Usuario();
		u.setEmail("segundouser@yo");
		u.setNombre_usuario("segundouser");
		try {
			System.out.println(dao.persistirUsuarioConPersona(u,9));
		} catch (ExceptionViajesCompartidos e) {
			e.printStackTrace();
		}
	}
}

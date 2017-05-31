package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import pruebas.Prueba;

public class Usuario extends BaseDatos {
	
	protected int id;
	protected int id_persona;
	protected String nombre;
	protected String password;
	protected String email;
	protected String descripcion;
	protected char estado;	
	protected JSONArray roles;
	
	//atributos de la clase para su funcionamiento y facilidad de codigo
	protected static String vector_atributos[]={"id_usuario","id_persona","nombre_usuario","password","email","descripcion","estado"};
	protected static int cantidad_atributos=7;
	protected static String tabla ="USUARIO";
	protected static String campo_pk = "id_usuario";
	
	public static JSONObject[] Select(){
		String consulta="SELECT * FROM "+tabla;
		JSONObject[] json=null;
		ResultSet r= BaseDatos.RealizarConsulta(consulta);
		int i=0;
		int rows=0;
		try {
			
			if (r.last()) { //cuento la cantidad de filas del resultado
			    rows = r.getRow();
			    // Move to beginning
			    r.beforeFirst();   
			}
			//System.out.println("columnas: "+rows);
			json= new JSONObject[rows];
			
			int j=0;
			while (r.next()) {
				json[j]= new JSONObject();
				while(i<cantidad_atributos){
					//System.out.println("por guardar en el json el dato leido");
					//System.out.println("las variables son J:"+j+"  i:"+i+" atributo[i]:"+vector_atributos[i]);
					
					//en el json guardo el nombre del atributo y el valor leido de la BD
					json[j].put(vector_atributos[i], r.getObject(vector_atributos[i]));
					i++;
				}
				json[j].put ("roles", Usuario.ListaIdRoles((Integer)r.getObject("id_usuario")));
				//cuando lei todos las columnas de ese renglon paso al siguiente, pasando tambien al siguiente json
				j++;
				i=0;
			}
			
		} catch (SQLException e) {
			System.out.println("error al realizar el select");
			e.printStackTrace();
		}
		
		return json;
	}
	
	public Usuario(){
		this.id=2;
		this.id_persona= 1;
		this.nombre= "user_update";
		this.password= "up";
		this.email= "up@gmail.com";
		this.descripcion= "update";
		this.estado= 'A';
	}
	
	public Usuario (JSONObject json){
		this.id=(int) json.get("id_usuario");
		this.id_persona= (int) json.get("id_persona");
		this.nombre= (String) json.get("nombre_usuario");
		this.password= (String) json.get("password");
		this.email= (String) json.get("email");
		this.descripcion= (String) json.get("descripcion");
		this.estado= json.get("estado").toString().charAt(0);
		this.roles= (JSONArray)json.get("roles");
	}
	
	
	@Override
	public boolean guardar(){
		boolean bandera=false;	
		
		//creo un pedaso del codigo automaticamente con la informacion en el vector_atributos
		String values="(";
		for(int i=1;i<cantidad_atributos-1;i++){
			values=values+vector_atributos[i]+",";
		}
		values=values+vector_atributos[cantidad_atributos-1]+")";
		
//protected static String vector_atributos[]={"id_usuario","id_persona","nombre_usuario","password","email","descripcion","estado"};
		
		//creo el query para ser enviado dependiendo si id de esta instacia es -1 para insert, u otro nro para update
		String query="";
		if(this.id==-1){
			query="INSERT INTO "+tabla+values+"VALUES ('"+this.id_persona+"','"+this.nombre+"','"+this.password+"','"+this.email
					+"','"+this.descripcion+"','"+this.estado+"');";
			
		}else{
			query="UPDATE "+tabla+" SET "
					+"id_persona = '"+this.id_persona+"',"
							+ "nombre_usuario= '"+this.nombre+"' ,password = '"+this.password+"',"
							+"email = '"+this.email+"',"+"descripcion = '"+this.descripcion+"',"
							+"estado = '"+this.estado+"'"+" WHERE id_usuario="+this.id+";";
		}
		this.Conectarse_BD();
		bandera=this.EnviarQuery(query);
		this.setId(); // Juan: parche para setear el id del usuario que acabo de insertar
		this.Desconectarse_BD();
		return bandera;
	}
	
	public static boolean  Eliminar(int clave_primaria_tabla){
		boolean bandera=false;
		bandera = BaseDatos.idExistente(tabla, campo_pk, clave_primaria_tabla);
			if(bandera){
				bandera=BaseDatos.QueryEliminar(tabla,campo_pk,clave_primaria_tabla);
			}
		
		return bandera;
	}
	
	public static boolean   Dar_alta(int clave_primaria_tabla){
		boolean bandera=false;
		bandera = BaseDatos.idExistente(tabla, campo_pk, clave_primaria_tabla);
			if(bandera){
				bandera=BaseDatos.QueryDarAlta(tabla,campo_pk,clave_primaria_tabla);
			}
		
		return bandera;
	}

	public boolean AsignarRol(int rol_id){
		boolean bandera=false;
		
		//creo un JSON para enviarle los datos a la clase Rol_Usuario mediante el constructor con JSON
		JSONObject json= new JSONObject();
		json.put("id_usuario", this.id);
		json.put("id_rol", rol_id);
		Rol_Usuario ru= new Rol_Usuario(json);
		bandera=ru.guardar();
		//uso este ultimo metodo para reusar codigo, q ya los tenia hechos
		
		return bandera;
	}
	
	public boolean QuitarRol(int rol_id){
		boolean bandera=false;
		
		return Rol_Usuario.Eliminar(this.id, rol_id);//uso este ultimo metodo para reusar codigo, q ya tambien lo tenia hecho
	}
	
	public static boolean AsignarRol(int rol_id, int usuario_id){
		boolean bandera=false;
		
		//creo un JSON para enviarle los datos a la clase Rol_Usuario mediante el constructor con JSON
		JSONObject json= new JSONObject();
		json.put("id_usuario", usuario_id);
		json.put("id_rol", rol_id);
		Rol_Usuario ru= new Rol_Usuario(json);
		bandera=ru.guardar();
		//uso este ultimo metodo para reusar codigo, q ya los tenia hechos
		
		return bandera;
	}
	
	public static boolean QuitarRol(int rol_id, int usuario_id){
		boolean bandera=false;
		
		return Rol_Usuario.Eliminar(usuario_id, rol_id);//uso este ultimo metodo para reusar codigo, q ya tambien lo tenia hecho
	}
	
	public static boolean isUsuarioPass(String nombre, String pass){
		String consulta = "SELECT * FROM USUARIO WHERE nombre_usuario= '"+nombre+"' and password='"+pass+"'";
		ResultSet r = BaseDatos.RealizarConsulta(consulta);
		try {
			if (r.next()){
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Error al realizar login en clase Usuario");
			e.printStackTrace();
		}
		return false;
	}
	
	public static JSONArray GetNombrePermisosUsuario(String nombre_usuario) {
		JSONArray listaIdPermisos = new JSONArray();
		// tomo el id del user.
		JSONArray listaRoles = GetRolesPorNombre(nombre_usuario);
		for (int i=0; i<listaRoles.size(); i++){
			// tomo permisos de cada rol
			JSONArray listaPermisosRol = Permiso_Rol.ListaIdPermisos((Integer)listaRoles.get(i));
			for (int j=0; j<listaPermisosRol.size(); j++){
				//Me fijo de no repetir permisos
				if (!listaIdPermisos.contains(listaPermisosRol.get(j))){
					//Agrego permisos
					listaIdPermisos.add(listaPermisosRol.get(j));
				}
			}
		}
		// Tomo todos los datos de cada id permiso
		JSONArray listaPermisos = new JSONArray();
		for (int i=0; i<listaIdPermisos.size(); i++){
			listaPermisos.add(Permiso.GetPermisoPorId((Integer)listaIdPermisos.get(i)));
		}
		return listaPermisos;
	}
	
	public static int GetIdPorNombre(String nombre_usuario) {
		String consulta = "SELECT id_usuario from USUARIO where nombre_usuario='"+nombre_usuario+"'";
		ResultSet r = BaseDatos.RealizarConsulta(consulta);
		try {
			if (r.next()){
				return r.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Error al obtener id_usuario por nombre_usuario");
			e.printStackTrace();
		}
		return -1; //no me gusta un carajo pero no se que poner.
	}

	private static JSONArray ListaIdRoles(int id) {
		JSONArray lista = new JSONArray();
		String consulta = "SELECT id_rol FROM USUARIO_ROL WHERE id_usuario="+id;
		ResultSet r= BaseDatos.RealizarConsulta(consulta);
		try {
			while (r.next()) {
				lista.add(r.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("Error al realizar el select para id_roles relacionados con usuario "+id);
			e.printStackTrace();
		}
		return lista;
	}

	public static JSONArray GetRolesPorNombre(String nombreUsuario) {
		int id_usuario = Usuario.GetIdPorNombre(nombreUsuario);
		return ListaIdRoles(id_usuario);		
	}

	/*
	 * Parche de Juan para setear la id del usuario que se inserta desde metodo guardar
	 */
	private void setId() {
		String query="SELECT id_usuario FROM "+tabla
				+" WHERE id_persona = '"+this.id_persona
				+"' AND nombre_usuario = '"+this.nombre
				+"' AND password = '"+this.password
				+"' AND email = '"+this.email+"';";
		this.Conectarse_BD();
		ResultSet rs =this.RealizarConsulta(query);
		this.Desconectarse_BD();
		try {
			if (rs.next()){
				this.id = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Usuario: Error al obtener id_usuario");
			e.printStackTrace();
		}
	}
	public int getId(){
		return this.id;
	}
}

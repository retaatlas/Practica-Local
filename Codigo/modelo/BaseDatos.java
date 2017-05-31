package modelo;

import org.json.simple.JSONObject;

import java.sql.*;

public abstract class BaseDatos {
	
	//atributos para la conexion a la BD
	protected static String usuario_BD="root";
	protected static String contrasena_BD="root";
	protected static String IP_BD="localhost";
	protected static int puerto_BD=3306;
	protected static String nombre_BD="seminario";
	
	//variables de uso de la BD
	protected Connection conexion = null;
	
	
	
	public static JSONObject[] Select(){
		System.out.println("Select de la superclase BaseDatos");
		return null;
	}
	
	public boolean guardar(){
		System.out.println("guardar de la superclase BaseDatos");
		return true;
	}
	
	public static boolean Eliminar(int clave_primaria_tabla){
		System.out.println("Eliminar de la superclase BaseDatos");
		return true;
	}
	
	protected static boolean QueryEliminar(String tabla, String campo, int clave_primaria_tabla ){
		String query="UPDATE "+tabla+" SET ESTADO='B' WHERE "+campo+"="+clave_primaria_tabla+";";
		boolean bandera=false;
		try {
			String datosBD= "jdbc:mysql://"+IP_BD+"/"+nombre_BD;
			Connection conexion = DriverManager.getConnection(datosBD,usuario_BD, contrasena_BD);
			System.out.println("me pude conectar a la BD: "+nombre_BD);
			System.out.println(query);
			Statement cmd = conexion.createStatement();
			cmd.executeUpdate(query);
			System.out.println("la eliminacion fue un exito");
			bandera=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return bandera;
	}
	
	public static boolean Dar_alta(int clave_primaria_tabla){
		System.out.println("Dar_Arta de la superclase BaseDatos");
		return true;
	}
	
	public static boolean QueryEliminar(String query){
		boolean bandera=false;
		try {
			String datosBD= "jdbc:mysql://"+IP_BD+"/"+nombre_BD;
			Connection conexion = DriverManager.getConnection(datosBD,usuario_BD, contrasena_BD);
			System.out.println("me pude conectar a la BD: "+nombre_BD);
			System.out.println(query);
			Statement cmd = conexion.createStatement();
			cmd.executeUpdate(query);
			System.out.println("se elimino la tupla bien");
			bandera=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bandera;
		
	}
	
	public static boolean QueryDarAlta(String tabla, String campo, int clave_primaria_tabla) {
		String query="UPDATE "+tabla+" SET ESTADO=1 WHERE "+campo+"="+clave_primaria_tabla+";";
		boolean bandera=false;
		try {
			String datosBD= "jdbc:mysql://"+IP_BD+"/"+nombre_BD;
			Connection conexion = DriverManager.getConnection(datosBD,usuario_BD, contrasena_BD);
			System.out.println("me pude conectar a la BD: "+nombre_BD);
			System.out.println(query);
			Statement cmd = conexion.createStatement();
			cmd.executeUpdate(query);
			System.out.println("la dada de alta fue un exito");
			bandera=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bandera;
		
	}
	
	protected boolean Conectarse_BD(){
		
		try {
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		    System.out.println("Obtuve bien la instacia del driver");
		} catch (Exception e) {
			System.out.println("error al obtener driver");
		    System.out.println(e.toString());
		}
		
		try {
			String datosBD= "jdbc:mysql://"+BaseDatos.IP_BD+"/"+BaseDatos.nombre_BD;
			conexion = DriverManager.getConnection(datosBD,BaseDatos.usuario_BD, BaseDatos.contrasena_BD);
			System.out.println("me pude conectar a la BD: "+BaseDatos.nombre_BD);
		} catch (SQLException ex) {
			System.out.println("no se pudo conectar a "+BaseDatos.nombre_BD+" con el usuario "+BaseDatos.usuario_BD+" y contrasena "+BaseDatos.contrasena_BD);
		    System.out.println("SQLException: " + ex.getMessage());
		}
		
		return true;
	}
	
	protected boolean Desconectarse_BD(){
		if(conexion!=null){
			try {
				conexion.close();
				System.out.println("me pude desconectar");
			} catch (SQLException e) {
				System.out.println("Error al cerrar la conexion");
				e.printStackTrace();
			}
		}
		return true;
	}
	
	protected boolean EnviarQuery(String query){
		boolean exito=false;
		try {
			Statement cmd = conexion.createStatement();
			cmd.executeUpdate(query);
			System.out.println("el query salio bien! :D");
			exito=true;
		} catch (SQLException e) {
			System.out.println("Error al enviar query:");
			System.out.println(query);
			e.printStackTrace();
		}
		
		return exito;
	}

	protected static ResultSet RealizarConsulta(String consulta){
		ResultSet rs = null;
		
		try {
			String datosBD= "jdbc:mysql://"+IP_BD+"/"+nombre_BD;
			Connection conexion = DriverManager.getConnection(datosBD,usuario_BD, contrasena_BD);
			System.out.println("me pude conectar a la BD: "+nombre_BD);

			Statement cmd = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = cmd.executeQuery(consulta);
			System.out.println("la consulta salio bien! :D");
			
			//conexion.close();
			//System.out.println("me pude desconectar");
		} catch (SQLException e) {
			System.out.println("Error al enviar consulta:");
			System.out.println(consulta);
			e.printStackTrace();
		}
		
		return rs;
	}
	
	protected static boolean idExistente(String tabla, String campo, int id){
		boolean existe = false;
		ResultSet rs;
		String consulta = "SELECT * FROM " + tabla + " WHERE " + tabla +"."+ campo + " = " + id;
		rs = RealizarConsulta(consulta);
		try {
			if (rs.next()) {
				existe = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return existe;
	}
	
}

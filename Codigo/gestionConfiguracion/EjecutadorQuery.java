package gestionConfiguracion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;


public class EjecutadorQuery {
	protected ConfiguracionDB configuracion;
	protected String path_archivo;
	protected boolean conectado;
	protected Connection conn = null;
	
	public EjecutadorQuery(){
		this.conectado=false;
	}
	
	public ResultSet ejecutarQuery(String query){
		 Statement stmt = null;
		 try {
			 stmt = this.conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query);
			 return rs;
		 } catch (SQLException e) {
			 e.printStackTrace();
			 return null;
		 }
	}

	public boolean ejecutarStatement(String query){
		 Statement stmt = null;
		 try {
			 stmt = this.conn.createStatement();
			 boolean rs = stmt.execute(query);
			 return rs;
		 } catch (SQLException e) {
			 e.printStackTrace();
			 return false;
		 }
	}
	
	public boolean conectarse(){
		return conectarse(false);
	}

	public boolean conectarse(boolean crear_db){
		if(this.configuracion==null){
			return false;
		}
		if(this.conectado){
			return false;
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");		
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return false;
		}
	    try {
	    	DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
	    	//jdbc:mysql://localhost:3306/seminario
			conn = DriverManager.getConnection("jdbc:mysql://"+this.configuracion.host+":"+this.configuracion.port +"/"+(crear_db?"":this.configuracion.dbname) //+"?max_allowed_packet=25000000000" no estaria funcando
			+"?characterEncoding=utf8"
					,this.configuracion.username,this.configuracion.password);
			this.ejecutarQuery("set global max_allowed_packet=99999999");
			ResultSet r = this.ejecutarQuery("show variables like 'max_allowed_packet'");
			System.out.println("resultado de cambiar variable global: ");
			r.next();
			System.out.print (r.getString(1) +"  ");
			System.out.println (r.getInt(2) );
			if(crear_db){
				this.conectado = this.crearDB();
				return this.conectado;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	    this.conectado=true;
		return true;
	}
	
	public boolean desconectarse(){
		if(conectado){
			try {
				conn.close();
				this.conectado=false;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public boolean crearDB() {
		try {
			conn.createStatement().execute("CREATE DATABASE "+configuracion.dbname);
			conn.createStatement().execute("ALTER DATABASE "+configuracion.dbname+" CHARSET=utf8");
			conn.createStatement().execute("USE "+configuracion.dbname);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean ejecutarArchivo(String archivo){
		ScriptRunner runner = new ScriptRunner(this.conn,false , true);
		try {
			runner.runScript(new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isConectado(){
		return conectado;
	}
	
	public ConfiguracionDB getConfiguracion() {
		return configuracion;
	}

	public void setConfiguracion(ConfiguracionDB configuracion) {
		this.configuracion = configuracion;
	}
}

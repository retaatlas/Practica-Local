package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;

import org.json.simple.JSONObject;

public class Rol_Usuario extends BaseDatos {  //EL NOMBRE DE LA TABLA EN LA BASE DE DATOS SE LLAMA usuario_rol
	
	protected int id_usuario;
	protected int id_rol;
	protected String fecha_modificacion;
	
	//atributos de la clase para su funcionamiento y facilidad de codigo
	protected static String vector_atributos[]={"id_usuario","id_rol","fecha_modificacion"};
	protected static int cantidad_atributos=3;
	protected static String tabla ="USUARIO_ROL";
	protected static String campo_pk1 = "id_usuario";
	protected static String campo_pk2 = "id_rol";
	
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
	
	public Rol_Usuario (JSONObject json){
		this.id_usuario=(int)json.get("id_usuario");
		this.id_rol=(int)json.get("id_rol");
		this.fecha_modificacion="current_date()";
	}
	
	public Rol_Usuario(){
		this.id_usuario=1;
		this.id_rol=1;
		this.fecha_modificacion="current_date()";
	}
	@Override
	public boolean guardar(){
		boolean bandera=false;	
		
		//creo un pedaso del codigo automaticamente con la informacion en el vector_atributos
		String values="(";
		for(int i=0;i<cantidad_atributos-1;i++){
			values=values+vector_atributos[i]+",";
		}
		values=values+vector_atributos[cantidad_atributos-1]+")";
		
		String query="";		
		query="INSERT INTO "+tabla+values+"VALUES ('"+this.id_usuario+"','"+this.id_rol+"',"+this.fecha_modificacion+");";
		
		this.Conectarse_BD();
		bandera=this.EnviarQuery(query);
		this.Desconectarse_BD();
		return bandera;
	}
	
	//recibe
	public static boolean  Eliminar(int id_usuario, int id_rol){
		String query= "DELETE FROM "+tabla+" WHERE "+campo_pk1+"="+id_usuario+" AND "+campo_pk2+"="+id_rol+";";
		boolean bandera=BaseDatos.QueryEliminar(query);
		return bandera;
	}
	
	public static boolean   Dar_alta(int clave_primaria_tabla){
		System.out.println("no hago nada yo! soy Rol_Usuario no tengo estado!");
		return true;
	}
}

package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Permiso_Rol extends BaseDatos {
	
	protected int id_permiso;
	protected int id_rol;
	protected String fecha_modificacion;
	
	//atributos de la clase para su funcionamiento y facilidad de codigo
		protected static String vector_atributos[]={"id_permiso","id_rol","fecha_modificacion"};
		protected static int cantidad_atributos=3;
		protected static String tabla ="PERMISO_ROL";
		protected static String campo_pk1 = "id_permiso";
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
	
	public Permiso_Rol (JSONObject json){
		this.id_permiso=(int)json.get("id_permiso");
		this.id_rol=(int)json.get("id_rol");
		this.fecha_modificacion="current_date()";
	}
	
	public Permiso_Rol(){
		this.id_permiso=1;
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
		query="INSERT INTO "+tabla+values+"VALUES ('"+this.id_permiso+"','"+this.id_rol+"',"+this.fecha_modificacion+");";
		
		this.Conectarse_BD();
		bandera=this.EnviarQuery(query);
		this.Desconectarse_BD();
		return bandera;
	}
	
	public static boolean  Eliminar(int id_permiso, int id_rol){
		String query= "DELETE FROM "+tabla+" WHERE "+campo_pk1+"="+id_permiso+" AND "+campo_pk2+"="+id_rol+";";
		boolean bandera=BaseDatos.QueryEliminar(query);
		return bandera;
	}
	
	public static boolean   Dar_alta(int clave_primaria_tabla){
		return true;
	}
	
	public static JSONArray ListaIdPermisos(int idRol) {
		JSONArray lista = new JSONArray();
		String consulta;
		if (idRol == 0){
			consulta = "SELECT * FROM PERMISO";
		}else{
			consulta = "SELECT id_permiso FROM PERMISO_ROL WHERE id_rol="+idRol;
		}
		ResultSet r= BaseDatos.RealizarConsulta(consulta);
		try {
			while (r.next()) {
				lista.add(r.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("Error al realizar el select para permisos relacionados con rol "+idRol);
			e.printStackTrace();
		}
		return lista;
	}
}

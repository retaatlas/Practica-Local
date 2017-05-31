package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Rol extends BaseDatos {
	
	protected int id;
	protected String nombre_rol;
	protected String nombre_amigable;
	protected String descripcion;
	protected char estado;	
	protected JSONArray permisos; //Relacion N a M
	
	//atributos de la clase para su funcionamiento y facilidad de codigo
	protected static String vector_atributos[]={"id_rol","nombre_rol","nombre_amigable","descripcion","estado"};
	protected static int cantidad_atributos=5;
	protected static String tabla ="ROL";
	protected static String campo_pk = "id_rol";
	
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
				if (((Integer)json[j].get("id_rol")) == 0) {
					// Un rol con id 0 es superusuario. Tiene siempre todos los permisos
					JSONArray todosPerms = new JSONArray();
					for (JSONObject perm: Permiso.Select()) {
						todosPerms.add(perm.get("id_permiso"));
					}
					json[j].put("permisos", todosPerms);
				} else {
					json[j].put("permisos", Rol.ListaIdPermisos((Integer)r.getObject("id_rol")));
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
	
	public Rol(JSONObject json){
		this.id=Integer.parseInt(json.get("id_rol").toString());
		this.nombre_rol=(String)json.get("nombre_rol");
		this.nombre_amigable=(String)json.get("nombre_amigable");
		this.descripcion=(String)json.get("descripcion");
		this.estado=json.get("estado").toString().charAt(0);
		this.permisos=(JSONArray)json.get("permisos");
		
	}
	
	public Rol(){
		this.id=-1;
		this.nombre_rol="prueba";
		this.nombre_amigable="prueba";
		this.descripcion="rol para probar";
		this.estado=1;
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
			
			//creo el query para ser enviado dependiendo si id de esta instacia es -1 para insert, u otro nro para update
			String query="";
			if(this.id==-1){
				query="INSERT INTO "+tabla+values+"VALUES ('"+this.nombre_rol+"','"+this.nombre_amigable+"','"+this.descripcion+"','"+
						this.estado+"');";
				
			}else{
				query="UPDATE "+tabla+" SET "
						+"nombre_rol = '"+this.nombre_rol+"',"
						+"nombre_amigable = '"+this.nombre_amigable+"',"
						+"descripcion = '"+this.descripcion+"',"
						+"estado = '"+this.estado+"' WHERE id_rol="+this.id+";";
			}
			this.Conectarse_BD();
			bandera=this.EnviarQuery(query);
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
	public boolean AsignarPermiso(int permiso_id){
		boolean bandera=false;
		
		//creo un JSON para enviarle los datos a la clase Permiso_Rol mediante el constructor con JSON
		JSONObject json= new JSONObject();
		json.put("id_permiso", permiso_id );
		json.put("id_rol", this.id);
		Permiso_Rol pr= new Permiso_Rol(json);
		bandera=pr.guardar();
		//uso este ultimo metodo para reusar codigo, q ya los tenia hechos
		
		return bandera;
	}
	
	public boolean QuitarPermiso(int permiso_id){
		boolean bandera=false;
		
		return Permiso_Rol.Eliminar(permiso_id, this.id);//uso este ultimo metodo para reusar codigo, q ya tambien lo tenia hecho
	}
	
	public static boolean AsignarPermiso(int permiso_id, int rol_id){
		boolean bandera=false;
		
		//creo un JSON para enviarle los datos a la clase Permiso_Rol mediante el constructor con JSON
		JSONObject json= new JSONObject();
		json.put("id_permiso", permiso_id );
		json.put("id_rol", rol_id);
		Permiso_Rol pr= new Permiso_Rol(json);
		bandera=pr.guardar();

		//uso este ultimo metodo para reusar codigo, q ya los tenia hechos
		
		return bandera;
	}
	
	public static boolean QuitarPermiso(int permiso_id, int rol_id){
		boolean bandera=false;
		
		return Permiso_Rol.Eliminar(permiso_id, rol_id);//uso este ultimo metodo para reusar codigo, q ya tambien lo tenia hecho
	}

	private static JSONArray ListaIdPermisos (int id) {
	JSONArray lista = new JSONArray();
	String consulta = "SELECT id_permiso FROM PERMISO_ROL WHERE id_rol="+id;
	ResultSet r= BaseDatos.RealizarConsulta(consulta);
		try {
			while (r.next()) {
				lista.add(r.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("Error al realizar el select para id_permisos relacionados con rol "+id);
			e.printStackTrace();
		}

		return lista;
	}
	
	public static int getRolPorNombre(String rol){
		
		String query = "SELECT "+campo_pk+ " FROM "+ tabla + " WHERE nombre_rol = '"+rol+"'";
		ResultSet r = BaseDatos.RealizarConsulta(query);
		try {
			while(r.next()){
				return r.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Error al obtener id_rol por nombre_rol");
			e.printStackTrace();
		}
		return -1;
	}
}

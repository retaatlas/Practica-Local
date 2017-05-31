package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import pruebas.Prueba;

public class Persona extends BaseDatos {
	
	protected int id;
	protected String nombres;
	protected String apellidos;
	protected int tipo_doc;
	protected long nro_doc;
	protected String fecha_nacimiento;
	protected char sexo;
	protected String domicilio;
	protected String foto;
	protected String telefono;
	protected String descripcion;
	protected char estado;
	protected String foto_registro;

	protected JSONArray usuarios;
	
	//atributos de la clase para su funcionamiento y facilidad de codigo
		protected static String vector_atributos[]={"id_persona","nombres","apellidos","tipo_doc","nro_doc","fecha_nacimiento","sexo","foto","domicilio","telefono","descripcion","estado","foto_registro"};
		protected static int cantidad_atributos=13;
		protected static String tabla ="PERSONA";
		protected static String campo_pk = "id_persona";
	
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
				json[j].put ("usuarios", Persona.ListaIdUsuarios((Integer)r.getObject("id_persona")));
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
	
	public Persona(JSONObject json){ 
		this.id=Integer.parseInt(json.get("id_persona").toString());
		this.nombres=(String)json.get("nombres");
		this.apellidos=(String)json.get("apellidos");
		this.tipo_doc=Integer.parseInt(json.get("tipo_doc").toString());
		this.nro_doc=Integer.parseInt(json.get("nro_doc").toString());
		this.fecha_nacimiento=(String)json.get("fecha_nacimiento");
		this.sexo=json.get("sexo").toString().charAt(0);
		this.foto=(String)json.get("foto");
		this.domicilio=(String)json.get("domicilio");
		this.telefono=(String)json.get("telefono");
		this.descripcion=(String)json.get("descripcion");
		this.estado=json.get("estado").toString().charAt(0);
		this.foto_registro=(String)json.get("foto_registro");
	}
	
	public Persona(){
		this.id=-1;
		this.nombres="";
		this.apellidos="";
		this.tipo_doc=1;
		this.nro_doc=0;
		this.fecha_nacimiento="current_date()";
		this.sexo='O';
		this.foto=null;
		this.domicilio="";
		this.telefono="";
		this.descripcion=null;
		this.estado='A';
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
			query="INSERT INTO "+tabla+values+"VALUES ('"
				+this.nombres+"','"
				+this.apellidos+"','"
				+this.tipo_doc+"','"
				+this.nro_doc+"','"
				+this.fecha_nacimiento+"','"
				+this.sexo+"','"
				+this.foto+"','"
				+this.domicilio+"','"
				+this.telefono+"','"
				+this.descripcion+"','"
				+this.estado+"','"
				+this.foto_registro
				+"');";
			
		}else{
			query="UPDATE "+tabla+" SET "
					+"nombres = '"+this.nombres+"',"
					+"apellidos= '"+this.apellidos+"',"
					+"tipo_doc = '"+this.tipo_doc+"',"
					+"nro_doc = '"+this.nro_doc+"',"
					+"fecha_nacimiento = '"+this.fecha_nacimiento+"',"
					+"sexo = '"+this.sexo+"',"
					+"domicilio = '"+this.domicilio+"',"
					+"foto = '"+this.foto+"',"
					+"telefono = '"+this.telefono+"',"
					+"descripcion = '"+this.descripcion+"',"
					+"estado = '"+this.estado+"', "
					+"foto_registro = '"+this.foto_registro+"' "
					+"WHERE id_persona="+this.id+";";
		}
		this.Conectarse_BD();
		bandera=this.EnviarQuery(query);
		this.setId();// parche para setear el id de la persona que acabo de insertar
		System.out.println("id: "+this.getId()+"bandera "+bandera);
		this.Desconectarse_BD();
		return bandera;
	}
	
	/*
	 * Parche de Juan para setear la id del usuario que se inserta desde metodo guardar
	 */
	private void setId(){
		String query="SELECT id_persona FROM "+tabla
				+" WHERE nombres = '"+this.nombres
				+"' AND apellidos = '"+this.apellidos
				+"' AND tipo_doc = '"+this.tipo_doc
				+"' AND nro_doc = '"+this.nro_doc
				+"' AND fecha_nacimiento = '"+this.fecha_nacimiento
				+"' AND sexo = '"+this.sexo
				+"' AND domicilio = '"+this.domicilio+"';";
		this.Conectarse_BD();
		ResultSet rs =this.RealizarConsulta(query);
		this.Desconectarse_BD();
		try {
			if (rs.next()){
				this.id = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Error al obtener id_persona");
			e.printStackTrace();
		}
	}
	public int getId(){
		return this.id;
	}
	public static boolean  Eliminar(int clave_primaria_tabla){
		boolean bandera=false;
		bandera = BaseDatos.idExistente(tabla, campo_pk, clave_primaria_tabla);
			if(bandera){
				bandera=BaseDatos.QueryEliminar(tabla,campo_pk,clave_primaria_tabla);
			}
		
		return bandera;
	}
	
	public static boolean  Dar_alta(int clave_primaria_tabla){
		boolean bandera=false;
		bandera = BaseDatos.idExistente(tabla, campo_pk, clave_primaria_tabla);
			if(bandera){
				bandera=BaseDatos.QueryDarAlta(tabla,campo_pk,clave_primaria_tabla);
			}
		
		return bandera;
	}

	private static JSONArray ListaIdUsuarios(int id) {
		JSONArray lista = new JSONArray();
		String consulta="SELECT id_usuario FROM USUARIO WHERE id_persona= "+id;
		ResultSet r= BaseDatos.RealizarConsulta(consulta);
		try {
			while (r.next()) {
				lista.add(r.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("Error al realizar el select para id_usuarios relacionados con persona");
			e.printStackTrace();
		}

		return lista;
	}

}

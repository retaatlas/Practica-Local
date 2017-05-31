package pruebas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import modelo.BaseDatos;
import modelo.Usuario;
import org.json.simple.JSONObject;

public class Prueba extends BaseDatos {
	
	//atributos a persistir y que tendrian que ser enviados atraves de JSON
	protected int idprueba;
	protected String palabra;
	protected int estado;
	
	//atributos de la clase para su funcionamiento y facilidad de codigo
	protected static String vector_atributos[]={"idprueba","palabra","estado"};
	protected static int cantidad_atributos=3;
	protected static String tabla ="prueba";
	protected static String campo_pk = "idprueba";
	
	public static void main(String[] args) {
		
		//aca les dejo una muestra de como funciona la clase completa
		//esto mismo tendria q ser mitad copaido a las otras clases y q tengan la misma funcionalidad
		
		//creo una nueva fila en la BD
		System.out.println("VOY A GUARDA ESTA INSTANCIA");
		
		BaseDatos p = new Prueba(-1,"nueva linea",1);
		p.guardar();
		
		System.out.println("");
		System.out.println("INSTANCIA GUARDADA");
		System.out.println("---------------------------------------------");
		System.out.println("");
		//modifico una creada, en realidad yo tengo como 8 filas
		System.out.println("MODIFICO UNA DATO EN LA BD");
		
		p= new Prueba(3,"modifico otra linea",0);
		p.guardar();
		
		//la doy de alta y de baja
		System.out.println("");
		System.out.println("----------------------------------------------");
		System.out.println("DOY DE ALTA");
		
		Prueba.Dar_alta(3);
		
		System.out.println("----------------------------------------------");
		System.out.println("DOY DE BAJA");
		
		Prueba.Eliminar(3);
		
		System.out.println("");
		System.out.println("MUESTRO COMO SE LEE EL SELECT");
		// metodo ver como quedo el JSON despues del metodo select
		 
		JSONObject[] j=Prueba.Select();
		for(int i=0; i<j.length;i++){
			for(int w=0;w<cantidad_atributos;w++){
				System.out.println(vector_atributos[w]+" "+j[i].get(vector_atributos[w])); //MUESTRO EL PAR NOMBRE ATRIBUTO Y VALOR
			}
			System.out.println("el json tiene la forma:");
			System.out.println(j[i].toString());  //MUESTRO COMO QUEDO EL JSON ENTERO
			System.out.println("");
		}
		
	}
	
	public Prueba (JSONObject json){
		this.idprueba=(int) json.get("idprueba");
		this.palabra =(String) json.get("palabra");
		this.estado = (int) json.get("estado");
	}
	
	public static JSONObject[] Select(){		
		String consulta="SELECT * FROM prueba";
		JSONObject[] json=null;
		ResultSet r= Prueba.RealizarConsulta(consulta);
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
	
	public Prueba(int id, String p, int e) {
		this.idprueba=id;
		this.palabra=p;
		this.estado=e;
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
		if(this.idprueba==-1){
			query="INSERT INTO PRUEBA "+values+"VALUES ('"+this.palabra+"',"+this.estado+");";
			
		}else{
			query="UPDATE PRUEBA SET "
					+"palabra = '"+this.palabra+"',"
							+ "estado= "+this.estado+" WHERE idprueba="+this.idprueba+";";
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
	
}

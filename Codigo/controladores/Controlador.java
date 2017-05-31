package controladores;

import org.json.simple.JSONObject;

import modelo.BaseDatos;
import modelo.Permiso;
import modelo.Permiso_Rol;
import modelo.Persona;
import modelo.Rol;
import modelo.Rol_Usuario;
import modelo.Usuario;
import pruebas.Prueba;

public class Controlador {
	
	//private final int NULO=-1;
	private final int PERSONA=1;
	private final int USUARIO=2;
	private final int ROL=3;
	private final int PERMISO=4;
	private final int PERMISO_ROL=5;
	private final int ROL_USUARIO=6;
	private final int PRUEBA= 10;
	
	private BaseDatos basedatos;
	
	public boolean Guardar(JSONObject datos, int tipo_entidad){ // metodo que trata de guardar o modificar datos en la BD, necesita todos
																//los datos de cada clase, por que usa el ID como dice ahi abajo
		
		//si lo que se quiere es crear una entidad nueva, el id se envia con valor -1
		// si se quiere modificar, el id debe ser positivo>0
		boolean bandera=false;
		switch(tipo_entidad){
		case PERSONA:
			basedatos = new Persona(datos);
			bandera=basedatos.guardar();
			break;
		case USUARIO:
			basedatos = new Usuario(datos);
			bandera=basedatos.guardar();
			break;
		case ROL:
			basedatos = new Rol(datos);
			bandera=basedatos.guardar();
			break;
		case PERMISO:
			basedatos = new Permiso(datos);
			bandera=basedatos.guardar();
			break;
		case PERMISO_ROL:
			basedatos = new Permiso_Rol(datos);
			bandera=basedatos.guardar();
			break;
		case ROL_USUARIO:
			basedatos = new Rol_Usuario(datos);
			bandera=basedatos.guardar();
			break;
		case PRUEBA:
			basedatos = new Prueba(datos);
			bandera=basedatos.guardar();
			break;
		}
		basedatos.guardar();
		return bandera;
	}
	
	public boolean Eliminar(int tipo_entidad, int clave_primaria_tabla){
		
		//este metodo solo necesita el nro que identifica a la clase, y la clave primaria de la entidad a dar de baja.
		//si la entidad ya estaba dada de baja devuelve true igual, solo devolveria false cuando explota de alguna forma.
		//dar de baja significa cambiar el atributo estado y ponerlo en 0
		
		boolean bandera=false;
		switch(tipo_entidad){
		case PERSONA:
			bandera=Persona.Eliminar(clave_primaria_tabla);
			break;
		case USUARIO:
			bandera=Usuario.Eliminar(clave_primaria_tabla);
			break;
		case ROL:
			bandera=Rol.Eliminar(clave_primaria_tabla);
			break;
		case PERMISO:
			bandera=Permiso.Eliminar(clave_primaria_tabla);
			break;
		case PERMISO_ROL:
			bandera=Permiso_Rol.Eliminar(clave_primaria_tabla);
			break;
		case ROL_USUARIO:
			bandera=Rol_Usuario.Eliminar(clave_primaria_tabla);
			break;
		case PRUEBA:
			bandera=Prueba.Eliminar(clave_primaria_tabla);
			break;
		}
		
		return bandera;
	}
	
	public boolean Dar_alta(int tipo_entidad, int clave_primaria_tabla){
		
		//este metodo funciona exactamente igual que dar de baja nada mas que en ves de poner el estado en 0, lo pone en 1
		//si el estado ya estaba en 1, devuelve true igual
		
		boolean bandera=false;
		switch(tipo_entidad){
		case PERSONA:
			bandera=Persona.Dar_alta(clave_primaria_tabla);
			break;
		case USUARIO:
			bandera=Usuario.Dar_alta(clave_primaria_tabla);
			break;
		case ROL:
			bandera=Rol.Dar_alta(clave_primaria_tabla);
			break;
		case PERMISO:
			bandera=Permiso.Dar_alta(clave_primaria_tabla);
			break;
		case PERMISO_ROL:
			bandera=Permiso_Rol.Dar_alta(clave_primaria_tabla);
			break;
		case ROL_USUARIO:
			bandera=Rol_Usuario.Dar_alta(clave_primaria_tabla);
			break;
		case PRUEBA:
			bandera=Prueba.Dar_alta(clave_primaria_tabla);
			break;
		}
		return bandera;
	}
	
	public JSONObject[] Select(int tipo_entidad){ 
		//este metodo hace un select * from tabla, recibe un nro que se mapea para ver q entidad es la q se quiere mostrar, y carga
		//todos los datos en un arreglo indefinido de objetos JSON. 
		
		//POR AHI TE RESULTE INTERESANTE SABER QUE EL METODO TOSTRING DE JSON DEVUELVE EL JSON BIEN FORMADO DE LA MANERA
		// {NOMBRE: PEPE, EDAD: 23, ETC: ETC}
		
		JSONObject[] datos_a_devolver=null;
		switch(tipo_entidad){
		case PERSONA:
			datos_a_devolver=Persona.Select();
			break;
		case USUARIO:
			datos_a_devolver=Usuario.Select();
			break;
		case ROL:
			datos_a_devolver=Rol.Select();
			break;
		case PERMISO:
			datos_a_devolver=Permiso.Select();
			break;
		case PERMISO_ROL:
			datos_a_devolver=Permiso_Rol.Select();
			break;
		case ROL_USUARIO:
			datos_a_devolver=Rol_Usuario.Select();
			break;
		case PRUEBA:
			datos_a_devolver=Prueba.Select();
			break;
		}
		return datos_a_devolver;
	}
	
}

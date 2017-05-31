package otros;

import javax.persistence.RollbackException;

public class ManejadorErrores {

	public static String parsearRollback(RollbackException e) {
		String error;
		try{
			error=e.getCause().getCause().getMessage();
		}catch(NullPointerException e1){
			try{
				error=e.getCause().getLocalizedMessage();
			}catch(NullPointerException e2){
				error=e.getMessage();
			}
		}
		
		//en la variable queda un string como: " Column 'patente' cannot be null "
		// o uno como: " Duplicate entry 'abd123' for key 'patente' "
		// a medida que aparescan mas errores de JPA se van a ir agregando
		System.out.println("ManejadorErrores.parsearRollback parsea el error: "+error);
		
		int inicio_campo_del_error;
		int fin_campo_del_error;
		
		String error_parseado="";
		if( error.contains("cannot be null") ){
			//primero busco que campo fue el que tiro el error, busco lo q hay entre '', ya se q este tipo de error
			//tiene un solo campo con comillas simples
			inicio_campo_del_error = error.indexOf("'");
			fin_campo_del_error = error.lastIndexOf("'");
			String campo_del_error = error.substring(++inicio_campo_del_error, fin_campo_del_error);
			System.out.println("el campo que tiro el error es: "+campo_del_error);
			error_parseado= "falta el dato: "+campo_del_error;
			return error_parseado;
		}else{
			if( error.contains("Duplicate entry") ){
				//este tipo de error tiene 2 datos entre comillas simple, asi q tengo q sacar ambos datos
				inicio_campo_del_error = error.indexOf("'");
				inicio_campo_del_error++;
				fin_campo_del_error = error.indexOf("'", inicio_campo_del_error);
				String dato_campo_del_error = error.substring(inicio_campo_del_error, fin_campo_del_error);
				fin_campo_del_error++;
				
				inicio_campo_del_error = error.indexOf("'",fin_campo_del_error);
				inicio_campo_del_error++;
				fin_campo_del_error = error.indexOf("'", inicio_campo_del_error);
				String campo_del_error = error.substring(inicio_campo_del_error, fin_campo_del_error);
				error_parseado= "ya existe el dato: "+campo_del_error+"="+dato_campo_del_error;
				return error_parseado;
			}
		}
		return "error no parseado!: "+e;
	}

}

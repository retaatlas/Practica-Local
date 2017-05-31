package gestionConfiguracion;

import java.util.Scanner;

public class TestEjecutadorQuery {

	public static void main(String[] args) {
		Scanner s= new Scanner(System.in);
		EjecutadorQuery exe = new EjecutadorQuery();
		ConfiguracionDB conf = new ConfiguracionDB();
		//creo la configuracion
		//jdbc:mysql://localhost:3306/seminario
		conf.host="localhost";
		conf.dbname="seminario";
		conf.port=3306;
		conf.password="root";
		conf.username="root";
		exe.setConfiguracion(conf);
		if(!exe.conectarse()){
			System.out.println("no me pude conectar!");
		}
		System.out.println("conectado a la BD");
		System.out.println("ingrese algo para ejecutar DDL.sql");
		s.nextLine();
		if(!exe.ejecutarArchivo("DDL.sql")){
			System.out.println("no pude ejecutar el archivo");
		}
		System.out.println("DDL.sql ejecutado con exito");
		
		System.out.println("ingrese algo para ejecutar DML_geonames.sql");
		s.nextLine();
		if(!exe.ejecutarArchivo("DML_geonames.sql")){
			System.out.println("no pude ejecutar el archivo");
		}
		System.out.println("DML_geonames.sql ejecutado con exito");
		
		System.out.println("ingrese algo para ejecutar DML.sql");
		s.nextLine();
		if(!exe.ejecutarArchivo("DML.sql")){
			System.out.println("no pude ejecutar el archivo");
		}
		System.out.println("DML.sql ejecutado con exito");
		
		System.out.println("desconectando a la BD");
		if(!exe.desconectarse()){
			System.out.println("no me pude desconectar");
		}

	}

}

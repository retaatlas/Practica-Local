package gestionConfiguracion;

import java.util.HashMap;
import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import javax.servlet.ServletContext;

class ConfiguradorThread implements Runnable {
	ConfiguracionDB config;
	boolean createSchema;
	ServletDBConfig servlet;
	EstadoCfg estado;

	public ConfiguradorThread (ConfiguracionDB config, boolean createSchema, ServletDBConfig servlet) {
		this.config = config;
		this.createSchema = createSchema;
		this.servlet = servlet;
		this.estado = new EstadoCfg();
	}

	@Override
	public void run () {
		boolean continuar = true;
		EjecutadorQuery ejecutor = null;
		ServletContext context = servlet.getServletConfig().getServletContext();

		servlet.setEstadoCfg(this.estado);

		this.estado.setEstado(EstadoCfg.TRABAJANDO);

		//Paso 1: Crear archivo Persistence.xml
		if(crearPersistenceXML(config, servlet)) {
			this.estado.stepUp();
			servlet.setEstadoCfg(this.estado);
		} else {
			this.estado.setEstado(EstadoCfg.FALLO);
			servlet.setEstadoCfg(this.estado);
			continuar = false;
		}

		//Paso 2: Conectarse a base de datos
		if(continuar) {
			ejecutor = new EjecutadorQuery();
			ejecutor.setConfiguracion(this.config);
			if (ejecutor.conectarse(createSchema)) {
				this.estado.stepUp();
				servlet.setEstadoCfg(this.estado);
			} else {
				this.estado.setEstado(EstadoCfg.FALLO);
				servlet.setEstadoCfg(this.estado);
				continuar = false;
			}
		}

		//Paso 3: Crear tablas en base de datos
		if(continuar) {
			String ddl = context.getRealPath ("/sql/DDL.sql");
			if(ejecutor.ejecutarArchivo(ddl)){
				this.estado.stepUp();
				servlet.setEstadoCfg(this.estado);
			} else {
				this.estado.setEstado(EstadoCfg.FALLO);
				servlet.setEstadoCfg(this.estado);
				continuar = false;
			}
		}

		//Paso 4: Cargar informacion geografica
		if(continuar) {
			String dml_geo = context.getRealPath ("/sql/DML_geonames.sql");
			if(ejecutor.ejecutarArchivo(dml_geo)) {
				this.estado.stepUp();
				servlet.setEstadoCfg(this.estado);
			} else {
				this.estado.setEstado(EstadoCfg.FALLO);
				servlet.setEstadoCfg(this.estado);
				continuar = false;
			}
		}

		//Paso 5: Cargar datos iniciales
		if(continuar) {
			String dml = context.getRealPath ("/sql/DML.sql");
			if(ejecutor.ejecutarArchivo(dml)) {
				this.estado.stepUp();
				servlet.setEstadoCfg(this.estado);
			} else {
				this.estado.setEstado(EstadoCfg.FALLO);
				servlet.setEstadoCfg(this.estado);
				continuar = false;
			}
		}

		// Completo
		if(continuar) {
			this.estado.setEstado(EstadoCfg.COMPLETO);
			servlet.setEstadoCfg(this.estado);
		}

		// Cerramos la conexion a la base de datos si es necesario
		if (ejecutor != null && ejecutor.isConectado()) {
			ejecutor.desconectarse();
		}
		// Si fallo en algun paso, vaciamos el archivo de configuracion
		if(this.estado.getEstado() == EstadoCfg.FALLO) {
			this.vaciarPersistenceXML();
		}
	}

	private boolean crearPersistenceXML (ConfiguracionDB config, ServletDBConfig servlet) {
		try {
			ServletContext context = servlet.getServletConfig().getServletContext();
			String inputPath = context.getRealPath ("/configurar/template_persistence.xml");
			String outputPath = context.getRealPath ("/WEB-INF/classes/META-INF/persistence.xml");

			Scanner input = new Scanner (new File (inputPath));
			PrintWriter output = new PrintWriter (new File (outputPath));

			String linea;
			while (input.hasNextLine()) {
				linea = input.nextLine();
				if (linea.indexOf("{{") != -1){
					output.println(this.reemplazar(linea, config));
				} else {
					output.println(linea);
				}
			}
			output.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private boolean vaciarPersistenceXML() {
		try {
			ServletContext context = servlet.getServletConfig().getServletContext();
			String path = context.getRealPath ("/WEB-INF/classes/META-INF/persistence.xml");
			File f = new File(path);
			f.delete();
			f.createNewFile();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	private String reemplazar (String texto, ConfiguracionDB config) {
		return texto.replace("{{USERNAME}}", config.username)
			.replace("{{PASSWORD}}", config.password)
			.replace("{{HOST}}", config.host)
			.replace("{{PORT}}", (new Integer (config.port)).toString())
			.replace("{{DBNAME}}", config.dbname);
	}

}

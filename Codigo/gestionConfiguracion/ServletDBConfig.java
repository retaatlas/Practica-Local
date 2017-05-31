package gestionConfiguracion;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Random;
import org.json.simple.JSONObject;


public class ServletDBConfig extends HttpServlet {

	// Asegurate de que este objeto tenga exclusion mutua
	private EstadoCfg estado;
	private Thread demonio = null;
	private String llave = null;
	private ConfiguracionDB config = null;

	@Override
	public void init () {
		this.estado = new EstadoCfg();
	}

	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Este se usa para pedir el estado de configuracion
		PrintWriter writer = response.getWriter();
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "application/json; charset=UTF-8");

		synchronized (this.estado) {
			writer.println(this.estado.toJSON());
		}
	}

	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Este se usa para enviar los parametros de configuracion
		PrintWriter writer = response.getWriter();
		JSONObject salida;
		String action;

		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "application/json; charset=UTF-8");
		
		action = request.getParameter("action");
			if(action.equals("configurar")) {
				salida = configurar(request, response);
			} else if(action.equals("set_password")) {
				salida = setPassword(request, response);
			} else if (action.equals("esta_configurado")) {
				salida = new JSONObject();
				salida.put("result", true);
				salida.put("configurado", yaConfigurado());
			} else {
				salida = new JSONObject();
				salida.put("result", false);
				salida.put("msg", "no implementado");
			}
			
		writer.println(salida);
		}

	public JSONObject configurar(HttpServletRequest request, HttpServletResponse response) {
		ConfiguracionDB cfg = new ConfiguracionDB();
		JSONObject salida = new JSONObject();
		String mode;

		if (!this.yaConfigurado()) {

			mode = request.getParameter("mode");
			cfg.host = request.getParameter("host");
			cfg.dbname = request.getParameter("dbname");
			cfg.username = request.getParameter("username");
			cfg.password = request.getParameter("password");
			cfg.port = Integer.parseInt(request.getParameter("port"));
			this.config = cfg;

			salida.put("result", true);

			// Iniciar thread que va a hacer todo
			demonio = new Thread (new ConfiguradorThread (cfg, mode.equals("crear"), this));
			demonio.start();

			// Esta llave puede usarse para setear la password de administrador
			this.llave = generarLLave();

			salida.put("result", true);
			salida.put("llave", this.llave);
		} else {
			salida.put("result", false);
			salida.put("msg", "El servidor ya esta configurado");
			
		}

		return salida;
	}

	public JSONObject setPassword(HttpServletRequest request, HttpServletResponse response) {
		JSONObject salida = new JSONObject();
		EjecutadorQuery ejecutor;
		
		String password = request.getParameter("password");
		String llave_cliente = request.getParameter("llave");
		String pass_escapado="'" + password.replace("'", "''") + "'";
		
		if (llave_cliente != null && llave_cliente.equals(this.llave)) {
			ejecutor = new EjecutadorQuery();
			ejecutor.setConfiguracion(this.config);
			ejecutor.conectarse(false);
			ejecutor.ejecutarStatement("UPDATE usuario set password="+pass_escapado+" WHERE nombre_usuario='administrador'");
			salida.put ("result", true);
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se permite cambiar la password de administrador, llave incorrecta.");
		}

		
		return salida;
	}

	private boolean yaConfigurado() {
		// Devuelve true si ya esta configurado.
		ServletContext context = this.getServletConfig().getServletContext();
		String configFile = context.getRealPath ("/WEB-INF/classes/META-INF/persistence.xml");
		File persxml = new File (configFile);

		try {
			if (persxml.exists() && persxml.length()>1) {
				synchronized (this.estado) {
					estado.setEstado (EstadoCfg.COMPLETO);
					estado.setStep (6); // Ultimo paso
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public void setEstadoCfg (EstadoCfg est1) {
		synchronized (this.estado) {
			this.estado.setEstado(est1.getEstado());
			this.estado.setStep(est1.getStep());
		}
	}

	String generarLLave (){
		int longitud = 24;
		String cadenaAleatoria = "";
		long milis = new java.util.GregorianCalendar().getTimeInMillis();
		Random r = new Random(milis);
		int i = 0;
		while ( i < longitud){
				char c = (char)r.nextInt(255);
				if ( (c >= '0' && c <='9') || (c >='A' && c <='Z') ){
				cadenaAleatoria += c;
				i ++;
			}
		}
		return cadenaAleatoria;
	}

}
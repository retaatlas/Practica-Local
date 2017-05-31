package controladores;

import java.io.*;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import modelo.BaseDatos;
import modelo.Persona;
import modelo.Rol;
import modelo.Usuario;
import controladorjpa.AccessManager;

public class ControladorRegistro extends HttpServlet {


	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("En doGet de ControladorRegistro");
		
		PrintWriter writer = response.getWriter();

		response.setContentType("application/json");

		if (AccessManager.EstaLogueado(request)){
			this.printDeniedRedirect(writer);
		}else{
			this.printAccept(writer);
		}
	}
	private void printAccept(PrintWriter writer) {
		JSONObject resultado;

		resultado = new JSONObject();
		resultado.put("result", true);

		writer.println(resultado);
	}
	private void printDeniedRedirect (PrintWriter writer) {
		JSONObject resultado;

		resultado = new JSONObject();
		resultado.put("result", false);
		resultado.put("redirect", "home.html");

		writer.println(resultado);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("En doPost de ControladorRegistro!");
		JSONObject out = null;

		// Setear driver
		PrintWriter writer = response.getWriter();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			writer.println("No se pudo abrir el driver de la base de datos");
			writer.println(e.getMessage());
		}
		
		String accion = request.getParameter("action");

		if (accion.equals("usuario_existe")){
			//recibo el nombre del usuario y pregunto si existe o no.
			out = validarUsuario(request,response);
		}else if(accion.equals("new")){
			out = registrarCliente(request);
		}
		
		if (out == null) {
			out = new JSONObject();
			out.put ("result", false);
		}
		
		out.put("action", accion);
		System.out.println("Lo que mando al js: "+out);
		writer.println (out);

	}

	private JSONObject validarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = request.getParameter("usuario");
		JSONObject salida = new JSONObject ();		
		Integer id = Usuario.GetIdPorNombre(user);
		if (id != null && id != -1) {
			System.out.println("Usuario existe");
			salida.put ("result", true); 
		}else{
			System.out.println("Usuario no existe");
			salida.put("result", false);
		}
		return salida;
	}

	private JSONObject registrarCliente(HttpServletRequest request) {
		JSONObject out = new JSONObject();
		out = guardarPersona(request);
		if ((boolean) out.get("result")){
			out = guardarUsuario(request, out.get("id"));
		}
		return out;
	}
	
	private JSONObject guardarPersona (HttpServletRequest request) {
		JSONObject recibida, salida;
		Persona persona;
		recibida = new JSONObject();
		salida = new JSONObject();

		recibida.put ("id_persona", -1);
		recibida.put("nombres", request.getParameter("persona[nombres]"));
		recibida.put("apellidos", request.getParameter("persona[apellidos]"));
		recibida.put("tipo_doc", Integer.parseInt(request.getParameter("persona[tipo_doc]")));
		recibida.put("nro_doc", Integer.parseInt(request.getParameter("persona[nro_doc]")));
		recibida.put("fecha_nacimiento", request.getParameter("persona[fecha_nacimiento]"));
		recibida.put("sexo", request.getParameter("persona[sexo]"));
		recibida.put("domicilio", request.getParameter("persona[domicilio]"));
		recibida.put("telefono", request.getParameter("persona[telefono]"));
		recibida.put("estado", "A");

		persona = new Persona (recibida);
		if (persona.guardar()) {
			System.out.println("Guarde persona");

			salida.put ("result", true);
			salida.put ("msg", "Se han guardado los datos de la persona");
			salida.put("id", persona.getId());
			System.out.println("guarde id");

		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido guardar los datos de la persona");
		}
		return salida;
	}

	private JSONObject guardarUsuario (HttpServletRequest request, Object idPersona) {
		JSONObject recibido, salida;
		Usuario usuario;
		recibido = new JSONObject();
		salida = new JSONObject();
		
		recibido.put("id_usuario", -1);
		recibido.put("id_persona", idPersona);
		recibido.put("nombre_usuario", request.getParameter("usuario[nombre_usuario]"));
		recibido.put("password", request.getParameter("usuario[password]"));
		recibido.put("email", request.getParameter("usuario[email]"));
		recibido.put("descripcion", request.getParameter("usuario[descripcion]"));
		recibido.put("estado", "A");

		usuario = new Usuario (recibido);
		if (usuario.guardar()) {
			if (asignarRolUsuarioCliente(usuario)){
				salida.put ("result", true);
				salida.put ("msg", "El usuario ha sido registrado correctamente");
			}else{
				salida.put ("result", false);
				salida.put ("msg", "Usted ha sido registrado pero han surgido fallas. Comuniquese con el administrador.");
			}
		} else {
			salida.put ("result", false);
			salida.put ("msg", "El usuario no ha sido registrado correctamente");
		}
		return salida;
	}
	
	private boolean asignarRolUsuarioCliente(Usuario usuario){
		// asigno rol cliente
		//en mi bd el rol cliente es el 6. Por pruebas se lo pongo a la fuerza.
		String rolCliente = "cliente"; // hardcodeo dedicado al marce
		return usuario.AsignarRol(Rol.getRolPorNombre(rolCliente));
	}
	
	public void destroy()
	{
	}
		
}

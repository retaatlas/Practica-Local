package controladores;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import modelo.Persona;
import modelo.Usuario;
import modelo.Rol;
import modelo.Permiso;

public class ControladorServlet extends HttpServlet {


	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter writer = response.getWriter();
		/* Asegurate de iniciar el driver de base de datos */
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			writer.println("No se pudo abrir el driver de la base de datos");
			writer.println(e.getMessage());
		}

		response.setContentType("application/json");

		if (!AccessManager.HasPermiso(request, "administrar_usuarios")){
			this.printDeniedRedirect(writer);
		}else{
			this.printDbData(writer);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action, entity;
		JSONObject out = null;

		PrintWriter writer = response.getWriter();

		action = request.getParameter("action");
		entity = request.getParameter("entity");

		if (action.equals("new") ||action.equals("edit")){
			if (entity.equals("persona")) {
				out = updatePersona(request);
			} else if (entity.equals("usuario")) {
				out = updateUsuario(request);
			} else if (entity.equals("rol")) {
				out = updateRol(request);
			} else if (entity.equals("permiso")) {
				out = updatePermiso(request);
			}
		} else if (action.equals("delete")) {
			if (entity.equals("persona")) {
				out = deletePersona(request);
			} else if (entity.equals("usuario")) {
				out = deleteUsuario(request);
			} else if (entity.equals("rol")) {
				out = deleteRol(request);
			} else if (entity.equals("permiso")) {
				out = deletePermiso(request);
			}
		} else if (action.equals("assignRol") && entity.equals("usuario")) {
			out = assignRol(request);
		} else if (action.equals("removeRol") && entity.equals("usuario")) {
			out = removeRol(request);
		} else if (action.equals("assignPermiso") && entity.equals("rol")) {
			out = assignPermiso(request);
		} else if (action.equals("revokePermiso") && entity.equals("rol")) {
			out = revokePermiso(request);
		} else if (action.equals("get_permisos")){
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (Exception e) {
				writer.println("No se pudo abrir el driver de la base de datos");
				writer.println(e.getMessage());
			}
			out = getPermisosUsuario(request);
		}
		

		if (out == null) {
			out = new JSONObject();
			out.put ("result", false);
		}

		out.put("action", action);
		out.put("entity", entity);
		System.out.println("Lo que mando al js: "+out);
		writer.println (out);
	}

	public void destroy()
	{
	}

	private void printDbData(PrintWriter writer) {
		JSONObject resultado;

		resultado = new JSONObject();
		resultado.put("personas", this.getPersonas());
		resultado.put("usuarios", this.getUsuarios());
		resultado.put("roles", this.getRoles());
		resultado.put("permisos", this.getPermisos());
		resultado.put("result", true);

		writer.println(resultado);
	}

	private void printDeniedRedirect (PrintWriter writer) {
		JSONObject resultado;

		resultado = new JSONObject();
		resultado.put("result", false);
		resultado.put("redirect", "acceso_denegado.html");

		writer.println(resultado);
	}

	private JSONArray getPersonas() {
		JSONArray personas;
		JSONObject[] in;
		Object id, fechaNac;

		in = Persona.Select();
		personas = new JSONArray ();
		if (in != null) {
			for (int i = 0; i< in.length; i++) {
				id = in[i].get("id_persona");
				fechaNac = in[i].get("fecha_nacimiento");
				in[i].put("id", id);
				in[i].remove("fecha_nacimiento");
				in[i].put("fecha_nacimiento", fechaNac.toString());
				personas.add(in[i]);
			}
		}
		return personas;
	}

	private JSONArray getUsuarios() {
		JSONArray usuarios;
		JSONObject[] in;
		Object id;

		in=Usuario.Select();
		usuarios = new JSONArray ();
		if(in != null) {
			for (int i = 0; i< in.length; i++) {
				id = in[i].get("id_usuario");
				in[i].put("id", id);
				usuarios.add(in[i]);
			}
		}
		return usuarios;
	}

	private JSONArray getRoles() {
		JSONArray roles;
		JSONObject[] in;
		Object id;

		in=Rol.Select();
		roles = new JSONArray ();
		if(in != null) {
			for (int i = 0; i< in.length; i++) {
				id = in[i].get("id_rol");
				in[i].put("id", id);
				roles.add(in[i]);
			}
		}
		return roles;
	}

	private JSONArray getPermisos() {
		JSONArray permisos;
		JSONObject[] in;
		Object id;

		in=Permiso.Select();
		permisos = new JSONArray ();
		if(in != null) {
			for (int i = 0; i< in.length; i++) {
				id = in[i].get("id_permiso");
				in[i].put("id", id);
				permisos.add(in[i]);
			}
		}
		return permisos;
	}

	private JSONObject updatePersona (HttpServletRequest request) {
		JSONObject recibida, salida;
		Persona persona;

		recibida = new JSONObject();
		salida = new JSONObject();

		if (request.getParameter("action").equals("new")) {
			recibida.put ("id_persona", -1);
		} else {
			recibida.put("id_persona", Integer.parseInt(request.getParameter("id_persona")));
		}
		recibida.put("nombres", request.getParameter("nombres"));
		recibida.put("apellidos", request.getParameter("apellidos"));
		recibida.put("tipo_doc", Integer.parseInt(request.getParameter("tipo_doc")));
		recibida.put("nro_doc", Integer.parseInt(request.getParameter("nro_doc")));
		recibida.put("fecha_nacimiento", request.getParameter("fecha_nacimiento"));
		recibida.put("sexo", request.getParameter("sexo"));
		recibida.put("foto", request.getParameter("foto"));
		recibida.put("domicilio", request.getParameter("domicilio"));
		recibida.put("telefono", request.getParameter("telefono"));
		recibida.put("descripcion", request.getParameter("descripcion"));
		recibida.put("estado", request.getParameter("estado"));
		recibida.put("foto", request.getParameter("foto_registro"));

		persona = new Persona (recibida);
		if (persona.guardar()) {
			salida.put ("result", true);
			salida.put ("msg", "Se han guardado los datos de la persona");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido guardar los datos de la persona");
		}
		/* TODO: Agregar los datos guardados en la base de datos */
		return salida;
	}

	private JSONObject updateUsuario (HttpServletRequest request) {
		JSONObject recibido, salida;
		Usuario usuario;

		recibido = new JSONObject();
		salida = new JSONObject();

		if (Integer.parseInt(request.getParameter("id_usuario")) == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los datos del usuario Administrador");
			return salida;
		}

		if (request.getParameter("action").equals("new")) {
			recibido.put ("id_usuario", -1);
		} else {
			recibido.put("id_usuario", Integer.parseInt(request.getParameter("id_usuario")));
		}
		recibido.put("id_persona", Integer.parseInt(request.getParameter("id_persona")));
		recibido.put("nombre_usuario", request.getParameter("nombre_usuario"));
		recibido.put("password", request.getParameter("password"));
		recibido.put("email", request.getParameter("email"));
		recibido.put("descripcion", request.getParameter("descripcion"));
		recibido.put("estado", (request.getParameter("estado")));

		usuario = new Usuario (recibido);
		if (usuario.guardar()) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha guardado los datos del usuario");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido guardar los datos del usuario");
		}
		/* TODO: Agregar los datos guardados en la base de datos */
		return salida;
	}

	private JSONObject updateRol (HttpServletRequest request) {
		JSONObject recibido, salida;
		Rol rol;

		recibido = new JSONObject();
		salida = new JSONObject();

		if (Integer.parseInt(request.getParameter("id_rol")) == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los datos del rol SuperUsuario");
			return salida;
		}

		if (request.getParameter("action").equals("new")) {
			recibido.put ("id_rol", -1);
		} else {
			recibido.put ("id_rol", Integer.parseInt (request.getParameter("id_rol")));
		}
		recibido.put ("nombre_rol", request.getParameter("nombre_rol"));
		recibido.put ("nombre_amigable", request.getParameter("nombre_amigable"));
		recibido.put ("descripcion", request.getParameter("descripcion"));
		recibido.put ("estado",request.getParameter("estado"));

		rol = new Rol(recibido);
		if (rol.guardar()){
			salida.put ("result", true);
			salida.put ("msg", "Se ha guardado los datos del rol");
		} else {
			salida.put("result", false);
			salida.put ("msg", "No se ha podido guardar los datos del rol");
		}
		/* TODO: Agregar los datos guardados en la base de datos */
		return salida;
	}

	private JSONObject updatePermiso (HttpServletRequest request) {
		JSONObject recibido, salida;
		Permiso permiso;

		recibido = new JSONObject();
		salida = new JSONObject();

		if (request.getParameter("action").equals("new")) {
			recibido.put ("id_permiso", -1);
		} else {
			recibido.put ("id_permiso", Integer.parseInt (request.getParameter("id_permiso")));
		}
		recibido.put ("nombre_permiso", request.getParameter("nombre_permiso"));
		recibido.put ("funcionalidad", request.getParameter("funcionalidad"));
		recibido.put ("descripcion", request.getParameter("descripcion"));
		recibido.put ("estado", request.getParameter("estado"));

		permiso = new Permiso(recibido);
		if (permiso.guardar()) {
			salida.put("result", true);
			salida.put ("msg", "Se ha guardado los datos del permiso");
		} else {
			salida.put("result", false);
			salida.put ("msg", "No se ha podido guardar los datos del permiso");
		}
		/* TODO: Agregar los datos guardados en la base de datos */
		return salida;
	}

	private JSONObject deletePersona (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id = Integer.parseInt (request.getParameter("id"));
		if (Persona.Eliminar (id)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha eliminado la persona");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha eliminado la persona");
		}
		return salida;
	}

	private JSONObject deleteUsuario (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id = Integer.parseInt (request.getParameter("id"));
		if (id == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite eliminar al usuario Administrador");
		} else if (Usuario.Eliminar(id)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha eliminado el usuario");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido eliminar el usuario");
		}
		return salida;
	}

	private JSONObject deleteRol (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id = Integer.parseInt (request.getParameter("id"));
		if (id == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite eliminar al rol Super Usuario");
		} else if (Rol.Eliminar(id)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha eliminado el rol");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido eliminar el rol");
		}
		return salida;
	}

	private JSONObject deletePermiso (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id = Integer.parseInt (request.getParameter("id"));
		if (Permiso.Eliminar (id)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha eliminado el permiso");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido eliminado el permiso");
		}
		return salida;
	}

	private JSONObject assignRol (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id_usuario = Integer.parseInt (request.getParameter("id_usuario"));
		int id_rol = Integer.parseInt (request.getParameter("id_rol"));
		salida.put ("id_usuario", id_usuario);
		salida.put ("id_rol", id_rol);

		if (id_usuario == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los roles del usuario Administrador");
		} else if (Usuario.AsignarRol(id_rol, id_usuario)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha asignado el rol al usuario");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido asignar el rol al usuario");
		}
		
		return salida;
	}

	private JSONObject removeRol (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id_usuario = Integer.parseInt (request.getParameter("id_usuario"));
		int id_rol = Integer.parseInt (request.getParameter("id_rol"));
		salida.put ("id_usuario", id_usuario);
		salida.put ("id_rol", id_rol);

		if (id_usuario == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los roles del usuario Administrador");
		} else if (Usuario.QuitarRol(id_rol, id_usuario)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha desasignado el rol al usuario");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido desasignar el rol al usuario");
		}
	
		return salida;
	}	
	
	private JSONObject getPermisosUsuario (HttpServletRequest request) {		
		JSONObject salida = new JSONObject ();
		if (AccessManager.EstaLogueado(request)){
			JSONArray permisos = Usuario.GetNombrePermisosUsuario(AccessManager.ValorCookie(request));
			salida.put ("result", true);
			salida.put("permisos", permisos);
		}else{
			salida.put ("result", false);
			salida.put("redirect", "index.html");
		}

		return salida;
	}	

	private JSONObject assignPermiso (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id_rol = Integer.parseInt (request.getParameter("id_rol"));
		int id_permiso = Integer.parseInt (request.getParameter("id_permiso"));
		salida.put ("id_rol", id_rol);
		salida.put ("id_permiso", id_permiso);
		if (id_rol == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los permisos del rol SuperUsuario");
		} else if (Rol.AsignarPermiso(id_permiso, id_rol)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha asignado el permiso al rol");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido asignar el permiso al rol");
		}
		
		return salida;
	}

	private JSONObject revokePermiso (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id_rol = Integer.parseInt (request.getParameter("id_rol"));
		int id_permiso = Integer.parseInt (request.getParameter("id_permiso"));
		salida.put ("id_rol", id_rol);
		salida.put ("id_permiso", id_permiso);
		if (id_rol == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los permisos del rol SuperUsuario");
		} else if(Rol.QuitarPermiso(id_permiso, id_rol)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha revocado el permiso al rol");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido revocar el permiso al rol");
		}
		
		return salida;
	}
}

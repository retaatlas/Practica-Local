package gestionUsuarios.controlador;
import java.io.*;

import gestionUsuarios.modelo.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import otros.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;




public class AdministracionUsuarios extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private DAOAdministracionUsuarios dao;
	
	public void init() throws ServletException
	{
		dao= DAOAdministracionUsuarios.getInstance();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter writer = response.getWriter();

		response.setContentType("application/json");

		if (!AccessManager.HasPermiso(request, "administrar_usuarios")){
			this.printDeniedRedirect(writer);
		}else{
			this.printDbData(writer);
		}
	}

	@SuppressWarnings("unchecked")
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
                            try {
                                out = deletePersona(request);
                            } catch (ExceptionViajesCompartidos ex) {
                                Logger.getLogger(AdministracionUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                            }
			} else if (entity.equals("usuario")) {
                            try {
                                out = deleteUsuario(request);
                            } catch (ExceptionViajesCompartidos ex) {
                                Logger.getLogger(AdministracionUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                            }
			} else if (entity.equals("rol")) {
                            try {
                                out = deleteRol(request);
                            } catch (ExceptionViajesCompartidos ex) {
                                Logger.getLogger(AdministracionUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                            }
			} else if (entity.equals("permiso")) {
                            try {
                                out = deletePermiso(request);
                            } catch (ExceptionViajesCompartidos ex) {
                                Logger.getLogger(AdministracionUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                            }
			}
		} else if (action.equals("assignRol") && entity.equals("usuario")) {
                    try {
                        out = assignRol(request);
                    } catch (ExceptionViajesCompartidos ex) {
                        Logger.getLogger(AdministracionUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                    }
		} else if (action.equals("removeRol") && entity.equals("usuario")) {
			out = removeRol(request);
		} else if (action.equals("assignPermiso") && entity.equals("rol")) {
                    try {
                        out = assignPermiso(request);
                    } catch (ExceptionViajesCompartidos ex) {
                        Logger.getLogger(AdministracionUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                    }
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

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	private void printDeniedRedirect (PrintWriter writer) {
		JSONObject resultado;

		resultado = new JSONObject();
		resultado.put("result", false);
		resultado.put("redirect", "acceso_denegado.html");

		writer.println(resultado);
	}

	@SuppressWarnings("unchecked")
	private JSONArray getPersonas() {
		JSONArray personas;
		JSONObject[] in;
		Object id, fechaNac;

		in = dao.selectAllJSON(Persona.class.getSimpleName());
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

	@SuppressWarnings("unchecked")
	private JSONArray getUsuarios() {
		JSONArray usuarios;
		JSONObject[] in;
		Object id;
                
		in=dao.selectAllJSON(Usuario.class.getSimpleName());
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

	@SuppressWarnings("unchecked")
	private JSONArray getRoles() {
		JSONArray roles;
		JSONObject[] in;
		Object id;

		in=dao.selectAllJSON(Rol.class.getSimpleName());
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

	@SuppressWarnings("unchecked")
	private JSONArray getPermisos() {
		JSONArray permisos;
		JSONObject[] in;
		Object id;

		in=dao.selectAllJSON(Permiso.class.getSimpleName());
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

	@SuppressWarnings("unchecked")
	private JSONObject updatePersona (HttpServletRequest request) {
		JSONObject recibida, salida;
		Persona persona;

		recibida = new JSONObject();
		salida = new JSONObject();

		recibida.put("id_persona", Integer.parseInt(request.getParameter("id_persona")));
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
		if (request.getParameter("action").equals("new")) {
			try {
				salida.put ("result", dao.persistir(persona));
				salida.put ("msg", "Se han guardado los datos de la persona");
			} catch (ExceptionViajesCompartidos e) {
				salida.put ("result", false);
				salida.put ("msg", e.getMessage());
			}
		} else {
			try {
				salida.put ("result",dao.actualizar(persona));
				salida.put ("msg", "No se ha podido guardar los datos de la persona");
			} catch (ExceptionViajesCompartidos e) {
				salida.put ("result", false);
				salida.put ("msg", e.getMessage());
			}
		}
		/* TODO: Agregar los datos guardados en la base de datos */
		return salida;
	}

	@SuppressWarnings("unchecked")
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
		
		recibido.put("id_usuario", Integer.parseInt(request.getParameter("id_usuario")));
		recibido.put("id_persona", Integer.parseInt(request.getParameter("id_persona")));
		recibido.put("nombre_usuario", request.getParameter("nombre_usuario"));
		recibido.put("password", request.getParameter("password"));
		recibido.put("email", request.getParameter("email"));
		recibido.put("descripcion", request.getParameter("descripcion"));
		recibido.put("estado", (request.getParameter("estado")));
		recibido.put("tipo", "U".charAt(0));
		usuario = new Usuario (recibido);
		if (request.getParameter("action").equals("new")) {
			try {
				salida.put ("result",dao.persistirUsuarioConPersona(usuario,Integer.parseInt(request.getParameter("id_persona"))));
				salida.put ("msg", "Se ha guardado los datos del usuario");
			} catch (Exception e) {
				salida.put ("result", false);
				salida.put ("msg", e.getMessage());
			}
		} else {
			try {
				salida.put ("result",dao.actualizar(usuario));
				salida.put ("msg", "No se ha podido guardar los datos del usuario");
			} catch (ExceptionViajesCompartidos e) {
				salida.put ("result", false);
				salida.put ("msg", e.getMessage());
			}
		}
		return salida;
	}

	@SuppressWarnings("unchecked")
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
		
		recibido.put ("id_rol", Integer.parseInt (request.getParameter("id_rol")));
		recibido.put ("nombre_rol", request.getParameter("nombre_rol"));
		recibido.put ("nombre_amigable", request.getParameter("nombre_amigable"));
		recibido.put ("descripcion", request.getParameter("descripcion"));
		recibido.put ("estado",request.getParameter("estado"));

		rol = new Rol(recibido);
		if (request.getParameter("action").equals("new")) {
			try {
				salida.put ("result",dao.persistir(rol));
				salida.put ("msg", "Se ha guardado los datos del rol");
			} catch (ExceptionViajesCompartidos e) {
				salida.put ("result", false);
				salida.put ("msg", e.getMessage());
			}
		} else {
			try {
				salida.put ("result",dao.actualizar(rol));
				salida.put ("msg", "No se ha podido guardar los datos del rol");
			} catch (ExceptionViajesCompartidos e) {
				salida.put ("result", false);
				salida.put ("msg", e.getMessage());
			}
		}
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject updatePermiso (HttpServletRequest request) {
		JSONObject recibido, salida;
		Permiso permiso;

		recibido = new JSONObject();
		salida = new JSONObject();

		recibido.put ("id_permiso", Integer.parseInt (request.getParameter("id_permiso")));
		recibido.put ("nombre_permiso", request.getParameter("nombre_permiso"));
		recibido.put ("funcionalidad", request.getParameter("funcionalidad"));
		recibido.put ("descripcion", request.getParameter("descripcion"));
		recibido.put ("estado", request.getParameter("estado"));

		permiso = new Permiso(recibido);
		if(request.getParameter("action").equals("new")) {
			try {
				salida.put ("result",dao.persistir(permiso));
				salida.put ("msg", "Se ha guardado los datos del permiso");
			} catch (ExceptionViajesCompartidos e) {
				salida.put ("result", false);
				salida.put ("msg", e.getMessage());
			}
		} else {
			try {
				salida.put ("result",dao.actualizar(permiso));
				salida.put ("msg", "No se ha podido guardar los datos del permiso");
			} catch (ExceptionViajesCompartidos e) {
				salida.put ("result", false);
				salida.put ("msg", e.getMessage());
			}
		}
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject deletePersona (HttpServletRequest request) throws ExceptionViajesCompartidos {
		JSONObject salida = new JSONObject ();
		int id = Integer.parseInt (request.getParameter("id"));
		if (dao.darDeBaja(id,"Persona")) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha eliminado la persona");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha eliminado la persona");
		}
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject deleteUsuario (HttpServletRequest request) throws ExceptionViajesCompartidos {
		JSONObject salida = new JSONObject ();
		int id = Integer.parseInt (request.getParameter("id"));
		if (id == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite eliminar al usuario Administrador");
		} else if (dao.darDeBaja(id,"Usuario")) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha eliminado el usuario");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido eliminar el usuario");
		}
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject deleteRol (HttpServletRequest request) throws ExceptionViajesCompartidos {
		JSONObject salida = new JSONObject ();
		int id = Integer.parseInt (request.getParameter("id"));
		if (id == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite eliminar al rol Super Usuario");
		} else if (dao.darDeBaja(id,"Rol")) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha eliminado el rol");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido eliminar el rol");
		}
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject deletePermiso (HttpServletRequest request) throws ExceptionViajesCompartidos {
		JSONObject salida = new JSONObject ();
		int id = Integer.parseInt (request.getParameter("id"));
		if (dao.darDeBaja(id,"Permiso")) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha eliminado el permiso");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido eliminado el permiso");
		}
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject assignRol (HttpServletRequest request) throws ExceptionViajesCompartidos {
		JSONObject salida = new JSONObject ();
		int id_usuario = Integer.parseInt (request.getParameter("id_usuario"));
		int id_rol = Integer.parseInt (request.getParameter("id_rol"));
		salida.put ("id_usuario", id_usuario);
		salida.put ("id_rol", id_rol);

		if (id_usuario == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los roles del usuario Administrador");
		} else if (dao.asignarRolAUsuario(id_rol, id_usuario)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha asignado el rol al usuario");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido asignar el rol al usuario");
		}
		
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject removeRol (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id_usuario = Integer.parseInt (request.getParameter("id_usuario"));
		int id_rol = Integer.parseInt (request.getParameter("id_rol"));
		salida.put ("id_usuario", id_usuario);
		salida.put ("id_rol", id_rol);

		if (id_usuario == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los roles del usuario Administrador");
		} else if (dao.desasignarRolAUsuario(id_rol, id_usuario)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha desasignado el rol al usuario");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido desasignar el rol al usuario");
		}
	
		return salida;
	}	
	
	@SuppressWarnings("unchecked")
	private JSONObject getPermisosUsuario (HttpServletRequest request) {		
		JSONObject salida = new JSONObject ();
		if (AccessManager.EstaLogueado(request)){
			JSONObject usuario = new JSONObject();
			String nombreUsuario = AccessManager.ValorCookie(request);
			usuario.put("nombre_usuario", nombreUsuario);
			JSONArray roles = dao.NombreRolUsuario(nombreUsuario);
			JSONArray permisos = dao.NombrePermisosDeUnUsuario(nombreUsuario);
			salida.put ("result", true);
			salida.put("usuario", usuario);
			salida.put("roles", roles);
			salida.put("permisos", permisos);
		}else{
			salida.put ("result", false);
			salida.put("redirect", "index.html");
		}
		return salida;
	}	

	@SuppressWarnings("unchecked")
	private JSONObject assignPermiso (HttpServletRequest request) throws ExceptionViajesCompartidos {
		JSONObject salida = new JSONObject ();
		int id_rol = Integer.parseInt (request.getParameter("id_rol"));
		int id_permiso = Integer.parseInt (request.getParameter("id_permiso"));
		salida.put ("id_rol", id_rol);
		salida.put ("id_permiso", id_permiso);
		if (id_rol == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los permisos del rol SuperUsuario");
		} else if (dao.asignarPermisoARol(id_rol, id_permiso)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha asignado el permiso al rol");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido asignar el permiso al rol");
		}
		
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject revokePermiso (HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		int id_rol = Integer.parseInt (request.getParameter("id_rol"));
		int id_permiso = Integer.parseInt (request.getParameter("id_permiso"));
		salida.put ("id_rol", id_rol);
		salida.put ("id_permiso", id_permiso);
		if (id_rol == 0) {
			salida.put ("result", false);
			salida.put ("msg", "No se permite modificar los permisos del rol SuperUsuario");
		} else if(dao.desasignarPermisoARol(id_permiso, id_rol)) {
			salida.put ("result", true);
			salida.put ("msg", "Se ha revocado el permiso al rol");
		} else {
			salida.put ("result", false);
			salida.put ("msg", "No se ha podido revocar el permiso al rol");
		}
		
		return salida;
	}
}

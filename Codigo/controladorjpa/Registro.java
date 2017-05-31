package controladorjpa;

import java.io.*;
import java.math.BigInteger;

import javax.servlet.*;
import javax.servlet.http.*;

import modelojpa.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import controladorjpa.AccessManager;

public class Registro extends HttpServlet {

	DAOAdministracioUsuarios dao;

	public void init() throws ServletException
	{
		dao= new DAOAdministracioUsuarios();
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
		
		String accion = request.getParameter("action");

		if (accion.equals("usuario_existe")){
			//recibo el nombre del usuario y pregunto si existe o no.
			out = validarUsuario(request,response);
		}else if(accion.equals("mail_existe")){
			out = validarMail(request,response);
		}else if(accion.equals("documento_existe")){
			out = validarDocumento(request,response);
		}else if(accion.equals("new")){
			out = registrarCliente(request);
		}else if(accion.equals("subir_imagen")){
			out = subirImagen(request);
		}
		
		if (out == null) {
			out = new JSONObject();
			out.put ("result", false);
		}
		
		out.put("action", accion);
		System.out.println("Lo que mando al js: "+out);
		writer.println (out);

	}

	private JSONObject registrarCliente(HttpServletRequest request) {
		JSONObject out = new JSONObject();
		JSONObject persona = this.cargarPersona(request);
		JSONObject cliente = this.cargarCliente(request);

		if (dao.nuevoCliente(persona,cliente)){
			out.put ("result", true);
			out.put ("msg", "El usuario ha sido registrado correctamente");
		}else{
			out.put ("result", false);
			out.put ("msg", "Error en registro de usuario");
		}

		return out;
	}
	private JSONObject cargarPersona(HttpServletRequest request){
		JSONObject persona = new JSONObject();
		
		persona.put ("id_persona", -1);
		persona.put("nombres", request.getParameter("persona[nombres]"));
		persona.put("apellidos", request.getParameter("persona[apellidos]"));
		persona.put("tipo_doc", Integer.parseInt(request.getParameter("persona[tipo_doc]")));
		persona.put("nro_doc", Integer.parseInt(request.getParameter("persona[nro_doc]")));
		persona.put("fecha_nacimiento", request.getParameter("persona[fecha_nacimiento]"));
		persona.put("sexo", request.getParameter("persona[sexo]"));
		persona.put("domicilio", request.getParameter("persona[domicilio]"));
		persona.put("telefono", request.getParameter("persona[telefono]"));
		persona.put("estado", "A");		
		
		return persona;
	}

	private JSONObject cargarCliente(HttpServletRequest request){
		JSONObject cliente = new JSONObject();

		cliente.put("id_usuario", -1);
		cliente.put("nombre_usuario", request.getParameter("usuario[nombre_usuario]"));
		cliente.put("password", request.getParameter("usuario[password]"));
		cliente.put("email", request.getParameter("usuario[email]"));
		cliente.put("descripcion", request.getParameter("usuario[descripcion]"));
		cliente.put("estado", "A");
		/*
		String pathImgRegistro = this.subirArchivo(request.getParameter("cliente[foto_registro]"));
		cliente.put("foto_registro", pathImgRegistro);
		
		String pathImgUsuario = this.subirArchivo(request.getParameter("cliente[foto_usuario]"));
		cliente.put("foto", pathImgUsuario);
			*/
		return cliente;
	}

	private JSONObject subirImagen(HttpServletRequest request) {
		JSONObject out = new JSONObject();
		System.out.println("en subir imagen de servlet");
		//Subo imagen al servidor
		String imagen = request.getParameter("imagen");
		String path = this.subirArchivo(imagen);
		System.out.println("Imagen subida");

		// Cargo JSON para subir path de imagen a la bd
		JSONObject foto = new JSONObject();
		foto.put("usuario", request.getParameter("usuario"));
		foto.put("imagen", path);
		
		// Es foto del registro de conducir o del cliente?
		boolean bandera=false;
		String campo = request.getParameter("campo");
		if (campo.equals("foto_registro")){
			System.out.println("Subiendo foto registro");
			bandera = dao.subirFotoRegistro(foto);
		}else if(campo.equals("foto")){
			System.out.println("Subiendo foto");
			bandera = dao.subirFotoCliente(foto);
		}
		System.out.println("servlet: foto subida");

		if (bandera){
			out.put ("result", true);
			out.put ("msg", "Imagen guardada");
		}else{
			out.put ("result", false);
			out.put ("msg", "Error al guardar la imagen");
		}
		
		return out;
	}
	
	private String subirArchivo(String archivo) {
		if (!archivo.isEmpty()){
			archivo = FileManager.uploadImage(getServletContext().getRealPath("/"), archivo);
		}
		return archivo;
	}

	public void destroy()
	{
	}
	private JSONObject validarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = request.getParameter("usuario");
		JSONObject salida = new JSONObject ();	
		Usuario u = dao.buscarUsuarioPorNombre(user);
		if (u != null) {
			System.out.println("Usuario existe");
			salida.put ("existe", true); 
			salida.put ("result", true); 
		}else{
			System.out.println("Usuario no existe");
			salida.put ("existe", false); 
			salida.put("result", true);
		}
		return salida;
	}
	private JSONObject validarMail(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String mail = request.getParameter("mail");
		JSONObject salida = new JSONObject ();	
		if (dao.mailExiste(mail)) {
			System.out.println("Mail existe");
			salida.put ("existe", true); 
			salida.put ("result", true); 
		}else{
			System.out.println("Mail no existe");
			salida.put ("existe", false); 
			salida.put("result", true);
		}
		return salida;
	}
	private JSONObject validarDocumento(HttpServletRequest request, HttpServletResponse response) throws IOException {
		BigInteger numero = new BigInteger(request.getParameter("nro"));
		Integer tipo = Integer.parseInt(request.getParameter("tipo"));
		JSONObject salida = new JSONObject ();	
		if (dao.documentoExiste(tipo,numero)) {
			System.out.println("Documento existe");
			salida.put ("existe", true); 
			salida.put ("result", true); 
		}else{
			System.out.println("Documento no existe");
			salida.put ("existe", false); 
			salida.put("result", true);
		}
		return salida;
	}
}

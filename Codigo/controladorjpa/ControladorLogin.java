package controladorjpa;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import controladorjpa.*;

@SuppressWarnings("serial")
public class ControladorLogin extends HttpServlet {
	
	DAOAdministracioUsuarios dao;

	public void init() throws ServletException
	{
		dao= new DAOAdministracioUsuarios();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("En doGet de ControladorLogin");
		
		// Cuando se desloguea
		if (AccessManager.EstaLogueado(request)){
			if (AccessManager.EliminarCookie(request, response)){
				System.out.println("Log out: cookie eliminada");
				response.sendRedirect("index.html");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("En doPost de ControladorLogin!");
		JSONObject out = null;

		PrintWriter writer = response.getWriter();
		
		String accion = request.getParameter("accion");

		if (accion.equals("login")){
			out = login(request,response);
		}
		
		if (out == null) {
			out = new JSONObject();
			out.put ("result", false);
		}
		
		out.put("action", accion);
		System.out.println("Lo que mando al js: "+out);
		writer.println (out);

	}

	@SuppressWarnings("unchecked")
	private JSONObject login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Parametros del formulario
		String user = request.getParameter("usuario");
		String pass = request.getParameter("pass");
		JSONObject salida = new JSONObject ();	
		if (dao.isUsuarioPass(user,pass)) {
			//agrego cookie
			AccessManager.SetearCookie(response,user);
			System.out.println("Usuario logueado");
			salida.put ("result", true); 
			salida.put ("msg", "El usuario se ha logueado correctamente.");
			salida.put("redirect", "home.html");
		}else{
			salida.put("result", false);
			salida.put ("msg", "<p>Usuario o contrase&ntilde;a incorrectas.</p>");
		}
		return salida;
	}

	public void destroy()
	{
	}

		
}

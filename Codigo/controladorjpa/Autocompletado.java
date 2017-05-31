package controladorjpa;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import modelojpa.Persona;
import controladorjpa.AccessManager;
import gestionViajes.modelo.Vehiculo;

public class Autocompletado extends HttpServlet {

	DAOAdministracioUsuarios dao;

	public void init() throws ServletException
	{
		dao= new DAOAdministracioUsuarios();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");

		PrintWriter writer = response.getWriter();
		String entity = request.getParameter("entity");
		String busqueda = request.getParameter("query");
		JSONArray encontrados = new JSONArray();

		if (busqueda == null || busqueda.equals("")) {
			//Hacer nada
		} else if (AccessManager.EstaLogueado(request) && entity.equals("persona")) {
			/* Deberia exisitir un permiso "buscar_persona" ??? */
			List<Persona> resultados;
			resultados = (List<Persona>) dao.autocompletar("Persona", busqueda);
			for (Persona persona: resultados) {
				JSONObject item = new JSONObject();
				item.put("id", persona.getId_persona());
				item.put("name", persona.getNombres()+ " " + persona.getApellidos() + ": " + persona.getNro_doc());
				encontrados.add(item);
				
			}
		}else if (AccessManager.EstaLogueado(request) && entity.equals("vehiculo")) {
			/* Deberia exisitir un permiso "buscar_persona" ??? */
			List<Vehiculo> resultados;
			resultados = (List<Vehiculo>) dao.autocompletar("Vehiculo", busqueda);
			for (Vehiculo vehiculo: resultados) {
				JSONObject item = new JSONObject();
				//item.put("id", vehiculo.getId_vehiculo());
				item.put("patente", vehiculo.getPatente());
				encontrados.add(item);
				
			}
		}

		writer.print(encontrados);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

	}

	public void destroy()
	{
	}

}

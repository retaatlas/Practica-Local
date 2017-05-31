package otros;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import gestionUsuarios.controlador.*;
import gestionUsuarios.modelo.*;
import gestionViajes.controlador.*;
import gestionViajes.modelo.*;
import otros.*;

public class Autocompletado extends HttpServlet {

	DAOAdministracionUsuarios dao;

	public void init() throws ServletException
	{
		dao= DAOAdministracionUsuarios.getInstance();
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
		} else if (entity.equals("localidad")) {
			List<Localidad> resultados;
			resultados = (List<Localidad>) dao.autocompletar("Localidad", busqueda);
			for (Localidad localidad: resultados) {
				JSONObject item = new JSONObject();
				item.put("id", localidad.getId());
				item.put("name", localidad.getNombre()+", "+localidad.getNombreProvincia());
				item.put("lat", localidad.getLatitud());
				item.put("lng", localidad.getLongitud());
				encontrados.add(item);
			}
		} else if (entity.equals("cliente")){
			List<Cliente> resultados;
			resultados = (List<Cliente>) dao.autocompletar("Cliente", busqueda);
			for (Cliente cliente: resultados) {
				if (cliente.isActivo()){
					JSONObject item = new JSONObject();
					item.put("id", cliente.getId_usuario());
					item.put("nombre_usuario", cliente.getNombre_usuario());
					item.put("name", cliente.getNombre_usuario() + " - " + cliente.getEmail());
					encontrados.add(item);
				}
			}
		}else if (entity.equals("vehiculo")){
			List<Vehiculo> resultados;
			resultados = (List<Vehiculo>) dao.autocompletar("Vehiculo", busqueda);
			for (Vehiculo vehiculo: resultados) {
				if (vehiculo.isActivo()){
					JSONObject item = new JSONObject();
					
					item.put("patente", vehiculo.getPatente());
                                        item.put("marca", vehiculo.getMarca());
                                        item.put("modelo", vehiculo.getModelo());
					encontrados.add(item);
				}
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

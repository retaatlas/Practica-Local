package gestionPuntos.controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gestionPuntos.modelo.MovimientoPuntos;
import gestionPuntos.modelo.TipoMovimientoPuntos;
import gestionUsuarios.controlador.DAOAdministracionUsuarios;
import gestionUsuarios.modelo.Cliente;
import otros.AccessManager;
import otros.ExceptionViajesCompartidos;

public class ServletPuntos extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	protected DAOPuntos daopuntos;
	protected DAOAdministracionUsuarios daoUsuarios;
	
	public void init() throws ServletException {
		daopuntos= DAOPuntos.getInstance();
		daoUsuarios=DAOAdministracionUsuarios.getInstance();
	}
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject respuesta = new JSONObject();
		PrintWriter writer = response.getWriter();
		String action = request.getParameter("action");
		String entity = request.getParameter("entity");

		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "application/json; charset=UTF-8");
		
		if (entity != null && entity.equals ("puntos")) {
			if (action != null && action.equals ("consultar")) {
				respuesta = this.consultarPuntos (request);
			}
		}else {
			respuesta = new JSONObject();
			respuesta.put ("result", false);
			respuesta.put ("msg", "No implementado");
		}
		
		respuesta.put("entity", entity);
		respuesta.put("action", action);
		
		
		writer.println (respuesta);
	}
	
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject respuesta = new JSONObject();
		PrintWriter writer = response.getWriter();
		String action = request.getParameter("action");
		String entity = request.getParameter("entity");
		
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "application/json; charset=UTF-8");
		
		if (entity != null && entity.equals ("puntos")) {
			if (action != null && action.equals("todos")) {
				respuesta = this.tipoMovimientoPuntos (request);
			} else if (action != null && action.equals("movimientos")) {
				respuesta = this.movimientoPuntos (request);
			}
		}else {
			respuesta = new JSONObject();
			respuesta.put ("result", false);
			respuesta.put ("msg", "No implementado");
		}
		respuesta.put("entity", entity);
		respuesta.put("action", action);

		System.out.println (respuesta);
		writer.println (respuesta);
	}

	private JSONObject tipoMovimientoPuntos(HttpServletRequest request) {
		
		ArrayList<String> err = new ArrayList<String>();
		JSONObject salida = new JSONObject();
		String descripcion=null;
		try {
			descripcion = request.getParameter("descripcion");
		} catch (Exception e) {
			err.add("id_comision no es valido");
		}
		if(err.size() > 0) {
			salida.put("result", false);
			salida.put("msg", err);
			return salida;
		}
		
		TipoMovimientoPuntos tmp=this.daopuntos.getTipoMovimientoPuntos(descripcion);
		
		salida.put("result", true);
		salida.put("TipoMovimientoPuntos", tmp.toJSON());
		return salida;
	}

	
	private JSONObject consultarPuntos(HttpServletRequest request) {
		JSONObject salida = new JSONObject();
		String username;
		int puntos;
		try {
			username = AccessManager.nombreUsuario(request);
			if (username == null) {
				throw new ExceptionViajesCompartidos("No ha iniciado sesion como usuario valido");
			}
			Cliente cliente = daoUsuarios.clientePorNombre(username);
			if (cliente == null && cliente.isActivo()) {
				throw new ExceptionViajesCompartidos("El usuario con el que ha iniciado sesion no es un cliente");
			}
			// Si no hacemos refresh no se actualiza hasta que reiniciemos el tomcat
			daoUsuarios.refresh(cliente);
			puntos = cliente.getPuntos();
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			salida.put("redirect", "/home.html");
			return salida;
		}

		salida.put ("result", true);
		salida.put("puntos", puntos);
		return salida;
	}

	
	private JSONObject movimientoPuntos(HttpServletRequest request) {
		
		JSONObject salida = new JSONObject();
		
		Integer id_cliente=AccessManager.getIdUsuario(request);
				
		List<MovimientoPuntos> movimientos =this.daopuntos.getMovimientoPuntos(id_cliente);
		if (movimientos == null){
			salida.put("result", false);
			salida.put("msg","Error al cargar sus movimientos de puntos");
			return salida;
		}
		
		JSONArray array = new JSONArray();
		for(MovimientoPuntos mp: movimientos){
			array.add(mp.toJSON());
		}
		
		salida.put("result", true);
		salida.put("mov_puntos", array);
		return salida;
	}
}

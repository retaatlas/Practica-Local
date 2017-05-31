package gestionComisiones.controlador;

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

import gestionComisiones.modelo.Comision;
import gestionComisiones.modelo.MovimientoSaldo;
import gestionUsuarios.controlador.DAOAdministracionUsuarios;
import gestionUsuarios.controlador.DAONotificaciones;
import gestionUsuarios.modelo.Cliente;
import otros.AccessManager;
import otros.ExceptionViajesCompartidos;

public class ServletComisiones extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected DAOComisiones daocomisiones;
	protected DAOAdministracionUsuarios daoUsuarios;
	protected DAONotificaciones daoNotificaciones;
	
	public void init() throws ServletException {
		daocomisiones = DAOComisiones.getInstance();
		daoUsuarios=DAOAdministracionUsuarios.getInstance();
		daoNotificaciones=DAONotificaciones.getInstance();
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject respuesta = new JSONObject();
		PrintWriter writer = response.getWriter();
		String action = request.getParameter("action");
		String entity = request.getParameter("entity");

		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "application/json; charset=UTF-8");
		
		if (entity != null && entity.equals ("comision")) {
			if (action != null && action.equals ("new")) {
				respuesta = this.nuevo_comision (request);
			}else if (action != null && action.equals("edit")) {
				respuesta = this.modificar_comision (request);
			}else if (action != null && action.equals("finalizar")){
				respuesta = this.finalizar_comision (request);
			}
		}else if (entity != null && entity.equals("saldo")) {
			if (action != null && action.equals ("cargar")) {
				respuesta = this.cargarSaldo(request);
			} else if (action != null && action.equals ("movimientos")) {
				respuesta = this.movimientosSaldo(request);
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

	@SuppressWarnings("unchecked")
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject respuesta = new JSONObject();
		PrintWriter writer = response.getWriter();
		String action = request.getParameter("action");
		String entity = request.getParameter("entity");

		if (entity != null && entity.equals ("comision")) {
			if (action != null && action.equals("todos")) {
				respuesta = this.todosComisiones (request);
			}else if(action != null && action.equals("vigentes")){
				respuesta = this.comisionesVigentes (request);
			}else if(action != null && action.equals("Novigentes")){
				respuesta = this.comisionesNoVigentes (request);
			}
		} else if (entity != null && entity.equals("saldo")) {
			if (action != null && action.equals ("cargar")) {
				respuesta = this.cargarSaldo(request);
			} else if (action != null && action.equals ("consultar")) {
				respuesta = this.consultarSaldo(request);
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
	
	@SuppressWarnings("unchecked")
	private JSONObject comisionesNoVigentes(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		List<Comision> listacomisiones = this.daocomisiones.getComisionesNOVigentes();
		JSONArray comisiones = new JSONArray();
		for (Comision c : listacomisiones){
			comisiones.add(c.toJSON());
		}
				
		respuesta.put("result", true);
		respuesta.put("msg", "Postulantes cargados");
		respuesta.put("comisiones", comisiones);
				
		return respuesta;
		
	}

	@SuppressWarnings("unchecked")
	private JSONObject comisionesVigentes(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		List<Comision> listacomisiones = this.daocomisiones.getComisionesVigentes();
		JSONArray comisiones = new JSONArray();
		for (Comision c : listacomisiones){
			comisiones.add(c.toJSON());
		}
				
		respuesta.put("result", true);
		respuesta.put("msg", "Postulantes cargados");
		respuesta.put("comisiones", comisiones);
				
		return respuesta;
	}

	@SuppressWarnings("unchecked")
	private JSONObject todosComisiones(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		List<Comision> listacomisiones = this.daocomisiones.getTodasLasComisiones();
		JSONArray comisiones = new JSONArray();
		for (Comision c : listacomisiones){
			comisiones.add(c.toJSON());
		}
				
		respuesta.put("result", true);
		respuesta.put("msg", "Postulantes cargados");
		respuesta.put("comisiones", comisiones);
				
		return respuesta;
	}

	@SuppressWarnings("unchecked")
	private JSONObject finalizar_comision(HttpServletRequest request) {
		ArrayList<String> err = new ArrayList<String>();
		JSONObject salida = new JSONObject();
		Integer id_comision=null;
		try {
			id_comision = Integer.parseInt(request.getParameter("id_comision"));
		} catch (Exception e) {
			err.add("id_comision no es valido");
		}
		if(err.size() > 0) {
			salida.put("result", false);
			salida.put("msg", err);
			return salida;
		}
		try {
			this.daocomisiones.FinalizarComision(id_comision);
		} catch (ExceptionViajesCompartidos e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			return salida;
		}
		salida.put("result", true);
		salida.put("msg", "Se ha finalizado la comision");
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject modificar_comision(HttpServletRequest request) {
		ArrayList<String> err = new ArrayList<String>();
		Integer limite_inferior=null, limite_superior=null,id_comision=null;
		Float precio=null;
		JSONObject salida = new JSONObject();
		JSONObject params = new JSONObject();

		try {
			limite_inferior = Integer.parseInt(request.getParameter("limite_inferior"));
		} catch (Exception e) {
			err.add("limite_inferior no es valido");
		}
		try {
			limite_superior = Integer.parseInt(request.getParameter("limite_superior"));
		} catch (Exception e) {
			err.add("limite_superior no es valido");
		}
		try {
			precio = Float.parseFloat(request.getParameter("precio"));
		} catch (Exception e) {
			err.add("precio no es valido");
		}
		try {
			id_comision = Integer.parseInt(request.getParameter("id_comision"));
		} catch (Exception e) {
			err.add("id_comision no es valido");
		}
		
		if(err.size() > 0) {
			salida.put("result", false);
			salida.put("msg", err);
			return salida;
		}
		
		params.put("limite_inferior", limite_inferior);
		params.put("limite_superior", limite_superior);
		params.put("id_comision", id_comision);
		params.put("precio", precio);
		
		try {
			this.daocomisiones.modificarComision(params);
		} catch (ExceptionViajesCompartidos e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			return salida;
		}
		salida.put("result", true);
		salida.put("msg", "Se ha modificado la comision");
		return salida;
	}
	

	
	@SuppressWarnings("unchecked")
	private JSONObject nuevo_comision(HttpServletRequest request) {
		ArrayList<String> err = new ArrayList<String>();
		Integer limite_inferior=null, limite_superior=null;
		Float precio=null;
		JSONObject salida = new JSONObject();
		JSONObject params = new JSONObject();
		//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			limite_inferior = Integer.parseInt(request.getParameter("limite_inferior"));
		} catch (Exception e) {
			err.add("limite_inferior no es valido");
		}
		try {
			limite_superior = Integer.parseInt(request.getParameter("limite_superior"));
		} catch (Exception e) {
			err.add("limite_superior no es valido");
		}
		try {
			precio = Float.parseFloat(request.getParameter("precio"));
		} catch (Exception e) {
			err.add("precio no es valido");
		}
		
		if(err.size() > 0) {
			salida.put("result", false);
			salida.put("msg", err);
			return salida;
		}
		
		params.put("limite_inferior", limite_inferior);
		params.put("limite_superior", limite_superior);
		params.put("precio", precio);
		
		try {
			this.daocomisiones.nuevaComision(params);
		} catch (ExceptionViajesCompartidos e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			return salida;
		}
		salida.put("result", true);
		salida.put("msg", "Se ha creado la comision");
		return salida;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject consultarSaldo(HttpServletRequest request) {
		JSONObject salida = new JSONObject();
		String username;
		float saldo=0f;
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
			saldo = cliente.getSaldo();
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			salida.put("redirect", "/login.html");
			return salida;
		}

		salida.put ("result", true);
		salida.put("saldo", saldo);
		return salida;
	}

	private JSONObject movimientosSaldo(HttpServletRequest request) {
		JSONObject salida = new JSONObject();
		String username = null;
		Cliente cliente = null;
		try {
			username = AccessManager.nombreUsuario(request);
			if (username == null) {
				throw new ExceptionViajesCompartidos("No ha iniciado sesion como usuario valido");
			}
			cliente = daoUsuarios.clientePorNombre(username);
			if (cliente == null && cliente.isActivo()) {
				throw new ExceptionViajesCompartidos("El usuario con el que ha iniciado sesion no es un cliente");
			}
			// Si no hacemos refresh no se actualiza hasta que reiniciemos el tomcat
			daoUsuarios.refresh(cliente);
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			salida.put("redirect", "/login.html");
			return salida;
		}

		List<MovimientoSaldo> lista = daocomisiones.getMovimientosSaldo(cliente.getId_usuario());
		JSONArray movimientos = new JSONArray();
		for (MovimientoSaldo ms : lista){
			movimientos.add(ms.toJSON());
		}
		
		salida.put ("result", true);
		salida.put ("mov_saldo", movimientos);
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject cargarSaldo(HttpServletRequest request) {
		/* Esto en realidad tendria que crear una factura y ponerle estado pendiente de pago, pero como todavia no implementamos ningun medio de pago vamos a hacer que se cargue directamente */
		JSONObject salida = new JSONObject();
		int monto;
		String username;
		try {
			username = AccessManager.nombreUsuario(request);
			if (username == null) {
				throw new ExceptionViajesCompartidos("No ha iniciado sesion como usuario valido");
			}
			Cliente cliente = daoUsuarios.clientePorNombre(username);
			if (cliente == null) {
				throw new ExceptionViajesCompartidos("El usuario con el que ha iniciado sesion no es un cliente");
			}
			monto = Integer.parseInt(request.getParameter("monto"));
			daocomisiones.sumarSaldo (cliente.getId_usuario(), monto);
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			salida.put("redirect", "/login.html");
			return salida;
		}
		salida.put("result", true);
		salida.put("msg", "La carga de saldo ha sido exitosa");
		return salida;
	}
}

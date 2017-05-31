package gestionViajes.controlador;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import gestionPuntos.controlador.DAOPuntos;
import gestionPuntos.modelo.Calificacion;
import gestionUsuarios.controlador.DAOAdministracionUsuarios;
import gestionUsuarios.modelo.*;
import gestionViajes.modelo.*;
import otros.ExceptionViajesCompartidos;
import otros.AccessManager;
import otros.FileManager;


public class ServletViaje extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected DAOAdministracionUsuarios daoUsuarios;
	protected DAOViajes daoViajes;
	protected DAOPuntos daoPuntos;

	public void init() throws ServletException {
		daoUsuarios = DAOAdministracionUsuarios.getInstance();
		daoViajes = DAOViajes.getInstance();
		daoPuntos = DAOPuntos.getInstance();
	}
	
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject respuesta = new JSONObject();
		PrintWriter writer = response.getWriter();
		String action = request.getParameter("action");
		String entity = request.getParameter("entity");
		/* TODO: comprobar permisos */	

		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "application/json; charset=UTF-8");


		if (entity != null && entity.equals ("viaje")) {
			if (action != null && action.equals ("new")) {
				respuesta = this.nuevo_viaje (request);
			} else if (action != null && action.equals("edit")) {
				respuesta = this.modificar_viaje (request);
			} else if (action != null && action.equals("cancelar")) {
				respuesta = this.cancelar_viaje (request);
			} else if (action != null && action.equals("buscar")) {
				respuesta = this.buscar_viaje (request);
			} else if (action != null && action.equals("detalle")) {
				respuesta = this.ver_viaje_detallado (request);
			} else if (action != null && action.equals("participar")){
				respuesta = this.participar_viaje(request);
			} else if(action != null && action.equals("cancelar_participacion")){
				respuesta = this.cancelar_participacion(request);
			} else if (action != null && action.equals("ver_postulantes")){
				respuesta = this.ver_postulantes(request);
			} else if (action != null && action.equals("aceptar_rechazar_postulante")){
				respuesta = this.aceptar_rechazar_postulante(request);
			} else if (action != null && action.equals("finalizar_viaje")) {
				respuesta = this.finalizarViaje(request);
			} else if (action != null && action.equals("ver_mis_viajes")) {
				respuesta = this.ver_mis_viajes (request);
			} else if (action != null && action.equals("seguir")) {
				respuesta = this.seguir_viaje (request);
			} else if (action != null && action.equals("dejar_de_seguir")) {
				respuesta = this.dejar_de_seguir (request);
			} else if (action != null && action.equals("informar_comision")) {
				respuesta = this.informarComision (request);
			}
		} else if (entity != null && entity.equals ("vehiculo")) {
			if (action != null && action.equals ("new")) {
				respuesta = this.nuevo_vehiculo (request);
			} else if (action != null && action.equals ("ver_mis_vehiculos")) {
				respuesta = this.listar_vehiculos (request);
			} else if (action != null && action.equals ("ver_un_vehiculo")){
				respuesta = this.verUnVehiculo(request);
			} else if (action != null && action.equals ("modificar_vehiculo")){
				respuesta = this.modificarVehiculo(request);
			} else if (action != null && action.equals ("eliminar_vehiculo")){
				respuesta = this.eliminarVehiculo(request);
			} else if (action != null && action.equals ("modificar_imagen_vehiculo")){
				respuesta = this.modificarImagenVehiculo(request);
			} else if (action != null && action.equals ("asignar_vehiculo_clientes")){
				respuesta = this.asignarVehiculoCliente(request);
			} else if (action != null && action.equals ("desasignar_vehiculo_cliente")){
				respuesta = this.desasignarVehiculoCliente(request);
			}
		} else if (entity != null && entity.equals ("comentario")) {
			if (action != null && action.equals ("new")) {
				respuesta = this.nuevo_comentario (request);
			} 
		} else if (entity != null && entity.equals ("calificacion")) {
			if (action != null && action.equals ("new")) {
				respuesta = this.nueva_calificacion (request);
			} else if (action != null && action.equals ("show")) {
				respuesta = this.listar_calificaciones (request);
			} 
		} else {
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
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject respuesta = new JSONObject();
		PrintWriter writer = response.getWriter();
		String action = request.getParameter("action");
		String entity = request.getParameter("entity");

		/* TODO: comprobar permisos */	

		if (entity != null && entity.equals ("viaje")) {
			if (action != null && action.equals("detalle")) {
				respuesta = this.ver_viaje_detallado (request);
			}
		} else {
			respuesta = new JSONObject();
			respuesta.put ("result", false);
			respuesta.put ("msg", "No implementado");
		}
		respuesta.put("entity", entity);
		respuesta.put("action", action);

		System.out.println (respuesta);
		writer.println (respuesta);
	}
	
	//----------------------------------------------COMENTARIOS---------------------------------------------------------------------//

	@SuppressWarnings("unchecked")
	private JSONObject nuevo_comentario(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Viaje no válido");
			return respuesta;
		}
		
		JSONObject comentario = new JSONObject();
		comentario.put("id_cliente",AccessManager.getIdUsuario(request));
		comentario.put("id_viaje", idViaje);
		comentario.put("texto", request.getParameter("comentario"));
		
		try {
			daoViajes.dejarComentarioEnViaje(comentario);
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "Comentario se ha enviado correctamente");
		return respuesta;
	}
	//----------------------------------------------FIN-COMENTARIOS---------------------------------------------------------------------//

//----------------------------------------------CALIFICACIONES---------------------------------------------------------------------//

	@SuppressWarnings("unchecked")
	private JSONObject listar_calificaciones(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			respuesta.put("redirect", "/index.html");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Viaje no válido");
			respuesta.put("redirect", "/mis_viajes.html");
			return respuesta;
		}
		
		// Chequeo que viaje existe
		Viaje viaje = daoViajes.getViajeById(idViaje);
		if (viaje==null){
			respuesta.put("result", false);
			respuesta.put("msg", "No existe el viaje");
			respuesta.put("redirect", "/mis_viajes.html");
			return respuesta;
		}
		
		//Chequeo que cliente es conductor o pasajero finalizado
		Cliente cliente = (Cliente) daoViajes.buscarPorPrimaryKey(new Cliente(), AccessManager.getIdUsuario(request));
		boolean esConductor = viaje.getConductor().equals(cliente);
		boolean esPasajero = viaje.getPasajerosCalificablesComoListCliente().contains(cliente);
		if (!esConductor && !esPasajero){
			respuesta.put("result", false);
			respuesta.put("msg", "Usted no puede calificar en este viaje");
			respuesta.put("redirect", "/mis_viajes.html");
			return respuesta;
		}
		
		JSONArray postulantes = new JSONArray();
		Cliente conductor = viaje.getConductor();

		// Es pasajero o conductor?
		if (esPasajero){
			JSONObject json = new JSONObject();
			
			// DATOS DEL CONDUCTOR
			json.put("nombre_usuario", conductor.getNombre_usuario());
			json.put("foto", conductor.getFoto());
			PasajeroViaje pv = viaje.recuperar_pasajeroViaje_por_cliente(cliente);


			//Calificacion calificacion = (Calificacion) daoViajes.buscarPorClaveCandidataCompuesta(Calificacion.class.getSimpleName(), pv, conductor);
			Calificacion calificacion = daoPuntos.getCalificacionPorPasajeroConductor(pv, conductor);

			System.out.println("");
			System.out.println("es pasajero");
			System.out.println("");
			System.out.println("conductor dijo:"+calificacion.getComentario_conductor()+" pasajero dijo:"+calificacion.getComentario_pasajero());
			System.out.println("conductor recibio:"+calificacion.getCalificacion_para_conductor()+" pasajero recibio:"+calificacion.getCalificacion_para_pasajero());
			System.out.println("conductor recibio:"+calificacion.getParticipo_conductor()+" pasajero recibio:"+calificacion.getParticipo_pasajero());
			System.out.println("");

			if (calificacion == null){
				respuesta.put("result", false);
				respuesta.put("msg", "Error interno: la calificacion no se encuentra instanciada");
				return respuesta;
			}

			// CALIFICACION DADA
			if (calificacion.getCalificacion_para_conductor() == null){
				json.put("estado","1");// yo no califique al conductor
			}else{
				// califique al conductor, muestro data
				json.put("estado","2"); 
				json.put("participo", calificacion.getParticipo_conductor().toString());
				json.put("valoracion", calificacion.getCalificacion_para_conductor());
				json.put("comentario", calificacion.getComentario_pasajero());
			}
			// calificacion recibida
			Character participo_recibido = calificacion.getParticipo_pasajero();
			json.put("participo_recibido", (participo_recibido == null)? null : participo_recibido.toString());
			json.put("valoracion_recibida", calificacion.getCalificacion_para_pasajero());
			json.put("comentario_recibido", calificacion.getComentario_conductor());
			
			postulantes.add(json);
			
		}else if (esConductor){
			List<PasajeroViaje> listaPasajeros = viaje.getPasajerosCalificables();
			for (PasajeroViaje pv : listaPasajeros){
				JSONObject json = new JSONObject();
				
				// DATOS DEL Pasajero
				Cliente pasajeroCliente = pv.getCliente();
				json.put("nombre_usuario", pasajeroCliente.getNombre_usuario());
				json.put("foto", pasajeroCliente.getFoto());

				// OBTENGO CALIFICACION
				//Calificacion calificacion = (Calificacion) daoViajes.buscarPorClaveCandidataCompuesta(Calificacion.class.getSimpleName(), pv, conductor);
				Calificacion calificacion = daoPuntos.getCalificacionPorPasajeroConductor(pv, conductor);
				if (calificacion == null){
					calificacion = daoPuntos.crearCalificacion(pv, conductor);
				}
				System.out.println("");
				System.out.println("es conductor");
				System.out.println("");
				System.out.println("conductor dijo:"+calificacion.getComentario_conductor()+" pasajero dijo:"+calificacion.getComentario_pasajero());
				System.out.println("conductor recibio:"+calificacion.getCalificacion_para_conductor()+" pasajero recibio:"+calificacion.getCalificacion_para_pasajero());
				System.out.println("conductor recibio:"+calificacion.getParticipo_conductor()+" pasajero recibio:"+calificacion.getParticipo_pasajero());
				System.out.println("");
				


				// CALIFICACION DADA
				if (calificacion.getCalificacion_para_pasajero() == null){
					json.put("estado","1");// yo no califique al pasajero
				}else{
					// califique al pasajero, muestro data
					json.put("estado","2"); 
					json.put("participo", calificacion.getParticipo_pasajero().toString());
					json.put("valoracion", calificacion.getCalificacion_para_pasajero());
					json.put("comentario", calificacion.getComentario_conductor());
				}
				// calificacion recibida
				Character participo_recibido = calificacion.getParticipo_conductor();
				json.put("participo_recibido", (participo_recibido == null)? null : participo_recibido.toString());
				json.put("valoracion_recibida", calificacion.getCalificacion_para_conductor());
				json.put("comentario_recibido", calificacion.getComentario_pasajero());
				
				postulantes.add(json);
			}
		}
		
		
		
		/*
		LO QUE TENGO QUE DEVOLVER
		 postulantes = [{
				estado: "1", //1: sin_calificar, 2: calificado,
				nombre_usuario: "Carolo4",
				foto:"img/home/administracion_usuarios.png",
				participo: "",
				valoracion: "",
				comentario: "",
				participo_recibido: "",
				valoracion_recibida: "",
				comentario_recibido: ""
			},{
			...
			}]
		 */
		
		respuesta.put("result", true);
		respuesta.put("postulantes",postulantes);
		return respuesta;
	}

	@SuppressWarnings("unchecked")
	private JSONObject nueva_calificacion(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();

		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Viaje no válido");
			return respuesta;
		}
		int valoracion;
		try {
			valoracion = Integer.parseInt(request.getParameter("valoracion"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Valoración no es un valor válido");
			return respuesta;
		}
		JSONObject calificacion = new JSONObject();
		calificacion.put("id_viaje", idViaje);
		calificacion.put("nombre_usuario", AccessManager.nombreUsuario(request));
		calificacion.put("nombre_calificado", request.getParameter("calificado"));
		calificacion.put("confirmacion", request.getParameter("confirmacion").charAt(0));
		calificacion.put("valoracion", valoracion);
		calificacion.put("comentario", request.getParameter("comentario"));

		try {
			daoPuntos.calificar(calificacion);
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true); 
		respuesta.put("msg", "El usuario ha sido calificado con éxito");
		return respuesta;
	}
	
//----------------------------------------------FIN-CALIFICACIONES---------------------------------------------------------------------//

//----------------------------------------------VIAJES---------------------------------------------------------------------//
	
	@SuppressWarnings("unchecked")
	public JSONObject nuevo_viaje(HttpServletRequest request){
		int id_origen=-1, id_destino=-1, id_conductor=-1;
		JSONArray id_intermedios=null;
		int asientos_ida=-1, asientos_vuelta=-1;
		float precio_ida=-1f, precio_vuelta=-1f;
		String nombre_amigable=null, patente_vehiculo=null;
		Timestamp fecha_ida=null, fecha_vuelta=null;
		boolean ida_vuelta = false;
		ArrayList<String> err = new ArrayList<String>();

		JSONObject salida = new JSONObject();
		JSONObject params = new JSONObject();
		id_intermedios = new JSONArray();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			id_origen = Integer.parseInt(request.getParameter("origen"));
		} catch (Exception e) {
			err.add("Origen no es valido");
		} try {
			id_destino = Integer.parseInt(request.getParameter("destino"));
		} catch (Exception e) {
			err.add("Destino no es valido");
		} try {
			String[] locs = request.getParameterValues("intermedios[]");
			if (locs != null) {
				for (String loc: locs) {
					id_intermedios.add(Integer.parseInt(loc));
					System.out.println("PUNTO INTERMEDIO: ID"+loc);
				}
			}
		} catch (NumberFormatException e) {
			err.add("Punto intermedio no es valido");
		} try {
			nombre_amigable = request.getParameter("nombre_amigable");
		} catch (Exception e) {
			err.add("Nombre amigable");
		} try {
			precio_ida = Float.parseFloat(request.getParameter ("precio_ida"));
		} catch (Exception e) {
			err.add("Precio de viaje de ida no es valido");
		} try {
			fecha_ida = new Timestamp (format.parse(request.getParameter("fecha_ida")).getTime());
		} catch (Exception e) {
			err.add("Fecha de viaje de ida no es valida");
		} try {
			patente_vehiculo = request.getParameter("vehiculo_ida");
		} catch (Exception e) {
			err.add("Fecha de viaje de vuelta no es valida");
		} try {
			asientos_ida = Integer.parseInt(request.getParameter("asientos_ida"));
		} catch (Exception e) {
			err.add("Cantidad de asientos en viaje de ida no es valida");
		} try {
			ida_vuelta = request.getParameter("tipo_viaje").equals("ida_vuelta");
		} catch (Exception e) {
			err.add("Tipo de viaje no es valido");
		}

		if(ida_vuelta) {
			try {
				fecha_vuelta = new Timestamp (format.parse(request.getParameter("fecha_vuelta")).getTime());
			} catch (Exception e) {
				err.add("Fecha de vuelta no es valida");
			} try {
				asientos_vuelta = Integer.parseInt(request.getParameter("asientos_vuelta"));
			} catch (Exception e) {
				err.add("Cantidad de asientos en viaje de vuelta no es valida");
			} try {
				precio_vuelta = Float.parseFloat(request.getParameter("precio_vuelta"));
			} catch (Exception e) {
				err.add("Precio de viaje de vuelta no es valido");
			}
		}
			
		if(err.size() > 0) {
			salida.put("result", false);
			salida.put("msg", err);
			return salida;
		}

		params.put("cliente", AccessManager.getIdUsuario(request));
		params.put("vehiculo", patente_vehiculo);

		JSONObject localidades = new JSONObject();
		localidades.put("origen", id_origen);
		localidades.put("intermedios", id_intermedios);
		localidades.put("destino", id_destino);
		params.put("localidades", localidades);


		JSONObject viaje = new JSONObject();
		viaje.put("fecha_inicio", fecha_ida);
		viaje.put("cantidad_asientos", asientos_ida);
		viaje.put("nombre_amigable", nombre_amigable);
		viaje.put("precio", precio_ida);
		params.put("viaje", viaje);

		if(ida_vuelta) {
			JSONObject vuelta = new JSONObject ();
			vuelta.put("fecha_inicio", fecha_vuelta);
			vuelta.put("cantidad_asientos", asientos_vuelta);
			//vuelta.put("nombre_amigable", nombre_amigable+" (VUELTA)");
			vuelta.put("precio", precio_vuelta);
			params.put("vuelta", vuelta);
		}

		
		try {
			daoViajes.nuevoViaje(params);
		} catch (ExceptionViajesCompartidos e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			return salida;
		}
		salida.put("result", true);
		salida.put("msg", "Se ha creado el viaje");
		return salida;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject modificar_viaje(HttpServletRequest request) {
		int id_viaje=-1, id_origen=-1, id_destino=-1, id_conductor=-1;
		JSONArray id_intermedios=null;
		int asientos=-1;
		float precio=-1f;
		String nombre_amigable=null, patente_vehiculo=null;
		Timestamp fecha=null;

		JSONObject salida = new JSONObject();
		JSONObject params = new JSONObject();
		id_intermedios = new JSONArray();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			id_viaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Viaje no es válido");
			return salida;
		}try {
			id_origen = Integer.parseInt(request.getParameter("origen"));
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Origen no es válido");
			return salida;
		} try {
			id_destino = Integer.parseInt(request.getParameter("destino"));
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Destino no es válido");
			return salida;
		} try {
			String[] locs = request.getParameterValues("intermedios[]");
			if (locs != null) {
				for (String loc: locs) {
					id_intermedios.add(Integer.parseInt(loc));
					System.out.println("PUNTO INTERMEDIO: ID"+loc);
				}
			}
		} catch (NumberFormatException e) {
			salida.put("result", false);
			salida.put("msg", "Punto Intermedio no es válido");
			return salida;
		} try {
			nombre_amigable = request.getParameter("nombre_amigable");
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Nombre de viaje no es válido");
			return salida;
		} try {
			precio = Float.parseFloat(request.getParameter ("precio"));
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Precio no es válido");
			return salida;
		} try {
			fecha = new Timestamp (format.parse(request.getParameter("fecha")).getTime());
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "fecha no es válida");
			return salida;
		} try {
			patente_vehiculo = request.getParameter("vehiculo");
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Vehiculo no es válido");
			return salida;
		} try {
			asientos = Integer.parseInt(request.getParameter("asientos"));
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Cantidad de asientos en viaje de ida no es válida");
			return salida;
		} 

		params.put("cliente", AccessManager.getIdUsuario(request));
		params.put("vehiculo", patente_vehiculo);

		JSONObject localidades = new JSONObject();
		localidades.put("origen", id_origen);
		localidades.put("intermedios", id_intermedios);
		localidades.put("destino", id_destino);
		params.put("localidades", localidades);

		JSONObject viaje = new JSONObject();
		viaje.put("id_viaje", id_viaje);
		viaje.put("fecha_inicio", fecha);
		viaje.put("cantidad_asientos", asientos);
		viaje.put("nombre_amigable", nombre_amigable);
		viaje.put("precio", precio);
		params.put("viaje", viaje);
		
		try {
			daoViajes.modificarViaje(params);
		} catch (ExceptionViajesCompartidos e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			return salida;
		}
		salida.put("result", true);
		salida.put("msg", "Se ha modificado el viaje con éxito");
		return salida;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject cancelar_viaje(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();

		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("redirect", "/home.html");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("redirect", "/home.html");
			return respuesta;
		}
		
		//Chequeo que cliente es conductor
		Viaje viaje = daoViajes.getViajeById(idViaje);
		int idCliente = AccessManager.getIdUsuario(request);
		int idConductor = viaje.getConductor().getId_usuario();
		if (idCliente != idConductor){
			respuesta.put("result", false);
			respuesta.put("redirect", "/acceso_denegado.html");
			return respuesta;
		}
		
		try {
			daoViajes.cancelarViaje(idViaje, idCliente);
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "El viaje ha sido cancelado correctamente");
		return respuesta;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject ver_viaje_detallado(HttpServletRequest request) {
		int id_viaje;
		JSONObject salida = new JSONObject();
		Viaje viaje = null;
		Cliente usuario_logueado = null;

		try {
			usuario_logueado = (Cliente) daoUsuarios.buscarPorClaveCandidata ("Cliente", AccessManager.nombreUsuario(request));
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "No se ha iniciado sesion como un cliente valido");
			return salida;
		}
		
		try {
			id_viaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("redirect", "/mis_viajes.html");
			salida.put("msg", "id_viaje invalido");
			return salida;
		}
		//TODO agregado por lucas
		//llama al metodo de pablo
		this.daoViajes.actualizarEstadoViaje(id_viaje);
	
		
		daoViajes.actualizarEstadoViaje(id_viaje); //Por si no se inicio correctamente
		viaje = daoViajes.getViajeById(id_viaje);
		
		if (viaje == null) {
			salida.put("result", false);
			salida.put("redirect", "/mis_viajes.html");
			salida.put("msg", "Viaje no valido");
			return salida;
		}
		
		// Si se pide privacy conductor, solo el conductor puede ver viaje (para el modificar viaje)
		String privacy = request.getParameter("privacy");
		if (privacy != null && privacy.equals("conductor") && !viaje.getConductor().getNombre_usuario().equals(AccessManager.nombreUsuario(request))){
			salida.put("result", false);
			salida.put("redirect", "/mis_viajes.html");
			salida.put("msg", "Usted no es el conductor de este viaje");
			return salida;
		}
		
		salida.put("viaje", viaje.toJSON());
		JSONArray localidades = new JSONArray();
		for (Localidad loc: viaje.getLocalidadesComoListLocalidad()) {
			localidades.add (loc.toJSON());
		}
		salida.put("localidades", localidades);
		salida.put("vehiculo", viaje.getVehiculo().toJSON());
		
		Cliente conductor = viaje.getConductor();
		JSONObject json_conductor = new JSONObject();
		json_conductor.put("id", conductor.getId_usuario());
		json_conductor.put("nombre_usuario", conductor.getNombre_usuario());
		json_conductor.put("reputacion", conductor.getReputacion());
		json_conductor.put("mail", conductor.getEmail());
		json_conductor.put("telefono", conductor.getPersona().getTelefono());
		json_conductor.put("foto", conductor.getFoto());
		json_conductor.put("foto_registro", conductor.getFoto_registro());
		salida.put("conductor", json_conductor);
		
		// Comentarios
		List<ComentarioViaje> comentarios = null;
		try {
			comentarios = daoViajes.getComentariosViaje(viaje.getId_viaje());
		} catch (ExceptionViajesCompartidos e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			return salida;
		}
		if (comentarios==null){
			salida.put("result", false);
			salida.put("msg", "Error interno en la carga de comentarios");
			return salida;
		}
		JSONArray arregloComentarios = new JSONArray();
		for (ComentarioViaje cv : comentarios){
			arregloComentarios.add(cv.toJSON());
		}
		salida.put("comentarios", arregloComentarios);

		
		JSONObject json_logged = new JSONObject();
		boolean esConductor = (usuario_logueado.getId_usuario() == viaje.getConductor().getId_usuario());
		boolean esAceptado = viaje.getPasajerosAceptadosComoListCliente().contains(usuario_logueado);
		boolean esRechazado = viaje.getPasajerosRechazadosComoListCliente().contains(usuario_logueado);
		boolean esPostulado = viaje.getPasajerosPostuladosComoListCliente().contains(usuario_logueado);
		boolean esFinalizo = viaje.getPasajerosFinalizadosComoListCliente().contains(usuario_logueado);
		json_logged.put("es_conductor", esConductor);
		json_logged.put("es_postulado", esPostulado);
		json_logged.put("es_aceptado", esAceptado);
		json_logged.put("es_rechazado", esRechazado);
		json_logged.put("es_finalizo", esFinalizo);
		json_logged.put("es_seguidor", daoViajes.esSeguidor(usuario_logueado.getId_usuario(), viaje.getId_viaje())); 
		
		// Ha calificado a todos?
		boolean haCalificado = false;
		if (esConductor){
			List<PasajeroViaje> pasajeros = viaje.getPasajerosCalificables();
			System.out.println("");
			System.out.println("fuera del for: "+haCalificado);
			System.out.println("");
			for (PasajeroViaje pv : pasajeros){
				System.out.println("");
				System.out.println("dentro del for: "+haCalificado);
				System.out.println("");
				Calificacion c = daoPuntos.getCalificacionPorPasajeroConductor(pv, conductor);
				haCalificado = c!=null && c.getCalificacion_para_pasajero()!=null;
				if (!haCalificado) break;
			}
		}else if (esFinalizo){
			PasajeroViaje pv = viaje.recuperar_pasajeroViaje_por_cliente(usuario_logueado);
			if (haCalificado = pv!=null){
				Calificacion c = daoPuntos.getCalificacionPorPasajeroConductor(pv, conductor);
				haCalificado = c!=null && c.getCalificacion_para_conductor()!=null;
			}
		}
		json_logged.put("ha_calificado", haCalificado); 
		salida.put("usuario_logueado", json_logged);
		salida.put("result", true);
		
		// Calcular cuanto falta para que inicie el viaje
		if (viaje.getEstado() == EstadoViaje.no_iniciado) {
			long restante = (viaje.getFecha_inicio().getTime()) - (new java.util.Date().getTime());
			salida.put("recargar_en", restante+3000);
		}
		return salida;
	}

	@SuppressWarnings("unchecked")
	private JSONObject participar_viaje(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		JSONObject postulacion = new JSONObject();
		
		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		postulacion.put("cliente",AccessManager.getIdUsuario(request));
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Id del viaje no es válido");
			return respuesta;
		}
		postulacion.put("viaje",idViaje);

		//Chequeo que origen y destino sean validos
		int origen,destino;
		try {
			origen = Integer.parseInt(request.getParameter("origen"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Localidad de subida no es válida");
			return respuesta;
		}
		try {
			destino = Integer.parseInt(request.getParameter("destino"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Localidad de bajada no es válida");
			return respuesta;
		}
		postulacion.put("localidad_subida", origen);
		postulacion.put("localidad_bajada", destino);

		try {
			int asientos_ocupar = Integer.parseInt(request.getParameter("asientos"));
			postulacion.put("nro_asientos", asientos_ocupar);
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Cantidad de asientos a ocupar no es válida");
			return respuesta;
		}

		//invoco al gran Lucas
		try {
			daoViajes.Cliente_se_postula_en_viaje(postulacion);
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		// Si todo salio bien le doy al cliente la data del conductor para que se contacte
		Viaje v = daoViajes.getViajeById(idViaje);
		Cliente c = v.getConductor();
		Persona p = c.getPersona();
		JSONObject conductor = new JSONObject();
		conductor.put("persona", p.toJSON());
		conductor.put("cliente", c.toJSON());
		
		// Respondo que todo salio bien y le doy el conductor
		respuesta.put("result", true);
		respuesta.put("msg", "Usted se ha postulado correctamente");
		respuesta.put("conductor", conductor);

		return respuesta;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject cancelar_participacion(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();

		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Id del viaje no es válido");
			return respuesta;
		}

		try {
			daoViajes.cancelarParticipacionEnViaje(idViaje, AccessManager.getIdUsuario(request));
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "Usted ha dejado de participar en este viaje");
		
		return respuesta;
	}

	private JSONObject informarComision(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();

		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("redirect", "/home.html");
			return respuesta;
		}
		ArrayList<Integer> localidades=new ArrayList<>();
		try {
			Integer origen = Integer.parseInt(request.getParameter("origen"));
			localidades.add(origen);
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Origen no válido");
			return respuesta;
		} try {
			String[] locs = request.getParameterValues("intermedios[]");
			if (locs != null) {
				for (String loc : locs) {
					localidades.add(Integer.parseInt(loc));
				}
			}
		} catch (NumberFormatException e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Punto intermédio no es válido");
			return respuesta;
		} try {
			Integer destino = Integer.parseInt(request.getParameter("destino"));
			localidades.add(destino);
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Destino no válido");
			return respuesta;
		} 
		
		JSONArray comisionRecorrido = daoViajes.comisionPorRecorrido(localidades);
		if (comisionRecorrido==null){
			respuesta.put("result", false);
			respuesta.put("msg", "Error al cargar valor de comisiones");
			return respuesta;
		}
		respuesta.put("result", true);
		respuesta.put("comisiones", comisionRecorrido);
		respuesta.put("msg", "Las comisiones se han cargado correctamente");
		return respuesta;
	}

	@SuppressWarnings("unchecked")
	private JSONObject ver_postulantes(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();

		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("redirect", "/home.html");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("redirect", "/home.html");
			return respuesta;
		}
		
		//Chequeo que cliente es conductor
		Viaje viaje = daoViajes.getViajeById(idViaje);
		String cliente = AccessManager.nombreUsuario(request);
		String conductor = viaje.getConductor().getNombre_usuario();
		if (!cliente.equals(conductor)){
			AccessManager.nombreUsuario(request);
			respuesta.put("result", false);
			respuesta.put("redirect", "/acceso_denegado.html");
			return respuesta;
		}
		
		// Tomo postulantes
		//List<PasajeroViaje> listaPasajeros = daoViajes.listarPasajerosPorViaje(idViaje);
		List<PasajeroViaje> listaPasajeros = daoViajes.getPasajerosOrdenadosPorReputacion(idViaje);
		
		//En el arreglo que devuelvo cargo json con la data que precisa la web
		JSONArray postulantes = new JSONArray();
		for (PasajeroViaje pv : listaPasajeros){
			postulantes.add(cargarPostulante(pv));
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "Postulantes cargados");
		respuesta.put("postulantes", postulantes);
		
		return respuesta;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject cargarPostulante(PasajeroViaje pv) {
		JSONObject postulante = new JSONObject();
		Cliente c = pv.getCliente();
		Persona p = c.getPersona();
		
		postulante.put("estado_postulacion", pv.getEstado().toString());
		postulante.put("origen", pv.getLocalidad_subida().getLocalidad().getNombre());
		postulante.put("destino", pv.getLocalidad_bajada().getLocalidad().getNombre());
		postulante.put("asientos_solicitados", pv.getNro_asientos().toString());
		postulante.put("nombre_usuario", c.getNombre_usuario());
		postulante.put("foto", c.getFoto());
		postulante.put("reputacion", c.getReputacion());
		postulante.put("apellido", p.getApellidos());
		postulante.put("nombres", p.getNombres());
		postulante.put("telefono", p.getTelefono());
		postulante.put("mail", c.getEmail());
		postulante.put("comision_tramo", String.valueOf(pv.getComision().getMonto()));
		
		System.out.println("");
		System.out.println(pv.getComision().getMonto());
		System.out.println("");

		return postulante;
	}

	@SuppressWarnings("unchecked")
	public JSONObject aceptar_rechazar_postulante(HttpServletRequest request){
		JSONObject respuesta = new JSONObject();
		
		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Id del viaje no es válido");
			return respuesta;
		}
		
		// Chequeo que cliente al cual se acepta o rechaza es cliente valido
		String nombre_postulante = request.getParameter("nombre_postulante");
		Cliente postulante = null;
		try {
			postulante = (Cliente) daoUsuarios.buscarPorClaveCandidata ("Cliente", nombre_postulante);
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Postulante no es un cliente válido");
			return respuesta;
		}	
		
		// Se acepta o no al postulante?
		int decision;
		try {
			decision = Integer.parseInt(request.getParameter("decision"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "El postulante solo puede ser aceptado o rechazado");
			return respuesta;
		}
		try {
			if(decision==1){
				daoViajes.aceptarPasajero(postulante.getId_usuario(),idViaje);
			}else if(decision==2){
					daoViajes.rechazarPasajero(postulante.getId_usuario(),idViaje);
			}else{
				respuesta.put("result", false);
				respuesta.put("msg", "El postulante solo puede ser aceptado o rechazado");
				return respuesta;
			}
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "Operación exitosa");
		return respuesta;
	}

	@SuppressWarnings("unchecked")
	private JSONObject finalizarViaje(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();

		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Viaje no válido");
			return respuesta;
		}
		
		//Viaje viaje = daoViajes.getViajeById(idViaje);
		//String cliente = AccessManager.nombreUsuario(request);
		/*
		String conductor = viaje.getConductor().getNombre_usuario();
		if (!cliente.equals(conductor)){
			AccessManager.nombreUsuario(request);
			respuesta.put("result", false);
			respuesta.put("msg", "Usted no tiene permitido realizar esta acción");
			return respuesta;
		}
		*/
		try {
			daoViajes.finalizarViaje(AccessManager.getIdUsuario(request), idViaje);
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "Viaje finalizado con éxito");
		return respuesta;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject ver_mis_viajes (HttpServletRequest request) {
		JSONObject salida = new JSONObject();
		try {
			JSONArray json_viajes = new JSONArray();
			int id_usuario = AccessManager.getIdUsuario(request);
			
			if (id_usuario < 0) {
				salida.put ("result", false);
				salida.put ("msg", "Nombre de usuario no valido");
				return salida;
			}

			List<Viaje> viajes = daoViajes.listarViajesPorCliente(id_usuario);
		
			for (Viaje viaje: viajes) {
				JSONObject jtmp = new JSONObject();
				jtmp.put("id", viaje.getId_viaje());
				jtmp.put("origen", viaje.getOrigen().getNombre());
				jtmp.put("destino", viaje.getDestino().getNombre());
				jtmp.put("fecha_inicio", (viaje.getFecha_inicio().toString()));
				jtmp.put("conductor", viaje.getConductor().getNombre_usuario());
				jtmp.put("reputacion", viaje.getConductor().getReputacion());
				jtmp.put("precio", viaje.getPrecio());
				jtmp.put("foto", viaje.getConductor().getFoto());
				jtmp.put("nombre_amigable", viaje.getNombre_amigable());
				jtmp.put("recorrido", viaje.getLocalidadesViajeJson());

				json_viajes.add(jtmp);
			}
			salida.put("viajes", json_viajes);
			salida.put("result", true);

		/* SEGUIR */
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Error interno del servidor ");
			e.printStackTrace();
			return salida;
		}

		return salida;
	}

	@SuppressWarnings("unchecked")
	public JSONObject buscar_viaje (HttpServletRequest request) {
		JSONObject salida = new JSONObject();
		List<Viaje> resultados;
		JSONObject busqueda = new JSONObject();
		Timestamp fecha_desde=null, fecha_hasta=null;
		int origen, destino;
		String estado=null, conductor=null;
		SimpleDateFormat format = new SimpleDateFormat ("yyy-MM-dd");
		try {
			try {
				origen = Integer.parseInt(request.getParameter("origen"));
			} catch (Exception e) {
				throw new ExceptionViajesCompartidos ("Origen no valido");
			} try {
				destino = Integer.parseInt(request.getParameter("destino"));
			} catch (Exception e) {
				throw new ExceptionViajesCompartidos ("Destino no valido");
			} try {
				fecha_desde = new Timestamp ( (format.parse(request.getParameter("fecha_desde"))).getTime() );
			} catch (Exception e) {
				throw new ExceptionViajesCompartidos ("Fecha no valida");
			} try {
				//Le sumamos un dia a la fecha para que se incluyan los resultados del ultimo dia
				fecha_hasta = new Timestamp ( (format.parse(request.getParameter("fecha_hasta"))).getTime()+(24l*60l*60l*1000l) );
			} catch (Exception e) {
				// No hacer nada, el campo fecha_hasta no es obligatorio
			} try {
				estado = request.getParameter("estado");
			} catch (Exception e) {
				estado = "ambas";
			} try {
				conductor = request.getParameter("conductor");
			} catch (Exception e) {
				// No hacer nada, el campo conductor no es obligatorio
			}
			busqueda.put("origen", origen);
			busqueda.put("destino", destino);
			busqueda.put("fecha_desde", fecha_desde);
			if (fecha_hasta != null) {
				busqueda.put("fecha_hasta", fecha_hasta);
			}
			if (estado != null) {
				busqueda.put("estado", estado);
			}
			if (conductor != null) {
				busqueda.put("conductor", conductor);
			}
			
			resultados = daoViajes.buscarViajes(busqueda);
			
			JSONArray json_viajes = new JSONArray();
			for (Viaje viaje: resultados) {
				JSONObject json_viaje = new JSONObject();
				json_viaje.put("id", viaje.getId_viaje());
				json_viaje.put("origen", viaje.getOrigen().getNombre());
				json_viaje.put("destino", viaje.getDestino().getNombre());
				json_viaje.put("fecha_inicio", (viaje.getFecha_inicio() != null)? viaje.getFecha_inicio().toString(): null);
				json_viaje.put("precio", viaje.getPrecio());
				json_viaje.put("estado", viaje.getEstado().toString());
				json_viaje.put("conductor", viaje.getConductor().getNombre_usuario());
				json_viaje.put("reputacion", viaje.getConductor().getReputacion());
				json_viaje.put("foto", viaje.getConductor().getFoto());
				json_viaje.put("nombre_amigable", viaje.getNombre_amigable());
				json_viaje.put("recorrido", viaje.getLocalidadesViajeJson());
				json_viajes.add (json_viaje);
			}
			salida.put("viajes", json_viajes);
		} catch (ExceptionViajesCompartidos e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
			return salida;
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Error interno del servidor");
			e.printStackTrace();
			return salida;
		}
		
		salida.put("result", true);
		return salida;
	}


	@SuppressWarnings("unchecked")
	private JSONObject dejar_de_seguir(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Id del viaje no es válido");
			return respuesta;
		}
		
		try {
			daoViajes.dejarDeSeguirViaje(idViaje,AccessManager.getIdUsuario(request));
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "Usted ha dejado de seguir este viaje");
		return respuesta;
	}

	@SuppressWarnings("unchecked")
	private JSONObject seguir_viaje(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		//Chequeo que id del viaje es valido
		int idViaje;
		try {
			idViaje = Integer.parseInt(request.getParameter("id_viaje"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Id del viaje no es válido");
			return respuesta;
		}
		
		try {
			daoViajes.seguirViaje(idViaje,AccessManager.getIdUsuario(request));
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "Usted se ha convertido en seguidor de este viaje");
		return respuesta;
	}
	//----------------------------------------------FIN VIAJES---------------------------------------------------------------------//
	
	//----------------------------------------------VEHICULOS----------------------------------------------------------------------//
	
	@SuppressWarnings({ "unchecked", "unchecked" })
	public JSONObject nuevo_vehiculo (HttpServletRequest request) {
		JSONObject salida = new JSONObject();
		JSONObject params = new JSONObject();
		int id_usuario = AccessManager.getIdUsuario(request);
		JSONObject json_vehiculo = new JSONObject ();

		json_vehiculo.put("anio", Integer.parseInt(request.getParameter("anio")));
		json_vehiculo.put("marca", request.getParameter("marca"));
		json_vehiculo.put("modelo", request.getParameter("modelo"));
		json_vehiculo.put("patente", request.getParameter("patente"));
		json_vehiculo.put("aire", request.getParameter("aire").toString().charAt(0));
		json_vehiculo.put("color", request.getParameter("color"));
		json_vehiculo.put("asientos", Integer.parseInt(request.getParameter("asientos")));
		json_vehiculo.put("seguro", request.getParameter("seguro").toString().charAt(0));

		params.put("conductor", id_usuario);
		params.put("vehiculo", json_vehiculo);

		try{

			//Subir imagen
			String foto_param = request.getParameter("foto");
			String foto_url = null;
			if(foto_param != null) {
				foto_url = FileManager.uploadImage(request, foto_param);
			}

			if (foto_url != null){
				json_vehiculo.put("foto", foto_url);
			}

			daoViajes.NuevoVehiculo(params);

			salida.put("result", true);
			salida.put("msg", "Se ha creado el vehiculo correctamente");
		} catch (ExceptionViajesCompartidos e) {
			salida.put("result", false);
			salida.put("msg", e.getMessage());
		}
		return salida;
	}

	public List<Vehiculo> conseguir_vehiculos_cliente(Integer id_cliente){
		List<Vehiculo> lista;
		try {
			lista = this.daoViajes.getVehiculosPorCliente(id_cliente);
		} catch (ExceptionViajesCompartidos e) {
			// Si falla devuelve una lista vacia
			lista = new ArrayList<Vehiculo>();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public JSONObject listar_vehiculos(HttpServletRequest request) {
		JSONObject salida = new JSONObject ();
		JSONArray json_vehiculos = new JSONArray();
		JSONArray json_conductores = new JSONArray();
		HashSet<Cliente> conductores = new HashSet<Cliente>();

		try {
			int id_usuario = AccessManager.getIdUsuario(request);
			List <Vehiculo> lista = this.conseguir_vehiculos_cliente(id_usuario);
			JSONObject json_vehiculo;
			for (Vehiculo vehiculo: lista) {
				json_vehiculo = vehiculo.toJSON();
				for (Cliente conductor: vehiculo.getConductoresAsListCliente()) {
					conductores.add(conductor);
				} 
				json_vehiculos.add (json_vehiculo);
			}
			salida.put("vehiculos", json_vehiculos);
			
			JSONObject json_conductor;
			for (Cliente conductor: conductores) {
				json_conductor = new JSONObject();
				/* No se usa Cliente.toJSON para que no se envie la contrasenia */
				json_conductor.put("id", conductor.getId_usuario());
				json_conductor.put("nombre_usuario", conductor.getNombre_usuario());
				json_conductor.put("foto", conductor.getFoto());
				json_conductores.add(json_conductor);
			}
			salida.put("clientes", json_conductores);
		} catch (Exception e) {
			salida.put("result", false);
			salida.put("msg", "Error interno del servidor: ");
			e.printStackTrace();
			return salida;
		}

		salida.put("result", true);
		return salida;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject verUnVehiculo(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		// Chequeo que usuario es cliente
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("redirect", "/acceso_denegado.html");
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		//Chequeo que id del vehiculo es valido
		int idVehiculo;
		try {
			idVehiculo = Integer.parseInt(request.getParameter("id_vehiculo"));
		} catch (Exception e) {
			respuesta.put("result", false);
			respuesta.put("redirect", "/mis_vehiculos.html");
			respuesta.put("msg", "Id del vehiculo no es válido");
			return respuesta;
		}

		// chequeo que vehiculo existe
		Vehiculo v = (Vehiculo) daoViajes.buscarPorPrimaryKey(new Vehiculo(), idVehiculo);
		if (v == null){
			respuesta.put("result", false);
			respuesta.put("redirect", "/mis_vehiculos.html");
			respuesta.put("msg", "Vehiculo no se encuentra en el sistema");
			return respuesta;
		}
		
		// Chequeo que vehiculo este activo
		if (!v.isActivo()){
			respuesta.put("result", false);
			respuesta.put("redirect", "/mis_vehiculos.html");
			respuesta.put("msg", "El vehículo no se encuentra activo");
			return respuesta;
		}
		
		Cliente logueado = (Cliente) daoViajes.buscarPorPrimaryKey(new Cliente(), AccessManager.getIdUsuario(request));
		Maneja m = daoViajes.getManejaActivoPorVehiculoConductor(v, logueado);
		if (m==null){
			respuesta.put("result", false);
			respuesta.put("redirect", "/mis_vehiculos.html");
			respuesta.put("msg", "Usted no tiene permitido manejar este vehículo");
			return respuesta;
		}
		
		//Devuelvo conductores pero solo aquellos que estan activos
		List<Cliente> listaConductores = daoViajes.listarConductoresPorVehiculo(v.getId());
		JSONArray conductores = new JSONArray();
		for (Cliente c: listaConductores){
			if (c.isActivo()){
				JSONObject conductor = new JSONObject();
				conductor.put("id", c.getId_usuario());
				conductor.put("nombre_usuario", c.getNombre_usuario());
				conductores.add(conductor);
			}
		}
		
		respuesta.put("result", true);
		respuesta.put("vehiculo", v.toJSON());
		respuesta.put("conductores",conductores);

		return respuesta;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject modificarVehiculo(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		int idVehiculo;
		try{
			idVehiculo = Integer.parseInt(request.getParameter("vehiculo[id]"));
		}catch(Exception e){
			respuesta.put("result", false);
			respuesta.put("msg", "Vehículo no es válido");
			return respuesta;
		}
		
		Vehiculo v = null;
		try{
			v = (Vehiculo) daoViajes.buscarPorPrimaryKey(new Vehiculo(), idVehiculo);
		}catch(Exception e){
			respuesta.put("result", false);
			respuesta.put("msg", "Vehículo no se encuentra en el sistema");
			return respuesta;
		}
		
		JSONObject json_vehiculo = new JSONObject ();
		
		json_vehiculo.put("id", idVehiculo);
		json_vehiculo.put("anio", Integer.parseInt(request.getParameter("vehiculo[anio]")));
		//json_vehiculo.put("marca", request.getParameter("vehiculo[marca]"));
		//json_vehiculo.put("modelo", request.getParameter("vehiculo[modelo]"));
		//json_vehiculo.put("patente", request.getParameter("vehiculo[patente]"));
		json_vehiculo.put("aire", request.getParameter("vehiculo[aire]").toString().charAt(0));
		json_vehiculo.put("color", request.getParameter("vehiculo[color]"));
		json_vehiculo.put("asientos", Integer.parseInt(request.getParameter("vehiculo[cantidad_asientos]")));
		json_vehiculo.put("seguro", request.getParameter("vehiculo[seguro]").toString().charAt(0));
		//json_vehiculo.put("foto", request.getParameter("vehiculo[foto]"));
		json_vehiculo.put("conductor_modifica", AccessManager.getIdUsuario(request));


		try {
			daoViajes.modificarVehiculo(json_vehiculo);
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", json_vehiculo.toJSONString());
		return respuesta;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject eliminarVehiculo(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("redirect", "/acceso_denegado.html");
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		int idVehiculo;
		try{
			idVehiculo = Integer.parseInt(request.getParameter("id"));
		}catch(Exception e){
			respuesta.put("result", false);
			respuesta.put("msg", "Vehículo no es válido");
			return respuesta;
		}
		
		Vehiculo v = null;
		try{
			v = (Vehiculo) daoViajes.buscarPorPrimaryKey(new Vehiculo(), idVehiculo);
		}catch(Exception e){
			respuesta.put("result", false);
			respuesta.put("msg", "Vehículo no se encuentra en el sistema");
			return respuesta;
		}
		
		if (!v.isActivo()){
			respuesta.put("result", false);
			respuesta.put("msg", "El vehículo no se encuentra activo");
			return respuesta;
		}
		//Verifico que vehiculo no tenga viajes pendientes
		if (daoViajes.vehiculoTieneViajes(v)){
			respuesta.put("result", false);
			respuesta.put("msg", "Vehiculo tiene viajes pendientes. Si aún asi desea continuar con la operación cancele los viajes asociados y vuelva a intentarlo.");
			return respuesta;
		}
		//
		// Desactivo vehiculo pero tambien todas las relaciones de Maneja.
		//
		try {
			if (!daoViajes.desactivarVehiculo(v,AccessManager.getIdUsuario(request))){
				respuesta.put("result", false);
				respuesta.put("msg", "Error al desactivar vehículo");
				return respuesta;
				
			}
			if (!daoViajes.desactivarManeja(v)){
				respuesta.put("result", false);
				respuesta.put("msg", "Se desactivó vehiculo pero hubo un error en la desactivacion del vehiculo a todos los conductores del mismo");
				return respuesta;
			}
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("redirect", "/mis_vehiculos.html");
		respuesta.put("msg", "Vehículo desactivado correctamente");
		return respuesta;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject asignarVehiculoCliente(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("redirect", "/acceso_denegado.html");
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		int idVehiculo;
		try{
			idVehiculo = Integer.parseInt(request.getParameter("id"));
		}catch(Exception e){
			respuesta.put("result", false);
			respuesta.put("msg", "Vehículo no es válido");
			return respuesta;
		}
		/*
		ArrayList<Cliente> listaConductores = new ArrayList<>();
		try {
			String[] conductores = request.getParameterValues("conductores[]");
			if (conductores != null) {
				for (String c: conductores) {
					Cliente cliente = (Cliente) daoViajes.buscarPorPrimaryKey(new Cliente(), Integer.parseInt(c));
					if (c==null){
						respuesta.put("result", false);
						respuesta.put("msg", "Conductor no existe en el sistema");
						return respuesta;
					}
					listaConductores.add(cliente);
				}
			}
		} catch (NumberFormatException e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Ha ingresado un conductor no válido");
			return respuesta;
		} 
		*/
		try {
			//llamo al metodo nuevo 
			daoViajes.asignarConductoresVehiculo2(idVehiculo,AccessManager.getIdUsuario(request),request.getParameterValues("conductores[]"));
			//daoViajes.asignarConductoresVehiculo(idVehiculo,listaConductores);
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "Conductores fueron asignados con éxito");
		return respuesta;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject desasignarVehiculoCliente(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		if (!this.usuarioEsClienteValido(request)){
			respuesta.put("result", false);
			respuesta.put("redirect", "/acceso_denegado.html");
			respuesta.put("msg", "No se ha iniciado sesion como un cliente válido");
			return respuesta;
		}
		
		int idVehiculo;
		try{
			idVehiculo = Integer.parseInt(request.getParameter("id_vehiculo"));
		}catch(Exception e){
			respuesta.put("result", false);
			respuesta.put("msg", "Vehículo no es válido");
			return respuesta;
		}
		
		int idConductor;
		try{
			idConductor = Integer.parseInt(request.getParameter("id_conductor"));
		}catch(Exception e){
			respuesta.put("result", false);
			respuesta.put("msg", "Conductor no es válido");
			return respuesta;
		}
		
		try {
			daoViajes.desasignarConductor(idVehiculo,idConductor,AccessManager.getIdUsuario(request));
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", e.getMessage());
			return respuesta;
		}
		
		respuesta.put("result", true);
		if (idConductor == AccessManager.getIdUsuario(request)){
			respuesta.put("redirect", "/mis_vehiculos.html");
		}
		respuesta.put("msg", "El conductor ha sido desasignado correctamente");
		return respuesta;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject modificarImagenVehiculo(HttpServletRequest request) {
		JSONObject respuesta = new JSONObject();
		
		// Chequeo que vehiculo tiene id valida
		int idVehiculo;
		try{
			idVehiculo = Integer.parseInt(request.getParameter("id_vehiculo"));
		}catch (Exception e){
			respuesta.put("result", false);
			respuesta.put("msg", "Vehiculo no es válido");
			return respuesta;
		}
		
		// Chequeo que vehiculo existe
		Vehiculo v = null;
		try{
			v = (Vehiculo) daoViajes.buscarPorPrimaryKey(new Vehiculo(), idVehiculo);
		}catch(Exception e){
			respuesta.put("result", false);
			respuesta.put("msg", "Vehiculo no se encuentra en el sistema");
			return respuesta;
		}
		
		//guardo imagen anterior
		String fotoAnterior = v.getFoto();
		
		//Subo imagen
		String pathFotoSubida = FileManager.uploadImage(request,request.getParameter("foto"));
		if (pathFotoSubida.isEmpty() || pathFotoSubida == null){
			respuesta.put("result", false);
			respuesta.put("msg", "Error al subir imagen al servidor");
			return respuesta;
		}
		
		//cargo objeto a mandar al dao
		JSONObject foto = new JSONObject();
		foto.put("vehiculo", idVehiculo);
		foto.put("imagen", pathFotoSubida);
		foto.put("conductor_modifica", AccessManager.getIdUsuario(request));

		try {
			// si sube imagen correctamente elimino imagen anterior
			if (daoViajes.subirFotoVehiculo(foto)){
				if (fotoAnterior != null && !fotoAnterior.isEmpty()){
					FileManager.deleteImage(request, fotoAnterior);
				}
			}
		} catch (ExceptionViajesCompartidos e) {
			respuesta.put("result", false);
			respuesta.put("msg", "Imagen fue subida al servidor pero hubo un error en el almacenamiento del path en la bd");
			return respuesta;
		}
		
		respuesta.put("result", true);
		respuesta.put("msg", "Imagen fue guardada con éxito");
		return respuesta;
	}
		
	
	//juan: FUCK!! NO HABIA VISTO ESTO!
	public JSONObject cliente_no_maneja_mas (HttpServletRequest request) {
		JSONObject salida = new JSONObject();
		//TODO
		//metodo para que un cliente no puede manejar mas un auto, esta chequeado en "testGetVehiculosPorCliente"
		//ponganle el nombre q quieran, soy malo para los nombres de los metodos jajajaja
		//this.daoViajes.clienteNoManejaVehiculo(id_cliente, id_vehiculo);
		return salida;
	}
	
		
	//---------------------------------------------------FIN VEHICULOS----------------------------------------------------------//
	
	//----------------------------------------------------OTRAS FUNCIONES-------------------------------------------------------//
	// Funcion que chequea que el usuario registrado es un cliente (solo los clientes tienen acceso a ciertas funcionalidades de viaje)
	private boolean usuarioEsClienteValido(HttpServletRequest request) {
		Cliente usuario_logueado = null;
		try {
			usuario_logueado = (Cliente) daoUsuarios.buscarPorClaveCandidata ("Cliente", AccessManager.nombreUsuario(request));
			return daoUsuarios.isUsuarioActivo(usuario_logueado.getNombre_usuario());
		} catch (Exception e) {
			return false;
		}		
	}
}

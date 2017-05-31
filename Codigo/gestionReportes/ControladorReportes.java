package gestionReportes;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.*;
import javax.servlet.http.*;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.j2ee.servlets.BaseHttpServlet;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import otros.*;

public class ControladorReportes extends HttpServlet {

	public void init() throws ServletException
	{

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("En doGet de Reportes");

		PrintWriter writer = response.getWriter();

		response.setContentType("application/json");
		if (!AccessManager.EstaLogueado(request)){
			this.printDeniedRedirect(writer,"/acceso_denegado.html");
		}else{
			this.printAccept(writer, request);
		}
	}
	
	private void printAccept(PrintWriter writer, HttpServletRequest request) {
		JSONObject resultado = new JSONObject();
		
		resultado.put("result", true);

		writer.println(resultado);
	}


	private void printDeniedRedirect (PrintWriter writer, String url) {
		JSONObject resultado;

		resultado = new JSONObject();
		resultado.put("result", false);
		resultado.put("redirect", url);

		writer.println(resultado);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("En doPost de Reportes!");
		PrintWriter writer = response.getWriter();
		JSONObject out = null;	

		String accion = request.getParameter("action");

		if (accion != null && accion.equals("cargar_data")){
			out = cargarData(request,response);
		} else if (accion != null && accion.equals("reporte_viajes")){
			out = reporteViajes(request,response);
		} else if (accion != null && accion.equals("reporte_comisiones")){
			out = reporteComisiones(request,response);
		} 
		
		if (out == null) {
			out = new JSONObject();
			out.put ("result", false);
		}
		
		out.put("action", accion);
		System.out.println("Lo que mando al js: "+out);
		writer.println (out);
	}

	private JSONObject reporteComisiones(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject respuesta = new JSONObject();
		
		if (!AccessManager.hasRol(request, "super_usuario")){
			respuesta.put("result", true); // le tengo que poner true para que no vaya al cartel de error de vc.peticionajax();
			respuesta.put("redirect", "/acceso_denegado.html");
			return respuesta;
		}
		
		ServletContext context = this.getServletConfig().getServletContext();
		String path = context.getRealPath("/reportes/reports/vc_reporte_comisiones.jrxml");

		//compilo reporte
		if (!this.compilarReporte(path)){
			respuesta.put("msg", "Error en compilacion de reporte de viajes");
			respuesta.put("result", false);
			return respuesta;
		}
		//creo archivo de reporte
		String reportFileName = context.getRealPath("/reportes/reports/vc_reporte_comisiones.jasper");
		if (!this.existeReporteCompilado(reportFileName)){
			respuesta.put("msg", "El reporte no se encuentra compilado");
			respuesta.put("result", false);
			return respuesta;
		}
		// tomo parametros de la web y relleno reporte con datos
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		String conductor = request.getParameter("data[conductor]");
		String estado = request.getParameter("data[estado]");
		Integer id_viaje_desde = parsearInteger(request.getParameter("data[id_viaje_desde]"));
		Integer id_viaje_hasta = parsearInteger(request.getParameter("data[id_viaje_hasta]"));
		String patente = (request.getParameter("data[id_comision_desde]"));
		Integer id_comision_hasta = parsearInteger(request.getParameter("data[id_comision_hasta]"));
		java.sql.Date fecha_desde = parsearFecha(request.getParameter("data[fecha_desde]"));
		java.sql.Date fecha_hasta = parsearFecha(request.getParameter("data[fecha_hasta]"));
		Double km_desde = parsearDouble(request.getParameter("data[km_desde]"));
		Double km_hasta = parsearDouble(request.getParameter("data[km_hasta]"));
		Integer asientos_desde = parsearInteger(request.getParameter("data[asientos_desde]"));
		Integer asientos_hasta = parsearInteger(request.getParameter("data[asientos_hasta]"));
		java.math.BigDecimal monto_desde = parsearBigDecimal(request.getParameter("data[monto_desde]"));
		java.math.BigDecimal monto_hasta = parsearBigDecimal(request.getParameter("data[monto_hasta]"));

		parameters.put("id_viaje_desde", id_viaje_desde); //cambiar a string
		parameters.put("id_viaje_hasta", id_viaje_hasta); //cambiar a string
		parameters.put("patente", patente);
		parameters.put("id_comision_hasta", id_comision_hasta); //desestimar
		parameters.put("fecha_desde", fecha_desde);
		parameters.put("fecha_hasta", fecha_hasta);
		parameters.put("conductor", conductor);
		parameters.put("estado", estado);
		parameters.put("monto_desde", monto_desde);
		parameters.put("monto_hasta", monto_hasta);
		parameters.put("km_desde", km_desde);
		parameters.put("km_hasta", km_hasta);
		parameters.put("asientos_desde", asientos_desde);
		parameters.put("asientos_hasta", asientos_hasta);

		JasperPrint jasperPrint = this.fillReporte(reportFileName, parameters);
		if (jasperPrint == null){
			respuesta.put("msg", "No se pudo rellenar el reporte con datos");
			respuesta.put("result", false);
			return respuesta;
		}

		String pathReal = context.getRealPath("/");
		String pathToPdf = FileManager.uploadPdf(pathReal, jasperPrint);
		if (pathToPdf == "" || pathToPdf == null){
			respuesta.put("msg", "Error al guardar pdf generado");
			respuesta.put("result", false);
			return respuesta;
		}

		respuesta.put("msg", "El reporte ha sido generado correctamente");
		respuesta.put("link", pathToPdf);
		respuesta.put("result", true);
		
		return respuesta;
	}

	private JSONObject cargarData(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject respuesta = new JSONObject();
		if (!AccessManager.hasRol(request, "super_usuario")){
			respuesta.put("result", true); // le tengo que poner true para que no vaya al cartel de error de vc.peticionajax();
			respuesta.put("redirect", "/acceso_denegado.html");
			return respuesta;
		}
		
		respuesta.put("result", true);
		return respuesta;
	}

	private JSONObject reporteViajes(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		JSONObject respuesta = new JSONObject();
		
		if (!AccessManager.hasRol(request, "super_usuario")){
			respuesta.put("result", true); // le tengo que poner true para que no vaya al cartel de error de vc.peticionajax();
			respuesta.put("redirect", "/acceso_denegado.html");
			return respuesta;
		}
		
		ServletContext context = this.getServletConfig().getServletContext();
		String path = context.getRealPath("/reportes/reports/vc_reporte_viajes.jrxml");

		//compilo reporte
		if (!this.compilarReporte(path)){
			respuesta.put("msg", "Error en compilacion de reporte de viajes");
			respuesta.put("result", false);
			return respuesta;
		}
		//creo archivo de reporte
		String reportFileName = context.getRealPath("/reportes/reports/vc_reporte_viajes.jasper");
		if (!this.existeReporteCompilado(reportFileName)){
			respuesta.put("msg", "El reporte no se encuentra compilado");
			respuesta.put("result", false);
			return respuesta;
		}
		// tomo parametros de la web y relleno reporte con datos
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		String conductor = request.getParameter("data[conductor]");
		String estado = request.getParameter("data[estado]");
		Integer id_desde = parsearInteger(request.getParameter("data[id_desde]"));
		Integer id_hasta = parsearInteger(request.getParameter("data[id_hasta]"));
		java.sql.Date fecha_desde = parsearFecha(request.getParameter("data[fecha_desde]"));
		java.sql.Date fecha_hasta = parsearFecha(request.getParameter("data[fecha_hasta]"));
		Double km_desde = parsearDouble(request.getParameter("data[km_desde]"));
		Double km_hasta = parsearDouble(request.getParameter("data[km_hasta]"));
		Float precio_desde = parsearFloat(request.getParameter("data[precio_desde]"));
		Float precio_hasta = parsearFloat(request.getParameter("data[precio_hasta]"));
		Integer pasajeros_desde = parsearInteger(request.getParameter("data[pasajeros_desde]"));
		Integer pasajeros_hasta = parsearInteger(request.getParameter("data[pasajeros_hasta]"));

		parameters.put("id_viaje_desde", id_desde);
		parameters.put("id_viaje_hasta", id_hasta);
		parameters.put("fecha_desde", fecha_desde);
		parameters.put("fecha_hasta", fecha_hasta);
		parameters.put("conductor", conductor);
		parameters.put("estado", estado);
		parameters.put("precio_desde", precio_desde);
		parameters.put("precio_hasta", precio_hasta);
		parameters.put("km_desde", km_desde);
		parameters.put("km_hasta", km_hasta);
		parameters.put("pasajeros_desde", pasajeros_desde);
		parameters.put("precio_hasta", pasajeros_hasta);
		
		JasperPrint jasperPrint = this.fillReporte(reportFileName, parameters);
		if (jasperPrint == null){
			respuesta.put("msg", "No se pudo rellenar el reporte con datos");
			respuesta.put("result", false);
			return respuesta;
		}

		String pathReal = context.getRealPath("/");
		String pathToPdf = FileManager.uploadPdf(pathReal, jasperPrint);
		if (pathToPdf == "" || pathToPdf == null){
			respuesta.put("msg", "Error al guardar pdf generado");
			respuesta.put("result", false);
			return respuesta;
		}
		respuesta.put("msg", "El reporte ha sido generado correctamente");
		respuesta.put("link", pathToPdf);
		respuesta.put("result", true);
		
		return respuesta;
	}

	private Integer parsearInteger(String valor) {
		try{
			return Integer.parseInt(valor);
		}catch(NumberFormatException e){
			return null;
		}
	}
	private BigDecimal parsearBigDecimal(String valor) {
		try{
			BigDecimal a = new BigDecimal(valor);
			return a;
		}catch(NumberFormatException e){
			return null;
		}
	}
	private Float parsearFloat(String valor) {
		try{
			return Float.parseFloat(valor);
		}catch(NumberFormatException e){
			return null;
		}
	}
	private Double parsearDouble(String valor) {
		try{
			return Double.parseDouble(valor);
		}catch(NumberFormatException e){
			return null;
		}
	}
	private java.sql.Date parsearFecha(String valor) {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date;
		try {
			date = formato.parse(valor);
		} catch (ParseException e) {
			return null;
		}
		return new java.sql.Date(date.getTime());  
	}
	private boolean existeReporteCompilado(String reportFileName) {
		File reportFile = new File(reportFileName);
		return reportFile.exists();
	}

	private JasperPrint fillReporte(String reportFileName, Map<String, Object> parameters) {

		JasperPrint jasperPrint;
		DAOReportes dao = DAOReportes.getInstance();
		Connection conexion = dao.getConnection();
		if (conexion==null){
			return null;
		}
		try {
			jasperPrint = JasperFillManager.fillReport(
				reportFileName, 
				parameters, 
				conexion
			);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		//dao.cerrarConexion(conexion);
		return jasperPrint;
	}

	private boolean compilarReporte(String path) {
		try {
			String string = JasperCompileManager.compileReportToFile(path);
		} catch (JRException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void destroy()
	{
	}
}

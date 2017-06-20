package otros;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

public class FileManager {
	
	private static String directorioRaiz = "/upload/";
	private static String directorioReportes = "/reportes/upload/";
	
	public static String uploadImage(String pathReal, String img){
		// si no existe directorio, lo creo
		createDirectory(pathReal,directorioRaiz);
		
		// Seteo imagen con nombre generado, formato de la imagen dada y el path relativo
		String imgName = generateFileName();
		String imgFormat = getImgFormat(img);
		String pathRelativo = directorioRaiz+imgName+imgFormat;
		//
		
		// Genero imagen en base a la uri dada y la subo 
		String imgBase64 = extractMetadata(img); // quitar metada de uri para convertir a binario 
		byte[] data = Base64.getDecoder().decode(imgBase64); // convierto la uri de String a bytes
		if (upload(data,pathReal+pathRelativo)){ // la subo al path completo
			return pathRelativo;
		} else{
			return "";
		}
	}
	
	//Mismo que arriba pero obtengo el path real del request, ya voy a mejorar esta redundancia
	public static String uploadImage(HttpServletRequest request, String img){
		
		String pathReal = getPathReal(request);
		
		// si no existe directorio, lo creo
		createDirectory(pathReal,directorioRaiz);
		
		// Seteo imagen con nombre generado, formato de la imagen dada y el path relativo
		String imgName = generateFileName();
		String imgFormat = getImgFormat(img);
		String pathRelativo = directorioRaiz+imgName+imgFormat;
		//
		
		// Genero imagen en base a la uri dada y la subo 
		String imgBase64 = extractMetadata(img); // quitar metada de uri para convertir a binario 
		byte[] data = Base64.getDecoder().decode(imgBase64); // convierto la uri de String a bytes
		if (upload(data,pathReal+pathRelativo)){ // la subo al path completo
			return pathRelativo;
		} else{
			return "";
		}
	}
	public static boolean deleteImage(HttpServletRequest request, String archivo) {
		File file = new File(getPathReal(request)+archivo);
		return file.delete();
	}
	
	private static String getPathReal(HttpServletRequest request) {
		return request.getRealPath("/");
	}

	public static String uploadPdf(String pathReal, JasperPrint jasperPrint){
		// si no existe directorio, lo creo
		createDirectory(pathReal,directorioReportes);
		
		// Seteo pdf con nombre generado, formato y el path relativo
		//String imgName = generateFileName();
                String imgName = (new Timestamp((new java.util.Date()).getTime())).toString();
                imgName = imgName.replace('.', '-');
                imgName = imgName.replace(' ', '_');
                imgName = imgName.replace(':', '-');
		String imgFormat = ".pdf";
		String pathRelativo = directorioReportes+imgName+imgFormat;
		if (generarPdf(jasperPrint,pathReal+pathRelativo)){
			return pathRelativo;
		}else{
			return "";
		}
	}

	private static boolean generarPdf(JasperPrint jasperPrint,
		String pathCompleto) {
		// Genero archivo donde se guardara el pdf
		File f = new File(pathCompleto);
		try {
			JasperExportManager.exportReportToPdfFile(jasperPrint,pathCompleto);
			return true;
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static boolean modifyImage(String realPath, String archivo) {
		File file = new File(realPath+archivo);
		return file.delete();
	}
	
	private static void createDirectory(String pathRealRaiz, String pathSave) {
		File directorio = new File(pathRealRaiz+pathSave);
		if (!directorio.exists()){
		    try{
		        directorio.mkdir();
		    } 
		    catch(SecurityException se){
				se.printStackTrace();
		    }
		}		
	}

	private static String extractMetadata(String base64String){
		return base64String.substring(base64String.indexOf(",")+1);
	}
	
	private static String getImgFormat(String base64String){
		/*
		int desde = base64String.indexOf("/")+1;
		int hasta = base64String.indexOf(";");
		return "."+base64String.substring(desde, hasta);
		*/
	    String pattern = "\\/([A-z]+)";
	    Pattern r = Pattern.compile(pattern);
	    Matcher m = r.matcher(base64String);
	    if (m.find()) {
	    	return "."+m.group(1);
	    } 
	    return "";
	    
	}
	
	public static String generateFileName(){
		// ACA EL AUTO GENERADO DEL NOMBRE  del ARCHIVO. 
		return UUID.randomUUID().toString();
	}
	
	public static boolean upload(byte[] data, String pathCompleto){
		try (OutputStream stream = new FileOutputStream(pathCompleto)) {
		    stream.write(data);
		    return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("No se pudo subir la imagen a "+pathCompleto);
			e.printStackTrace();
			return false;
		}
		
	}

}
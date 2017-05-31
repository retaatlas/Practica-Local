package otros;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import gestionViajes.controlador.TestViaje;

public class TestRunner {

	/*
	 * ESTA CLASE SE ENCARGA DE CORRER TODOS LOS TEST Y MOSTRAR LOS RESULTADOS POR PANTALLA
	 */
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(TestViaje.class);
		
	      for (Failure failure : result.getFailures()) {
	         System.out.println(failure.toString());
	      }
	      System.out.println(result.wasSuccessful());
	   
	}

}

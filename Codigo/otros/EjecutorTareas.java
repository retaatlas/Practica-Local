package otros;

import java.util.Timer;
import java.util.TimerTask;

/* Esta clase es Singleton.
 No se puede crear mediante constructor.
 Para obtener una instacia llamar al metodo de clase getInstance() */

public class EjecutorTareas {
	private static EjecutorTareas instance = null;
	private static Timer timer;

	private EjecutorTareas() {
		EjecutorTareas.timer = new Timer();
	}

	public static EjecutorTareas getInstance() {
		if (EjecutorTareas.instance == null) {
			EjecutorTareas.instance = new EjecutorTareas();
		} 

		return EjecutorTareas.instance;
	}

	public void programarTarea (TareaProgramable tarea) {
		timer.schedule(tarea, tarea.getTiempo());
	}

	public void ejecutar (TareaProgramable tarea) {
		tarea.ejecutar();
	}
}

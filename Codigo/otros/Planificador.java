package otros;

import java.util.List;
import java.util.Date;

// Los objetos de esta clase crearan instancias de TareaProgramable para enviarselas al planificador maestro
public abstract class Planificador implements Runnable {
	private static final long TIEMPO_PLANIFICACION = 300000; //5 minutos
	private boolean terminar = false;
	private EjecutorTareas ejecutor = null;
	private Thread thread = null;
	private Date proximaPlanificacion = null;

	public Planificador () {
		this.ejecutor = EjecutorTareas.getInstance();
	}

	@Override
	public void run () {
		List<TareaProgramable> atrasadas, proximas;
		long t_inicio, t_fin, transcurrido;

		atrasadas = this.tareasAtrasadas();
		for (TareaProgramable tarea: atrasadas) {
			ejecutor.ejecutar (tarea);
		}

		while (!this.terminar) {
			t_inicio = new Date().getTime();
			proximaPlanificacion = new Date(t_inicio + TIEMPO_PLANIFICACION);
			proximas = this.tareasProximas(TIEMPO_PLANIFICACION);
			for (TareaProgramable tarea: proximas) {
				this.ejecutor.programarTarea (tarea);
			}
			t_fin = new Date().getTime();
			transcurrido = t_fin - t_inicio;

			try {
				Thread.sleep(TIEMPO_PLANIFICACION -transcurrido);
			} catch (InterruptedException e) {
				this.terminar = true;
			}
		}
	}

	// Inicia el thread 
	public void iniciar () {
		if (this.thread == null || this.thread.getState() == Thread.State.TERMINATED) {
			this.thread = new Thread (this);
			this.thread.start();
		}
	}

	// Deja de planificar las tareas proximas
	public void detener () {
		this.terminar = true;
	}

	// Agrega una nueva tarea al planificador
	public void nuevaTarea(TareaProgramable tarea) {
		Date tiempo = tarea.getTiempo();

		if(tiempo == null || tiempo.compareTo(new Date ()) <= 0) {
			// Si la tarea no tiene tiempo asignado o bien el tiempo es en el pasado, ejecutarla inmediatamente
			this.ejecutor.ejecutar(tarea);
		} else if (proximaPlanificacion != null && tiempo.compareTo(proximaPlanificacion) <= 0){
			// Si la tarea deberia ejecutarse antes de la proxima corrida del planificador, programarla
			this.ejecutor.programarTarea(tarea);
		}
	}

	// tareasAtrasadas debe ser implementado por las subclases
	// Se ejecuta una sola vez antes de iniciar la planificacion de tareas proximas
	public abstract List<TareaProgramable> tareasAtrasadas();

	// tareasProximas debe ser implementado por las subclases
	// Se ejecuta cada cierto tiempo, debe devolver las tareas que se programaran para ejecutarse en los proximos milisecs millisegundos
	public abstract List<TareaProgramable> tareasProximas(long milisecs);


}

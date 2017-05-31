package gestionViajes.controlador;

import otros.TareaProgramable;
import gestionViajes.modelo.Viaje;
import gestionViajes.controlador.DAOViajes;

import java.util.TimerTask;

class TareaIniciarViaje extends TareaProgramable {
	private Viaje viaje;
	private DAOViajes dao;

	public TareaIniciarViaje (DAOViajes dao, Viaje viaje) {
		this.viaje = viaje;
		this.dao = dao;
		this.setTiempo(viaje.getFecha_inicio());
	}
	
	@Override
	public void ejecutar () {
		if (this.dao.actualizarEstadoViaje(this.viaje.getId_viaje())) {
			System.out.println("[PLANIFICADOR]: Se ha iniciado por temporizacion el viaje con id "+this.viaje.getId_viaje());
		}
	}
}

package gestionViajes.controlador;

import otros.TareaProgramable;
import gestionViajes.modelo.Viaje;
import gestionViajes.controlador.DAOViajes;

import java.util.TimerTask;

class TareaFinalizarViaje extends TareaProgramable {
	private Viaje viaje;
	private DAOViajes dao;

	public TareaFinalizarViaje (DAOViajes dao, Viaje viaje) {
		this.viaje = viaje;
		this.dao = dao;
		this.setTiempo(viaje.getFecha_autofinalizar());
	}
	
	@Override
	public void ejecutar () {
		if (this.dao.actualizarEstadoViaje(this.viaje.getId_viaje())) {
			System.out.println("[PLANIFICADOR]: Se ha finalizado por temporizacion el viaje con id "+this.viaje.getId_viaje());
		}
	}
}

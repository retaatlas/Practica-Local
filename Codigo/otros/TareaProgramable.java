package otros;

import java.util.TimerTask;
import java.util.Date;

public abstract class TareaProgramable extends TimerTask {
	protected Date tiempo = null;

	public void setTiempo(Date tiempo) {
		this.tiempo = tiempo;
	}

	public Date getTiempo() {
		return this.tiempo;
	}

	// ejecutar debe ser implementado en las subclases
	public abstract void ejecutar();

	// sobreescribe el metodo run de TimerTask
	@Override
	public void run() {
		this.ejecutar();
	}
}

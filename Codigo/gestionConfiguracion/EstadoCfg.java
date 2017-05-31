package gestionConfiguracion;

import org.json.simple.JSONObject;

/* Esta clase representa el estado del proceso de configuracion */

class EstadoCfg {
	public final static int NO_INICIADO=0;
	public final static int TRABAJANDO=1;
	public final static int COMPLETO=2;
	public final static int FALLO=-1;

	// estado: 0=no_iniciado, 1=trabajando, 2=completo, 3=fallo
	private int estado;

	// step: numero del ultimo paso exitoso
	private int step;

	public EstadoCfg () {
		this.estado = 0;
		this.step = 0;
	}

	public int getEstado() {
		return this.estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public int getStep() {
		return this.step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void stepUp() {
		this.step++;
	}

	public void stepDown() {
		this.step--;
	}

	public void reset() {
		this.estado = 0;
		this.step = 0;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
	
		String estadoStr;
		switch(this.estado) {
			case NO_INICIADO: estadoStr = "no_iniciado"; break;
			case TRABAJANDO: estadoStr = "trabajando"; break;
			case COMPLETO: estadoStr = "completo"; break;
			case FALLO: estadoStr = "fallo"; break;
			default: estadoStr = null;
		}
		json.put("estado", estadoStr);
		json.put("step", this.step);
		return json;
	}

}

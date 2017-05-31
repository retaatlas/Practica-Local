package otros;

public class ExceptionViajesCompartidos extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public ExceptionViajesCompartidos() { super(); }
	public ExceptionViajesCompartidos(String mensaje) { super(mensaje); }
	public ExceptionViajesCompartidos(String mensaje, Throwable causa) { super(mensaje, causa); }
	public ExceptionViajesCompartidos(Throwable causa) { super(causa); }
	
}

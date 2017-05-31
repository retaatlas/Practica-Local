package gestionUsuarios.controlador;

import gestionUsuarios.modelo.Cliente;
import gestionUsuarios.modelo.EstadoNotificacion;
import gestionUsuarios.modelo.Mail;
import gestionUsuarios.modelo.Notificacion;
import otros.DataAccesObject;
import otros.ExceptionViajesCompartidos;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.RollbackException;
import otros.ManejadorErrores;

public class DAONotificaciones extends DataAccesObject {

    private static DAONotificaciones instance = null;

    DAONotificaciones() {
        super();
    }

    public static DAONotificaciones getInstance() {
        if (DAONotificaciones.instance == null) {
            DAONotificaciones.instance = new DAONotificaciones();
        }
        return DAONotificaciones.instance;
    }

    public synchronized void marcarLeida(int idNotificacion) throws ExceptionViajesCompartidos {
        this.limpiarTransacciones(); 
        this.getInstance().iniciarTransaccion(); //probarlo
        Notificacion notificacion = (Notificacion) this.buscarPorPrimaryKey(new Notificacion(), idNotificacion);
        notificacion.setEstado(EstadoNotificacion.leido);
        try{
        this.entitymanager.getTransaction().commit();
        }catch(RollbackException e){
		  	String error= ManejadorErrores.parsearRollback(e);
		 	throw new ExceptionViajesCompartidos("ERROR: "+error);
		}
    }

    /* METODO REPETIDO
	public List<Notificacion> listarNoLeidas(int id_usuario) throws ExceptionViajesCompartidos {
		List<Notificacion> notificacionesNoLeidas=null;
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_usuario);
		if(cliente == null){
			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
		}
		List<Notificacion> notificacionesTotales = cliente.getNotificaciones();
		for (Integer i = 0; i < notificacionesTotales.size(); i++) {
			if (notificacionesTotales.get(i).getEstado().equals(EstadoNotificacion.no_leido)) {
				notificacionesNoLeidas.add(notificacionesTotales.get(i));
			}
		}
		return notificacionesNoLeidas;
	}
     */

    public List<Notificacion> getNotificaciones(Integer id_usuario) throws ExceptionViajesCompartidos {
        Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_usuario);
        if (cliente == null) {
            throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
        }
        Query qry = entitymanager.createNamedQuery("Notificacion.porUsuario");
        qry.setParameter("id_cliente", cliente);
        List<Notificacion> notificaciones = qry.getResultList();
        return notificaciones;
    }

    public List<Notificacion> getNotificacionesNoLeidas(Integer id_usuario) throws ExceptionViajesCompartidos {
        Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_usuario);
        if (cliente == null) {
            throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
        }
        Query qry = entitymanager.createNamedQuery("Notificacion.NoLeidasPorUsuario");
        qry.setParameter("id_cliente", cliente);
        List<Notificacion> notificaciones = qry.getResultList();
        return notificaciones;
    }

    public Integer getCantidadNotificacionesNoLeidas(Integer id_usuario) throws ExceptionViajesCompartidos {
        Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_usuario);
        if (cliente == null) {
            throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
        }
        Query qry = entitymanager.createNamedQuery("Notificacion.cantidadNoLeidaPorUsuario");
        qry.setParameter("id_cliente", cliente);
        Long cantidad = (Long) qry.getSingleResult();
        return cantidad.intValue();
    }

    public synchronized boolean setNotificacionesLeidas(Integer id_usuario, List<Integer> lista_id_notificaciones) throws ExceptionViajesCompartidos {
        this.limpiarTransacciones();
        Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_usuario);
        if (cliente == null) {
            throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO EXISTE");
        }
        this.getInstance().iniciarTransaccion(); //probarlo
        for (Integer i : lista_id_notificaciones) {
            Notificacion n = (Notificacion) this.buscarPorPrimaryKey(new Notificacion(), i);
            n.setEstado(EstadoNotificacion.leido);
        }
        try{
            
            this.entitymanager.getTransaction().commit();
        }catch(RollbackException e){
		  	String error= ManejadorErrores.parsearRollback(e);
		 	throw new ExceptionViajesCompartidos("ERROR: "+error);
		}
        return true;
    }

    public void enviarNotificacionPorMail(Notificacion notificacion) {

        Mail correo = new Mail();
        correo.setAsunto("Nueva Notificación - Viajes Compartidos");
        correo.setDestino(notificacion.getCliente().getEmail());
        correo.setMensaje(notificacion.getTexto()+"\n Usted puede ingresar al mismo desde el siguiente link:\n"+notificacion.getLink()+"\n Saludos cordiales, \n Staff Viajes Compartidos.");
        correo.setUsuarioCorreo("viajescompartidosvc@gmail.com");
        correo.setPassword("xvyjiqwlqxwkvxvu");
        

        ControladorMail cm = new ControladorMail();

        if (cm.enviarCorreo(correo)) {
            System.out.println("Enviado");
        } else {
            System.out.println("Error");
            System.err.println();
        }

    }

}

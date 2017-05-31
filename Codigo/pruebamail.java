
import gestionUsuarios.controlador.ControladorMail;
import gestionUsuarios.modelo.Cliente;
import gestionUsuarios.modelo.EstadoNotificacion;
import gestionUsuarios.modelo.Mail;
import gestionUsuarios.modelo.Notificacion;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fede
 */
public class pruebamail {
    public static void main(String argv[]){
        Notificacion not = new Notificacion();
        Cliente cli = new Cliente();
        cli.setEmail("reta.atlas@gmail.com");
        not.setEstado(EstadoNotificacion.no_leido);
        not.setTexto("prueba");
        not.setLink("http://localhost:8080/detalle_viaje.html?id=35");
        ControladorMail cm = new ControladorMail();
        Mail mail = new Mail();
        mail.setUsuarioCorreo("viajescompartidosvc@gmail.com");
        mail.setAsunto("asunto prueba");
        mail.setDestino("reta.atlas@gmail.com");
        mail.setMensaje("mensajedeprueba");
        cm.enviarCorreo(mail);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionUsuarios.controlador;

import gestionUsuarios.modelo.Mail;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Fede
 */
public class ControladorMail {
    Mail c = new Mail();
    
    public boolean enviarCorreo(Mail c){
      
       try {
           
           Properties p = new Properties();
           p.put("mail.smtp.host", "smtp.gmail.com");
           p.setProperty("mail.smtp.starttls.enable", "true");
           p.setProperty("mail.smtp.port", "587");
           p.setProperty("mail.smtp.user", "viajescompartidosvc@gmail.com");
           p.setProperty("mail.smtp.auth", "true");
           
           Session s = Session.getDefaultInstance(p, null);
           BodyPart texto =new MimeBodyPart();
           texto.setText(c.getMensaje());
           
           MimeMultipart m = new MimeMultipart();
           m.addBodyPart(texto);
           MimeMessage mensaje = new MimeMessage(s);
           mensaje.setFrom(new InternetAddress(c.getUsuarioCorreo()));
           mensaje.addRecipient(Message.RecipientType.TO,new InternetAddress(c.getDestino()));
           mensaje.setSubject(c.getAsunto());
           mensaje.setContent(m);
           
           Transport t = s.getTransport("smtp");
           t.connect("viajescompartidosvc@gmail.com","xvyjiqwlqxwkvxvu");
           t.sendMessage(mensaje, mensaje.getAllRecipients());
           t.close();
           return true;
           
           
       } catch (MessagingException ex) {
           Logger.getLogger(ControladorMail.class.getName()).log(Level.SEVERE, null, ex);
           System.out.println("error");
           System.err.println(ex.getMessage());
           return false;
       }
                
       
        
    }
}

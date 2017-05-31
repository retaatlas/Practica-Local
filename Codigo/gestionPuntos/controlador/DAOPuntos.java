package gestionPuntos.controlador;

import gestionPuntos.modelo.*;
import gestionUsuarios.modelo.*;
import gestionViajes.controlador.DAOViajes;
import gestionViajes.modelo.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import static java.time.Instant.now;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NamedQuery;

import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.NoResultException;

import org.json.simple.JSONObject;

import otros.DataAccesObject;
import otros.ExceptionViajesCompartidos;
import otros.ManejadorErrores;

public class DAOPuntos extends DataAccesObject {
	private static DAOPuntos instance = null;

	//vacio por ahora
	private float alfa = (float) 1.5;
	private float beta = (float) 2.5;
      
	private DAOPuntos(){
		super();
	}

	public static DAOPuntos getInstance() {
		if(DAOPuntos.instance == null) {
			DAOPuntos.instance = new DAOPuntos();
		}
		return DAOPuntos.instance;
		
	}
        
	public Calificacion getCalificacionPorPasajeroConductor(PasajeroViaje pv, Cliente c){
		try {
			Query q = this.entitymanager.createNamedQuery("Calificacion.ClaveCandidateCompuesta");
			Calificacion calif;
			q.setParameter("cc1", pv);
			q.setParameter("cc2", c);
			try {
			calif = (Calificacion) q.getSingleResult();
			} catch (NoResultException e) {
				calif = crearCalificacion (pv, c);
			}
			return calif;
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized Calificacion crearCalificacion(PasajeroViaje pv, Cliente c) {
		try {
			this.iniciarTransaccion();
			Calificacion calificacion = new Calificacion();
			calificacion.setCalificacion_para_conductor(null);
			calificacion.setCalificacion_para_pasajero(null);
			calificacion.setComentario_conductor(null);
			calificacion.setComentario_pasajero(null);
			calificacion.setParticipo_pasajero(null);
			calificacion.setParticipo_conductor(null);
			calificacion.setPasajero_viaje(this.entitymanager.merge(pv));
			calificacion.setConductor(this.entitymanager.merge(c));
			this.entitymanager.persist(calificacion);
			this.entitymanager.getTransaction().commit();
			return calificacion;
		} catch (Exception e) {
			this.limpiarTransacciones();
			return null;
		}
	}
	
    //byfede    
    public synchronized boolean evaluarSancion(Integer id_cliente, Integer id_viaje, Timestamp fechaYHoraCancelacion) throws ExceptionViajesCompartidos{
       
        //System.out.println("Entro a DAOPUNTOS Con:\n IdCli:"+id_cliente+"\n IdViaje:"+id_viaje+"\n Time:"+fechaYHoraCancelacion+"");
        double descuento = this.calculcarDescuentoPuntos(id_viaje, id_cliente);
        if(descuento!=0){ //sanciono si cancelo tarde 
            this.iniciarTransaccion( );
            MovimientoPuntos mov = new MovimientoPuntos();
            Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);         
            mov.setCliente(cliente);
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date fecha = new java.sql.Date(utilDate.getTime());
            mov.setFecha( new Timestamp((new java.util.Date()).getTime()) );		//modificado por lucas al cambiar la fecha en movimientoPuntos
            mov.setTipo_mov_puntos(this.getTipoMovimientoPuntos("sancion"));
            descuento = descuento - 2*(descuento);
            Integer descuento_int = (int)descuento;
            mov.setMonto(descuento_int);
            Sancion sancion = new Sancion();
            sancion.setCliente(cliente);
            sancion.setMovimiento_puntos(mov);
            utilDate = new java.util.Date();
            fecha = new java.sql.Date(utilDate.getTime());
            sancion.setFecha_inicio(fecha);
            sancion.setFecha_fin(fecha);            
            sancion.setEstado(EstadoSancion.caduca);//le pongo caduca porque es de puntos, no es por tiempo.
            TipoSancion tipo_sancion = new TipoSancion();
            //Query qry = entitymanager.createNamedQuery("TipoSancion.buscarPorClaveCandidata");
            //qry.setParameter("clave_candidata", "Descuento de Puntos por CancelaciÃ³n de viaje con Pasajeros");
            tipo_sancion= (TipoSancion) this.buscarPorPrimaryKey(new TipoSancion(),2);
            Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
            //FIN
            //notificacion
            sancion.setTipo_sancion(tipo_sancion);
            Notificacion notificacion= new Notificacion();
            notificacion.setCliente(cliente); 
            notificacion.setEstado(EstadoNotificacion.no_leido);
            notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) ); 
            notificacion.setTexto("Usted ha sido sancionado a causa de: "+tipo_sancion.getDescripcion()+".Correspondiente al viaje: "+viaje.getNombre_amigable());
            //fin notif
            
            //sancion de dias
            TipoSancion tipo_sancion_dias = (TipoSancion) this.buscarPorPrimaryKey(new TipoSancion(), 4);            
            Sancion sancion_dias = new Sancion();
            sancion_dias.setCliente(cliente);
            sancion_dias.setFecha_inicio(fecha);
            sancion_dias.setEstado(EstadoSancion.vigente);
            sancion_dias.setTipo_sancion(tipo_sancion_dias);
                //calculo los dias que lo voy a sancionar
                Timestamp actual= new Timestamp((new java.util.Date()).getTime());
                
                Timestamp salida_viaje = viaje.getFecha_inicio();
                double diferencia = diferenciaTimestamps(salida_viaje, fechaYHoraCancelacion);
                List<Sancion> sanciones_anteriores = buscarSanciones(cliente);
                int num_sanciones_anteriores = sanciones_anteriores.size();
                //formulo
                // dias = (1/diferencia)* SancionesAnteriores
                double dias = (1/diferencia)*(1+num_sanciones_anteriores);
                //pongo un tope a dias maximos de suspension
                int dias_int = 0;
                if(dias>50){
                    dias_int=30;
                }else{
                     dias_int = (int) dias;
                }                 

                Calendar Calendario = Calendar.getInstance();
                Calendario.setTimeInMillis(actual.getTime());
                Calendario.add(Calendar.DATE, dias_int);
                Timestamp fecha_fin_ts = new Timestamp(Calendario.getTimeInMillis());
                java.sql.Date fecha_fin = new java.sql.Date(fecha_fin_ts.getTime());
                sancion_dias.setFecha_fin((Date) fecha_fin);
                //fin calculo de dias
            //fin sancion dias            
            
            try{    this.entitymanager.persist(notificacion);
                    this.entitymanager.persist(mov);
                    this.entitymanager.getTransaction( ).commit( );
                    this.iniciarTransaccion();
                    this.entitymanager.persist(sancion);
                    this.entitymanager.getTransaction( ).commit( );
                    this.iniciarTransaccion();
                    this.entitymanager.persist(sancion_dias);
                    this.entitymanager.getTransaction( ).commit( );
                    boolean bandera = this.actualizarPuntosCliente(descuento_int, cliente.getId_usuario());
            }catch(RollbackException e){
                    String error= ManejadorErrores.parsearRollback(e);
                    throw new ExceptionViajesCompartidos("ERROR: "+error);
            }     

        }
       
        return true;
    }
    
    //byfede
    public double calculcarDescuentoPuntos(Integer id_viaje, Integer id_cliente){
        double puntos=0;
           Timestamp actual= new Timestamp((new java.util.Date()).getTime());
           DAOViajes daoviajes = DAOViajes.getInstance();           
           Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), id_viaje);
           Timestamp salida_viaje = viaje.getFecha_inicio();
           //Formula
           // puntos_a_restar = (SancionesAnteriores * Alfa) + ( (1/HorasQueFaltanHastaPartir) * Beta)
           Cliente cliente = new Cliente();
           cliente.setId_usuario(id_cliente);
            try {
                List <Sancion> sanciones =  this.buscarSanciones(cliente);          //busco cuantas tuvo
                Integer cant_sanciones_ant = sanciones.size();                      //saco el cardinal 
                double dif_horas = this.diferenciaTimestamps(salida_viaje, actual); //calculo diferencia de fechas
                
                if(dif_horas<=12){ //si cancelo con menos de 12 horas de anticipacion entro
                    float primer_termino_formula = (cant_sanciones_ant * alfa);
                    double segundo_termino_formula = ((1/dif_horas)*beta);
                    double resultado = primer_termino_formula + segundo_termino_formula;
                    puntos = resultado; 
                }//sino entro es porque cancelo con anticipacion y le descuento 0
            } catch (ExceptionViajesCompartidos ex) {
                Logger.getLogger(DAOPuntos.class.getName()).log(Level.SEVERE, null, ex);
            }          
           
         
        return puntos;
    }
    
    //byfede
    public List<Sancion> buscarSanciones(Cliente cliente) throws ExceptionViajesCompartidos{
		List<Sancion> sanciones = null;
		if(cliente.getId_usuario()==null){
			throw new ExceptionViajesCompartidos("ERROR: USUARIO INEXISTENTE");
                }		
		Query qry = entitymanager.createNamedQuery("Sancion.PorIDCliente");
    		qry.setParameter("id_cliente", cliente);
    		sanciones = (List<Sancion>)qry.getResultList();
		
		return sanciones;
	}
    
    //byfede
    public double diferenciaTimestamps(Timestamp salida_viaje, Timestamp cancelacion){
        
        double diferencia = 0;        
        long long_salida = salida_viaje.getTime();
        long long_cancelacion = cancelacion.getTime();
        long long_diferencia = long_salida - long_cancelacion;
        if(long_diferencia<0){
            diferencia= 0.01;
        }else{
                long totalSecs = long_diferencia/1000;
                long hours = (totalSecs / 3600);
                long mins = (totalSecs / 60) % 60;
                long secs = totalSecs % 60;

                if (hours > 0){
                    diferencia = (double)diferencia + hours+(mins/60d);
                }else{
                    if (mins > 0){
                        diferencia = (double)(mins/60d);   


                    }else{
                        diferencia = (double) 0.0001;
                    }

                }
        }
        return diferencia;
    }
    
    // by fede
    public synchronized boolean actualizarPuntosCliente(int monto, int id_cliente) throws ExceptionViajesCompartidos{
        
        this.iniciarTransaccion( );
        Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);     
        Integer puntos_cuenta = cliente.getPuntos();
        puntos_cuenta  = puntos_cuenta + (int)monto;
        cliente.setPuntos(puntos_cuenta);
        try{                
                this.entitymanager.getTransaction( ).commit( );
        }catch(Exception e){
		String error= ManejadorErrores.parsearRollback((RollbackException) e);
        	throw new ExceptionViajesCompartidos("ERROR: "+error);
		
        }        
        return true;
    }
    
   //by fede
 public synchronized boolean sancionarChofer(int id_viaje, int id_chofer,int aceptados) throws ExceptionViajesCompartidos{
        //formula
        // puntos = (CantPas * 50 ) + (1/hsfaltan * beta)
        this.iniciarTransaccion();
        Viaje viaje = new Viaje();
        Integer id_viaje2 = id_viaje;
        viaje = (Viaje) this.buscarPorPrimaryKey(viaje, id_viaje);
        Timestamp fecha_inicio = viaje.getFecha_inicio();
        Timestamp actual= new Timestamp((new java.util.Date()).getTime());
        double dif_horas = this.diferenciaTimestamps(fecha_inicio, actual); //calculo diferencia de fechas
        
        double segundo_termino = (1/dif_horas)*beta;
        List<PasajeroViaje> lista = viaje.getPasajeros();
        int cantidad_pasajeros = lista.size();
        double primer_termino = cantidad_pasajeros * 50;
        double resultado = primer_termino + segundo_termino;
        
        MovimientoPuntos mov = new MovimientoPuntos();
        Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_chofer);         
        mov.setCliente(cliente);
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date fecha = new java.sql.Date(utilDate.getTime());
        mov.setFecha( new Timestamp((new java.util.Date()).getTime()) ); 	//modificado por lucas al cambiar el tipo de fecha en movimientoPuntos
        mov.setTipo_mov_puntos(this.getTipoMovimientoPuntos("sancion"));
        resultado = resultado - 2*(resultado);
        Integer resultado_int = (int)resultado;
        mov.setMonto(resultado_int);
        Sancion sancion = new Sancion();
        sancion.setCliente(cliente);
        sancion.setMovimiento_puntos(mov);
        utilDate = new java.util.Date();
        fecha = new java.sql.Date(utilDate.getTime());
        sancion.setFecha_inicio(fecha);
        sancion.setFecha_fin(fecha);            
        sancion.setEstado(EstadoSancion.caduca);//le pongo caduca porque es de puntos, no es por tiempo.
        
            TipoSancion tipo_sancion = new TipoSancion();
            //Query qry = entitymanager.createNamedQuery("TipoSancion.buscarPorClaveCandidata");
            //qry.setParameter("clave_candidata", "Descuento de Puntos por Cancelación de viaje con Pasajeros");
            tipo_sancion= (TipoSancion) this.buscarPorPrimaryKey(new TipoSancion(),1);
            //tipo_sancion =(TipoSancion)qry.getSingleResult();
          
            //FIN
            sancion.setTipo_sancion(tipo_sancion);
            Notificacion notificacion= new Notificacion();
            notificacion.setCliente(cliente); 
            notificacion.setEstado(EstadoNotificacion.no_leido);
            notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()) ); 
            notificacion.setTexto("Usted ha sido sancionado a causa de:"+tipo_sancion.getDescripcion()+". Correspondiente al viaje: "+viaje.getNombre_amigable());
            
            //sancion de dias
            TipoSancion tipo_sancion_dias = (TipoSancion) this.buscarPorPrimaryKey(new TipoSancion(), 4);            
            Sancion sancion_dias = new Sancion();
            sancion_dias.setCliente(cliente);
            sancion_dias.setFecha_inicio(fecha);
            sancion_dias.setEstado(EstadoSancion.vigente);
            sancion_dias.setTipo_sancion(tipo_sancion_dias);
                //calculo los dias que lo voy a sancionar
                               
                Timestamp salida_viaje = viaje.getFecha_inicio();
                double diferencia = diferenciaTimestamps(salida_viaje, actual);
                List<Sancion> sanciones_anteriores = buscarSanciones(cliente);
                int num_sanciones_anteriores = sanciones_anteriores.size();
                //formulo
                // dias = ((1/diferencia)* SancionesAnteriores)*Pasajeros Aceptados
                double dias = ((1/diferencia)*(1+num_sanciones_anteriores)*aceptados);
                
                //pongo un tope a dias maximos de suspension
                int dias_int = 0;
                if(dias>50){
                    dias_int=30;
                }else{
                     dias_int = (int) dias;
                }  

                Calendar Calendario = Calendar.getInstance();
                Calendario.setTimeInMillis(actual.getTime());
                Calendario.add(Calendar.DATE, dias_int);
                Timestamp fecha_fin_ts = new Timestamp(Calendario.getTimeInMillis());
                java.sql.Date fecha_fin = new java.sql.Date(fecha_fin_ts.getTime());
                sancion_dias.setFecha_fin((Date) fecha_fin);
                //fin calculo de dias
            //fin sancion dias        
        try{    
                    this.entitymanager.persist(notificacion);
                    this.entitymanager.persist(mov);
                    this.entitymanager.getTransaction().commit();
                    this.iniciarTransaccion();
                    this.entitymanager.persist(sancion);
                     this.entitymanager.persist(sancion_dias);
                    this.entitymanager.getTransaction().commit();
                    //this.iniciarTransaccion();
                   
                   // this.entitymanager.getTransaction().commit();
                    //this.entitymanager.getTransaction( ).commit( );
                    boolean bandera = this.actualizarPuntosCliente(resultado_int, cliente.getId_usuario());
            }catch(RollbackException e){
                    String error= ManejadorErrores.parsearRollback(e);
                    throw new ExceptionViajesCompartidos("ERROR: "+error);
            }
        return true;
    }
 	
 	//by mufa
 	public synchronized boolean calificar(JSONObject datos) throws ExceptionViajesCompartidos{
 		/*
 		 * Cuando el participante califica a otro usuario, te mando esta data:	
			id_viaje: 4,
			nombre_usuario: nombre_usuario que realiza la calificacion,
				si soy el conductor, necesito saber a quien estoy calificando
				nombre_calificado: id_usuario, es a quien le pongo la nota
			confirmacion: "s"
			valoracion: "3"
			comentario: "piola viaje loco, recomendado"
 		 */
 		//VALIDACIONES GENERALES
 		Viaje viaje= (Viaje) this.buscarPorPrimaryKey(new Viaje(), datos.get("id_viaje"));
 		if(viaje==null){
 			throw new ExceptionViajesCompartidos("ERROR: NO EXISTE EL VIAJE");
 		}
 		
 		Character confirmacion = datos.get("confirmacion").toString().charAt(0) ;
 		if(confirmacion== null){
 			throw new ExceptionViajesCompartidos("ERROR: FALTA LA CONFIRMACION");
 		}
 		if(confirmacion!='S'&&confirmacion!='N'){
 			throw new ExceptionViajesCompartidos("ERROR: LA CONFIRMACION DEBE SER SI O NO");
 		}
 		Integer valoracion = Integer.parseInt(datos.get("valoracion").toString());
 		if(valoracion==null){
 			throw new ExceptionViajesCompartidos("ERROR: FALTA LA VALORACION");
 		}
 		if(valoracion<0 || valoracion>5){
 			throw new ExceptionViajesCompartidos("ERROR: VALOR INCORRECTO DE VALORACION");
 		}
 		String nomb_user = datos.get("nombre_usuario").toString();

 		Cliente cliente= (Cliente) this.buscarPorClaveCandidata("Cliente",nomb_user );
 		if (cliente==null){
 			throw new ExceptionViajesCompartidos("ERROR: NO EXISTE EL CLIENTE");
 		}
 		String comentario = datos.get("comentario").toString();
 		//si no pusieron comentario no importa
 		
 		Calificacion calificacion;
 		PasajeroViaje pasajeroviaje;
 		
 		this.iniciarTransaccion();
 		Notificacion notificacion = new Notificacion();
 		if(cliente.equals(viaje.getConductor())){ 
 			//si soy el conductor, necesito saber a q pasajero puntuo
 			nomb_user=(String) datos.get("nombre_calificado");
 			if(nomb_user==null){
 				throw new ExceptionViajesCompartidos("ERROR: FALTA EL PASAJERO A CALIFICAR");
 			}
 			Cliente cliente_a_calificar= (Cliente) this.buscarPorClaveCandidata("Cliente", nomb_user);
 	 		if (cliente==null){
 	 			throw new ExceptionViajesCompartidos("ERROR: EL PASAJERO A CALIFICAR NO EXISTE");
 	 		}
 	 		pasajeroviaje = viaje.recuperar_pasajeroViaje_por_cliente(cliente_a_calificar);
 	 		if(pasajeroviaje==null){
 	 			throw new ExceptionViajesCompartidos("ERROR: EL PASAJERO NO PARTICIPO DEL VIAJE");
 	 		}
 	 		// ya se a q pasajero puntuar
 	 		//busco la calificacion que tiene como unique al conductor y al pasajero
 	 		calificacion = (Calificacion) this.buscarPorClaveCandidataCompuesta("Calificacion", pasajeroviaje, cliente);
 	 		if(calificacion==null){
 	 			throw new ExceptionViajesCompartidos("ERROR: NO EXISTE LA CALIFICACION");
 	 		}
 	 		if(calificacion.getCalificacion_para_pasajero()!=null){	//significa que ya lo puntuo
 	 			throw new ExceptionViajesCompartidos("ERROR: YA PUNTUASTE AL PASAJERO");
 	 		}
 	 		calificacion.setCalificacion_para_pasajero(valoracion);		//puntuacion que el chofer le da al pasajero
 	 		calificacion.setComentario_conductor(comentario);			//comentarios que da el chofer
 	 		calificacion.setParticipo_pasajero(confirmacion);			//lo que dice el chofer de si el pasajero participo
 	 		
                        
                        //notificacion by fede
                       
                        String participo = null;
                        if(calificacion.getParticipo_pasajero().equals("S".charAt(0))){
                            participo= " participó del viaje";
                        }else{
                            if(calificacion.getParticipo_pasajero().equals("N".charAt(0))){
                             participo= " no participó del viaje";
                            }
                        
                        }                       
                        
                        notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()));
                        notificacion.setEstado(EstadoNotificacion.no_leido);
                        notificacion.setCliente(pasajeroviaje.getCliente());
                        notificacion.setTexto("Usted ha sido calificado por: <<"+cliente.getNombre_usuario()+" >>");
                        notificacion.setLink("/calificar_usuarios.html?id="+viaje.getId_viaje());
                        //fin notificacion
                        
                        
 		}else{
 			//si soy el pasajero puntuando al conductor
 			pasajeroviaje = viaje.recuperar_pasajeroViaje_por_cliente(cliente);
 	 		if(pasajeroviaje==null){
 	 			throw new ExceptionViajesCompartidos("ERROR: EL CLIENTE NO PARTICIPO DEL VIAJE");
 	 		}
 	 		calificacion = (Calificacion) this.buscarPorClaveCandidataCompuesta("Calificacion", pasajeroviaje, viaje.getConductor());
 	 		if(calificacion==null){
 	 			throw new ExceptionViajesCompartidos("ERROR: NO EXISTE LA CALIFICACION");
 	 		}
 	 		if(calificacion.getCalificacion_para_conductor()!=null){	//significa que ya lo puntuo
 	 			throw new ExceptionViajesCompartidos("ERROR: YA PUNTUASTE AL CONDUCTOR");
 	 		}
 	 		calificacion.setCalificacion_para_conductor(valoracion);		//puntuacion que el pasajero le da al chofer
 	 		calificacion.setComentario_pasajero(comentario);				//comentarios que deja el pasajero
 	 		calificacion.setParticipo_conductor(confirmacion);				//lo que dice el pasajero de "si lo pasaron a buscar"
 		
                        //notificacion by fede
                        String participo = null;
                        if(calificacion.getParticipo_conductor().equals("S".charAt(0))){
                            participo= "participó del viaje";
                        }else{
                            if(calificacion.getParticipo_conductor().equals("N".charAt(0))){
                             participo= " no participó del viaje";
                            }
                        
                        }       
                        notificacion.setCliente(viaje.getConductor());
                        notificacion.setFecha(new Timestamp((new java.util.Date()).getTime()));
                        notificacion.setEstado(EstadoNotificacion.no_leido);
                        notificacion.setTexto("Usted ha sido calificado por: <<"+cliente.getNombre_usuario()+" >>");
                        notificacion.setLink("/calificar_usuarios.html?id="+viaje.getId_viaje());
                        //fin notif
                
                }
 		
 		MovimientoPuntos movimientopuntos;
 		Cliente cliente_a_modificar;
 		
 		if(cliente.equals(viaje.getConductor())){ //si soy conductor
 			cliente_a_modificar=pasajeroviaje.getCliente();
 		}else{
 			//si soy un pasajero y puntue al conductor
 			cliente_a_modificar=viaje.getConductor();
 		}
 		//modifico la reputacion
 		Integer nueva_reputacion=this.actualizarReputacionPorCalificacion(valoracion, cliente_a_modificar);
		cliente_a_modificar.setReputacion(nueva_reputacion);
		//modifico los puntos creando un movimientoPuntos, y por ultimo sumo los puntos al cliente.
		Integer puntos = this.puntosPorCalificacion(valoracion);
		movimientopuntos = new MovimientoPuntos();
		movimientopuntos.setCliente(cliente_a_modificar);
		movimientopuntos.setMonto(puntos);
		movimientopuntos.setFecha( new Timestamp((new java.util.Date()).getTime()) );
		movimientopuntos.setTipo_mov_puntos( this.getTipoMovimientoPuntos("Puntos como consecuencia de calificación") );	//busco ese TipoMovPuntos
		cliente_a_modificar.sumarPuntos(puntos);
 		
		if(cliente.equals(viaje.getConductor())){ //si soy conductor
 			calificacion.setMovimiento_puntos_pasajero(movimientopuntos);
 		}else{
 			//si soy un pasajero y puntue al conductor
 			calificacion.setMovimiento_puntos_chofer(movimientopuntos);
 		}
		
		//guardo las cosas nuevas
		try{    
 			this.entitymanager.persist(movimientopuntos);
                        this.entitymanager.persist(notificacion);
                        this.entitymanager.getTransaction().commit();
	    }catch(RollbackException e){
	    	String error= ManejadorErrores.parsearRollback(e);
	        throw new ExceptionViajesCompartidos("ERROR: "+error);
	    }
 		
 		return true;
 	}
        
 	public synchronized boolean nuevoBeneficio(JSONObject json) throws ExceptionViajesCompartidos{
 		Beneficio beneficio = new Beneficio();
 		beneficio.setId_beneficio(Integer.MIN_VALUE);
 		String nombre_usuario = (String) json.get("nombre_usuario");
 		Query qry = entitymanager.createNamedQuery("Sponsor.buscarPorClaveCandidata");
 		qry.setParameter("clave_candidata", nombre_usuario);
 		Sponsor sponsor =(Sponsor)qry.getSingleResult();
 		beneficio.setSponsor(sponsor);
 		beneficio.setPuntos_necesarios((Integer) json.get("puntos_necesarios"));
 		beneficio.setProducto((String) json.get("producto"));
 		beneficio.setFecha_caduca((Date) json.get("fecha_caduca"));
 		try{
 			this.iniciarTransaccion(); 
 			this.entitymanager.persist(beneficio);
 			this.entitymanager.getTransaction().commit();
                
	    }catch(RollbackException e){
                
	    	String error= ManejadorErrores.parsearRollback(e);
	        throw new ExceptionViajesCompartidos("ERROR: "+error);        
	    } 
 		
 		return true;
 	}
 	
 	//by mufa
 	protected Integer puntosPorCalificacion(Integer calificacion){
 		//formula para asignar puntos en base a la calificacion, va en un metodo aparte asi es mas facil de mantener
 		Integer i;
 		if(calificacion<2){
 			i=calificacion*5;
 		}else{
 			i = calificacion*10;
 		}
 		return i;
 	}
 	
 	//by mufa
 	protected synchronized Integer actualizarReputacionPorCalificacion(Integer calificacion, Cliente cliente){
 		//formula para recalcular la nueva reputacion
 		Integer reputacion_nueva;
 		Integer reputacion = cliente.getReputacion();
 		reputacion_nueva =(Integer) (reputacion + calificacion)/2;	//no es un promedio, es peor
 		this.refresh(cliente);
                return reputacion_nueva;
 	}
 	
 	public TipoMovimientoPuntos getTipoMovimientoPuntos(String descripcion){
 		TipoMovimientoPuntos tipo;
 		Query q2 = entitymanager.createNamedQuery("TipoMovimientoPuntos.PorDescripcion");
	    q2.setParameter("descripcion", descripcion);
	    tipo = (TipoMovimientoPuntos) q2.getSingleResult();
 		return tipo;
 	}
        
        public double diferenciaTimestampsParaVehiculos(Timestamp salida_viaje, Timestamp cancelacion){
        
        double diferencia = 0;        
        long long_salida = salida_viaje.getTime();
        long long_cancelacion = cancelacion.getTime();
        long long_diferencia = long_salida - long_cancelacion;
        
                long totalSecs = long_diferencia/1000;
                long hours = (totalSecs / 3600);
                long mins = (totalSecs / 60) % 60;
                long secs = totalSecs % 60;

                
                    diferencia = (double)diferencia + hours+(mins/60d);
               
                
        return diferencia;
        
        }
        
        
        public List<MovimientoPuntos> getMovimientoPuntos(int id_cliente){
            List<MovimientoPuntos> lista_mov_puntos = new ArrayList();
            Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
            Query qry = this.entitymanager.createNamedQuery("MovimientoPuntos.PorCiente");
            qry.setParameter("cliente", cliente);
            lista_mov_puntos = qry.getResultList();
            return lista_mov_puntos;        
        }
        
        
}
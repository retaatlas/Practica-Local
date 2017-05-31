package gestionComisiones.controlador;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.json.simple.JSONObject;

import gestionComisiones.modelo.Comision;
import gestionComisiones.modelo.ComisionCobrada;
import gestionComisiones.modelo.EstadoComisionCobrada;
import gestionComisiones.modelo.MovimientoSaldo;
import gestionComisiones.modelo.Pago;
import gestionComisiones.modelo.TipoMovSaldo;
import gestionUsuarios.modelo.Cliente;
import gestionViajes.modelo.PasajeroViaje;
import gestionViajes.modelo.Viaje;

import java.util.ArrayList;

import otros.DataAccesObject;
import otros.ExceptionViajesCompartidos;
import otros.ManejadorErrores;

public class DAOComisiones extends DataAccesObject {
	private static DAOComisiones instance = null;

	private DAOComisiones() {
		super();
	}

	public static DAOComisiones getInstance() {
		if (DAOComisiones.instance == null) {
			DAOComisiones.instance = new DAOComisiones();
		}
		return DAOComisiones.instance;
		
	}
	
	//by mufa
	public synchronized boolean nuevaComision(JSONObject datos) throws ExceptionViajesCompartidos{
		/*
		 * datos que recibo:
		 * {
		 * limite inferior: integer,
		 * limite superior: integer,
		 * precio: float
		 * }
		 */
		
		Integer limite_inferior = (Integer) datos.get("limite_inferior");
		if(limite_inferior==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL LIMITE INFERIOR");
		}
		Integer limite_superior = (Integer) datos.get("limite_superior");
		if(limite_superior==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL LIMITE SUPERIOR");
		}
		if(limite_superior<limite_inferior){
			throw new ExceptionViajesCompartidos("ERROR: EL LIMITE SUPERIOR NO PUEDE SER MENOR QUE EL LIMITE INFERIOR");
		}
		if(limite_superior<=0 || limite_inferior<0){
			throw new ExceptionViajesCompartidos("ERROR: LOS LIMITES NO PUEDEN SER MENORES QUE CERO");
		}
		Float precio=null;
		try{
			precio = (Float) datos.get("precio");
		}catch(Exception e){
			precio = ((Integer) datos.get("precio")).floatValue();
		}
		if(precio==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL DATO PRECIO");
		}
		if(precio<=0){
			throw new ExceptionViajesCompartidos("ERROR: EL PRECIO NO PUEDE SER NEGATIVO");
		}
		
		this.iniciarTransaccion();
		
		Comision comision = new Comision();
		comision.setLimite_inferior(limite_inferior);
		comision.setLimite_superior(limite_superior);
		comision.setPrecio(precio);
		comision.setFecha_inicio( new Date((new java.util.Date()).getTime()) );
		comision.setFecha_fin(null);
		
		try{
			this.entitymanager.persist(comision);
    		this.entitymanager.getTransaction( ).commit( );	
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
		return true;
	}
	
	//by mufa
	public synchronized boolean FinalizarComision(Integer id_comision) throws ExceptionViajesCompartidos{
		Comision comision = (Comision) this.buscarPorPrimaryKey(new Comision(), id_comision);
		if(comision==null){
			throw new ExceptionViajesCompartidos("ERROR: LA COMISION NO EXISTE");
		}
		
		this.iniciarTransaccion();
		
		comision.setFecha_fin( new Date((new java.util.Date()).getTime()) );
		
		try{
    		this.entitymanager.getTransaction( ).commit( );	
                 this.refresh(comision);
    	}catch(RollbackException e){
    		String error= ManejadorErrores.parsearRollback(e);
    		throw new ExceptionViajesCompartidos("ERROR: "+error);
    	}
		return true;
	}
	
	//by mufa
	@SuppressWarnings("unchecked")
	public List<Comision> getComisionesVigentes(){
		Query qry = entitymanager.createNamedQuery("Comision.vigentes");
		return qry.getResultList();
	}
	
	//by mufa
	@SuppressWarnings("unchecked")
	public List<Comision> getComisionesNOVigentes(){
		Query qry = entitymanager.createNamedQuery("Comision.NOvigentes");
		return qry.getResultList();
	}
	
	//by mufa
	@SuppressWarnings("unchecked")
	public List<Comision> getTodasLasComisiones(){
		return this.selectAll("Comision");
	}
	
	public synchronized boolean modificarComision(JSONObject datos) throws ExceptionViajesCompartidos{
		/*
		 * datos que recibo:
		 * {
		 * comision: id_comision,
		 * limite inferior: integer,
		 * limite superior: integer,
		 * precio: float
		 * }
		 */
		
		Integer limite_inferior = (Integer) datos.get("limite_inferior");
		if(limite_inferior==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL LIMITE INFERIOR");
		}
		Integer limite_superior = (Integer) datos.get("limite_superior");
		if(limite_superior==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL LIMITE SUPERIOR");
		}
		if(limite_superior<limite_inferior){
			throw new ExceptionViajesCompartidos("ERROR: EL LIMITE SUPERIOR NO PUEDE SER MENOR QUE EL LIMITE INFERIOR");
		}
		if(limite_superior<=0 || limite_inferior<0){
			throw new ExceptionViajesCompartidos("ERROR: LOS LIMITES NO PUEDEN SER MENORES QUE CERO");
		}
		Float precio=null;
		try{
			precio = (Float) datos.get("precio");
		}catch(Exception e){
			precio = ((Integer) datos.get("precio")).floatValue();
		}
		if(precio==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA EL DATO PRECIO");
		}
		if(precio<=0){
			throw new ExceptionViajesCompartidos("ERROR: EL PRECIO NO PUEDE SER NEGATIVO");
		}
		Integer id_comision = (Integer) datos.get("id_comision");
		if(id_comision==null){
			throw new ExceptionViajesCompartidos("ERROR: FALTA LA COMISION A MODIFICAR");
		}
		Comision comision=(Comision) this.buscarPorPrimaryKey(new Comision(), id_comision);
		if(comision==null){
			throw new ExceptionViajesCompartidos("ERROR: LA COMISION NO EXISTE EN EL SISTEMA");
		}
		if(comision.getComisiones_cobradas().size()>0){
			throw new ExceptionViajesCompartidos("ERROR: NO SE PUEDE MODIFICAR UNA COMISION QUE TIENE COMISIONES COBRADAS ASOCIADAS");
		}
		this.iniciarTransaccion();
		
		comision.setLimite_inferior(limite_inferior);
		comision.setLimite_superior(limite_superior);
		comision.setPrecio(precio);		
		
		try{
                    this.entitymanager.getTransaction( ).commit( );	
                    this.refresh(comision);
                }catch(RollbackException e){
                    String error= ManejadorErrores.parsearRollback(e);
                    throw new ExceptionViajesCompartidos("ERROR: "+error);
                }
		
		return true;
	}
	
	//retocado por mufa
	public synchronized ComisionCobrada nuevaComisionCobrada(Double km) throws ExceptionViajesCompartidos{
			this.limpiarTransacciones();
            
            ComisionCobrada cc = new ComisionCobrada();
			Query qry = entitymanager.createNamedQuery("Comision.PrecioPorKM");
			qry.setParameter("km", km);
			Comision comision = (Comision) qry.getSingleResult();
			if(comision==null){
				throw new ExceptionViajesCompartidos("ERROR: NO SE PUDO RECUPERAR LA COMISION PARA: "+km+" Kms");
			}
			cc.setComision(comision);
			//TODO linea nueva con precio del km x Km, y redonde a 2 decimales
			Float monto= comision.getPrecio() * km.floatValue();
			monto = (float) (Math.round(monto * 100.0) / 100.0);		
			cc.setMonto( monto );
			return cc;
	}
	
	//by jasmin?
	public synchronized boolean cobrarComision(PasajeroViaje pv) throws ExceptionViajesCompartidos{
		this.limpiarTransacciones();
                
		
		ComisionCobrada cc=this.entitymanager.merge(pv.getComision());
                if(cc.getEstado().equals(EstadoComisionCobrada.pendiente)){
                    //BUSCO CONDUCTOR
                    
                    this.iniciarTransaccion( );
                    pv = this.entitymanager.merge(pv);
                    Viaje v=pv.getViaje();
                    Cliente conductor=this.entitymanager.merge(v.getConductor());
                    //CREO EL MOVIMIENTO SALDO
                    MovimientoSaldo ms=new MovimientoSaldo();
                    java.util.Date utilDate = new java.util.Date();
                    java.sql.Date fecha = new java.sql.Date(utilDate.getTime());
                    ms.setCliente(conductor);
                    ms.setFecha(fecha);
                    ms.setComision_cobrada(cc);
                    ms.setMonto(cc.getMonto());
                    ms.setPago(null);
                    Query qry = entitymanager.createNamedQuery("TipoMovSaldo.SearchById");
                    qry.setParameter("id",1);
                    TipoMovSaldo tms= (TipoMovSaldo) qry.getSingleResult();
                    ms.setTipo_mov_saldo(tms);
                    //enlazo a la CC con el MS anterior
                    cc.setfecha(new Timestamp((new java.util.Date()).getTime()));
                    cc.setMovimiento_saldo(ms);                    
                    //LE DESCUENCTO LA COMISION POR ESE PASAJERO Q RECIBIO
                    float saldo=conductor.getSaldo();
                    conductor.setSaldo(saldo-cc.getMonto());                    
                    //le cambio el estado al PV por comision cobrada
                    pv.getComision().setEstado(EstadoComisionCobrada.pagado);
                     try{
                        this.entitymanager.persist(ms);
                        this.entitymanager.getTransaction().commit();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                                
                }else{
                    if(cc.getEstado().equals(EstadoComisionCobrada.desestimada)){
                        throw new ExceptionViajesCompartidos("ERROR: NO SE PUEDE COBRAR UNA COMISION DESESTIMADA");
                    }
                    if(cc.getEstado().equals(EstadoComisionCobrada.pagado)){
                        throw new ExceptionViajesCompartidos("ERROR: LA COMISION YA FUE PAGADA");
                    }
                    if(cc.getEstado().equals(EstadoComisionCobrada.vencido)){
                        throw new ExceptionViajesCompartidos("ERROR: LA COMISION ESTA VENCIDA");
                    }
                }
        
		return true;
	}
	
	
	public synchronized boolean sumarSaldo(int id_cliente,float monto){
            
            
        this.iniciarTransaccion( );
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente); 
		// Esto es por las dudas
		this.refresh(cliente);
		float saldo=cliente.getSaldo();
		cliente.setSaldo(saldo+monto);
		
		//CREO PAGO
		Pago p=new Pago();
		java.util.Date utilDate = new java.util.Date();
                java.sql.Date fecha = new java.sql.Date(utilDate.getTime());
		p.setFecha(fecha);
		p.setMonto(monto);
		p.setCliente(cliente);
                try{
                 this.entitymanager.persist(p);
                 this.entitymanager.getTransaction().commit();
                 }catch(Exception e){
                    e.printStackTrace();
                 }
                
                this.iniciarTransaccion();
		//CREO EL MOVIMIENTO SALDO
		 MovimientoSaldo ms=new MovimientoSaldo();
		 ms.setComision_cobrada(null);
		 ms.setFecha(fecha);
		 ms.setMonto(monto);
		 ms.setPago(p);
                 ms.setCliente(cliente);
		 Query qry = entitymanager.createNamedQuery("TipoMovSaldo.SearchById");
		 qry.setParameter("id",2);
		 TipoMovSaldo tms= (TipoMovSaldo) qry.getSingleResult();
		 ms.setTipo_mov_saldo(tms);
		 
		 //p.setMovimiento_saldo(ms); 
                 
                 try{
                 //this.entitymanager.persist(p);
                 this.entitymanager.persist(ms);
                 this.entitymanager.getTransaction().commit();
                 }catch(Exception e){
                    e.printStackTrace();
                 }
		
		return true;
	}

	public float consultarSaldo(int id_cliente){
		Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente); 
		float saldo=cliente.getSaldo();
	return saldo;
	}
        
        public List<MovimientoSaldo> getMovimientosSaldo(int id_cliente){
            List<MovimientoSaldo> lista_movimientos_saldo = new ArrayList();
            Cliente cliente = (Cliente) this.buscarPorPrimaryKey(new Cliente(), id_cliente);
            Query qry = this.entitymanager.createNamedQuery("MovSaldo.PorCliente");
            qry.setParameter("cliente", cliente);
            lista_movimientos_saldo = qry.getResultList();
            return lista_movimientos_saldo;
        }
        
}

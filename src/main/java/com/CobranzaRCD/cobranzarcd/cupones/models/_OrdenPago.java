package com.CobranzaRCD.cobranzarcd.cupones.models;

import com.CobranzaRCD.cobranzarcd.clases.OrdenPago;
import com.CobranzaRCD.cobranzarcd.clases.DatosSilice;
import com.CobranzaRCD.cobranzarcd.clases.Respuesta;
import com.CobranzaRCD.cobranzarcd.clases.OrdenPagoDet;
import com.CobranzaRCD.cobranzarcd.cupones.clases.CUPON;
import com.CobranzaRCD.cobranzarcd.cupones.clases.CUPONDET;
import com.CobranzaRCD.cobranzarcd.cupones.interfaces.OrdenPagoCuponInterface;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.ArrayList;

@Repository
@Transactional
public class _OrdenPago implements OrdenPagoCuponInterface{

    @PersistenceContext
    private EntityManager entitymanager;

    public OrdenPago ObtenerCupon(int reference)
    {
        OrdenPago ordenpago = new OrdenPago();
        
        try{            
            String query = "FROM CUPON WHERE PAGADO = 0 AND IDCUPON = "+ reference;
            //CUPON cupon = (CUPON) entitymanager.find(CUPON.class , reference);
            CUPON cupon = (CUPON)entitymanager.createQuery(query).getSingleResult();
            if(cupon != null)
            {
                ordenpago.ordenId = Integer.toString(cupon.getIDCUPON());
                ordenpago.folio = cupon.getFOLIO();
                ordenpago.nombreCliente = cupon.getCLIENTENOMBRE()+ " "+cupon.getCLIENTEAPELLIDOS();
                ordenpago.telefonoCliente = cupon.getTELEFONO();;
                ordenpago.total = Double.toString(cupon.getCOSTO());
                ordenpago.concepto = cupon.getFOLIO();
                ordenpago.emailCliente = cupon.getCLIENTEEMAIL();                
                
            }
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }

        return ordenpago;
    }
    public ArrayList<OrdenPagoDet> ObtenerDetalleCupon(int id)
    {
        ArrayList<OrdenPagoDet> detalle = new ArrayList<>();

        try{
            String query = "FROM CUPONDET WHERE IDCUPON ="+id;

            List<CUPONDET> list = entitymanager.createQuery(query).getResultList();            

            for(CUPONDET item : list)
            {
                OrdenPagoDet ordendet = new OrdenPagoDet();
                ordendet.cantidad = 1;
                ordendet.moneda = item.getMONEDA();
                ordendet.precio = item.getCOSTO();
                ordendet.producto = item.getCLAVECERT();
                ordendet.propiedad = item.getPROPIEDAD();
                detalle.add(ordendet);
            }            
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
        return detalle;
        
    }
    public Respuesta GuardarDatosSilice(DatosSilice datossilice)
    {
        Respuesta resp = new Respuesta();

        try{
            Session session = entitymanager.unwrap(Session.class);
            session.save(datossilice);
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.mensaje = e.getMessage();
            System.err.println(e.getMessage());
        }

        return resp;
    }
}

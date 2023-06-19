package com.CobranzaRCD.cobranzarcd.cupones.models;

import com.CobranzaRCD.cobranzarcd.cupones.clases.CERTIFICADOS;
import com.CobranzaRCD.cobranzarcd.cupones.clases.CUPON;
import com.CobranzaRCD.cobranzarcd.cupones.clases.CUPONDET;
import com.CobranzaRCD.cobranzarcd.cupones.clases.TIPOCERTIFICADO;
import com.CobranzaRCD.cobranzarcd.clases.Respuesta;
import com.CobranzaRCD.cobranzarcd.cupones.interfaces.CertificadoInterface;
import com.CobranzaRCD.cobranzarcd.cupones.interfaces.CuponInterface;
import com.CobranzaRCD.cobranzarcd.src.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.hibernate.Session;
import lombok.RequiredArgsConstructor;

@Repository
@Transactional
@RequiredArgsConstructor
@EnableTransactionManagement
public class _CUPON implements CuponInterface{
    @Autowired
    private EntityManager entitymanager;
 
    @Autowired
    public Respuesta resp;    

    @Autowired
    private CertificadoInterface certificadoInterface;

    @Autowired
    private GenericUtils genericUtils;

    public CUPON ObtenerPorId(int idCupon)
    {
        return entitymanager.find(CUPON.class, idCupon);
    }
    public List<CUPONDET> ObtenerDetallePorCupon(int idCupon)
    {
        String consulta = "FROM CUPONDET WHERE IDCUPON = "+ idCupon;
        return entitymanager.createQuery(consulta).getResultList();
    }
    public CUPONDET ObtenerDetallePorIdDet(int idDetalle)
    {
        CUPONDET cupondet = new CUPONDET();

        try{
            cupondet = entitymanager.find(CUPONDET.class, idDetalle);
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
        return cupondet;
    }

    public TIPOCERTIFICADO ObtenerPorClve(String clave)
    {
        TIPOCERTIFICADO tipoCertificado = new TIPOCERTIFICADO();

        try{
            tipoCertificado = entitymanager.createQuery("FROM TIPOCERTIFICADO WHERE CLAVE = '"+clave+"'", TIPOCERTIFICADO.class).getSingleResult();
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }

        return tipoCertificado;
    }
    public Respuesta ActualizarRegistroCupon(CUPON cupon, List<CUPONDET> detalle)
    {
        try{
            Session session = entitymanager.unwrap(Session.class);
            session.update(cupon);

            for(CUPONDET item : detalle)
            {
                Session sessiondet = entitymanager.unwrap(Session.class);
                sessiondet.update(item);
            }
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.mensaje = e.getMessage();   
        }
        
        return resp;
    }

    public Respuesta EnviarMailVendedor(CUPON registro, List<CUPONDET> detalle)
    {
        Respuesta resp = new Respuesta();

        try{
            java.text.DecimalFormat formatomoneda = new java.text.DecimalFormat("$ #,###.00");
			SimpleDateFormat formato=new SimpleDateFormat("MM/dd/yyyy");
            StringBuilder cuerpoTabla = new StringBuilder();
            Double Total = 0.0;
            
            for(CUPONDET item : detalle)
            {
                CERTIFICADOS certificado = certificadoInterface.ObtenerPorClave(item.getCLAVECERT());
                cuerpoTabla.append("  <tr> " + 
                        "<td class=\"tg-buh4\">"+certificado.getDESCRIPCION()+"</td> " + 
                        "<td class=\"tg-buh4\">"+formatomoneda.format(item.getCOSTO())+"</td> " +
                        "<td class=\"tg-buh4\">"+(item.getNORESERVA()==null?"":item.getNORESERVA())+"</td> " + 
                        "<td class=\"tg-buh4\">"+(item.getFECHAVIAJE()==null?"":formato.format(item.getFECHAVIAJE()))+"</td> " +
                        "<td></td>"+
                        // "    <td class=\"tg-buh4\">"+(hotel==null?"":hotel)+"</td> " + 
                        "  </tr> ");

                if(item.getIDCUPONDETCAMBIO()>0)
                {
                    CUPONDET cuponCambio=ObtenerDetallePorIdDet(item.getIDCUPONDETCAMBIO());
					CERTIFICADOS certCambio=certificadoInterface.ObtenerPorClave(cuponCambio.getCLAVECERT());
					// String hotelCambio=_Propiedad.ObtenerNombrePropiedad(cuponCambio.PROPIEDAD);
                    cuerpoTabla.append("<tr> " + 
			                "<td class=\"tg-buh4\">"+certCambio.getDESCRIPCION()+"</td> " + 
			                "<td class=\"tg-buh4\">-"+formatomoneda.format(cuponCambio.getCOSTO())+"</td> " +
			                "<td class=\"tg-buh4\">"+(cuponCambio.getNORESERVA()==null?"":cuponCambio.getNORESERVA())+"</td> " + 
			                "<td class=\"tg-buh4\">"+(cuponCambio.getFECHAVIAJE()==null?"":formato.format(cuponCambio.getFECHAVIAJE()))+"</td> " +
			                // "    <td class=\"tg-buh4\">"+(hotelCambio==null?"":hotelCambio)+"</td> " + 
                            "<td></td>"+
			                "  </tr> ");							
                }

                Total+=item.getIMPORTECOBRARWP();
            }

            String mensaje = "<table style='max-width: 600px; padding: 10px; margin:0 auto; border-collapse: collapse;'>"
                        + "<tr>"
                        + "<td style='background-color: #223343; text-align: left; padding: 0'>"
                        + "<div>"
                        + "<img width='93%' style='display:block; margin: 1.5% 3%' src='https://usergui.rcdhotels.com/imagenes/BannerRCD.gif'>"
                        + "</div>"
                        + "</td>"
                        + "</tr>"
                        + "<!--Mensaje-->"
                        + "<tr>"
                        + "<td style='background-color: #223343'>"
                        + "<div style='color: white; margin: 4% 10% 2%; text-align: justify;font-family: sans-serif'>"
                        + "<h2 style='color: #6585c8; margin: 0 0 7px'>Cupon registrado / Registered coupon </h2>"
                        + "<br>"
                        + "<hr noshade='noshade' size='2' width='100%' style='color: #a1a1a1;' />"
                        + "<p style='color: #a1a1a1;'></p>"
                        + "<ul style='font-size: 15px;  margin: 10px 0'>"
                        + "<li><b>Folio / Coupon : </b>"+registro.getFOLIO() +"</li>"
                        + "<li><b>Fecha venta / Sale date : </b>"+formato.format(registro.getFECHAVENTA()) +"</li>"
                        + "<li><b>Nombre / Name : </b>"+registro.getCLIENTENOMBRE() + " " + registro.getCLIENTEAPELLIDOS()+"</li>"
                        + "<li><b>No. membresía / Membership  : </b>"+(registro.getNOMEMBRESIA()==null ? "":registro.getNOMEMBRESIA()) +"</li>"  
                        + "<li><b>Costo ("+registro.getMONEDA()+") / Price ("+registro.getMONEDA()+") : </b>"+formatomoneda.format(Total)+"</li>"
                        + "<li><b>Vendedor / Seller : </b>"+registro.getAGENTEVENDEDOR() +"</li>"
                        + "</ul>"
                        + "<br>"
                        + "<hr noshade='noshade' size='2' width='100%' style='color: #a1a1a1;' />"
                        + "<p style='color: #a1a1a1;'></p>"
                        + "<br> "
                        + "<div style='width: 100%; text-align: center'>"
                        + "</div>"
                        + "<div style='width: 100%; text-align: center'>"    
                        +"<style type=\"text/css\"> " + 
                        ".tg  {border-collapse:collapse;border-spacing:0;border-color:#ccc;margin:0px auto;width:100%;} " + 
                        ".tg td{font-family:sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:0px;overflow:hidden;word-break:normal;border-top-width:1px;border-bottom-width:1px;border-color:#ccc;color:white;} " + 
                        ".tg th{font-family:sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:0px;overflow:hidden;word-break:normal;border-top-width:1px;border-bottom-width:1px;border-color:#ccc;} " + 
                        ".tg .tg-buh4{text-align:left;vertical-align:top} " + 
                        ".tg .tg-0lax{text-align:left;vertical-align:top} " + 
                        "@media screen and (max-width: 767px) {.tg {width: auto !important;}.tg col {width: auto !important;}.tg-wrap {overflow-x: auto;-webkit-overflow-scrolling: touch;margin: auto 0px;}}</style> " + 
                        "<div class=\"tg-wrap\"><table class=\"tg\" style=\"margin-top:20px\"> " + 
                        "<thead> " + 
                        "<tr style=\"background: white;color: #223343;\"> " + 
                        "<th style=\"width: 40%;\" class=\"tg-0lax\">Certificado</th> " +
                        "<th style=\"width: 40%;\" class=\"tg-0lax\">Costo</th> " + 
                        "<th style=\"width: 20%;\" class=\"tg-0lax\">Reserva</th> " + 
                        "<th style=\"width: 20%;\" class=\"tg-0lax\">Fecha viaje</th> " + 
                        "<th style=\"width: 20%;\" class=\"tg-0lax\">Propiedad</th> " +
                        "</tr> " + 
                        "</thead> " + 
                        "<tbody> " + 
                         cuerpoTabla.toString() +                       
                        "  </tbody> " + 
                        "</table></div>"
                        + "</div>"
                        + "<br>"
                        + "<p style='color: #b3b3b3; font-size: 12px; text-align: center;margin: 30px 0 0'>RCD Hotels � 2019</p>"
                        + "</div>"
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style='background-color: #223343; text-align: left; padding: 0'>"
                        + "<div>"
                        + "<img width='93%' style='display:block; margin: 1.5% 3%; margin-top: -2%; margin-left:5%;' src='https://usergui.rcdhotels.com/imagenes/bannerFoot.png'>"
                        + "</div>"
                        + "</td>"
                        + "</tr>"
                        + "</table>";

                        genericUtils.EnviarEmailMiddleware("", "notificacionesscp@legendaryvacationclub.com", registro.getCLIENTEEMAIL(), "Cobro Reservaciones", "Payment / Pago Autorizado", mensaje, "");
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.mensaje = e.getMessage();
        }
        return resp;
    }
}

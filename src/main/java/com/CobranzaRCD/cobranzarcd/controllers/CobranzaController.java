package com.CobranzaRCD.cobranzarcd.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;
import java.util.List;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.CobranzaRCD.cobranzarcd.clases.Silice;
import com.CobranzaRCD.cobranzarcd.clases.Respuesta;
import com.CobranzaRCD.cobranzarcd.clases.TarjetaTokenizada;
import com.CobranzaRCD.cobranzarcd.cupones.clases.CUPON;
import com.CobranzaRCD.cobranzarcd.cupones.clases.CUPONDET;
import com.CobranzaRCD.cobranzarcd.cupones.interfaces.CuponInterface;
import com.CobranzaRCD.cobranzarcd.cupones.interfaces.OrdenPagoCuponInterface;

import org.springframework.beans.factory.annotation.Autowired;
import com.CobranzaRCD.cobranzarcd.clases.OrdenPago;
import com.CobranzaRCD.cobranzarcd.clases.DatosSilice;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
public class CobranzaController {
    
    @Autowired
    private OrdenPagoCuponInterface ordenpagointerface;

    @Autowired
    private CuponInterface cuponinterface;

    @RequestMapping(value = "")
    public ModelAndView home(@RequestParam String modulo, @RequestParam String reference)
    {
        ModelAndView vista = new ModelAndView("nofound");        

        if(!modulo.equals("") || !reference.equals(""))        
        {            
            OrdenPago ordenpago = new OrdenPago();
            switch(modulo)
            {
                case "CUPONES":
                    ordenpago = ordenpagointerface.ObtenerCupon(Integer.parseInt(reference));
                    ordenpago.sistema = modulo;                    
                    break;
                default:
                    break;
            }

            if(ordenpago.ordenId != null)
            {
                vista = new ModelAndView("IndexCobranza");
                UUID uuid = UUID.randomUUID();
                vista.addObject("token", uuid.toString());    
                vista.addObject("ordenpago", ordenpago);
            }
            else
            {
                vista = new ModelAndView("nofound");
            }
        }
        
        return vista;
    }

    @RequestMapping(value = "clavecifrado", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Respuesta token()
    {
        Respuesta resp = new Respuesta();
        Silice silice = new Silice();
        try{
            String clave = silice.ObtenerClaveCifrado();            
            resp.data = clave;
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.mensaje = e.getMessage();
        }
        
        return resp;
    }
    
    @RequestMapping(value="cobrosilice",method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    public Respuesta CobroSilice(@RequestParam String DatosTarjeta, @RequestParam String OrdenPago)
    {
        Respuesta resp = new Respuesta();
        Gson gson = new Gson();
        Silice silice = new Silice();
        String card = "";
        String ordenid= "";
        String cardType = "";
        String cardNumber = "";

        try{
            java.util.Date FechaActual = new java.util.Date();
            TarjetaTokenizada tk = gson.fromJson(DatosTarjeta, TarjetaTokenizada.class);
            String jsonTarjeta = gson.toJson(tk);
            resp = silice.TokenizarTarjeta(jsonTarjeta);

            if(resp.error == false)
            {
                //      EJEMPLO RESPUESTA TARJETA TOKENIZADA
                //  "{"status":true,"data":{"card":"************8153","token":"1acef9b64e228dd79abf593cced34d40317de21fd587210cb518","typeT":"visa","expDate":"06/2025","clientId":"645aab2a34a7d1e9e8a5afce","dataExtra":""},"menssage":""}"

                JSONObject jsonobj = new JSONObject(resp.data);
                JSONObject jsonobjtar = jsonobj.getJSONObject("data");
                card = jsonobjtar.getString("token");
                cardNumber = jsonobjtar.getString("card");
                cardType = jsonobjtar.getString("typeT");

                OrdenPago orden = gson.fromJson(OrdenPago, OrdenPago.class);
                orden.items = ordenpagointerface.ObtenerDetalleCupon(Integer.parseInt(orden.ordenId));

                if(orden.items.size() > 0)
                {
                    String ordenjson = gson.toJson(orden);
                    resp = silice.CrearOrdenPago(ordenjson);
    
                    if(resp.error == false)
                    {
                        //      EJEMPLO RESPUESTA ORDEN DE PAGO
                        //  "{"url":"https://qa.upayment.app//orden/t8TvUorTjMRN","sessionId":"t8TvUorTjMRN","reciboId":"646bdba3eed446f7e438516d"}"
                        JSONObject jsonobj2 = new JSONObject(resp.data);
                        ordenid = jsonobj2.getString("reciboId");                    
                                                
                        String jsondebito = "{\"card\": {\"token\":\""+card+"\"},\"order\": {\"reciboId\": \""+ordenid+"\"}}";
                        resp = silice.CobroDebitoDirecto(jsondebito);    
                        
                        if(resp.error == false)                        
                        {                           
                            JSONObject jsoncobro = new JSONObject(resp.data) ;
                            if(jsoncobro.getBoolean("status"))
                            {                                
                                JSONObject dataCobroSilice = jsoncobro.getJSONObject("data");
                                DatosSilice datossilice = new DatosSilice(); 
                                datossilice.setTRANSACTIONID(dataCobroSilice.getString("_id"));                           
                                datossilice.setIDREGISTRO(Integer.parseInt(orden.ordenId));
                                datossilice.setSISTEMA(orden.sistema);
                                datossilice.setTOKENTARJETA(card);
                                datossilice.setCOMPANY("");
                                datossilice.setNUMCONFIRM("");
                                datossilice.setNUMRESERVATION("");
                                datossilice.setIDHOTEL(orden.items.get(0).propiedad);
                                datossilice.setCARDNUMBER(cardNumber);
                                datossilice.setCARDTYPE(cardType);
                                //  AGREGAR EL TRANSACTION ID PARA GUARDAR EN LA BASE DE DATOS
    
                                Respuesta respguardado = ordenpagointerface.GuardarDatosSilice(datossilice);
                                resp.error = respguardado.error;
                                resp.mensaje = respguardado.mensaje;
    
                                if(resp.error == false)
                                {
                                    
                                    CUPON cupon = cuponinterface.ObtenerPorId(Integer.parseInt(orden.ordenId));
                                    cupon.setPAGADO(1);
                                    cupon.setNOTARJETA(cardNumber);
                                    cupon.setTIPOTARJETA(cardType);
    
                                    List<CUPONDET> detailCupon = cuponinterface.ObtenerDetallePorCupon(cupon.getIDCUPON());
    
                                    for(CUPONDET item : detailCupon)
                                    {
                                        item.setPAGADO(1);
                                        item.setFECHASTATUSWP(new java.sql.Timestamp(FechaActual.getTime()));
                                        item.setNOTARJETA(cardNumber);
                                        item.setTIPOTARJETA(cardType);
                                    }
    
                                    cuponinterface.ActualizarRegistroCupon(cupon, detailCupon);

                                    cuponinterface.EnviarMailVendedor(cupon, detailCupon);

                                    for(CUPONDET cupondet : detailCupon)
                                    {
                                        //  AQUI ME QUEDE, SUERTE TOÑITO NO MUERAS 

                                        //
                                        //  PolizaECC
                                        //
                                        //  GuestRequestsSOAPRequest
                                        //
                                    }
                                }
                            }                            
                            else
                            {
                                resp.error = true;
                                resp.mensaje = "Sin respuesta de silice, favor de comunicarse con soporte";
                            }
                        }   

                    }
                    else
                    {
                        resp.mensaje = "No se pudo generar la orden de pago, favor de comunicarse con soporte";
                    }
                }
                else
                {
                    resp.error = true;
                    resp.mensaje = "No se pudo generar el detalle de la orden de pago, favor de comunicarse con soporte";
                }

            }
            else
            {
                resp.mensaje = "No se pudo tokenizar la tarjeta, favor de comunicarse con soporte";
            }
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.data = e.getMessage();            
            resp.mensaje = "Ocurrio un error";
            System.err.println(e.getMessage());
        }

        return resp;
    }

    @RequestMapping(value = "gracias")
    public ModelAndView gracias()
    {
        ModelAndView vista = new ModelAndView("gracias");

        return vista;
    }
    @RequestMapping(value = "no-foud")
    public ModelAndView nofound()
    {
        ModelAndView vista = new ModelAndView("nofound");

        return vista;
    }
}

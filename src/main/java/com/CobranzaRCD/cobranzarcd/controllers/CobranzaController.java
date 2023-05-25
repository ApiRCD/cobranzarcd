package com.CobranzaRCD.cobranzarcd.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

import org.json.JSONObject;
import com.google.gson.Gson;
import com.CobranzaRCD.cobranzarcd.clases.Silice;
import com.CobranzaRCD.cobranzarcd.clases.Respuesta;
import com.CobranzaRCD.cobranzarcd.clases.TarjetaTokenizada;
import com.CobranzaRCD.cobranzarcd.clases.Cliente;
import com.CobranzaRCD.cobranzarcd.clases.OrdenPago;
import com.CobranzaRCD.cobranzarcd.clases.DebitoDirecto;


@RestController
public class CobranzaController {
    @RequestMapping(value = "")
    public ModelAndView home(@RequestParam String modulo, @RequestParam String reference)
    {
        ModelAndView vista = new ModelAndView("IndexCobranza");
        UUID uuid = UUID.randomUUID();
        vista.addObject("token", uuid.toString());
        
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
        //DebitoDirecto debito = new DebitoDirecto();
        String card = "";
        String ordenid= "";

        try{
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

                OrdenPago orden = gson.fromJson(OrdenPago, OrdenPago.class);
                String ordenjson = gson.toJson(orden);
                resp = silice.CrearOrdenPago(ordenjson);

                if(resp.error == false)
                {
                    //      EJEMPLO RESPUESTA ORDEN DE PAGO
                    //  "{"url":"https://qa.upayment.app//orden/t8TvUorTjMRN","sessionId":"t8TvUorTjMRN","reciboId":"646bdba3eed446f7e438516d"}"
                    JSONObject jsonobj2 = new JSONObject(resp.data);
                    ordenid = jsonobj2.getString("reciboId");                    
                    
                    //      PENDIENTE REALIZAR COBRO CAMBIAR EL IDPRODUCTO
                    //String jsondebito = gson.toJson(debito);
                    String jsondebito = "{\"card\": {\"token\":\""+card+"\"},\"order\": {\"reciboId\": \""+ordenid+"\"}}";
                    resp = silice.CobroDebitoDirecto(jsondebito);                    
                }
                else
                {
                    resp.mensaje = "No se pudo generar la orden de pago";
                }
            }
            else
            {
                resp.mensaje = "No se pudo tokenizar la tarjeta";
            }
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.data = e.getMessage();
        }

        return resp;
    }










    @RequestMapping(value = "tokenizartarjeta", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Respuesta TokenizarTarjeta(@RequestParam String data0, @RequestParam String email, @RequestParam String name, @RequestParam String phone)    
    {
        Respuesta resp = new Respuesta();
        try{
            Gson gson = new Gson();
            Silice silice = new Silice();
            TarjetaTokenizada tk = new TarjetaTokenizada();
            Cliente client = new Cliente();
            client.name = name;
            client.email = email;
            client.phone = phone;
            tk.data0 = data0;
            tk.client = client;
            String jsonparse = gson.toJson(tk);

            resp = silice.TokenizarTarjeta(jsonparse);            
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.mensaje = e.getMessage();
        }
        return resp;
    }

    @RequestMapping(value = "generarlinkpago", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Respuesta ConsultaPrueba(@RequestParam String objson)    
    {
        Respuesta resp = new Respuesta();
        Silice silice = new Silice();
        Gson gson = new Gson();
        try{
            OrdenPago orden = gson.fromJson(objson, OrdenPago.class);
            String ordenjson = gson.toJson(orden);
            resp = silice.CrearOrdenPago(ordenjson);
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.mensaje = e.getMessage();
        }

        return resp;
    }
}

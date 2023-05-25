package com.CobranzaRCD.cobranzarcd.clases;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import com.google.gson.Gson;
import org.json.JSONObject;


public class Silice {

    private String api_token_dev = "https://api-qa.dspayment.zone/api/v2/auth/signin";
    private String api_clave_cifrado = "https://api-qa.dspayment.zone/api/v2/user/genpwdcryto";
    private String api_clave_tokenizar = "https://api-qa.dspayment.zone/api/v2/tarjetas-dsp/tokenize";
    private String api_shopping_card = "https://api-qa.dspayment.zone/api/v2/recibo/shopping_car";
    private String api_debito_directo = "https://api-qa.dspayment.zone/api/v2/transaction/debitTokenRecibo";
    private String username_dev = "RCD_SevQA";
    private String password_dev = "Silice2023";

    public String ObtenerToken(){
        String token = "";

        try{
            URL url = new URL(this.api_token_dev);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            TokenParams obj = new TokenParams();
            obj.username = this.username_dev;
            obj.password = this.password_dev;

            Gson gson = new Gson();
            String jsonString =gson.toJson(obj);
            
            try(OutputStream os = conn.getOutputStream()){
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0 , input.length);
            }
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))){
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }

                JSONObject jobj = new JSONObject(response.toString());
                JSONObject objdatastr = jobj.getJSONObject("data");
                token = (String)objdatastr.get("token");
                
            }
        }
        catch(Exception e)
        {
             System.err.println(e.getMessage());
        }
        return token; 
    }

    public String ObtenerClaveCifrado()
    {
        String clave = "";
        String token = ObtenerToken();
        try{
            URL url = new URL(this.api_clave_cifrado);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "aplicaction/json");
            conn.setRequestProperty("Authorization", "bearer "+token);
            conn.setDoOutput(true);

            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))){
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }
                JSONObject jobj = new JSONObject(response.toString());
                clave = (String)jobj.get("data");
            }
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }

        return clave;
    }
    public Respuesta TokenizarTarjeta(String datos){
        Respuesta resp = new Respuesta();
        String token = ObtenerToken();
        try{
            URL url = new URL(this.api_clave_tokenizar);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Authorization", "bearer "+token);
            conn.setDoOutput(true);

            try(OutputStream os = conn.getOutputStream())
            {
                byte[] input = datos.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))){
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());                    
                }

                resp.data = response.toString();
            }
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.data = e.getMessage();
        }

        return resp;
    }

    public Respuesta CrearOrdenPago(String orden)
    {
        Respuesta resp = new Respuesta();
        String token = ObtenerToken();

        try{
            URL url = new URL(this.api_shopping_card);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "bearer "+token);
            conn.setDoOutput(true);

            try(OutputStream os = conn.getOutputStream())
            {
                byte[] input = orden.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br  = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8")))
            {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }

                resp.data = response.toString();            
            }

        }
        catch(Exception e)
        {
            resp.error = true;
            resp.data = e.getMessage();
        }

        return resp;
    }

    public Respuesta CobroDebitoDirecto(String debitodirecto)
    {
        Respuesta resp = new Respuesta();
        String token = ObtenerToken();

        try{
            URL url = new URL(this.api_debito_directo);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "bearer "+token);
            conn.setDoOutput(true);

            try(OutputStream os = conn.getOutputStream())
            {
                byte[] input = debitodirecto.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8")))
            {
                StringBuilder response = new StringBuilder();
                String responseLine = null;

                while((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }
                resp.data = response.toString();
            }
            
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.data = e.getMessage();
        }

        return resp;
    }
}

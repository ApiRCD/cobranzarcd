package com.CobranzaRCD.cobranzarcd.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.CobranzaRCD.cobranzarcd.clases.Respuesta;
import com.google.gson.Gson;

@Component
public class GenericUtils {
    private String api_email = "https://api.rcdhotels.com/MiddleWare/api/ApiEmail/PostEmail";
    private String api_log = "https://api.rcdservices.io/api/ApiGenericos/CreateLog";
    private String api_token = "https://api.rcdservices.io/api/Token";

    public Respuesta GuardarLogError(String sistema, int idRegistro, String webService, String peticion, String respuesta, String tabla)
    {
        Respuesta resp = new Respuesta();
        String token = ObtenerToken();

        try{
            String params = "{"
						+ "\"Sistema\":\"" + sistema + "\","
						+ "\"IdRegistro\":" + idRegistro + ","
						+ "\"WebService\":\"" + webService + "\","
						+ "\"Peticion\":\"" + peticion + "\","
						+ "\"Respuesta\":\"" + respuesta + "\","
						+ "\"Tabla\":\"" + tabla + "\""
						+ "}";
            URL url = new URL(this.api_log);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "apllication/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("verify", "false");
            con.setRequestProperty("Authorization", "bearer "+token);
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream())
            {
                byte[] input = params.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8")))
            {
                StringBuilder response = new StringBuilder();
                String responseLine = null;

                while((responseLine = br.readLine()) != null)
                {
                    response.append((responseLine.trim()));
                }

                JSONObject jsonobject = new JSONObject(response.toString());

                if(jsonobject.getBoolean("error") != false)
                {
                    resp.error = true;
                    resp.mensaje = "Error del middleware";
                }
            }
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.mensaje = e.getMessage();
        }

        return resp;
    }

    public Respuesta EnviarEmailMiddleware(String sistema, String from, String to, String sendername, String subject, String body, String adjuntos)
    {        
        Respuesta resp = new Respuesta();
        String token = ObtenerToken();

        try{
            String params = "{"
						+ "\"Sistema\":\"" + sistema + "\","
						+ "\"From\":\"" + from + "\","
						+ "\"To\":[\"" + to + "\"],"
						+ "\"SenderName\":\"" + sendername + "\","
						+ "\"Subject\":\"" + subject + "\","
						+ "\"Body\":\"" + body + "\","
						+ "\"Adjuntos\":[" + adjuntos + "]"
						+ "}";

            URL url = new URL(this.api_email);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("verify", "false");
            con.setRequestProperty("Authorization", "bearer "+token);
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream())
            {
                byte[] input = params.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "urf-8")))
            {
                StringBuilder response =  new StringBuilder();
                String responseLine = null;

                while((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }

                JSONObject jobj = new JSONObject(response.toString());

                if(jobj.getBoolean("errror") != false)
                {
                    resp.error = true;
                    resp.mensaje = "Error en el middleware al enviar el correo";
                }
            }
        }
        catch(Exception e)
        {
            resp.error = true;
            resp.mensaje = e.getMessage();
        }
        
        return resp;
    }








    public String ObtenerToken()
    {
        String token = "";
        try{
            URL url = new URL(this.api_token);
            TokenParams request = new TokenParams();
            request.usuario = "programacionAPI";
            request.password = "123";
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept","application/json");
            con.setDoOutput(true);

            Gson gson = new Gson();
            String jsonString = gson.toJson(request);
            try(OutputStream os = con.getOutputStream())
            {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br  = new  BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8")))
            {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }

                JSONObject jsonObj = new JSONObject(response.toString());
                token = jsonObj.getString("token");
            }
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
        return token;
    }
}

class TokenParams{
    public String usuario;
    public String password;
}

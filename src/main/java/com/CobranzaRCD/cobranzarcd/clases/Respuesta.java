package com.CobranzaRCD.cobranzarcd.clases;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class Respuesta {
    public boolean error;
    public String mensaje;
    public JSONObject jsonobject;
    public String data;
}

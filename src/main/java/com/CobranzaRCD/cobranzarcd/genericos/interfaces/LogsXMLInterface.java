package com.CobranzaRCD.cobranzarcd.genericos.interfaces;

import com.CobranzaRCD.cobranzarcd.genericos.clases.LogsXML;

public interface LogsXMLInterface {
    String GuardarLog(LogsXML log);

    LogsXML ObtenerLog(int idLog);
}

package com.CobranzaRCD.cobranzarcd.genericos.models;

import com.CobranzaRCD.cobranzarcd.genericos.interfaces.LogsXMLInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.CobranzaRCD.cobranzarcd.genericos.clases.LogsXML;

@Repository
@Transactional
public class _LogsXML implements LogsXMLInterface{
    @Autowired
    private EntityManager entitymanager;

    public String GuardarLog(LogsXML log)
    {
        return "";
    }

    public LogsXML ObtenerLog(int idLog)
    {
        return entitymanager.find(LogsXML.class, idLog);
        // String consulta = "FROM LogsXML WHERE idLogsXML = "+ idLog;
        // return (LogsXML) entitymanager.createQuery(consulta).getSingleResult();
    }
}

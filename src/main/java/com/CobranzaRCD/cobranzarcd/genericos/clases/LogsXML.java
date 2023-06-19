package com.CobranzaRCD.cobranzarcd.genericos.clases;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "LogsXML", schema = "GENERICOS")
public class LogsXML {
    @Id
    private int idLogsXML;
    private Date FechaRegistro;
    private String IdSistema;
    private int IdRegistro;
    private String WebService;
    private String Peticion;
    private String Respuesta;
    private String Tabla;
}

package com.CobranzaRCD.cobranzarcd.clases;

import javax.persistence.Id;
import lombok.Data;
import javax.persistence.Table;
import javax.persistence.Entity;

@Data @Entity @Table(name = "DATOSSILICE")
public class DatosSilice {
    @Id
    private int IDDATOSSILICE;
    private int IDREGISTRO;
    private String SISTEMA;
    private String TOKENTARJETA;
    private String COMPANY;
    private String NUMCONFIRM;
    private String NUMRESERVATION;
    private String IDHOTEL;
}

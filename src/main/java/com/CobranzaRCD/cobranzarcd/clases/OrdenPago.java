package com.CobranzaRCD.cobranzarcd.clases;

import java.util.ArrayList;

public class OrdenPago {
    public String ordenId;
    public String folio;
    public String nombreCliente;
    public String telefonoCliente;
    public String subtotal;
    public String impuestos;
    public String total;
    public String concepto;
    public String urlReturn;
    public String emailCliente;
    public String documentoCliente;
    public String tipoDocumentoCliente;
    public ArrayList<OrdenPagoDet> items;

    public String sistema;
}

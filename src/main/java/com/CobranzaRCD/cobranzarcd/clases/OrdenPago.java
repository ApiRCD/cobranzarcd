package com.CobranzaRCD.cobranzarcd.clases;

import java.util.ArrayList;

public class OrdenPago {
    public String ordenId;
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
    public ArrayList<Items> items;
}

class Items{
    public int cantidad;
    public String producto;
    public double precio;
    public String moneda;
}

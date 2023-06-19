package com.CobranzaRCD.cobranzarcd.cupones.interfaces;

import java.util.ArrayList;
import com.CobranzaRCD.cobranzarcd.clases.DatosSilice;
import com.CobranzaRCD.cobranzarcd.clases.OrdenPago;
import com.CobranzaRCD.cobranzarcd.clases.OrdenPagoDet;
import com.CobranzaRCD.cobranzarcd.clases.Respuesta;

public interface OrdenPagoCuponInterface {
    OrdenPago ObtenerCupon(int reference);

    ArrayList<OrdenPagoDet> ObtenerDetalleCupon(int id);

    Respuesta GuardarDatosSilice(DatosSilice datossilice);
}

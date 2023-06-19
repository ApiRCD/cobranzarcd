package com.CobranzaRCD.cobranzarcd.cupones.interfaces;

import com.CobranzaRCD.cobranzarcd.clases.Respuesta;
import com.CobranzaRCD.cobranzarcd.cupones.clases.CUPON;
import com.CobranzaRCD.cobranzarcd.cupones.clases.CUPONDET;
import com.CobranzaRCD.cobranzarcd.cupones.clases.TIPOCERTIFICADO;

import java.util.List;

public interface CuponInterface {
    CUPON ObtenerPorId(int idCupon);

    List<CUPONDET> ObtenerDetallePorCupon(int idCupon);

    Respuesta ActualizarRegistroCupon(CUPON cupon, List<CUPONDET> detalle);

    Respuesta EnviarMailVendedor(CUPON registro, List<CUPONDET> detalle);

    CUPONDET ObtenerDetallePorIdDet(int idDetalle);
    
    TIPOCERTIFICADO ObtenerPorClve(String clave);
}

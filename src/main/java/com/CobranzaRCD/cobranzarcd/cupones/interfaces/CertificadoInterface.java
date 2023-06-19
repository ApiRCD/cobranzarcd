package com.CobranzaRCD.cobranzarcd.cupones.interfaces;

import com.CobranzaRCD.cobranzarcd.cupones.clases.CERTIFICADOS;

public interface CertificadoInterface {
    CERTIFICADOS ObtenerPorClave(String claveCert);
}

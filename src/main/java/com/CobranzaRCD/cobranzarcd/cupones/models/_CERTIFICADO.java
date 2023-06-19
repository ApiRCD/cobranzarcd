package com.CobranzaRCD.cobranzarcd.cupones.models;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.CobranzaRCD.cobranzarcd.cupones.clases.CERTIFICADOS;
import com.CobranzaRCD.cobranzarcd.cupones.interfaces.CertificadoInterface;

import lombok.RequiredArgsConstructor;

@Repository
@Transactional
@RequiredArgsConstructor
@EnableTransactionManagement
public class _CERTIFICADO implements CertificadoInterface{

    @Autowired
    private EntityManager entityManager;

    public CERTIFICADOS ObtenerPorClave(String claveCert)
    {
        CERTIFICADOS certificado = new CERTIFICADOS();

        try{
            certificado = entityManager.createQuery("FROM CERTIFICADOS WHERE CLAVECERT = "+claveCert,CERTIFICADOS.class).getSingleResult();
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }

        return certificado;
    }
    
}

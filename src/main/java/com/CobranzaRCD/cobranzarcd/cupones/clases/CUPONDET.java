package com.CobranzaRCD.cobranzarcd.cupones.clases;

import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;

import java.sql.Date;

import javax.persistence.Entity;

@Data
@Entity
@Table(name = "CUPONDET")
public class CUPONDET {
    @Id
    private int IDCUPONDET;
    private int IDCUPON;
    private String CLAVECERT;
    private double COSTO;    
    private String MONEDA;   
    private String PROPIEDAD;
}

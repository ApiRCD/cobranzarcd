package com.CobranzaRCD.cobranzarcd.cupones.clases;

import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import java.sql.Date;
import java.sql.Timestamp;


@Data
@Entity
@Table(name = "CUPONDET", schema = "Cupones")
public class CUPONDET {
    @Id
    private int IDCUPONDET;
    private int IDCUPON;
    private String NORESERVA;
    private String PROPIEDAD;
    private double COSTO;    
    private double IMPORTECOBRARWP;
    private Date FECHAVIAJE;
    private Date FECHAVENTA;
    private String URLRESPWP;
    private String MONEDA;   
    private String TIPOCERT;
    private int PAGADO;
    private String STATUSPAGO;
    private String CLAVECERT;
    private String STATUSSAPDESCRIPCION;
    private Date FECHASTATUSSAP;
    private String NOPOLIZA;
    private int STATUSSAP;
    private String CERTEXTENSION;
    private String USUARIOMODIFICA;
    private Date FECHAMODIF;
    private int ACTIVO;
    private int COMISION1;
    private double COMISION1IMPORTE;
    private int COMISION2;
    private double COMISION2IMPORTE;
    private int COMISION3;
    private double COMISION3IMPORTE;
    private double COMISION1PORC;
    private double COMISION2PORC;
    private double COMISION3PORC;
    private int IDCUPONDETCAMBIO;
    private String FORMAPAGO;
    private String NOCONFIRMACIONTERMINAL;
    private String ORDERCODE;
    private int DEVOLUCION;
    private Date FECHADEVOLUCION;
    private String NOTARJETA;
    private String TIPOTARJETA;
    private Timestamp FECHASTATUSWP;
}

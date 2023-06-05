package com.CobranzaRCD.cobranzarcd.cupones.clases;
import javax.persistence.Id;
import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CUPON")
@Data
public class CUPON {
    @Id
    private int IDCUPON;
    private String FOLIO;
    private String CLIENTENOMBRE;
    private String CLIENTEAPELLIDOS;
    private String CLIENTEEMAIL;
    private String DEPTOVENTA;
    private String AGENTEVENDEDOR;
    private double COSTO;
    private Date FECHAVENTA;
    private String URLRESPWP;
    private String MONEDA;
    private String NOMEMBRESIA;
    private Integer PAGADO;
    private Integer STATUSPAGO;
    private String STATUSSAPDESCRIPCION;
    private Date FECHASTATUSSAP;
    private String AUTHORIZATIONID;
    private String MERCHANTCODE;
    private String NOPOLIZA;
    private String TELEFONO;
    private String CIUDAD;
    private String PAIS;
    private String TELEFONO2;
    private String CLIENTEEMAIL2;
    private int ACEPTATERMINOS;
    private String USUARIOMODIFICA;
    private Date FECHAMODIF;
    private int ACTIVO;
    private String NOTARJETA;
    private String TIPOTARJETA;
    private Date FECHACAPTURED;
    private String MEMBRESIACANC;
    private int EDAD;
    private String LUGARVENTA;
    private String SALAVENTA;
}

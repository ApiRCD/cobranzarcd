package com.CobranzaRCD.cobranzarcd.cupones.clases;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "CERTIFICADOS", schema = "Cupones")
public class CERTIFICADOS {
    @Id
    private int IDCERTIFICADO;
	private String TIPOCERT;
	private String DESCRIPCION;
	private String DESCRIPCIONEN;
	private String DESCRIPCIONPT;
	private double COSTO;
	private String CLAVECERT;
	private int GENERAPOLIZA;
	private int EXTENCION;
	private double COMISION1PORC;
	private double COMISION2PORC;
	private double COMISION3PORC;
	private int ACTIVO;
}

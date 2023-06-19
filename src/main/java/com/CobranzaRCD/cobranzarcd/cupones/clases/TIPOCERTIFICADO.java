package com.CobranzaRCD.cobranzarcd.cupones.clases;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "TIPOCERTIFICADO", schema = "Cupones")
public class TIPOCERTIFICADO {
	@Id
    private int IDTIPOCERTIFICADO;
	private String CLAVE;
	private String DESCRIPCION;
	private String DEPTO;
	private int ACTIVO;
	private int ESREGALO;
	private String MODULO;
	private String TERMINOS;
	private String TIPODOC;
	private String ENCABEZADOPOLIZA;
	private String IDMONEDA;
	private String CUENTAINTERCENTRO;
	private String SOCIEDAD;
	private String CUSTOMERCARGO;
	private String CUSTOMERABONO;
	private String VIAPAGO;
	private String INDICADOROPERACION;
	private int GENERARPOLIZA;
	// public String CodigoMoneda;
}

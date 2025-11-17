package org.example.patron_de_diseno.creacional.Builder;

import java.math.BigDecimal;

/**
 * Representa un descuento aplicado a la factura.
 * Puede ser:
 *  - monto fijo (montoFijo != null), o
 *  - porcentaje (porcentaje != null)
 */
class DescuentoFactura {

    private final BigDecimal montoFijo;
    private final String motivo;
    private final BigDecimal porcentaje;

    DescuentoFactura(BigDecimal montoFijo, String motivo) {
        this.montoFijo = montoFijo;
        this.motivo = motivo;
        this.porcentaje = null;
    }

    DescuentoFactura(BigDecimal montoFijo, String motivo, BigDecimal porcentaje) {
        this.montoFijo = montoFijo;
        this.motivo = motivo;
        this.porcentaje = porcentaje;
    }

    BigDecimal getMontoFijo() {
        return montoFijo;
    }

    BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public String getMotivo() {
        return motivo;
    }
}

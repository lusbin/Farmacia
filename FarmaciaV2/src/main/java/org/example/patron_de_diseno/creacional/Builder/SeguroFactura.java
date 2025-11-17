package org.example.patron_de_diseno.creacional.Builder;

import java.math.BigDecimal;

/**
 * Representa un seguro adicional cargado a la factura.
 */
class SeguroFactura {

    private final String tipo;
    private final BigDecimal monto;

    SeguroFactura(String tipo, BigDecimal monto) {
        this.tipo = tipo;
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}

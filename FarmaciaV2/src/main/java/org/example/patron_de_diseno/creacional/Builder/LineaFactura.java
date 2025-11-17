package org.example.patron_de_diseno.creacional.Builder;

import org.example.model.Producto;

import java.math.BigDecimal;

class LineaFactura {

    private final Producto producto;
    private final int cantidad;
    private final BigDecimal precioUnitario;

    LineaFactura(Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public Producto getProducto() {
        return producto;
    }
}

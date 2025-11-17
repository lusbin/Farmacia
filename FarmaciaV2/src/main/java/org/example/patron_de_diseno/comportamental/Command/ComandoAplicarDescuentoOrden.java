package org.example.patron_de_diseno.comportamental.Command;

import org.example.model.Orden;

import java.math.BigDecimal;

/**
 * Command que aplica un descuento porcentual a la orden.
 * Deshacer = restaurar totalNeto anterior.
 */
public class ComandoAplicarDescuentoOrden implements ComandoPuntoVenta {

    private final Orden orden;
    private final BigDecimal porcentaje; // ej: 0.10 = 10%

    private BigDecimal totalNetoAnterior;

    public ComandoAplicarDescuentoOrden(Orden orden,
                                        BigDecimal porcentaje) {
        this.orden = orden;
        this.porcentaje = porcentaje;
    }

    @Override
    public void ejecutar() {
        if (orden.getTotalNeto() == null) {
            return;
        }
        totalNetoAnterior = orden.getTotalNeto();

        BigDecimal factor = BigDecimal.ONE.subtract(porcentaje);
        BigDecimal nuevoTotal = totalNetoAnterior.multiply(factor);

        orden.setTotalNeto(nuevoTotal);
    }

    @Override
    public void deshacer() {
        if (totalNetoAnterior != null) {
            orden.setTotalNeto(totalNetoAnterior);
        }
    }

    @Override
    public String getNombre() {
        return "Aplicar descuento " + porcentaje.multiply(new BigDecimal("100")) + "%";
    }
}

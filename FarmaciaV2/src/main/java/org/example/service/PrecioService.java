package org.example.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PrecioService {

    public BigDecimal calcularSubtotal(BigDecimal precioUnitario, int cantidad) {
        validarPrecio(precioUnitario);
        validarCantidad(cantidad);
        return precioUnitario
                .multiply(new BigDecimal(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularIVA(BigDecimal base, BigDecimal porcentaje) {
        validarBase(base);
        validarPorcentaje(porcentaje);
        return base
                .multiply(porcentaje)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /** Conveniencia: devuelve subtotal, iva y total en un solo cálculo */
    public Totales calcularTotales(BigDecimal precioUnitario, int cantidad, BigDecimal ivaPorcentaje) {
        BigDecimal subtotal = calcularSubtotal(precioUnitario, cantidad);
        BigDecimal iva      = calcularIVA(subtotal, ivaPorcentaje);
        BigDecimal total    = subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);
        return new Totales(subtotal, iva, total);
    }

    // ---------- helpers/validaciones ----------
    private void validarPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() < 0)
            throw new IllegalArgumentException("precio inválido");
    }
    private void validarCantidad(int cantidad) {
        if (cantidad < 0)
            throw new IllegalArgumentException("cantidad inválida");
    }
    private void validarBase(BigDecimal base) {
        if (base == null || base.signum() < 0)
            throw new IllegalArgumentException("base inválida");
    }
    private void validarPorcentaje(BigDecimal porcentaje) {
        if (porcentaje == null || porcentaje.signum() < 0)
            throw new IllegalArgumentException("porcentaje inválido");
    }

    /** DTO simple para devolver totales */
    public record Totales(BigDecimal subtotal, BigDecimal iva, BigDecimal total) {}
}

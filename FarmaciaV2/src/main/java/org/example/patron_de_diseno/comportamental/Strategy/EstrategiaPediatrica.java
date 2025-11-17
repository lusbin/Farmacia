package org.example.patron_de_diseno.comportamental.Strategy;

import org.example.model.Paciente;
import org.example.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class EstrategiaPediatrica implements EstrategiaContextoClinico {

    private int calcularEdad(Paciente paciente) {
        if (paciente == null || paciente.getFechaNacimiento() == null) {
            return 0;
        }
        return Period.between(paciente.getFechaNacimiento(), LocalDate.now()).getYears();
    }

    @Override
    public BigDecimal calcularPrecioUnitario(Producto producto,
                                             BigDecimal precioBase,
                                             String canalVenta,
                                             Paciente paciente) {

        if (precioBase == null) return BigDecimal.ZERO;

        // Descuento pediátrico: 10% en mostrador, 15% online
        BigDecimal factorDescuento =
                "ONLINE".equalsIgnoreCase(canalVenta) ? new BigDecimal("0.85")
                        : new BigDecimal("0.90");

        return precioBase.multiply(factorDescuento);
    }

    @Override
    public String ajustarDosis(Producto producto,
                               String dosisBase,
                               Paciente paciente) {

        int edad = calcularEdad(paciente);
        // Regla simple: niños muy pequeños -> 50% de la dosis
        String sufijo = " (ajustada pediátrica)";
        if (dosisBase == null) return "Dosis pediátrica no definida";

        if (edad <= 5 && dosisBase.contains("mg")) {
            // No nos complicamos parseando el número: solo documentamos el ajuste
            return dosisBase + " (aprox. 50% de la dosis adulta)" + sufijo;
        }

        return dosisBase + sufijo;
    }
}

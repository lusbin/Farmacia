package org.example.patron_de_diseno.comportamental.Strategy;

import org.example.model.Paciente;
import org.example.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class EstrategiaGeriatrica implements EstrategiaContextoClinico {

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

        // Política ejemplo: 5% de descuento general a adultos mayores
        BigDecimal factorDescuento = new BigDecimal("0.95");
        return precioBase.multiply(factorDescuento);
    }

    @Override
    public String ajustarDosis(Producto producto,
                               String dosisBase,
                               Paciente paciente) {

        int edad = calcularEdad(paciente);
        String sufijo = " (ajustada geriátrica)";
        if (dosisBase == null) return "Dosis geriátrica no definida";

        if (edad >= 75 && dosisBase.contains("mg")) {
            return dosisBase + " (considerar reducción 25% y vigilar función renal)" + sufijo;
        }

        return dosisBase + sufijo;
    }
}

package org.example.patron_de_diseno.comportamental.Strategy;

import org.example.model.Paciente;
import org.example.model.Producto;

import java.math.BigDecimal;

public class EstrategiaAdulto implements EstrategiaContextoClinico {

    @Override
    public BigDecimal calcularPrecioUnitario(Producto producto,
                                             BigDecimal precioBase,
                                             String canalVenta,
                                             Paciente paciente) {

        if (precioBase == null) return BigDecimal.ZERO;

        // Ejemplo: online tiene 5% descuento; mostrador precio base
        if ("ONLINE".equalsIgnoreCase(canalVenta)) {
            return precioBase.multiply(new BigDecimal("0.95"));
        }
        return precioBase;
    }

    @Override
    public String ajustarDosis(Producto producto,
                               String dosisBase,
                               Paciente paciente) {

        // Adulto: mantenemos dosis base
        return dosisBase != null ? dosisBase : "Dosis no definida";
    }
}

package org.example.patron_de_diseno.comportamental.Strategy;

import org.example.model.Paciente;
import org.example.model.Producto;

import java.math.BigDecimal;

public interface EstrategiaContextoClinico {

    /**
     * Calcula el precio unitario final según canal de venta
     * (MOSTRADOR, ONLINE, DOMICILIO, etc.) y tipo de paciente.
     */
    BigDecimal calcularPrecioUnitario(Producto producto,
                                      BigDecimal precioBase,
                                      String canalVenta,
                                      Paciente paciente);

    /**
     * Ajusta la dosis base (ej: "500 mg cada 8h") según
     * edad / contexto del paciente.
     */
    String ajustarDosis(Producto producto,
                        String dosisBase,
                        Paciente paciente);
}

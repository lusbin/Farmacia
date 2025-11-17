package org.example.unit.comportamental.Strategy;


import org.example.model.Paciente;
import org.example.model.Producto;
import org.example.patron_de_diseno.comportamental.Strategy.EstrategiaAdulto;
import org.example.patron_de_diseno.comportamental.Strategy.EstrategiaContextoClinico;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para EstrategiaAdulto.
 * Se cubren:
 *  - precioBase == null  -> retorna BigDecimal.ZERO
 *  - canal ONLINE        -> aplica 5% descuento
 *  - otros canales       -> precio sin cambiar
 *  - ajustarDosis con y sin dosisBase
 */
class EstrategiaAdultoTest {

    private final EstrategiaContextoClinico estrategia = new EstrategiaAdulto();

    @Test
    void calcularPrecioUnitario_precioBaseNull_devuelveZero() {
        // given
        Producto producto = null;
        BigDecimal precioBase = null;
        String canal = "ONLINE";
        Paciente paciente = null;

        // when
        BigDecimal resultado = estrategia.calcularPrecioUnitario(
                producto, precioBase, canal, paciente
        );

        // then
        assertThat(resultado).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calcularPrecioUnitario_online_aplicaDescuento5Porciento() {
        // given
        Producto producto = new Producto();
        BigDecimal precioBase = new BigDecimal("100.00");
        String canal = "ONLINE";
        Paciente paciente = new Paciente();

        // when
        BigDecimal resultado = estrategia.calcularPrecioUnitario(
                producto, precioBase, canal, paciente
        );

        // then: 100 * 0.95 = 95.00
        assertThat(resultado).isEqualByComparingTo("95.00");
    }

    @Test
    void calcularPrecioUnitario_otroCanal_devuelvePrecioBaseSinCambios() {
        // given
        Producto producto = new Producto();
        BigDecimal precioBase = new BigDecimal("80.00");
        String canal = "MOSTRADOR"; // no es ONLINE
        Paciente paciente = new Paciente();

        // when
        BigDecimal resultado = estrategia.calcularPrecioUnitario(
                producto, precioBase, canal, paciente
        );

        // then: debe ser exactamente el mismo valor
        assertThat(resultado).isEqualByComparingTo(precioBase);
    }

    @Test
    void ajustarDosis_conDosisBase_laMantiene() {
        // given
        Producto producto = new Producto();
        Paciente paciente = new Paciente();
        String dosisBase = "500 mg cada 8h";

        // when
        String dosisResult = estrategia.ajustarDosis(producto, dosisBase, paciente);

        // then
        assertThat(dosisResult).isEqualTo("500 mg cada 8h");
    }

    @Test
    void ajustarDosis_sinDosisBase_devuelveMensajePorDefecto() {
        // given
        Producto producto = new Producto();
        Paciente paciente = new Paciente();
        String dosisBase = null;

        // when
        String dosisResult = estrategia.ajustarDosis(producto, dosisBase, paciente);

        // then
        assertThat(dosisResult).isEqualTo("Dosis no definida");
    }
}
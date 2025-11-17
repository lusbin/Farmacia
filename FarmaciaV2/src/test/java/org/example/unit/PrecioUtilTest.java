package org.example.unit;

import org.example.service.PrecioService;
import org.example.service.PrecioService.Totales;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;


/*Valida todo los metodos de la capa services de la clase PrecioServices*/


class PrecioServiceTest {

    // Instancia del servicio sin Spring: prueba unitaria pura (solo lógica)
    private final PrecioService service = new PrecioService();

    @Test
    void subtotal_ok() {
        // Verifica que el subtotal = precio * cantidad se calcule correctamente
        assertThat(service.calcularSubtotal(new BigDecimal("12.30"), 3))
                .isEqualByComparingTo("36.90");
    }

    @Test
    void iva_ok() {
        // Verifica el cálculo del IVA: base * (porcentaje / 100)
        assertThat(service.calcularIVA(new BigDecimal("100.00"), new BigDecimal("19")))
                .isEqualByComparingTo("19.00");
    }

    @Test
    void totales_ok() {
        // Calcula subtotal + IVA y valida todos los valores retornados
        Totales t = service.calcularTotales(new BigDecimal("10.00"), 2, new BigDecimal("19"));
        assertThat(t.subtotal()).isEqualByComparingTo("20.00");
        assertThat(t.iva()).isEqualByComparingTo("3.80");
        assertThat(t.total()).isEqualByComparingTo("23.80");
    }

    @Test
    void errores_validaciones() {
        // Validaciones de entrada: deben lanzar IllegalArgumentException con mensajes específicos

        assertThatThrownBy(() -> service.calcularSubtotal(new BigDecimal("-1"), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");

        assertThatThrownBy(() -> service.calcularSubtotal(new BigDecimal("1"), -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cantidad");

        assertThatThrownBy(() -> service.calcularIVA(new BigDecimal("-1"), new BigDecimal("19")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("base");

        assertThatThrownBy(() -> service.calcularIVA(new BigDecimal("1"), new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("porcentaje");
    }
}


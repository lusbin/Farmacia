package org.example.unit.creacional.builder;


import org.example.model.Factura;
import org.example.model.Orden;
import org.example.model.Producto;
import org.example.patron_de_diseno.creacional.Builder.FacturaBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class FacturaBuilderTest {

    private Producto buildProducto(String nombre, BigDecimal iva) {
        return Producto.builder()
                .id(1L)
                .nombre(nombre)
                .ivaPorcentaje(iva)
                .build();
    }

    private Orden buildOrden() {
        Orden orden = new Orden();
        orden.setId(10L);
        return orden;
    }

    @Test
    void build_sinDescuentosNiSeguros_calculaMontoComoSumaDeLineas() {
        // given
        Orden orden = buildOrden();
        Producto prod1 = buildProducto("Paracetamol", BigDecimal.ZERO);
        Producto prod2 = buildProducto("Ibuprofeno", BigDecimal.ZERO);

        // when
        Factura factura = new FacturaBuilder()
                .conOrden(orden)
                .conFolio("FAC-001")
                // 2 unidades x 10 = 20
                .agregarProducto(prod1, 2, new BigDecimal("10.00"))
                // 1 unidad x 5 = 5
                .agregarProducto(prod2, 1, new BigDecimal("5.00"))
                .build();

        // then
        // subtotal = 20 + 5 = 25
        assertThat(factura.getMonto()).isEqualByComparingTo("25.00");
        assertThat(factura.getOrden()).isEqualTo(orden);
        assertThat(factura.getFolio()).isEqualTo("FAC-001");
        assertThat(factura.getEstado()).isEqualTo("EMITIDA"); // valor por defecto
        assertThat(factura.getFechaEmision()).isNotNull();
    }

    @Test
    void build_conDescuentoFijoYPorcentajeYSeguro_aplicaTodosLosAjustes() {
        // given
        Orden orden = buildOrden();
        Producto producto = buildProducto("Antibiótico", BigDecimal.ZERO);

        // subtotal bruto: 3 x 100 = 300
        // descuento fijo: 20
        // descuento %: 10% de 300 = 30
        // seguros: 5
        // monto final = 300 - 20 - 30 + 5 = 255

        Factura factura = new FacturaBuilder()
                .conOrden(orden)
                .conFolio("FAC-002")
                .agregarProducto(producto, 3, new BigDecimal("100.00"))
                .agregarDescuentoMonto(new BigDecimal("20.00"), "Cupón 20")
                .agregarDescuentoPorcentaje(new BigDecimal("0.10"), "Descuento 10% fidelidad")
                .agregarSeguro("Seguro de envío", new BigDecimal("5.00"))
                .build();

        // then
        assertThat(factura.getMonto()).isEqualByComparingTo("255.00");
    }

    @Test
    void build_sinOrden_lanzaExcepcion() {
        // given: no se configura orden

        FacturaBuilder builder = new FacturaBuilder()
                .conFolio("FAC-003");

        // when / then
        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requiere una orden");
    }

    @Test
    void build_conDescuentosMasAltosQueSubtotal_noGeneraMontoNegativo() {
        // given
        Orden orden = buildOrden();
        Producto producto = buildProducto("Vitamina C", BigDecimal.ZERO);

        // subtotal: 1 x 10 = 10
        // descuentos: 50
        // seguros: 0
        // monto final teórico: -40, pero lo protegimos a 0

        Factura factura = new FacturaBuilder()
                .conOrden(orden)
                .agregarProducto(producto, 1, new BigDecimal("10.00"))
                .agregarDescuentoMonto(new BigDecimal("50.00"), "Cuponazo")
                .build();

        // then
        assertThat(factura.getMonto()).isEqualByComparingTo("0.00");
    }
}
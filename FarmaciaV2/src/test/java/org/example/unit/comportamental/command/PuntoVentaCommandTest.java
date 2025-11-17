package org.example.unit.comportamental.command;


import org.example.model.Orden;
import org.example.model.Producto;
import org.example.patron_de_diseno.comportamental.Command.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PuntoVentaCommandTest {

    private Orden buildOrdenVacia() {
        Orden o = new Orden();
        o.setId(1L);
        o.setCreadoEn(LocalDateTime.now());
        o.setEstado("CREADA");
        return o;
    }

    private Producto buildProducto() {
        Producto p = new Producto();
        p.setId(10L);
        p.setNombre("Paracetamol 500mg");
        return p;
    }

    @Test
    void agregarItemYDeshacer_actualizaYRevierteTotales() {
        // given
        Orden orden = buildOrdenVacia();
        Producto producto = buildProducto();

        PuntoVentaInvoker invoker = new PuntoVentaInvoker();

        ComandoAgregarItemOrden cmdAgregar =
                new ComandoAgregarItemOrden(orden, producto, 2, new BigDecimal("100.00"));

        // when: agregamos ítem
        invoker.ejecutarComando(cmdAgregar);

        // then
        assertThat(orden.getItems()).hasSize(1);
        assertThat(orden.getTotalBruto()).isEqualByComparingTo("200.00");
        assertThat(orden.getTotalNeto()).isEqualByComparingTo("200.00");

        // when: deshacer
        invoker.deshacerUltimo();

        // then
        assertThat(orden.getItems()).isEmpty();
        assertThat(orden.getTotalBruto()).isEqualByComparingTo("0.00");
        assertThat(orden.getTotalNeto()).isEqualByComparingTo("0.00");
    }

    @Test
    void aplicarDescuentoYDeshacer_restauranTotalNeto() {
        // given
        Orden orden = buildOrdenVacia();
        orden.setTotalNeto(new BigDecimal("300.00"));

        PuntoVentaInvoker invoker = new PuntoVentaInvoker();

        ComandoAplicarDescuentoOrden cmdDescuento =
                new ComandoAplicarDescuentoOrden(orden, new BigDecimal("0.10")); // 10%

        // when: aplicamos descuento
        invoker.ejecutarComando(cmdDescuento);

        // then: 300 - 10% = 270
        assertThat(orden.getTotalNeto()).isEqualByComparingTo("270.00");

        // when: deshacer
        invoker.deshacerUltimo();

        // then
        assertThat(orden.getTotalNeto()).isEqualByComparingTo("300.00");
    }


    @Test
    void getNombre_incluyeNombreProducto() {
        Orden orden = buildOrdenVacia();
        Producto producto = buildProducto();

        ComandoAgregarItemOrden cmd =
                new ComandoAgregarItemOrden(orden, producto, 1, new BigDecimal("50.00"));

        String nombre = cmd.getNombre();

        assertThat(nombre)
                .contains("Agregar ítem")
                .contains("Paracetamol 500mg");
    }


    @Test
    void recalcularTotales_conItemsNull_dejaTotalesEnCero() throws Exception {
        Orden orden = buildOrdenVacia();
        orden.setTotalBruto(new BigDecimal("100.00"));
        orden.setTotalImpuestos(new BigDecimal("19.00"));
        orden.setTotalNeto(new BigDecimal("119.00"));

        // command con producto null porque aquí no lo necesitamos
        ComandoAgregarItemOrden cmd =
                new ComandoAgregarItemOrden(orden, null, 0, BigDecimal.ZERO);

        // Forzamos items = null
        orden.setItems(null);

        // Invocar método privado via reflexión
        Method m = ComandoAgregarItemOrden.class.getDeclaredMethod("recalcularTotales");
        m.setAccessible(true);
        m.invoke(cmd);

        assertThat(orden.getTotalBruto()).isEqualByComparingTo("0.00");
        assertThat(orden.getTotalImpuestos()).isEqualByComparingTo("0.00");
        assertThat(orden.getTotalNeto()).isEqualByComparingTo("0.00");
    }

    @Test
    void recalcularTotales_conListaVacia_dejaTotalesEnCero() throws Exception {
        Orden orden = buildOrdenVacia();
        orden.setTotalBruto(new BigDecimal("50.00"));
        orden.setTotalImpuestos(new BigDecimal("5.00"));
        orden.setTotalNeto(new BigDecimal("55.00"));

        ComandoAgregarItemOrden cmd =
                new ComandoAgregarItemOrden(orden, null, 0, BigDecimal.ZERO);

        // items vacía pero no null
        orden.setItems(new ArrayList<>());

        Method m = ComandoAgregarItemOrden.class.getDeclaredMethod("recalcularTotales");
        m.setAccessible(true);
        m.invoke(cmd);

        assertThat(orden.getTotalBruto()).isEqualByComparingTo("0.00");
        assertThat(orden.getTotalImpuestos()).isEqualByComparingTo("0.00");
        assertThat(orden.getTotalNeto()).isEqualByComparingTo("0.00");
    }

}
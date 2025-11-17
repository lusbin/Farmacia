package org.example.e2e;


import org.example.model.Orden;
import org.example.model.OrdenItem;
import org.example.model.Producto;
import org.example.patron_de_diseno.comportamental.Command.ComandoAgregarItemOrden;
import org.example.patron_de_diseno.comportamental.Command.ComandoAplicarDescuentoOrden;
import org.example.patron_de_diseno.comportamental.Command.PuntoVentaInvoker;
import org.example.repository.OrdenRepository;
import org.example.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")   // usa tu H2 de application-test.yml
@Transactional
@Rollback                 // no deja basura en la BD
class PuntoVentaCommandE2EITest {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * E2E:
     * 1) Creamos Producto y Orden en H2.
     * 2) Usamos los Commands para agregar ítem y aplicar descuento.
     * 3) Persistimos la Orden.
     * 4) Volvemos a leer desde la BD y validamos totales e items.
     */
    @Test
    void flujoCompleto_agregarItem_aplicarDescuento_yPersistirEnH2() {
        // ---------- 1. GIVEN: datos reales en BD (H2) -------------------

        // Producto
        Producto producto = Producto.builder()
                .creadoEn(LocalDateTime.now())
                .sku("CMD-001")
                .nombre("Ibuprofeno 400mg")
                .principioActivo("Ibuprofeno")
                .unidad("tableta")
                .presentacion("Caja x 10")
                .ivaPorcentaje(new BigDecimal("19"))
                .esControlado(false)
                .esOtc(true)
                .build();
        producto = productoRepository.save(producto);

        // Orden vacía (sin items todavía)
        Orden orden = new Orden();
        orden.setCreadoEn(LocalDateTime.now());
        orden.setEstado("CREADA");
        orden.setCanal("MOSTRADOR");
        orden.setCreadoPor("CAJERO-1");

        // Inicializamos totales en 0
        orden.setTotalBruto(BigDecimal.ZERO);
        orden.setTotalImpuestos(BigDecimal.ZERO);
        orden.setTotalNeto(BigDecimal.ZERO);

        orden = ordenRepository.save(orden); // persistimos la orden base

        // ---------- 2. WHEN: usamos Commands + Invoker ------------------

        PuntoVentaInvoker invoker = new PuntoVentaInvoker();

        // 2.1 Agregar un ítem: 3 unidades a 50.00 c/u -> totalBruto 150.00
        ComandoAgregarItemOrden cmdAgregar =
                new ComandoAgregarItemOrden(
                        orden,
                        producto,
                        3,
                        new BigDecimal("50.00")
                );

        invoker.ejecutarComando(cmdAgregar);

        // 2.2 Aplicar descuento del 10% sobre el totalNeto de la orden
        ComandoAplicarDescuentoOrden cmdDescuento =
                new ComandoAplicarDescuentoOrden(
                        orden,
                        new BigDecimal("0.10")       // 10%
                );

        invoker.ejecutarComando(cmdDescuento);

        // Persistimos los cambios de la Orden (items + totales) en BD
        orden = ordenRepository.save(orden);

        // ---------- 3. THEN: leemos desde la BD real (H2) ---------------

        Orden ordenDesdeBd = ordenRepository.findById(orden.getId())
                .orElseThrow();

        // Validamos que tenga un item
        List<OrdenItem> items = ordenDesdeBd.getItems();
        assertThat(items).hasSize(1);

        OrdenItem item = items.get(0);
        assertThat(item.getProducto().getId()).isEqualTo(producto.getId());
        assertThat(item.getCantidad()).isEqualTo(3);
        assertThat(item.getPrecioUnitario()).isEqualByComparingTo("50.00");
        assertThat(item.getSubtotal()).isEqualByComparingTo("150.00");

        // Total bruto debe ser 150.00
        assertThat(ordenDesdeBd.getTotalBruto()).isEqualByComparingTo("150.00");

        // Descuento 10% -> totalNeto = 135.00
        assertThat(ordenDesdeBd.getTotalNeto()).isEqualByComparingTo("135.00");

        // Estado y canal se mantienen
        assertThat(ordenDesdeBd.getEstado()).isEqualTo("CREADA");
        assertThat(ordenDesdeBd.getCanal()).isEqualTo("MOSTRADOR");
    }
}
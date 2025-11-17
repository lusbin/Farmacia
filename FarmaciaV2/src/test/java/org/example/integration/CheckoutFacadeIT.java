package org.example.integration;


import org.example.patron_de_diseno.estructural.Facade.dto.CheckoutItemDTO;
import org.example.patron_de_diseno.estructural.Facade.dto.CheckoutRequestDTO;
import org.example.patron_de_diseno.estructural.Facade.dto.CheckoutResultDTO;
import org.example.model.*;
import org.example.patron_de_diseno.estructural.Facade.CheckoutFacade;
import org.example.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapterFactory.TipoPasarela;

@SpringBootTest
@ActiveProfiles("test")   // usa H2 y propiedades de application-test.yml
@Transactional
@Rollback
class CheckoutFacadeIT {

    @Autowired CheckoutFacade checkoutFacade;

    @Autowired PacienteRepository pacienteRepository;
    @Autowired ProductoRepository productoRepository;
    @Autowired LoteRepository loteRepository;
    @Autowired OrdenRepository ordenRepository;
    @Autowired StockMovimientoRepository stockMovimientoRepository;
    @Autowired PagoRepository pagoRepository;
    @Autowired FacturaRepository facturaRepository;

    @Test
    void realizarCheckout_persisteOrdenPagoFacturaYStockEnH2() {
        // 1) GIVEN: datos reales en H2 -----------------------

        // Paciente
        Paciente paciente = Paciente.builder()
                .creadoEn(LocalDateTime.now())
                .docIdentidad("CC-123")
                .nombre("Juan Perez")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .sexo("M")
                .alergias("Ninguna")
                .build();
        paciente = pacienteRepository.save(paciente);

        // Producto
        Producto producto = Producto.builder()
                .creadoEn(LocalDateTime.now())
                .sku("PROD-001")
                .nombre("Paracetamol 500mg")
                .principioActivo("Paracetamol")
                .unidad("tableta")
                .presentacion("Caja x 10")
                .ivaPorcentaje(new BigDecimal("19"))
                .esControlado(false)
                .esOtc(true)
                .build();
        producto = productoRepository.save(producto);

        // Lote
        Lote lote = new Lote();
        lote.setCreadoEn(LocalDateTime.now());
        lote.setProducto(producto);
        lote.setNumeroLote("L-001");
        lote.setFechaVencimiento(LocalDate.now().plusMonths(6));
        lote.setUbicacion("BODEGA-1");
        lote = loteRepository.save(lote);   // ✅ AHORA sí tiene ID

// 2) Armamos el request del Facade -------------------

        CheckoutItemDTO itemDTO = CheckoutItemDTO.builder()
                .productoId(producto.getId())
                .loteId(lote.getId())       // ✅ ahora no es null
                .cantidad(2)
                .precioUnitario(new BigDecimal("100.00"))
                .requiereReceta(false)
                .build();


        CheckoutRequestDTO request = CheckoutRequestDTO.builder()
                .pacienteId(paciente.getId())
                .canal("MOSTRADOR")
                .creadoPor("CAJERO-1")
                .items(List.of(itemDTO))
                .metodoPago("TARJETA")
                .tipoPasarela(TipoPasarela.STRIPE)
                .build();

        // 3) WHEN: ejecutamos el Facade ----------------------

        CheckoutResultDTO result = checkoutFacade.realizarCheckout(request);

        // 4) THEN: validamos en BD real (H2) -----------------

        // Orden
        Orden orden = ordenRepository.findById(result.getOrdenId())
                .orElseThrow();
        assertThat(orden.getPaciente().getId()).isEqualTo(paciente.getId());
        assertThat(orden.getItems()).hasSize(1);
        assertThat(orden.getTotalNeto()).isEqualByComparingTo(result.getTotalNeto());

        // Pago
        Pago pago = pagoRepository.findById(result.getPagoId())
                .orElseThrow();
        assertThat(pago.getOrden().getId()).isEqualTo(orden.getId());
        assertThat(pago.getEstado()).isEqualTo("APROBADO");
        assertThat(pago.getTransaccionRef()).startsWith("STRIPE-");

        // Factura
        Factura factura = facturaRepository.findById(result.getFacturaId())
                .orElseThrow();
        assertThat(factura.getOrden().getId()).isEqualTo(orden.getId());
        assertThat(factura.getMonto()).isEqualByComparingTo(result.getTotalNeto());

        // Stock movimientos
        List<StockMovimiento> movimientos = stockMovimientoRepository.findAll();
        assertThat(movimientos).hasSize(1);
        StockMovimiento mov = movimientos.get(0);
        assertThat(mov.getTipo()).isEqualTo("OUT");
        assertThat(mov.getCantidad()).isEqualTo(2);
        assertThat(mov.getReferencia()).contains("ORDEN #" + orden.getId());
    }
}
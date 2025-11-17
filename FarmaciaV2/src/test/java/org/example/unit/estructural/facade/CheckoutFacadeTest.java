package org.example.unit.estructural.facade;


import org.example.patron_de_diseno.estructural.Facade.dto.CheckoutItemDTO;
import org.example.patron_de_diseno.estructural.Facade.dto.CheckoutRequestDTO;
import org.example.patron_de_diseno.estructural.Facade.dto.CheckoutResultDTO;
import org.example.model.*;
import org.example.patron_de_diseno.estructural.Facade.CheckoutFacade;
import org.example.repository.*;
import org.example.service.PagoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapterFactory.TipoPasarela;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test UNITARIO del Facade:
 * - Mockea todos los repos + PagoService.
 * - Verifica que el Facade:
 *   * crea la Orden con totales correctos
 *   * descuenta stock (StockMovimiento OUT)
 *   * llama a PagoService
 *   * genera Factura
 */
@ExtendWith(MockitoExtension.class)
class CheckoutFacadeTest {

    @Mock PacienteRepository pacienteRepository;
    @Mock ProductoRepository productoRepository;
    @Mock LoteRepository loteRepository;
    @Mock OrdenRepository ordenRepository;
    @Mock StockMovimientoRepository stockMovimientoRepository;
    @Mock PagoService pagoService;
    @Mock FacturaRepository facturaRepository;

    @InjectMocks
    CheckoutFacade checkoutFacade;

    @Test
    void realizarCheckout_creaOrdenPagoFacturaYMovStock() {
        // given -----------------------------
        Long pacienteId = 1L;
        Long productoId = 10L;
        Long loteId = 20L;

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);

        Producto producto = new Producto();
        producto.setId(productoId);
        producto.setIvaPorcentaje(new BigDecimal("19")); // 19%

        Lote lote = new Lote();
        lote.setId(loteId);

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente));
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(loteRepository.findById(loteId)).thenReturn(Optional.of(lote));

        // simulamos guardado de Orden
        when(ordenRepository.save(any(Orden.class))).thenAnswer(invocation -> {
            Orden o = invocation.getArgument(0);
            o.setId(100L);
            return o;
        });

        // simulamos PagoService
        Pago pago = new Pago();
        pago.setId(200L);
        when(pagoService.procesarPago(any(Orden.class), any(), anyString(), any()))
                .thenReturn(pago);

        // simulamos Factura
        Factura factura = new Factura();
        factura.setId(300L);
        when(facturaRepository.save(any(Factura.class))).thenReturn(factura);

        // request del facade
        CheckoutItemDTO itemDTO = CheckoutItemDTO.builder()
                .productoId(productoId)
                .loteId(loteId)
                .cantidad(2)
                .precioUnitario(new BigDecimal("100.00"))
                .requiereReceta(false)
                .build();

        CheckoutRequestDTO request = CheckoutRequestDTO.builder()
                .pacienteId(pacienteId)
                .canal("MOSTRADOR")
                .creadoPor("CAJERO-1")
                .items(List.of(itemDTO))
                .metodoPago("TARJETA")
                .tipoPasarela(TipoPasarela.STRIPE)
                .build();

        // when -----------------------------
        CheckoutResultDTO result = checkoutFacade.realizarCheckout(request);

        // then -----------------------------
        // Totales esperados:
        // subtotal = 2 * 100 = 200
        // iva 19% = 38
        // neto = 238
        assertThat(result.getOrdenId()).isEqualTo(100L);
        assertThat(result.getPagoId()).isEqualTo(200L);
        assertThat(result.getFacturaId()).isEqualTo(300L);
        assertThat(result.getTotalBruto()).isEqualByComparingTo("200.00");
        assertThat(result.getTotalImpuestos()).isEqualByComparingTo("38.00");
        assertThat(result.getTotalNeto()).isEqualByComparingTo("238.00");

        // capturamos la Orden enviada al repo
        ArgumentCaptor<Orden> ordenCaptor = ArgumentCaptor.forClass(Orden.class);
        verify(ordenRepository).save(ordenCaptor.capture());
        Orden ordenEnviada = ordenCaptor.getValue();

        assertThat(ordenEnviada.getPaciente()).isEqualTo(paciente);
        assertThat(ordenEnviada.getItems()).hasSize(1);
        OrdenItem item = ordenEnviada.getItems().get(0);
        assertThat(item.getProducto()).isEqualTo(producto);
        assertThat(item.getLote()).isEqualTo(lote);
        assertThat(item.getCantidad()).isEqualTo(2);

        // verificamos que se registró al menos un movimiento de stock OUT
        ArgumentCaptor<StockMovimiento> stockCaptor = ArgumentCaptor.forClass(StockMovimiento.class);
        verify(stockMovimientoRepository, atLeastOnce()).save(stockCaptor.capture());
        StockMovimiento mov = stockCaptor.getValue();
        assertThat(mov.getTipo()).isEqualTo("OUT");
        assertThat(mov.getCantidad()).isEqualTo(2);
        assertThat(mov.getReferencia()).contains("ORDEN #100");

        // verificar que se llamó al PagoService con la misma orden
        verify(pagoService).procesarPago(
                eq(ordenEnviada),
                any(BigDecimal.class),
                eq("TARJETA"),
                eq(TipoPasarela.STRIPE)
        );
    }
}
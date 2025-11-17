package org.example.unit.estructural.adapter;

import org.example.model.Orden;
import org.example.model.Pago;
import org.example.repository.PagoRepository;
import org.example.service.PagoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapterFactory.TipoPasarela;

/**
 * Pruebas UNITARIAS de PagoService, enfocadas en el uso del patrón Adapter.
 * - No se levanta Spring ni H2.
 * - Se mockea PagoRepository.
 * - Se usan los adapters reales (Stripe / PayPal) a través de la factory.
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceAdapterTest {

    @Mock
    PagoRepository pagoRepository;  // mock del repositorio, no toca BD real

    @InjectMocks
    PagoService pagoService; // instancia real, pero con el repo mockeado

    /**
     * Método helper para crear una Orden "simple" para las pruebas.
     */
    private Orden buildOrden() {
        Orden orden = new Orden();
        orden.setId(10L);
        return orden;
    }

    @Test
    void procesarPago_conStripe_guardaPagoAprobadoConRefStripe() {
        // given
        Orden orden = buildOrden();
        BigDecimal monto = new BigDecimal("100.00");
        String metodo = "TARJETA";

        // Simulamos que el repo guarda el pago y le asigna ID=1
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // when: se llama al service indicando que use la pasarela STRIPE
        Pago resultado = pagoService.procesarPago(
                orden,
                monto,
                metodo,
                TipoPasarela.STRIPE
        );

        // then: capturamos qué Pago se mandó a guardar en el repo
        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(captor.capture());

        Pago enviadoAlRepo = captor.getValue();

        // Verificamos que el Pago se armó bien
        assertThat(enviadoAlRepo.getOrden()).isEqualTo(orden);
        assertThat(enviadoAlRepo.getMetodo()).isEqualTo("TARJETA");
        assertThat(enviadoAlRepo.getMonto()).isEqualByComparingTo("100.00");
        assertThat(enviadoAlRepo.getEstado()).isEqualTo("APROBADO");
        assertThat(enviadoAlRepo.getTransaccionRef())
                .isNotNull()
                .startsWith("STRIPE-");

        // El resultado devuelto por el service debe ser el mismo que devolvió el repo
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    void procesarPago_conPaypal_guardaPagoAprobadoConRefPaypal() {
        // given
        Orden orden = buildOrden();
        BigDecimal monto = new BigDecimal("50.00");
        String metodo = "TARJETA";

        // Simulamos guardado con ID=2
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        // when: ahora usamos la pasarela PAYPAL
        Pago resultado = pagoService.procesarPago(
                orden,
                monto,
                metodo,
                TipoPasarela.PAYPAL
        );

        // then
        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(captor.capture());

        Pago enviadoAlRepo = captor.getValue();

        assertThat(enviadoAlRepo.getOrden()).isEqualTo(orden);
        assertThat(enviadoAlRepo.getEstado()).isEqualTo("APROBADO");
        assertThat(enviadoAlRepo.getTransaccionRef())
                .isNotNull()
                .startsWith("PAYPAL-");

        assertThat(resultado.getId()).isEqualTo(2L);
    }
}

package org.example.unit.estructural.adapter;

import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapter;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapter.PagoExternoRequest;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapter.PagoExternoResponse;
import org.example.patron_de_diseno.estructural.adapter.StripePagoAdapter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test UNITARIO del adapter concreto StripePagoAdapter.
 * Verifica que:
 *  - devuelva aprobado = true
 *  - genere un id que empiece por "STRIPE-"
 *  - incluya "Stripe" en el mensaje
 */
class StripePagoAdapterTest {

    @Test
    void procesarPago_devuelveTransaccionStripeYAprobado() {
        // given: un adapter real y un request genérico
        PasarelaPagoAdapter adapter = new StripePagoAdapter();

        PagoExternoRequest request = new PagoExternoRequest(
                new BigDecimal("123.45"),
                "COP",
                "Pago orden #1",
                "TARJETA"
        );

        // when: se procesa el pago a través del adapter
        PagoExternoResponse response = adapter.procesarPago(request);

        // then: la respuesta genérica debe indicar éxito y formatear la transacción
        assertThat(response.isAprobado()).isTrue();
        assertThat(response.getTransaccionId())
                .isNotNull()
                .startsWith("STRIPE-");
        assertThat(response.getMensaje())
                .containsIgnoringCase("Stripe");
    }
}

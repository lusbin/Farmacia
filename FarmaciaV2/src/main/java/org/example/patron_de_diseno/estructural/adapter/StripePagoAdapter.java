package org.example.patron_de_diseno.estructural.adapter;

import org.example.patron_de_diseno.estructural.adapter.sdk.PasarelaStripeSdk;

import java.math.BigDecimal;

/**
 * Implementación concreta del Adapter para la pasarela Stripe.
 * Adapta el modelo genérico de pago a lo que espera el "SDK" de Stripe.
 */
public class StripePagoAdapter implements PasarelaPagoAdapter {

    // Composición: el adapter envuelve un "SDK" específico de Stripe
    private final PasarelaStripeSdk sdk = new PasarelaStripeSdk();

    @Override
    public PagoExternoResponse procesarPago(PagoExternoRequest request) {
        // Stripe trabaja en centavos -> convertimos BigDecimal a long (centavos)
        long cents = request.getMonto()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        // Si no viene moneda, por defecto COP
        String currency = request.getMoneda() != null ? request.getMoneda() : "COP";

        // Llamamos al SDK simulado de Stripe
        String id = sdk.crearCargo(cents, currency, request.getReferencia());

        // Devolvemos una respuesta genérica para nuestro dominio
        return new PagoExternoResponse(
                true,
                id,
                "Pago procesado vía Stripe"
        );
    }
}

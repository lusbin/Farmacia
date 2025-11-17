package org.example.patron_de_diseno.estructural.adapter;

import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapter.PagoExternoRequest;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapter.PagoExternoResponse;
import org.example.patron_de_diseno.estructural.adapter.sdk.PasarelaPaypalSdk;

/**
 * Implementación concreta del Adapter para la pasarela PayPal.
 * Adapta el request genérico de la app al "SDK" simulado de PayPal.
 */
public class PaypalPagoAdapter implements PasarelaPagoAdapter {

    // Composición: usamos internamente un "SDK" específico de PayPal
    private final PasarelaPaypalSdk sdk = new PasarelaPaypalSdk();

    @Override
    public PagoExternoResponse procesarPago(PagoExternoRequest request) {
        // En un caso real, podrías recibir email del cliente en el request.
        // Aquí dejamos uno fijo para simplificar.
        String email = "cliente@example.com";

        // El SDK de PayPal espera un email + total como double
        String id = sdk.pagarConEmail(email, request.getMonto().doubleValue());

        // Devolvemos una respuesta genérica para el dominio
        return new PagoExternoResponse(
                true,
                id,
                "Pago procesado vía PayPal"
        );
    }
}

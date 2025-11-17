package org.example.patron_de_diseno.estructural.adapter;

/**
 * Factory simple para obtener una implementación concreta de PasarelaPagoAdapter
 * según el tipo de pasarela (STRIPE, PAYPAL, etc.).
 *
 * Esto evita "if / else" o "switch" repetidos en los services.
 */
public class PasarelaPagoAdapterFactory {

    public enum TipoPasarela {
        STRIPE,
        PAYPAL
    }

    /**
     * Devuelve el adapter adecuado según el tipo de pasarela.
     */
    public static PasarelaPagoAdapter obtenerAdapter(TipoPasarela tipo) {
        return switch (tipo) {
            case STRIPE -> new StripePagoAdapter();
            case PAYPAL -> new PaypalPagoAdapter();
        };
    }
}

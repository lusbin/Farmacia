package org.example.patron_de_diseno.estructural.adapter.sdk;

import java.util.UUID;

public class PasarelaPaypalSdk {

    public String pagarConEmail(String emailCliente, double total) {
        return "PAYPAL-" + UUID.randomUUID();
    }
}

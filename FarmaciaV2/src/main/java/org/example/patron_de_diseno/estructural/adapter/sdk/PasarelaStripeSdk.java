package org.example.patron_de_diseno.estructural.adapter.sdk;

import java.util.UUID;

public class PasarelaStripeSdk {

    public String crearCargo(long amountInCents, String currency, String description) {
        return "STRIPE-" + UUID.randomUUID();
    }
}

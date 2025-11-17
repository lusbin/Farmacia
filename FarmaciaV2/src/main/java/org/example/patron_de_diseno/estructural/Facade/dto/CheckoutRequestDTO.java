package org.example.patron_de_diseno.estructural.Facade.dto;

import lombok.*;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapterFactory;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDTO {

    private Long pacienteId;              // paciente al que se le dispensa (puede ser null)
    private String canal;                 // MOSTRADOR, ONLINE
    private String creadoPor;             // usuario cajero/farmacéutico

    private List<CheckoutItemDTO> items;  // líneas de la orden

    private String metodoPago;            // EFECTIVO, TARJETA, etc.
    private PasarelaPagoAdapterFactory.TipoPasarela tipoPasarela; // STRIPE / PAYPAL

    // opcional: si quieres forzar un monto exacto de pago
    private BigDecimal montoPago;         // si es null, se usa totalNeto calculado
}
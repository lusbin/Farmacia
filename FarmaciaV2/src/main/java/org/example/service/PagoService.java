package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Orden;
import org.example.model.Pago;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapter;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapter.PagoExternoRequest;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapter.PagoExternoResponse;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapterFactory;
import org.example.repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapterFactory.TipoPasarela;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;

    /**
     * Procesa un pago usando la pasarela indicada (STRIPE / PAYPAL),
     * a través del patrón Adapter.
     */
    public Pago procesarPago(Orden orden,
                             BigDecimal monto,
                             String metodo,
                             TipoPasarela tipoPasarela) {

        // 1. Construir request genérico
        PagoExternoRequest request = new PagoExternoRequest(
                monto,
                "COP",
                "Pago de orden #" + orden.getId(),
                metodo
        );

        // 2. Obtener el adapter correcto (Stripe, PayPal, etc.)
        PasarelaPagoAdapter adapter =
                PasarelaPagoAdapterFactory.obtenerAdapter(tipoPasarela);

        // 3. Ejecutar pago en la pasarela a través del Adapter
        PagoExternoResponse response = adapter.procesarPago(request);

        // 4. Construir entidad Pago en nuestro modelo de dominio
        Pago pago = new Pago();
        pago.setOrden(orden);
        pago.setMetodo(metodo);
        pago.setMonto(monto);
        pago.setEstado(response.isAprobado() ? "APROBADO" : "RECHAZADO");
        pago.setTransaccionRef(response.getTransaccionId());
        pago.setCreadoEn(LocalDateTime.now());

        // 5. Persistir en BD
        return pagoRepository.save(pago);
    }
}

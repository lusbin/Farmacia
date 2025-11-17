package org.example.integration;

import org.example.model.Orden;
import org.example.model.Pago;
import org.example.repository.OrdenRepository;
import org.example.repository.PagoRepository;
import org.example.service.PagoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapterFactory.TipoPasarela;

@SpringBootTest
@ActiveProfiles("test")   // usa la H2 en memoria definida en application-test.yml
@Transactional
@Rollback                 // deshace los cambios al terminar el test
class PagoServiceIT {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Test
    void procesarPago_conStripe_persistePagoAprobadoEnH2() {
        // 1) GIVEN: una ORDEN real en la BD H2
        Orden orden = Orden.builder()
                .creadoEn(LocalDateTime.now())
                .estado("CREADA")
                .canal("MOSTRADOR")
                .creadoPor("TEST-USER")
                .totalBruto(new BigDecimal("150.00"))
                .totalImpuestos(new BigDecimal("0.00"))
                .totalNeto(new BigDecimal("150.00"))
                .build();

        orden = ordenRepository.save(orden); // ahora tiene ID

        BigDecimal monto = new BigDecimal("150.00");
        String metodo = "TARJETA";

        // 2) WHEN: procesamos el pago a través del Adapter (Stripe) usando el servicio real
        Pago pago = pagoService.procesarPago(
                orden,
                monto,
                metodo,
                TipoPasarela.STRIPE   // aquí decide qué adapter usar
        );

        // 3) THEN: el pago tiene ID y quedó realmente en la BD H2
        assertThat(pago.getId()).isNotNull();

        Pago pagoEnBD = pagoRepository.findById(pago.getId())
                .orElseThrow(() -> new IllegalStateException("Pago no encontrado en BD"));

        // verificamos datos clave
        assertThat(pagoEnBD.getOrden().getId()).isEqualTo(orden.getId());
        assertThat(pagoEnBD.getMetodo()).isEqualTo("TARJETA");
        assertThat(pagoEnBD.getMonto()).isEqualByComparingTo("150.00");
        assertThat(pagoEnBD.getEstado()).isEqualTo("APROBADO");
        assertThat(pagoEnBD.getTransaccionRef())
                .isNotNull()
                .startsWith("STRIPE-");

        // y opcional: validamos que la orden asociada mantiene su totalNeto
        assertThat(pagoEnBD.getOrden().getTotalNeto())
                .isEqualByComparingTo("150.00");
    }
}

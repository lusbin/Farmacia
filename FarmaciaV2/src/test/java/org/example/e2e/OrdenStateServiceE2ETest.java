package org.example.e2e;

import org.example.model.Orden;
import org.example.repository.OrdenRepository;
import org.example.service.OrdenStateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")   // usa H2 y configuración de pruebas
@Transactional            // cada test se revierte solo
class OrdenStateServiceE2ETest {

    @Autowired
    OrdenStateService ordenStateService;

    @Autowired
    OrdenRepository ordenRepository;

    /** Crea y persiste una orden en estado CREADA para usar en los tests */
    private Orden nuevaOrdenCreada() {
        Orden orden = new Orden();
        orden.setCreadoEn(LocalDateTime.now());
        orden.setEstado("CREADA");
        orden.setCanal("MOSTRADOR");
        orden.setCreadoPor("CAJERO-1");
        orden.setTotalBruto(new BigDecimal("100.00"));
        orden.setTotalImpuestos(new BigDecimal("19.00"));
        orden.setTotalNeto(new BigDecimal("119.00"));
        return ordenRepository.save(orden);
    }

    /**
     * Flujo “feliz”:
     *  CREADA -> APROBADA -> ENTREGADA
     *  Verificamos que los cambios queden persistidos en H2.
     */
    @Test
    void flujoCompleto_aprobarYEntregar_actualizaEstadoEnH2() {
        // given
        Orden orden = nuevaOrdenCreada();
        Long id = orden.getId();

        // when: aprobamos
        Orden aprobada = ordenStateService.aprobarOrden(id);

        // then: estado en memoria y en BD
        assertThat(aprobada.getEstado()).isEqualTo("APROBADA");
        Orden desdeBD = ordenRepository.findById(id).orElseThrow();
        assertThat(desdeBD.getEstado()).isEqualTo("APROBADA");

        // when: entregamos
        Orden entregada = ordenStateService.entregarOrden(id);

        // then: estado actualizado y persistido
        assertThat(entregada.getEstado()).isEqualTo("ENTREGADA");
        Orden desdeBD2 = ordenRepository.findById(id).orElseThrow();
        assertThat(desdeBD2.getEstado()).isEqualTo("ENTREGADA");
    }

    /**
     * Flujo inválido:
     *  intentar ENTREGAR directamente una orden CREADA.
     *  El patrón State debería lanzar IllegalStateException
     *  y NO cambiar el estado en la base de datos.
     */
    @Test
    void flujoInvalido_entregarSinAprobar_lanzaExcepcionYNoCambiaEstado() {
        // given
        Orden orden = nuevaOrdenCreada();
        Long id = orden.getId();

        // when / then
        assertThatThrownBy(() -> ordenStateService.entregarOrden(id))
                .isInstanceOf(IllegalStateException.class);

        // y la orden sigue en estado CREADA en H2
        Orden desdeBD = ordenRepository.findById(id).orElseThrow();
        assertThat(desdeBD.getEstado()).isEqualTo("CREADA");
    }
}

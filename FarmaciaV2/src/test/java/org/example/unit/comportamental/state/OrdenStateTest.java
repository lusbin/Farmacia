package org.example.unit.comportamental.state;

import org.example.model.Orden;
import org.example.patron_de_diseno.comportamental.state.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class OrdenStateTest {

    private Orden buildOrdenCreada() {
        Orden o = new Orden();
        o.setId(1L);
        o.setEstado("CREADA");
        o.setTotalNeto(new BigDecimal("100.00"));
        return o;
    }

    @Test
    void creada_puedePasarAAprobadaYLuegoAEntregada() {
        // given
        Orden orden = buildOrdenCreada();
        OrdenContexto contexto = new OrdenContexto(orden);

        // estado inicial
        assertThat(contexto.getEstadoActual())
                .isInstanceOf(EstadoCreada.class);
        assertThat(orden.getEstado()).isEqualTo("CREADA");

        // when: aprobar
        contexto.aprobar();

        // then
        assertThat(contexto.getEstadoActual())
                .isInstanceOf(EstadoAprobada.class);
        assertThat(orden.getEstado()).isEqualTo("APROBADA");

        // when: entregar
        contexto.entregar();

        // then
        assertThat(contexto.getEstadoActual())
                .isInstanceOf(EstadoEntregada.class);
        assertThat(orden.getEstado()).isEqualTo("ENTREGADA");
    }

    @Test
    void creada_noSePuedeEntregarDirecto() {
        Orden orden = buildOrdenCreada();
        OrdenContexto contexto = new OrdenContexto(orden);

        assertThatThrownBy(contexto::entregar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CREADA");
    }

    @Test
    void aprobada_puedeSerEntregadaOPasadaAAnulada() {
        // given
        Orden orden = buildOrdenCreada();
        orden.setEstado("APROBADA");
        OrdenContexto contexto = new OrdenContexto(orden);

        assertThat(contexto.getEstadoActual())
                .isInstanceOf(EstadoAprobada.class);

        // when: anular
        contexto.anular();

        assertThat(contexto.getEstadoActual())
                .isInstanceOf(EstadoAnulada.class);
        assertThat(orden.getEstado()).isEqualTo("ANULADA");
    }

    @Test
    void entregada_noPermiteMasCambios() {
        Orden orden = buildOrdenCreada();
        orden.setEstado("ENTREGADA");
        OrdenContexto contexto = new OrdenContexto(orden);

        assertThat(contexto.getEstadoActual())
                .isInstanceOf(EstadoEntregada.class);

        assertThatThrownBy(contexto::aprobar)
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(contexto::anular)
                .isInstanceOf(IllegalStateException.class);
    }
}

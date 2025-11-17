package org.example.unit.creacional.singleton;

import org.example.model.Receta;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RecetaTest {

    @Test
    void prePersist_asignaCreadoEnCuandoEsNull() {
        // given
        Receta receta = new Receta();   // creadoEn = null por defecto

        // when
        receta.prePersist();            // simulamos la llamada que hará JPA antes de persistir

        // then
        assertThat(receta.getCreadoEn()).isNotNull();
    }

    @Test
    void prePersist_noSobrescribeCreadoEnSiYaTieneValor() {
        // given
        Receta receta = new Receta();
        LocalDateTime yaExistente = LocalDateTime.of(2024, 1, 1, 10, 0);
        receta.setCreadoEn(yaExistente);

        // when
        receta.prePersist();            // JPA lo llamaría igual antes de persistir

        // then: el valor original se mantiene
        assertThat(receta.getCreadoEn()).isEqualTo(yaExistente);
    }
}
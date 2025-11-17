package org.example.e2e;

import org.example.model.Medico;
import org.example.model.Producto;
import org.example.model.Usuario;
import org.example.service.AlertaStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class AlertaStockServiceE2ETest {

    @Autowired
    AlertaStockService alertaStockService;

    /**
     * Verifica la validación básica del service:
     * si el producto es null, debe lanzar IllegalArgumentException.
     */
    @Test
    void evaluarYNotificarStockBajo_productoNull_lanzaExcepcion() {
        assertThatThrownBy(() ->
                alertaStockService.evaluarYNotificarStockBajo(
                        null,
                        5,
                        10,
                        null,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContainingAll("producto no puede ser null");
    }

    /**
     * Escenario feliz:
     * producto + usuario + médico no nulos, stock por debajo del umbral.
     * El service crea el Subject y los Observers y NO lanza excepciones.
     * (Las notificaciones se manejan en memoria dentro de los observers).
     */
    @Test
    void evaluarYNotificarStockBajo_conDatosValidos_noLanzaExcepcion() {
        // given: construimos entidades en memoria (no es necesario guardarlas en BD)
        Producto producto = Producto.builder()
                .id(1L)
                .creadoEn(LocalDateTime.now())
                .sku("OBS-001")
                .nombre("Amoxicilina 500mg")
                .build();

        Usuario usuario = Usuario.builder()
                .id(10L)
                .creadoEn(LocalDateTime.now())
                .username("farmaceutico1")
                .build();

        Medico medico = Medico.builder()
                .id(20L)
                .creadoEn(LocalDateTime.now())
                .nombre("Dra. Notificada")
                .build();

        // when / then: no debe lanzar excepción
        assertThatCode(() ->
                alertaStockService.evaluarYNotificarStockBajo(
                        producto,
                        3,   // stock actual
                        5,   // umbral mínimo
                        usuario,
                        medico
                )
        ).doesNotThrowAnyException();
    }
}

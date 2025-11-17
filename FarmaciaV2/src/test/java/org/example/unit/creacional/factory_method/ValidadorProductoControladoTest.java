package org.example.unit.creacional.factory_method;

import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.Producto;
import org.example.patron_de_diseno.creacional.Factory_Method.ValidadorProducto;
import org.example.patron_de_diseno.creacional.Factory_Method.ValidadorProductoControlado;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorProductoControladoTest {

    // Instancia del validador que vamos a probar (el concreto para controlados)
    private final ValidadorProducto validador = new ValidadorProductoControlado();

    /**
     * Método de ayuda para construir una RecetaCreateDTO con un solo item.
     * Permite variar pacienteId, medicoId y duración en días para las pruebas.
     */
    private RecetaCreateDTO buildReceta(Long pacienteId, Long medicoId, Integer duracionDias) {
        // Creamos un item de receta con los datos básicos
        RecetaItemDTO item = RecetaItemDTO.builder()
                .productoId(1L)
                .cantidad(1)
                .dosis("10 mg")
                .frecuencia("cada 8h")
                .duracionDias(duracionDias)
                .build();

        // Construimos la receta con ese item
        return RecetaCreateDTO.builder()
                .pacienteId(pacienteId)
                .medicoId(medicoId)
                .fechaEmision(LocalDate.now())
                .validezDias(10)
                .observaciones("prueba")
                .items(List.of(item))
                .build();
    }

    @Test
    void validar_ok_cuandoPacienteYMedicoYDuracionCorrectos() {
        // given: producto controlado + receta con paciente, médico y duración permitida
        Producto producto = new Producto();
        producto.setEsControlado(true);

        RecetaCreateDTO receta = buildReceta(1L, 2L, 15); // 15 días < 30 → válido
        RecetaItemDTO itemDTO = receta.getItems().get(0);

        // when / then: la validación NO debe lanzar ninguna excepción
        assertThatCode(() -> validador.validar(producto, receta, itemDTO))
                .doesNotThrowAnyException();
    }

    @Test
    void validar_falla_cuandoFaltaMedico() {
        // given: producto controlado + receta SIN médico
        Producto producto = new Producto();
        producto.setEsControlado(true);

        RecetaCreateDTO receta = buildReceta(1L, null, 15); // médicoId = null
        RecetaItemDTO itemDTO = receta.getItems().get(0);

        // when / then: el validador debe rechazar la receta por falta de médico
        assertThatThrownBy(() -> validador.validar(producto, receta, itemDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("médico");
    }

    @Test
    void validar_falla_cuandoFaltaPaciente() {
        // given: producto controlado + receta SIN paciente
        Producto producto = new Producto();
        producto.setEsControlado(true);

        RecetaCreateDTO receta = buildReceta(null, 2L, 15); // pacienteId = null
        RecetaItemDTO itemDTO = receta.getItems().get(0);

        // when / then: el validador debe rechazar la receta por falta de paciente
        assertThatThrownBy(() -> validador.validar(producto, receta, itemDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("paciente");
    }

    @Test
    void validar_falla_cuandoDuracionMayorA30Dias() {
        // given: producto controlado + duración mayor a 30 días (límite máximo)
        Producto producto = new Producto();
        producto.setEsControlado(true);

        RecetaCreateDTO receta = buildReceta(1L, 2L, 45); // 45 > 30 → invalida
        RecetaItemDTO itemDTO = receta.getItems().get(0);

        // when / then: el validador debe rechazar por duración excesiva
        assertThatThrownBy(() -> validador.validar(producto, receta, itemDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("30 días");
    }
}

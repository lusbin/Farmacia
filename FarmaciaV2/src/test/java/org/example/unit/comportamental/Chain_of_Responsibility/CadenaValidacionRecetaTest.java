package org.example.unit.comportamental.Chain_of_Responsibility;


import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.Paciente;
import org.example.model.Producto;
import org.example.patron_de_diseno.comportamental.Chain_of_Responsibility.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CadenaValidacionRecetaTest {

    private ValidadorCadenaReceta buildCadena() {
        return new ValidadorVigenciaReceta(
                new ValidadorAlergiasPaciente(
                        new ValidadorDosisDuracion(
                                new ValidadorSeguro(null)
                        )
                )
        );
    }

    private ContextoValidacionReceta buildContextOk() {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .alergias("Ninguna")
                .build();

        Producto producto = Producto.builder()
                .id(10L)
                .principioActivo("Paracetamol")
                .esControlado(false)
                .build();

        RecetaItemDTO itemDTO = RecetaItemDTO.builder()
                .productoId(producto.getId())
                .cantidad(1)
                .dosis("500 mg")
                .frecuencia("cada 8h")
                .duracionDias(7)
                .build();

        RecetaCreateDTO recetaDTO = RecetaCreateDTO.builder()
                .pacienteId(paciente.getId())
                .medicoId(2L)
                .fechaEmision(LocalDate.now())   // vigente
                .validezDias(10)
                .items(List.of(itemDTO))
                .build();

        return new ContextoValidacionReceta(recetaDTO, itemDTO, paciente, producto);
    }

    @Test
    void cadenaCompleta_noLanzaExcepcion_cuandoTodoEsValido() {
        // given
        ValidadorCadenaReceta cadena = buildCadena();
        ContextoValidacionReceta ctx = buildContextOk();

        // when / then
        assertThatCode(() -> cadena.validar(ctx))
                .doesNotThrowAnyException();
    }

    @Test
    void cadenaCompleta_fallaEnAlergias_cuandoPacienteEsAlergico() {
        // given
        ValidadorCadenaReceta cadena = buildCadena();

        ContextoValidacionReceta ctx = buildContextOk();
        // simulamos alergia al principio activo
        ctx.getPaciente().setAlergias("Alergia a Paracetamol");

        // when / then
        assertThatThrownBy(() -> cadena.validar(ctx))
                .isInstanceOf(IllegalArgumentException.class)
                // comprobamos que viene de la cadena de alergias
                .hasMessageContaining("Cadena: el paciente es")
                // y que menciona el principio activo concreto
                .hasMessageContaining("Paracetamol");
    }

}
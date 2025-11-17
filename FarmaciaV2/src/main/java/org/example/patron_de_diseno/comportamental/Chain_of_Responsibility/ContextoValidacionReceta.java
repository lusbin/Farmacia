package org.example.patron_de_diseno.comportamental.Chain_of_Responsibility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.Paciente;
import org.example.model.Producto;

/**
 * Contexto que viaja a través de la cadena de validación.
 * Aquí tienes TODO lo que un validador podría necesitar.
 */
@Getter
@AllArgsConstructor
public class ContextoValidacionReceta {

    private final RecetaCreateDTO recetaDTO;
    private final RecetaItemDTO itemDTO;
    private final Paciente paciente;
    private final Producto producto;
}
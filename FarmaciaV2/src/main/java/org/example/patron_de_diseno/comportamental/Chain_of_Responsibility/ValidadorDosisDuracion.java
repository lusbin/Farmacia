package org.example.patron_de_diseno.comportamental.Chain_of_Responsibility;


import org.example.dto.RecetaItemDTO;

/**
 * Valida reglas de dosis / duración (muy simplificadas).
 */
public class ValidadorDosisDuracion extends ValidadorBase {

    public ValidadorDosisDuracion(ValidadorCadenaReceta siguiente) {
        super(siguiente);
    }

    @Override
    public void validar(ContextoValidacionReceta contexto) {
        RecetaItemDTO item = contexto.getItemDTO();

        if (item.getDuracionDias() != null && item.getDuracionDias() > 60) {
            throw new IllegalArgumentException(
                    "Cadena: la duración del tratamiento supera el máximo permitido (60 días)."
            );
        }

        if (item.getCantidad() != null && item.getCantidad() > 10) {
            throw new IllegalArgumentException(
                    "Cadena: la cantidad prescrita es excesiva para un solo despacho."
            );
        }

        continuar(contexto);
    }
}
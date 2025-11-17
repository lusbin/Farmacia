package org.example.patron_de_diseno.comportamental.Chain_of_Responsibility;

import org.example.patron_de_diseno.comportamental.Chain_of_Responsibility.*;
/**
 * Implementación base que gestiona el "siguiente" eslabón.
 */
public abstract class ValidadorBase implements ValidadorCadenaReceta {

    private final ValidadorCadenaReceta siguiente;

    protected ValidadorBase(ValidadorCadenaReceta siguiente) {
        this.siguiente = siguiente;
    }

    /**
     * Llama al siguiente eslabón, si existe.
     */
    protected void continuar(ContextoValidacionReceta contexto) {
        if (siguiente != null) {
            siguiente.validar(contexto);
        }
    }
}
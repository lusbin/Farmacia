package org.example.patron_de_diseno.comportamental.Chain_of_Responsibility;

public interface ValidadorCadenaReceta {

    /**
     * Ejecuta la validación de este eslabón y, si todo está OK,
     * delega al siguiente en la cadena (si existe).
     */
    void validar(ContextoValidacionReceta contexto);
}
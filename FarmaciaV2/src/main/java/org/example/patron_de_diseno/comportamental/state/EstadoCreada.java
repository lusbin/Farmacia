package org.example.patron_de_diseno.comportamental.state;

import org.example.model.Orden;

/**
 * Estado: CREADA
 * Desde aquí se puede: aprobar o anular.
 * No se puede entregar directamente.
 */
public class EstadoCreada implements EstadoOrden {

    @Override
    public void aprobar(OrdenContexto contexto) {
        // transición válida: CREADA -> APROBADA
        contexto.setEstadoActual(new EstadoAprobada());
    }

    @Override
    public void entregar(OrdenContexto contexto) {
        throw new IllegalStateException(
                "No se puede ENTREGAR una orden en estado CREADA. Debe estar APROBADA primero.");
    }

    @Override
    public void anular(OrdenContexto contexto) {
        // transición válida: CREADA -> ANULADA
        contexto.setEstadoActual(new EstadoAnulada());
    }

    @Override
    public String getNombre() {
        return "CREADA";
    }
}

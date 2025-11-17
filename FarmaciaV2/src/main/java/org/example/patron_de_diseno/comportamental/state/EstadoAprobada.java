package org.example.patron_de_diseno.comportamental.state;

/**
 * Estado: APROBADA
 * Desde aquí se puede: ENTREGAR o ANULAR.
 */
public class EstadoAprobada implements EstadoOrden {

    @Override
    public void aprobar(OrdenContexto contexto) {
        throw new IllegalStateException("La orden ya está APROBADA.");
    }

    @Override
    public void entregar(OrdenContexto contexto) {
        // transición válida: APROBADA -> ENTREGADA
        contexto.setEstadoActual(new EstadoEntregada());
    }

    @Override
    public void anular(OrdenContexto contexto) {
        // transición válida: APROBADA -> ANULADA
        contexto.setEstadoActual(new EstadoAnulada());
    }

    @Override
    public String getNombre() {
        return "APROBADA";
    }
}

package org.example.patron_de_diseno.comportamental.state;

/**
 * Estado: ANULADA
 * Estado terminal. No se permiten acciones posteriores.
 */
public class EstadoAnulada implements EstadoOrden {

    @Override
    public void aprobar(OrdenContexto contexto) {
        throw new IllegalStateException("No se puede APROBAR una orden ANULADA.");
    }

    @Override
    public void entregar(OrdenContexto contexto) {
        throw new IllegalStateException("No se puede ENTREGAR una orden ANULADA.");
    }

    @Override
    public void anular(OrdenContexto contexto) {
        throw new IllegalStateException("La orden ya está ANULADA.");
    }

    @Override
    public String getNombre() {
        return "ANULADA";
    }
}

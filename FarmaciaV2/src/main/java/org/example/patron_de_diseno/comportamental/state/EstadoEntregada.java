package org.example.patron_de_diseno.comportamental.state;

/**
 * Estado: ENTREGADA
 * Estado terminal. No se permiten más transiciones.
 */
public class EstadoEntregada implements EstadoOrden {

    @Override
    public void aprobar(OrdenContexto contexto) {
        throw new IllegalStateException("No se puede APROBAR una orden ENTREGADA.");
    }

    @Override
    public void entregar(OrdenContexto contexto) {
        throw new IllegalStateException("La orden ya está ENTREGADA.");
    }

    @Override
    public void anular(OrdenContexto contexto) {
        throw new IllegalStateException("No se puede ANULAR una orden ENTREGADA.");
    }

    @Override
    public String getNombre() {
        return "ENTREGADA";
    }
}

package org.example.patron_de_diseno.comportamental.state;

import org.example.model.Orden;

/**
 * Contexto del patrón State.
 *
 * Envuelve la entidad Orden y delega la lógica de transición de estado
 * a instancias de EstadoOrden concretas.
 */
public class OrdenContexto {

    private final Orden orden;
    private EstadoOrden estadoActual;

    public OrdenContexto(Orden orden) {
        if (orden == null) {
            throw new IllegalArgumentException("La orden no puede ser null");
        }
        this.orden = orden;
        // Elegimos el estado concreto en base al String orden.getEstado()
        this.estadoActual = EstadoOrdenFactory.desdeNombre(orden.getEstado());
        // Alineamos la entidad con el nombre del estado
        this.orden.setEstado(this.estadoActual.getNombre());
    }

    public Orden getOrden() {
        return orden;
    }

    public EstadoOrden getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(EstadoOrden estado) {
        this.estadoActual = estado;
        // mantenemos la entidad sincronizada con el patrón
        this.orden.setEstado(estado.getNombre());
    }

    // Métodos de alto nivel que delegan al estado actual
    public void aprobar() {
        estadoActual.aprobar(this);
    }

    public void entregar() {
        estadoActual.entregar(this);
    }

    public void anular() {
        estadoActual.anular(this);
    }
}

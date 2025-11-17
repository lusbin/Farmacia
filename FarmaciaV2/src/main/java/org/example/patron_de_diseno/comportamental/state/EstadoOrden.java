package org.example.patron_de_diseno.comportamental.state;

import org.example.model.Orden;

/**
 * Contrato del patrón State para manejar el ciclo de vida de una Orden.
 */
public interface EstadoOrden {

    /**
     * Intenta aprobar la orden (CREADA -> APROBADA).
     */
    void aprobar(OrdenContexto contexto);

    /**
     * Intenta entregar la orden (APROBADA -> ENTREGADA).
     */
    void entregar(OrdenContexto contexto);

    /**
     * Intenta anular la orden (CREADA/APROBADA -> ANULADA).
     */
    void anular(OrdenContexto contexto);

    /**
     * Nombre lógico del estado (“CREADA”, “APROBADA”, etc.).
     */
    String getNombre();
}

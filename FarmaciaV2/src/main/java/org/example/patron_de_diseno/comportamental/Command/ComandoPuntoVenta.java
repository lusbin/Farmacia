package org.example.patron_de_diseno.comportamental.Command;

/**
 * Comando genérico del punto de venta.
 * Cada acción (agregar ítem, aplicar descuento, etc.) se encapsula en una implementación.
 */
public interface ComandoPuntoVenta {

    /**
     * Ejecuta la acción.
     */
    void ejecutar();

    /**
     * Revierte la acción (deshacer).
     */
    void deshacer();

    /**
     * Nombre descriptivo del comando (útil para logs / UI).
     */
    String getNombre();
}

package org.example.patron_de_diseno.comportamental.Command;

import java.util.Stack;

/**
 * Invoker del patrón Command para el punto de venta.
 * Ejecuta comandos y mantiene un historial para permitir deshacer.
 */
public class PuntoVentaInvoker {

    private final Stack<ComandoPuntoVenta> historial = new Stack<>();

    /**
     * Ejecuta un comando y lo guarda en el historial para poder deshacer.
     */
    public void ejecutarComando(ComandoPuntoVenta comando) {
        comando.ejecutar();
        historial.push(comando);
    }

    /**
     * Deshace el último comando ejecutado.
     */
    public void deshacerUltimo() {
        if (historial.isEmpty()) {
            return;
        }
        ComandoPuntoVenta ultimo = historial.pop();
        ultimo.deshacer();
    }

    public boolean tieneHistorial() {
        return !historial.isEmpty();
    }
}

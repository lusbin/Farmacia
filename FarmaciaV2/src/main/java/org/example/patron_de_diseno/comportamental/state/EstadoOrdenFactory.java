package org.example.patron_de_diseno.comportamental.state;

/**
 * Factory para instanciar el EstadoOrden correcto según el nombre guardado
 * en la entidad Orden (campo estado).
 */
public class EstadoOrdenFactory {

    public static EstadoOrden desdeNombre(String estado) {
        if (estado == null) {
            // Por defecto, si no hay nada, consideramos CREADA
            return new EstadoCreada();
        }

        return switch (estado.toUpperCase()) {
            case "APROBADA" -> new EstadoAprobada();
            case "ENTREGADA" -> new EstadoEntregada();
            case "ANULADA" -> new EstadoAnulada();
            case "CREADA" -> new EstadoCreada();
            default -> throw new IllegalArgumentException("Estado de orden desconocido: " + estado);
        };
    }
}

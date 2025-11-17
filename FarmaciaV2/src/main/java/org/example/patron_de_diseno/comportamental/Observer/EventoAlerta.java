package org.example.patron_de_diseno.comportamental.Observer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.model.Producto;

/**
 * Representa un evento de alerta que se dispara desde el Subject
 * (stock bajo, interacción medicamentosa, etc.).
 */
@Data
@AllArgsConstructor
public class EventoAlerta {

    public enum TipoAlerta {
        STOCK_BAJO,
        INTERACCION_MEDICAMENTOSA
    }

    private TipoAlerta tipo;
    private String mensaje;
    private Producto producto;
}

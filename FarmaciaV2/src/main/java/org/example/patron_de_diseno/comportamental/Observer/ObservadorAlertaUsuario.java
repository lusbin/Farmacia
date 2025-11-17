package org.example.patron_de_diseno.comportamental.Observer;

import lombok.Getter;
import org.example.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Observador que representa a un Usuario del sistema.
 * Aquí simulamos que "recibe" las alertas guardándolas en memoria.
 * En un sistema real, este observer podría mandar emails, notificaciones, etc.
 */
public class ObservadorAlertaUsuario implements ObservadorAlerta {

    private final Usuario usuario;

    // Para efectos de pruebas, guardamos las alertas recibidas
    @Getter
    private final List<EventoAlerta> alertasRecibidas = new ArrayList<>();

    public ObservadorAlertaUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public void notificar(EventoAlerta evento) {
        // Aquí podrías integrar email / notificación push
        alertasRecibidas.add(evento);
    }
}

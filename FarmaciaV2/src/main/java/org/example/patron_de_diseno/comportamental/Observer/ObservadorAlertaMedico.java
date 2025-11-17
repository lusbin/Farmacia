package org.example.patron_de_diseno.comportamental.Observer;

import lombok.Getter;
import org.example.model.Medico;

import java.util.ArrayList;
import java.util.List;

/**
 * Observador para médicos.
 * Igual que el de Usuario, pero semánticamente separado para mostrar
 * que distintos tipos de actores pueden observar al mismo Subject.
 */
public class ObservadorAlertaMedico implements ObservadorAlerta {

    private final Medico medico;

    @Getter
    private final List<EventoAlerta> alertasRecibidas = new ArrayList<>();

    public ObservadorAlertaMedico(Medico medico) {
        this.medico = medico;
    }

    @Override
    public void notificar(EventoAlerta evento) {
        alertasRecibidas.add(evento);
    }
}

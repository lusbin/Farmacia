package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Orden;
import org.example.patron_de_diseno.comportamental.state.OrdenContexto;
import org.example.repository.OrdenRepository;
import org.springframework.stereotype.Service;

/**
 * Service que utiliza el patrón State para gestionar el ciclo de vida
 * de la entidad Orden.
 */
@Service
@RequiredArgsConstructor
public class OrdenStateService {

    private final OrdenRepository ordenRepository;

    private OrdenContexto buildContexto(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con id=" + ordenId));
        return new OrdenContexto(orden);
    }

    public Orden aprobarOrden(Long ordenId) {
        OrdenContexto contexto = buildContexto(ordenId);
        contexto.aprobar();                 // delega al estado actual
        return ordenRepository.save(contexto.getOrden());
    }

    public Orden entregarOrden(Long ordenId) {
        OrdenContexto contexto = buildContexto(ordenId);
        contexto.entregar();
        return ordenRepository.save(contexto.getOrden());
    }

    public Orden anularOrden(Long ordenId) {
        OrdenContexto contexto = buildContexto(ordenId);
        contexto.anular();
        return ordenRepository.save(contexto.getOrden());
    }
}

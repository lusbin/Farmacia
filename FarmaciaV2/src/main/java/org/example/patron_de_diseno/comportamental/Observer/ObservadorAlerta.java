package org.example.patron_de_diseno.comportamental.Observer;

/**
 * Contrato para cualquier observador que quiera recibir alertas
 * (usuarios internos, médicos, sistemas externos, etc.).
 */
public interface ObservadorAlerta {

    void notificar(EventoAlerta evento);
}

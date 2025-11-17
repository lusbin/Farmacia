package org.example.patron_de_diseno.estructural.Proxy;


import org.example.model.LibroControl;
import org.example.model.Receta;

/**
 * Interfaz que expone operaciones sensibles:
 * ver recetas y registros del libro de control.
 */
public interface AccesoDatosSensibles {

    Receta obtenerRecetaPorId(Long recetaId, String rolSolicitante);

    LibroControl obtenerLibroControlPorId(Long libroControlId, String rolSolicitante);
}
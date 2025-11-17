package org.example.patron_de_diseno.estructural.Proxy;


import org.example.model.LibroControl;
import org.example.model.Receta;

/**
 * Proxy que se coloca delante de la implementación real
 * y valida el rol antes de delegar.
 */
public class AccesoDatosSensiblesProxy implements AccesoDatosSensibles {

    private final AccesoDatosSensibles target;

    public AccesoDatosSensiblesProxy(AccesoDatosSensibles target) {
        this.target = target;
    }

    @Override
    public Receta obtenerRecetaPorId(Long recetaId, String rolSolicitante) {
        validarAcceso(rolSolicitante, "RECETA");
        return target.obtenerRecetaPorId(recetaId, rolSolicitante);
    }

    @Override
    public LibroControl obtenerLibroControlPorId(Long libroControlId, String rolSolicitante) {
        validarAcceso(rolSolicitante, "LIBRO_CONTROL");
        return target.obtenerLibroControlPorId(libroControlId, rolSolicitante);
    }

    /**
     * Reglas simples de ejemplo:
     * - FARMACEUTICO, MEDICO y ADMIN pueden ver TODO
     * - otros roles (CAJERO, INVITADO, etc.) no pueden
     */
    private void validarAcceso(String rolSolicitante, String recurso) {
        if (rolSolicitante == null) {
            throw new SecurityException("Acceso denegado: rol no informado");
        }

        String rol = rolSolicitante.toUpperCase();

        boolean permitido =
                rol.equals("FARMACEUTICO") ||
                        rol.equals("FARMACÉUTICO") || // por si usas tilde
                        rol.equals("MEDICO") ||
                        rol.equals("MÉDICO") ||
                        rol.equals("ADMIN");

        if (!permitido) {
            throw new SecurityException(
                    "Acceso denegado al recurso " + recurso + " para el rol: " + rolSolicitante
            );
        }
    }
}
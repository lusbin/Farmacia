package org.example.patron_de_diseno.estructural.Proxy;


import lombok.RequiredArgsConstructor;
import org.example.model.LibroControl;
import org.example.model.Receta;
import org.example.repository.LibroControlRepository;
import org.example.repository.RecetaRepository;

/**
 * Implementación real: solo se encarga de ir a los repositorios
 * y devolver los datos. NO tiene lógica de seguridad.
 */
@RequiredArgsConstructor
public class AccesoDatosSensiblesReal implements AccesoDatosSensibles {

    private final RecetaRepository recetaRepository;
    private final LibroControlRepository libroControlRepository;

    @Override
    public Receta obtenerRecetaPorId(Long recetaId, String rolSolicitante) {
        // el rol aquí no se usa; el control de acceso lo hace el Proxy
        return recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));
    }

    @Override
    public LibroControl obtenerLibroControlPorId(Long libroControlId, String rolSolicitante) {
        return libroControlRepository.findById(libroControlId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de libro de control no encontrado"));
    }
}
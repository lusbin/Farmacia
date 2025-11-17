package org.example.patron_de_diseno.creacional.Factory_Method;


import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.Producto;

public class ValidadorProductoOtc implements ValidadorProducto {

    @Override
    public void validar(Producto producto,
                        RecetaCreateDTO recetaDTO,
                        RecetaItemDTO itemDTO) {
        // Ejemplo suave: podrías limitar cantidad, etc.
        // Aquí solo dejamos un lugar para reglas futuras.
        if (itemDTO.getCantidad() != null && itemDTO.getCantidad() > 5) {
            throw new IllegalArgumentException(
                    "Para productos OTC la cantidad máxima recomendada es 5 unidades.");
        }
    }
}
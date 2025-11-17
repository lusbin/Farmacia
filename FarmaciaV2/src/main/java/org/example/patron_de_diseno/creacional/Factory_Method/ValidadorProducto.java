package org.example.patron_de_diseno.creacional.Factory_Method;

import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.Producto;

public interface ValidadorProducto {

    /**
     * Lanza IllegalArgumentException si las reglas para este producto
     * no se cumplen.
     */
    void validar(Producto producto,
                 RecetaCreateDTO recetaDTO,
                 RecetaItemDTO itemDTO);
}
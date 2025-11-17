package org.example.patron_de_diseno.creacional.Factory_Method;

import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.Producto;

public class ValidadorProductoGenerico implements ValidadorProducto {

    @Override
    public void validar(Producto producto,
                        RecetaCreateDTO recetaDTO,
                        RecetaItemDTO itemDTO) {
        // Aquí puedes dejarlo vacío o poner reglas comunes.
        // Por ahora, no hacemos nada especial.
    }
}
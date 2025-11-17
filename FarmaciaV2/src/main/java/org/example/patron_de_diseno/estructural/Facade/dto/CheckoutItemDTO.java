package org.example.patron_de_diseno.estructural.Facade.dto;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutItemDTO {

    private Long productoId;
    private Long loteId;          // opcional, puede ser null
    private Integer cantidad;
    private BigDecimal precioUnitario;

    private Boolean requiereReceta;   // true si el producto requiere receta
    private Long recetaItemId;       // opcional: enlazar con RecetaItem
}
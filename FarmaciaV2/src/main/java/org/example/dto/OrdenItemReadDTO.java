package org.example.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenItemReadDTO {
    private Long id;
    private Long productoId;
    private Long loteId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private Boolean requiereReceta;
    private Long recetaItemId;
}

package org.example.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenItemCreateDTO {
    @NotNull private Long productoId;
    private Long loteId; // puede venir vacío y asignarse en picking
    @NotNull @Min(1) private Integer cantidad;
    @NotNull private BigDecimal precioUnitario;
    private Boolean requiereReceta;
    private Long recetaItemId; // si viene de receta
}

package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaItemDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;      // ID del producto (medicamento) a dispensar

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;     // número de unidades (cajas, tabletas, etc.)

    @NotBlank(message = "La dosis es obligatoria")
    private String dosis;         // ej: "500 mg"

    @NotBlank(message = "La frecuencia es obligatoria")
    private String frecuencia;    // ej: "cada 8 horas"

    @NotNull(message = "La duración en días es obligatoria")
    @Min(value = 1, message = "La duración mínima es 1 día")
    private Integer duracionDias; // ej: 7 días, 10 días, etc.
}

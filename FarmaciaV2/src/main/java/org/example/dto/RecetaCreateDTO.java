package org.example.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RecetaCreateDTO {
    private Long pacienteId; // opcional si es venta libre
    private Long medicoId;

    @NotNull
    private LocalDate fechaEmision;

    @Min(0)
    private Integer validezDias;

    private String observaciones;

    @NotEmpty
    private List<RecetaItemDTO> items;
}

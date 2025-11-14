package org.example.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CompraCreateDTO {
    @NotNull private Long proveedorId;
    @NotNull private LocalDate fecha;
    private String numeroDoc;
    private String observaciones;

    @NotEmpty
    private List<CompraItemCreateDTO> items;
}

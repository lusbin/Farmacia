package org.example.dto;

import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LibroControlDTO {
    private Long id;
    private LocalDate fecha;
    private Long productoId;
    private Long loteId;
    private Long pacienteId;
    private Long recetaId;
    private Integer cantidad;
    private String responsable;
    private String observaciones;
}

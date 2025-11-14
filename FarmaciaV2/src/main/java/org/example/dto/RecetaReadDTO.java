package org.example.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RecetaReadDTO {
    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private LocalDate fechaEmision;
    private Integer validezDias;
    private String observaciones;
    private List<RecetaItemDTO> items;
}

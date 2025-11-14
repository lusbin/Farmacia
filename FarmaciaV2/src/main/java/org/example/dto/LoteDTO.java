package org.example.dto;

import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoteDTO {
    private Long id;
    private Long productoId;
    private String numeroLote;
    private LocalDate fechaVencimiento;
    private String ubicacion;
}

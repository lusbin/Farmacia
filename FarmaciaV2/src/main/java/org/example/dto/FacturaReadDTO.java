package org.example.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FacturaReadDTO {
    private Long id;
    private Long ordenId;
    private String folio;
    private BigDecimal monto;
    private String estado; // EMITIDA, ANULADA
    private LocalDate fechaEmision;
}

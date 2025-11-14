package org.example.dto;

import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StockMovimientoDTO {
    private Long id;
    private Long loteId;
    private String tipo; // IN, OUT, AJUSTE
    private Integer cantidad;
    private LocalDate fecha;
    private String referencia;
    private String motivo;
    private Long usuarioId;
}

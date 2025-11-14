package org.example.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PagoReadDTO {
    private Long id;
    private Long ordenId;
    private String metodo;
    private BigDecimal monto;
    private String estado;
    private String transaccionRef;
}

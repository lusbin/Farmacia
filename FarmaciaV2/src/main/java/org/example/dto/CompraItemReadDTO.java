package org.example.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CompraItemReadDTO {
    private Long id;
    private Long loteId;
    private Integer cantidad;
    private BigDecimal costoUnitario;
}

package org.example.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CompraItemCreateDTO {
    @NotNull private Long loteId;
    @NotNull @Min(1) private Integer cantidad;
    @NotNull private BigDecimal costoUnitario;
}

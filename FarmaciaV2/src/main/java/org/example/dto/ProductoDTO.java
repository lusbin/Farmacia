package org.example.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoDTO {
    private Long id;
    private String sku;
    private String nombre;
    private String principioActivo;
    private String unidad;
    private String presentacion;
    private BigDecimal ivaPorcentaje;
    private Boolean esControlado;
    private Boolean esOtc;
}

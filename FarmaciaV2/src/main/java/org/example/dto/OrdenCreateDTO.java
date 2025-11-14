package org.example.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenCreateDTO {
    private Long pacienteId; // null si venta OTC
    @NotBlank private String canal;  // MOSTRADOR/ONLINE
    @NotBlank private String creadoPor;

    @NotEmpty
    private List<OrdenItemCreateDTO> items;

    // opcional si quieres calcular en backend:
    private BigDecimal totalBruto;
    private BigDecimal totalImpuestos;
    private BigDecimal totalNeto;
}

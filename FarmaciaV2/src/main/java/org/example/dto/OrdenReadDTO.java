package org.example.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenReadDTO {
    private Long id;
    private Long pacienteId;
    private String estado;
    private String canal;
    private String creadoPor;
    private BigDecimal totalBruto;
    private BigDecimal totalImpuestos;
    private BigDecimal totalNeto;
    private List<OrdenItemReadDTO> items;
}

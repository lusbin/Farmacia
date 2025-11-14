package org.example.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CompraReadDTO {
    private Long id;
    private Long proveedorId;
    private LocalDate fecha;
    private String numeroDoc;
    private String observaciones;
    private List<CompraItemReadDTO> items;
}

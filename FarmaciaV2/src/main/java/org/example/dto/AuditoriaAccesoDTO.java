package org.example.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditoriaAccesoDTO {
    private Long id;
    private Long usuarioId;
    private String recursoId;
    private String accion;
    private String ip;
    private String detalle;
    private LocalDateTime fecha;
}

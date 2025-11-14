package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "auditoria_accesos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditoriaAcceso {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne @JoinColumn(name="usuario_id")
    private Usuario usuario;

    @Column(length = 80) private String recursoId;
    @Column(length = 20) private String accion; // LOGIN, LECTURA...
    @Column(length = 60) private String ip;
    @Column(length = 200) private String detalle;

    private LocalDateTime fecha;
}

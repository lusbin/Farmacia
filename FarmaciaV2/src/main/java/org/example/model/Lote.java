package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "lotes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Lote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne(optional = false) @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name="numero_lote", length = 60)
    private String numeroLote;

    @Column(name="fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(length = 120)
    private String ubicacion;
}

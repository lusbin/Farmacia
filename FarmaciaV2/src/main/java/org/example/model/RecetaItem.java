package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "receta_items")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RecetaItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne(optional = false) @JoinColumn(name="receta_id")
    private Receta receta;

    @ManyToOne(optional = false) @JoinColumn(name="producto_id")
    private Producto producto;

    private Integer cantidad;

    @Column(length = 200)
    private String indicaciones;

    @Column(name="sustitucion_permitida")
    private Boolean sustitucionPermitida;
}

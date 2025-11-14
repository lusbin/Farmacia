package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "libro_control")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LibroControl {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    private LocalDate fecha;

    @ManyToOne @JoinColumn(name="producto_id")
    private Producto producto;

    @ManyToOne @JoinColumn(name="lote_id")
    private Lote lote;

    @ManyToOne @JoinColumn(name="paciente_id")
    private Paciente paciente;

    @ManyToOne @JoinColumn(name="receta_id")
    private Receta receta;

    private Integer cantidad;

    @Column(length = 120) private String responsable;
    @Column(length = 200) private String observaciones;
}

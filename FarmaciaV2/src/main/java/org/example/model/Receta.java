package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "recetas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Receta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne @JoinColumn(name="paciente_id")
    private Paciente paciente;

    @ManyToOne @JoinColumn(name="medico_id")
    private Medico medico;

    @Column(name="fecha_emision")
    private LocalDate fechaEmision;

    @Column(name="validez_dias")
    private Integer validezDias;

    @Column(length = 300)
    private String observaciones;
}

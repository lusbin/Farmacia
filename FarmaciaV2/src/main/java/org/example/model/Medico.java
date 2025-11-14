package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "medicos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Medico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name="registro_profesional", length = 60)
    private String registroProfesional;

    @Column(length = 100)
    private String especialidad;
}

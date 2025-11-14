package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "pacientes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Paciente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name="doc_identidad", length = 30, unique = true)
    private String docIdentidad;

    @Column(length = 120) private String nombre;
    private LocalDate fechaNacimiento;
    @Column(length = 20) private String sexo;
    @Column(length = 250) private String alergias;
}

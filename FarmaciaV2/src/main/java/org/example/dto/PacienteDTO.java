package org.example.dto;

import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PacienteDTO {
    private Long id;
    private String docIdentidad;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String alergias;
}

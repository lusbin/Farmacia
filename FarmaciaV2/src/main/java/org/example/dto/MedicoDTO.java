package org.example.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicoDTO {
    private Long id;
    private String nombre;
    private String registroProfesional;
    private String especialidad;
}

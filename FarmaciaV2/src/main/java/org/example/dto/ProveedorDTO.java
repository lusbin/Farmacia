package org.example.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProveedorDTO {
    private Long id;
    private String nombre;
    private String nit;
    private String telefono;
    private String email;
    private String direccion;
    private String estado;
}

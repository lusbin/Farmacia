package org.example.dto;

import lombok.*;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioReadDTO {
    private Long id;
    private String username;
    private String nombreCompleto;
    private String email;
    private String estado;
    private Set<RolDTO> roles;
}

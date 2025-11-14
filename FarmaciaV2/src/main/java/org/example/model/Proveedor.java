package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "proveedores")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Proveedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(nullable = false, length = 120)
    private String nombre;
    @Column(length = 30)  private String nit;
    @Column(length = 30)  private String telefono;
    @Column(length = 120) private String email;
    @Column(length = 200) private String direccion;
    @Column(length = 20)  private String estado;
}

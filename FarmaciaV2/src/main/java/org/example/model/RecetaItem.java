package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "receta_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    // estos TRES campos deben existir para que los set... funcionen
    private String dosis;
    private String frecuencia;
    private Integer duracionDias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id")
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;
}

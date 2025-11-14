package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "orden_items")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne(optional = false) @JoinColumn(name="orden_id")
    private Orden orden;

    @ManyToOne(optional = false) @JoinColumn(name="producto_id")
    private Producto producto;

    @ManyToOne @JoinColumn(name="lote_id")
    private Lote lote;

    private Integer cantidad;

    @Column(name="precio_unitario")
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;

    @Column(name="requiere_receta")
    private Boolean requiereReceta;

    @ManyToOne @JoinColumn(name="receta_item_id")
    private RecetaItem recetaItem;
}

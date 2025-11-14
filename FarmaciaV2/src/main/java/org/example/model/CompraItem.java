package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "compra_items")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CompraItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne(optional = false) @JoinColumn(name="compra_id")
    private Compra compra;

    @ManyToOne(optional = false) @JoinColumn(name="lote_id")
    private Lote lote;

    private Integer cantidad;

    @Column(name="costo_unitario")
    private BigDecimal costoUnitario;
}

package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "stock_movimientos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StockMovimiento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne(optional = false) @JoinColumn(name="lote_id")
    private Lote lote;

    @Column(length = 10) private String tipo; // IN, OUT, AJUSTE
    private Integer cantidad;

    private LocalDate fecha;

    @Column(length = 80) private String referencia;
    @Column(length = 120) private String motivo;

    @ManyToOne @JoinColumn(name="usuario_id")
    private Usuario usuario;
}

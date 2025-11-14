package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "facturas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Factura {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne(optional = false) @JoinColumn(name="orden_id")
    private Orden orden;

    @Column(length = 30) private String folio;
    private BigDecimal monto;
    @Column(length = 20) private String estado; // EMITIDA,ANULADA
    @Column(name="fecha_emision") private LocalDate fechaEmision;
}

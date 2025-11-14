package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "pagos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Pago {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne(optional = false) @JoinColumn(name="orden_id")
    private Orden orden;

    @Column(length = 20) private String metodo; // EFECTIVO,TARJETA
    private BigDecimal monto;
    @Column(length = 20) private String estado; // APROBADO,RECHAZADO
    @Column(name="transaccion_ref", length = 80)
    private String transaccionRef;
}

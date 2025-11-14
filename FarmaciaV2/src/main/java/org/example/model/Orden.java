package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "ordenes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Orden {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @ManyToOne @JoinColumn(name="paciente_id")
    private Paciente paciente;

    @Column(length = 20) private String estado; // CREADA,PAGADA,ANULADA
    @Column(length = 20) private String canal;  // MOSTRADOR,ONLINE
    @Column(name="creado_por", length = 60) private String creadoPor;

    @Column(name="total_bruto")     private BigDecimal totalBruto;
    @Column(name="total_impuestos") private BigDecimal totalImpuestos;
    @Column(name="total_neto")      private BigDecimal totalNeto;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenItem> items;
}

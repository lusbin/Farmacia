package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(length = 60, unique = true)
    private String sku;

    @Column(nullable = false, length = 160)
    private String nombre;

    @Column(name="principio_activo", length = 160)
    private String principioActivo;

    @Column(length = 30)
    private String unidad;

    @Column(length = 60)
    private String presentacion;

    @Column(name="iva_porcentaje")
    private BigDecimal ivaPorcentaje;

    @Column(name="es_controlado")
    private Boolean esControlado;

    @Column(name="es_otc")
    private Boolean esOtc;
}

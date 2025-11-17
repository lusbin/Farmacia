package org.example.patron_de_diseno.estructural.Facade.dto;


import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutResultDTO {

    private Long ordenId;
    private Long pagoId;
    private Long facturaId;

    private BigDecimal totalBruto;
    private BigDecimal totalImpuestos;
    private BigDecimal totalNeto;

    private String mensaje;    // algo tipo "Checkout exitoso"
}
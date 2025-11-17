package org.example.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.service.PrecioService;
import org.example.service.PrecioService.Totales;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/precios")
@RequiredArgsConstructor
public class PrecioController {

    private final PrecioService precioService;

    @PostMapping("/totales")
    public Totales calcularTotales(@RequestBody CalculoRequest request) {
        return precioService.calcularTotales(
                request.getPrecioUnitario(),
                request.getCantidad(),
                request.getIvaPorcentaje()
        );
    }

    @Data
    public static class CalculoRequest {
        private BigDecimal precioUnitario;
        private int cantidad;
        private BigDecimal ivaPorcentaje;
    }
}

package org.example.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.model.Orden;
import org.example.model.Pago;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapterFactory.TipoPasarela;
import org.example.repository.OrdenRepository;
import org.example.service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final OrdenRepository ordenRepository;

    @PostMapping
    public ResponseEntity<Pago> procesarPago(@RequestBody PagoRequest request) {

        Orden orden = ordenRepository.findById(request.getOrdenId())
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con id=" + request.getOrdenId()));

        Pago pago = pagoService.procesarPago(
                orden,
                request.getMonto(),
                request.getMetodo(),
                request.getTipoPasarela()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }

    @Data
    public static class PagoRequest {
        private Long ordenId;
        private String metodo;
        private BigDecimal monto;
        private TipoPasarela tipoPasarela;
    }
}

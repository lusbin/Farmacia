package org.example.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.model.Medico;
import org.example.model.Producto;
import org.example.model.Usuario;
import org.example.repository.MedicoRepository;
import org.example.repository.ProductoRepository;
import org.example.repository.UsuarioRepository;
import org.example.service.AlertaStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaStockController {

    private final AlertaStockService alertaStockService;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;

    @PostMapping("/stock-bajo")
    public ResponseEntity<Void> dispararAlertaStockBajo(@RequestBody AlertaStockRequest request) {

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Usuario usuario = null;
        if (request.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        }

        Medico medico = null;
        if (request.getMedicoId() != null) {
            medico = medicoRepository.findById(request.getMedicoId())
                    .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));
        }

        alertaStockService.evaluarYNotificarStockBajo(
                producto,
                request.getStockActual(),
                request.getUmbralMinimo(),
                usuario,
                medico
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Data
    public static class AlertaStockRequest {
        private Long productoId;
        private Integer stockActual;
        private Integer umbralMinimo;
        private Long usuarioId; // opcional
        private Long medicoId;  // opcional
    }
}

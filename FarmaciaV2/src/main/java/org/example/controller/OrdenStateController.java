package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Orden;
import org.example.service.OrdenStateService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenStateController {

    private final OrdenStateService ordenStateService;

    @PostMapping("/{id}/aprobar")
    public Orden aprobar(@PathVariable Long id) {
        return ordenStateService.aprobarOrden(id);
    }

    @PostMapping("/{id}/entregar")
    public Orden entregar(@PathVariable Long id) {
        return ordenStateService.entregarOrden(id);
    }

    @PostMapping("/{id}/anular")
    public Orden anular(@PathVariable Long id) {
        return ordenStateService.anularOrden(id);
    }
}

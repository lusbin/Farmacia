package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.RecetaCreateDTO;
import org.example.model.Receta;
import org.example.service.RecetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;

    @PostMapping
    public ResponseEntity<Receta> crear(@RequestBody RecetaCreateDTO dto) {
        Receta creada = recetaService.crearReceta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
}

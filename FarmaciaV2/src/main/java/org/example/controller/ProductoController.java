package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ProductoDTO;
import org.example.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
/*            */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Validated
public class ProductoController {

    private final ProductoService productoService;

    // Crear
    @PostMapping
    public ResponseEntity<ProductoDTO> create(@RequestBody ProductoDTO dto) {
        ProductoDTO creado = productoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> update(@PathVariable Long id, @RequestBody ProductoDTO dto) {
        ProductoDTO actualizado = productoService.update(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getById(id));
    }

    // Buscar por SKU
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductoDTO> getBySku(@PathVariable String sku) {
        Optional<ProductoDTO> opt = productoService.getBySku(sku);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Búsqueda por nombre (query param ?q=)
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> search(@RequestParam(name = "q", defaultValue = "") String q) {
        return ResponseEntity.ok(productoService.searchByNombre(q));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package org.example.integration;

import org.example.dto.ProductoDTO;
import org.example.service.ProductoService;
import org.example.service.exception.BusinessException;
import org.example.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ProductoServiceTest {

    @Autowired
    private ProductoService service;

    @Test
    @DisplayName("Crear y obtener producto por ID")
    void createAndGet() {
        ProductoDTO dto = ProductoDTO.builder()
                .sku("PRD-001")
                .nombre("Amoxicilina")
                .ivaPorcentaje(new BigDecimal("19"))
                .build();

        ProductoDTO creado = service.create(dto);
        assertThat(creado.getId()).isNotNull();

        var rec = service.getById(creado.getId());
        assertThat(rec.getNombre()).isEqualTo("Amoxicilina");
    }

    @Test
    @DisplayName("No permitir SKU duplicado (BusinessException)")
    void duplicatedSku() {
        service.create(ProductoDTO.builder().sku("DUP-01").nombre("A").build());
        assertThrows(BusinessException.class,
                () -> service.create(ProductoDTO.builder().sku("DUP-01").nombre("B").build()));
    }

    @Test
    @DisplayName("Eliminar y lanzar 404 al consultar")
    void deleteAndNotFound() {
        ProductoDTO creado = service.create(ProductoDTO.builder().sku("DEL-01").nombre("X").build());
        service.delete(creado.getId());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(creado.getId()));
    }
}

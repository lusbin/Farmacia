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


/*createAndGet(): guarda correctamente un producto/ genera un ID/ permita obtenerlo nuevamente*/
/*duplicatedSku(): Valida que el servicio detecta y bloquea SKUs duplicados con una BusinessException.*/
/*deleteAndNotFound(): eliminar un producto funciona, intentar consultarlo después lanza un ResourceNotFoundException*/

@SpringBootTest
@ActiveProfiles("test")
class ProductoServiceTest {

    @Autowired
    private ProductoService service;

    @Test
    @DisplayName("Crear y obtener producto por ID")
    void createAndGet() {
        // Construir DTO de entrada para crear el producto
        ProductoDTO dto = ProductoDTO.builder()
                .sku("PRD-001")
                .nombre("Amoxicilina")
                .ivaPorcentaje(new BigDecimal("19"))
                .build();

        // Crear el producto mediante el servicio
        ProductoDTO creado = service.create(dto);
        assertThat(creado.getId()).isNotNull();   // Debe generarse un ID

        // Recuperar el producto y verificar sus datos
        var rec = service.getById(creado.getId());
        assertThat(rec.getNombre()).isEqualTo("Amoxicilina");
    }

    @Test
    @DisplayName("No permitir SKU duplicado (BusinessException)")
    void duplicatedSku() {
        // Crear un producto inicial con un SKU
        service.create(ProductoDTO.builder().sku("DUP-01").nombre("A").build());

        // Intentar crear otro con el mismo SKU debe lanzar BusinessException
        assertThrows(BusinessException.class,
                () -> service.create(ProductoDTO.builder().sku("DUP-01").nombre("B").build()));
    }

    @Test
    @DisplayName("Eliminar y lanzar 404 al consultar")
    void deleteAndNotFound() {
        // Crear un producto para luego eliminarlo
        ProductoDTO creado = service.create(
                ProductoDTO.builder().sku("DEL-01").nombre("X").build()
        );

        // Eliminar el producto
        service.delete(creado.getId());

        // Luego de eliminarlo, buscarlo debe lanzar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class,
                () -> service.getById(creado.getId()));
    }
}

package org.example.util;

import org.example.dto.ProductoDTO;
import org.example.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProductoSkuNormalizationTest {

    @Autowired
    private ProductoService productoService;

    @Test
    @DisplayName("El Service normaliza el SKU: trim + upper-case")
    void skuSeNormalizaAlCrear() {
        // given: un SKU “sucio” (minúsculas y espacios)
        ProductoDTO entrada = ProductoTestUtil.dtoConSkuSucio("  abc-123  ");

        // when
        ProductoDTO creado = productoService.create(entrada);

        // then: el service lo normaliza según su lógica interna (trim + toUpperCase)
        assertThat(creado.getSku()).isEqualTo("ABC-123");
    }

    @Test
    @DisplayName("El Service normaliza el nombre con trim (no cambia mayúsculas)")
    void nombreSeTrimea() {
        ProductoDTO entrada = ProductoTestUtil.dtoBasico("x-1", "   Paracetamol  ");
        ProductoDTO creado = productoService.create(entrada);

        assertThat(creado.getNombre()).isEqualTo("Paracetamol");
    }
}

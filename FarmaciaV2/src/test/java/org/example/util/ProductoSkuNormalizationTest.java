package org.example.util;

import org.example.dto.ProductoDTO;
import org.example.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;
/*todo: Porque prueba lógica interna de transformación,
   no operaciones de negocio completas, ni BD, ni el repositorio, ni el controlador.*/



/*e test se centra en la lógica interna de normalización aplicada por ProductoService*/

/*Normalización de SKU():trim() → elimina espacios al inicio y final / toUpperCase() → convierte a mayúsculas */
/*Normalización del nombre: Solo aplica trim() / No modifica mayúsculas / minúsculas*/
/*Usa ProductoTestUtil: Esto confirma que las utilidades de creación de DTOs funcionan como generadores de datos sucios para las pruebas.*/


@SpringBootTest
@ActiveProfiles("test")
class ProductoSkuNormalizationTest {

    @Autowired
    private ProductoService productoService; // Se usa el service real para validar su normalización interna

    @Test
    @DisplayName("El Service normaliza el SKU: trim + upper-case")
    void skuSeNormalizaAlCrear() {
        // given: un SKU con espacios y minúsculas, generado por una utilidad de prueba
        ProductoDTO entrada = ProductoTestUtil.dtoConSkuSucio("  abc-123  ");

        // when: se crea el producto usando la lógica real del servicio
        ProductoDTO creado = productoService.create(entrada);

        // then: el SKU debe haber sido normalizado (sin espacios y en mayúsculas)
        assertThat(creado.getSku()).isEqualTo("ABC-123");
    }

    @Test
    @DisplayName("El Service normaliza el nombre con trim (no cambia mayúsculas)")
    void nombreSeTrimea() {
        // given: nombre con espacios antes y después
        ProductoDTO entrada = ProductoTestUtil.dtoBasico("x-1", "   Paracetamol  ");

        // when: creación real del producto
        ProductoDTO creado = productoService.create(entrada);

        // then: el nombre debe quedar sin espacios, conservando las mayúsculas originales
        assertThat(creado.getNombre()).isEqualTo("Paracetamol");
    }
}

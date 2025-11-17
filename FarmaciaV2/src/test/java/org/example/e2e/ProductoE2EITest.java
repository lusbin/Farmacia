package org.example.e2e;

import org.example.FarmaciaApplication;
import org.example.dto.ProductoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/*Este test valida todo el flujo real de crear un producto mediante la API REST y luego consultarlo por su ID.*/

/* Prueba la aplicacion completo de
* Controlador REST
*Servicio
*Repositorio
*Base de datos (perfil test)
* Serialización JSON
* Servidor HTTP (puerto aleatorio)
* */





@SpringBootTest(
        classes = FarmaciaApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class ProductoE2EITest {

    @LocalServerPort
    int port;

    @Autowired
    private TestRestTemplate rest;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    @DisplayName("E2E: Crear y consultar producto por id")
    void createAndGetById() {
        ProductoDTO in = ProductoDTO.builder()
                .sku("E2E-01").nombre("Omeprazol")
                .ivaPorcentaje(new BigDecimal("19"))
                .build();

        ResponseEntity<ProductoDTO> resCreate =
                rest.postForEntity(url("/api/productos"), in, ProductoDTO.class);

        assertThat(resCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long id = resCreate.getBody().getId();

        ResponseEntity<ProductoDTO> resGet =
                rest.getForEntity(url("/api/productos/" + id), ProductoDTO.class);

        assertThat(resGet.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resGet.getBody()).isNotNull();
        assertThat(resGet.getBody().getNombre()).isEqualTo("Omeprazol");
    }
}

package org.example.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.ProductoController;
import org.example.controller.advice.GlobalExceptionHandler;
import org.example.dto.ProductoDTO;
import org.example.service.ProductoService;
import org.example.service.exception.BusinessException;
import org.example.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/*Todo esta mockeado y solo se prueba la capa web del controlador que seria:*/
/*Rutas y respuestas HTTP
*JSON retornado
*Códigos de estado (200, 201, 400, 404)
*Conversiones de excepciones → GlobalExceptionHandler
* Parámetros, paths y búsquedas
* */

// Carga solo la capa web: controlador, validaciones y mapeo HTTP
@WebMvcTest(controllers = ProductoController.class)
// Importa el manejador global de excepciones para probar errores reales
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ProductoControllerWebMvcTest {

    @Autowired
    private MockMvc mvc;  // Simula peticiones HTTP sin levantar servidor real

    @Autowired
    private ObjectMapper objectMapper; // Convierte JSON ↔ objetos

    @MockBean
    private ProductoService productoService; // Servicio mockeado (sin lógica real)

    @Test
    @DisplayName("GET /api/productos/{id} devuelve 200 con el producto")
    void getByIdOk() throws Exception {
        // Respuesta simulada del servicio
        var dto = ProductoDTO.builder()
                .id(10L).sku("X1").nombre("Vitamina C")
                .ivaPorcentaje(new BigDecimal("0"))
                .build();
        Mockito.when(productoService.getById(10L)).thenReturn(dto);

        // Ejecutar GET y validar JSON de respuesta
        mvc.perform(get("/api/productos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Vitamina C"));
    }

    @Test
    @DisplayName("GET /api/productos/sku/{sku} 404 cuando no existe")
    void getBySkuNotFound() throws Exception {
        // El servicio retorna vacío → el controller debe responder 404
        Mockito.when(productoService.getBySku("NOPE")).thenReturn(Optional.empty());

        mvc.perform(get("/api/productos/sku/NOPE"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/productos?q= filtra por nombre")
    void searchByNombre() throws Exception {
        // Respuesta simulada para la búsqueda
        Mockito.when(productoService.searchByNombre("vit"))
                .thenReturn(List.of(
                        ProductoDTO.builder()
                                .id(1L).sku("VIT").nombre("Vitamina D")
                                .build()
                ));

        // Validar lista JSON con filtro q=vit
        mvc.perform(get("/api/productos").param("q", "vit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("VIT"));
    }

    @Test
    @DisplayName("POST /api/productos devuelve 201 y el creado")
    void createOk() throws Exception {
        var in = ProductoDTO.builder().sku("NEW-1").nombre("Paracetamol").build();
        var out = ProductoDTO.builder().id(5L).sku("NEW-1").nombre("Paracetamol").build();

        // Mock: el servicio devuelve el producto creado con ID
        Mockito.when(productoService.create(Mockito.any())).thenReturn(out);

        // Ejecutar POST y validar código 201 + ID asignado
        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("POST /api/productos devuelve 400 por BusinessException")
    void createDuplicatedSku() throws Exception {
        var in = ProductoDTO.builder().sku("DUP-1").nombre("X").build();

        // Simular regla de negocio violada
        Mockito.when(productoService.create(Mockito.any()))
                .thenThrow(new BusinessException("Ya existe un producto con SKU DUP-1"));

        // Validar que el controlador convierte la excepción en 400 + mensaje JSON
        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("SKU")));
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} devuelve 404 cuando no existe")
    void deleteNotFound() throws Exception {
        // Simula que el servicio lanza 404
        Mockito.doThrow(new ResourceNotFoundException("Producto id=99 no encontrado"))
                .when(productoService).delete(99L);

        // Validación: debe devolver 404 y JSON con "Not Found"
        mvc.perform(delete("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }


}


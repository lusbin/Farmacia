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

// Carga solo la capa web del controlador
@WebMvcTest(controllers = ProductoController.class)
@Import(GlobalExceptionHandler.class) // Advice real para mapear excepciones
@ActiveProfiles("test")
class ProductoControllerWebMvcTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @Test
    @DisplayName("GET /api/productos/{id} devuelve 200 con el producto")
    void getByIdOk() throws Exception {
        var dto = ProductoDTO.builder()
                .id(10L).sku("X1").nombre("Vitamina C")
                .ivaPorcentaje(new BigDecimal("0"))
                .build();

        Mockito.when(productoService.getById(10L)).thenReturn(dto);

        mvc.perform(get("/api/productos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Vitamina C"));
    }

    @Test
    @DisplayName("GET /api/productos/sku/{sku} 404 cuando no existe")
    void getBySkuNotFound() throws Exception {
        Mockito.when(productoService.getBySku("NOPE")).thenReturn(Optional.empty());

        mvc.perform(get("/api/productos/sku/NOPE"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/productos?q= filtra por nombre")
    void searchByNombre() throws Exception {
        Mockito.when(productoService.searchByNombre("vit"))
                .thenReturn(List.of(
                        ProductoDTO.builder()
                                .id(1L).sku("VIT").nombre("Vitamina D")
                                .build()
                ));

        mvc.perform(get("/api/productos").param("q", "vit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("VIT"));
    }

    @Test
    @DisplayName("POST /api/productos devuelve 201 y el creado")
    void createOk() throws Exception {
        var in = ProductoDTO.builder().sku("NEW-1").nombre("Paracetamol").build();
        var out = ProductoDTO.builder().id(5L).sku("NEW-1").nombre("Paracetamol").build();

        Mockito.when(productoService.create(Mockito.any())).thenReturn(out);

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

        Mockito.when(productoService.create(Mockito.any()))
                .thenThrow(new BusinessException("Ya existe un producto con SKU DUP-1"));

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
        Mockito.doThrow(new ResourceNotFoundException("Producto id=99 no encontrado"))
                .when(productoService).delete(99L);

        mvc.perform(delete("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}

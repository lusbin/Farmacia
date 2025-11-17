package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.PrecioService.Totales;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración MVC para PrecioController.
 *
 * Usa POST /api/precios/totales con JSON en el body:
 * {
 *   "precioUnitario": 100.00,
 *   "cantidad": 2,
 *   "ivaPorcentaje": 19
 * }
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class PrecioControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // DTO local solo para armar el JSON de request
    public static class CalculoRequest {
        public BigDecimal precioUnitario;
        public int cantidad;
        public BigDecimal ivaPorcentaje;
    }

    @Test
    void calcularTotales_devuelveJsonTotales() throws Exception {
        CalculoRequest req = new CalculoRequest();
        req.precioUnitario = new BigDecimal("100.00");
        req.cantidad = 2;
        req.ivaPorcentaje = new BigDecimal("19");

        mockMvc.perform(
                        post("/api/precios/totales")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(200.00))
                .andExpect(jsonPath("$.iva").value(38.00))
                .andExpect(jsonPath("$.total").value(238.00));
    }
}

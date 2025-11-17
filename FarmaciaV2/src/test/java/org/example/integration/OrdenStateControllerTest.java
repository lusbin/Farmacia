package org.example.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.OrdenStateController;
import org.example.model.Orden;
import org.example.service.OrdenStateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenStateController.class)
class OrdenStateControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrdenStateService ordenStateService;

    private Orden buildOrden(String estado) {
        Orden o = new Orden();
        o.setId(1L);
        o.setCreadoEn(LocalDateTime.now());
        o.setEstado(estado);
        return o;
    }

    @Test
    void aprobarOrden_devuelveOrdenAprobada() throws Exception {
        when(ordenStateService.aprobarOrden(1L))
                .thenReturn(buildOrden("APROBADA"));

        mockMvc.perform(post("/api/ordenes/{id}/aprobar", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADA"));

        verify(ordenStateService).aprobarOrden(1L);
    }

    @Test
    void entregarOrden_devuelveOrdenEntregada() throws Exception {
        when(ordenStateService.entregarOrden(1L))
                .thenReturn(buildOrden("ENTREGADA"));

        mockMvc.perform(post("/api/ordenes/{id}/entregar", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADA"));

        verify(ordenStateService).entregarOrden(1L);
    }
}

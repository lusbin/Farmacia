package org.example.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.RecetaController;
import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.Receta;
import org.example.model.RecetaItem;
import org.example.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecetaController.class)
class RecetaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecetaService recetaService;

    private RecetaCreateDTO buildDTO() {
        RecetaItemDTO item = RecetaItemDTO.builder()
                .productoId(100L)
                .cantidad(1)
                .dosis("500 mg")
                .frecuencia("cada 8h")
                .duracionDias(5)
                .build();

        return RecetaCreateDTO.builder()
                .pacienteId(1L)
                .medicoId(2L)
                .fechaEmision(LocalDate.now())
                .validezDias(10)
                .observaciones("test")
                .items(List.of(item))
                .build();
    }

    @Test
    void crearReceta_devuelve201YReceta() throws Exception {
        RecetaCreateDTO dto = buildDTO();

        Receta receta = new Receta();
        receta.setId(10L);
        RecetaItem item = new RecetaItem();
        item.setDosis("500 mg");
        receta.setItems(List.of(item));

        when(recetaService.crearReceta(any(RecetaCreateDTO.class))).thenReturn(receta);

        mockMvc.perform(post("/api/recetas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));

        verify(recetaService).crearReceta(any(RecetaCreateDTO.class));
    }
}
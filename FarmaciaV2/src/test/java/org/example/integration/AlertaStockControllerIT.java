package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Medico;
import org.example.model.Producto;
import org.example.model.Usuario;
import org.example.repository.MedicoRepository;
import org.example.repository.ProductoRepository;
import org.example.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración MVC para AlertaStockController.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AlertaStockControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    private Long productoId;
    private Long usuarioId;
    private Long medicoId;

    // DTO igual a AlertaStockRequest del controller
    public static class AlertaStockRequest {
        public Long productoId;
        public Integer stockActual;
        public Integer umbralMinimo;
        public Long usuarioId;
        public Long medicoId;
    }

    @BeforeEach
    void setUp() {
        // Producto
        Producto p = Producto.builder()
                .sku("ALERTA-001")
                .nombre("Amoxicilina 500mg")
                .build();
        p = productoRepository.save(p);
        productoId = p.getId();

        // Usuario
        Usuario u = Usuario.builder()
                .username("cajero1")
                .build();
        u = usuarioRepository.save(u);
        usuarioId = u.getId();

        // Medico
        Medico m = Medico.builder()
                .nombre("Dr. Test")
                .build();
        m = medicoRepository.save(m);
        medicoId = m.getId();
    }

    @Test
    void evaluarYNotificarStockBajo_devuelve202Accepted() throws Exception {
        AlertaStockRequest dto = new AlertaStockRequest();
        dto.productoId = productoId;
        dto.stockActual = 3;
        dto.umbralMinimo = 5;
        dto.usuarioId = usuarioId;
        dto.medicoId = medicoId;

        mockMvc.perform(post("/api/alertas/stock-bajo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isAccepted());
    }
}

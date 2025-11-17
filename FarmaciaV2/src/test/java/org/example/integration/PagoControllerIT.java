package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Orden;
import org.example.patron_de_diseno.estructural.adapter.PasarelaPagoAdapterFactory.TipoPasarela;
import org.example.repository.OrdenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class PagoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrdenRepository ordenRepository;

    private Long ordenIdCreada;

    public static class PagoRequestDTO {
        public Long ordenId;
        public String metodo;
        public BigDecimal monto;
        public TipoPasarela tipoPasarela;
    }

    @BeforeEach
    void setUp() {
        Orden orden = new Orden();
        orden.setCreadoEn(LocalDateTime.now());
        orden.setEstado("CREADA");
        orden.setCanal("MOSTRADOR");
        orden.setCreadoPor("TEST");
        orden = ordenRepository.save(orden);
        ordenIdCreada = orden.getId();
    }

    @Test
    void procesarPago_conStripe_devuelvePagoAprobado() throws Exception {

        PagoRequestDTO req = new PagoRequestDTO();
        req.ordenId = ordenIdCreada;
        req.metodo = "TARJETA";
        req.monto = new BigDecimal("150.00");
        req.tipoPasarela = TipoPasarela.STRIPE;

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.monto").value(150.00))
                .andExpect(jsonPath("$.estado").value("APROBADO"))
                .andExpect(jsonPath("$.transaccionRef").exists());
    }
}

package org.example.integration;

import org.example.model.Producto;
import org.example.repository.ProductoRepository;
import org.example.FarmaciaApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FarmaciaApplication.class)
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repo;

    @Test
    @DisplayName("Guardar y encontrar por SKU")
    void saveAndFindBySku() {
        Producto p = new Producto();
        p.setSku("ABC123");
        p.setNombre("Paracetamol 500 mg");
        p.setIvaPorcentaje(new BigDecimal("19"));
        repo.save(p);

        var opt = repo.findBySku("ABC123");
        assertThat(opt).isPresent();
        assertThat(opt.get().getNombre()).isEqualTo("Paracetamol 500 mg");
    }

    @Test
    @DisplayName("existsBySku retorna true si ya existe el SKU")
    void existsBySku() {
        Producto p = new Producto();
        p.setSku("DUPL-001");
        p.setNombre("Ibuprofeno 400 mg");
        repo.save(p);

        assertThat(repo.existsBySku("DUPL-001")).isTrue();
    }
}

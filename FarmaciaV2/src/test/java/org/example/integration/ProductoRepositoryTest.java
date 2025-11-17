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


/*Valida que el repositorio funcione correctamente con la BD real de pruebas.*/

/*La capa de acceso a datos
*El mapeo de entidades JPA
*La conexión con la base de datos del perfil test H2
*El comportamiento de los métodos save, findBySku, existsBySku
* */



@SpringBootTest(classes = FarmaciaApplication.class)
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repo;

    @Test
    @DisplayName("Guardar y encontrar por SKU")
    void saveAndFindBySku() {
        // Crear entidad de prueba
        Producto p = new Producto();
        p.setSku("ABC123");
        p.setNombre("Paracetamol 500 mg");
        p.setIvaPorcentaje(new BigDecimal("19"));

        // Guardar en la base de datos de pruebas
        repo.save(p);

        // Buscar por SKU usando el método del repositorio
        var opt = repo.findBySku("ABC123");

        // Verificar que existe y que el nombre coincide
        assertThat(opt).isPresent();
        assertThat(opt.get().getNombre()).isEqualTo("Paracetamol 500 mg");
    }

    @Test
    @DisplayName("existsBySku retorna true si ya existe el SKU")
    void existsBySku() {
        // Insertar un producto con un SKU conocido
        Producto p = new Producto();
        p.setSku("DUPL-001");
        p.setNombre("Ibuprofeno 400 mg");
        repo.save(p);

        // Verificar que existsBySku detecta el SKU en la base de datos
        assertThat(repo.existsBySku("DUPL-001")).isTrue();
    }
}
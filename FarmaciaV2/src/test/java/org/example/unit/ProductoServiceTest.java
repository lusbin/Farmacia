package org.example.unit;

import org.example.dto.ProductoDTO;
import org.example.model.Producto;
import org.example.repository.ProductoRepository;
import org.example.service.ProductoService;
import org.example.service.exception.BusinessException;
import org.example.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    // ---------- UPDATE ----------

    @Test
    void update_cuandoExiste_ySkuNoDuplicado_actualizaCampos() {
        // given: producto actual en BD
        Producto current = new Producto();
        current.setId(10L);
        current.setSku("AAA-001");
        current.setNombre(" Paracetamol 500mg ");
        current.setPrincipioActivo("Paracetamol");
        current.setUnidad("tableta");
        current.setPresentacion("Caja x 10");
        current.setIvaPorcentaje(new BigDecimal("19"));
        current.setEsControlado(false);
        current.setEsOtc(true);

        when(productoRepository.findById(10L)).thenReturn(Optional.of(current));

        // DTO con datos nuevos (incluyendo SKU con espacios y minúsculas)
        ProductoDTO dto = ProductoDTO.builder()
                .sku("  bbb-002  ")
                .nombre(" Paracetamol 650mg ")
                .principioActivo("Paracetamol")
                .unidad("tableta")
                .presentacion("Caja x 20")
                .ivaPorcentaje(new BigDecimal("19"))
                .esControlado(false)
                .esOtc(true)
                .build();

        // al normalizar, el SKU será "BBB-002"
        when(productoRepository.existsBySku("BBB-002")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        ProductoDTO result = productoService.update(10L, dto);

        // then
        // se normalizó y se actualizaron campos
        assertThat(result.getSku()).isEqualTo("BBB-002");
        assertThat(result.getNombre()).isEqualTo("Paracetamol 650mg");

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        Producto enviado = captor.getValue();

        assertThat(enviado.getId()).isEqualTo(10L);
        assertThat(enviado.getSku()).isEqualTo("BBB-002");
        assertThat(enviado.getNombre()).isEqualTo("Paracetamol 650mg");
        assertThat(enviado.getPresentacion()).isEqualTo("Caja x 20");
    }

    @Test
    void update_cuandoProductoNoExiste_lanzaResourceNotFound() {
        // given
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        ProductoDTO dto = ProductoDTO.builder()
                .sku("SKU-001")
                .nombre("Algo")
                .build();

        // when / then
        assertThatThrownBy(() -> productoService.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("id=99");

        verify(productoRepository, never()).save(any());
    }

    @Test
    void update_cuandoSkuNuevoYaExiste_lanzaBusinessException() {
        // given: producto actual con SKU AAA-001
        Producto current = new Producto();
        current.setId(10L);
        current.setSku("AAA-001");

        when(productoRepository.findById(10L)).thenReturn(Optional.of(current));

        // DTO con SKU diferente que ya existe en BD
        ProductoDTO dto = ProductoDTO.builder()
                .sku("BBB-002")
                .nombre("Paracetamol")
                .build();

        when(productoRepository.existsBySku("BBB-002")).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> productoService.update(10L, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("SKU BBB-002");

        verify(productoRepository, never()).save(any());
    }

    // ---------- GET BY SKU ----------

    @Test
    void getBySku_normalizaSkuYLlamaRepositorio() {
        // given
        Producto p = new Producto();
        p.setId(5L);
        p.setSku("ABC-123");
        p.setNombre("Ibuprofeno");

        when(productoRepository.findBySku("ABC-123"))
                .thenReturn(Optional.of(p));

        // when: SKU con espacios y minúsculas
        Optional<ProductoDTO> opt = productoService.getBySku("  abc-123  ");

        // then
        assertThat(opt).isPresent();
        assertThat(opt.get().getId()).isEqualTo(5L);
        assertThat(opt.get().getSku()).isEqualTo("ABC-123");
        assertThat(opt.get().getNombre()).isEqualTo("Ibuprofeno");

        verify(productoRepository).findBySku("ABC-123");
    }

    @Test
    void getBySku_cuandoSkuEsNull_devuelveOptionalVacio() {
        // given
        when(productoRepository.findBySku(null))
                .thenReturn(Optional.empty());

        // when
        Optional<ProductoDTO> opt = productoService.getBySku(null);

        // then
        assertThat(opt).isEmpty();
        verify(productoRepository).findBySku(null);
    }

    // ---------- SEARCH BY NOMBRE ----------

    @Test
    void searchByNombre_normalizaQueryYMapeaLista() {
        // given
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Paracetamol 500mg");

        Producto p2 = new Producto();
        p2.setId(2L);
        p2.setNombre("Paracetamol 650mg");

        when(productoRepository.findByNombreContainingIgnoreCase("paracetamol"))
                .thenReturn(Arrays.asList(p1, p2));

        // when: query con espacios extras
        List<ProductoDTO> result = productoService.searchByNombre("  paracetamol  ");

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ProductoDTO::getId)
                .containsExactlyInAnyOrder(1L, 2L);

        verify(productoRepository).findByNombreContainingIgnoreCase("paracetamol");
    }

    @Test
    void searchByNombre_cuandoQueryEsNull_usaStringVacio() {
        // given
        when(productoRepository.findByNombreContainingIgnoreCase(""))
                .thenReturn(Collections.emptyList());

        // when
        List<ProductoDTO> result = productoService.searchByNombre(null);

        // then
        assertThat(result).isEmpty();
        verify(productoRepository).findByNombreContainingIgnoreCase("");
    }

    // ---------- DELETE ----------

    @Test
    void delete_cuandoExiste_eliminaPorId() {
        // given
        when(productoRepository.existsById(10L)).thenReturn(true);

        // when
        productoService.delete(10L);

        // then
        verify(productoRepository).deleteById(10L);
    }

    @Test
    void delete_cuandoNoExiste_lanzaResourceNotFound() {
        // given
        when(productoRepository.existsById(10L)).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> productoService.delete(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("id=10");

        verify(productoRepository, never()).deleteById(anyLong());
    }
}
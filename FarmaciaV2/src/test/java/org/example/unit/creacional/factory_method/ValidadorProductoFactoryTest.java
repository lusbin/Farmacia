package org.example.unit.creacional.factory_method;

import org.example.model.Producto;
import org.example.patron_de_diseno.creacional.Factory_Method.ValidadorProducto;
import org.example.patron_de_diseno.creacional.Factory_Method.ValidadorProductoControlado;
import org.example.patron_de_diseno.creacional.Factory_Method.ValidadorProductoFactory;
import org.example.patron_de_diseno.creacional.Factory_Method.ValidadorProductoGenerico;
import org.example.patron_de_diseno.creacional.Factory_Method.ValidadorProductoOtc;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorProductoFactoryTest {

    @Test
    void obtenerValidador_devuelveControlado_cuandoEsControladoTrue() {
        // given: un producto marcado como "controlado" y NO OTC
        Producto p = new Producto();
        p.setEsControlado(true);
        p.setEsOtc(false);

        // when: pedimos al Factory el validador correspondiente
        ValidadorProducto validador = ValidadorProductoFactory.obtenerValidador(p);

        // then: verificamos que el Factory devolvió la clase correcta
        assertThat(validador).isInstanceOf(ValidadorProductoControlado.class);
    }

    @Test
    void obtenerValidador_devuelveOtc_cuandoEsOtcTrueYNoEsControlado() {
        // given: un producto marcado como OTC y NO controlado
        Producto p = new Producto();
        p.setEsControlado(false);
        p.setEsOtc(true);

        // when: pedimos al Factory el validador correspondiente
        ValidadorProducto validador = ValidadorProductoFactory.obtenerValidador(p);

        // then: el Factory debe devolver el validador específico para OTC
        assertThat(validador).isInstanceOf(ValidadorProductoOtc.class);
    }

    @Test
    void obtenerValidador_devuelveGenerico_cuandoNoEsControladoNiOtc() {
        // given: un producto que no es ni controlado ni OTC
        Producto p = new Producto();
        p.setEsControlado(false);
        p.setEsOtc(false);

        // when: pedimos el validador al Factory
        ValidadorProducto validador = ValidadorProductoFactory.obtenerValidador(p);

        // then: se debe retornar el validador genérico (caso por defecto)
        assertThat(validador).isInstanceOf(ValidadorProductoGenerico.class);
    }

    @Test
    void obtenerValidador_lanzaExcepcion_cuandoProductoEsNull() {
        // given: el producto es null (caso inválido)
        Producto producto = null;

        // when / then: al pedir un validador con producto null, debe lanzarse IllegalArgumentException
        assertThatThrownBy(() -> ValidadorProductoFactory.obtenerValidador(producto))
                .isInstanceOf(IllegalArgumentException.class)
                // comprobamos que el mensaje contenga una explicación clara
                .hasMessageContaining("no puede ser null");
    }
}

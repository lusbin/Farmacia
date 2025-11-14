package org.example.util;

import org.example.dto.ProductoDTO;

import java.math.BigDecimal;

/**
 * Helper SOLO PARA TESTS.
 * Construye DTOs de Producto con datos válidos y variantes de SKU.
 */
public final class ProductoTestUtil {

    private ProductoTestUtil() {}

    public static ProductoDTO dtoBasico(String sku, String nombre) {
        return ProductoDTO.builder()
                .sku(sku)
                .nombre(nombre)
                .principioActivo("GENERICA")
                .unidad("mg")
                .presentacion("tablet")
                .ivaPorcentaje(new BigDecimal("19"))
                .esControlado(false)
                .esOtc(true)
                .build();
    }

    public static ProductoDTO dtoConSkuSucio(String rawSku) {
        // nombre dummy para tests
        return dtoBasico(rawSku, "Producto Test");
    }
}

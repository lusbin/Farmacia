package org.example.patron_de_diseno.creacional.Factory_Method;


import org.example.model.Producto;

public class ValidadorProductoFactory {

    private ValidadorProductoFactory() {
        // evitar instanciación
    }

    public static ValidadorProducto obtenerValidador(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }

        Boolean esControlado = producto.getEsControlado();
        Boolean esOtc = producto.getEsOtc();

        if (Boolean.TRUE.equals(esControlado)) {
            return new ValidadorProductoControlado();
        }
        if (Boolean.TRUE.equals(esOtc)) {
            return new ValidadorProductoOtc();
        }

        return new ValidadorProductoGenerico();
    }
}
package org.example.patron_de_diseno.comportamental.Command;

import org.example.model.Orden;
import org.example.model.OrdenItem;
import org.example.model.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Command que agrega un ítem a la Orden y actualiza los totales.
 * Deshacer = quitar ese ítem y restaurar los totales previos.
 */
public class ComandoAgregarItemOrden implements ComandoPuntoVenta {

    private final Orden orden;
    private final Producto producto;
    private final int cantidad;
    private final BigDecimal precioUnitario;

    private OrdenItem itemCreado;
    private BigDecimal totalBrutoAnterior;
    private BigDecimal totalImpuestosAnterior;
    private BigDecimal totalNetoAnterior;

    public ComandoAgregarItemOrden(Orden orden,
                                   Producto producto,
                                   int cantidad,
                                   BigDecimal precioUnitario) {
        this.orden = orden;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    @Override
    public void ejecutar() {
        if (orden.getItems() == null) {
            orden.setItems(new ArrayList<>());
        }

        // Guardamos totales previos para poder deshacer
        totalBrutoAnterior     = valorSeguro(orden.getTotalBruto());
        totalImpuestosAnterior = valorSeguro(orden.getTotalImpuestos());
        totalNetoAnterior      = valorSeguro(orden.getTotalNeto());

        // Creamos el item
        itemCreado = new OrdenItem();
        itemCreado.setOrden(orden);
        itemCreado.setProducto(producto);
        itemCreado.setCantidad(cantidad);
        itemCreado.setPrecioUnitario(precioUnitario);

        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        itemCreado.setSubtotal(subtotal);

        // En este ejemplo simple no usamos receta ni lote
        itemCreado.setRequiereReceta(Boolean.FALSE);

        // Agregamos el item a la lista de la Órden
        orden.getItems().add(itemCreado);

        // Recalculamos totales simplificados:
        // totalBruto = suma subtotales
        // totalImpuestos = 0 (o podrías usar iva del producto)
        // totalNeto = totalBruto + totalImpuestos
        recalcularTotales();
    }

    @Override
    public void deshacer() {
        if (itemCreado != null && orden.getItems() != null) {
            orden.getItems().remove(itemCreado);
        }
        // Restaurar totales previos
        orden.setTotalBruto(totalBrutoAnterior);
        orden.setTotalImpuestos(totalImpuestosAnterior);
        orden.setTotalNeto(totalNetoAnterior);
    }

    @Override
    public String getNombre() {
        return "Agregar ítem " + (producto != null ? producto.getNombre() : "");
    }

    // --------- helpers privados ---------

    private void recalcularTotales() {
        List<OrdenItem> items = orden.getItems();
        if (items == null || items.isEmpty()) {
            orden.setTotalBruto(BigDecimal.ZERO);
            orden.setTotalImpuestos(BigDecimal.ZERO);
            orden.setTotalNeto(BigDecimal.ZERO);
            return;
        }

        BigDecimal totalBruto = BigDecimal.ZERO;
        for (OrdenItem it : items) {
            if (it.getSubtotal() != null) {
                totalBruto = totalBruto.add(it.getSubtotal());
            }
        }

        // Aquí podrías aplicar IVA, etc. Para el ejemplo, impuestos=0
        BigDecimal impuestos = BigDecimal.ZERO;
        BigDecimal neto = totalBruto.add(impuestos);

        orden.setTotalBruto(totalBruto);
        orden.setTotalImpuestos(impuestos);
        orden.setTotalNeto(neto);
    }

    private BigDecimal valorSeguro(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}

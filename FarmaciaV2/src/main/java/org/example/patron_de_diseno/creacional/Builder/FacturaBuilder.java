package org.example.patron_de_diseno.creacional.Builder;

import org.example.model.Factura;
import org.example.model.Orden;
import org.example.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/*Este factura builder a comparacion de @Builder de lombol / es una estructura paso a paso de una factura que
* calcula monto final aplica relgas como no dejar monto negativo requerir orden etc
* capsula complejidad de ensamblar una factura real */
public class FacturaBuilder {

    // --- Estado interno que vamos armando paso a paso ---

    private Orden orden;
    private String folio;
    private String estado = "EMITIDA";
    private LocalDate fechaEmision = LocalDate.now();

    // líneas de productos, descuentos y seguros que se irán agregando
    private final List<LineaFactura> lineas = new ArrayList<>();
    private final List<DescuentoFactura> descuentos = new ArrayList<>();
    private final List<SeguroFactura> seguros = new ArrayList<>();

    // --- Métodos "fluent" del Builder (devuelven this) ---

    public FacturaBuilder conOrden(Orden orden) {
        this.orden = orden;
        return this;
    }

    public FacturaBuilder conFolio(String folio) {
        this.folio = folio;
        return this;
    }

    public FacturaBuilder conEstado(String estado) {
        this.estado = estado;
        return this;
    }

    public FacturaBuilder conFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
        return this;
    }

    /**
     * Agrega una línea de producto a la factura.
     *
     * @param producto      producto de la farmacia
     * @param cantidad      cantidad del producto
     * @param precioUnitario precio unitario (sin descuentos ni seguros)
     */
    public FacturaBuilder agregarProducto(Producto producto,
                                          int cantidad,
                                          BigDecimal precioUnitario) {
        this.lineas.add(new LineaFactura(producto, cantidad, precioUnitario));
        return this;
    }

    /**
     * Agrega un descuento fijo en dinero (no porcentaje).
     */
    public FacturaBuilder agregarDescuentoMonto(BigDecimal monto, String motivo) {
        this.descuentos.add(new DescuentoFactura(monto, motivo));
        return this;
    }

    /**
     * Agrega un descuento en porcentaje sobre el subtotal bruto.
     * Ejemplo: 0.10 = 10%
     */
    public FacturaBuilder agregarDescuentoPorcentaje(BigDecimal porcentaje, String motivo) {
        this.descuentos.add(new DescuentoFactura(null, motivo, porcentaje));
        return this;
    }

    /**
     * Agrega un seguro asociado a la factura (por ejemplo seguro de envío, seguro adicional).
     */
    public FacturaBuilder agregarSeguro(String tipo, BigDecimal monto) {
        this.seguros.add(new SeguroFactura(tipo, monto));
        return this;
    }

    // --- Método final: construye la Factura lista ---

    public Factura build() {
        if (orden == null) {
            throw new IllegalStateException("La factura requiere una orden asociada.");
        }

        // 1) Subtotal bruto = suma de todas las líneas
        BigDecimal subtotal = lineas.stream()
                .map(LineaFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2) Descuentos en monto fijo
        BigDecimal descuentosFijos = descuentos.stream()
                .filter(d -> d.getMontoFijo() != null)
                .map(DescuentoFactura::getMontoFijo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3) Descuentos por porcentaje sobre el subtotal
        BigDecimal descuentosPorcentuales = descuentos.stream()
                .filter(d -> d.getPorcentaje() != null)
                .map(d -> subtotal.multiply(d.getPorcentaje()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescuentos = descuentosFijos.add(descuentosPorcentuales);

        // 4) Seguros (se suman al total final)
        BigDecimal totalSeguros = seguros.stream()
                .map(SeguroFactura::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5) Monto final = subtotal - descuentos + seguros
        BigDecimal montoFinal = subtotal
                .subtract(totalDescuentos)
                .add(totalSeguros);

        if (montoFinal.compareTo(BigDecimal.ZERO) < 0) {
            montoFinal = BigDecimal.ZERO; // por seguridad, no dejar totales negativos
        }

        // 6) Crear la entidad Factura usando lo calculado
        Factura factura = new Factura();
        factura.setOrden(orden);
        factura.setFolio(folio);
        factura.setEstado(estado);
        factura.setFechaEmision(fechaEmision);
        factura.setMonto(montoFinal);

        return factura;
    }
}

package org.example.patron_de_diseno.estructural.Facade;


import lombok.RequiredArgsConstructor;
import org.example.patron_de_diseno.estructural.Facade.dto.CheckoutItemDTO;
import org.example.patron_de_diseno.estructural.Facade.dto.CheckoutRequestDTO;
import org.example.patron_de_diseno.estructural.Facade.dto.CheckoutResultDTO;
import org.example.model.*;

import org.example.repository.*;
import org.example.service.PagoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FACADE:
 *  Simplifica el proceso de checkout / dispensación en una sola interfaz.
 *
 *  Desde afuera solo llamas a realizarCheckout(request) y por dentro se hace:
 *   1. Crear Orden + OrdenItems
 *   2. Calcular totales (bruto, impuestos, neto)
 *   3. Descontar stock (StockMovimiento)
 *   4. Registrar Pago usando el Adapter (Stripe / PayPal)
 *   5. Generar Factura
 */
@Service
@RequiredArgsConstructor
public class CheckoutFacade {

    private final PacienteRepository pacienteRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;                 // asumo que lo tienes
    private final OrdenRepository ordenRepository;
    private final StockMovimientoRepository stockMovimientoRepository;
    private final PagoService pagoService;                       // usa Adapter
    private final FacturaRepository facturaRepository;

    @Transactional
    public CheckoutResultDTO realizarCheckout(CheckoutRequestDTO request) {

        // 1) Cargar paciente (si viene)
        Paciente paciente = null;
        if (request.getPacienteId() != null) {
            paciente = pacienteRepository.findById(request.getPacienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        }

        // 2) Crear ORDEN vacía
        Orden orden = new Orden();
        orden.setCreadoEn(LocalDateTime.now());
        orden.setPaciente(paciente);
        orden.setEstado("CREADA");
        orden.setCanal(request.getCanal());
        orden.setCreadoPor(request.getCreadoPor());

        List<OrdenItem> items = new ArrayList<>();

        BigDecimal totalBruto = BigDecimal.ZERO;
        BigDecimal totalImpuestos = BigDecimal.ZERO;

        // 3) Construir OrdenItems y calcular totales
        for (CheckoutItemDTO itemDTO : request.getItems()) {

            Producto producto = productoRepository.findById(itemDTO.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            Lote lote = null;
            if (itemDTO.getLoteId() != null) {
                lote = loteRepository.findById(itemDTO.getLoteId())
                        .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
            }

            BigDecimal precioUnitario = itemDTO.getPrecioUnitario();
            BigDecimal cantidadBD = BigDecimal.valueOf(itemDTO.getCantidad());
            BigDecimal subtotal = precioUnitario.multiply(cantidadBD);

            // IVA = subtotal * (ivaPorcentaje / 100)
            BigDecimal iva = BigDecimal.ZERO;
            if (producto.getIvaPorcentaje() != null) {
                iva = subtotal.multiply(
                        producto.getIvaPorcentaje()
                                .divide(BigDecimal.valueOf(100))
                );
            }

            totalBruto = totalBruto.add(subtotal);
            totalImpuestos = totalImpuestos.add(iva);

            OrdenItem item = new OrdenItem();
            item.setCreadoEn(LocalDateTime.now());
            item.setOrden(orden);
            item.setProducto(producto);
            item.setLote(lote);
            item.setCantidad(itemDTO.getCantidad());
            item.setPrecioUnitario(precioUnitario);
            item.setSubtotal(subtotal);
            item.setRequiereReceta(itemDTO.getRequiereReceta());

            // Si quieres enlazar con RecetaItem (ya existente en tu modelo):
            if (itemDTO.getRecetaItemId() != null) {
                RecetaItem recetaItem = new RecetaItem();
                recetaItem.setId(itemDTO.getRecetaItemId());
                item.setRecetaItem(recetaItem); // referencia “ligera”
            }

            items.add(item);
        }

        BigDecimal totalNeto = totalBruto.add(totalImpuestos);

        orden.setItems(items);
        orden.setTotalBruto(totalBruto);
        orden.setTotalImpuestos(totalImpuestos);
        orden.setTotalNeto(totalNeto);

        // 4) Persistir ORDEN (cascade guarda los items)
        orden = ordenRepository.save(orden);

        // 5) Registrar movimientos de STOCK (OUT por cada ítem)
        for (OrdenItem item : items) {
            if (item.getLote() == null) continue; // si no hay lote, no movemos stock

            StockMovimiento mov = new StockMovimiento();
            mov.setCreadoEn(LocalDateTime.now());
            mov.setLote(item.getLote());
            mov.setTipo("OUT");
            mov.setCantidad(item.getCantidad());
            mov.setFecha(LocalDate.now());
            mov.setReferencia("ORDEN #" + orden.getId());
            mov.setMotivo("Checkout/Dispensación");

            stockMovimientoRepository.save(mov);
        }

        // 6) Registrar PAGO usando el Adapter (Stripe / PayPal)
        BigDecimal montoPago = request.getMontoPago() != null
                ? request.getMontoPago()
                : totalNeto;

        Pago pago = pagoService.procesarPago(
                orden,
                montoPago,
                request.getMetodoPago(),
                request.getTipoPasarela()
        );

        // 7) Generar FACTURA
        Factura factura = Factura.builder()
                .creadoEn(LocalDateTime.now())
                .orden(orden)
                .monto(totalNeto)
                .estado("EMITIDA")
                .fechaEmision(LocalDate.now())
                .folio("FAC-" + orden.getId() + "-" + System.currentTimeMillis())
                .build();

        factura = facturaRepository.save(factura);

        // 8) Devolver resultado simplificado
        return CheckoutResultDTO.builder()
                .ordenId(orden.getId())
                .pagoId(pago.getId())
                .facturaId(factura.getId())
                .totalBruto(totalBruto)
                .totalImpuestos(totalImpuestos)
                .totalNeto(totalNeto)
                .mensaje("Checkout completado correctamente")
                .build();
    }
}
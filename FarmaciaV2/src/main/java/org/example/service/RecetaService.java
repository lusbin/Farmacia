package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.RecetaCreateDTO;
import org.example.model.*;
import org.example.patron_de_diseno.comportamental.Chain_of_Responsibility.ContextoValidacionReceta;
import org.example.patron_de_diseno.comportamental.Chain_of_Responsibility.ValidadorAlergiasPaciente;
import org.example.patron_de_diseno.comportamental.Chain_of_Responsibility.ValidadorCadenaReceta;
import org.example.patron_de_diseno.comportamental.Chain_of_Responsibility.ValidadorDosisDuracion;
import org.example.patron_de_diseno.comportamental.Chain_of_Responsibility.ValidadorSeguro;
import org.example.patron_de_diseno.comportamental.Chain_of_Responsibility.ValidadorVigenciaReceta;
import org.example.patron_de_diseno.comportamental.Strategy.EstrategiaContextoClinico;
import org.example.patron_de_diseno.comportamental.Strategy.EstrategiaContextoClinicoFactory;
import org.example.patron_de_diseno.creacional.Factory_Method.ValidadorProducto;
import org.example.patron_de_diseno.creacional.Factory_Method.ValidadorProductoFactory;
import org.example.patron_de_diseno.creacional.singleton.FarmaciaConfigSingleton;
import org.example.repository.MedicoRepository;
import org.example.repository.PacienteRepository;
import org.example.repository.ProductoRepository;
import org.example.repository.RecetaRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ProductoRepository productoRepository;

    /**
     * Construye la cadena de responsabilidad:
     * Vigencia -> Alergias -> Dosis/Duración -> Seguro.
     */
    private ValidadorCadenaReceta buildCadenaValidacion() {
        return new ValidadorVigenciaReceta(
                new ValidadorAlergiasPaciente(
                        new ValidadorDosisDuracion(
                                new ValidadorSeguro(null)   // último eslabón
                        )
                )
        );
    }

    /**
     * Caso de uso principal para crear una receta.
     * Aquí se integran:
     *  - Singleton (FarmaciaConfigSingleton)  -> regla general de vigencia.
     *  - Factory Method (ValidadorProducto*)  -> reglas por tipo de medicamento.
     *  - Chain of Responsibility              -> receta + alergias + dosis + seguro.
     *  - Strategy (EstrategiaContextoClinico) -> dosis ajustada por contexto (pediátrico, geriátrico, adulto).
     */
    public Receta crearReceta(RecetaCreateDTO dto) {

        // 1. Singleton: vigencia general de la receta
        if (!FarmaciaConfigSingleton.getInstance()
                .recetaSigueVigente(dto.getFechaEmision())) {
            throw new IllegalArgumentException("La receta está vencida según las reglas sanitarias.");
        }

        // 2. Cargar PACIENTE (puede ser null para venta libre)
        Paciente pacienteTmp = null;
        if (dto.getPacienteId() != null) {
            pacienteTmp = pacienteRepository.findById(dto.getPacienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        }
        // para poder usarlo dentro del stream debe ser effectively final
        final Paciente paciente = pacienteTmp;

        // 3. Cargar MÉDICO (obligatorio)
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

        // 4. Construir entidad Receta (cabecera)
        Receta receta = new Receta();
        receta.setFechaEmision(dto.getFechaEmision());
        receta.setValidezDias(dto.getValidezDias());
        receta.setObservaciones(dto.getObservaciones());
        receta.setPaciente(paciente);
        receta.setMedico(medico);

        // 5. Chain of Responsibility (un solo objeto por receta)
        ValidadorCadenaReceta cadena = buildCadenaValidacion();

        // 6. Mapear items: Factory Method + Chain + Strategy
        receta.setItems(
                dto.getItems().stream().map(itemDTO -> {

                    // 6.1 Cargar producto
                    Producto producto = productoRepository.findById(itemDTO.getProductoId())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

                    // 6.2 Factory Method: validador de producto según esControlado / esOtc
                    ValidadorProducto validadorProducto =
                            ValidadorProductoFactory.obtenerValidador(producto);

                    // Aplica reglas sanitarias específicas del tipo de producto
                    validadorProducto.validar(producto, dto, itemDTO);

                    // 6.3 Chain of Responsibility: vigencia + alergias + dosis + seguro, etc.
                    ContextoValidacionReceta contexto =
                            new ContextoValidacionReceta(dto, itemDTO, paciente, producto);
                    cadena.validar(contexto);

                    // 6.4 Strategy: elegir estrategia pediátrica / geriátrica / adulto
                    //     según la edad del paciente y ajustar la dosis.
                    EstrategiaContextoClinico estrategia =
                            EstrategiaContextoClinicoFactory.obtenerEstrategia(paciente);

                    // Aquí solo usamos la dosis; el cálculo de precio unitario
                    // sería más natural en Orden/OrdenItem o Checkout.
                    String dosisAjustada = estrategia.ajustarDosis(
                            producto,
                            itemDTO.getDosis(),
                            paciente
                    );

                    // 6.5 Construir RecetaItem con la dosis ya ajustada por Strategy
                    RecetaItem item = new RecetaItem();
                    item.setCantidad(itemDTO.getCantidad());
                    item.setDosis(dosisAjustada);
                    item.setFrecuencia(itemDTO.getFrecuencia());
                    item.setDuracionDias(itemDTO.getDuracionDias());
                    item.setReceta(receta);
                    item.setProducto(producto);

                    return item;
                }).collect(Collectors.toList())
        );

        // 7. Guardar Receta + items en BD
        return recetaRepository.save(receta);
    }
}

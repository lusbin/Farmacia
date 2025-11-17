package org.example.patron_de_diseno.creacional.Factory_Method;

import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.Producto;

public class ValidadorProductoControlado implements ValidadorProducto {

    @Override
    public void validar(Producto producto,
                        RecetaCreateDTO recetaDTO,
                        RecetaItemDTO itemDTO) {

        if (recetaDTO.getMedicoId() == null) {
            throw new IllegalArgumentException(
                    "Los productos controlados requieren un médico responsable.");
        }
        if (recetaDTO.getPacienteId() == null) {
            throw new IllegalArgumentException(
                    "Los productos controlados requieren un paciente asociado.");
        }
        if (itemDTO.getDuracionDias() != null && itemDTO.getDuracionDias() > 30) {
            throw new IllegalArgumentException(
                    "La duración máxima para productos controlados es de 30 días.");
        }
    }
}
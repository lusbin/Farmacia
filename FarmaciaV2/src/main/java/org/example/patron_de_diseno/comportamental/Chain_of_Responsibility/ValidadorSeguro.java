package org.example.patron_de_diseno.comportamental.Chain_of_Responsibility;


import org.example.model.Paciente;
import org.example.model.Producto;

/**
 * Valida “seguro/cobertura” de forma simplificada:
 * - si el producto es CONTROLADO y el paciente NO tiene docIdentidad
 *   con prefijo "SS-", se considera que no tiene seguro/cobertura.
 */
public class ValidadorSeguro extends ValidadorBase {

    public ValidadorSeguro(ValidadorCadenaReceta siguiente) {
        super(siguiente);
    }

    @Override
    public void validar(ContextoValidacionReceta contexto) {
        Paciente paciente = contexto.getPaciente();
        Producto producto = contexto.getProducto();

        if (Boolean.TRUE.equals(producto.getEsControlado())) {
            String doc = paciente.getDocIdentidad();
            boolean tieneSeguro = doc != null && doc.startsWith("SS-");

            if (!tieneSeguro) {
                throw new IllegalArgumentException(
                        "Cadena: el paciente no tiene seguro/cobertura válida para medicamento controlado."
                );
            }
        }

        continuar(contexto);
    }
}
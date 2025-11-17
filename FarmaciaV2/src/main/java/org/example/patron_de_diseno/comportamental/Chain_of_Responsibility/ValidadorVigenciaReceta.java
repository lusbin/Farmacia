package org.example.patron_de_diseno.comportamental.Chain_of_Responsibility;


import org.example.patron_de_diseno.creacional.singleton.FarmaciaConfigSingleton;

/**
 * Valida que la receta esté vigente según las reglas sanitarias globales.
 */
public class ValidadorVigenciaReceta extends ValidadorBase {

    public ValidadorVigenciaReceta(ValidadorCadenaReceta siguiente) {
        super(siguiente);
    }

    @Override
    public void validar(ContextoValidacionReceta contexto) {
        var recetaDTO = contexto.getRecetaDTO();

        boolean vigente = FarmaciaConfigSingleton.getInstance()
                .recetaSigueVigente(recetaDTO.getFechaEmision());

        if (!vigente) {
            throw new IllegalArgumentException(
                    "Cadena: la receta no está vigente según las reglas sanitarias."
            );
        }

        // pasa al siguiente
        continuar(contexto);
    }
}
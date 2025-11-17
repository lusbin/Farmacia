package org.example.patron_de_diseno.comportamental.Chain_of_Responsibility;


import org.example.model.Paciente;
import org.example.model.Producto;

/**
 * Verifica que el paciente no sea alérgico al principio activo del producto.
 */
public class ValidadorAlergiasPaciente extends ValidadorBase {

    public ValidadorAlergiasPaciente(ValidadorCadenaReceta siguiente) {
        super(siguiente);
    }

    @Override
    public void validar(ContextoValidacionReceta contexto) {
        Paciente paciente = contexto.getPaciente();
        Producto producto = contexto.getProducto();

        String alergias = paciente.getAlergias();
        String principioActivo = producto.getPrincipioActivo();

        if (alergias != null && principioActivo != null) {
            // comparación simple tipo “contains”, en la vida real sería más elaborado
            if (alergias.toLowerCase().contains(principioActivo.toLowerCase())) {
                throw new IllegalArgumentException(
                        "Cadena: el paciente es alérgico al principio activo: " + principioActivo
                );
            }
        }

        continuar(contexto);
    }
}

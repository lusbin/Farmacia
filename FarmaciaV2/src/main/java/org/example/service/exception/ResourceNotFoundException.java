package org.example.service.exception;

/*  Cuando no se encuentra un recurso
"El producto/orden/paciente no existe"
404 Not Found */

/*Se usa para comunicar que una entidad específica no se encontró, en lugar de devolver null.*/
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}

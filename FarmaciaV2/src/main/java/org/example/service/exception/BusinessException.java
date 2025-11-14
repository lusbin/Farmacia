package org.example.service.exception;

/*  Cuando la operación viola reglas del negocio
"Ya existe ese SKU", "stock insuficiente", etc.
Manda un http   400 o 409 */

/*Sirve para indicar que la operación viola alguna regla definida por el negocio.
Por ejemplo:

Intentar registrar un producto con un SKU que ya existe.

Crear una orden con cantidad negativa.

Emitir una receta sin médico asignado.*/

public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}


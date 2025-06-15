package com.aluracursos.literalura.service;

public interface IConvierteDatos {
    //Creamos la interfaz generica, esta interfaz recibe nuestro archivo
    //json y una clase genericam que en este caso es resultados, pero
    //puede ser cualquier otra clase para libros o autor,
    //esto hace que el proyecto sea escalable
    <T> T convertirDatos(String json, Class<T> clase);
}

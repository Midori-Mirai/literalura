package com.aluracursos.literalura.model;

public enum CategoriaIdioma {
    ES("es", "Español"),
    EN("en", "Inglés"),
    FR("fr", "Francés"),
    PT("pt", "Portugués");
    private String idiomaAPIGutendex;
    private String idiomaEspanol;

    CategoriaIdioma(String idiomaAPIGutendex, String idiomaEspanol){
        this.idiomaAPIGutendex = idiomaAPIGutendex;
        this.idiomaEspanol = idiomaEspanol;
    }
    public static CategoriaIdioma fromString(String text) {
        for (CategoriaIdioma categoria : CategoriaIdioma.values()) {
            if (categoria.idiomaAPIGutendex.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }
    /*va a comparar la entrada del ususario con las categorias existentes, si existe regresa el género
     * sino existe regresa: categoria no encontrada*/
    public static CategoriaIdioma fromEspanol(String text) {
        for (CategoriaIdioma categoria : CategoriaIdioma.values()) {
            if (categoria.idiomaEspanol.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }
}

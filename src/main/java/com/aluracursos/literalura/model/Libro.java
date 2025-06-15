package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;

/*La relación de la tabla es Libros -> Autor = ManyToOne*/
@Entity
@Table(name = "Libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String titulo;
    private List<String> idioma;
    private boolean derechosAutor;
    private Integer descargas;
    /*Porque aún no voy a ocupar esta Lista de Autor, luego quitamos Transient para usar la tabla de Autor*/
//    @Transient
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;

    /*Constructor por default*/
    public Libro(){}

    /*Constructor personalizado*/
    public Libro(DatosLibro datos){
        this.titulo = datos.titulo();
        this.idioma = datos.idioma();
        this.derechosAutor = datos.derechosAutor();
        this.descargas = datos.descargas();
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getIdioma() {
        return idioma;
    }

    public void setIdioma(List<String> idioma) {
        this.idioma = idioma;
    }

    public boolean isDerechosAutor() {
        return derechosAutor;
    }

    public void setDerechosAutor(boolean derechosAutor) {
        this.derechosAutor = derechosAutor;
    }

    public Integer getDescargas() {
        return descargas;
    }

    public void setDescargas(Integer descargas) {
        this.descargas = descargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return  "\n***********Libro***********" +
                "\ntitulo = " + titulo +
                "\ndatosAutor = " + autor.getNombre() +
                "\nidioma = " + idioma +
                "\nderechosAutor = " + derechosAutor +
                "\ndescargas = " + descargas +
                "\n****************************";

    }
}

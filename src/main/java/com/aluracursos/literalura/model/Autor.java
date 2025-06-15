package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/*La relación de la tabla es de Autor -> Libros = OneToMany*/
@Entity
@Table(name = "autor_datos")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String nombre;
    private Integer fechaNac;
    private Integer fechaDeceso;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libro> libros = new ArrayList<>();

    /*Constructor por default*/
    public Autor(){}
    /*Constructor personalizado*/
    public Autor(String nombre, Integer fechaNac, Integer fechaDeceso){
        this.nombre = nombre;
        this.fechaNac = fechaNac;
        this.fechaDeceso = fechaDeceso;
    }

    public void setLibro(Libro libro) {
        libro.setAutor(this);
        this.libros.add(libro);
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getFechaNac() {
        return fechaNac;
    }

    public Integer getFechaDeceso() {
        return fechaDeceso;
    }

    @Override
    public String toString() {
        return  "\n°°°°°°°°°°°°°°Autor°°°°°°°°°°°°°°" +
                "\nNombre = " + nombre +
                "\nNacido en = " + fechaNac +
                "\nMuerte en = " + fechaDeceso +
                "\nlibros = " + libros.size() +
                "\n°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°";
    }
}

package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.CategoriaIdioma;
import com.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByIdioma(CategoriaIdioma idiomaBuscado);
    List<Libro> findByDerechosAutor(boolean copyright);
}

package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombreIgnoreCaseAndFechaNac(String nombre, Integer fechaNac);
    List<Autor> findByFechaNacGreaterThanEqual(Integer fechNac);
}
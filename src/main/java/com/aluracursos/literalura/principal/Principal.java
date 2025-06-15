package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    final String URL_BASE = "https://gutendex.com//books/";
    Scanner entrada = new Scanner(System.in);
    ConvierteDatos conversor = new ConvierteDatos();
    ConsumoAPI consumoApi = new ConsumoAPI();
    LibroRepository repository;
    AutorRepository autorRepository;
    private String nombreLibro;

    public Principal(LibroRepository repository, AutorRepository autorRepository) {
        this.repository = repository;
        this.autorRepository = autorRepository;
    }

    public void muestraelMenu(){
        var opcion = -1;

        while(opcion != 0){
            var menu = """
                    \n|||||****°°°°Bienvenidos al buscador de libros°°°°****|||||
                    Menú
                    1 - Ingresa el nombre del libro que deseas buscar.
                    2 - Mostrar libros registrados.
                    3 - Mostrar autores registrados.
                    4 - Mostrar autores vivos después un determinado año.
                    0 - Salir""";
            System.out.println(menu);
            opcion = entrada.nextInt();
            entrada.nextLine();
            switch (opcion){
                case 1:
                    guardarLibrosBuscados();
                    break;
                case 2:
                    mostrarLibrosGuardados();
                    break;
                case 3:
                    mostrarAutoresGuardados();
                    break;
                case 4:
                    mostrarAutoresPorFecha();
                case 0:
                    System.out.println("Byeeeee!!!");
                    break;
                default:
                    System.out.println("Opción no valida");
            }
        }
    }
    /*Buscar libro por título*/
    private DatosResultadoLibros getDatosBusqueda() {
        System.out.println("Ingresa el libro que quieras buscar: ");
        nombreLibro = entrada.nextLine();
        var json = consumoApi.obtenerDatosAPI(URL_BASE + "?search="
                + URLEncoder.encode(nombreLibro, StandardCharsets.UTF_8));
        DatosResultadoLibros librosResultado = conversor.convertirDatos(json, DatosResultadoLibros.class);
        return librosResultado;
    }
    /*Guardar los libros buscados*/
    private Libro guardarLibrosBuscados(){
        var libroResultado = getDatosBusqueda();
        Optional<DatosLibro> busquedaLibro = libroResultado.datosTodosLosLibros().stream()
                .filter(l -> l.titulo().toLowerCase()
                        .contains(nombreLibro.toLowerCase()))
                .findFirst();
        if(busquedaLibro.isPresent()){
            DatosLibro datos = busquedaLibro.get();
            Libro libro = new Libro(datos);
            /*Obtener el primer autor*/
            if(datos.autores() != null && !datos.autores().isEmpty()){
                DatosAutor datosAutor = datos.autores().get(0);

                /*Buscar si ya existe (por nombre y por nacimiento) en la base de datos*/
                Optional<Autor> autorBuscado = autorRepository
                        .findByNombreIgnoreCaseAndFechaNac(datosAutor.nombre(), datosAutor.fechaNac());

                Autor autor = autorBuscado.orElse(
                        new Autor(datosAutor.nombre(), datosAutor.fechaNac(), datosAutor.fechaDeceso())
                );

                /*Asignar autor al libro*/
                autor.setLibro(libro);
                autorRepository.save(autor);
            }
//            repository.save(libro);
            System.out.println("Libro encontrado ->" + libro);
            return libro;
        }else{
            System.out.println("Libro no encontrado");
            return null;
        }
    }

    /*Muestra los libros guardados en la DB*/
    private void mostrarLibrosGuardados(){
        List<Libro> libros = repository.findAll();

        libros.forEach(System.out::println);
    }

    private void mostrarAutoresGuardados(){
        List<Autor> autores = autorRepository.findAll();
        autores.stream().sorted(Comparator.comparing(Autor::getNombre))
                        .forEach(System.out::println);
//        autores.forEach(System.out::println);
    }

    private void mostrarAutoresPorFecha(){
        System.out.println("Ingresa el año de nacimiento del autor: ");
        var fechNac = entrada.nextInt();
        List<Autor> autorPorFecha = autorRepository
                .findByFechaNacGreaterThanEqual(fechNac);

        if(!autorPorFecha.isEmpty()){
            autorPorFecha.forEach(System.out::println);
        }else{
            System.out.println("No existen autores nacidos después de " + fechNac);
        }

    }
}

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

import java.util.concurrent.atomic.AtomicInteger;

public class Principal {
    final String URL_BASE = "https://gutendex.com//books/";
    Scanner entrada = new Scanner(System.in);
    ConvierteDatos conversor = new ConvierteDatos();
    ConsumoAPI consumoApi = new ConsumoAPI();
    LibroRepository repository;
    AutorRepository autorRepository;
    private String nombreLibro;
    List<Libro> libros;

    public Principal(LibroRepository repository, AutorRepository autorRepository) {
        this.repository = repository;
        this.autorRepository = autorRepository;
    }

    public void muestraelMenu(){
        var opcion = -1;
        boolean opcionValida = true;
        var menu = """
                    \n|||||****°°°°Bienvenidos al buscador de libros°°°°****|||||
                    Menú
                    1 -  Ingresa el nombre del libro que deseas buscar.
                    2 -  Mostrar libros registrados.
                    3 -  Mostrar autores registrados.
                    4 -  Mostrar autores vivos después un determinado año.
                    5 -  Mostrar libros por idioma.
                    6 -  Mostrar estadísticas de descargas de los libros registrados.
                    7 -  Top 10 de los libros más descargados.
                    8 -  Buscar autores almacenados por nombre.
                    9 -  Mostrar libros por derechos de autor.
                    10 - Mostrar autores vivos durante un periodo.
                    0 -  Salir""";

        var menuIdiomas = """
                \n-----Ingresa el idioma para buscar los libros-----
                es -> Español
                en -> Inglés
                fr -> Francés
                pt -> Portugués
                ->
                """;
        var menuDerechosAutor = """
                \nIngresa si deseas ver los libros almacenados que tienen o no tienen derechos de autor:
                1 - Libros con derechos de autor.
                2 - Libros sin derechos de autor.""";

        while(opcionValida){
            System.out.println(menu);
            if(entrada.hasNextInt()){
                opcion = entrada.nextInt();
                entrada.nextLine();

            }else{
                System.out.println("Ingresa únicamente las opciones del menú (números)");
                opcion = -1;
                entrada.nextLine();
            }

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
                    break;
                case 5:
                    System.out.println(menuIdiomas);
                    var opcionIdioma = entrada.nextLine();
                    mostraLisbrosPorIdioma(opcionIdioma);
                    break;
                case 6:
                    mostrarStatsDeLibrosEnBD();
                    break;
                case 7:
                    top10LibrosMasDescargados();
                    break;
                case 8:
                    buscarPorAutor();
                    break;
                case 9:
                    System.out.println(menuDerechosAutor);
                    var opcionCopyright = entrada.nextInt();
                    buscarLibrosPorDerechosDeAutor(opcionCopyright);
                    break;
                case 10:
                    mostrarAutoresEntreFechas();
                    break;
                case 0:
                    System.out.println("Byeeeee!!!");
                    opcionValida = false;
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
        libros = repository.findAll();
        libros.forEach(System.out::println);
        System.out.println("El total de libros guardados es: " + libros.size());
    }

    private void mostrarAutoresGuardados(){
        List<Autor> autores = autorRepository.findAll();
        autores.stream().sorted(Comparator.comparing(Autor::getNombre))
                .forEach(System.out::println);
        System.out.println("*****************************************************\n" +
                "Existen " + autores.size() + " registrados");
//        autores.forEach(System.out::println);
    }

    private void mostrarAutoresPorFecha(){
        System.out.println("Ingresa el año de nacimiento del autor: ");
        var fechNac = entrada.nextInt();
        List<Autor> autorPorFecha = autorRepository
                .findByFechaNacGreaterThanEqual(fechNac);

        if(!autorPorFecha.isEmpty()){
            autorPorFecha.forEach(System.out::println);
            System.out.println("Existen " + autorPorFecha.size() + (autorPorFecha.size()==1 ? " autor" : " autores"));
        }else{
            System.out.println("No existen autores nacidos después de " + fechNac);
        }
    }
    private void mostraLisbrosPorIdioma(String opcionIdioma){
        CategoriaIdioma idiomaBuscado = CategoriaIdioma.fromEspanol(opcionIdioma);
        List<Libro> librosPorIdioma = repository.findByIdioma(idiomaBuscado);
        librosPorIdioma.forEach(System.out::println);
        System.out.println("Existen " + librosPorIdioma.size() + (librosPorIdioma.size()==1 ? " libro" : " libros")
        + " en " + opcionIdioma);
    }

    private void mostrarStatsDeLibrosEnBD(){
        libros = repository.findAll();
        DoubleSummaryStatistics est = libros.stream()
                .filter(l -> l.getDescargas() > 0.0)
                .collect(Collectors.summarizingDouble(Libro::getDescargas));
        Optional<Libro> libroMasDescargado = libros.stream()
                .filter(l -> l.getDescargas() > 0.0)
                .max(Comparator.comparing(Libro::getDescargas));
        Optional<Libro> libroMenosDescargado = libros.stream()
                .filter(l -> l.getDescargas() > 0.0)
                .min(Comparator.comparing(Libro::getDescargas));

        System.out.println("---------------------------------------------------------------" +
                "\nLa media de las descargas de todos los libros almacenados es: "  + est.getAverage() +
                "\nCantidad de registros evaluados para calcular la media de las descargas es de: " +est.getCount() +
                "\n---------------------------------------------------------------");
        libroMasDescargado.ifPresent(l ->
                System.out.println("El libro almacenado con más descargas es: " + l.getTitulo()
                        + " con -> " + l.getDescargas() + " descargas."));
        libroMenosDescargado.ifPresent(l ->
                System.out.println("El libro almacenado con menos descargas es: " +l.getTitulo()
                        + " con -> " + l.getDescargas() + " descargas."));
    }

    public void top10LibrosMasDescargados(){
        AtomicInteger contador = new AtomicInteger(1);
        libros = repository.findAll();
        System.out.println("""
                ++++~~~~~~~~~~~~~~~~~~~~~~~~~++++
                Top 10 - Libros más descargados
                ++++~~~~~~~~~~~~~~~~~~~~~~~~~++++""");
        libros.stream().sorted(Comparator.comparing(Libro::getDescargas).reversed())
                .limit(10)
                .forEach(l -> System.out.println(contador.getAndIncrement() + ".- " +
                        l.getTitulo() + " -> " + l.getDescargas()));
    }

    public void buscarPorAutor(){
        System.out.println("Ingresa el nombre del autor que deseas consultar en la base de datos: ");
        var nombreAutor = entrada.nextLine();
        List<Autor> busquedaAutor = autorRepository.findByNombreContainsIgnoreCase(nombreAutor);
        busquedaAutor.forEach(a -> {System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°Autor°°°°°°°°°°°°°°°°°°°°°°°° \n- " + a.getNombre());
            System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°Libro(s)°°°°°°°°°°°°°°°°°°°°°°°°");
        a.getLibros().forEach(l -> System.out.println("- " + l.getTitulo()));
            System.out.println("\n");});
//        busquedaAutor.ifPresentOrElse(a -> {
//                    System.out.println("Autor: " + a.getNombre() + "\nLibros -> " + a.getLibros().stream()
//                            .map(Libro::getTitulo)
//                            .collect(Collectors.toList()));},
//                () -> {
//                    System.out.println("¬¬¬¬¬Autor no encontrado¬¬¬¬¬");
//                });
    }
    public void  buscarLibrosPorDerechosDeAutor(Integer opcionCopyright){
        switch (opcionCopyright){
            case 1:
                List<Libro> conDerechos = repository.findByDerechosAutor(true);
                System.out.println("Se encontraron: " + conDerechos.size() + " libro(s)" +
                "\n°°°Lista de libros que tienen copyright°°°°°");
                conDerechos.stream().map(Libro::getTitulo).forEach(l -> System.out.println("- " + l));
                break;
            case 2:
                List<Libro> sinDerechos = repository.findByDerechosAutor(false);
                System.out.println("Se encontraron: " + sinDerechos.size() + " libro(s)" +
                        "\n°°°Lista de libros que no tienen copyright°°°°°");
                sinDerechos.stream().map(Libro::getTitulo).forEach(l -> System.out.println("- " + l));
                break;
            default:
                System.out.println("opción no valida");
        }
    }
    public void mostrarAutoresEntreFechas(){
        System.out.println("Introduce la fecha de inicio del periodo: ");
        var fechaInicio = entrada.nextInt();
        entrada.nextLine();
        System.out.println("Introduce la fecha final del periodo: ");
        var fechaFin = entrada.nextInt();
        entrada.nextLine();

        List<Autor> autoresPorIntervalo = autorRepository
                .findByFechaNacLessThanEqualAndFechaDecesoGreaterThanEqual(fechaFin, fechaInicio);
        autoresPorIntervalo.forEach(a -> {
            System.out.println("*****************************************************");
            System.out.println("Autor: " + a.getNombre());
            System.out.println("Fecha de nacimiento: " + a.getFechaNac());
            System.out.println("Fecha de fallecimiento: " + a.getFechaDeceso());});
        System.out.println("*************************************************************** \n" +
                "En el periodo entre " + fechaInicio + " y " + fechaFin + " se encontraron "
                + autoresPorIntervalo.size() + (autoresPorIntervalo.size() == 1 ? " autor" : " autores") +
                " vivos.\n***************************************************************");
    }
}
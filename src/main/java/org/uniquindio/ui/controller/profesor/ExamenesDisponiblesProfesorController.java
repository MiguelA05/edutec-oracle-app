package org.uniquindio.ui.controller.profesor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
// Asegúrate que ExamenProfesorDTO esté definido, ya sea aquí o importado
// import org.uniquindio.model.dto.ExamenProfesorDTO;
import org.uniquindio.model.entity.academico.Curso;
import org.uniquindio.model.entity.evaluacion.Examen;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.repository.impl.CursoRepositoryImpl;
import org.uniquindio.repository.impl.ExamenRepositoryImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date; // Para el campo fecha en el DTO
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExamenesDisponiblesProfesorController {

    @FXML
    private BorderPane rootExamenesDisponibles;
    @FXML
    private ComboBox<Curso> comboFiltroCursoExamen;
    @FXML
    private Button btnRefrescarExamenes;
    @FXML
    private TableView<ExamenProfesorDTO> tablaExamenesDisponibles;
    @FXML
    private TableColumn<ExamenProfesorDTO, String> colNombreExamenDisp;
    @FXML
    private TableColumn<ExamenProfesorDTO, String> colCursoExamenDisp;
    @FXML
    private TableColumn<ExamenProfesorDTO, String> colFechaExamenDisp;
    @FXML
    private TableColumn<ExamenProfesorDTO, String> colHoraExamenDisp;
    @FXML
    private TableColumn<ExamenProfesorDTO, Integer> colDuracionExamenDisp;
    @FXML
    private TableColumn<ExamenProfesorDTO, Void> colAccionExamenDisp;

    private ObservableList<ExamenProfesorDTO> listaObservableExamenes = FXCollections.observableArrayList();
    private ObservableList<Curso> listaObservableCursos = FXCollections.observableArrayList();
    private List<ExamenProfesorDTO> todosLosExamenesCargadosParaFiltro = new ArrayList<>();

    private ExamenRepositoryImpl examenRepository;
    private CursoRepositoryImpl cursoRepository;

    private Profesor profesorLogueado;

    // DTO para la tabla. Puede estar en su propio archivo o como clase interna.
    public static class ExamenProfesorDTO {
        private String id;
        private String nombre;
        private String idCurso;
        private String nombreCurso;
        private java.util.Date fecha; // Usando java.util.Date para coincidir con tu uso
        private LocalTime hora;
        private int duracion;

        public ExamenProfesorDTO(String id, String nombre, String idCurso, String nombreCurso, java.util.Date fecha, LocalTime hora, int duracion) {
            this.id = id;
            this.nombre = nombre;
            this.idCurso = idCurso;
            this.nombreCurso = nombreCurso;
            this.fecha = fecha;
            this.hora = hora;
            this.duracion = duracion;
        }

        public String getId() { return id; }
        public String getNombre() { return nombre; }
        public String getIdCurso() { return idCurso; }
        public String getNombreCurso() { return nombreCurso; }
        public java.util.Date getFecha() { return fecha; }
        public LocalTime getHora() { return hora; }
        public int getDuracion() { return duracion; }

        public String getFechaFormateada() {
            if (fecha == null) return "N/A";
            LocalDate localDate = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return localDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        public String getHoraFormateada() {
            return hora != null ? hora.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "N/A";
        }
        @Override
        public String toString() { return nombre + " (" + nombreCurso + ")"; }
    }


    public void setProfesor(Profesor profesor) {
        this.profesorLogueado = profesor;

        if (this.profesorLogueado != null && this.profesorLogueado.getCedula() != 0) {
            System.out.println("Profesor establecido en ExamenesDisponiblesController: " + this.profesorLogueado.getNombre() + " (Cédula: " + this.profesorLogueado.getCedula() +")");

            // Inicializar repositorios
            examenRepository = new ExamenRepositoryImpl();
            cursoRepository = new CursoRepositoryImpl();

            // Configurar UI y cargar datos iniciales
            configurarColumnasTabla();
            configurarColumnaAcciones();
            cargarCursosParaFiltro(); // Cargar cursos primero
            cargarExamenesDelProfesor(); // Luego exámenes, que pueden depender de los nombres de curso

            comboFiltroCursoExamen.setOnAction(event -> aplicarFiltroLocal());
            tablaExamenesDisponibles.setItems(listaObservableExamenes);

            actualizarPlaceholderTabla();

        } else {
            System.err.println("Error: Objeto Profesor es nulo o cédula inválida en setProfesor.");
            limpiarVistaPorErrorDeProfesor();
        }
    }

    private void limpiarVistaPorErrorDeProfesor() {
        if (listaObservableExamenes != null) listaObservableExamenes.clear();
        if (listaObservableCursos != null) listaObservableCursos.clear();
        if (todosLosExamenesCargadosParaFiltro != null) todosLosExamenesCargadosParaFiltro.clear();
        if (tablaExamenesDisponibles != null) {
            tablaExamenesDisponibles.setPlaceholder(new Label("Información del profesor no disponible para cargar exámenes."));
        }
    }

    private void actualizarPlaceholderTabla() {
        if (tablaExamenesDisponibles != null) {
            if (todosLosExamenesCargadosParaFiltro.isEmpty() && profesorLogueado != null) {
                tablaExamenesDisponibles.setPlaceholder(new Label("No hay exámenes creados por este profesor."));
            } else if (listaObservableExamenes.isEmpty() && profesorLogueado != null) {
                tablaExamenesDisponibles.setPlaceholder(new Label("Ningún examen coincide con el filtro actual."));
            } else if (profesorLogueado == null) {
                tablaExamenesDisponibles.setPlaceholder(new Label("Información del profesor no disponible."));
            } else {
                tablaExamenesDisponibles.setPlaceholder(new Label("No hay exámenes para mostrar."));
            }
        }
    }


    @FXML
    public void initialize() {
        // La inicialización principal de datos se mueve a setProfesor()
        // para asegurar que profesorLogueado esté disponible.
        // Aquí solo configuraciones que no dependan del profesor.
        System.out.println("ExamenesDisponiblesProfesorController: initialize() llamado.");
    }

    private void configurarColumnasTabla() {
        colNombreExamenDisp.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCursoExamenDisp.setCellValueFactory(new PropertyValueFactory<>("nombreCurso"));
        colFechaExamenDisp.setCellValueFactory(new PropertyValueFactory<>("fechaFormateada"));
        colHoraExamenDisp.setCellValueFactory(new PropertyValueFactory<>("horaFormateada"));
        colDuracionExamenDisp.setCellValueFactory(new PropertyValueFactory<>("duracion"));
    }

    private void configurarColumnaAcciones() {
        // ... (código de configurarColumnaAcciones sin cambios)
        colAccionExamenDisp.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(5, btnEditar, btnEliminar);

            {
                pane.setAlignment(Pos.CENTER);
                btnEditar.getStyleClass().add("button-editar-tabla");
                btnEliminar.getStyleClass().add("button-eliminar-tabla");

                btnEditar.setOnAction(event -> {
                    ExamenProfesorDTO examen = getTableView().getItems().get(getIndex());
                    handleEditarExamen(examen);
                });

                btnEliminar.setOnAction(event -> {
                    ExamenProfesorDTO examen = getTableView().getItems().get(getIndex());
                    handleEliminarExamen(examen);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void cargarCursosParaFiltro() {
        if (profesorLogueado == null || profesorLogueado.getCedula() == 0) {
            System.err.println("cargarCursosParaFiltro: No se puede ejecutar porque profesorLogueado es nulo o cédula inválida.");
            listaObservableCursos.clear();
            return;
        }
        try {
            List<Curso> cursosDelProfesor = cursoRepository.listarCursosPorProfesor(profesorLogueado.getCedula());
            if (cursosDelProfesor == null) {
                cursosDelProfesor = new ArrayList<>();
                System.err.println("Advertencia: listarCursosPorProfesor devolvió null. Se usará una lista vacía.");
            }
            listaObservableCursos.setAll(cursosDelProfesor);
            comboFiltroCursoExamen.setItems(listaObservableCursos);
            comboFiltroCursoExamen.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Curso curso) {
                    return curso == null ? "Todos los cursos" : (curso.getNombre() != null ? curso.getNombre() : "Curso sin nombre");
                }
                @Override
                public Curso fromString(String string) {
                    return listaObservableCursos.stream()
                            .filter(c -> c != null && c.getNombre() != null && c.getNombre().equals(string))
                            .findFirst().orElse(null);
                }
            });
            comboFiltroCursoExamen.setPromptText("Todos los cursos");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Cargar Cursos", "No se pudieron cargar los cursos para el filtro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarExamenesDelProfesor() {
        if (profesorLogueado == null || profesorLogueado.getCedula() == 0) {
            System.err.println("cargarExamenesDelProfesor: No se puede ejecutar porque profesorLogueado es nulo o cédula inválida.");
            todosLosExamenesCargadosParaFiltro.clear();
            aplicarFiltroLocal(); // Para actualizar la tabla (mostrarla vacía)
            return;
        }
        try {
            List<Examen> examenesDesdeRepo = examenRepository.listarExamenesPorProfesor(profesorLogueado.getCedula());
            if (examenesDesdeRepo == null) {
                examenesDesdeRepo = new ArrayList<>();
                System.err.println("Advertencia: listarExamenesPorProfesor devolvió null. Se usará una lista vacía.");
            }
            todosLosExamenesCargadosParaFiltro.clear();

            for (Examen examenEntidad : examenesDesdeRepo) {
                if (examenEntidad == null) {
                    System.err.println("Advertencia: Se encontró una entidad Examen nula en la lista del repositorio.");
                    continue;
                }

                java.util.Date fechaUtil = null;
                if (examenEntidad.getFecha() != null) { // examenEntidad.getFecha() es java.sql.Date
                    fechaUtil = new java.util.Date(examenEntidad.getFecha().getTime()); // Convertir a java.util.Date
                }

                LocalTime hora = null;
                if (examenEntidad.getHora() != null) { // examenEntidad.getHora() es LocalDateTime
                    hora = examenEntidad.getHora().toLocalTime();
                }

                String nombreCurso = "Curso no asignado";
                Integer cursoIdObj = examenEntidad.getCursoId(); // Puede ser null si es Integer

                if (cursoIdObj != null && cursoIdObj != 0) {
                    try {
                        // Intenta obtener el curso de la lista ya cargada para el ComboBox (más eficiente)
                        Optional<Curso> cursoOpt = listaObservableCursos.stream()
                                .filter(c -> c.getIdCurso() == cursoIdObj)
                                .findFirst();
                        if (cursoOpt.isPresent() && cursoOpt.get().getNombre() != null) {
                            nombreCurso = cursoOpt.get().getNombre();
                        } else {
                            // Fallback: si no está en la lista (o la lista no está poblada aún), consulta el repo
                            // Esto podría ser menos eficiente si se hace para muchos exámenes
                            System.out.println("Curso ID " + cursoIdObj + " no encontrado en listaObservableCursos, consultando repositorio...");
                            Curso cursoDelRepo = cursoRepository.obtenerCursoPorId(cursoIdObj);
                            if (cursoDelRepo != null && cursoDelRepo.getNombre() != null) {
                                nombreCurso = cursoDelRepo.getNombre();
                            } else if (cursoDelRepo != null) {
                                nombreCurso = "Curso ID: " + cursoIdObj + " (Sin nombre en repo)";
                            } else {
                                nombreCurso = "Curso ID: " + cursoIdObj + " (No encontrado en repo)";
                            }
                        }
                    } catch (SQLException se) {
                        System.err.println("Error SQL al obtener curso por ID " + cursoIdObj + ": " + se.getMessage());
                        nombreCurso = "Error al cargar (ID: " + cursoIdObj + ")";
                    } catch (Exception ex) {
                        System.err.println("Error inesperado al obtener curso por ID " + cursoIdObj + ": " + ex.getMessage());
                        nombreCurso = "Error al cargar (ID: " + cursoIdObj + ")";
                    }
                } else {
                    nombreCurso = "Curso no asignado";
                }

                todosLosExamenesCargadosParaFiltro.add(new ExamenProfesorDTO(
                        String.valueOf(examenEntidad.getId()),
                        examenEntidad.getNombre() != null ? examenEntidad.getNombre() : "Examen sin nombre",
                        cursoIdObj != null ? String.valueOf(cursoIdObj) : "0",
                        nombreCurso,
                        fechaUtil,
                        hora,
                        examenEntidad.getTiempo() != null ? examenEntidad.getTiempo() : 0
                ));
            }
            aplicarFiltroLocal();
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar los exámenes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            actualizarPlaceholderTabla(); // Asegurar que el placeholder se actualice
        }
    }

    @FXML
    void handleRefrescarExamenes(ActionEvent event) {
        System.out.println("Refrescando lista de exámenes...");
        if (profesorLogueado != null && profesorLogueado.getCedula() != 0) {
            cargarCursosParaFiltro();
            cargarExamenesDelProfesor();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Lista Actualizada", "La lista de exámenes ha sido refrescada.");
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "No se puede refrescar", "Información del profesor no disponible.");
        }
    }

    private void aplicarFiltroLocal() {
        Curso cursoSeleccionado = comboFiltroCursoExamen.getValue();
        if (cursoSeleccionado == null) {
            listaObservableExamenes.setAll(todosLosExamenesCargadosParaFiltro);
        } else {
            List<ExamenProfesorDTO> examenesFiltrados = todosLosExamenesCargadosParaFiltro.stream()
                    .filter(examen -> examen.getIdCurso().equals(String.valueOf(cursoSeleccionado.getIdCurso())))
                    .collect(Collectors.toList());
            listaObservableExamenes.setAll(examenesFiltrados);
        }
        actualizarPlaceholderTabla();
    }

    private void handleEditarExamen(ExamenProfesorDTO examenDTO) {
        // ... (código de handleEditarExamen sin cambios significativos, pero asegúrate que CrearEditarExamenController.setProfesor exista)
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se puede editar el examen sin información del profesor.");
            return;
        }
        System.out.println("Editando examen: " + examenDTO.getNombre() + " ID: " + examenDTO.getId());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profesor/crear_editar_examen.fxml"));
            Parent root = loader.load();

            CrearEditarExamenController controller = loader.getController();
            controller.setProfesor(this.profesorLogueado);
            controller.cargarExamenParaEdicion(Integer.parseInt(examenDTO.getId()));

            Stage stage = new Stage();
            stage.setTitle("Editar Examen");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarExamenesDelProfesor();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error al intentar editar el examen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEliminarExamen(ExamenProfesorDTO examenDTO) {
        // ... (código de handleEliminarExamen sin cambios)
        Alert alertConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirmacion.setTitle("Confirmar Eliminación");
        alertConfirmacion.setHeaderText("Eliminar Examen: " + examenDTO.getNombre());
        alertConfirmacion.setContentText("¿Estás seguro de que deseas eliminar este examen? Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alertConfirmacion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean eliminado = examenRepository.eliminarExamenPorId(Integer.parseInt(examenDTO.getId()));
                if (eliminado) {
                    todosLosExamenesCargadosParaFiltro.removeIf(e -> e.getId().equals(examenDTO.getId()));
                    aplicarFiltroLocal();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Examen Eliminado", "El examen '" + examenDTO.getNombre() + "' ha sido eliminado.");
                }
            } catch (SQLException e) {
                if (e.getMessage() != null && e.getMessage().contains("tiene") && e.getMessage().contains("presentaciones")) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Eliminación Fallida", "No se pudo eliminar el examen: " + e.getMessage());
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo eliminar el examen: " + e.getMessage());
                }
                e.printStackTrace();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos", "El ID del examen no es un número válido para eliminar: " + examenDTO.getId());
                e.printStackTrace();
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
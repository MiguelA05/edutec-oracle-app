package org.uniquindio.ui.controller.estudiante;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.uniquindio.model.dto.ExamenDisponibleDTO;
import org.uniquindio.model.entity.academico.*;
import org.uniquindio.model.entity.evaluacion.Examen;
import org.uniquindio.model.entity.usuario.Estudiante;
import org.uniquindio.repository.impl.CursoRepositoryImpl;
import org.uniquindio.repository.impl.ExamenRepositoryImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ExamenesDisponiblesController implements Initializable {

    @FXML private ComboBox<Curso> comboFiltroCursoExamen;
    @FXML private TableView<ExamenDisponibleDTO> tablaExamenesDisponibles;
    @FXML private TableColumn<ExamenDisponibleDTO, String> colNombreExamenDisp;
    @FXML private TableColumn<ExamenDisponibleDTO, String> colCursoExamenDisp;
    @FXML private TableColumn<ExamenDisponibleDTO, String> colFechaExamenDisp;
    @FXML private TableColumn<ExamenDisponibleDTO, String> colHoraExamenDisp;
    @FXML private TableColumn<ExamenDisponibleDTO, Integer> colDuracionExamenDisp;
    @FXML private TableColumn<ExamenDisponibleDTO, Void> colAccionExamenDisp;

    private ObservableList<ExamenDisponibleDTO> listaExamenesDTO = FXCollections.observableArrayList();
    private Estudiante estudianteLogueado;
    private DashboardEstudianteController dashboardController; // Para volver al dashboard si es necesario

    private ExamenRepositoryImpl examenRepository = new ExamenRepositoryImpl();
    private CursoRepositoryImpl cursoRepository = new CursoRepositoryImpl(); // Para cargar cursos del estudiante

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaExamenesDisponibles();
        // La carga de datos se hará en initDataEstudiante
    }

    /**
     * Inicializa los datos del estudiante.
     * Este método es llamado desde el DashboardEstudianteController.
     * @param estudiante El estudiante que ha iniciado sesión.
     */
    public void initDataEstudiante(Estudiante estudiante) {
        this.estudianteLogueado = estudiante;
        cargarFiltroCursos();
        cargarExamenesDisponibles();
    }

    public void setDashboardController(DashboardEstudianteController dashboardController) {
        this.dashboardController = dashboardController;
    }


    private void configurarTablaExamenesDisponibles() {
        colNombreExamenDisp.setCellValueFactory(new PropertyValueFactory<>("nombreExamen"));
        colCursoExamenDisp.setCellValueFactory(new PropertyValueFactory<>("nombreCurso"));
        colFechaExamenDisp.setCellValueFactory(new PropertyValueFactory<>("fechaPresentacion"));
        colHoraExamenDisp.setCellValueFactory(new PropertyValueFactory<>("horaPresentacion"));
        colDuracionExamenDisp.setCellValueFactory(new PropertyValueFactory<>("tiempoDuracion"));

        colAccionExamenDisp.setCellFactory(param -> new TableCell<>() {
            private final Button btnPresentar = new Button("Presentar Examen");

            {
                btnPresentar.getStyleClass().add("button-presentar-examen");
                btnPresentar.setOnAction(event -> {
                    ExamenDisponibleDTO examenDTO = getTableView().getItems().get(getIndex());
                    handlePresentarExamen(examenDTO.getExamenOriginal());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnPresentar);
            }
        });
        tablaExamenesDisponibles.setItems(listaExamenesDTO);
        tablaExamenesDisponibles.setPlaceholder(new Label("No hay exámenes disponibles en este momento o con los filtros seleccionados."));
    }

    private void cargarFiltroCursos() {
        if (estudianteLogueado == null) return;
        try {
            List<Curso> cursosEstudiante = cursoRepository.listarCursosPorEstudiante(estudianteLogueado.getCedula());
            comboFiltroCursoExamen.setItems(FXCollections.observableArrayList(cursosEstudiante));
            comboFiltroCursoExamen.getItems().add(0, null); // Opción "Todos los cursos"
            comboFiltroCursoExamen.setConverter(new StringConverter<>() {
                @Override public String toString(Curso curso) {
                    return curso == null ? "Todos los cursos" : curso.getNombre();
                }
                @Override public Curso fromString(String string) { return null; }
            });
            comboFiltroCursoExamen.getSelectionModel().selectFirst(); // Seleccionar "Todos" por defecto
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los cursos del estudiante: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleRefrescarExamenes(ActionEvent event) {
        cargarExamenesDisponibles();
    }

    private void cargarExamenesDisponibles() {
        if (estudianteLogueado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Información Requerida", "No se ha identificado al estudiante.");
            return;
        }
        listaExamenesDTO.clear();
        try {
            Curso cursoSeleccionado = comboFiltroCursoExamen.getValue();
            Integer cursoIdFiltro = (cursoSeleccionado != null) ? cursoSeleccionado.getIdCurso() : null;

            // Llamar al repositorio para obtener la lista de Examen (entidad)
            List<Examen> examenesEntidad = examenRepository.listarExamenesDisponiblesParaEstudiante(estudianteLogueado.getCedula(), cursoIdFiltro);

            if (examenesEntidad != null && !examenesEntidad.isEmpty()) {
                for (Examen examen : examenesEntidad) {
                    // Necesitamos el nombre del curso para el DTO.
                    // Si listarExamenesDisponiblesParaEstudiante no devuelve el nombre del curso,
                    // tendríamos que obtenerlo aquí o modificar la función PL/SQL.
                    // Por ahora, asumimos que podemos obtenerlo o usar un placeholder.
                    String nombreCurso = "Curso Desconocido"; // Placeholder
                    if (examen.getCursoId() != null) {
                        // Podrías tener un método para obtener el nombre del curso por ID
                        // o el objeto Curso ya está cargado en el combo.
                        Curso cursoDelExamen = obtenerCursoDeLista(examen.getCursoId(), comboFiltroCursoExamen.getItems());
                        if (cursoDelExamen != null) {
                            nombreCurso = cursoDelExamen.getNombre();
                        }
                    }

                    listaExamenesDTO.add(new ExamenDisponibleDTO(
                            examen.getId(),
                            examen.getNombre(),
                            nombreCurso,
                            examen.getFecha(),
                            examen.getHora(),
                            examen.getTiempo(),
                            examen.getNumeroPreguntas(),
                            examen
                    ));
                }
            }
            if (listaExamenesDTO.isEmpty()){
                tablaExamenesDisponibles.setPlaceholder(new Label("No hay exámenes disponibles para los filtros seleccionados."));
            }

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar los exámenes disponibles: " + e.getMessage());
            e.printStackTrace();
            tablaExamenesDisponibles.setPlaceholder(new Label("Error al cargar exámenes."));
        }
    }

    private Curso obtenerCursoDeLista(int cursoId, List<Curso> listaCursos) {
        if (listaCursos == null) return null;
        for (Curso c : listaCursos) {
            if (c != null && c.getIdCurso() == cursoId) {
                return c;
            }
        }
        return null;
    }


    private void handlePresentarExamen(Examen examenAPresentar) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Iniciar Examen");
        confirmacion.setHeaderText("Va a iniciar el examen: " + examenAPresentar.getDescripcion());
        confirmacion.setContentText("Tendrá " + examenAPresentar.getTiempo() + " minutos para completarlo una vez iniciado.\n¿Está seguro de que desea comenzar ahora?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                String ipAddress = "127.0.0.1";
                try {
                    ipAddress = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    System.err.println("Advertencia: No se pudo obtener la IP local automáticamente, usando IP por defecto ("+ ipAddress +"): " + e.getMessage());
                }

                int idPresentacion = examenRepository.iniciarPresentacionExamen(
                        estudianteLogueado.getCedula(),
                        examenAPresentar.getId(),
                        ipAddress // Se pasa la IP al método del repositorio
                );

                if (idPresentacion > 0) {
                    // Navegar a la pantalla de presentación del examen
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/estudiante/presentacion_examen.fxml"));
                    Parent rootPresentacion = loader.load();

                    PresentacionExamenController presentacionController = loader.getController();
                    presentacionController.initData(estudianteLogueado, examenAPresentar.getId(), idPresentacion, examenAPresentar);

                    Stage stage = (Stage) (tablaExamenesDisponibles != null ? tablaExamenesDisponibles.getScene().getWindow() : new Stage());
                    Scene scene = new Scene(rootPresentacion);
                    stage.setScene(scene);
                    stage.setTitle("EduTec - Presentando Examen: " + examenAPresentar.getDescripcion());
                    if (tablaExamenesDisponibles == null) stage.show();

                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al Iniciar", "No se pudo iniciar el examen. Verifique si cumple los requisitos o si ya tiene una presentación activa/finalizada.");
                }

            } catch (SQLException e) {
                String mensajeError = e.getMessage();
                if (mensajeError != null && mensajeError.contains("ORA-20")) {
                    mensajeError = mensajeError.substring(mensajeError.indexOf("ORA-20") + 9);
                    int endPos = mensajeError.indexOf("ORA-");
                    if (endPos != -1) {
                        mensajeError = mensajeError.substring(0, endPos).trim();
                    }
                } else {
                    mensajeError = "Error de base de datos al intentar iniciar el examen.";
                }
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", mensajeError);
                e.printStackTrace();
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir la pantalla de presentación del examen.");
                e.printStackTrace();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

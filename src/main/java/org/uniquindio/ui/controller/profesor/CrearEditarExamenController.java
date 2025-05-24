package org.uniquindio.ui.controller.profesor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
// FXMLLoader, Parent, Scene, Modality, Stage for dialogs/new windows if needed for question management
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.uniquindio.model.dto.PreguntaExamenDTO;
import org.uniquindio.model.entity.academico.Contenido;
import org.uniquindio.model.entity.academico.Curso;
import org.uniquindio.model.entity.evaluacion.Examen; // Corrected import
import org.uniquindio.model.entity.catalogo.Categoria; // Assuming Categoria is in .catalogo
import org.uniquindio.model.entity.evaluacion.Pregunta;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.repository.impl.CursoRepositoryImpl;
import org.uniquindio.repository.impl.ExamenRepositoryImpl;
import org.uniquindio.ui.controller.profesor.dialogs.CrearPreguntaDialogController;
import org.uniquindio.ui.controller.profesor.dialogs.SeleccionarPreguntaDialogController;
import org.uniquindio.model.dto.PreguntaSeleccionDTO;

import java.sql.SQLException;
import java.util.List;

// Importa tus clases de Repositorio que llamarán a PL/SQL
import org.uniquindio.repository.impl.CatalogoRepositoryImpl;
// import org.uniquindio.repository.impl.CategoriaRepositoryImpl;
// import org.uniquindio.repository.impl.ExamenRepositoryImpl;
// import org.uniquindio.repository.impl.PreguntaRepositoryImpl;

// import java.io.IOException; // If opening new FXMLs for question management
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.ResourceBundle;

public class CrearEditarExamenController implements Initializable {

    private static final Integer ID_CREACION_AUTOMATICA = 2;
    @FXML private TextField txtNombreExamen;
    @FXML private ComboBox<Curso> comboCurso;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private TextArea txtDescripcionExamen;
    @FXML private Spinner<Integer> spinnerPesoCurso; // Representa porcentaje, se convertirá a BigDecimal
    @FXML private Spinner<Integer> spinnerUmbralAprobacion; // Representa porcentaje, se convertirá a BigDecimal
    @FXML private DatePicker datePickerFecha;
    @FXML private TextField txtHoraPresentacion;
    @FXML private Spinner<Integer> spinnerDuracion;
    @FXML private Spinner<Integer> spinnerNumPreguntasEstudiante;
    @FXML private TableView<PreguntaExamenDTO> tablaPreguntasExamen;
    @FXML private TableColumn<PreguntaExamenDTO, String> colPreguntaTexto;
    @FXML private TableColumn<PreguntaExamenDTO, String> colPreguntaTipo;
    @FXML private TableColumn<PreguntaExamenDTO, Double> colPreguntaPorcentaje; // DTO usa Double, entidad Examen no guarda esto directamente
    @FXML private TableColumn<PreguntaExamenDTO, Void> colPreguntaAcciones;
    @FXML private Label lblTotalPorcentaje;
    @FXML private CheckBox checkSeleccionAutomatica;
    @FXML private Button btnAnadirPreguntaExistente;
    @FXML private Button btnCrearNuevaPregunta;
    @FXML private Button btnGuardarExamen;
    @FXML private Button btnCancelarExamen;

    private ObservableList<PreguntaExamenDTO> preguntasDelExamenList = FXCollections.observableArrayList();
    private Profesor profesorLogueado;
    private Examen examenActual; // Para modo edición

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private CursoRepositoryImpl cursoRepository = new CursoRepositoryImpl();
    private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    private ExamenRepositoryImpl examenRepository = new ExamenRepositoryImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaPreguntas();
        actualizarTotalPorcentaje();

        checkSeleccionAutomatica.selectedProperty().addListener((obs, oldVal, newVal) -> {
            btnAnadirPreguntaExistente.setDisable(newVal);
            btnCrearNuevaPregunta.setDisable(newVal);
            tablaPreguntasExamen.setDisable(newVal);
            if (newVal) {
                preguntasDelExamenList.clear();
            }
            actualizarTotalPorcentaje();
        });
    }

    public void setProfesor(Profesor profesor) {
        this.profesorLogueado = profesor;
        // cargarCursosDelProfesor(); // Implementar si es necesario
    }

    /**
     * Este método DEBE ser llamado por el DashboardProfesorController DESPUÉS de cargar este FXML
     * y DESPUÉS de obtener la instancia de este controlador.
     * @param profesor El profesor que ha iniciado sesión.
     * @param examen (Opcional) El examen a editar, null si es creación.
     */
    public void initData(Profesor profesor, Examen examen) throws SQLException {
        this.profesorLogueado = profesor;
        this.examenActual = examen;

        cargarCombosIniciales(); // Ahora es seguro llamar esto

        if (examenActual != null) {
            cargarExamenParaEdicion(examenActual);
        }
    }

    public void cargarExamenParaEdicion(Examen examen) throws SQLException {
        if (examen == null) return;

        txtNombreExamen.setText(examen.getNombre());
        txtNombreExamen.setText(examen.getNombre() != null ? examen.getNombre().substring(0, Math.min(examen.getNombre().length(), 50)) : "");


        // TODO: Cargar y seleccionar el Curso y Categoria en los ComboBox
        // Necesitarás obtener el objeto Curso y Categoria basado en examen.getCursoId() y examen.getCategoriaId()
        // y luego hacer comboCurso.setValue(...) y comboCategoria.setValue(...)
        if (comboCurso.getItems() != null && examen.getCursoId() != null) {
            comboCurso.setValue(comboCurso.getItems().stream()
            .filter(c -> c.getIdCurso() == examen.getCursoId())
            .findFirst().orElse(null));
            }
        if (comboCategoria.getItems() != null && examen.getCategoriaId() != null) {
            comboCategoria.setValue(comboCategoria.getItems().stream()
            .filter(cat -> cat.getId() == examen.getCategoriaId())
            .findFirst().orElse(null));
            }

        txtDescripcionExamen.setText(examen.getDescripcion());

        if (examen.getPesoCurso() != null) {
            spinnerPesoCurso.getValueFactory().setValue(examen.getPesoCurso().intValue());
        }
        if (examen.getCalificacionMinAprobatoria() != null) {
            spinnerUmbralAprobacion.getValueFactory().setValue(examen.getCalificacionMinAprobatoria().intValue());
        }

        if (examen.getFecha() != null) {
            datePickerFecha.setValue(examen.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        if (examen.getHora() != null) {
            // Convertir java.util.Date (solo parte de hora) a String HH:mm
            LocalTime localTime = examen.getHora().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            txtHoraPresentacion.setText(localTime.format(timeFormatter));
        }

        spinnerDuracion.getValueFactory().setValue(examen.getTiempo() != null ? examen.getTiempo() : 60);
        spinnerNumPreguntasEstudiante.getValueFactory().setValue(examen.getNumeroPreguntas() != null ? examen.getNumeroPreguntas() : 10);

        // Manejo de checkSeleccionAutomatica:
        // La entidad Examen no tiene un campo 'seleccionAutomatica'.
        // Esto podría depender del 'creacionId'. Por ejemplo, si creacionId = 2 es "Automático".
        // Necesitarías una forma de mapear esto.
        if (examen.getCreacionId() != null && examen.getCreacionId() == ID_CREACION_AUTOMATICA) {
            checkSeleccionAutomatica.setSelected(true);
        } else {
            checkSeleccionAutomatica.setSelected(false);
        }

        checkSeleccionAutomatica.setSelected(false);


        // TODO: Cargar las preguntas asociadas al examen (DetallePreguntaExamen) desde la BD
        List<PreguntaExamenDTO> preguntas = examenRepository.obtenerPreguntasDeExamenDTO(examen.getId());
        preguntasDelExamenList.setAll(preguntas);
        actualizarTotalPorcentaje();
    }


    private void configurarTablaPreguntas() {
        colPreguntaTexto.setCellValueFactory(new PropertyValueFactory<>("textoPregunta"));
        colPreguntaTipo.setCellValueFactory(new PropertyValueFactory<>("tipoPregunta"));
        colPreguntaPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        colPreguntaAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");
            {
                btnEliminar.setOnAction(event -> {
                    PreguntaExamenDTO preguntaDTO = getTableView().getItems().get(getIndex());
                    preguntasDelExamenList.remove(preguntaDTO);
                    actualizarTotalPorcentaje();
                });
                btnEliminar.getStyleClass().add("button-eliminar-tabla");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
        });
        tablaPreguntasExamen.setItems(preguntasDelExamenList);
    }



    private void cargarCombosIniciales() {
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Interno", "No se ha podido identificar al profesor para cargar sus cursos.");
            return;
        }

        try {
            List<Curso> cursos = cursoRepository.listarCursosPorProfesor(profesorLogueado.getCedula());
            comboCurso.setItems(FXCollections.observableArrayList(cursos));
            comboCurso.setConverter(new StringConverter<Curso>() {
                @Override
                public String toString(Curso curso) {
                    return curso == null ? null : curso.getNombre();
                }

                @Override
                public Curso fromString(String string) {
                    return null;
                }
            });

            List<Categoria> categorias = catalogoRepository.listarCategoriasExamen();
            comboCategoria.setItems(FXCollections.observableArrayList(categorias));
            comboCategoria.setConverter(new StringConverter<Categoria>() {
                @Override
                public String toString(Categoria categoria) {
                    return categoria == null ? null : categoria.getNombre();
                }

                @Override
                public Categoria fromString(String string) {
                    return null;
                }
            });

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los datos para los combos (cursos/categorías): " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAnadirPreguntaExistente(ActionEvent event) {
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Profesor no identificado.");
            return;
        }
        Curso cursoSeleccionado = comboCurso.getValue();
        if (cursoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, seleccione primero un curso. El banco de preguntas se filtrará por este curso.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profesor/dialogs/seleccionar_pregunta_dialog.fxml"));
            Parent parent = loader.load();

            SeleccionarPreguntaDialogController dialogController = loader.getController();
            dialogController.setProfesorYContexto(this.profesorLogueado, cursoSeleccionado);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleccionar Preguntas del Banco para: " + cursoSeleccionado.getNombre());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner( (Stage) ((Node)event.getSource()).getScene().getWindow() );


            Scene scene = new Scene(parent);
            dialogStage.setScene(scene);
            dialogController.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            List<PreguntaSeleccionDTO> preguntasAnadidas = dialogController.getPreguntasSeleccionadas();
            if (preguntasAnadidas != null && !preguntasAnadidas.isEmpty()) {
                for (PreguntaSeleccionDTO pSeleccionada : preguntasAnadidas) {
                    PreguntaExamenDTO nuevaPreguntaParaExamen = new PreguntaExamenDTO(
                            pSeleccionada.getIdPregunta(),
                            pSeleccionada.getTextoPregunta(),
                            pSeleccionada.getTipoPreguntaNombre(),
                            pSeleccionada.getPorcentajeEnExamen()
                    );
                    boolean existe = preguntasDelExamenList.stream()
                            .anyMatch(ex -> ex.getIdPregunta() == nuevaPreguntaParaExamen.getIdPregunta());
                    if(!existe){
                        preguntasDelExamenList.add(nuevaPreguntaParaExamen);
                    } else {
                        System.out.println("Pregunta con ID " + nuevaPreguntaParaExamen.getIdPregunta() + " ya existe en el examen.");
                    }
                }
                actualizarTotalPorcentaje();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Preguntas Añadidas", preguntasAnadidas.size() + " pregunta(s) seleccionada(s) y añadida(s) al examen.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga FXML", "No se pudo abrir el diálogo de selección de pregunta: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void handleCrearNuevaPregunta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profesor/dialogs/crear_pregunta_dialog.fxml"));
            Parent parent = loader.load();

            CrearPreguntaDialogController dialogController = loader.getController();
            dialogController.setProfesor(this.profesorLogueado); // Pasar el profesor logueado
            // Si necesitas pasar el tema/contenido del examen actual para preseleccionar el combo:
            // if (comboCurso.getValue() != null) {
            //    // Necesitarías una forma de obtener el ContenidoId del Curso o un objeto Contenido
            //    // dialogController.setContenidoPredeterminado(obtenerContenidoDelCursoSeleccionado());
            // }


            Stage dialogStage = new Stage();
            dialogStage.setTitle("Crear Nueva Pregunta");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // dialogStage.initOwner(((Node)event.getSource()).getScene().getWindow()); // Opcional para centrar respecto a la ventana padre
            Scene scene = new Scene(parent);
            dialogStage.setScene(scene);

            dialogController.setDialogStage(dialogStage); // Pasar el stage al controlador del diálogo

            dialogStage.showAndWait(); // Mostrar el diálogo y esperar

            Pregunta preguntaCreada = dialogController.getPreguntaCreada();
            if (preguntaCreada != null) {
                // TODO: Añadir la preguntaCreada (o un DTO) a preguntasDelExamenList
                // Esto requiere que el ID de la pregunta ya esté asignado (devuelto por PL/SQL)
                // PreguntaExamenDTO nuevaPreguntaDTO = new PreguntaExamenDTO(
                //        (long) preguntaCreada.getIdPregunta(),
                //        preguntaCreada.getTexto(),
                //        comboTipoPregunta.getValue() != null ? comboTipoPregunta.getValue().getNombre() : "Desconocido", // Obtener nombre del tipo
                //        preguntaCreada.getPorcentaje() != null ? preguntaCreada.getPorcentaje().doubleValue() : 10.0
                // );
                // preguntasDelExamenList.add(nuevaPreguntaDTO);
                // actualizarTotalPorcentaje();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Pregunta Añadida", "La pregunta '" + preguntaCreada.getTexto().substring(0, Math.min(preguntaCreada.getTexto().length(), 30)) + "...' se ha creado y añadido (simulado).");
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el diálogo de creación de pregunta.");
        }
    }

    private void actualizarTotalPorcentaje() {
        double total = preguntasDelExamenList.stream().mapToDouble(PreguntaExamenDTO::getPorcentaje).sum();
        lblTotalPorcentaje.setText(String.format("Total Porcentaje: %.2f%%", total));
        if (Math.abs(total - 100.0) > 0.01 && !preguntasDelExamenList.isEmpty() && !checkSeleccionAutomatica.isSelected()) {
            lblTotalPorcentaje.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            lblTotalPorcentaje.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleGuardarExamen(ActionEvent event) {
        // Validaciones básicas
        if (txtNombreExamen.getText().trim().isEmpty()) { // Asumiendo que txtNombreExamen es para el título/descripción corta
            mostrarAlerta(Alert.AlertType.ERROR, "Campo Incompleto", "El nombre/título del examen es obligatorio.");
            return;
        }
        if (comboCurso.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo Incompleto", "Debe seleccionar un curso asociado.");
            return;
        }
        if (comboCategoria.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo Incompleto", "Debe seleccionar una categoría para el examen.");
            return;
        }
        if (datePickerFecha.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo Incompleto", "La fecha de presentación es obligatoria.");
            return;
        }
        LocalTime horaPresentacion = null;
        try {
            if (txtHoraPresentacion.getText() != null && !txtHoraPresentacion.getText().trim().isEmpty()) {
                horaPresentacion = LocalTime.parse(txtHoraPresentacion.getText().trim(), timeFormatter);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Campo Incompleto", "La hora de presentación es obligatoria (formato HH:mm).");
                return;
            }
        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato de Hora Incorrecto", "Use el formato HH:mm para la hora (ej: 08:30 o 14:00).");
            return;
        }

        if (!checkSeleccionAutomatica.isSelected() && preguntasDelExamenList.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin Preguntas", "Debe añadir preguntas al examen o seleccionar la opción de selección automática.");
            return;
        }
        double totalPorcentaje = preguntasDelExamenList.stream().mapToDouble(PreguntaExamenDTO::getPorcentaje).sum();
        if (!checkSeleccionAutomatica.isSelected() && Math.abs(totalPorcentaje - 100.0) > 0.01 && !preguntasDelExamenList.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Porcentaje", "La suma de los porcentajes de las preguntas añadidas manualmente debe ser 100%. Actualmente es: " + String.format("%.2f%%", totalPorcentaje));
            return;
        }


        Examen examen = (examenActual == null) ? new Examen() : examenActual;
        // Tu entidad Examen usa descripcion para el nombre/titulo.
        // Si quieres un campo 'nombre' separado, debes añadirlo a la entidad y BD.
        examen.setDescripcion(txtNombreExamen.getText().trim() + (txtDescripcionExamen.getText().trim().isEmpty() ? "" : " - " + txtDescripcionExamen.getText().trim()));


        examen.setCursoId(comboCurso.getValue().getIdCurso()); // Asumiendo que Curso tiene getId_curso()
        examen.setCategoriaId(comboCategoria.getValue().getId()); // Asumiendo que Categoria tiene getId()

        examen.setPesoCurso(BigDecimal.valueOf(spinnerPesoCurso.getValue()));
        examen.setCalificacionMinAprobatoria(BigDecimal.valueOf(spinnerUmbralAprobacion.getValue()));

        LocalDate localDate = datePickerFecha.getValue();
        LocalDateTime localDateTime = LocalDateTime.of(localDate, horaPresentacion);
        examen.setFecha(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())); // Para el campo Fecha (solo día)
        examen.setHora(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())); // Para el campo Hora (fecha y hora)

        examen.setTiempo(spinnerDuracion.getValue());
        examen.setNumeroPreguntas(spinnerNumPreguntasEstudiante.getValue());

        // Lógica para creacionId basado en checkSeleccionAutomatica
        // Debes tener los IDs de la tabla CREACION (ej. 1=Manual, 2=Automático)
        // Integer idCreacionManual = 1; // Obtener de BD o constante
        // Integer idCreacionAutomatica = 2; // Obtener de BD o constante
        // examen.setCreacionId(checkSeleccionAutomatica.isSelected() ? idCreacionAutomatica : idCreacionManual);


        // TODO: Llamar a procedimiento PL/SQL via Repositorio para guardar el examen y sus detalles (DetallePreguntaExamen)
        // try {
        //     if (examenActual == null) { // Crear nuevo
        //         // examenRepository.crearExamenCompletoPLSQL(examen, preguntasDelExamenList, profesorLogueado.getCedula());
        //         mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Examen creado correctamente.");
        //     } else { // Actualizar existente
        //         // examenRepository.actualizarExamenCompletoPLSQL(examen, preguntasDelExamenList);
        //         mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Examen actualizado correctamente.");
        //     }
        //     cerrarVentana();
        // } catch (SQLException e) {
        //     mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo guardar el examen: " + e.getMessage());
        //     e.printStackTrace();
        // }
        mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado (Simulado)", "La lógica de guardado con PL/SQL y manejo de preguntas debe implementarse. Examen: " + examen.getDescripcion());

    }

    @FXML
    private void handleCancelarExamen(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        // Stage stage = (Stage) btnCancelarExamen.getScene().getWindow();
        // stage.close();
        System.out.println("Operación cancelada o ventana cerrada.");
        // Para cerrar la vista actual cargada en el dashboard del profesor,
        // necesitarías una referencia al controlador del dashboard y un método allí para limpiar el panel de contenido.
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
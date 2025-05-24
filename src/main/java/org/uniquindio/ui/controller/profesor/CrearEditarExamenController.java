package org.uniquindio.ui.controller.profesor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import org.uniquindio.model.entity.academico.Curso;
import org.uniquindio.model.entity.catalogo.Categoria;
import org.uniquindio.model.entity.catalogo.TipoPregunta;
import org.uniquindio.model.entity.evaluacion.Examen;
import org.uniquindio.model.entity.evaluacion.Pregunta;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.repository.impl.CatalogoRepositoryImpl;
import org.uniquindio.repository.impl.CursoRepositoryImpl;
import org.uniquindio.repository.impl.ExamenRepositoryImpl;
import org.uniquindio.ui.controller.profesor.dialogs.CrearPreguntaDialogController;
import org.uniquindio.ui.controller.profesor.dialogs.SeleccionarPreguntaDialogController;
import org.uniquindio.model.dto.PreguntaSeleccionDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CrearEditarExamenController implements Initializable {

    private static final Integer ID_CREACION_AUTOMATICA = 2;
    private static final Integer ID_CREACION_MANUAL = 1;
    @FXML private TextField txtNombreExamen;
    @FXML private ComboBox<Curso> comboCurso;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private TextArea txtDescripcionExamen;
    @FXML private Spinner<Integer> spinnerPesoCurso;
    @FXML private Spinner<Double> spinnerUmbralAprobacion;
    @FXML private DatePicker datePickerFecha;
    // @FXML private TextField txtHoraPresentacion; // Comentado o eliminado
    @FXML private Spinner<Integer> spinnerHoraPresentacion; // Nuevo Spinner para Hora
    @FXML private Spinner<Integer> spinnerMinutoPresentacion; // Nuevo Spinner para Minuto
    @FXML private Spinner<Integer> spinnerDuracion;
    @FXML private Spinner<Integer> spinnerNumPreguntasEstudiante;
    @FXML private TableView<PreguntaExamenDTO> tablaPreguntasExamen;
    @FXML private TableColumn<PreguntaExamenDTO, String> colPreguntaTexto;
    @FXML private TableColumn<PreguntaExamenDTO, String> colPreguntaTipo;
    @FXML private TableColumn<PreguntaExamenDTO, Double> colPreguntaPorcentaje;
    @FXML private TableColumn<PreguntaExamenDTO, Void> colPreguntaAcciones;
    @FXML private Label lblTotalPorcentaje;
    @FXML private CheckBox checkSeleccionAutomatica;
    @FXML private Button btnAnadirPreguntaExistente;
    @FXML private Button btnCrearNuevaPregunta;
    @FXML private Button btnGuardarExamen;
    @FXML private Button btnCancelarExamen;


    private ObservableList<PreguntaExamenDTO> preguntasDelExamenList = FXCollections.observableArrayList();
    private Profesor profesorLogueado;
    private Examen examenActual;
    private DashboardProfesorController dashboardProfesorController;

    // DateTimeFormatter ya no es necesario para el input directo, pero puede ser útil para mostrar
    // private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private CursoRepositoryImpl cursoRepository = new CursoRepositoryImpl();
    private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    private ExamenRepositoryImpl examenRepository = new ExamenRepositoryImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaPreguntas();
        actualizarTotalPorcentaje();
        configurarSpinnersHoraMinuto(); // Configurar los nuevos spinners

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

    private void configurarSpinnersHoraMinuto() {
        // Configurar Spinner para Hora (0-23)
        SpinnerValueFactory<Integer> horaValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8); // Valor inicial 8 AM
        spinnerHoraPresentacion.setValueFactory(horaValueFactory);
        spinnerHoraPresentacion.setEditable(true);
        // Formateador para que siempre muestre dos dígitos (opcional, pero mejora la estética)
        TextFormatter<Integer> horaFormatter = new TextFormatter<>(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                if (object == null) return "00";
                return String.format("%02d", object);
            }
            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }, 8, change -> { // Valor por defecto si la entrada es inválida
            if (change.getControlNewText().matches("\\d{0,2}")) {
                return change;
            }
            return null;
        });
        spinnerHoraPresentacion.getEditor().setTextFormatter(horaFormatter);


        // Configurar Spinner para Minuto (0-59)
        SpinnerValueFactory<Integer> minutoValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15); // Valor inicial 0, step 15
        spinnerMinutoPresentacion.setValueFactory(minutoValueFactory);
        spinnerMinutoPresentacion.setEditable(true);
        // Formateador para minutos
        TextFormatter<Integer> minutoFormatter = new TextFormatter<>(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                if (object == null) return "00";
                return String.format("%02d", object);
            }
            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }, 0, change -> {
            if (change.getControlNewText().matches("\\d{0,2}")) {
                return change;
            }
            return null;
        });
        spinnerMinutoPresentacion.getEditor().setTextFormatter(minutoFormatter);
    }

    public void setProfesor(Profesor profesor) {
        this.profesorLogueado = profesor;
    }

    public void initData(Profesor profesor, Examen examen, DashboardProfesorController dashboardController) throws SQLException {
        this.profesorLogueado = profesor;
        this.examenActual = examen;
        this.dashboardProfesorController = dashboardController;

        cargarCombosIniciales();
        if (examenActual != null) {
            cargarExamenParaEdicion(examenActual);
        } else {
            limpiarFormularioExamen();
        }
    }

    private void limpiarFormularioExamen() {
        txtNombreExamen.clear();
        txtDescripcionExamen.clear();
        comboCurso.getSelectionModel().clearSelection();
        comboCurso.setValue(null);
        comboCategoria.getSelectionModel().clearSelection();
        comboCategoria.setValue(null);

        if (spinnerPesoCurso.getValueFactory() != null) {
            spinnerPesoCurso.getValueFactory().setValue(20);
        }
        if (spinnerUmbralAprobacion.getValueFactory() != null) {
            ((SpinnerValueFactory.DoubleSpinnerValueFactory)spinnerUmbralAprobacion.getValueFactory()).setValue(3.0);
        }
        if (spinnerDuracion.getValueFactory() != null) {
            spinnerDuracion.getValueFactory().setValue(60);
        }
        if (spinnerNumPreguntasEstudiante.getValueFactory() != null) {
            spinnerNumPreguntasEstudiante.getValueFactory().setValue(10);
        }

        datePickerFecha.setValue(LocalDate.now().plusDays(1));
        // Establecer hora y minuto por defecto en los spinners
        spinnerHoraPresentacion.getValueFactory().setValue(8); // 08 AM
        spinnerMinutoPresentacion.getValueFactory().setValue(0); // 00 minutos

        checkSeleccionAutomatica.setSelected(false);
        if (preguntasDelExamenList != null) {
            preguntasDelExamenList.clear();
        }
        actualizarTotalPorcentaje();
        examenActual = null;
        btnAnadirPreguntaExistente.setDisable(false);
        btnCrearNuevaPregunta.setDisable(false);
        tablaPreguntasExamen.setDisable(false);
        spinnerNumPreguntasEstudiante.setDisable(false);
        colPreguntaPorcentaje.setEditable(true);
        txtNombreExamen.requestFocus();
    }

    public void cargarExamenParaEdicion(Examen examen) throws SQLException {
        if (examen == null) return;

        txtNombreExamen.setText(examen.getNombre() != null ? examen.getNombre() : "");

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
            spinnerUmbralAprobacion.getValueFactory().setValue(examen.getCalificacionMinAprobatoria().doubleValue());
        }

        if (examen.getFecha() != null) {
            datePickerFecha.setValue(examen.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        // Cargar hora y minuto en los spinners
        if (examen.getHora() != null) {
            LocalDateTime localDateTimeHora = examen.getHora(); // Asumiendo que Examen.getHora() devuelve LocalDateTime
            spinnerHoraPresentacion.getValueFactory().setValue(localDateTimeHora.getHour());
            spinnerMinutoPresentacion.getValueFactory().setValue(localDateTimeHora.getMinute());
        } else { // Valores por defecto si no hay hora
            spinnerHoraPresentacion.getValueFactory().setValue(8);
            spinnerMinutoPresentacion.getValueFactory().setValue(0);
        }


        spinnerDuracion.getValueFactory().setValue(examen.getTiempo() != null ? examen.getTiempo() : 60);
        spinnerNumPreguntasEstudiante.getValueFactory().setValue(examen.getNumeroPreguntas() != null ? examen.getNumeroPreguntas() : 10);

        if (examen.getCreacionId() != null && examen.getCreacionId().equals(ID_CREACION_AUTOMATICA)) {
            checkSeleccionAutomatica.setSelected(true);
        } else {
            checkSeleccionAutomatica.setSelected(false);
        }

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
            comboCurso.setConverter(new StringConverter<>() {
                @Override
                public String toString(Curso curso) {
                    return curso == null ? null : curso.getNombre();
                }
                @Override
                public Curso fromString(String string) { return null; }
            });

            List<Categoria> categorias = catalogoRepository.listarCategoriasExamen();
            comboCategoria.setItems(FXCollections.observableArrayList(categorias));
            comboCategoria.setConverter(new StringConverter<>() {
                @Override
                public String toString(Categoria categoria) {
                    return categoria == null ? null : categoria.getNombre();
                }
                @Override
                public Categoria fromString(String string) { return null; }
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
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Profesor no identificado.");
            return;
        }
        Curso cursoDelExamen = comboCurso.getValue();

        if (cursoDelExamen == null && !checkSeleccionAutomatica.isSelected()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Curso Requerido",
                    "Por favor, seleccione un curso para el examen antes de crear una nueva pregunta manualmente.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profesor/dialogs/crear_pregunta_dialog.fxml"));
            Parent parent = loader.load();
            CrearPreguntaDialogController dialogController = loader.getController();

            dialogController.initData(this.profesorLogueado, cursoDelExamen, null);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Crear Nueva Pregunta" + (cursoDelExamen != null ? " para el Curso: " + cursoDelExamen.getNombre() : " (Banco General)"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialogStage.setScene(new Scene(parent));
            dialogController.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            Pregunta preguntaCreada = dialogController.getPreguntaCreadaOEditada();

            if (preguntaCreada != null) {
                if (!checkSeleccionAutomatica.isSelected()) {
                    String tipoPreguntaNombre = "Desconocido";
                    if (preguntaCreada.getTipoPreguntaId() != null) {
                        try {
                            TipoPregunta tp = catalogoRepository.listarTiposPregunta().stream()
                                    .filter(t -> t.getId() == preguntaCreada.getTipoPreguntaId())
                                    .findFirst().orElse(null);
                            if (tp != null) {
                                tipoPreguntaNombre = tp.getNombre();
                            } else {
                                tipoPreguntaNombre = "Tipo ID: " + preguntaCreada.getTipoPreguntaId();
                            }
                        } catch (SQLException e) {
                            System.err.println("Error obteniendo nombre de tipo de pregunta: " + e.getMessage());
                            if(preguntaCreada.getTipoPreguntaId() != null){
                                tipoPreguntaNombre = "Tipo ID: " + preguntaCreada.getTipoPreguntaId();
                            }
                        }
                    }

                    PreguntaExamenDTO nuevaPreguntaDTO = new PreguntaExamenDTO(
                            (long) preguntaCreada.getIdPregunta(),
                            preguntaCreada.getTexto(),
                            tipoPreguntaNombre,
                            preguntaCreada.getPorcentaje() != null ? preguntaCreada.getPorcentaje().doubleValue() : 10.0
                    );
                    preguntasDelExamenList.add(nuevaPreguntaDTO);
                    actualizarTotalPorcentaje();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Pregunta Añadida al Examen",
                            "La pregunta '" + preguntaCreada.getTexto().substring(0, Math.min(preguntaCreada.getTexto().length(), 30)) + "...' se ha creado y añadido a este examen.");
                } else {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Pregunta Creada en Banco",
                            "La pregunta '" + preguntaCreada.getTexto().substring(0, Math.min(preguntaCreada.getTexto().length(), 30)) + "...' se ha creado en el banco de preguntas. No se añadió a este examen porque la selección es automática.");
                }
            }

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el diálogo de creación de pregunta: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
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
        if (txtNombreExamen.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo Incompleto", "El nombre del examen es obligatorio.");
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

        // Validar y obtener hora de los Spinners
        Integer hora = spinnerHoraPresentacion.getValue();
        Integer minuto = spinnerMinutoPresentacion.getValue();

        if (hora == null || minuto == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo Incompleto", "La hora y minuto de presentación son obligatorios.");
            return;
        }
        LocalTime horaPresentacion = LocalTime.of(hora, minuto);


        LocalDate fechaSeleccionada = datePickerFecha.getValue();
        LocalDateTime fechaHoraExamen = LocalDateTime.of(fechaSeleccionada, horaPresentacion);
        LocalDateTime fechaHoraActual = LocalDateTime.now();

        if (fechaHoraExamen.isBefore(fechaHoraActual)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Fecha Inválida",
                    "La fecha y hora de presentación del examen no puede ser anterior a la fecha y hora actual.");
            return;
        }

        if (!checkSeleccionAutomatica.isSelected() && preguntasDelExamenList.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin Preguntas", "Debe añadir preguntas al examen o seleccionar la opción de selección automática.");
            return;
        }
        double totalPorcentaje = preguntasDelExamenList.stream().mapToDouble(PreguntaExamenDTO::getPorcentaje).sum();
        if (!checkSeleccionAutomatica.isSelected() && !preguntasDelExamenList.isEmpty() && Math.abs(totalPorcentaje - 100.0) > 0.01) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Porcentaje", "La suma de los porcentajes de las preguntas añadidas manualmente debe ser 100%. Actualmente es: " + String.format("%.2f%%", totalPorcentaje));
            return;
        }
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Interno", "No se ha identificado al profesor.");
            return;
        }

        Examen examen = (examenActual == null) ? new Examen() : examenActual;
        examen.setNombre(txtNombreExamen.getText().trim());
        examen.setDescripcion(txtDescripcionExamen.getText().trim());
        examen.setCursoId(comboCurso.getValue().getIdCurso());
        examen.setCategoriaId(comboCategoria.getValue().getId());
        examen.setPesoCurso(BigDecimal.valueOf(spinnerPesoCurso.getValue()));
        examen.setCalificacionMinAprobatoria(BigDecimal.valueOf(spinnerUmbralAprobacion.getValue()));

        examen.setFecha(Date.from(fechaSeleccionada.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        examen.setHora(fechaHoraExamen); // Guardar LocalDateTime directamente

        examen.setTiempo(spinnerDuracion.getValue());
        examen.setNumeroPreguntas(spinnerNumPreguntasEstudiante.getValue());
        examen.setCreacionId(checkSeleccionAutomatica.isSelected() ? ID_CREACION_AUTOMATICA : ID_CREACION_MANUAL);

        List<PreguntaExamenDTO> listaParaRepositorio = new ArrayList<>(preguntasDelExamenList);

        try {
            if (examenActual == null) {
                int idExamenCreado = examenRepository.crearExamenCompleto(
                        examen,
                        checkSeleccionAutomatica.isSelected() ? new ArrayList<>() : listaParaRepositorio,
                        profesorLogueado.getCedula()
                );
                examen.setId(idExamenCreado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Examen creado correctamente con ID: " + idExamenCreado);
                limpiarFormularioExamen();
            } else {
                boolean actualizado = examenRepository.actualizarExamenCompleto(
                        examen,
                        checkSeleccionAutomatica.isSelected() ? new ArrayList<>() : listaParaRepositorio
                );
                if (actualizado) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Examen ID: " + examen.getId() + " actualizado correctamente.");
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se pudo actualizar el examen o no hubo cambios.");
                }
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo guardar el examen: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error al guardar el examen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelarExamen(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        // Si este controlador es parte de una vista principal, no se cierra directamente.
        // Se podría navegar a otra vista o limpiar el formulario.
        // Si es un diálogo, entonces sí se cierra:
        // Stage stage = (Stage) btnCancelarExamen.getScene().getWindow();
        // stage.close();
        System.out.println("Operación cancelada o vista limpiada.");
        if (dashboardProfesorController != null) {
            //dashboardProfesorController.ha(null); // Volver al inicio del dashboard del profesor
        } else {
            limpiarFormularioExamen(); // O simplemente limpiar
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

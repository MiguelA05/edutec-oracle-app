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
import org.uniquindio.model.entity.catalogo.TipoPregunta; // No se usa directamente aquí, pero sí en diálogos
import org.uniquindio.model.entity.evaluacion.Examen;
import org.uniquindio.model.entity.evaluacion.Pregunta;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.repository.impl.CatalogoRepositoryImpl;
import org.uniquindio.repository.impl.CursoRepositoryImpl;
import org.uniquindio.repository.impl.ExamenRepositoryImpl;
import org.uniquindio.repository.impl.PreguntaRepositoryImpl; // Para obtener detalles de pregunta si es necesario
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
    @FXML private Spinner<Integer> spinnerHoraPresentacion;
    @FXML private Spinner<Integer> spinnerMinutoPresentacion;
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
    private Examen examenActual; // Para edición
    private DashboardProfesorController dashboardProfesorController; // Para navegación

    private CursoRepositoryImpl cursoRepository = new CursoRepositoryImpl();
    private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    private ExamenRepositoryImpl examenRepository = new ExamenRepositoryImpl();
    private PreguntaRepositoryImpl preguntaRepository = new PreguntaRepositoryImpl(); // Para obtener info de pregunta


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaPreguntas();
        actualizarTotalPorcentaje();
        configurarSpinnersHoraMinuto();

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
        SpinnerValueFactory<Integer> horaValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8);
        spinnerHoraPresentacion.setValueFactory(horaValueFactory);
        spinnerHoraPresentacion.setEditable(true);
        TextFormatter<Integer> horaFormatter = new TextFormatter<>(new StringConverter<>() {
            @Override public String toString(Integer object) { return object == null ? "08" : String.format("%02d", object); }
            @Override public Integer fromString(String string) { try { return Integer.parseInt(string); } catch (NumberFormatException e) { return 8; }}
        }, 8, change -> change.getControlNewText().matches("\\d{0,2}") ? change : null);
        spinnerHoraPresentacion.getEditor().setTextFormatter(horaFormatter);

        SpinnerValueFactory<Integer> minutoValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15);
        spinnerMinutoPresentacion.setValueFactory(minutoValueFactory);
        spinnerMinutoPresentacion.setEditable(true);
        TextFormatter<Integer> minutoFormatter = new TextFormatter<>(new StringConverter<>() {
            @Override public String toString(Integer object) { return object == null ? "00" : String.format("%02d", object); }
            @Override public Integer fromString(String string) { try { return Integer.parseInt(string); } catch (NumberFormatException e) { return 0; }}
        }, 0, change -> change.getControlNewText().matches("\\d{0,2}") ? change : null);
        spinnerMinutoPresentacion.getEditor().setTextFormatter(minutoFormatter);
    }

    public void setProfesor(Profesor profesor) { // Este método puede ser llamado por el Dashboard
        this.profesorLogueado = profesor;
        // Si es necesario, recargar combos que dependan del profesor aquí
        // cargarCombosIniciales(); // Ya se llama en initData
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

        if (spinnerPesoCurso.getValueFactory() != null) spinnerPesoCurso.getValueFactory().setValue(20);
        if (spinnerUmbralAprobacion.getValueFactory() != null) ((SpinnerValueFactory.DoubleSpinnerValueFactory)spinnerUmbralAprobacion.getValueFactory()).setValue(3.0);
        if (spinnerDuracion.getValueFactory() != null) spinnerDuracion.getValueFactory().setValue(60);
        if (spinnerNumPreguntasEstudiante.getValueFactory() != null) spinnerNumPreguntasEstudiante.getValueFactory().setValue(10);

        datePickerFecha.setValue(LocalDate.now().plusDays(1));
        spinnerHoraPresentacion.getValueFactory().setValue(8);
        spinnerMinutoPresentacion.getValueFactory().setValue(0);

        checkSeleccionAutomatica.setSelected(false);
        if (preguntasDelExamenList != null) preguntasDelExamenList.clear();
        actualizarTotalPorcentaje();
        examenActual = null; // Asegurar que se está creando uno nuevo
        btnAnadirPreguntaExistente.setDisable(false);
        btnCrearNuevaPregunta.setDisable(false);
        tablaPreguntasExamen.setDisable(false);
        spinnerNumPreguntasEstudiante.setDisable(false);
        colPreguntaPorcentaje.setEditable(true);
        txtNombreExamen.requestFocus();
    }

    public void cargarExamenParaEdicion(Examen examen) throws SQLException {
        if (examen == null) return;
        this.examenActual = examen; // Asegurar que examenActual está seteado

        txtNombreExamen.setText(examen.getNombre() != null ? examen.getNombre() : "");

        if (comboCurso.getItems() != null && examen.getCursoId() != null) {
            comboCurso.setValue(comboCurso.getItems().stream()
                    .filter(c -> c != null && c.getIdCurso() == examen.getCursoId())
                    .findFirst().orElse(null));
        }
        if (comboCategoria.getItems() != null && examen.getCategoriaId() != null) {
            comboCategoria.setValue(comboCategoria.getItems().stream()
                    .filter(cat -> cat != null && cat.getId() == examen.getCategoriaId())
                    .findFirst().orElse(null));
        }

        txtDescripcionExamen.setText(examen.getDescripcion());

        if (examen.getPesoCurso() != null) spinnerPesoCurso.getValueFactory().setValue(examen.getPesoCurso().intValue());
        if (examen.getCalificacionMinAprobatoria() != null) spinnerUmbralAprobacion.getValueFactory().setValue(examen.getCalificacionMinAprobatoria().doubleValue());

        if (examen.getFecha() != null) datePickerFecha.setValue(examen.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        if (examen.getHora() != null) {
            LocalDateTime localDateTimeHora = examen.getHora();
            spinnerHoraPresentacion.getValueFactory().setValue(localDateTimeHora.getHour());
            spinnerMinutoPresentacion.getValueFactory().setValue(localDateTimeHora.getMinute());
        } else {
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

        // Cargar solo preguntas principales en la tabla
        List<PreguntaExamenDTO> preguntasPrincipales = examenRepository.obtenerPreguntasDeExamenDTO(examen.getId());
        preguntasDelExamenList.setAll(preguntasPrincipales);
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
                @Override public String toString(Curso curso) { return curso == null ? null : curso.getNombre(); }
                @Override public Curso fromString(String string) { return null; }
            });

            List<Categoria> categorias = catalogoRepository.listarCategoriasExamen();
            comboCategoria.setItems(FXCollections.observableArrayList(categorias));
            comboCategoria.setConverter(new StringConverter<>() {
                @Override public String toString(Categoria categoria) { return categoria == null ? null : categoria.getNombre(); }
                @Override public Categoria fromString(String string) { return null; }
            });

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los datos para los combos (cursos/categorías): " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAnadirPreguntaExistente(ActionEvent event) {
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Profesor no identificado."); return;
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
            dialogStage.setTitle("Seleccionar Preguntas Principales para: " + cursoSeleccionado.getNombre());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner( (Stage) ((Node)event.getSource()).getScene().getWindow() );
            Scene scene = new Scene(parent);
            dialogStage.setScene(scene);
            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            List<PreguntaSeleccionDTO> preguntasAnadidas = dialogController.getPreguntasSeleccionadas();
            if (preguntasAnadidas != null && !preguntasAnadidas.isEmpty()) {
                for (PreguntaSeleccionDTO pSeleccionada : preguntasAnadidas) {
                    // Se asume que SeleccionarPreguntaDialogController ya filtra para que solo se puedan seleccionar preguntas principales.
                    PreguntaExamenDTO nuevaPreguntaParaExamen = new PreguntaExamenDTO(
                            pSeleccionada.getIdPregunta(),
                            pSeleccionada.getTextoPregunta(),
                            pSeleccionada.getTipoPreguntaNombre(),
                            pSeleccionada.getPorcentajeEnExamen() // Este es el porcentaje EN EL EXAMEN
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
                mostrarAlerta(Alert.AlertType.INFORMATION, "Preguntas Añadidas", preguntasAnadidas.size() + " pregunta(s) principal(es) seleccionada(s) y añadida(s) al examen.");
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Profesor no identificado."); return;
        }
        Curso cursoDelExamen = comboCurso.getValue();

        if (cursoDelExamen == null && !checkSeleccionAutomatica.isSelected()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Curso Requerido", "Por favor, seleccione un curso para el examen antes de crear una nueva pregunta manualmente.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profesor/dialogs/crear_pregunta_dialog.fxml"));
            Parent parent = loader.load();
            CrearPreguntaDialogController dialogController = loader.getController();

            // Se pasa null para preguntaAEditar para indicar que es una nueva pregunta.
            // El diálogo de creación de pregunta debe permitir crear preguntas principales (sin padre)
            // y subpreguntas (seleccionando un padre).
            dialogController.initData(this.profesorLogueado, cursoDelExamen, null);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Crear Nueva Pregunta" + (cursoDelExamen != null ? " (Contexto Curso: " + cursoDelExamen.getNombre() + ")" : " (Banco General)"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialogStage.setScene(new Scene(parent));
            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            Pregunta preguntaCreada = dialogController.getPreguntaCreadaOEditada();

            if (preguntaCreada != null) {
                // Solo añadir a la tabla del examen si es una pregunta PRINCIPAL
                if (preguntaCreada.getPreguntaPadre() == null || preguntaCreada.getPreguntaPadre() == 0) {
                    if (!checkSeleccionAutomatica.isSelected()) {
                        String tipoPreguntaNombre = "Desconocido";
                        if (preguntaCreada.getTipoPreguntaId() != null) {
                            try {
                                // Obtener nombre del tipo de pregunta
                                TipoPregunta tp = catalogoRepository.listarTiposPregunta().stream()
                                        .filter(t -> t.getId() == preguntaCreada.getTipoPreguntaId())
                                        .findFirst().orElse(null);
                                if (tp != null) tipoPreguntaNombre = tp.getNombre();
                                else tipoPreguntaNombre = "Tipo ID: " + preguntaCreada.getTipoPreguntaId();
                            } catch (SQLException e) {
                                System.err.println("Error obteniendo nombre de tipo de pregunta: " + e.getMessage());
                                if(preguntaCreada.getTipoPreguntaId() != null) tipoPreguntaNombre = "Tipo ID: " + preguntaCreada.getTipoPreguntaId();
                            }
                        }

                        PreguntaExamenDTO nuevaPreguntaDTO = new PreguntaExamenDTO(
                                (long) preguntaCreada.getIdPregunta(),
                                preguntaCreada.getTexto(),
                                tipoPreguntaNombre,
                                10.0 // Porcentaje por defecto al añadir al examen, el profesor puede ajustarlo
                        );
                        preguntasDelExamenList.add(nuevaPreguntaDTO);
                        actualizarTotalPorcentaje();
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Pregunta Principal Añadida al Examen",
                                "La pregunta '" + preguntaCreada.getTexto().substring(0, Math.min(preguntaCreada.getTexto().length(), 30)) + "...' se ha creado y añadido a este examen. Ajuste su porcentaje.");
                    } else {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Pregunta Creada en Banco",
                                "La pregunta principal '" + preguntaCreada.getTexto().substring(0, Math.min(preguntaCreada.getTexto().length(), 30)) + "...' se ha creado en el banco. No se añadió a este examen (selección automática activada).");
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Subpregunta Creada en Banco",
                            "La subpregunta '" + preguntaCreada.getTexto().substring(0, Math.min(preguntaCreada.getTexto().length(), 30)) + "...' se ha creado en el banco y asociado a su padre. No se añade directamente a la tabla del examen.");
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
            lblTotalPorcentaje.setStyle("-fx-text-fill: black; -fx-font-weight: bold;"); // O el color por defecto de tu tema
        }
    }

    @FXML
    private void handleGuardarExamen(ActionEvent event) {
        if (txtNombreExamen.getText().trim().isEmpty() || comboCurso.getValue() == null ||
                comboCategoria.getValue() == null || datePickerFecha.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campos Incompletos", "Nombre, Curso, Categoría y Fecha son obligatorios.");
            return;
        }

        Integer hora = spinnerHoraPresentacion.getValue();
        Integer minuto = spinnerMinutoPresentacion.getValue();
        if (hora == null || minuto == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo Incompleto", "La hora y minuto de presentación son obligatorios.");
            return;
        }
        LocalTime horaPresentacion = LocalTime.of(hora, minuto);
        LocalDate fechaSeleccionada = datePickerFecha.getValue();
        LocalDateTime fechaHoraExamen = LocalDateTime.of(fechaSeleccionada, horaPresentacion);

        if (fechaHoraExamen.isBefore(LocalDateTime.now())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Fecha Inválida", "La fecha y hora de presentación no puede ser anterior a la actual.");
            return;
        }

        if (!checkSeleccionAutomatica.isSelected() && preguntasDelExamenList.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin Preguntas", "Debe añadir preguntas principales al examen o seleccionar la opción de selección automática.");
            return;
        }
        double totalPorcentaje = preguntasDelExamenList.stream().mapToDouble(PreguntaExamenDTO::getPorcentaje).sum();
        if (!checkSeleccionAutomatica.isSelected() && !preguntasDelExamenList.isEmpty() && Math.abs(totalPorcentaje - 100.0) > 0.01) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Porcentaje", "La suma de los porcentajes de las preguntas principales añadidas debe ser 100%. Actualmente es: " + String.format("%.2f%%", totalPorcentaje));
            return;
        }
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Interno", "No se ha identificado al profesor."); return;
        }

        Examen examen = (examenActual == null) ? new Examen() : examenActual;
        examen.setNombre(txtNombreExamen.getText().trim());
        examen.setDescripcion(txtDescripcionExamen.getText().trim());
        examen.setCursoId(comboCurso.getValue().getIdCurso());
        examen.setCategoriaId(comboCategoria.getValue().getId());
        examen.setPesoCurso(BigDecimal.valueOf(spinnerPesoCurso.getValue()));
        examen.setCalificacionMinAprobatoria(BigDecimal.valueOf(spinnerUmbralAprobacion.getValue()));
        examen.setFecha(Date.from(fechaSeleccionada.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        examen.setHora(fechaHoraExamen);
        examen.setTiempo(spinnerDuracion.getValue());
        examen.setNumeroPreguntas(spinnerNumPreguntasEstudiante.getValue());
        examen.setCreacionId(checkSeleccionAutomatica.isSelected() ? ID_CREACION_AUTOMATICA : ID_CREACION_MANUAL);

        // La lista p_preguntas_detalle solo contiene las preguntas principales y su porcentaje en el examen.
        // El PL/SQL ASIGNAR_PREGUNTA_A_EXAMEN se encarga de la jerarquía.
        List<PreguntaExamenDTO> listaParaRepositorio = new ArrayList<>(preguntasDelExamenList);

        try {
            if (examenActual == null || examen.getId() == 0) { // Es un nuevo examen
                int idExamenCreado = examenRepository.crearExamenCompleto(
                        examen,
                        checkSeleccionAutomatica.isSelected() ? new ArrayList<>() : listaParaRepositorio,
                        profesorLogueado.getCedula()
                );
                examen.setId(idExamenCreado); // Actualizar el ID en el objeto local
                this.examenActual = examen; // Establecer como examen actual para posible edición posterior sin recargar
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Examen creado correctamente con ID: " + idExamenCreado);
                // limpiarFormularioExamen(); // Opcional: limpiar para crear otro, o mantener para editar
            } else { // Actualizar examen existente
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
        // Podría navegar de vuelta a la lista de exámenes o al dashboard del profesor
        if (dashboardProfesorController != null) {
            //dashboardProfesorController.handleInicioProfesor(null); // Ejemplo de navegación
        } else {
            limpiarFormularioExamen(); // O simplemente limpiar si no hay dashboard controller
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

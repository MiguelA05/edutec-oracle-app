package org.uniquindio.ui.controller.profesor.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.uniquindio.model.entity.academico.Contenido;
import org.uniquindio.model.entity.evaluacion.OpcionPregunta;
import org.uniquindio.model.entity.evaluacion.Pregunta;
import org.uniquindio.model.entity.catalogo.Nivel;
import org.uniquindio.model.entity.catalogo.TipoPregunta;
import org.uniquindio.model.entity.catalogo.Visibilidad;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.repository.impl.CatalogoRepositoryImpl;
import org.uniquindio.repository.impl.ConnectionOracle;
import org.uniquindio.repository.impl.ContenidoRepositoryImpl;
import org.uniquindio.repository.impl.PreguntaRepositoryImpl;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CrearPreguntaDialogController implements Initializable {

    @FXML private TextArea txtTextoPregunta;
    @FXML private ComboBox<TipoPregunta> comboTipoPregunta;
    @FXML private ComboBox<Contenido> comboContenido;
    @FXML private ComboBox<Nivel> comboNivel;
    @FXML private ComboBox<Visibilidad> comboVisibilidad;
    @FXML private Spinner<Integer> spinnerTiempoEstimado; // CAMBIADO DE TextField a Spinner
    @FXML private Spinner<Double> spinnerPorcentajeDefecto;
    @FXML private ComboBox<Pregunta> comboPreguntaPadre;

    @FXML private VBox vboxOpcionesPregunta;
    @FXML private VBox contenedorDinamicoOpciones;
    @FXML private Button btnAnadirOpcion;

    @FXML private VBox vboxVerdaderoFalso;
    @FXML private RadioButton radioVerdadero;
    @FXML private RadioButton radioFalso;
    @FXML private ToggleGroup grupoVerdaderoFalso;

    @FXML private Button btnGuardarPregunta;
    @FXML private Button btnCancelarCreacion;

    private Profesor profesorLogueado;
    private Pregunta preguntaEditando; // Para modo edición
    private Pregunta preguntaCreadaOEditada; // Para devolverla al controlador que llamó
    private Stage dialogStage;

    private PreguntaRepositoryImpl preguntaRepository = new PreguntaRepositoryImpl();
    private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    private ContenidoRepositoryImpl contenidoRepository = new ContenidoRepositoryImpl();

    private List<HBoxOpcionController> opcionesUIList = new ArrayList<>();

    // Constantes para nombres de tipos de pregunta (para evitar errores de tipeo)
    private static final String TIPO_OPCION_MULTIPLE = "Selección Múltiple"; // Ajusta si el nombre en BD es diferente
    private static final String TIPO_VERDADERO_FALSO = "Verdadero/Falso";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // La carga de combos se hará en setProfesor o setPreguntaParaEdicion
        configurarSpinners();
        configurarListenersTipoPregunta();
    }

    private void configurarSpinners() {
        SpinnerValueFactory.DoubleSpinnerValueFactory doubleFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100.0, 10.0, 1.0);
        spinnerPorcentajeDefecto.setValueFactory(doubleFactory);
        spinnerPorcentajeDefecto.setEditable(true);

        SpinnerValueFactory.IntegerSpinnerValueFactory intFactoryTiempo =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 5, 1); // min, max, initial, step
        spinnerTiempoEstimado.setValueFactory(intFactoryTiempo);
        spinnerTiempoEstimado.setEditable(true);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setProfesor(Profesor profesor) {
        this.profesorLogueado = profesor;
        cargarCombos(); // Cargar combos una vez que el profesor está disponible
    }

    public void setPreguntaParaEdicion(Pregunta pregunta) {
        this.preguntaEditando = pregunta;
        //if (profesorLogueado == null && pregunta.getCreadorCedulaProfesor() != null) {
            // Si el profesor no se pasó directamente, intentar cargarlo si es necesario para filtros
            // (Aunque generalmente el profesor que abre el diálogo es el logueado)
            // this.profesorLogueado = cargarProfesorPorCedula(pregunta.getCreadorCedulaProfesor());
        //}
        cargarCombos(); // Cargar combos primero

        txtTextoPregunta.setText(pregunta.getTexto());
        if (pregunta.getTiempoEstimado() != null) {
            spinnerTiempoEstimado.getValueFactory().setValue(pregunta.getTiempoEstimado());
        }
        if (pregunta.getPorcentaje() != null) {
            spinnerPorcentajeDefecto.getValueFactory().setValue(pregunta.getPorcentaje().doubleValue());
        }

        // Seleccionar valores en ComboBoxes
        comboTipoPregunta.setValue(comboTipoPregunta.getItems().stream().filter(tp -> tp != null && tp.getId() == pregunta.getTipoPreguntaId()).findFirst().orElse(null));
        comboContenido.setValue(comboContenido.getItems().stream().filter(c -> c != null && c.getIdContenido() == pregunta.getContenidoId()).findFirst().orElse(null));
        comboNivel.setValue(comboNivel.getItems().stream().filter(n -> n != null && n.getId() == pregunta.getNivelId()).findFirst().orElse(null));
        comboVisibilidad.setValue(comboVisibilidad.getItems().stream().filter(v -> v != null && v.getId() == pregunta.getVisibilidadId()).findFirst().orElse(null));
        if (pregunta.getPreguntaPadre() != null) {
            comboPreguntaPadre.setValue(comboPreguntaPadre.getItems().stream().filter(pp -> pp != null && pp.getIdPregunta() == pregunta.getPreguntaPadre()).findFirst().orElse(null));
        }

        // Cargar opciones si la pregunta las tiene
        if (pregunta.getIdPregunta() > 0) { // Asumiendo que ID > 0 significa que ya existe
            try (Connection conn = ConnectionOracle.conectar()) {
                List<OpcionPregunta> opcionesExistentes = preguntaRepository.obtenerOpcionesDePregunta(pregunta.getIdPregunta(), conn);
                TipoPregunta tipoSeleccionado = comboTipoPregunta.getValue();
                if (tipoSeleccionado != null) {
                    if (TIPO_OPCION_MULTIPLE.equalsIgnoreCase(tipoSeleccionado.getNombre())) {
                        opcionesExistentes.forEach(op -> anadirOpcionUIExistente(op.getRespuesta(), op.getEsCorrecta() == 'S'));
                    } else if (TIPO_VERDADERO_FALSO.equalsIgnoreCase(tipoSeleccionado.getNombre())) {
                        opcionesExistentes.stream()
                                .filter(op -> op.getEsCorrecta() == 'S')
                                .findFirst()
                                .ifPresent(opCorrecta -> {
                                    if ("Verdadero".equalsIgnoreCase(opCorrecta.getRespuesta())) {
                                        radioVerdadero.setSelected(true);
                                    } else if ("Falso".equalsIgnoreCase(opCorrecta.getRespuesta())) {
                                        radioFalso.setSelected(true);
                                    }
                                });
                    }
                }
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Cargar Opciones", "No se pudieron cargar las opciones de la pregunta: " + e.getMessage());
                e.printStackTrace();
            }


        }
    }

    public Pregunta getPreguntaCreadaOEditada() {
        return preguntaCreadaOEditada;
    }

    private void cargarCombos() {
        try {
            comboTipoPregunta.setItems(FXCollections.observableArrayList(catalogoRepository.listarTiposPregunta()));
            comboTipoPregunta.setConverter(new StringConverter<>() {
                @Override public String toString(TipoPregunta tp) { return tp == null ? "Seleccione tipo" : tp.getNombre(); }
                @Override public TipoPregunta fromString(String s) { return null; }
            });

            comboNivel.setItems(FXCollections.observableArrayList(catalogoRepository.listarNiveles()));
            comboNivel.setConverter(new StringConverter<>() {
                @Override public String toString(Nivel n) { return n == null ? "Seleccione nivel" : n.getNombre(); }
                @Override public Nivel fromString(String s) { return null; }
            });

            comboVisibilidad.setItems(FXCollections.observableArrayList(catalogoRepository.listarVisibilidades()));
            comboVisibilidad.setConverter(new StringConverter<>() {
                @Override public String toString(Visibilidad v) { return v == null ? "Seleccione visibilidad" : v.getNombre(); }
                @Override public Visibilidad fromString(String s) { return null; }
            });

            // Cargar todos los contenidos. Podría filtrarse por curso del profesor si se pasa el curso.
            comboContenido.setItems(FXCollections.observableArrayList(contenidoRepository.listarTodosLosContenidos()));
            comboContenido.setConverter(new StringConverter<>() {
                @Override public String toString(Contenido c) { return c == null ? "Seleccione tema/contenido" : c.getNombre(); }
                @Override public Contenido fromString(String s) { return null; }
            });

            // Cargar preguntas que pueden ser padre (preguntas simples del profesor)
            if (profesorLogueado != null) {
                // Necesitarías un método en PreguntaRepository que liste preguntas aptas para ser padre
                // (ej. que no sean ya subpreguntas y que pertenezcan al profesor o sean públicas)
                // List<Pregunta> preguntasPadre = preguntaRepository.listarPreguntasSimplesParaCombo(profesorLogueado.getCedula());
                // comboPreguntaPadre.setItems(FXCollections.observableArrayList(preguntasPadre));
                // comboPreguntaPadre.getItems().add(0, null); // Opción para no tener padre
                // comboPreguntaPadre.setConverter(new StringConverter<Pregunta>() {
                //    @Override public String toString(Pregunta p) { return p == null ? "Ninguna (Pregunta Principal)" : p.getIdPregunta() + ": " + p.getTexto().substring(0, Math.min(p.getTexto().length(), 50)) + "..."; }
                //    @Override public Pregunta fromString(String s) { return null; }
                // });
            } else {
                comboPreguntaPadre.setDisable(true);
            }

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los datos para los combos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarListenersTipoPregunta() {
        comboTipoPregunta.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            vboxOpcionesPregunta.setVisible(false);
            vboxOpcionesPregunta.setManaged(false);
            vboxVerdaderoFalso.setVisible(false);
            vboxVerdaderoFalso.setManaged(false);
            contenedorDinamicoOpciones.getChildren().clear();
            opcionesUIList.clear();

            if (newValue != null) {
                String tipoSeleccionado = newValue.getNombre();
                // Usar constantes para los nombres de los tipos de pregunta
                if (TIPO_OPCION_MULTIPLE.equalsIgnoreCase(tipoSeleccionado)) {
                    vboxOpcionesPregunta.setVisible(true);
                    vboxOpcionesPregunta.setManaged(true);
                    // Añadir al menos 2 opciones por defecto para UX
                    if (opcionesUIList.isEmpty() && (preguntaEditando == null || preguntaEditando.getTipoPreguntaId() != newValue.getId())) { // Solo si es nueva o el tipo cambió
                        handleAnadirOpcion(null);
                        handleAnadirOpcion(null);
                    }
                } else if (TIPO_VERDADERO_FALSO.equalsIgnoreCase(tipoSeleccionado)) {
                    vboxVerdaderoFalso.setVisible(true);
                    vboxVerdaderoFalso.setManaged(true);
                    if (preguntaEditando == null || preguntaEditando.getTipoPreguntaId() != newValue.getId()) {
                        grupoVerdaderoFalso.selectToggle(null); // Limpiar selección
                    }
                }
                // TODO: Implementar lógica para otros tipos de pregunta (Respuesta Corta, Emparejamiento, etc.)
                // que podrían no necesitar UI de opciones aquí o necesitar una diferente.
            }
        });
    }

    @FXML
    private void handleAnadirOpcion(ActionEvent event) {
        anadirOpcionUIExistente("", false);
    }

    private void anadirOpcionUIExistente(String texto, boolean esCorrecta) {
        HBox nuevaOpcionUI = new HBox(10);
        nuevaOpcionUI.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        TextField textoOpcion = new TextField(texto);
        textoOpcion.setPromptText("Texto de la opción");
        textoOpcion.setPrefWidth(400); // Ajustar según FXML
        HBox.setHgrow(textoOpcion, javafx.scene.layout.Priority.ALWAYS);

        CheckBox esCorrectaCheck = new CheckBox("Correcta");
        esCorrectaCheck.setSelected(esCorrecta);

        Button btnEliminarOpcion = new Button("X");
        btnEliminarOpcion.getStyleClass().add("button-eliminar-opcion-dialog");

        HBoxOpcionController opcionController = new HBoxOpcionController(textoOpcion, esCorrectaCheck, nuevaOpcionUI);
        opcionesUIList.add(opcionController);

        btnEliminarOpcion.setOnAction(e -> {
            contenedorDinamicoOpciones.getChildren().remove(nuevaOpcionUI);
            opcionesUIList.remove(opcionController);
        });

        nuevaOpcionUI.getChildren().addAll(textoOpcion, esCorrectaCheck, btnEliminarOpcion);
        contenedorDinamicoOpciones.getChildren().add(nuevaOpcionUI);
    }


    @FXML
    private void handleGuardarPregunta(ActionEvent event) {
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha identificado al profesor. No se puede guardar la pregunta.");
            return;
        }
        if (txtTextoPregunta.getText().trim().isEmpty() ||
                comboTipoPregunta.getValue() == null ||
                comboContenido.getValue() == null || // Tema es obligatorio
                comboNivel.getValue() == null ||
                comboVisibilidad.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campos Incompletos", "Texto, Tipo, Tema/Contenido, Nivel y Visibilidad son obligatorios.");
            return;
        }

        Integer tiempoEstimado = null;
        try {
            if (spinnerTiempoEstimado.getValue() != null ) { // Asumiendo que es Spinner<Integer>
                tiempoEstimado = spinnerTiempoEstimado.getValue();
                if (tiempoEstimado < 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato Incorrecto", "El tiempo estimado debe ser un número entero no negativo.");
            return;
        }


        Pregunta preguntaAGuardar = (preguntaEditando == null) ? new Pregunta() : preguntaEditando;
        preguntaAGuardar.setTexto(txtTextoPregunta.getText().trim());
        preguntaAGuardar.setTiempoEstimado(tiempoEstimado); // Asumiendo que Pregunta.java tiene setTiempoEstimado(Integer)
        preguntaAGuardar.setPorcentaje(BigDecimal.valueOf(spinnerPorcentajeDefecto.getValue()));
        preguntaAGuardar.setTipoPreguntaId(comboTipoPregunta.getValue().getId());
        preguntaAGuardar.setContenidoId(comboContenido.getValue().getIdContenido());
        preguntaAGuardar.setNivelId(comboNivel.getValue().getId());
        preguntaAGuardar.setVisibilidadId(comboVisibilidad.getValue().getId());
        preguntaAGuardar.setCreadorCedulaProfesor((int) profesorLogueado.getCedula()); // Siempre el profesor logueado

        if (comboPreguntaPadre.getValue() != null) {
            preguntaAGuardar.setPreguntaPadre(comboPreguntaPadre.getValue().getIdPregunta());
        } else {
            preguntaAGuardar.setPreguntaPadre(null);
        }

        List<OpcionPregunta> opcionesParaGuardar = new ArrayList<>();
        TipoPregunta tipoSeleccionado = comboTipoPregunta.getValue();

        if (TIPO_OPCION_MULTIPLE.equalsIgnoreCase(tipoSeleccionado.getNombre())) {
            if (opcionesUIList.size() < 2) { // Requisito de al menos 2 opciones
                mostrarAlerta(Alert.AlertType.ERROR, "Opciones Insuficientes", "Debe añadir al menos dos opciones para este tipo de pregunta.");
                return;
            }
            boolean alMenosUnaCorrecta = false;
            for (HBoxOpcionController opcionUI : opcionesUIList) {
                if (opcionUI.getTextoOpcion().trim().isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Opción Vacía", "El texto de todas las opciones es obligatorio.");
                    return;
                }
                OpcionPregunta op = new OpcionPregunta();
                op.setRespuesta(opcionUI.getTextoOpcion().trim());
                op.setEsCorrecta(opcionUI.esCorrecta() ? 'S' : 'N');
                if (op.getEsCorrecta() == 'S') alMenosUnaCorrecta = true;
                opcionesParaGuardar.add(op);
            }
            if (!alMenosUnaCorrecta) {
                mostrarAlerta(Alert.AlertType.ERROR, "Sin Respuesta Correcta", "Debe marcar al menos una opción como correcta para 'Selección Múltiple'.");
                return;
            }
        } else if (TIPO_VERDADERO_FALSO.equalsIgnoreCase(tipoSeleccionado.getNombre())) {
            if (grupoVerdaderoFalso.getSelectedToggle() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Selección Requerida", "Debe seleccionar si la respuesta es Verdadera o Falsa.");
                return;
            }
            OpcionPregunta opVerdadero = new OpcionPregunta();
            opVerdadero.setRespuesta("Verdadero"); // Texto estándar para la opción
            opVerdadero.setEsCorrecta(radioVerdadero.isSelected() ? 'S' : 'N');
            opcionesParaGuardar.add(opVerdadero);

            OpcionPregunta opFalso = new OpcionPregunta();
            opFalso.setRespuesta("Falso"); // Texto estándar para la opción
            opFalso.setEsCorrecta(radioFalso.isSelected() ? 'S' : 'N');
            opcionesParaGuardar.add(opFalso);
        }
        // TODO: Añadir lógica para recolectar respuestas para otros tipos de pregunta (Respuesta Corta, Emparejamiento, etc.)
        // Por ejemplo, para respuesta corta, la respuesta correcta podría ir en OpcionPregunta.

        try {
            if (preguntaEditando == null) { // Crear nueva
                int idPreguntaCreada = preguntaRepository.crearPreguntaCompleta(preguntaAGuardar, opcionesParaGuardar, profesorLogueado.getCedula());
                preguntaAGuardar.setIdPregunta(idPreguntaCreada);
                this.preguntaCreadaOEditada = preguntaAGuardar;
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Pregunta creada correctamente con ID: " + idPreguntaCreada);
            } else { // Actualizar existente
                preguntaRepository.actualizarPreguntaCompleta(preguntaAGuardar, opcionesParaGuardar, profesorLogueado.getNombre()); // O cédula como usuario modificacion
                this.preguntaCreadaOEditada = preguntaAGuardar;
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Pregunta ID: " + preguntaAGuardar.getIdPregunta() + " actualizada correctamente.");
            }
            if (dialogStage != null) dialogStage.close();
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo guardar la pregunta: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) { // Captura más general
            mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error al guardar la pregunta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelarCreacion(ActionEvent event) {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Clase interna para ayudar a manejar las opciones de UI dinámicas
    private static class HBoxOpcionController {
        TextField textoOpcionField;
        CheckBox esCorrectaCheck;
        HBox parentHBox; // Referencia al HBox que contiene estos controles

        public HBoxOpcionController(TextField textoOpcionField, CheckBox esCorrectaCheck, HBox parentHBox) {
            this.textoOpcionField = textoOpcionField;
            this.esCorrectaCheck = esCorrectaCheck;
            this.parentHBox = parentHBox;
        }
        public String getTextoOpcion() { return textoOpcionField.getText(); }
        public boolean esCorrecta() { return esCorrectaCheck.isSelected(); }
        // public HBox getParentHBox() { return parentHBox; } // No parece usarse, se puede quitar
    }
}

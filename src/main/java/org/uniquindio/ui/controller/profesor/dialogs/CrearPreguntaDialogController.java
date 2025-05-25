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
import org.uniquindio.model.entity.academico.Curso;
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
    @FXML private Spinner<Integer> spinnerTiempoEstimado;
    @FXML private Spinner<Double> spinnerPorcentajeDefecto;
    @FXML private Label lblInfoPorcentaje; // Label para explicar el porcentaje
    @FXML private ComboBox<Pregunta> comboPreguntaPadre;

    @FXML private VBox vboxOpcionesPregunta;
    @FXML private VBox contenedorDinamicoOpciones;
    @FXML private Button btnAnadirOpcion;

    @FXML private VBox vboxVerdaderoFalso;
    @FXML private RadioButton radioVerdadero;
    @FXML private RadioButton radioFalso;
    @FXML private ToggleGroup grupoVerdaderoFalso;

    @FXML private VBox vboxOrdenarConceptos;
    @FXML private VBox contenedorDinamicoOrdenar;
    @FXML private Button btnAnadirConceptoOrdenar;
    @FXML private ScrollPane scrollPaneOrdenarConceptos;

    @FXML private VBox vboxRelacionarConceptos;
    @FXML private VBox contenedorDinamicoRelacionar;
    @FXML private Button btnAnadirParRelacionar;
    @FXML private ScrollPane scrollPaneRelacionarConceptos;

    @FXML private VBox vboxSeleccionUnica; // Este VBox podría eliminarse si ID 5 usa vboxOpcionesPregunta
    @FXML private TextField txtRespuestaSeleccionUnica; // Este TextField podría eliminarse

    @FXML private Button btnGuardarPregunta;
    @FXML private Button btnCancelarCreacion;

    private static final String TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS = "Selección múltiple";
    private static final String TIPO_VERDADERO_FALSO = "Verdadero/Falso";
    private static final String TIPO_ORDENAR_CONCEPTOS = "Ordenar conceptos";
    private static final String TIPO_RELACIONAR_CONCEPTOS = "Relacionar conceptos";
    private static final String TIPO_SELECCION_UNICA_UNA_CORRECTA = "Seleccion Unica";

    private Profesor profesorLogueado;
    private Pregunta preguntaEditando;
    private Pregunta preguntaCreadaOEditada;
    private Stage dialogStage;

    private Curso cursoContextoExamen;
    private List<Contenido> contenidosValidosParaCursoContexto = new ArrayList<>();

    private PreguntaRepositoryImpl preguntaRepository = new PreguntaRepositoryImpl();
    private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    private ContenidoRepositoryImpl contenidoRepository = new ContenidoRepositoryImpl();

    private List<TextField> conceptosOrdenarList = new ArrayList<>();
    private List<HBoxParController> paresRelacionarList = new ArrayList<>();
    private List<HBoxOpcionController> opcionesUIList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarSpinners();
        configurarListenersTipoPregunta();
        configurarListenerPreguntaPadre();
        lblInfoPorcentaje.setText("Porcentaje/Peso de la pregunta (0-100)."); // Texto inicial
        lblInfoPorcentaje.setWrapText(true);
    }

    private void configurarSpinners() {
        SpinnerValueFactory.DoubleSpinnerValueFactory doubleFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.01, 100.0, 10.0, 0.01); // Min 0.01
        spinnerPorcentajeDefecto.setValueFactory(doubleFactory);
        spinnerPorcentajeDefecto.setEditable(true);
        // Formateador para asegurar dos decimales
        TextFormatter<Double> formatter = new TextFormatter<>(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if (object == null) return "10.00";
                return String.format("%.2f", object);
            }
            @Override
            public Double fromString(String string) {
                try {
                    double val = Double.parseDouble(string.replace(",", "."));
                    return Math.max(0.01, Math.min(100.0, val)); // Asegurar rango
                } catch (NumberFormatException e) { return 10.0; }
            }
        }, 10.0, change -> { // Valor por defecto si la entrada es inválida
            if (change.getControlNewText().matches("^\\d*([\\.,]\\d{0,2})?$")) {
                return change;
            }
            return null;
        });
        spinnerPorcentajeDefecto.getEditor().setTextFormatter(formatter);


        SpinnerValueFactory.IntegerSpinnerValueFactory intFactoryTiempo =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 5, 1);
        spinnerTiempoEstimado.setValueFactory(intFactoryTiempo);
        spinnerTiempoEstimado.setEditable(true);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void initData(Profesor profesor, Curso cursoDelExamenSiAplica, Pregunta preguntaAEditar) {
        this.profesorLogueado = profesor;
        this.cursoContextoExamen = cursoDelExamenSiAplica;
        this.preguntaEditando = preguntaAEditar;

        cargarCombos(); // Carga todos los combos, incluyendo el de pregunta padre

        if (this.preguntaEditando != null) {
            setPreguntaParaEdicionInternal(this.preguntaEditando);
        } else {
            // Es una nueva pregunta, actualizar la info del porcentaje según si hay padre o no
            actualizarInfoPorcentaje(comboPreguntaPadre.getValue() != null);
        }
    }

    private void configurarListenerPreguntaPadre() {
        comboPreguntaPadre.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            actualizarInfoPorcentaje(newVal != null);
            if (newVal != null) { // Si se selecciona una pregunta padre
                // Opcional: Deshabilitar la edición del contenido si se quiere que herede del padre
                // comboContenido.setDisable(true);
                // comboContenido.setValue(obtenerContenidoDePreguntaPadre(newVal));
            } else { // Si no hay pregunta padre (es principal)
                // comboContenido.setDisable(false);
            }
        });
    }

    private void actualizarInfoPorcentaje(boolean esSubpregunta) {
        if (esSubpregunta) {
            lblInfoPorcentaje.setText("Porcentaje de esta subpregunta relativo a su pregunta padre (0.01-100.00%). La suma de subpreguntas de un mismo padre no debe exceder 100%.");
        } else {
            lblInfoPorcentaje.setText("Porcentaje/Peso por defecto de esta pregunta principal (0.01-100.00%). El porcentaje final en un examen se definirá al añadirla.");
        }
    }


    private void setPreguntaParaEdicionInternal(Pregunta pregunta) {
        txtTextoPregunta.setText(pregunta.getTexto());
        if (pregunta.getTiempoEstimado() != null) {
            spinnerTiempoEstimado.getValueFactory().setValue(pregunta.getTiempoEstimado());
        }
        if (pregunta.getPorcentaje() != null) {
            spinnerPorcentajeDefecto.getValueFactory().setValue(pregunta.getPorcentaje().doubleValue());
        }

        comboTipoPregunta.setValue(comboTipoPregunta.getItems().stream().filter(tp -> tp != null && tp.getId() == pregunta.getTipoPreguntaId()).findFirst().orElse(null));
        comboContenido.setValue(comboContenido.getItems().stream().filter(c -> c != null && c.getIdContenido() == pregunta.getContenidoId()).findFirst().orElse(null));
        comboNivel.setValue(comboNivel.getItems().stream().filter(n -> n != null && n.getId() == pregunta.getNivelId()).findFirst().orElse(null));
        comboVisibilidad.setValue(comboVisibilidad.getItems().stream().filter(v -> v != null && v.getId() == pregunta.getVisibilidadId()).findFirst().orElse(null));

        if (pregunta.getPreguntaPadre() != null && pregunta.getPreguntaPadre() > 0) {
            comboPreguntaPadre.setValue(
                    comboPreguntaPadre.getItems().stream()
                            .filter(pp -> pp != null && pp.getIdPregunta() == pregunta.getPreguntaPadre())
                            .findFirst().orElse(null)
            );
        }
        actualizarInfoPorcentaje(pregunta.getPreguntaPadre() != null && pregunta.getPreguntaPadre() > 0);


        if (pregunta.getIdPregunta() > 0) {
            try (Connection conn = ConnectionOracle.conectar()) {
                List<OpcionPregunta> opcionesExistentes = preguntaRepository.obtenerOpcionesDePregunta(pregunta.getIdPregunta(), conn);
                TipoPregunta tipoSeleccionado = comboTipoPregunta.getValue();
                if (tipoSeleccionado != null) {
                    String nombreTipo = tipoSeleccionado.getNombre();

                    if (TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS.equalsIgnoreCase(nombreTipo) || TIPO_SELECCION_UNICA_UNA_CORRECTA.equalsIgnoreCase(nombreTipo)) {
                        opcionesExistentes.forEach(op -> anadirOpcionUIExistente(op.getRespuesta(), op.getEsCorrecta() == 'S'));
                    } else if (TIPO_VERDADERO_FALSO.equalsIgnoreCase(nombreTipo)) {
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
                    } else if (TIPO_ORDENAR_CONCEPTOS.equalsIgnoreCase(nombreTipo)) {
                        vboxOrdenarConceptos.setVisible(true);
                        vboxOrdenarConceptos.setManaged(true);
                        contenedorDinamicoOrdenar.getChildren().clear();
                        conceptosOrdenarList.clear();
                        opcionesExistentes.forEach(op -> anadirConceptoOrdenarUI(op.getRespuesta()));
                    } else if (TIPO_RELACIONAR_CONCEPTOS.equalsIgnoreCase(nombreTipo)) {
                        vboxRelacionarConceptos.setVisible(true);
                        vboxRelacionarConceptos.setManaged(true);
                        contenedorDinamicoRelacionar.getChildren().clear();
                        paresRelacionarList.clear();
                        opcionesExistentes.forEach(op -> {
                            String[] partes = op.getRespuesta().split(":::", 2);
                            if (partes.length == 2) anadirParRelacionarUI(partes[0], partes[1]);
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

            ObservableList<Contenido> contenidosDisponiblesParaCombo = FXCollections.observableArrayList();
            this.contenidosValidosParaCursoContexto.clear();

            if (cursoContextoExamen != null) {
                List<Contenido> temasDelCurso = contenidoRepository.listarContenidosPorCurso(cursoContextoExamen.getIdCurso());
                this.contenidosValidosParaCursoContexto.addAll(temasDelCurso);
                contenidosDisponiblesParaCombo.addAll(temasDelCurso);
                comboContenido.setPromptText("Seleccione tema del curso");
                if (temasDelCurso.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Sin Contenidos", "El curso '" + cursoContextoExamen.getNombre() + "' no tiene contenidos definidos. No podrá asociar esta pregunta a un tema específico de este curso.");
                }
            } else {
                List<Contenido> todosLosTemas = contenidoRepository.listarTodosLosContenidos();
                this.contenidosValidosParaCursoContexto.addAll(todosLosTemas); // Permitir cualquier tema si no hay curso
                contenidosDisponiblesParaCombo.addAll(todosLosTemas);
                comboContenido.setPromptText("Seleccione tema/contenido");
            }
            comboContenido.setItems(contenidosDisponiblesParaCombo);
            comboContenido.setConverter(new StringConverter<>() {
                @Override public String toString(Contenido c) { return c == null ? (cursoContextoExamen != null ? "Seleccione tema del curso" : "Seleccione tema/contenido") : c.getNombre(); }
                @Override public Contenido fromString(String s) { return null; }
            });

            if (profesorLogueado != null) {
                Integer idPreguntaActualAExcluir = (preguntaEditando != null) ? preguntaEditando.getIdPregunta() : null;
                List<Pregunta> preguntasPadreCandidatas = preguntaRepository.listarPreguntasCandidatasPadre(profesorLogueado.getCedula(), idPreguntaActualAExcluir);

                ObservableList<Pregunta> itemsPreguntaPadre = FXCollections.observableArrayList();
                itemsPreguntaPadre.add(null); // Opción para "Ninguna (Pregunta Principal)"
                itemsPreguntaPadre.addAll(preguntasPadreCandidatas);
                comboPreguntaPadre.setItems(itemsPreguntaPadre);

                comboPreguntaPadre.setConverter(new StringConverter<>() {
                    @Override public String toString(Pregunta p) {
                        if (p == null) {
                            return "Ninguna (Pregunta Principal)";
                        }
                        return p.getIdPregunta() + ": " + (p.getTexto() != null ? p.getTexto().substring(0, Math.min(p.getTexto().length(), 50)) + "..." : "Sin texto");
                    }
                    @Override public Pregunta fromString(String s) { return null; }
                });
                comboPreguntaPadre.setDisable(false);
            } else {
                comboPreguntaPadre.setDisable(true);
                comboPreguntaPadre.setPromptText("Profesor no identificado");
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
            vboxOrdenarConceptos.setVisible(false);
            vboxOrdenarConceptos.setManaged(false);
            vboxRelacionarConceptos.setVisible(false);
            vboxRelacionarConceptos.setManaged(false);
            vboxSeleccionUnica.setVisible(false);
            vboxSeleccionUnica.setManaged(false);

            boolean tipoCambioOEsNueva = (preguntaEditando == null) ||
                    (preguntaEditando != null && oldValue != null && newValue != null && oldValue.getId() != newValue.getId());

            if (tipoCambioOEsNueva) {
                contenedorDinamicoOpciones.getChildren().clear();
                opcionesUIList.clear();
                contenedorDinamicoOrdenar.getChildren().clear();
                conceptosOrdenarList.clear();
                contenedorDinamicoRelacionar.getChildren().clear();
                paresRelacionarList.clear();
                txtRespuestaSeleccionUnica.clear();
                if (grupoVerdaderoFalso.getSelectedToggle() != null) {
                    grupoVerdaderoFalso.getSelectedToggle().setSelected(false);
                }
            }

            if (newValue != null) {
                String tipoSeleccionado = newValue.getNombre();
                if (TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS.equalsIgnoreCase(tipoSeleccionado) ||
                        TIPO_SELECCION_UNICA_UNA_CORRECTA.equalsIgnoreCase(tipoSeleccionado) ) {
                    vboxOpcionesPregunta.setVisible(true);
                    vboxOpcionesPregunta.setManaged(true);
                    if (tipoCambioOEsNueva && opcionesUIList.isEmpty() &&
                            (preguntaEditando == null ||
                                    !( (TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS.equalsIgnoreCase(oldValue != null ? oldValue.getNombre() : "")) ||
                                            (TIPO_SELECCION_UNICA_UNA_CORRECTA.equalsIgnoreCase(oldValue != null ? oldValue.getNombre() : ""))
                                    )
                            )
                    ) {
                        handleAnadirOpcion(null); // Añadir al menos una opción
                        if(opcionesUIList.size() < 2) handleAnadirOpcion(null); // Y otra más si es el caso
                    }
                } else if (TIPO_VERDADERO_FALSO.equalsIgnoreCase(tipoSeleccionado)) {
                    vboxVerdaderoFalso.setVisible(true);
                    vboxVerdaderoFalso.setManaged(true);
                } else if (TIPO_ORDENAR_CONCEPTOS.equalsIgnoreCase(tipoSeleccionado)) {
                    vboxOrdenarConceptos.setVisible(true);
                    vboxOrdenarConceptos.setManaged(true);
                    if (tipoCambioOEsNueva && conceptosOrdenarList.isEmpty() && (preguntaEditando == null || !TIPO_ORDENAR_CONCEPTOS.equalsIgnoreCase(oldValue != null ? oldValue.getNombre() : ""))) {
                        handleAnadirConceptoOrdenar(null);
                        handleAnadirConceptoOrdenar(null);
                    }
                } else if (TIPO_RELACIONAR_CONCEPTOS.equalsIgnoreCase(tipoSeleccionado)) {
                    vboxRelacionarConceptos.setVisible(true);
                    vboxRelacionarConceptos.setManaged(true);
                    if (tipoCambioOEsNueva && paresRelacionarList.isEmpty() && (preguntaEditando == null || !TIPO_RELACIONAR_CONCEPTOS.equalsIgnoreCase(oldValue != null ? oldValue.getNombre() : ""))) {
                        handleAnadirParRelacionar(null);
                    }
                }
            }
        });
    }

    private void anadirConceptoOrdenarUI(String texto) {
        HBox hboxConcepto = new HBox(10);
        hboxConcepto.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        TextField textoConcepto = new TextField(texto);
        textoConcepto.setPromptText("Texto del concepto");
        HBox.setHgrow(textoConcepto, javafx.scene.layout.Priority.ALWAYS);
        conceptosOrdenarList.add(textoConcepto);

        Button btnEliminarConcepto = new Button("X");
        btnEliminarConcepto.getStyleClass().add("button-eliminar-opcion-dialog");
        btnEliminarConcepto.setOnAction(e -> {
            contenedorDinamicoOrdenar.getChildren().remove(hboxConcepto);
            conceptosOrdenarList.remove(textoConcepto);
        });

        hboxConcepto.getChildren().addAll(textoConcepto, btnEliminarConcepto);
        contenedorDinamicoOrdenar.getChildren().add(hboxConcepto);
    }

    private void anadirParRelacionarUI(String textoA, String textoB) {
        HBox hboxPar = new HBox(10);
        hboxPar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        TextField tfA = new TextField(textoA);
        tfA.setPromptText("Concepto A");
        HBox.setHgrow(tfA, javafx.scene.layout.Priority.ALWAYS);

        Label lblConector = new Label("<->");

        TextField tfB = new TextField(textoB);
        tfB.setPromptText("Concepto B");
        HBox.setHgrow(tfB, javafx.scene.layout.Priority.ALWAYS);

        HBoxParController parController = new HBoxParController(tfA, tfB, hboxPar);
        paresRelacionarList.add(parController);

        Button btnEliminarPar = new Button("X");
        btnEliminarPar.getStyleClass().add("button-eliminar-opcion-dialog");
        btnEliminarPar.setOnAction(e -> {
            contenedorDinamicoRelacionar.getChildren().remove(hboxPar);
            paresRelacionarList.remove(parController);
        });

        hboxPar.getChildren().addAll(tfA, lblConector, tfB, btnEliminarPar);
        contenedorDinamicoRelacionar.getChildren().add(hboxPar);
    }

    @FXML
    private void handleAnadirConceptoOrdenar(ActionEvent event) {
        anadirConceptoOrdenarUI("");
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
        textoOpcion.setPrefWidth(400);
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha identificado al profesor.");
            return;
        }
        if (txtTextoPregunta.getText().trim().isEmpty() ||
                comboTipoPregunta.getValue() == null ||
                comboContenido.getValue() == null ||
                comboNivel.getValue() == null ||
                comboVisibilidad.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campos Incompletos", "Texto, Tipo, Tema/Contenido, Nivel y Visibilidad son obligatorios.");
            return;
        }

        Contenido contenidoSeleccionado = comboContenido.getValue();
        Pregunta preguntaPadreSeleccionada = comboPreguntaPadre.getValue();

        if (cursoContextoExamen != null && preguntaPadreSeleccionada == null) { // Si es pregunta principal y hay contexto de curso
            boolean contenidoEsValidoParaCurso = this.contenidosValidosParaCursoContexto.stream()
                    .anyMatch(c -> c.getIdContenido() == contenidoSeleccionado.getIdContenido());
            if (!contenidoEsValidoParaCurso) {
                mostrarAlerta(Alert.AlertType.ERROR, "Contenido Inválido",
                        "El tema/contenido '" + contenidoSeleccionado.getNombre() +
                                "' no pertenece al curso '" + cursoContextoExamen.getNombre() +
                                "'. Seleccione un tema válido para este curso o cree la pregunta sin contexto de curso (desde el banco general).");
                return;
            }
        }
        // Si es subpregunta, el contenido podría heredarse o ser específico. Por ahora, se toma el del combo.

        Integer tiempoEstimado = null;
        try {
            if (spinnerTiempoEstimado.getValue() != null ) {
                tiempoEstimado = spinnerTiempoEstimado.getValue();
                if (tiempoEstimado < 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato Incorrecto", "El tiempo estimado debe ser un número entero no negativo.");
            return;
        }

        // Validación de porcentaje para subpreguntas
        if (preguntaPadreSeleccionada != null) {
            double porcentajeActualSubpregunta = spinnerPorcentajeDefecto.getValue();
            try {
                List<Pregunta> hermanas = preguntaRepository.obtenerSubpreguntasConPorcentaje(preguntaPadreSeleccionada.getIdPregunta());
                double sumaPorcentajesHermanas = hermanas.stream()
                        .filter(p -> preguntaEditando == null || p.getIdPregunta() != preguntaEditando.getIdPregunta()) // Excluir la actual si se edita
                        .mapToDouble(p -> p.getPorcentaje() != null ? p.getPorcentaje().doubleValue() : 0.0)
                        .sum();

                if (sumaPorcentajesHermanas + porcentajeActualSubpregunta > 100.01) { // Usar epsilon para doubles
                    mostrarAlerta(Alert.AlertType.ERROR, "Porcentaje Excedido para Subpreguntas",
                            "La suma de los porcentajes de las subpreguntas para '" + preguntaPadreSeleccionada.getTexto().substring(0, Math.min(preguntaPadreSeleccionada.getTexto().length(), 20)) + "...' (" +
                                    String.format("%.2f", sumaPorcentajesHermanas) + "% + " + String.format("%.2f", porcentajeActualSubpregunta) + "%) " +
                                    "excede el 100%. Ajuste los valores.");
                    return;
                }
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se pudo validar la suma de porcentajes de subpreguntas hermanas: " + e.getMessage());
            }
        }


        Pregunta preguntaAGuardar = (preguntaEditando == null) ? new Pregunta() : preguntaEditando;
        preguntaAGuardar.setTexto(txtTextoPregunta.getText().trim());
        preguntaAGuardar.setTiempoEstimado(tiempoEstimado);
        preguntaAGuardar.setPorcentaje(BigDecimal.valueOf(spinnerPorcentajeDefecto.getValue()));
        preguntaAGuardar.setTipoPreguntaId(comboTipoPregunta.getValue().getId());
        preguntaAGuardar.setContenidoId(contenidoSeleccionado.getIdContenido());
        preguntaAGuardar.setNivelId(comboNivel.getValue().getId());
        preguntaAGuardar.setVisibilidadId(comboVisibilidad.getValue().getId());
        preguntaAGuardar.setCreadorCedulaProfesor((int) profesorLogueado.getCedula());

        if (preguntaPadreSeleccionada != null) {
            preguntaAGuardar.setPreguntaPadre(preguntaPadreSeleccionada.getIdPregunta());
        } else {
            preguntaAGuardar.setPreguntaPadre(null);
        }

        List<OpcionPregunta> opcionesParaGuardar = new ArrayList<>();
        TipoPregunta tipoSeleccionadoObj = comboTipoPregunta.getValue();
        String nombreTipoSeleccionado = tipoSeleccionadoObj.getNombre();

        if (TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS.equalsIgnoreCase(nombreTipoSeleccionado)) {
            if (opcionesUIList.size() < 2) {
                mostrarAlerta(Alert.AlertType.ERROR, "Opciones Insuficientes", "Debe añadir al menos dos opciones para 'Selección Múltiple'.");
                return;
            }
            int correctasMarcadas = 0;
            for (HBoxOpcionController opcionUI : opcionesUIList) {
                if (opcionUI.getTextoOpcion().trim().isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Opción Vacía", "El texto de todas las opciones es obligatorio.");
                    return;
                }
                OpcionPregunta op = new OpcionPregunta();
                op.setRespuesta(opcionUI.getTextoOpcion().trim());
                op.setEsCorrecta(opcionUI.esCorrecta() ? 'S' : 'N');
                if (op.getEsCorrecta() == 'S') {
                    correctasMarcadas++;
                }
                opcionesParaGuardar.add(op);
            }
            if (correctasMarcadas == 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Sin Respuesta Correcta", "Debe marcar al menos una opción como correcta para 'Selección Múltiple'.");
                return;
            }
        } else if (TIPO_SELECCION_UNICA_UNA_CORRECTA.equalsIgnoreCase(nombreTipoSeleccionado)) {
            if (opcionesUIList.size() < 2) {
                mostrarAlerta(Alert.AlertType.ERROR, "Opciones Insuficientes", "Debe añadir al menos dos opciones para 'Seleccion Unica'.");
                return;
            }
            int correctasMarcadas = 0;
            for (HBoxOpcionController opcionUI : opcionesUIList) {
                if (opcionUI.getTextoOpcion().trim().isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Opción Vacía", "El texto de todas las opciones es obligatorio.");
                    return;
                }
                OpcionPregunta op = new OpcionPregunta();
                op.setRespuesta(opcionUI.getTextoOpcion().trim());
                op.setEsCorrecta(opcionUI.esCorrecta() ? 'S' : 'N');
                if (op.getEsCorrecta() == 'S') {
                    correctasMarcadas++;
                }
                opcionesParaGuardar.add(op);
            }
            if (correctasMarcadas != 1) {
                mostrarAlerta(Alert.AlertType.ERROR, "Respuesta Correcta Inválida", "Para 'Seleccion Unica', debe marcar exactamente una opción como correcta. Encontradas: " + correctasMarcadas);
                return;
            }
        } else if (TIPO_VERDADERO_FALSO.equalsIgnoreCase(nombreTipoSeleccionado)) {
            if (grupoVerdaderoFalso.getSelectedToggle() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Selección Requerida", "Debe seleccionar si la respuesta es Verdadera o Falsa.");
                return;
            }
            OpcionPregunta opVerdadero = new OpcionPregunta();
            opVerdadero.setRespuesta("Verdadero");
            opVerdadero.setEsCorrecta(radioVerdadero.isSelected() ? 'S' : 'N');
            opcionesParaGuardar.add(opVerdadero);

            OpcionPregunta opFalso = new OpcionPregunta();
            opFalso.setRespuesta("Falso");
            opFalso.setEsCorrecta(radioFalso.isSelected() ? 'S' : 'N');
            opcionesParaGuardar.add(opFalso);
        }else if (TIPO_ORDENAR_CONCEPTOS.equalsIgnoreCase(nombreTipoSeleccionado)) {
            if (conceptosOrdenarList.size() < 2) {
                mostrarAlerta(Alert.AlertType.ERROR, "Conceptos Insuficientes", "Debe añadir al menos dos conceptos para ordenar.");
                return;
            }
            for (TextField tfConcepto : conceptosOrdenarList) {
                String textoConcepto = tfConcepto.getText().trim();
                if (textoConcepto.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Concepto Vacío", "El texto de todos los conceptos a ordenar es obligatorio.");
                    return;
                }
                OpcionPregunta op = new OpcionPregunta();
                op.setRespuesta(textoConcepto);
                op.setEsCorrecta('S');
                opcionesParaGuardar.add(op);
            }
        } else if (TIPO_RELACIONAR_CONCEPTOS.equalsIgnoreCase(nombreTipoSeleccionado)) {
            if (paresRelacionarList.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Pares Insuficientes", "Debe añadir al menos un par de conceptos a relacionar.");
                return;
            }
            for (HBoxParController parCtrl : paresRelacionarList) {
                String conceptoA = parCtrl.getTextoConceptoA().trim();
                String conceptoB = parCtrl.getTextoConceptoB().trim();
                if (conceptoA.isEmpty() || conceptoB.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Par Incompleto", "Ambos conceptos de un par deben tener texto.");
                    return;
                }
                OpcionPregunta op = new OpcionPregunta();
                op.setRespuesta(conceptoA + ":::" + conceptoB);
                op.setEsCorrecta('S');
                opcionesParaGuardar.add(op);
            }
        }

        try {
            if (preguntaEditando == null) {
                int idPreguntaCreada = preguntaRepository.crearPreguntaCompleta(preguntaAGuardar, opcionesParaGuardar, profesorLogueado.getCedula());
                preguntaAGuardar.setIdPregunta(idPreguntaCreada);
                this.preguntaCreadaOEditada = preguntaAGuardar;
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Pregunta creada correctamente con ID: " + idPreguntaCreada);
            } else {
                preguntaRepository.actualizarPreguntaCompleta(preguntaAGuardar, opcionesParaGuardar, profesorLogueado.getNombre());
                this.preguntaCreadaOEditada = preguntaAGuardar;
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Pregunta ID: " + preguntaAGuardar.getIdPregunta() + " actualizada correctamente.");
            }
            if (dialogStage != null) dialogStage.close();
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo guardar la pregunta: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
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

    private static class HBoxOpcionController {
        TextField textoOpcionField;
        CheckBox esCorrectaCheck;
        HBox parentHBox;

        public HBoxOpcionController(TextField textoOpcionField, CheckBox esCorrectaCheck, HBox parentHBox) {
            this.textoOpcionField = textoOpcionField;
            this.esCorrectaCheck = esCorrectaCheck;
            this.parentHBox = parentHBox;
        }
        public String getTextoOpcion() { return textoOpcionField.getText(); }
        public boolean esCorrecta() { return esCorrectaCheck.isSelected(); }
    }

    private static class HBoxParController {
        TextField textoConceptoAField;
        TextField textoConceptoBField;
        HBox parentHBox;

        public HBoxParController(TextField textoConceptoAField, TextField textoConceptoBField, HBox parentHBox) {
            this.textoConceptoAField = textoConceptoAField;
            this.textoConceptoBField = textoConceptoBField;
            this.parentHBox = parentHBox;
        }
        public String getTextoConceptoA() { return textoConceptoAField.getText(); }
        public String getTextoConceptoB() { return textoConceptoBField.getText(); }
    }

    @FXML
    private void handleAnadirParRelacionar(ActionEvent event) {
        anadirParRelacionarUI("","");
    }
}

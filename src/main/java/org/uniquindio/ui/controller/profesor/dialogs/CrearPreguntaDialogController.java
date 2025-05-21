package org.uniquindio.ui.controller.profesor.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.uniquindio.model.entity.academico.*; // Asumiendo que tienes esta entidad
import org.uniquindio.model.entity.evaluacion.*; // Tu entidad Pregunta
import org.uniquindio.model.entity.catalogo.Nivel; // Asumiendo entidad
import org.uniquindio.model.entity.catalogo.TipoPregunta; // Asumiendo entidad
import org.uniquindio.model.entity.catalogo.Visibilidad; // Asumiendo entidad
import org.uniquindio.model.entity.usuario.Profesor;

// Importa tus clases de Repositorio que llamarán a PL/SQL
// import org.uniquindio.repository.impl.PreguntaRepositoryImpl;
// import org.uniquindio.repository.impl.CatalogoRepositoryImpl; // Para Tipos, Niveles, Visibilidad
// import org.uniquindio.repository.impl.ContenidoRepositoryImpl; // Para Temas/Contenido

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CrearPreguntaDialogController implements Initializable {

    @FXML private TextArea txtTextoPregunta;
    @FXML private ComboBox<TipoPregunta> comboTipoPregunta;
    @FXML private ComboBox<Contenido> comboContenido;
    @FXML private ComboBox<Nivel> comboNivel;
    @FXML private ComboBox<Visibilidad> comboVisibilidad;
    @FXML private TextField txtTiempoEstimado;
    @FXML private Spinner<Double> spinnerPorcentajeDefecto;
    @FXML private ComboBox<Pregunta> comboPreguntaPadre; // Para subpreguntas

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
    private Pregunta preguntaCreada; // Para devolverla al controlador que llamó
    private Stage dialogStage;

    // Repositorios
    // private PreguntaRepositoryImpl preguntaRepository = new PreguntaRepositoryImpl();
    // private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    // private ContenidoRepositoryImpl contenidoRepository = new ContenidoRepositoryImpl();

    // Lista para manejar las opciones de forma dinámica en la UI
    private List<HBoxOpcionController> opcionesUIList = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarCombos();
        configurarListenersTipoPregunta();
        // Inicializar spinner para porcentaje
        SpinnerValueFactory.DoubleSpinnerValueFactory doubleFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100.0, 10.0, 1.0);
        spinnerPorcentajeDefecto.setValueFactory(doubleFactory);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setProfesor(Profesor profesor) {
        this.profesorLogueado = profesor;
        // Aquí podrías filtrar el comboContenido basado en los cursos del profesor si es necesario
        // cargarContenidosDelProfesor();
    }

    // Este método es para cargar una pregunta existente si el diálogo se usa para editar.
    // Por ahora, nos enfocamos en crear.
    public void setPreguntaParaEdicion(Pregunta pregunta) {
        // ... lógica para llenar los campos si se edita una pregunta ...
    }

    public Pregunta getPreguntaCreada() {
        return preguntaCreada;
    }

    private void cargarCombos() {
        // TODO: Cargar datos para ComboBoxes desde la BD usando repositorios y PL/SQL
        // Ejemplo:
        // comboTipoPregunta.setItems(FXCollections.observableArrayList(catalogoRepository.listarTiposPregunta()));
        // comboNivel.setItems(FXCollections.observableArrayList(catalogoRepository.listarNiveles()));
        // comboVisibilidad.setItems(FXCollections.observableArrayList(catalogoRepository.listarVisibilidades()));
        // comboContenido.setItems(FXCollections.observableArrayList(contenidoRepository.listarContenidosActivos()));
        // comboPreguntaPadre.setItems(FXCollections.observableArrayList(preguntaRepository.listarPreguntasSimplesDelProfesor(profesorLogueado.getCedula()))); // Para subpreguntas

        // Placeholder data
        // comboTipoPregunta.setItems(FXCollections.observableArrayList(new TipoPregunta(1, "Opción Múltiple"), new TipoPregunta(2, "Verdadero/Falso")));
        // comboNivel.setItems(FXCollections.observableArrayList(new Nivel(1, "Básico"), new Nivel(2, "Intermedio")));
        // comboVisibilidad.setItems(FXCollections.observableArrayList(new Visibilidad(1, "Pública"), new Visibilidad(2, "Privada")));
        // comboContenido.setItems(FXCollections.observableArrayList(new Contenido(1, "Tema 1 de Cálculo")));
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
                // Asumiendo que tienes una forma de identificar los tipos, ej. por nombre o ID
                // String tipoSeleccionado = newValue.getNombre(); // Si TipoPregunta tiene getNombre()
                // if ("Opción Múltiple".equalsIgnoreCase(tipoSeleccionado) || "Selección Múltiple".equalsIgnoreCase(tipoSeleccionado)) {
                //     vboxOpcionesPregunta.setVisible(true);
                //     vboxOpcionesPregunta.setManaged(true);
                //     // Añadir al menos 2 opciones por defecto
                //     handleAnadirOpcion(null);
                //     handleAnadirOpcion(null);
                // } else if ("Verdadero/Falso".equalsIgnoreCase(tipoSeleccionado)) {
                //    vboxVerdaderoFalso.setVisible(true);
                //    vboxVerdaderoFalso.setManaged(true);
                // }
                // Implementar lógica para otros tipos de pregunta que requieran UI de opciones diferente
            }
        });
    }

    @FXML
    private void handleAnadirOpcion(ActionEvent event) {
        // Este método crea una nueva fila de UI para una opción de pregunta (TextField + CheckBox)
        HBox nuevaOpcionUI = new HBox(10);
        nuevaOpcionUI.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        TextField textoOpcion = new TextField();
        textoOpcion.setPromptText("Texto de la opción");
        textoOpcion.setPrefWidth(400);
        CheckBox esCorrectaCheck = new CheckBox("Correcta");
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
        // Validaciones
        if (txtTextoPregunta.getText().trim().isEmpty() ||
                comboTipoPregunta.getValue() == null ||
                comboContenido.getValue() == null ||
                comboNivel.getValue() == null ||
                comboVisibilidad.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campos Incompletos", "Texto, Tipo, Tema, Nivel y Visibilidad son obligatorios.");
            return;
        }

        Pregunta nuevaPregunta = new Pregunta();
        nuevaPregunta.setTexto(txtTextoPregunta.getText().trim());
        nuevaPregunta.setTiempo(txtTiempoEstimado.getText().trim().isEmpty() ? null : txtTiempoEstimado.getText().trim());
        nuevaPregunta.setPorcentaje(BigDecimal.valueOf(spinnerPorcentajeDefecto.getValue()));
        nuevaPregunta.setTipoPreguntaId(comboTipoPregunta.getValue().getId()); // Asumiendo que TipoPregunta tiene getId()
        nuevaPregunta.setContenidoId(comboContenido.getValue().getIdContenido()); // Asumiendo que Contenido tiene getId_contenido()
        nuevaPregunta.setNivelId(comboNivel.getValue().getId()); // Asumiendo que Nivel tiene getId()
        nuevaPregunta.setVisibilidadId(comboVisibilidad.getValue().getId()); // Asumiendo que Visibilidad tiene getId()
        if (comboPreguntaPadre.getValue() != null) {
            nuevaPregunta.setPreguntaPadre(comboPreguntaPadre.getValue().getIdPregunta());
        }

        List<OpcionPregunta> opciones = new ArrayList<>();
        // String tipoSeleccionado = comboTipoPregunta.getValue().getNombre();

        // if ("Opción Múltiple".equalsIgnoreCase(tipoSeleccionado) || "Selección Múltiple".equalsIgnoreCase(tipoSeleccionado)) {
        //     if (opcionesUIList.size() < 2) {
        //         mostrarAlerta(Alert.AlertType.ERROR, "Opciones Insuficientes", "Debe añadir al menos dos opciones para este tipo de pregunta.");
        //         return;
        //     }
        //     boolean alMenosUnaCorrecta = false;
        //     for (HBoxOpcionController opcionUI : opcionesUIList) {
        //         if (opcionUI.getTextoOpcion().trim().isEmpty()) {
        //             mostrarAlerta(Alert.AlertType.ERROR, "Opción Vacía", "El texto de todas las opciones es obligatorio.");
        //             return;
        //         }
        //         OpcionPregunta op = new OpcionPregunta();
        //         op.setRespuesta(opcionUI.getTextoOpcion().trim());
        //         op.setEsCorrecta(opcionUI.esCorrecta() ? 'S' : 'N');
        //         if (op.getEsCorrecta() == 'S') alMenosUnaCorrecta = true;
        //         opciones.add(op);
        //     }
        //     if (!alMenosUnaCorrecta) {
        //         mostrarAlerta(Alert.AlertType.ERROR, "Sin Respuesta Correcta", "Debe marcar al menos una opción como correcta.");
        //         return;
        //     }
        // } else if ("Verdadero/Falso".equalsIgnoreCase(tipoSeleccionado)) {
        //     if (grupoVerdaderoFalso.getSelectedToggle() == null) {
        //         mostrarAlerta(Alert.AlertType.ERROR, "Selección Requerida", "Debe seleccionar si la respuesta es Verdadera o Falsa.");
        //         return;
        //     }
        //     OpcionPregunta opVerdadero = new OpcionPregunta();
        //     opVerdadero.setRespuesta("Verdadero");
        //     opVerdadero.setEsCorrecta(radioVerdadero.isSelected() ? 'S' : 'N');
        //     opciones.add(opVerdadero);

        //     OpcionPregunta opFalso = new OpcionPregunta();
        //     opFalso.setRespuesta("Falso");
        //     opFalso.setEsCorrecta(radioFalso.isSelected() ? 'S' : 'N');
        //     opciones.add(opFalso);
        // }
        // ... Lógica para otros tipos de pregunta

        // TODO: Llamar a procedimiento PL/SQL via Repositorio para guardar la pregunta y sus opciones.
        // El procedimiento PL/SQL debería devolver el ID de la pregunta creada.
        // try {
        //     int idPreguntaCreada = preguntaRepository.crearPreguntaCompletaPLSQL(nuevaPregunta, opciones, profesorLogueado.getCedula());
        //     nuevaPregunta.setIdPregunta(idPreguntaCreada); // Asignar el ID devuelto
        //     this.preguntaCreada = nuevaPregunta;
        //     mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Pregunta creada correctamente con ID: " + idPreguntaCreada);
        //     dialogStage.close();
        // } catch (SQLException e) {
        //     mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo guardar la pregunta: " + e.getMessage());
        //     e.printStackTrace();
        // }
        mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado (Simulado)", "Lógica de guardado de pregunta con PL/SQL debe implementarse.");
        this.preguntaCreada = nuevaPregunta; // Simulación
        if (dialogStage != null) dialogStage.close();

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
        HBox parentHBox;

        public HBoxOpcionController(TextField textoOpcionField, CheckBox esCorrectaCheck, HBox parentHBox) {
            this.textoOpcionField = textoOpcionField;
            this.esCorrectaCheck = esCorrectaCheck;
            this.parentHBox = parentHBox;
        }
        public String getTextoOpcion() { return textoOpcionField.getText(); }
        public boolean esCorrecta() { return esCorrectaCheck.isSelected(); }
        public HBox getParentHBox() { return parentHBox; }
    }
}
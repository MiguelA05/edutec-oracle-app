package org.uniquindio.ui.controller.estudiante;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.uniquindio.model.dto.PreguntaPresentacionDTO;
import org.uniquindio.model.dto.ResultadoExamenDTO;
import org.uniquindio.model.entity.evaluacion.Examen;
import org.uniquindio.model.entity.usuario.Estudiante;
import org.uniquindio.repository.impl.ExamenRepositoryImpl;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PresentacionExamenController implements Initializable {

    @FXML private BorderPane rootPresentacionExamen;
    @FXML private Label lblTituloExamen;
    @FXML private Label lblTiempoRestante;
    @FXML private ProgressBar progressBarTiempo;
    @FXML private Label lblProgresoPreguntas;
    @FXML private Label lblNumeroPregunta;
    @FXML private Label lblTextoPregunta;
    @FXML private Label lblTipoPreguntaInfo;
    @FXML private Label lblTiempoSugeridoPregunta;
    @FXML private VBox contenedorOpcionesRespuesta;
    @FXML private Button btnAnterior;
    @FXML private Button btnMarcarParaRevision;
    @FXML private Button btnSiguiente;
    @FXML private Button btnFinalizarExamen;

    private Estudiante estudianteLogueado;
    private int idExamen;
    private int idPresentacionExamen;
    private Examen infoExamen;

    private List<PreguntaPresentacionDTO> preguntasDelExamen;
    private int preguntaActualIndex = 0;
    // Key: ID de PreguntaExamenEstudiante
    // Value: Para selección única/V-F: Integer (ID de OpcionPregunta)
    //        Para selección múltiple: List<Integer> (IDs de OpcionPregunta seleccionadas)
    //        Para texto/ordenar/relacionar: String
    private Map<Integer, Object> respuestasEstudiante = new HashMap<>();
    private Map<Integer, Boolean> preguntasMarcadasRevision = new HashMap<>();

    private Timeline temporizadorExamen;
    private long tiempoRestanteSegundos;

    private ExamenRepositoryImpl examenRepository = new ExamenRepositoryImpl();

    // Constantes para los nombres de tipos de pregunta (deben coincidir con los de la BD)
    private static final String TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS = "Selección múltiple"; // ID 1 (Ahora con múltiples respuestas posibles)
    private static final String TIPO_VERDADERO_FALSO = "Verdadero/Falso";         // ID 2
    private static final String TIPO_ORDENAR_CONCEPTOS = "Ordenar conceptos";     // ID 3
    private static final String TIPO_RELACIONAR_CONCEPTOS = "Relacionar conceptos"; // ID 4
    private static final String TIPO_SELECCION_UNICA_UNA_CORRECTA = "Seleccion Unica"; // ID 5 (Múltiples opciones, una correcta)


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initData se llama externamente
    }

    public void initData(Estudiante estudiante, int examenId, int presentacionExamenId, Examen infoExamenGeneral) {
        this.estudianteLogueado = estudiante;
        this.idExamen = examenId;
        this.idPresentacionExamen = presentacionExamenId;
        this.infoExamen = infoExamenGeneral;

        if (this.infoExamen != null && this.infoExamen.getNombre() != null && !this.infoExamen.getNombre().isEmpty()) {
            lblTituloExamen.setText(this.infoExamen.getNombre());
        } else {
            lblTituloExamen.setText("Examen en Curso");
        }

        cargarPreguntasDelExamen(); // Esto también llama a mostrarPreguntaActual
        if (this.infoExamen != null && this.infoExamen.getTiempo() != null && this.infoExamen.getTiempo() > 0) {
            iniciarTemporizador((long)this.infoExamen.getTiempo() * 60);
        } else {
            lblTiempoRestante.setText("Tiempo: Ilimitado");
            progressBarTiempo.setProgress(1.0);
            progressBarTiempo.setVisible(false);
        }
    }

    private void cargarPreguntasDelExamen() {
        try {
            this.preguntasDelExamen = examenRepository.obtenerPreguntasParaPresentacion(this.idPresentacionExamen);
            if (this.preguntasDelExamen == null || this.preguntasDelExamen.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Examen", "No se pudieron cargar las preguntas para este examen.");
                btnSiguiente.setDisable(true);
                btnAnterior.setDisable(true);
                btnFinalizarExamen.setDisable(true);
                return;
            }
            preguntaActualIndex = 0;
            mostrarPreguntaActual(); // Llama a renderizar y actualizar botones
        } catch (SQLException e) {
            System.err.println("Error SQL al cargar preguntas del examen: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "Hubo un problema al cargar las preguntas del examen.");
        }
    }

    private void mostrarPreguntaActual() {
        if (preguntasDelExamen == null || preguntaActualIndex < 0 || preguntaActualIndex >= preguntasDelExamen.size()) {
            return;
        }
        PreguntaPresentacionDTO preguntaActual = preguntasDelExamen.get(preguntaActualIndex);
        lblNumeroPregunta.setText("Pregunta " + (preguntaActualIndex + 1) + ":");
        lblTextoPregunta.setText(preguntaActual.getTextoPregunta());
        lblTipoPreguntaInfo.setText("(Tipo: " + preguntaActual.getTipoPreguntaNombre() + ")");

        if (preguntaActual.getTiempoSugerido() != null && !preguntaActual.getTiempoSugerido().isEmpty() && !preguntaActual.getTiempoSugerido().equals("0 min")) {
            lblTiempoSugeridoPregunta.setText("Tiempo sugerido: " + preguntaActual.getTiempoSugerido());
            lblTiempoSugeridoPregunta.setVisible(true);
            lblTiempoSugeridoPregunta.setManaged(true);
        } else {
            lblTiempoSugeridoPregunta.setVisible(false);
            lblTiempoSugeridoPregunta.setManaged(false);
        }

        lblProgresoPreguntas.setText("Pregunta " + (preguntaActualIndex + 1) + " de " + preguntasDelExamen.size());
        renderizarOpcionesRespuesta(preguntaActual);
        actualizarEstadoBotonesNavegacion();
        btnMarcarParaRevision.setText(
                preguntasMarcadasRevision.getOrDefault(preguntaActual.getPreguntaExamenEstudianteId(), false) ?
                        "Desmarcar" : "Marcar"
        );
    }

    @SuppressWarnings("unchecked") // Para el casteo de respuestaGuardada a List<Integer>
    private void renderizarOpcionesRespuesta(PreguntaPresentacionDTO pregunta) {
        contenedorOpcionesRespuesta.getChildren().clear();
        String tipoNombre = pregunta.getTipoPreguntaNombre();
        Object respuestaGuardada = respuestasEstudiante.get(pregunta.getPreguntaExamenEstudianteId());

        if (TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS.equalsIgnoreCase(tipoNombre)) { // ID 1 - Múltiples correctas
            if (pregunta.getOpciones() != null) {
                for (PreguntaPresentacionDTO.OpcionPresentacionDTO opcion : pregunta.getOpciones()) {
                    CheckBox cb = new CheckBox(opcion.getTextoOpcion());
                    cb.setUserData(opcion.getIdOpcion());
                    cb.setWrapText(true);
                    contenedorOpcionesRespuesta.getChildren().add(cb);
                    if (respuestaGuardada instanceof List) {
                        List<Integer> idsSeleccionados = (List<Integer>) respuestaGuardada;
                        if (idsSeleccionados.contains(opcion.getIdOpcion())) {
                            cb.setSelected(true);
                        }
                    }
                }
            }
        } else if (TIPO_SELECCION_UNICA_UNA_CORRECTA.equalsIgnoreCase(tipoNombre) || TIPO_VERDADERO_FALSO.equalsIgnoreCase(tipoNombre)) { // ID 5 y ID 2
            ToggleGroup grupoOpciones = new ToggleGroup();
            if (pregunta.getOpciones() != null) {
                for (PreguntaPresentacionDTO.OpcionPresentacionDTO opcion : pregunta.getOpciones()) {
                    RadioButton rb = new RadioButton(opcion.getTextoOpcion());
                    rb.setUserData(opcion.getIdOpcion());
                    rb.setToggleGroup(grupoOpciones);
                    rb.setWrapText(true);
                    contenedorOpcionesRespuesta.getChildren().add(rb);
                    if (respuestaGuardada != null && respuestaGuardada.equals(opcion.getIdOpcion())) {
                        rb.setSelected(true);
                    }
                }
            }
        } else if (TIPO_ORDENAR_CONCEPTOS.equalsIgnoreCase(tipoNombre)) {
            Label info = new Label("Ordene los siguientes conceptos arrastrándolos:");
            VBox conceptosBox = new VBox(5);
            conceptosBox.getStyleClass().add("conceptos-ordenar-box");

            List<String> conceptosAOrdenar = new ArrayList<>();
            if (pregunta.getOpciones() != null) {
                pregunta.getOpciones().forEach(op -> conceptosAOrdenar.add(op.getTextoOpcion()));
            }

            if (respuestaGuardada instanceof String && !((String)respuestaGuardada).isEmpty()){
                conceptosAOrdenar.clear();
                conceptosAOrdenar.addAll(Arrays.asList(((String)respuestaGuardada).split(":::")));
            } else {
                Collections.shuffle(conceptosAOrdenar);
            }

            for(String concepto : conceptosAOrdenar) {
                Label lblConcepto = new Label(concepto);
                lblConcepto.getStyleClass().add("concepto-ordenar-item");
                configurarDragAndDrop(lblConcepto, conceptosBox);
                conceptosBox.getChildren().add(lblConcepto);
            }
            contenedorOpcionesRespuesta.getChildren().addAll(info, conceptosBox);

        } else if (TIPO_RELACIONAR_CONCEPTOS.equalsIgnoreCase(tipoNombre)) {
            Label info = new Label("Implementar UI para relacionar conceptos.");
            contenedorOpcionesRespuesta.getChildren().add(info);
        } else {
            Label placeholder = new Label("Tipo de pregunta '" + tipoNombre + "' no implementado para renderizar.");
            contenedorOpcionesRespuesta.getChildren().add(placeholder);
        }
    }

    private void configurarDragAndDrop(Label label, VBox parentContainer) {
        label.setOnDragDetected(event -> {
            Dragboard db = label.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(label.getText());
            db.setContent(content);
            event.consume();
        });

        parentContainer.setOnDragOver(event -> {
            if (event.getGestureSource() != parentContainer && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        parentContainer.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                //String draggedText = db.getString(); // No es necesario si usamos la fuente
                Label sourceLabel = null;

                if(event.getGestureSource() instanceof Label){
                    sourceLabel = (Label)event.getGestureSource();
                }

                if (sourceLabel != null && parentContainer.getChildren().contains(sourceLabel)) {
                    int newIndex = 0;
                    for (javafx.scene.Node node : parentContainer.getChildren()) {
                        if (node == sourceLabel) continue;
                        if (event.getY() < node.getBoundsInParent().getMinY() + node.getBoundsInParent().getHeight() / 2) {
                            break;
                        }
                        newIndex++;
                    }
                    parentContainer.getChildren().remove(sourceLabel);
                    if (newIndex >= parentContainer.getChildren().size()) { // >= para añadir al final si es necesario
                        parentContainer.getChildren().add(sourceLabel);
                    } else {
                        parentContainer.getChildren().add(newIndex, sourceLabel);
                    }
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void guardarRespuestaActual() {
        if (preguntasDelExamen == null || preguntaActualIndex < 0 || preguntaActualIndex >= preguntasDelExamen.size()) {
            return;
        }
        PreguntaPresentacionDTO preguntaActual = preguntasDelExamen.get(preguntaActualIndex);
        int preguntaExamenEstId = preguntaActual.getPreguntaExamenEstudianteId();
        Object respuestaParaGuardarLocalmente = null;
        String respuestaDadaTextoParaBD = null;
        Integer opcionIdSeleccionadaParaBD = null; // Para tipos de una sola opción

        String tipoNombre = preguntaActual.getTipoPreguntaNombre();

        if (TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS.equalsIgnoreCase(tipoNombre)) { // ID 1
            List<Integer> idsSeleccionados = new ArrayList<>();
            for(javafx.scene.Node node : contenedorOpcionesRespuesta.getChildren()){
                if(node instanceof CheckBox && ((CheckBox)node).isSelected()){
                    idsSeleccionados.add((Integer) node.getUserData());
                }
            }
            if (!idsSeleccionados.isEmpty()) {
                respuestaParaGuardarLocalmente = idsSeleccionados;
                // Para la BD, concatenamos los IDs en respuestaDadaTextoParaBD
                // El PL/SQL PROC_REGISTRAR_RESPUESTA y ES_PREGUNTA_CORRECTA necesitarán manejar esto.
                respuestaDadaTextoParaBD = idsSeleccionados.stream().map(String::valueOf).collect(Collectors.joining(","));
                opcionIdSeleccionadaParaBD = null; // No aplica un único ID de opción
            } else {
                respuestaParaGuardarLocalmente = null; // O una lista vacía si se prefiere
                respuestaDadaTextoParaBD = null;
            }
        } else if (TIPO_SELECCION_UNICA_UNA_CORRECTA.equalsIgnoreCase(tipoNombre) || TIPO_VERDADERO_FALSO.equalsIgnoreCase(tipoNombre)) { // ID 5 y ID 2
            ToggleGroup grupo = null;
            if (!contenedorOpcionesRespuesta.getChildren().isEmpty()) {
                for(javafx.scene.Node node : contenedorOpcionesRespuesta.getChildren()){
                    if (node instanceof RadioButton) {
                        grupo = ((RadioButton)node).getToggleGroup();
                        break;
                    }
                }
            }
            if (grupo != null) {
                RadioButton seleccionada = (RadioButton) grupo.getSelectedToggle();
                if (seleccionada != null) {
                    opcionIdSeleccionadaParaBD = (Integer) seleccionada.getUserData();
                    respuestaParaGuardarLocalmente = opcionIdSeleccionadaParaBD;
                    // respuestaDadaTextoParaBD podría ser seleccionada.getText() si el PL/SQL lo necesita
                }
            }
        } else if (TIPO_ORDENAR_CONCEPTOS.equalsIgnoreCase(tipoNombre)) {
            VBox conceptosBox = null;
            for(javafx.scene.Node node : contenedorOpcionesRespuesta.getChildren()){
                if(node.getStyleClass().contains("conceptos-ordenar-box") && node instanceof VBox){
                    conceptosBox = (VBox) node;
                    break;
                }
            }
            if (conceptosBox != null) {
                List<String> orderedConceptTexts = new ArrayList<>();
                for (javafx.scene.Node nodeLabel : conceptosBox.getChildren()) {
                    if (nodeLabel instanceof Label) {
                        orderedConceptTexts.add(((Label) nodeLabel).getText());
                    }
                }
                if (!orderedConceptTexts.isEmpty()) {
                    respuestaDadaTextoParaBD = String.join(":::", orderedConceptTexts);
                    respuestaParaGuardarLocalmente = respuestaDadaTextoParaBD;
                }
            }
        } else if (TIPO_RELACIONAR_CONCEPTOS.equalsIgnoreCase(tipoNombre)) {
            // TODO: Implementar la obtención de respuesta para relacionar conceptos
            respuestaDadaTextoParaBD = "Respuesta_Relacionar_Conceptos_Placeholder";
            respuestaParaGuardarLocalmente = respuestaDadaTextoParaBD;
        }

        // Guardar o eliminar respuesta
        if (respuestaParaGuardarLocalmente != null || (respuestaDadaTextoParaBD != null && !respuestaDadaTextoParaBD.isEmpty()) || opcionIdSeleccionadaParaBD != null ) {
            respuestasEstudiante.put(preguntaExamenEstId, respuestaParaGuardarLocalmente);
            try {
                examenRepository.registrarRespuestaEstudiante(preguntaExamenEstId, respuestaDadaTextoParaBD, opcionIdSeleccionadaParaBD);
                System.out.println("Respuesta guardada en BD para PEE ID: " + preguntaExamenEstId);
            } catch (SQLException e) {
                System.err.println("Error SQL al guardar respuesta en BD: " + e.getMessage());
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Guardado", "No se pudo guardar su respuesta. Verifique su conexión.");
            }
        } else { // Si no hay nada seleccionado/ingresado, y antes había algo, se borra
            if (respuestasEstudiante.containsKey(preguntaExamenEstId)) {
                respuestasEstudiante.remove(preguntaExamenEstId);
                try {
                    examenRepository.registrarRespuestaEstudiante(preguntaExamenEstId, null, null); // Enviar nulls para borrar
                    System.out.println("Respuesta eliminada en BD para PEE ID: " + preguntaExamenEstId);
                } catch (SQLException e) {
                    System.err.println("Error SQL al eliminar respuesta en BD: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleAnteriorPregunta(ActionEvent event) {
        guardarRespuestaActual();
        if (preguntaActualIndex > 0) {
            preguntaActualIndex--;
            mostrarPreguntaActual();
        }
    }

    @FXML
    private void handleSiguientePregunta(ActionEvent event) {
        guardarRespuestaActual();
        if (preguntasDelExamen != null && preguntaActualIndex < preguntasDelExamen.size() - 1) {
            preguntaActualIndex++;
            mostrarPreguntaActual();
        }
    }

    @FXML
    private void handleMarcarParaRevision(ActionEvent event) {
        if (preguntasDelExamen == null || preguntaActualIndex < 0 || preguntaActualIndex >= preguntasDelExamen.size()) {
            return;
        }
        int currentPeeId = preguntasDelExamen.get(preguntaActualIndex).getPreguntaExamenEstudianteId();
        boolean marcada = preguntasMarcadasRevision.getOrDefault(currentPeeId, false);
        preguntasMarcadasRevision.put(currentPeeId, !marcada);
        btnMarcarParaRevision.setText(!marcada ? "Desmarcar" : "Marcar");
    }

    private void actualizarEstadoBotonesNavegacion() {
        btnAnterior.setDisable(preguntaActualIndex <= 0);
        btnSiguiente.setDisable(preguntasDelExamen == null || preguntasDelExamen.isEmpty() || preguntaActualIndex >= preguntasDelExamen.size() - 1);
    }

    private void iniciarTemporizador(long segundosTotales) {
        this.tiempoRestanteSegundos = segundosTotales;
        actualizarLabelTiempo();
        if (segundosTotales > 0) {
            progressBarTiempo.setProgress(1.0);
            progressBarTiempo.setVisible(true);
        } else {
            progressBarTiempo.setVisible(false);
            lblTiempoRestante.setText("Tiempo: Ilimitado");
            return;
        }

        temporizadorExamen = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            tiempoRestanteSegundos--;
            actualizarLabelTiempo();
            progressBarTiempo.setProgress((double) tiempoRestanteSegundos / segundosTotales);
            if (tiempoRestanteSegundos <= 0) {
                if (temporizadorExamen != null) temporizadorExamen.stop();
                finalizarExamenPorTiempo();
            }
        }));
        temporizadorExamen.setCycleCount(Timeline.INDEFINITE);
        temporizadorExamen.play();
    }

    private void actualizarLabelTiempo() {
        if (this.infoExamen != null && this.infoExamen.getTiempo() != null && this.infoExamen.getTiempo() > 0) {
            long horas = tiempoRestanteSegundos / 3600;
            long minutos = (tiempoRestanteSegundos % 3600) / 60;
            long segundos = tiempoRestanteSegundos % 60;
            lblTiempoRestante.setText(String.format("Tiempo: %02d:%02d:%02d", horas, minutos, segundos));
        } else {
            lblTiempoRestante.setText("Tiempo: Ilimitado");
        }
    }

    private void finalizarExamenPorTiempo() {
        Platform.runLater(() -> {
            guardarRespuestaActual();
            mostrarAlerta(Alert.AlertType.WARNING, "Tiempo Terminado", "El tiempo para presentar el examen ha finalizado. Se enviarán tus respuestas.");
            finalizarExamenLogica();
        });
    }


    @FXML
    private void handleFinalizarExamen(ActionEvent event) {
        guardarRespuestaActual();

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Finalizar Examen");
        confirmacion.setHeaderText("¿Está seguro de que desea finalizar y enviar el examen?");
        confirmacion.setContentText("Una vez enviado, no podrá realizar cambios.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (temporizadorExamen != null) {
                temporizadorExamen.stop();
            }
            finalizarExamenLogica();
        }
    }

    private void finalizarExamenLogica() {
        if (temporizadorExamen != null) {
            temporizadorExamen.stop();
        }
        btnAnterior.setDisable(true);
        btnSiguiente.setDisable(true);
        btnMarcarParaRevision.setDisable(true);
        btnFinalizarExamen.setDisable(true);
        if (contenedorOpcionesRespuesta != null) {
            contenedorOpcionesRespuesta.setDisable(true);
        }

        try {
            ResultadoExamenDTO resultadoDTO = examenRepository.finalizarYCalificarExamen(this.idPresentacionExamen);
            navegarAResultados(resultadoDTO);
        } catch (SQLException e) {
            System.err.println("Error SQL al finalizar y calificar el examen: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo finalizar el examen. Contacte al administrador.");
        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla de resultados: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo mostrar la pantalla de resultados.");
        }
    }

    private void navegarAResultados(ResultadoExamenDTO resultadoDTO) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/estudiante/resultados_examen.fxml"));
        Parent rootResultados = loader.load();
        ResultadosExamenController resultadosController = loader.getController();
        if (this.estudianteLogueado == null) {
            System.err.println("Estudiante logueado es null al intentar navegar a resultados.");
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Usuario", "No se pudo identificar al usuario para mostrar los resultados.");
            return;
        }
        resultadosController.initData(estudianteLogueado, resultadoDTO);

        Stage stage = (Stage) rootPresentacionExamen.getScene().getWindow();
        Scene scene = new Scene(rootResultados);
        stage.setScene(scene);
        stage.setTitle("EduTec - Resultados del Examen");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Runnable alertTask = () -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        };

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(alertTask);
        } else {
            alertTask.run();
        }
    }
}


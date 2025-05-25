package org.uniquindio.ui.controller.estudiante;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat; // Se mantiene por si se usa en otro lado, pero no para el Label drag
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
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
import java.util.*;
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
    private Map<Integer, Object> respuestasEstudiante = new HashMap<>();
    private Map<Integer, Boolean> preguntasMarcadasRevision = new HashMap<>();

    private Timeline temporizadorExamen;
    private long tiempoRestanteSegundos;

    private ExamenRepositoryImpl examenRepository = new ExamenRepositoryImpl();

    private static final String TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS = "Selección múltiple";
    private static final String TIPO_VERDADERO_FALSO = "Verdadero/Falso";
    private static final String TIPO_ORDENAR_CONCEPTOS = "Ordenar conceptos";
    private static final String TIPO_RELACIONAR_CONCEPTOS = "Relacionar conceptos";
    private static final String TIPO_SELECCION_UNICA_UNA_CORRECTA = "Seleccion Unica";

    // Para Relacionar Conceptos:
    // No necesitamos un DataFormat especial si solo transferimos texto.
    // private static final DataFormat DRAGGED_LABEL_DATA_FORMAT = new DataFormat("application/x-java-dragged-label");
    private Map<Label, String> draggableConceptBMap = new HashMap<>();
    private Map<Label, String> targetConceptAMap = new HashMap<>();


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

        cargarPreguntasDelExamen();
        if (this.infoExamen != null && this.infoExamen.getTiempo() != null && this.infoExamen.getTiempo() > 0) {
            iniciarTemporizador((long) this.infoExamen.getTiempo() * 60);
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
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Examen", "No se pudieron cargar las preguntas para este examen. Por favor, contacte al administrador.");
                btnSiguiente.setDisable(true);
                btnAnterior.setDisable(true);
                btnFinalizarExamen.setDisable(true);
                btnMarcarParaRevision.setDisable(true);
                return;
            }
            preguntaActualIndex = 0;
            respuestasEstudiante.clear();
            preguntasMarcadasRevision.clear();
            mostrarPreguntaActual();
        } catch (SQLException e) {
            System.err.println("Error SQL al cargar preguntas del examen: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "Hubo un problema al cargar las preguntas del examen. Verifique su conexión e inténtelo de nuevo.");
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

        if (preguntaActual.getTiempoSugerido() != null && !preguntaActual.getTiempoSugerido().isEmpty() && !preguntaActual.getTiempoSugerido().trim().equals("0")) {
            lblTiempoSugeridoPregunta.setText("Tiempo sugerido para esta pregunta: " + preguntaActual.getTiempoSugerido() + " min");
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

    @SuppressWarnings("unchecked")
    private void renderizarOpcionesRespuesta(PreguntaPresentacionDTO pregunta) {
        contenedorOpcionesRespuesta.getChildren().clear();
        draggableConceptBMap.clear();
        targetConceptAMap.clear();
        String tipoNombre = pregunta.getTipoPreguntaNombre();
        Object respuestaGuardada = respuestasEstudiante.get(pregunta.getPreguntaExamenEstudianteId());

        if (TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS.equalsIgnoreCase(tipoNombre)) {
            if (pregunta.getOpciones() != null) {
                for (PreguntaPresentacionDTO.OpcionPresentacionDTO opcion : pregunta.getOpciones()) {
                    CheckBox cb = new CheckBox(opcion.getTextoOpcion());
                    cb.setUserData(opcion.getIdOpcion());
                    cb.setWrapText(true);
                    cb.getStyleClass().add("opcion-respuesta");
                    contenedorOpcionesRespuesta.getChildren().add(cb);
                    if (respuestaGuardada instanceof List) {
                        List<Integer> idsSeleccionados = (List<Integer>) respuestaGuardada;
                        if (idsSeleccionados.contains(opcion.getIdOpcion())) {
                            cb.setSelected(true);
                        }
                    }
                }
            }
        } else if (TIPO_SELECCION_UNICA_UNA_CORRECTA.equalsIgnoreCase(tipoNombre) || TIPO_VERDADERO_FALSO.equalsIgnoreCase(tipoNombre)) {
            ToggleGroup grupoOpciones = new ToggleGroup();
            if (pregunta.getOpciones() != null) {
                for (PreguntaPresentacionDTO.OpcionPresentacionDTO opcion : pregunta.getOpciones()) {
                    RadioButton rb = new RadioButton(opcion.getTextoOpcion());
                    rb.setUserData(opcion.getIdOpcion());
                    rb.setToggleGroup(grupoOpciones);
                    rb.setWrapText(true);
                    rb.getStyleClass().add("opcion-respuesta");
                    contenedorOpcionesRespuesta.getChildren().add(rb);
                    if (respuestaGuardada != null && respuestaGuardada.equals(opcion.getIdOpcion())) {
                        rb.setSelected(true);
                    }
                }
            }
        } else if (TIPO_ORDENAR_CONCEPTOS.equalsIgnoreCase(tipoNombre)) {
            Label infoOrdenar = new Label("Instrucciones: Arrastra y suelta los conceptos en el orden correcto de arriba hacia abajo.");
            infoOrdenar.getStyleClass().add("instruccion-pregunta");

            VBox conceptosBox = new VBox(8);
            conceptosBox.getStyleClass().add("conceptos-ordenar-box");
            conceptosBox.setAlignment(Pos.TOP_LEFT);
            conceptosBox.setPadding(new Insets(10));

            List<String> conceptosTextos = new ArrayList<>();
            if (pregunta.getOpciones() != null) {
                pregunta.getOpciones().forEach(op -> conceptosTextos.add(op.getTextoOpcion()));
            }

            if (respuestaGuardada instanceof String && !((String) respuestaGuardada).isEmpty()) {
                conceptosTextos.clear();
                conceptosTextos.addAll(Arrays.asList(((String) respuestaGuardada).split(":::")));
            } else if (!conceptosTextos.isEmpty()) {
                Collections.shuffle(conceptosTextos);
            }

            for (String textoConcepto : conceptosTextos) {
                Label lblConcepto = new Label(textoConcepto);
                lblConcepto.getStyleClass().add("concepto-ordenar-item");
                lblConcepto.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(lblConcepto, Priority.ALWAYS);
                configurarDragAndDropOrdenar(lblConcepto, conceptosBox);
                conceptosBox.getChildren().add(lblConcepto);
            }
            contenedorOpcionesRespuesta.getChildren().addAll(infoOrdenar, conceptosBox);

        } else if (TIPO_RELACIONAR_CONCEPTOS.equalsIgnoreCase(tipoNombre)) {
            Label infoRelacionar = new Label("Instrucciones: Arrastra un concepto de la columna derecha y suéltalo sobre su pareja correcta en la columna izquierda.");
            infoRelacionar.getStyleClass().add("instruccion-pregunta");

            HBox panelRelacionarPrincipal = new HBox(20);
            panelRelacionarPrincipal.getStyleClass().add("panel-relacionar-principal");
            panelRelacionarPrincipal.setAlignment(Pos.TOP_LEFT);

            VBox panelColumnaA = new VBox(10);
            panelColumnaA.getStyleClass().add("columna-relacionar-a");
            panelColumnaA.setPadding(new Insets(5));

            VBox panelColumnaB = new VBox(8);
            panelColumnaB.getStyleClass().add("columna-relacionar-b");
            panelColumnaB.setAlignment(Pos.TOP_CENTER);
            panelColumnaB.setPadding(new Insets(5));
            Label lblTituloColB = new Label("Conceptos a relacionar:");
            lblTituloColB.getStyleClass().add("subtitulo-columna");
            panelColumnaB.getChildren().add(lblTituloColB);

            List<String> conceptosA = new ArrayList<>();
            List<String> conceptosBOriginales = new ArrayList<>();
            Map<String, String> paresGuardados = new HashMap<>();

            if (pregunta.getOpciones() != null) {
                for (PreguntaPresentacionDTO.OpcionPresentacionDTO opcion : pregunta.getOpciones()) {
                    String[] partes = opcion.getTextoOpcion().split(":::", 2);
                    if (partes.length == 2) {
                        conceptosA.add(partes[0].trim());
                        conceptosBOriginales.add(partes[1].trim());
                    }
                }
            }

            if (respuestaGuardada instanceof String && !((String) respuestaGuardada).isEmpty()) {
                String[] paresStr = ((String) respuestaGuardada).split(";;;");
                for (String parStr : paresStr) {
                    String[] partesPar = parStr.split(":::", 2);
                    if (partesPar.length == 2) {
                        paresGuardados.put(partesPar[0].trim(), partesPar[1].trim());
                    }
                }
            }

            List<String> conceptosBParaArrastrar = new ArrayList<>(conceptosBOriginales);
            if (paresGuardados.isEmpty() && !conceptosBParaArrastrar.isEmpty()) {
                Collections.shuffle(conceptosBParaArrastrar);
            }

            for (String conceptoA : conceptosA) {
                HBox filaPar = new HBox(10);
                filaPar.setAlignment(Pos.CENTER_LEFT);
                filaPar.getStyleClass().add("fila-par-relacionar");

                Label lblConceptoA = new Label(conceptoA);
                lblConceptoA.getStyleClass().add("concepto-fijo-item");
                lblConceptoA.setMinWidth(Region.USE_PREF_SIZE);
                lblConceptoA.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(lblConceptoA, Priority.SOMETIMES);

                Label lblFlecha = new Label("↔");
                lblFlecha.getStyleClass().add("flecha-relacionar");

                Label lblDestinoConceptoB = new Label("[Arrastra aquí]");
                lblDestinoConceptoB.getStyleClass().add("concepto-destino-item");
                lblDestinoConceptoB.setMinWidth(150);
                lblDestinoConceptoB.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(lblDestinoConceptoB, Priority.ALWAYS);
                targetConceptAMap.put(lblDestinoConceptoB, conceptoA);

                String conceptoBGuardado = paresGuardados.get(conceptoA);
                if (conceptoBGuardado != null) {
                    lblDestinoConceptoB.setText(conceptoBGuardado);
                    lblDestinoConceptoB.getStyleClass().remove("concepto-destino-item");
                    lblDestinoConceptoB.getStyleClass().add("concepto-destino-item-lleno");
                    conceptosBParaArrastrar.remove(conceptoBGuardado);
                }
                configurarDropTargetRelacionar(lblDestinoConceptoB, panelColumnaB);

                filaPar.getChildren().addAll(lblConceptoA, lblFlecha, lblDestinoConceptoB);
                panelColumnaA.getChildren().add(filaPar);
            }

            for (String conceptoB : conceptosBParaArrastrar) {
                Label lblConceptoBDraggable = new Label(conceptoB);
                lblConceptoBDraggable.getStyleClass().add("concepto-relacionar-draggable");
                lblConceptoBDraggable.setMaxWidth(Double.MAX_VALUE);
                draggableConceptBMap.put(lblConceptoBDraggable, conceptoB);
                configurarDragSourceRelacionar(lblConceptoBDraggable);
                panelColumnaB.getChildren().add(lblConceptoBDraggable);
            }

            ScrollPane scrollPaneColumnaB = new ScrollPane(panelColumnaB);
            scrollPaneColumnaB.setFitToWidth(true);
            scrollPaneColumnaB.setPrefHeight(Math.max(150, conceptosA.size() * 45.0));
            scrollPaneColumnaB.setMinViewportWidth(200);

            HBox.setHgrow(panelColumnaA, Priority.ALWAYS);
            HBox.setHgrow(scrollPaneColumnaB, Priority.SOMETIMES);
            panelRelacionarPrincipal.getChildren().addAll(panelColumnaA, scrollPaneColumnaB);
            contenedorOpcionesRespuesta.getChildren().addAll(infoRelacionar, panelRelacionarPrincipal);

        } else {
            Label placeholder = new Label("Tipo de pregunta '" + tipoNombre + "' aún no implementado para renderizar en esta interfaz.");
            contenedorOpcionesRespuesta.getChildren().add(placeholder);
        }
    }

    private void configurarDragAndDropOrdenar(Label label, VBox parentContainer) {
        label.setOnDragDetected(event -> {
            if (label.getText().isEmpty()) { event.consume(); return; }
            Dragboard db = label.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(label.getText());
            db.setContent(content);
            db.setDragView(label.snapshot(null, null));
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
            if (db.hasString() && event.getGestureSource() instanceof Label) {
                Label sourceLabel = (Label) event.getGestureSource();
                if (parentContainer.getChildren().contains(sourceLabel)) {
                    double eventY = event.getY();
                    int newIndex = 0;
                    for (Node child : parentContainer.getChildren()) {
                        if (child == sourceLabel) continue;
                        if (eventY < child.getBoundsInParent().getMinY() + child.getBoundsInParent().getHeight() / 2) {
                            break;
                        }
                        newIndex++;
                    }
                    parentContainer.getChildren().remove(sourceLabel);
                    if (newIndex >= parentContainer.getChildren().size()) {
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
        for(Node node : parentContainer.getChildren()){
            if(node instanceof Label){
                Label targetItem = (Label) node;
                targetItem.setOnDragOver(event -> {
                    if (event.getGestureSource() != targetItem && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });
                targetItem.setOnDragEntered(event -> {
                    if (event.getGestureSource() != targetItem && event.getDragboard().hasString()) {
                        targetItem.getStyleClass().add("concepto-ordenar-item-dragover");
                    }
                });
                targetItem.setOnDragExited(event -> {
                    targetItem.getStyleClass().remove("concepto-ordenar-item-dragover");
                });
                targetItem.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString() && event.getGestureSource() instanceof Label) {
                        Label sourceLabel = (Label) event.getGestureSource();
                        if (parentContainer.getChildren().contains(sourceLabel) && sourceLabel != targetItem) {
                            int targetIndex = parentContainer.getChildren().indexOf(targetItem);
                            parentContainer.getChildren().remove(sourceLabel);
                            parentContainer.getChildren().add(targetIndex, sourceLabel);
                            success = true;
                        }
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });
            }
        }
    }

    private void configurarDragSourceRelacionar(Label sourceLabel) {
        sourceLabel.setOnDragDetected(event -> {
            if (sourceLabel.getText().isEmpty() || sourceLabel.isDisabled() || !sourceLabel.isVisible()) {
                event.consume(); return;
            }
            Dragboard db = sourceLabel.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // El texto original ya está en el Label, y lo recuperaremos del mapa si es necesario.
            // Lo importante es transferir el texto que el Label *muestra actualmente*.
            content.putString(sourceLabel.getText());
            db.setContent(content);
            db.setDragView(sourceLabel.snapshot(null, null));
            event.consume();
        });
    }

    private void configurarDropTargetRelacionar(Label targetDropLabel, VBox sourceConceptBContainer) {
        targetDropLabel.setOnDragOver(event -> {
            // Aceptar el drop solo si la fuente es un Label y el portapapeles tiene un String
            if (event.getGestureSource() instanceof Label && event.getDragboard().hasString()) {
                // Solo permitir soltar si el destino está vacío ("[Arrastra aquí]")
                if (targetDropLabel.getText().equals("[Arrastra aquí]") || targetDropLabel.getText().isEmpty()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
            event.consume();
        });

        targetDropLabel.setOnDragEntered(event -> {
            if (event.getGestureSource() instanceof Label && event.getDragboard().hasString()) {
                if (targetDropLabel.getText().equals("[Arrastra aquí]") || targetDropLabel.getText().isEmpty()) {
                    targetDropLabel.getStyleClass().add("concepto-destino-item-dragover");
                }
            }
        });

        targetDropLabel.setOnDragExited(event -> {
            targetDropLabel.getStyleClass().remove("concepto-destino-item-dragover");
        });

        targetDropLabel.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) { // Solo necesitamos verificar si hay un String
                String draggedConceptBText = db.getString(); // Texto del concepto B arrastrado

                // Encontrar el Label original en la columna B que corresponde a este texto y que está habilitado
                Label sourceLabelConceptBOriginal = null;
                for (Map.Entry<Label, String> entry : draggableConceptBMap.entrySet()) {
                    // Comparamos el texto del Dragboard con el valor en el mapa (texto original del Label)
                    // Y nos aseguramos que el Label fuente (entry.getKey()) no esté ya deshabilitado.
                    if (entry.getValue().equals(draggedConceptBText) && !entry.getKey().isDisabled()) {
                        sourceLabelConceptBOriginal = entry.getKey();
                        break;
                    }
                }

                // Si no encontramos un Label fuente válido (ya fue usado o no existe), no hacer nada.
                if (sourceLabelConceptBOriginal == null) {
                    event.setDropCompleted(false);
                    event.consume();
                    return;
                }

                // 1. Si el targetDropLabel (destino en Col A) ya tenía un concepto B:
                //    Devolver ese concepto B previo a la columna B (reactivarlo).
                if (!targetDropLabel.getText().equals("[Arrastra aquí]") && !targetDropLabel.getText().isEmpty()) {
                    String previousConceptBInTarget = targetDropLabel.getText();
                    Optional<Label> originalLabelToReactivateOpt = draggableConceptBMap.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(previousConceptBInTarget) && entry.getKey().isDisabled())
                            .map(Map.Entry::getKey)
                            .findFirst();

                    if(originalLabelToReactivateOpt.isPresent()){
                        Label labelToReactivate = originalLabelToReactivateOpt.get();
                        labelToReactivate.setDisable(false);
                        labelToReactivate.setVisible(true);
                        labelToReactivate.setManaged(true);
                        // No es necesario re-añadirlo a sourceConceptBContainer si solo se ocultó/deshabilitó.
                    }
                }

                // 2. Colocar el nuevo concepto B arrastrado en el targetDropLabel
                targetDropLabel.setText(draggedConceptBText);
                targetDropLabel.getStyleClass().remove("concepto-destino-item");
                targetDropLabel.getStyleClass().add("concepto-destino-item-lleno");

                // 3. Deshabilitar/ocultar el Label original del concepto B que se acaba de arrastrar
                sourceLabelConceptBOriginal.setDisable(true);
                sourceLabelConceptBOriginal.setVisible(false);
                sourceLabelConceptBOriginal.setManaged(false);

                success = true;
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
        Integer opcionIdSeleccionadaParaBD = null;

        String tipoNombre = preguntaActual.getTipoPreguntaNombre();

        if (TIPO_SELECCION_MULTIPLE_VARIAS_CORRECTAS.equalsIgnoreCase(tipoNombre)) {
            List<Integer> idsSeleccionados = new ArrayList<>();
            for (javafx.scene.Node node : contenedorOpcionesRespuesta.getChildren()) {
                if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                    idsSeleccionados.add((Integer) node.getUserData());
                }
            }
            if (!idsSeleccionados.isEmpty()) {
                respuestaParaGuardarLocalmente = idsSeleccionados;
                respuestaDadaTextoParaBD = idsSeleccionados.stream().map(String::valueOf).collect(Collectors.joining(","));
            } else {
                respuestaParaGuardarLocalmente = null;
                respuestaDadaTextoParaBD = null;
            }
        } else if (TIPO_SELECCION_UNICA_UNA_CORRECTA.equalsIgnoreCase(tipoNombre) || TIPO_VERDADERO_FALSO.equalsIgnoreCase(tipoNombre)) {
            ToggleGroup grupo = null;
            for(Node node : contenedorOpcionesRespuesta.getChildren()){
                if(node instanceof RadioButton){
                    grupo = ((RadioButton)node).getToggleGroup();
                    break;
                }
            }
            if (grupo != null) {
                RadioButton seleccionada = (RadioButton) grupo.getSelectedToggle();
                if (seleccionada != null) {
                    opcionIdSeleccionadaParaBD = (Integer) seleccionada.getUserData();
                    respuestaParaGuardarLocalmente = opcionIdSeleccionadaParaBD;
                }
            }
        } else if (TIPO_ORDENAR_CONCEPTOS.equalsIgnoreCase(tipoNombre)) {
            VBox conceptosBox = encontrarVBoxPorEstilo("conceptos-ordenar-box");
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
            HBox panelPrincipal = encontrarHBoxPorEstilo("panel-relacionar-principal");
            if (panelPrincipal != null && panelPrincipal.getChildren().size() > 0 && panelPrincipal.getChildren().get(0) instanceof VBox) {
                VBox panelColumnaA = (VBox) panelPrincipal.getChildren().get(0);
                List<String> paresFormados = new ArrayList<>();
                for (Node nodeFila : panelColumnaA.getChildren()) {
                    if (nodeFila instanceof HBox) {
                        HBox filaParHBox = (HBox) nodeFila;
                        Label lblConceptoA = null;
                        Label lblDestinoConceptoB = null;
                        for(Node nodeEnFila : filaParHBox.getChildren()){
                            if(nodeEnFila.getStyleClass().contains("concepto-fijo-item")){
                                lblConceptoA = (Label) nodeEnFila;
                            } else if (nodeEnFila.getStyleClass().contains("concepto-destino-item-lleno") ||
                                    nodeEnFila.getStyleClass().contains("concepto-destino-item")) {
                                lblDestinoConceptoB = (Label) nodeEnFila;
                            }
                        }

                        if (lblConceptoA != null && lblDestinoConceptoB != null) {
                            String textoConceptoA = lblConceptoA.getText();
                            String textoConceptoB = lblDestinoConceptoB.getText();
                            if (!textoConceptoB.equals("[Arrastra aquí]") && !textoConceptoB.isEmpty()) {
                                paresFormados.add(textoConceptoA + ":::" + textoConceptoB);
                            }
                        }
                    }
                }
                if (!paresFormados.isEmpty()) {
                    Collections.sort(paresFormados);
                    respuestaDadaTextoParaBD = String.join(";;;", paresFormados);
                    respuestaParaGuardarLocalmente = respuestaDadaTextoParaBD;
                }
            }
        }

        if (respuestaParaGuardarLocalmente != null || (respuestaDadaTextoParaBD != null && !respuestaDadaTextoParaBD.isEmpty()) || opcionIdSeleccionadaParaBD != null ) {
            respuestasEstudiante.put(preguntaExamenEstId, respuestaParaGuardarLocalmente);
            try {
                examenRepository.registrarRespuestaEstudiante(preguntaExamenEstId, respuestaDadaTextoParaBD, opcionIdSeleccionadaParaBD);
                System.out.println("Respuesta guardada en BD para PEE ID: " + preguntaExamenEstId + " -> TextoBD: " + respuestaDadaTextoParaBD + ", OpcionID: " + opcionIdSeleccionadaParaBD);
            } catch (SQLException e) {
                System.err.println("Error SQL al guardar respuesta en BD para PEE ID " + preguntaExamenEstId + ": " + e.getMessage());
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Guardado", "No se pudo guardar su respuesta. Verifique su conexión e inténtelo de nuevo.");
            }
        } else {
            if (respuestasEstudiante.containsKey(preguntaExamenEstId)) {
                respuestasEstudiante.remove(preguntaExamenEstId);
                try {
                    examenRepository.registrarRespuestaEstudiante(preguntaExamenEstId, null, null);
                    System.out.println("Respuesta eliminada en BD para PEE ID: " + preguntaExamenEstId);
                } catch (SQLException e) {
                    System.err.println("Error SQL al eliminar respuesta en BD para PEE ID " + preguntaExamenEstId + ": " + e.getMessage());
                }
            }
        }
    }

    private VBox encontrarVBoxPorEstilo(String styleClass) {
        for (javafx.scene.Node node : contenedorOpcionesRespuesta.getChildren()) {
            if (node.getStyleClass().contains(styleClass) && node instanceof VBox) {
                return (VBox) node;
            }
        }
        return null;
    }
    private HBox encontrarHBoxPorEstilo(String styleClass) {
        for (javafx.scene.Node node : contenedorOpcionesRespuesta.getChildren()) {
            if (node.getStyleClass().contains(styleClass) && node instanceof HBox) {
                return (HBox) node;
            }
        }
        return null;
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
            for(Node node : contenedorOpcionesRespuesta.getChildren()){
                node.setDisable(true);
            }
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

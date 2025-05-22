package org.uniquindio.ui.controller.estudiante;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.uniquindio.model.dto.PreguntaPresentacionDTO;
import org.uniquindio.model.dto.ResultadoExamenDTO;
import org.uniquindio.model.entity.evaluacion.Examen; // Para obtener el tiempo total del examen
import org.uniquindio.model.entity.usuario.Estudiante;
import org.uniquindio.repository.impl.ExamenRepositoryImpl; // Para interactuar con la BD

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

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
    @FXML private Button btnMarcarParaRevision; // Funcionalidad futura o simple marca visual
    @FXML private Button btnSiguiente;
    @FXML private Button btnFinalizarExamen;

    private Estudiante estudianteLogueado;
    private int idExamen; // ID del Examen que se está presentando
    private int idPresentacionExamen; // ID del intento actual (PresentacionExamen.id)
    private Examen infoExamen; // Para obtener el tiempo total

    private List<PreguntaPresentacionDTO> preguntasDelExamen;
    private int preguntaActualIndex = 0;
    private Map<Integer, Object> respuestasEstudiante = new HashMap<>(); // Key: preguntaExamenEstudianteId, Value: respuesta (String o Integer para ID de opción)
    private Map<Integer, Boolean> preguntasMarcadasRevision = new HashMap<>();


    private Timeline temporizadorExamen;
    private long tiempoRestanteSegundos;

    private ExamenRepositoryImpl examenRepository = new ExamenRepositoryImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // La inicialización de datos se hará a través de initData
    }

    /**
     * Inicializa los datos necesarios para comenzar la presentación del examen.
     * Este método debe ser llamado desde el controlador que carga esta vista.
     * @param estudiante El estudiante que presenta el examen.
     * @param examenId El ID del examen a presentar.
     * @param presentacionExamenId El ID del registro de PresentacionExamen (intento actual).
     * @param infoExamenGeneral El objeto Examen con datos generales como el tiempo total.
     */
    public void initData(Estudiante estudiante, int examenId, int presentacionExamenId, Examen infoExamenGeneral) {
        this.estudianteLogueado = estudiante;
        this.idExamen = examenId;
        this.idPresentacionExamen = presentacionExamenId;
        this.infoExamen = infoExamenGeneral;

        if (this.infoExamen != null && this.infoExamen.getDescripcion() != null) {
            lblTituloExamen.setText(this.infoExamen.getDescripcion()); // O el nombre si lo tienes
        } else {
            lblTituloExamen.setText("Examen en Curso");
        }

        cargarPreguntasDelExamen();
        if (this.infoExamen != null && this.infoExamen.getTiempo() != null && this.infoExamen.getTiempo() > 0) {
            iniciarTemporizador(this.infoExamen.getTiempo() * 60); // Convertir minutos a segundos
        } else {
            lblTiempoRestante.setText("Tiempo: Ilimitado");
            progressBarTiempo.setProgress(1.0); // O ocultarlo
        }
    }

    private void cargarPreguntasDelExamen() {
        try {
            // Llamar al repositorio para obtener las preguntas para esta presentación específica
            this.preguntasDelExamen = examenRepository.obtenerPreguntasParaPresentacion(this.idPresentacionExamen);

            if (this.preguntasDelExamen == null || this.preguntasDelExamen.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Examen", "No se pudieron cargar las preguntas para este examen.");
                // Considerar cerrar la ventana o volver al dashboard
                return;
            }
            preguntaActualIndex = 0;
            mostrarPreguntaActual();
            actualizarEstadoBotonesNavegacion();
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

        if (preguntaActual.getTiempoSugerido() != null && !preguntaActual.getTiempoSugerido().isEmpty()) {
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

        // Actualizar estado del botón de marcar para revisión
        btnMarcarParaRevision.setText(
                preguntasMarcadasRevision.getOrDefault(preguntaActual.getPreguntaExamenEstudianteId(), false) ?
                        "Desmarcar Pregunta" : "Marcar para Revisión"
        );
    }

    private void renderizarOpcionesRespuesta(PreguntaPresentacionDTO pregunta) {
        contenedorOpcionesRespuesta.getChildren().clear();
        // TODO: Implementar la lógica para renderizar diferentes tipos de pregunta
        // Basado en pregunta.getTipoPreguntaId() o pregunta.getTipoPreguntaNombre()
        // Ejemplo para Opción Múltiple (asumiendo tipo ID 1 es opción única, 2 es múltiple)
        // if (pregunta.getTipoPreguntaId() == 1) { // Opción Única
        //     ToggleGroup grupoOpciones = new ToggleGroup();
        //     for (PreguntaPresentacionDTO.OpcionPresentacionDTO opcion : pregunta.getOpciones()) {
        //         RadioButton rb = new RadioButton(opcion.getTextoOpcion());
        //         rb.setUserData(opcion.getIdOpcion()); // Guardar el ID de la opción
        //         rb.setToggleGroup(grupoOpciones);
        //         contenedorOpcionesRespuesta.getChildren().add(rb);
        //         // Restaurar selección si ya se respondió
        //         Object respuestaGuardada = respuestasEstudiante.get(pregunta.getPreguntaExamenEstudianteId());
        //         if (respuestaGuardada != null && respuestaGuardada.equals(opcion.getIdOpcion())) {
        //             rb.setSelected(true);
        //         }
        //     }
        // } else if (pregunta.getTipoPreguntaId() == 2) { // Opción Múltiple (varias respuestas)
        //     for (PreguntaPresentacionDTO.OpcionPresentacionDTO opcion : pregunta.getOpciones()) {
        //         CheckBox cb = new CheckBox(opcion.getTextoOpcion());
        //         cb.setUserData(opcion.getIdOpcion());
        //         contenedorOpcionesRespuesta.getChildren().add(cb);
        //         // Restaurar selección
        //         Object respuestaGuardada = respuestasEstudiante.get(pregunta.getPreguntaExamenEstudianteId());
        //         if (respuestaGuardada instanceof List && ((List<Integer>)respuestaGuardada).contains(opcion.getIdOpcion())) {
        //             cb.setSelected(true);
        //         }
        //     }
        // } else if ("Verdadero/Falso".equalsIgnoreCase(pregunta.getTipoPreguntaNombre())) {
        //     // ... Lógica para V/F ...
        // } else { // Pregunta abierta
        //     TextArea taRespuesta = new TextArea();
        //     taRespuesta.setPromptText("Escriba su respuesta...");
        //     taRespuesta.setPrefRowCount(5);
        //     Object respuestaGuardada = respuestasEstudiante.get(pregunta.getPreguntaExamenEstudianteId());
        //     if (respuestaGuardada instanceof String) {
        //         taRespuesta.setText((String) respuestaGuardada);
        //     }
        //     contenedorOpcionesRespuesta.getChildren().add(taRespuesta);
        // }
        // Placeholder
        Label placeholder = new Label("Renderizado de opciones/respuesta para tipo '" + pregunta.getTipoPreguntaNombre() + "' aún no implementado.");
        contenedorOpcionesRespuesta.getChildren().add(placeholder);
    }

    private void guardarRespuestaActual() {
        if (preguntasDelExamen == null || preguntaActualIndex < 0 || preguntaActualIndex >= preguntasDelExamen.size()) {
            return;
        }
        PreguntaPresentacionDTO preguntaActual = preguntasDelExamen.get(preguntaActualIndex);
        int preguntaExamenEstId = preguntaActual.getPreguntaExamenEstudianteId();
        Object respuesta = null;
        String respuestaDadaTexto = null;
        Integer opcionIdSeleccionada = null;

        // TODO: Implementar lógica para obtener la respuesta según el tipo de pregunta renderizado
        // Ejemplo para Opción Única (RadioButton)
        // Node primerNodo = contenedorOpcionesRespuesta.getChildren().isEmpty() ? null : contenedorOpcionesRespuesta.getChildren().get(0);
        // if (primerNodo instanceof RadioButton) { // Asumiendo que todas son RadioButton para este tipo
        //     ToggleGroup grupo = ((RadioButton) primerNodo).getToggleGroup();
        //     RadioButton seleccionada = (RadioButton) grupo.getSelectedToggle();
        //     if (seleccionada != null) {
        //         respuesta = seleccionada.getUserData(); // ID de la opción
        //         opcionIdSeleccionada = (Integer) respuesta;
        //     }
        // } else if (primerNodo instanceof TextArea) { // Pregunta abierta
        //     respuesta = ((TextArea) primerNodo).getText();
        //     respuestaDadaTexto = (String) respuesta;
        // } else if (primerNodo instanceof CheckBox) { // Opción Múltiple (varias respuestas)
        //     List<Integer> idsSeleccionados = new ArrayList<>();
        //     for(Node nodoOpcion : contenedorOpcionesRespuesta.getChildren()){
        //         if(nodoOpcion instanceof CheckBox && ((CheckBox)nodoOpcion).isSelected()){
        //             idsSeleccionados.add((Integer) nodoOpcion.getUserData());
        //         }
        //     }
        //     respuesta = idsSeleccionados; // Guardar lista de IDs
        //     // Para registrar en BD, podrías necesitar enviar esto de forma especial
        //     // o tener un PROC_REGISTRAR_RESPUESTA_MULTIPLE
        // }


        if (respuesta != null) {
            respuestasEstudiante.put(preguntaExamenEstId, respuesta);
            try {
                // El PL/SQL PROC_REGISTRAR_RESPUESTA debe manejar si es texto o ID de opción
                examenRepository.registrarRespuestaEstudiante(preguntaExamenEstId, respuestaDadaTexto, opcionIdSeleccionada);
                System.out.println("Respuesta guardada para PEE ID: " + preguntaExamenEstId);
            } catch (SQLException e) {
                System.err.println("Error SQL al guardar respuesta: " + e.getMessage());
                // Considerar reintentar o notificar al usuario
            }
        } else {
            // Si no se seleccionó nada, se podría eliminar una respuesta previa o no hacer nada
            // respuestasEstudiante.remove(preguntaExamenEstId);
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
        if (preguntaActualIndex < preguntasDelExamen.size() - 1) {
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
        btnMarcarParaRevision.setText(!marcada ? "Desmarcar Pregunta" : "Marcar para Revisión");
        // TODO: Podrías añadir una indicación visual en una lista de preguntas si la tuvieras.
    }


    private void actualizarEstadoBotonesNavegacion() {
        btnAnterior.setDisable(preguntaActualIndex <= 0);
        btnSiguiente.setDisable(preguntasDelExamen == null || preguntaActualIndex >= preguntasDelExamen.size() - 1);
    }

    private void iniciarTemporizador(long segundosTotales) {
        this.tiempoRestanteSegundos = segundosTotales;
        actualizarLabelTiempo();
        progressBarTiempo.setProgress(1.0);

        temporizadorExamen = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            tiempoRestanteSegundos--;
            actualizarLabelTiempo();
            progressBarTiempo.setProgress((double) tiempoRestanteSegundos / segundosTotales);
            if (tiempoRestanteSegundos <= 0) {
                temporizadorExamen.stop();
                finalizarExamenPorTiempo();
            }
        }));
        temporizadorExamen.setCycleCount(Timeline.INDEFINITE);
        temporizadorExamen.play();
    }

    private void actualizarLabelTiempo() {
        long horas = tiempoRestanteSegundos / 3600;
        long minutos = (tiempoRestanteSegundos % 3600) / 60;
        long segundos = tiempoRestanteSegundos % 60;
        lblTiempoRestante.setText(String.format("Tiempo: %02d:%02d:%02d", horas, minutos, segundos));
    }

    private void finalizarExamenPorTiempo() {
        mostrarAlerta(Alert.AlertType.WARNING, "Tiempo Terminado", "El tiempo para presentar el examen ha finalizado. Se enviarán tus respuestas.");
        finalizarExamenLogica();
    }

    @FXML
    private void handleFinalizarExamen(ActionEvent event) {
        guardarRespuestaActual(); // Guardar la respuesta de la última pregunta vista

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
        try {
            // TODO: Asegurarse que todas las respuestas no guardadas se envíen si hay un mecanismo de guardado en segundo plano.
            // Por ahora, `guardarRespuestaActual` se llama al navegar o finalizar.

            ResultadoExamenDTO resultadoDTO = examenRepository.finalizarYCalificarExamen(this.idPresentacionExamen);

            // Navegar a la pantalla de resultados
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/estudiante/resultados_examen.fxml"));
            Parent rootResultados = loader.load();

            ResultadosExamenController resultadosController = loader.getController();
            resultadosController.initData(estudianteLogueado, resultadoDTO); // Pasar el DTO de resultado

            Stage stage = (Stage) rootPresentacionExamen.getScene().getWindow();
            Scene scene = new Scene(rootResultados);
            stage.setScene(scene);
            stage.setTitle("EduTec - Resultados del Examen");
            // stage.setMaximized(true); // Opcional

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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        // Asegurarse que la alerta se muestre en el hilo de la UI si se llama desde otro hilo (ej. temporizador)
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(alert::showAndWait);
        } else {
            alert.showAndWait();
        }
    }
}
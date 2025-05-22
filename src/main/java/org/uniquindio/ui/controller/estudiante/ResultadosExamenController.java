package org.uniquindio.ui.controller.estudiante;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.uniquindio.model.dto.DetalleRespuestaPreguntaDTO;
import org.uniquindio.model.dto.ResultadoExamenDTO;
import org.uniquindio.model.entity.usuario.Estudiante;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.text.DecimalFormat;


public class ResultadosExamenController implements Initializable {

    @FXML private Label lblTituloVistaResultados;
    @FXML private Label lblNombreExamenResultado;
    @FXML private Label lblFechaPresentacionResultado;
    @FXML private Label lblCalificacionFinal;
    @FXML private Label lblRespuestasCorrectas;
    @FXML private Label lblRespuestasIncorrectas;
    @FXML private Label lblPreguntasRespondidas;
    @FXML private Label lblMensajeFeedbackGeneralResultado;
    @FXML private TableView<DetalleRespuestaPreguntaDTO> tablaDetalleRespuestas;
    @FXML private TableColumn<DetalleRespuestaPreguntaDTO, Integer> colNumeroPreguntaDetalle;
    @FXML private TableColumn<DetalleRespuestaPreguntaDTO, String> colTextoPreguntaDetalle;
    @FXML private TableColumn<DetalleRespuestaPreguntaDTO, String> colTuRespuestaDetalle;
    @FXML private TableColumn<DetalleRespuestaPreguntaDTO, String> colRespuestaCorrectaDetalle;
    @FXML private TableColumn<DetalleRespuestaPreguntaDTO, Label> colEstadoRespuestaDetalle; // Usar Label para estilo
    @FXML private Button btnVolverAlDashboard;

    private Estudiante estudianteLogueado;
    private ResultadoExamenDTO resultadoExamen;
    private DashboardEstudianteController dashboardEstudianteController; // Para volver

    private static final DecimalFormat df = new DecimalFormat("0.00");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaDetalles();
    }

    /**
     * Inicializa los datos con la información del estudiante y los resultados del examen.
     * @param estudiante El estudiante que presentó el examen.
     * @param resultado El DTO con los resultados del examen.
     */
    public void initData(Estudiante estudiante, ResultadoExamenDTO resultado) {
        this.estudianteLogueado = estudiante;
        this.resultadoExamen = resultado;
        poblarDatosResumen();
        poblarTablaDetalles();
    }

    /**
     * Opcional: Método para pasar la referencia del DashboardEstudianteController si se necesita
     * para una navegación más compleja o para actualizar el dashboard.
     * @param dashboardController La instancia del controlador del dashboard.
     */
    public void setDashboardController(DashboardEstudianteController dashboardController) {
        this.dashboardEstudianteController = dashboardController;
    }


    private void poblarDatosResumen() {
        if (resultadoExamen == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos", "No se recibieron los resultados del examen.");
            return;
        }
        lblNombreExamenResultado.setText(resultadoExamen.getNombreExamen() != null ? resultadoExamen.getNombreExamen() : "Examen sin nombre");
        lblFechaPresentacionResultado.setText("Presentado el: " + (resultadoExamen.getFechaPresentacion() != null ? resultadoExamen.getFechaPresentacion() : "Fecha no disponible"));

        if (resultadoExamen.getCalificacionFinal() != null) {
            // Asumiendo que la calificación es sobre 5.0 y el DTO la da en esa escala.
            // Si el DTO da un porcentaje (0-100), necesitarías convertirla.
            // Por ahora, asumimos que ya está en la escala final.
            lblCalificacionFinal.setText(df.format(resultadoExamen.getCalificacionFinal().setScale(2, RoundingMode.HALF_UP)) + " / 5.0"); // Ajusta la escala máxima si es diferente
        } else {
            lblCalificacionFinal.setText("N/A");
        }

        lblRespuestasCorrectas.setText(String.valueOf(resultadoExamen.getRespuestasCorrectas()));
        lblRespuestasIncorrectas.setText(String.valueOf(resultadoExamen.getRespuestasIncorrectas()));
        lblPreguntasRespondidas.setText(resultadoExamen.getPreguntasRespondidas() + " de " + resultadoExamen.getTotalPreguntasExamen());
        lblMensajeFeedbackGeneralResultado.setText(resultadoExamen.getMensajeFeedbackGeneral() != null ? resultadoExamen.getMensajeFeedbackGeneral() : "No hay retroalimentación general disponible.");
    }

    private void configurarTablaDetalles() {
        // Usar un contador para el número de pregunta en la tabla
        colNumeroPreguntaDetalle.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(tablaDetalleRespuestas.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        colTextoPreguntaDetalle.setCellValueFactory(new PropertyValueFactory<>("textoPregunta"));
        colTuRespuestaDetalle.setCellValueFactory(new PropertyValueFactory<>("respuestaEstudiante"));

        // Para respuestas correctas, si es una lista, la unimos.
        colRespuestaCorrectaDetalle.setCellValueFactory(cellData -> {
            List<String> correctas = cellData.getValue().getOpcionesCorrectasTexto();
            if (correctas == null || correctas.isEmpty()) {
                return new SimpleStringProperty("N/A");
            }
            return new SimpleStringProperty(String.join(", ", correctas));
        });

        colEstadoRespuestaDetalle.setCellValueFactory(cellData -> {
            Label lblEstado = new Label();
            if (cellData.getValue().isEsCorrectaLaRespuesta()) {
                lblEstado.setText("Correcta");
                lblEstado.getStyleClass().add("estado-correcto");
            } else {
                lblEstado.setText("Incorrecta");
                lblEstado.getStyleClass().add("estado-incorrecto");
            }
            return new SimpleObjectProperty<>(lblEstado);
        });
    }

    private void poblarTablaDetalles() {
        if (resultadoExamen != null && resultadoExamen.getDetalleRespuestas() != null) {
            tablaDetalleRespuestas.setItems(FXCollections.observableArrayList(resultadoExamen.getDetalleRespuestas()));
        } else {
            tablaDetalleRespuestas.setPlaceholder(new Label("No hay detalles de respuestas disponibles."));
        }
    }

    @FXML
    private void handleVolverAlDashboard(ActionEvent event) {
        try {
            // Obtener el Stage actual
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Cargar la pantalla del dashboard del estudiante
            // Asumimos que DashboardEstudianteController ya tiene un FXML asociado
            // y que el LoginController lo carga y le pasa el estudiante.
            // Aquí, simplemente volvemos a cargar el dashboard.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/estudiante/dashboard_estudiante.fxml"));
            Parent rootDashboard = loader.load();

            // Si necesitas reinicializar el dashboard con el estudiante:
            DashboardEstudianteController dashboardCtrl = loader.getController();
            if (this.estudianteLogueado != null) {
                dashboardCtrl.setEstudiante(this.estudianteLogueado);
            } else {
                // Caso raro, pero si no hay estudiante, podría redirigir al login.
                // Por ahora, asumimos que siempre habrá un estudiante logueado aquí.
                System.err.println("Advertencia: Estudiante no disponible al volver al dashboard desde resultados.");
            }


            Scene scene = new Scene(rootDashboard);
            stageActual.setScene(scene);
            stageActual.setTitle("EduTec - Dashboard Estudiante");
            // stageActual.setMaximized(true); // Opcional

        } catch (IOException e) {
            System.err.println("Error al cargar el dashboard del estudiante: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo volver al panel principal.");
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
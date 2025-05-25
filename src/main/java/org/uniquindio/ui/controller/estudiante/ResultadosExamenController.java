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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import org.uniquindio.model.dto.DetalleRespuestaPreguntaDTO;
import org.uniquindio.model.dto.ResultadoExamenDTO;
import org.uniquindio.model.entity.usuario.Estudiante;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;
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

    @FXML private TreeTableView<DetalleRespuestaPreguntaDTO> treeTablaDetalleRespuestas;
    @FXML private TreeTableColumn<DetalleRespuestaPreguntaDTO, String> colNumeroPreguntaDetalle; // Cambiado a String para jerarquía "1", "1.1"
    @FXML private TreeTableColumn<DetalleRespuestaPreguntaDTO, String> colTextoPreguntaDetalle;
    @FXML private TreeTableColumn<DetalleRespuestaPreguntaDTO, String> colTuRespuestaDetalle;
    @FXML private TreeTableColumn<DetalleRespuestaPreguntaDTO, String> colRespuestaCorrectaDetalle;
    @FXML private TreeTableColumn<DetalleRespuestaPreguntaDTO, Label> colEstadoRespuestaDetalle;

    @FXML private Button btnVolverAlDashboard;

    private Estudiante estudianteLogueado;
    private ResultadoExamenDTO resultadoExamen;
    private DashboardEstudianteController dashboardEstudianteController;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTreeTablaDetalles();
    }

    public void initData(Estudiante estudiante, ResultadoExamenDTO resultado) {
        this.estudianteLogueado = estudiante;
        this.resultadoExamen = resultado;
        poblarDatosResumen();
        poblarTreeTablaDetalles();
    }

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
            lblCalificacionFinal.setText(df.format(resultadoExamen.getCalificacionFinal().setScale(2, RoundingMode.HALF_UP)) + " / 5.0");
        } else {
            lblCalificacionFinal.setText("N/A");
        }

        lblRespuestasCorrectas.setText(String.valueOf(resultadoExamen.getRespuestasCorrectas()));
        lblRespuestasIncorrectas.setText(String.valueOf(resultadoExamen.getRespuestasIncorrectas()));
        lblPreguntasRespondidas.setText(resultadoExamen.getPreguntasRespondidas() + " de " + resultadoExamen.getTotalPreguntasExamen());
        lblMensajeFeedbackGeneralResultado.setText(resultadoExamen.getMensajeFeedbackGeneral() != null ? resultadoExamen.getMensajeFeedbackGeneral() : "No hay retroalimentación general disponible.");
    }

    private void configurarTreeTablaDetalles() {
        colNumeroPreguntaDetalle.setCellValueFactory(cellData -> {
            // Se necesitará lógica para generar "1", "1.1", "1.2", "2", etc.
            // Esto se manejará al construir los TreeItems. Por ahora, un placeholder.
            // El TreeItem<DetalleRespuestaPreguntaDTO> podría tener un campo extra para el número formateado.
            // O se puede calcular aquí basado en el nivel del TreeItem.
            TreeItem<DetalleRespuestaPreguntaDTO> treeItem = cellData.getValue();
            if (treeItem != null && treeItem.getValue() != null) {
                // Lógica para numeración jerárquica (simplificada aquí)
                String numero = "";
                TreeItem<DetalleRespuestaPreguntaDTO> current = treeItem;
                List<String> parts = new ArrayList<>();
                while (current != null && current.getParent() != null && current.getParent() != treeTablaDetalleRespuestas.getRoot()) {
                    parts.add(String.valueOf(current.getParent().getChildren().indexOf(current) + 1));
                    current = current.getParent();
                }
                if (treeTablaDetalleRespuestas.getRoot() != null && treeTablaDetalleRespuestas.getRoot().getChildren().contains(treeItem.getParent() != null ? treeItem.getParent() : treeItem)) {
                    parts.add(String.valueOf(treeTablaDetalleRespuestas.getRoot().getChildren().indexOf(treeItem.getParent() != null ? treeItem.getParent() : treeItem) +1));
                }


                Collections.reverse(parts);
                numero = String.join(".", parts);
                if (parts.isEmpty() && treeTablaDetalleRespuestas.getRoot() != null && treeTablaDetalleRespuestas.getRoot().getChildren().contains(treeItem)) { // Pregunta principal
                    numero = String.valueOf(treeTablaDetalleRespuestas.getRoot().getChildren().indexOf(treeItem) + 1);
                }


                return new SimpleStringProperty(numero);
            }
            return new SimpleStringProperty("");
        });

        colTextoPreguntaDetalle.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getTextoPregunta())
        );
        colTuRespuestaDetalle.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getRespuestaEstudiante())
        );
        colRespuestaCorrectaDetalle.setCellValueFactory(cellData -> {
            List<String> correctas = cellData.getValue().getValue().getOpcionesCorrectasTexto();
            return new SimpleStringProperty((correctas == null || correctas.isEmpty()) ? "N/A" : String.join(", ", correctas));
        });
        colEstadoRespuestaDetalle.setCellValueFactory(cellData -> {
            Label lblEstado = new Label();
            if (cellData.getValue().getValue().isEsCorrectaLaRespuesta()) {
                lblEstado.setText("Correcta");
                lblEstado.getStyleClass().add("estado-correcto");
            } else {
                lblEstado.setText("Incorrecta");
                lblEstado.getStyleClass().add("estado-incorrecto");
            }
            return new SimpleObjectProperty<>(lblEstado);
        });
    }

    private void poblarTreeTablaDetalles() {
        if (resultadoExamen == null || resultadoExamen.getDetalleRespuestas() == null) {
            //treeTablaDetalleRespuestas.setPlaceholder(new Label("No hay detalles de respuestas disponibles."));
            TreeItem<DetalleRespuestaPreguntaDTO> root = new TreeItem<>();
            treeTablaDetalleRespuestas.setRoot(root);
            treeTablaDetalleRespuestas.setShowRoot(false);
            return;
        }

        List<DetalleRespuestaPreguntaDTO> todosLosDetalles = resultadoExamen.getDetalleRespuestas();
        TreeItem<DetalleRespuestaPreguntaDTO> rootItem = new TreeItem<>(); // Raíz invisible

        Map<Integer, TreeItem<DetalleRespuestaPreguntaDTO>> mapIdPreguntaOriginalToTreeItem = new HashMap<>();

        // Primera pasada: añadir todas las preguntas principales
        for (DetalleRespuestaPreguntaDTO detalle : todosLosDetalles) {
            if (detalle.getIdPreguntaPadreOriginal() == null || detalle.getIdPreguntaPadreOriginal() == 0) {
                TreeItem<DetalleRespuestaPreguntaDTO> principalItem = new TreeItem<>(detalle);
                rootItem.getChildren().add(principalItem);
                mapIdPreguntaOriginalToTreeItem.put(detalle.getIdPreguntaOriginalPropia(), principalItem); // Usar el ID original como clave
            }
        }

        if (!mapIdPreguntaOriginalToTreeItem.isEmpty()) { // Solo si pudimos mapear padres
            for (DetalleRespuestaPreguntaDTO detalle : todosLosDetalles) {
                if (detalle.getIdPreguntaPadreOriginal() != null && detalle.getIdPreguntaPadreOriginal() > 0) {
                    TreeItem<DetalleRespuestaPreguntaDTO> parentTreeItem = mapIdPreguntaOriginalToTreeItem.get(detalle.getIdPreguntaPadreOriginal());
                    if (parentTreeItem != null) {
                        parentTreeItem.getChildren().add(new TreeItem<>(detalle));
                    } else {
                        // Subpregunta huérfana: añadir a la raíz o manejar como error
                        System.err.println("Subpregunta huérfana PEE_ID: " + detalle.getPreguntaExamenEstudianteId() + " con padre ID: " + detalle.getIdPreguntaPadreOriginal() + " no encontrado en el mapa.");
                        rootItem.getChildren().add(new TreeItem<>(detalle)); // Fallback
                    }
                }
            }
        } else if (!todosLosDetalles.isEmpty()) { // Si no se pudo construir jerarquía, mostrar lista plana
            todosLosDetalles.forEach(detalle -> rootItem.getChildren().add(new TreeItem<>(detalle)));
            mostrarAlerta(Alert.AlertType.WARNING, "Visualización Plana", "No se pudo construir la jerarquía de preguntas. Mostrando lista plana.");
        }


        treeTablaDetalleRespuestas.setRoot(rootItem);
        treeTablaDetalleRespuestas.setShowRoot(false); // No mostrar el nodo raíz invisible
        // Expandir todos los nodos principales por defecto
        for (TreeItem<DetalleRespuestaPreguntaDTO> item : rootItem.getChildren()) {
            item.setExpanded(true);
        }
    }


    @FXML
    private void handleVolverAlDashboard(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/estudiante/dashboard_estudiante.fxml"));
            Parent rootDashboard = loader.load();
            DashboardEstudianteController dashboardCtrl = loader.getController();
            if (this.estudianteLogueado != null) {
                dashboardCtrl.setEstudiante(this.estudianteLogueado);
            } else {
                System.err.println("Advertencia: Estudiante no disponible al volver al dashboard desde resultados.");
            }
            Scene scene = new Scene(rootDashboard);
            stageActual.setScene(scene);
            stageActual.setTitle("EduTec - Dashboard Estudiante");
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

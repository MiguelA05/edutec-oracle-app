package org.uniquindio.ui.controller.profesor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.ui.controller.comun.LoginController; // Para volver al login

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardProfesorController implements Initializable {

    @FXML
    private VBox panelNavegacionProfesor;
    @FXML
    private Label lblNombreProfesor;
    @FXML
    private Label lblCorreoProfesor;
    @FXML
    private Button btnInicioProfesor;
    @FXML
    private Button btnGestionCursos;
    @FXML
    private Button btnGestionExamenes;
    @FXML
    private Button btnBancoPreguntas;
    @FXML
    private Button btnGestionPreguntas;
    @FXML
    private Button btnEstadisticas;
    @FXML
    private Button btnHorariosProfesor;
    @FXML
    private Button btnCerrarSesionProfesor;
    @FXML
    private AnchorPane panelContenidoProfesor;

    private Profesor profesorLogueado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cargar una vista de bienvenida o resumen por defecto
        handleInicioProfesor(null);
    }

    /**
     * Método para recibir el objeto Profesor desde el LoginController.
     * @param profesor El profesor que ha iniciado sesión.
     */
    public void setProfesor(Profesor profesor) {
        this.profesorLogueado = profesor;
        if (profesor != null) {
            lblNombreProfesor.setText(profesor.getNombre() != null ? profesor.getNombre() : "Nombre no disponible");
            lblCorreoProfesor.setText(profesor.getCorreo() != null ? profesor.getCorreo() : "Correo no disponible");
        } else {
            lblNombreProfesor.setText("Profesor Desconocido");
            lblCorreoProfesor.setText("");
        }
        // Podrías recargar la vista de inicio si depende de los datos del profesor
        // handleInicioProfesor(null);
    }

    @FXML
    private void handleInicioProfesor(ActionEvent event) {
        // Cargar una vista de bienvenida o resumen para el profesor
        // Debes crear 'inicio_profesor_content.fxml' o la vista que desees mostrar aquí.
        String fxmlPath = "/fxml/profesor/inicio_profesor_content.fxml";
        cargarVista(fxmlPath, "Inicio Profesor");
    }

    @FXML
    void handleGestionCursos(ActionEvent event) {
        cargarVista("/fxml/profesor/gestion_cursos_profesor.fxml", "Gestión de Cursos");
    }

    @FXML
    void handleGestionExamenes(ActionEvent event) {
        cargarVista("/fxml/profesor/crear_editar_examen.fxml", "Gestión de Exámenes");
    }

    @FXML
    void handleBancoPreguntas(ActionEvent event) {
        cargarVista("/fxml/profesor/banco_preguntas.fxml", "Banco de Preguntas");
    }

    @FXML
    void handleGestionPreguntas(ActionEvent event) {
        cargarVista("/fxml/profesor/gestion_preguntas.fxml", "Gestión de Preguntas");
    }

    @FXML
    void handleEstadisticas(ActionEvent event) {
        cargarVista("/fxml/profesor/estadisticas_profesor.fxml", "Estadísticas y Reportes");
    }

    @FXML
    void handleHorariosProfesor(ActionEvent event) {
        cargarVista("/fxml/profesor/horarios_profesor.fxml", "Mis Horarios");
    }


    private void cargarVista(String fxmlPath, String tituloSeccion) {
        try {
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("Error: No se pudo encontrar el archivo FXML: " + fxmlPath);
                Label lblError = new Label("Error: No se pudo encontrar la vista '" + tituloSeccion + "'.\nVerifique que el archivo " + fxmlPath.substring(fxmlPath.lastIndexOf("/") + 1) + " exista.");
                lblError.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-wrap-text: true;");
                panelContenidoProfesor.getChildren().setAll(lblError);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Node vista = loader.load();

            Object controller = loader.getController();
            if (profesorLogueado != null && controller != null) {
                // Intentar pasar el objeto Profesor al controlador de la sub-vista
                try {
                    java.lang.reflect.Method initMethod = null;
                    try {
                        initMethod = controller.getClass().getMethod("setProfesor", Profesor.class);
                    } catch (NoSuchMethodException nsme1) {
                        try {
                            initMethod = controller.getClass().getMethod("initDataProfesor", Profesor.class);
                        } catch (NoSuchMethodException nsme2) {
                            System.out.println("Nota: El controlador para " + fxmlPath + " no tiene un método setProfesor(Profesor) o initDataProfesor(Profesor).");
                        }
                    }
                    if (initMethod != null) {
                        initMethod.invoke(controller, profesorLogueado);
                    }
                } catch (Exception e) {
                    System.err.println("Error al intentar pasar datos de profesor a " + fxmlPath + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            panelContenidoProfesor.getChildren().setAll(vista);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
            Label lblError = new Label("No se pudo cargar la sección: " + tituloSeccion);
            lblError.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            panelContenidoProfesor.getChildren().setAll(lblError);
        }
    }

    @FXML
    private void handleCerrarSesionProfesor(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            URL loginFxmlUrl = LoginController.class.getResource("/fxml/comun/login.fxml");
            if (loginFxmlUrl == null) {
                System.err.println("Error crítico: No se pudo encontrar el archivo FXML de login.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(loginFxmlUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            stageActual.setScene(scene);
            stageActual.setTitle("EduTec - Iniciar Sesión");
            stageActual.setMaximized(false);
            stageActual.sizeToScene();
            stageActual.centerOnScreen();
            System.out.println("Sesión de profesor cerrada. Volviendo a la pantalla de login.");

        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla de login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package org.uniquindio.ui.controller.estudiante;

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
import org.uniquindio.model.entity.usuario.Estudiante;
import org.uniquindio.ui.controller.comun.LoginController; // Para volver al login

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardEstudianteController implements Initializable {

    @FXML
    private VBox panelNavegacion;
    @FXML
    private Label lblNombreEstudiante;
    @FXML
    private Label lblCorreoEstudiante;
    @FXML
    private Button btnInicio;
    @FXML
    private Button btnMisCursos;
    @FXML
    private Button btnExamenesDisponibles;
    @FXML
    private Button btnMisResultados;
    @FXML
    private Button btnMisHorarios;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private AnchorPane panelContenido;

    private Estudiante estudianteLogueado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cargar la vista de inicio por defecto
        handleInicio(null);
    }

    /**
     * Método para recibir el objeto Estudiante desde el LoginController.
     * Este método debe ser llamado después de cargar el FXML del dashboard.
     * @param estudiante El estudiante que ha iniciado sesión.
     */
    public void setEstudiante(Estudiante estudiante) {
        this.estudianteLogueado = estudiante;
        if (estudiante != null) {
            lblNombreEstudiante.setText(estudiante.getNombre());
            lblCorreoEstudiante.setText(estudiante.getCorreo());
            // Aquí podrías cargar datos iniciales o personalizar más la UI
        } else {
            lblNombreEstudiante.setText("Estudiante Desconocido");
            lblCorreoEstudiante.setText("");
            // Manejar el caso de estudiante nulo si es necesario (ej. redirigir a login)
        }
    }

    @FXML
    private void handleInicio(ActionEvent event) {
        // Cargar una vista de bienvenida o resumen en el panelContenido
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/estudiante/inicio_estudiante_content.fxml")); // Debes crear este FXML
            Node vistaInicio = loader.load();
            // Si el controlador de inicio_estudiante_content.fxml necesita datos del estudiante:
            // Object controller = loader.getController();
            // if (controller instanceof InicioEstudianteContentController && estudianteLogueado != null) {
            //    ((InicioEstudianteContentController) controller).initData(estudianteLogueado);
            // }
            panelContenido.getChildren().setAll(vistaInicio);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista de inicio del estudiante: " + e.getMessage());
            e.printStackTrace();
            // Mostrar un mensaje de error en la UI si es necesario
        }
    }

    @FXML
    void handleMisCursos(ActionEvent event) {
        cargarVista("/fxml/estudiante/cursos_estudiante.fxml");
    }

    @FXML
    void handleExamenesDisponibles(ActionEvent event) {
        cargarVista("/fxml/estudiante/examenes_disponibles.fxml");
    }

    @FXML
    void handleMisResultados(ActionEvent event) {
        cargarVista("/fxml/estudiante/resultados_examen.fxml");
    }

    @FXML
    private void handleMisHorarios(ActionEvent event) {
        cargarVista("/fxml/estudiante/horarios_estudiante.fxml");
    }

    private void cargarVista(String fxmlPath) {
        try {
            // Es importante usar una ruta absoluta desde la raíz de resources si los FXML están allí
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node vista = loader.load();

            // Si el controlador de la vista cargada necesita el objeto Estudiante:
            Object controller = loader.getController();
            if (estudianteLogueado != null) {
                // Puedes usar una interfaz común para pasar datos o reflexión específica
                // Ejemplo con una interfaz común InitData<T>
                // if (controller instanceof InitData) {
                //    ((InitData<Estudiante>) controller).initData(estudianteLogueado);
                // }
                // O con reflexión si conoces el método (menos robusto):
                try {
                    controller.getClass().getMethod("initDataEstudiante", Estudiante.class).invoke(controller, estudianteLogueado);
                } catch (NoSuchMethodException nsme) {
                    // El controlador no tiene el método initDataEstudiante, puede que no lo necesite.
                    System.out.println("Nota: El controlador para " + fxmlPath + " no tiene initDataEstudiante(Estudiante).");
                }
                catch (Exception e) {
                    System.err.println("Error al intentar pasar datos de estudiante a " + fxmlPath + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            panelContenido.getChildren().setAll(vista);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
            // Aquí podrías mostrar un Label de error en el panelContenido
            Label lblError = new Label("No se pudo cargar la sección: " + fxmlPath.substring(fxmlPath.lastIndexOf("/") + 1));
            lblError.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            panelContenido.getChildren().setAll(lblError);
        }
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        try {
            // Obtener el Stage actual
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Cargar la pantalla de login
            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("/fxml/comun/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            stageActual.setScene(scene);
            stageActual.setTitle("EduTec - Iniciar Sesión");
            stageActual.setMaximized(false); // Opcional: restaurar tamaño
            stageActual.sizeToScene(); // Ajustar tamaño a la escena de login
            stageActual.centerOnScreen();
            System.out.println("Sesión cerrada. Volviendo a la pantalla de login.");

        } catch (IOException e) {
            System.err.println("Error al cargar la pantalla de login: " + e.getMessage());
            e.printStackTrace();
            // Mostrar alerta al usuario si es necesario
        }
    }
}
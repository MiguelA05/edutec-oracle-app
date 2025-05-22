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
            lblNombreEstudiante.setText(estudiante.getNombre() != null ? estudiante.getNombre() : "Nombre no disponible");
            lblCorreoEstudiante.setText(estudiante.getCorreo() != null ? estudiante.getCorreo() : "Correo no disponible");
        } else {
            lblNombreEstudiante.setText("Estudiante Desconocido");
            lblCorreoEstudiante.setText("");
        }
        if (panelContenido.getChildren().isEmpty() || !(panelContenido.getChildren().get(0).getUserData() instanceof InicioEstudianteContentController)) {
            handleInicio(null); // Carga la vista de inicio si no está o si no es la de inicio
        } else {
            // Si ya está la vista de inicio, intenta actualizarla
            try {
                Object currentController = panelContenido.getChildren().get(0).getUserData(); // Asumiendo que guardas el controller en userData
                if (currentController instanceof InicioEstudianteContentController) {
                    ((InicioEstudianteContentController) currentController).initDataEstudiante(estudianteLogueado, this);
                } else { // Si no es la de inicio, recárgala
                    handleInicio(null);
                }
            } catch (Exception e) {
                // Si falla la actualización, recargar.
                handleInicio(null);
            }
        }
    }

    @FXML
    private void handleInicio(ActionEvent event) {
        String fxmlPath = "/fxml/estudiante/inicio_estudiante_content.fxml";
        try {
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("Error: No se pudo encontrar el archivo FXML: " + fxmlPath);
                Label lblError = new Label("No se pudo cargar la vista de inicio (archivo no encontrado).");
                lblError.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                panelContenido.getChildren().setAll(lblError);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Node vistaInicio = loader.load();
            Object controller = loader.getController();
            // Guardar el controlador en el UserData del nodo raíz de la vista cargada
            // para poder acceder a él si necesitamos actualizarlo (ej. en setEstudiante).
            vistaInicio.setUserData(controller);


            if (controller instanceof InicioEstudianteContentController && estudianteLogueado != null) {
                ((InicioEstudianteContentController) controller).initDataEstudiante(estudianteLogueado, this);
            }
            panelContenido.getChildren().setAll(vistaInicio);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista de inicio del estudiante (" + fxmlPath + "): " + e.getMessage());
            e.printStackTrace();
            Label lblError = new Label("Error al cargar la vista de inicio.");
            lblError.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            panelContenido.getChildren().setAll(lblError);
        } catch (IllegalStateException e) {
            System.err.println("Error de estado ilegal al cargar FXML (" + fxmlPath + "): " + e.getMessage());
            e.printStackTrace();
            Label lblError = new Label("Error interno al cargar la vista de inicio.");
            lblError.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            panelContenido.getChildren().setAll(lblError);
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
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("Error: No se pudo encontrar el archivo FXML: " + fxmlPath);
                Label lblError = new Label("No se pudo cargar la sección (archivo '" + fxmlPath.substring(fxmlPath.lastIndexOf("/") + 1) + "' no encontrado).");
                lblError.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                panelContenido.getChildren().setAll(lblError);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Node vista = loader.load();
            Object controller = loader.getController();
            vista.setUserData(controller);

            if (estudianteLogueado != null && controller != null) {
                try {
                    controller.getClass().getMethod("initDataEstudiante", Estudiante.class).invoke(controller, estudianteLogueado);
                    System.out.println("Datos (Estudiante y Dashboard) pasados a " + controller.getClass().getSimpleName());
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
package org.uniquindio.ui.controller.comun;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.uniquindio.model.entity.usuario.Estudiante;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.repository.impl.ConnectionOracle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Controlador para la pantalla de login de la aplicación Edutec
 */
public class LoginController implements Initializable {

    @FXML
    private ComboBox<String> comboRol;

    @FXML
    private TextField textFieldUsuario;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox checkBoxRecordar;

    @FXML
    private Button btnIngresar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Hyperlink linkOlvido;

    // Preferencias para recordar los datos del usuario
    private Preferences prefs;

    /**
     * Inicializa el controlador
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar preferencias
        prefs = Preferences.userNodeForPackage(LoginController.class);

        // Configurar valores por defecto para el ComboBox
        comboRol.getSelectionModel().selectFirst();

        // Cargar credenciales guardadas si existe la opción de recordar
        if (prefs.getBoolean("recordar", false)) {
            checkBoxRecordar.setSelected(true);
            textFieldUsuario.setText(prefs.get("usuario", ""));
            passwordField.setText(prefs.get("password", ""));
            comboRol.setValue(prefs.get("rol", "Estudiante"));
        }

        // Validación de entrada: Solo permitir números en el campo de cédula
        textFieldUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textFieldUsuario.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Maneja el evento de clic en el botón Ingresar
     */
    @FXML
    private void handleIngresar(ActionEvent event) {
        String usuario = textFieldUsuario.getText().trim();
        String password = passwordField.getText();
        String rol = comboRol.getValue();

        // Validar campos vacíos
        if (usuario.isEmpty() || password.isEmpty() || rol == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de validación",
                    "Por favor complete todos los campos");
            return;
        }

        try {
            // Verificar credenciales según el rol seleccionado
            boolean autenticado = autenticarUsuario(usuario, password, rol);

            if (autenticado) {
                // Guardar preferencias si está seleccionado recordar
                if (checkBoxRecordar.isSelected()) {
                    prefs.put("usuario", usuario);
                    prefs.put("password", password);
                    prefs.put("rol", rol);
                    prefs.putBoolean("recordar", true);
                } else {
                    prefs.remove("usuario");
                    prefs.remove("password");
                    prefs.remove("rol");
                    prefs.putBoolean("recordar", false);
                }

                // Redirigir según el rol
                redirigirSegunRol(event, rol, Long.parseLong(usuario));

            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación",
                        "Credenciales incorrectas. Por favor intente nuevamente.");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de sistema",
                    "Ha ocurrido un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Autentica al usuario contra la base de datos
     */
    private boolean autenticarUsuario(String usuario, String password, String rol) throws SQLException {
        String tabla = rol.equals("Estudiante") ? "ESTUDIANTE" : rol.equals("Profesor") ? "PROFESOR" : "ADMINISTRADOR";
        String query = "SELECT * FROM " + tabla + " WHERE cedula = ? AND contrasena = ?";

        try (Connection conn = ConnectionOracle.conectar();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, Long.parseLong(usuario));
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Si hay al menos un resultado, las credenciales son válidas
            }
        }
    }

    /**
     * Redirige al usuario a su respectivo dashboard según su rol
     *
     * @return
     */
    private Estudiante redirigirSegunRol(ActionEvent event, String rol, long cedula) throws IOException, SQLException {
        String fxmlPath;
        Object userData = null;

        // Determinar qué ventana abrir según el rol
        switch (rol) {
            case "Estudiante":
                fxmlPath = "/fxml/estudiante/dashboard_estudiante.fxml";
                // Cargar datos del estudiante
                userData = cargarEstudiante(cedula);
                break;

            case "Profesor":
                fxmlPath = "/fxml/profesor/dashboard_profesor.fxml";
                // Cargar datos del profesor
                userData = cargarProfesor(cedula);
                break;

            case "Administrador":
                fxmlPath = "/fxml/admin/dashboard_admin.fxml";
                break;

            default:
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Rol no reconocido");
                return;
        }

        // Cargar la nueva ventana
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Obtener controlador y pasar datos de usuario si es necesario
        if (userData != null) {
            Object controller = loader.getController();
        /*
            // Configurar datos en el controlador según el tipo
            if (controller instanceof DashboardEstudianteController && userData instanceof Estudiante) {
                ((DashboardEstudianteController) controller).setEstudiante((Estudiante) userData);
            } else if (controller instanceof DashboardProfesorController && userData instanceof Profesor) {
                ((DashboardProfesorController) controller).setProfesor((Profesor) userData);
            }
        }

         */

        // Mostrar la nueva ventana
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Edutec - " + rol);
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Carga los datos de un estudiante desde la base de datos
     */
    private Estudiante cargarEstudiante(long cedula) {
        return null;
    }

    /**
     * Carga los datos de un profesor desde la base de datos
     */
    private Profesor cargarProfesor(long cedula) {
        return null;
    }

    /**
     * Maneja el evento de clic en el botón Cancelar
     */
    @FXML
    public void handleCancelar(ActionEvent event) {
        // Limpiar campos
        textFieldUsuario.clear();
        passwordField.clear();
        comboRol.getSelectionModel().selectFirst();
        checkBoxRecordar.setSelected(false);
    }

    /**
     * Maneja el evento de clic en el enlace de olvidó contraseña
     */
    @FXML
    private void handleOlvido(ActionEvent event) {
        // Crear un diálogo para recuperación de contraseña
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Recuperar Contraseña");
        dialog.setHeaderText("Ingrese su correo electrónico para recuperar su contraseña");

        // Crear campos para el diálogo
        TextField emailField = new TextField();
        emailField.setPromptText("ejemplo@correo.com");

        // Crear contenido del diálogo
        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Correo electrónico:"),
                emailField
        );

        dialog.getDialogPane().setContent(content);

        // Agregar botones
        ButtonType btnEnviar = new ButtonType("Enviar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEnviar, btnCancelar);

        // Mostrar diálogo y procesar resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnEnviar) {
                return emailField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(email -> {
            if (!email.isEmpty() && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                // Aquí iría la lógica para enviar el correo de recuperación
                mostrarAlerta(Alert.AlertType.INFORMATION, "Recuperación de Contraseña",
                        "Se ha enviado un correo con las instrucciones para recuperar su contraseña a: " + email);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Por favor ingrese un correo válido");
            }
        });
    }

    /**
     * Muestra una alerta con el tipo, título y mensaje especificados
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
package org.uniquindio.ui.controller.comun;

import javafx.application.Platform;
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
import org.uniquindio.repository.impl.ConnectionOracle; // Asegúrate que esta clase esté usando la URL correcta

import java.io.IOException;
import java.net.URL;
import java.sql.*;
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

    private Preferences prefs;

    // Nombres de los paquetes y funciones PL/SQL esperados (ajusta según tu implementación en BD)
    private static final String PAQUETE_AUTENTICACION = "PAQUETE_AUTENTICACION"; // Ejemplo
    private static final String FUNCION_VALIDAR_CREDENCIALES = "VALIDAR_CREDENCIALES";
    private static final String PAQUETE_USUARIOS = "PAQUETE_USUARIOS"; // Ejemplo
    private static final String FUNCION_OBTENER_ESTUDIANTE = "OBTENER_ESTUDIANTE_POR_CEDULA";
    private static final String FUNCION_OBTENER_PROFESOR = "OBTENER_PROFESOR_POR_CEDULA";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prefs = Preferences.userNodeForPackage(LoginController.class);

        comboRol.getItems().addAll("Estudiante", "Profesor");
        comboRol.getSelectionModel().selectFirst();

        if (prefs.getBoolean("recordar", false)) {
            checkBoxRecordar.setSelected(true);
            textFieldUsuario.setText(prefs.get("usuario", ""));
            passwordField.setText(prefs.get("password", "")); // Considera la seguridad de guardar contraseñas
            comboRol.setValue(prefs.get("rol", "Estudiante"));
        }

        textFieldUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textFieldUsuario.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        verificarConexionBD();
    }

    private void verificarConexionBD() {
        try (Connection conn = ConnectionOracle.conectar()) {
            System.out.println("Conexión a la base de datos Oracle establecida correctamente.");
            String esquema = conn.getMetaData().getUserName();
            System.out.println("Conectado como usuario (esquema): " + esquema);

            // Listar tablas propiedad del usuario actual
            System.out.println("Tablas en el esquema '" + esquema + "':");
            String queryUserTables = "SELECT table_name FROM user_tables ORDER BY table_name";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(queryUserTables)) {
                int count = 0;
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("TABLE_NAME"));
                    count++;
                }
                if (count == 0) {
                    System.out.println("No se encontraron tablas propiedad del usuario " + esquema);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar la conexión con la base de datos: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión",
                    "No se pudo establecer conexión con la base de datos. Verifique la configuración y que el servidor Oracle esté accesible."));
        }
    }

    @FXML
    private void handleIngresar(ActionEvent event) {
        String usuarioCedulaStr = textFieldUsuario.getText().trim();
        String password = passwordField.getText();
        String rol = comboRol.getValue();

        if (usuarioCedulaStr.isEmpty() || password.isEmpty() || rol == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor complete todos los campos.");
            return;
        }

        long cedula;
        try {
            cedula = Long.parseLong(usuarioCedulaStr);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "La cédula debe ser un valor numérico.");
            return;
        }

        try {
            boolean autenticado = autenticarUsuario(cedula, password, rol);

            if (autenticado) {
                if (checkBoxRecordar.isSelected()) {
                    prefs.put("usuario", usuarioCedulaStr);
                    prefs.put("password", password); // Considerar implicaciones de seguridad
                    prefs.put("rol", rol);
                    prefs.putBoolean("recordar", true);
                } else {
                    prefs.remove("usuario");
                    prefs.remove("password");
                    prefs.remove("rol");
                    prefs.putBoolean("recordar", false);
                }
                redirigirSegunRol(event, rol, cedula);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Autenticación", "Cédula, contraseña o rol incorrectos.");
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "Error al autenticar: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo cargar la pantalla de " + rol + ".");
            e.printStackTrace();
        }
    }

    /**
     * Autentica al usuario llamando a una función PL/SQL en la base de datos.
     * Se espera una función PL/SQL como:
     * FUNCTION PAQUETE_AUTENTICACION.VALIDAR_CREDENCIALES(p_cedula IN NUMBER, p_contrasena IN VARCHAR2, p_rol IN VARCHAR2) RETURN BOOLEAN;
     * O podría devolver un NUMBER (1 para éxito, 0 para fallo).
     */
    private boolean autenticarUsuario(long cedula, String password, String rolSeleccionado) throws SQLException {
        // Asegúrate que el nombre del paquete y la función coincidan con tu implementación en la BD.
        String sql = String.format("{? = call %s.%s(?, ?, ?)}", PAQUETE_AUTENTICACION, FUNCION_VALIDAR_CREDENCIALES);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.BOOLEAN); // O Types.INTEGER si devuelve 0 o 1
            cstmt.setLong(2, cedula);
            cstmt.setString(3, password);
            cstmt.setString(4, rolSeleccionado.toUpperCase()); // Enviar ROL en mayúsculas si PL/SQL lo espera así

            cstmt.execute();
            return cstmt.getBoolean(1);
        }
    }

    private void redirigirSegunRol(ActionEvent event, String rol, long cedula) throws IOException, SQLException {
        String fxmlPath;
        Object userData = null;

        switch (rol) {
            case "Estudiante":
                fxmlPath = "/fxml/estudiante/dashboard_estudiante.fxml";
                userData = cargarEstudiante(cedula);
                if (userData == null) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos", "No se encontraron datos para el estudiante con cédula: " + cedula);
                    return;
                }
                break;
            case "Profesor":
                fxmlPath = "/fxml/profesor/dashboard_profesor.fxml";
                userData = cargarProfesor(cedula);
                if (userData == null) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos", "No se encontraron datos para el profesor con cédula: " + cedula);
                    return;
                }
                break;
            default:
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Rol", "Rol no reconocido: " + rol);
                return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Object controller = loader.getController();

        // Pasar datos al controlador del dashboard.
        // Considerar usar una interfaz común (ej. UserAwareController) para evitar reflexión frágil.
        if (userData != null && controller != null) {
            try {
                if (rol.equals("Estudiante") && userData instanceof Estudiante) {
                    // Asumiendo que DashboardEstudianteController tiene un método setEstudiante(Estudiante e)
                    controller.getClass().getMethod("setEstudiante", Estudiante.class).invoke(controller, userData);
                } else if (rol.equals("Profesor") && userData instanceof Profesor) {
                    // Asumiendo que DashboardProfesorController tiene un método setProfesor(Profesor p)
                    controller.getClass().getMethod("setProfesor", Profesor.class).invoke(controller, userData);
                }
            } catch (Exception e) {
                System.err.println("Error al pasar datos de usuario al controlador del dashboard: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se pudieron pasar los datos de usuario a la siguiente pantalla.");
            }
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("EduTec - " + rol);
        stage.show();
    }

    /**
     * Carga datos del estudiante desde una función PL/SQL que devuelve SYS_REFCURSOR.
     * Se espera una función PL/SQL como:
     * FUNCTION PAQUETE_USUARIOS.OBTENER_ESTUDIANTE_POR_CEDULA(p_cedula IN NUMBER) RETURN SYS_REFCURSOR;
     */
    private Estudiante cargarEstudiante(long cedula) throws SQLException {
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_USUARIOS, FUNCION_OBTENER_ESTUDIANTE);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.REF_CURSOR); // OracleTypes.CURSOR si usas el driver específico de Oracle
            cstmt.setLong(2, cedula);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                if (rs != null && rs.next()) {
                    Estudiante estudiante = new Estudiante();
                    // Asumiendo que las columnas en la BD son CEDULA, NOMBRE, CORREO (en mayúsculas)
                    estudiante.setCedula(rs.getLong("CEDULA"));
                    estudiante.setNombre(rs.getString("NOMBRE"));
                    estudiante.setCorreo(rs.getString("CORREO"));
                    // Cargar otros campos si es necesario
                    return estudiante;
                }
            }
        }
        return null;
    }

    /**
     * Carga datos del profesor desde una función PL/SQL que devuelve SYS_REFCURSOR.
     * Se espera una función PL/SQL como:
     * FUNCTION PAQUETE_USUARIOS.OBTENER_PROFESOR_POR_CEDULA(p_cedula IN NUMBER) RETURN SYS_REFCURSOR;
     */
    private Profesor cargarProfesor(long cedula) throws SQLException {
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_USUARIOS, FUNCION_OBTENER_PROFESOR);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.REF_CURSOR); // OracleTypes.CURSOR
            cstmt.setLong(2, cedula);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                if (rs != null && rs.next()) {
                    Profesor profesor = new Profesor();
                    // Asumiendo que las columnas en la BD son CEDULA, NOMBRE, CORREO (en mayúsculas)
                    profesor.setCedula(rs.getLong("CEDULA"));
                    profesor.setNombre(rs.getString("NOMBRE"));
                    profesor.setCorreo(rs.getString("CORREO"));
                    // Cargar otros campos si es necesario
                    return profesor;
                }
            }
        }
        return null;
    }


    @FXML
    public void handleCancelar(ActionEvent event) {
        textFieldUsuario.clear();
        passwordField.clear();
        comboRol.getSelectionModel().selectFirst();
        checkBoxRecordar.setSelected(false);
    }

    @FXML
    private void handleOlvido(ActionEvent event) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Recuperar Contraseña");
        dialog.setHeaderText("Ingrese su correo electrónico para iniciar el proceso de recuperación.");

        TextField emailField = new TextField();
        emailField.setPromptText("ejemplo@correo.com");
        VBox content = new VBox(10, new Label("Correo electrónico:"), emailField);
        dialog.getDialogPane().setContent(content);

        ButtonType btnEnviar = new ButtonType("Enviar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEnviar, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> dialogButton == btnEnviar ? emailField.getText() : null);

        dialog.showAndWait().ifPresent(email -> {
            if (email != null && !email.trim().isEmpty() && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                enviarCorreoRecuperacion(email.trim()); // Lógica de envío de correo
                mostrarAlerta(Alert.AlertType.INFORMATION, "Recuperación Solicitada",
                        "Si el correo está registrado, recibirá instrucciones para recuperar su contraseña a: " + email.trim());
            } else if (email != null) { // Solo muestra error si se presionó Enviar con email inválido
                mostrarAlerta(Alert.AlertType.ERROR, "Correo Inválido", "Por favor ingrese una dirección de correo electrónico válida.");
            }
        });
    }

    private void enviarCorreoRecuperacion(String email) {
        // TODO: Implementar la lógica real de envío de correo.
        // Esto requeriría una función PL/SQL que genere un token de recuperación,
        // lo almacene temporalmente asociado al usuario, y luego Java podría usar
        // JavaMail API para enviar un enlace con ese token.
        // O, si el servidor de BD puede enviar correos (UTL_SMTP), PL/SQL podría hacerlo todo.
        System.out.println("Simulando envío de correo de recuperación a: " + email);
        // Lógica PL/SQL:
        // 1. Validar que el email exista para algún usuario.
        // 2. Generar un token único y con expiración.
        // 3. Almacenar el token asociado al usuario.
        // 4. Enviar email (JavaMail o UTL_SMTP) con un enlace que incluya el token.
        //    El enlace apuntaría a una nueva pantalla/funcionalidad para resetear la contraseña.
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null); // No usar header text para un look más simple
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

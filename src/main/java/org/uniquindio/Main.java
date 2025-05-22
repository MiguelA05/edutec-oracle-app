package org.uniquindio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.uniquindio.repository.impl.ConnectionOracle;

import java.sql.Connection;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            Connection conexion = ConnectionOracle.conectar();
            System.out.println("✅ Conexión exitosa a Oracle");
            conexion.close();
        } catch (Exception e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
        }

        // Continúa con tu interfaz
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/profesor/banco_preguntas.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/estudiante/dashboard_estudiante.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/estudiante/inicio_estudiante_content.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/profesor/crear_editar_examen.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/profesor/dialogs/crear_pregunta_dialog.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/profesor/dialogs/seleccionar_pregunta_dialogs.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/profesor/dashboard_profesor.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/estudiante/inicio_profesor_content.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/estudiante/examenes_disponibles.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/estudiante/resultados_examen.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/estudiante/presentacion_examen.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/comun/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("EduTec");
        stage.getIcons().add(new Image("/images/logo_edutec.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}

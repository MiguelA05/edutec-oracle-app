package org.uniquindio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/profesor/dashboard_profesor.fxml"));
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

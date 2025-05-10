package org.uniquindio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.uniquindio.repositorie.ConnectionOracle;

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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/interface.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("App JavaFX con Oracle");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}

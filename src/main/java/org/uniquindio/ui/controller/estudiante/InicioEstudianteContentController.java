package org.uniquindio.ui.controller.estudiante;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.uniquindio.model.entity.usuario.Estudiante;
// Importar clases de repositorio si es necesario para obtener datos resumidos
// import org.uniquindio.repository.impl.ExamenRepositoryImpl; // Ejemplo

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InicioEstudianteContentController implements Initializable {

    @FXML
    private VBox rootInicioEstudiante;
    @FXML
    private Label lblBienvenida;
    @FXML
    private Label lblProximosExamenesInfo;
    @FXML
    private Label lblCursosActualesInfo;
    @FXML
    private Label lblUltimosResultadosInfo;

    private Estudiante estudiante;
    private DashboardEstudianteController dashboardController; // Referencia al controlador del dashboard principal

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuración inicial si es necesaria, pero los datos del estudiante
        // se cargarán a través de setEstudiante o initDataEstudiante.
        // Puedes establecer textos predeterminados aquí.
        lblBienvenida.setText("¡Bienvenido a EduTec!");
        lblProximosExamenesInfo.setText("Cargando información de exámenes...");
        lblCursosActualesInfo.setText("Cargando información de cursos...");
        lblUltimosResultadosInfo.setText("Cargando resultados recientes...");
    }

    /**
     * Método para recibir el objeto Estudiante y la referencia al DashboardController.
     * Este método debe ser llamado después de cargar este FXML.
     * @param estudiante El estudiante que ha iniciado sesión.
     * @param dashboardController El controlador del dashboard principal para la navegación.
     */
    public void initDataEstudiante(Estudiante estudiante, DashboardEstudianteController dashboardController) {
        this.estudiante = estudiante;
        this.dashboardController = dashboardController;
        actualizarInformacionBienvenida();
        cargarDatosResumen();
    }

    // Alternativa si solo pasas el estudiante
    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
        actualizarInformacionBienvenida();
        cargarDatosResumen();
    }


    private void actualizarInformacionBienvenida() {
        if (this.estudiante != null && this.estudiante.getNombre() != null && !this.estudiante.getNombre().isEmpty()) {
            lblBienvenida.setText("¡Bienvenido de nuevo, " + this.estudiante.getNombre() + "!");
        } else {
            lblBienvenida.setText("¡Bienvenido a EduTec!");
        }
    }

    private void cargarDatosResumen() {
        if (this.estudiante == null) {
            lblProximosExamenesInfo.setText("No hay información de usuario disponible.");
            lblCursosActualesInfo.setText("No hay información de usuario disponible.");
            lblUltimosResultadosInfo.setText("No hay información de usuario disponible.");
            return;
        }

        // TODO: Implementar lógica para cargar datos reales desde la base de datos (usando PL/SQL vía repositorios)
        // Ejemplo conceptual (deberás crear los métodos en tus repositorios y funciones PL/SQL):
        // ExamenRepositoryImpl examenRepo = new ExamenRepositoryImpl();
        // CursoRepositoryImpl cursoRepo = new CursoRepositoryImpl();
        // try {
        //     int numProximosExamenes = examenRepo.contarProximosExamenes(estudiante.getCedula());
        //     lblProximosExamenesInfo.setText("Tienes " + numProximosExamenes + " exámenes próximamente.");
        //
        //     int numCursos = cursoRepo.contarCursosActivosEstudiante(estudiante.getCedula());
        //     lblCursosActualesInfo.setText("Estás inscrito en " + numCursos + " cursos actualmente.");
        //
        //     String ultimoResultado = examenRepo.obtenerUltimoResultado(estudiante.getCedula());
        //     lblUltimosResultadosInfo.setText(ultimoResultado.isEmpty() ? "Aún no tienes resultados." : "Último examen: " + ultimoResultado);
        //
        // } catch (SQLException e) {
        //     System.err.println("Error al cargar datos de resumen para el estudiante: " + e.getMessage());
        //     lblProximosExamenesInfo.setText("Error al cargar datos.");
        //     lblCursosActualesInfo.setText("Error al cargar datos.");
        //     lblUltimosResultadosInfo.setText("Error al cargar datos.");
        // }

        // Datos de placeholder mientras implementas la lógica real:
        lblProximosExamenesInfo.setText("Tienes 2 exámenes programados esta semana.");
        lblCursosActualesInfo.setText("Estás inscrito en 5 cursos actualmente.");
        lblUltimosResultadosInfo.setText("Última calificación: 4.5 en Matemáticas.");
    }

    // Métodos para manejar los clics en los botones de las tarjetas
    // Estos métodos llamarán a los métodos correspondientes en el DashboardEstudianteController
    // para cambiar la vista en el panelContenido principal.
    @FXML
    private void irAExamenesDisponibles(ActionEvent event) {
        if (dashboardController != null) {
            dashboardController.handleExamenesDisponibles(event);
        } else {
            System.err.println("DashboardController no está inicializado en InicioEstudianteContentController.");
            // Podrías intentar cargar la vista directamente si es necesario, pero es mejor a través del dashboard
        }
    }

    @FXML
    private void irAMisCursos(ActionEvent event) {
        if (dashboardController != null) {
            dashboardController.handleMisCursos(event);
        } else {
            System.err.println("DashboardController no está inicializado en InicioEstudianteContentController.");
        }
    }

    @FXML
    private void irAMisResultados(ActionEvent event) {
        if (dashboardController != null) {
            dashboardController.handleMisResultados(event);
        } else {
            System.err.println("DashboardController no está inicializado en InicioEstudianteContentController.");
        }
    }
}
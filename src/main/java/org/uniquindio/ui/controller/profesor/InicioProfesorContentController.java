package org.uniquindio.ui.controller.profesor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.uniquindio.model.entity.usuario.Profesor;
// Importar clases de repositorio si es necesario para obtener datos resumidos
// import org.uniquindio.repository.impl.CursoRepositoryImpl; // Ejemplo
// import org.uniquindio.repository.impl.ExamenRepositoryImpl; // Ejemplo
// import org.uniquindio.repository.impl.PreguntaRepositoryImpl; // Ejemplo

import java.net.URL;
import java.util.ResourceBundle;

public class InicioProfesorContentController implements Initializable {

    @FXML
    private Label lblBienvenidaProfesor;
    @FXML
    private Label lblCursosActivosInfo;
    @FXML
    private Label lblExamenesCreadosInfo;
    @FXML
    private Label lblPreguntasBancoInfo;
    @FXML
    private Label lblRendimientoInfo;
    @FXML
    private Label lblHorariosInfo;


    private Profesor profesor;
    private DashboardProfesorController dashboardController; // Referencia al controlador del dashboard principal

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Textos iniciales mientras se cargan los datos reales
        lblBienvenidaProfesor.setText("Bienvenido, Profesor!");
        lblCursosActivosInfo.setText("Cargando...");
        lblExamenesCreadosInfo.setText("Cargando...");
        lblPreguntasBancoInfo.setText("Cargando...");
        lblRendimientoInfo.setText("Cargando...");
        lblHorariosInfo.setText("Consulta tus horarios.");
    }

    /**
     * Método para recibir el objeto Profesor y la referencia al DashboardController.
     * @param profesor El profesor que ha iniciado sesión.
     * @param dashboardController El controlador del dashboard principal para la navegación.
     */
    public void initDataProfesor(Profesor profesor, DashboardProfesorController dashboardController) {
        this.profesor = profesor;
        this.dashboardController = dashboardController;
        actualizarInformacionBienvenida();
        cargarDatosResumenProfesor();
    }

    // Alternativa si solo pasas el profesor
    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
        actualizarInformacionBienvenida();
        cargarDatosResumenProfesor();
    }

    private void actualizarInformacionBienvenida() {
        if (this.profesor != null && this.profesor.getNombre() != null && !this.profesor.getNombre().isEmpty()) {
            lblBienvenidaProfesor.setText("Bienvenido, Profesor " + this.profesor.getNombre() + "!");
        } else {
            lblBienvenidaProfesor.setText("Bienvenido, Profesor!");
        }
    }

    private void cargarDatosResumenProfesor() {
        if (this.profesor == null) {
            String errorMsg = "No hay información de usuario disponible.";
            lblCursosActivosInfo.setText(errorMsg);
            lblExamenesCreadosInfo.setText(errorMsg);
            lblPreguntasBancoInfo.setText(errorMsg);
            lblRendimientoInfo.setText(errorMsg);
            return;
        }

        // TODO: Implementar lógica para cargar datos reales desde la base de datos
        // utilizando los repositorios que llaman a funciones/procedimientos PL/SQL.
        // Por ejemplo:
        // CursoRepositoryImpl cursoRepo = new CursoRepositoryImpl();
        // ExamenRepositoryImpl examenRepo = new ExamenRepositoryImpl();
        // PreguntaRepositoryImpl preguntaRepo = new PreguntaRepositoryImpl();
        // try {
        //     int numCursos = cursoRepo.contarCursosActivosProfesor(profesor.getCedula());
        //     lblCursosActivosInfo.setText(numCursos + " cursos activos");
        //
        //     int numExamenes = examenRepo.contarExamenesPorProfesor(profesor.getCedula());
        //     lblExamenesCreadosInfo.setText(numExamenes + " exámenes creados");
        //
        //     int numPreguntas = preguntaRepo.contarPreguntasPorProfesor(profesor.getCedula());
        //     lblPreguntasBancoInfo.setText(numPreguntas + " preguntas en banco");
        //
        //     // Para rendimiento, podrías necesitar una consulta más compleja
        //     double rendimientoPromedio = examenRepo.obtenerRendimientoPromedioCursosProfesor(profesor.getCedula());
        //     lblRendimientoInfo.setText(String.format("Promedio general: %.2f", rendimientoPromedio));
        //
        // } catch (SQLException e) {
        //     System.err.println("Error al cargar datos de resumen para el profesor: " + e.getMessage());
        //     String errorCarga = "Error al cargar datos.";
        //     lblCursosActivosInfo.setText(errorCarga);
        //     lblExamenesCreadosInfo.setText(errorCarga);
        //     lblPreguntasBancoInfo.setText(errorCarga);
        //     lblRendimientoInfo.setText(errorCarga);
        // }

        // Datos de placeholder mientras implementas la lógica real:
        lblCursosActivosInfo.setText("3 cursos activos");
        lblExamenesCreadosInfo.setText("12 exámenes creados");
        lblPreguntasBancoInfo.setText("75 preguntas en banco");
        lblRendimientoInfo.setText("Promedio general: 4.2");
    }

    // Métodos de navegación que llaman al DashboardProfesorController
    @FXML
    private void irAGestionCursos(ActionEvent event) {
        if (dashboardController != null) dashboardController.handleGestionCursos(event);
        else System.err.println("DashboardProfesorController no está disponible en InicioProfesorContentController.");
    }

    @FXML
    private void irAGestionExamenes(ActionEvent event) {
        if (dashboardController != null) dashboardController.handleGestionExamenes(event);
        else System.err.println("DashboardProfesorController no está disponible.");
    }

    @FXML
    private void irABancoPreguntas(ActionEvent event) {
        if (dashboardController != null) dashboardController.handleBancoPreguntas(event);
        else System.err.println("DashboardProfesorController no está disponible.");
    }

    @FXML
    private void irAEstadisticas(ActionEvent event) {
        if (dashboardController != null) dashboardController.handleEstadisticas(event);
        else System.err.println("DashboardProfesorController no está disponible.");
    }

    @FXML
    private void irAGestionPreguntas(ActionEvent event) {
        if (dashboardController != null) dashboardController.handleExamenesCreados(event);
        else System.err.println("DashboardProfesorController no está disponible.");
    }
    @FXML
    private void irAHorariosProfesor(ActionEvent event) {
        if (dashboardController != null) dashboardController.handleHorariosProfesor(event);
        else System.err.println("DashboardProfesorController no está disponible.");
    }
}
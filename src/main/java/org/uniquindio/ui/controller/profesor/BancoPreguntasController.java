package org.uniquindio.ui.controller.profesor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.uniquindio.model.entity.evaluacion.*;
import org.uniquindio.model.entity.academico.*; // Tu entidad Pregunta
import org.uniquindio.model.entity.catalogo.Nivel;
import org.uniquindio.model.entity.catalogo.TipoPregunta;
import org.uniquindio.model.entity.catalogo.Visibilidad;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.ui.controller.profesor.dialogs.CrearPreguntaDialogController; // Para crear/editar

// Importar repositorios
// import org.uniquindio.repository.impl.PreguntaRepositoryImpl;
// import org.uniquindio.repository.impl.CatalogoRepositoryImpl;
// import org.uniquindio.repository.impl.ContenidoRepositoryImpl;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BancoPreguntasController implements Initializable {

    @FXML private TextField txtBusquedaGeneral;
    @FXML private ComboBox<TipoPregunta> comboFiltroTipoPreguntaBanco;
    @FXML private ComboBox<Contenido> comboFiltroTemaBanco;
    // Añadir ComboBox para Nivel y Visibilidad si se implementan filtros para ellos
    @FXML private Button btnAplicarFiltros;
    @FXML private TableView<Pregunta> tablaBancoPreguntas; // Usará la entidad Pregunta directamente o un DTO
    @FXML private TableColumn<Pregunta, Integer> colIdPregunta;
    @FXML private TableColumn<Pregunta, String> colTextoPreguntaBanco;
    @FXML private TableColumn<Pregunta, String> colTipoPreguntaBanco; // Necesitará mapeo de ID a Nombre
    @FXML private TableColumn<Pregunta, String> colTemaBanco;       // Necesitará mapeo de ID a Nombre
    @FXML private TableColumn<Pregunta, String> colNivelBanco;      // Necesitará mapeo de ID a Nombre
    @FXML private TableColumn<Pregunta, String> colVisibilidadBanco; // Necesitará mapeo de ID a Nombre
    @FXML private TableColumn<Pregunta, Void> colAccionesBanco;
    @FXML private Button btnCrearNuevaPreguntaBanco;

    private ObservableList<Pregunta> listaPreguntas = FXCollections.observableArrayList();
    private Profesor profesorLogueado;

    // Repositorios
    // private PreguntaRepositoryImpl preguntaRepository = new PreguntaRepositoryImpl();
    // private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    // private ContenidoRepositoryImpl contenidoRepository = new ContenidoRepositoryImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaBancoPreguntas();
        cargarFiltrosBanco();
        // Cargar todas las preguntas del profesor al inicio
        // handleAplicarFiltros(null); // O cargar un conjunto inicial
    }

    public void setProfesor(Profesor profesor) {
        this.profesorLogueado = profesor;
        // Cargar preguntas una vez que el profesor está seteado
        handleAplicarFiltros(null);
        cargarFiltrosBanco(); // Recargar filtros que puedan depender del profesor (ej. sus temas)
    }

    private void configurarTablaBancoPreguntas() {
        colIdPregunta.setCellValueFactory(new PropertyValueFactory<>("idPregunta"));
        colTextoPreguntaBanco.setCellValueFactory(new PropertyValueFactory<>("texto"));

        // Para las columnas que muestran nombres basados en IDs, necesitarás convertidores o celdas personalizadas
        // si la entidad Pregunta solo tiene los IDs.
        // Ejemplo conceptual (requiere que cargues listas de TipoPregunta, Contenido, etc. para mapear):
        // colTipoPreguntaBanco.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerNombreTipoPregunta(cellData.getValue().getTipoPreguntaId())));
        // colTemaBanco.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerNombreContenido(cellData.getValue().getContenidoId())));
        // colNivelBanco.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerNombreNivel(cellData.getValue().getNivelId())));
        // colVisibilidadBanco.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerNombreVisibilidad(cellData.getValue().getVisibilidadId())));
        // Por ahora, se asume que podrías tener estos nombres en un DTO o los cargarás.
        // Si usas la entidad Pregunta directamente, estas columnas mostrarán IDs si no se personalizan.


        colAccionesBanco.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.getStyleClass().add("button-editar-tabla-banco");
                btnEliminar.getStyleClass().add("button-eliminar-tabla-banco");

                btnEditar.setOnAction(event -> {
                    Pregunta pregunta = getTableView().getItems().get(getIndex());
                    handleEditarPregunta(pregunta);
                });
                btnEliminar.setOnAction(event -> {
                    Pregunta pregunta = getTableView().getItems().get(getIndex());
                    handleEliminarPregunta(pregunta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        tablaBancoPreguntas.setItems(listaPreguntas);
    }

    private void cargarFiltrosBanco() {
        if (profesorLogueado == null) return; // No cargar filtros si no hay profesor

        // TODO: Cargar datos para ComboBoxes de filtro desde la BD usando repositorios y PL/SQL
        // Deberían incluir una opción "Todos" o ser opcionales.
        // List<TipoPregunta> tipos = catalogoRepository.listarTiposPreguntaConOpcionTodos();
        // comboFiltroTipoPreguntaBanco.setItems(FXCollections.observableArrayList(tipos));

        // List<Contenido> temas = contenidoRepository.listarContenidosPorProfesorConOpcionTodos(profesorLogueado.getCedula());
        // comboFiltroTemaBanco.setItems(FXCollections.observableArrayList(temas));

        // Placeholder
        // comboFiltroTipoPreguntaBanco.getItems().add(null); // Para "Todos"
        // comboFiltroTemaBanco.getItems().add(null);
    }

    @FXML
    private void handleAplicarFiltros(ActionEvent event) {
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Información Requerida", "No se ha identificado al profesor.");
            return;
        }
        // TODO: Implementar la lógica de búsqueda/filtrado de preguntas
        // 1. Obtener los valores de los filtros.
        // String texto = txtBusquedaGeneral.getText();
        // Integer tipoId = comboFiltroTipoPreguntaBanco.getValue() != null ? comboFiltroTipoPreguntaBanco.getValue().getId() : null;
        // Integer contenidoId = comboFiltroTemaBanco.getValue() != null ? comboFiltroTemaBanco.getValue().getId_contenido() : null;

        // 2. Llamar a un procedimiento/función PL/SQL (via Repositorio) que devuelva las preguntas filtradas
        //    para este profesor (incluyendo sus preguntas privadas y las públicas).
        // try {
        //     List<Pregunta> preguntasEncontradas = preguntaRepository.buscarPreguntasBanco(
        //         profesorLogueado.getCedula(), // Para obtener privadas y públicas
        //         texto,
        //         tipoId,
        //         contenidoId
        //         // Añadir más filtros como nivelId, visibilidadId si se implementan
        //     );
        //     listaPreguntas.setAll(preguntasEncontradas);
        //     if (preguntasEncontradas.isEmpty()){
        //         mostrarAlerta(Alert.AlertType.INFORMATION, "Sin Resultados", "No se encontraron preguntas con los filtros aplicados.");
        //     }
        // } catch (SQLException e) {
        //     mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar las preguntas: " + e.getMessage());
        //     e.printStackTrace();
        // }

        // Placeholder
        listaPreguntas.clear();
        // listaPreguntas.add(new Pregunta(1, "¿Qué es POO?", "5 min", BigDecimal.valueOf(10), 1, 1, 1, null, 1));
        // listaPreguntas.add(new Pregunta(2, "Java es un lenguaje compilado.", "2 min", BigDecimal.valueOf(5), 2, 1, 1, null, 2));
        System.out.println("Búsqueda/Filtro de preguntas (simulado). Implementar lógica real.");
        if (listaPreguntas.isEmpty()) {
            tablaBancoPreguntas.setPlaceholder(new Label("No hay preguntas para mostrar con los filtros actuales."));
        }
    }

    @FXML
    private void handleCrearNuevaPreguntaBanco(ActionEvent event) {
        abrirDialogoCrearEditarPregunta(null); // null para crear nueva
    }

    private void handleEditarPregunta(Pregunta preguntaAEditar) {
        abrirDialogoCrearEditarPregunta(preguntaAEditar);
    }

    private void abrirDialogoCrearEditarPregunta(Pregunta pregunta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profesor/dialogs/crear_pregunta_dialog.fxml"));
            Parent parent = loader.load();

            CrearPreguntaDialogController dialogController = loader.getController();
            dialogController.setProfesor(this.profesorLogueado);
            if (pregunta != null) {
                dialogController.setPreguntaParaEdicion(pregunta); // Necesitarás este método en CrearPreguntaDialogController
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(pregunta == null ? "Crear Nueva Pregunta" : "Editar Pregunta");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // dialogStage.initOwner(btnCrearNuevaPreguntaBanco.getScene().getWindow()); // Opcional
            Scene scene = new Scene(parent);
            dialogStage.setScene(scene);
            dialogController.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            // Refrescar la tabla después de crear/editar
            handleAplicarFiltros(null);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el diálogo de creación/edición de pregunta.");
        }
    }


    private void handleEliminarPregunta(Pregunta pregunta) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("Eliminar Pregunta ID: " + pregunta.getIdPregunta());
        confirmacion.setContentText("¿Está seguro de que desea eliminar la pregunta: \"" + pregunta.getTexto().substring(0, Math.min(pregunta.getTexto().length(), 50)) + "...\"?\nEsta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // TODO: Llamar a procedimiento PL/SQL via Repositorio para eliminar la pregunta.
            // Considerar restricciones (si la pregunta está en uso en exámenes ya presentados, etc.)
            // try {
            //     boolean eliminado = preguntaRepository.eliminarPreguntaPLSQL(pregunta.getIdPregunta());
            //     if (eliminado) {
            //         mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminación Exitosa", "La pregunta ha sido eliminada.");
            //         handleAplicarFiltros(null); // Refrescar tabla
            //     } else {
            //         mostrarAlerta(Alert.AlertType.ERROR, "Error de Eliminación", "No se pudo eliminar la pregunta. Puede estar en uso.");
            //     }
            // } catch (SQLException e) {
            //     mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "Error al eliminar: " + e.getMessage());
            //     e.printStackTrace();
            // }
            System.out.println("Eliminación de pregunta (simulada): " + pregunta.getIdPregunta());
            listaPreguntas.remove(pregunta); // Simulación de eliminación en UI
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Métodos helper para obtener nombres de IDs (necesitarás cargar estas listas)
    // private String obtenerNombreTipoPregunta(Integer id) { /* ... */ return "Tipo Placeholder"; }
    // private String obtenerNombreContenido(Integer id) { /* ... */ return "Tema Placeholder"; }
    // private String obtenerNombreNivel(Integer id) { /* ... */ return "Nivel Placeholder"; }
    // private String obtenerNombreVisibilidad(Integer id) { /* ... */ return "Visibilidad Placeholder"; }
}
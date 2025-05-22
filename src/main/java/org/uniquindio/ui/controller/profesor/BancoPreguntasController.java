package org.uniquindio.ui.controller.profesor;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.StringConverter;
import org.uniquindio.model.dto.PreguntaBancoDTO; // Usar el nuevo DTO
import org.uniquindio.model.entity.academico.*;
import org.uniquindio.model.entity.evaluacion.*;
import org.uniquindio.model.entity.catalogo.Nivel;
import org.uniquindio.model.entity.catalogo.TipoPregunta;
import org.uniquindio.model.entity.catalogo.Visibilidad;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.repository.impl.CatalogoRepositoryImpl;
import org.uniquindio.repository.impl.ContenidoRepositoryImpl;
import org.uniquindio.repository.impl.PreguntaRepositoryImpl;
import org.uniquindio.ui.controller.profesor.dialogs.CrearPreguntaDialogController;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BancoPreguntasController implements Initializable {

    @FXML private TextField txtBusquedaGeneral;
    @FXML private ComboBox<TipoPregunta> comboFiltroTipoPreguntaBanco;
    @FXML private ComboBox<Contenido> comboFiltroTemaBanco;
    // TODO: Añadir ComboBox para Nivel y Visibilidad si se desea filtrar por ellos
    // @FXML private ComboBox<Nivel> comboFiltroNivelBanco;
    // @FXML private ComboBox<Visibilidad> comboFiltroVisibilidadBanco;
    @FXML private Button btnAplicarFiltros;
    @FXML private TableView<PreguntaBancoDTO> tablaBancoPreguntas; // Cambiado a PreguntaBancoDTO
    @FXML private TableColumn<PreguntaBancoDTO, Integer> colIdPregunta;
    @FXML private TableColumn<PreguntaBancoDTO, String> colTextoPreguntaBanco;
    @FXML private TableColumn<PreguntaBancoDTO, String> colTipoPreguntaBanco;
    @FXML private TableColumn<PreguntaBancoDTO, String> colTemaBanco;
    @FXML private TableColumn<PreguntaBancoDTO, String> colNivelBanco;
    @FXML private TableColumn<PreguntaBancoDTO, String> colVisibilidadBanco;
    @FXML private TableColumn<PreguntaBancoDTO, Void> colAccionesBanco;
    @FXML private Button btnCrearNuevaPreguntaBanco;

    private ObservableList<PreguntaBancoDTO> listaPreguntasDTO = FXCollections.observableArrayList();
    private Profesor profesorLogueado;

    // Repositorios
    private PreguntaRepositoryImpl preguntaRepository = new PreguntaRepositoryImpl();
    private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    private ContenidoRepositoryImpl contenidoRepository = new ContenidoRepositoryImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablaBancoPreguntas();
        // Cargar filtros se hará cuando se setee el profesor
    }

    public void setProfesor(Profesor profesor) {
        this.profesorLogueado = profesor;
        cargarFiltrosBanco(); // Cargar filtros que pueden depender del profesor
        handleAplicarFiltros(null); // Cargar preguntas iniciales
    }

    private void configurarTablaBancoPreguntas() {
        colIdPregunta.setCellValueFactory(new PropertyValueFactory<>("idPregunta"));
        colTextoPreguntaBanco.setCellValueFactory(new PropertyValueFactory<>("texto"));
        colTipoPreguntaBanco.setCellValueFactory(new PropertyValueFactory<>("tipoPreguntaNombre"));
        colTemaBanco.setCellValueFactory(new PropertyValueFactory<>("contenidoNombre"));
        colNivelBanco.setCellValueFactory(new PropertyValueFactory<>("nivelNombre"));
        colVisibilidadBanco.setCellValueFactory(new PropertyValueFactory<>("visibilidadNombre"));

        colAccionesBanco.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.getStyleClass().add("button-editar-tabla-banco");
                btnEliminar.getStyleClass().add("button-eliminar-tabla-banco");

                btnEditar.setOnAction(event -> {
                    PreguntaBancoDTO dto = getTableView().getItems().get(getIndex());
                    // Para editar, necesitamos la entidad Pregunta completa, no solo el DTO de visualización.
                    // El DTO podría llevar el ID para recuperarla, o la función PL/SQL de búsqueda podría devolver más datos.
                    // Por ahora, asumimos que necesitamos recuperar la Pregunta original.
                    try {
                        Pregunta preguntaAEditar = preguntaRepository.obtenerPreguntaPorId(dto.getIdPregunta());
                        if (preguntaAEditar != null) {
                            handleEditarPregunta(preguntaAEditar);
                        } else {
                            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo encontrar la pregunta original para editar.");
                        }
                    } catch (SQLException e) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "Error al obtener detalles de la pregunta: " + e.getMessage());
                    }
                });
                btnEliminar.setOnAction(event -> {
                    PreguntaBancoDTO dto = getTableView().getItems().get(getIndex());
                    handleEliminarPregunta(dto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        tablaBancoPreguntas.setItems(listaPreguntasDTO);
        tablaBancoPreguntas.setPlaceholder(new Label("No hay preguntas para mostrar o que coincidan con los filtros."));
    }

    private void cargarFiltrosBanco() {
        if (profesorLogueado == null) return;

        try {
            // Cargar Tipos de Pregunta
            List<TipoPregunta> tipos = catalogoRepository.listarTiposPregunta();
            comboFiltroTipoPreguntaBanco.setItems(FXCollections.observableArrayList(tipos));
            // Añadir opción "Todos"
            comboFiltroTipoPreguntaBanco.getItems().add(0, null); // O un objeto TipoPregunta especial
            comboFiltroTipoPreguntaBanco.setConverter(new StringConverter<>() {
                @Override public String toString(TipoPregunta object) { return object == null ? "Todos los Tipos" : object.getNombre(); }
                @Override public TipoPregunta fromString(String string) { return null; }
            });


            // Cargar Temas/Contenidos (asumiendo que son generales o filtrados por profesor si es necesario)
            // Si los temas dependen de los cursos del profesor, la lógica de carga sería más compleja.
            // Por ahora, asumimos que se listan todos los contenidos o los que el profesor puede acceder.
            List<Contenido> temas = contenidoRepository.listarTodosLosContenidos(); // O un método específico
            comboFiltroTemaBanco.setItems(FXCollections.observableArrayList(temas));
            comboFiltroTemaBanco.getItems().add(0, null); // Opción "Todos"
            comboFiltroTemaBanco.setConverter(new StringConverter<>() {
                @Override public String toString(Contenido object) { return object == null ? "Todos los Temas" : object.getNombre(); }
                @Override public Contenido fromString(String string) { return null; }
            });

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los filtros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAplicarFiltros(ActionEvent event) {
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Información Requerida", "No se ha identificado al profesor.");
            return;
        }

        String texto = txtBusquedaGeneral.getText();
        Integer tipoId = comboFiltroTipoPreguntaBanco.getValue() != null ? comboFiltroTipoPreguntaBanco.getValue().getId() : null;
        Integer contenidoId = comboFiltroTemaBanco.getValue() != null ? comboFiltroTemaBanco.getValue().getIdContenido() : null;
        // Integer nivelId = comboFiltroNivelBanco.getValue() != null ? comboFiltroNivelBanco.getValue().getId() : null; // Si se añade filtro

        try {
            List<PreguntaBancoDTO> preguntasEncontradas = preguntaRepository.buscarPreguntasBancoDTO(
                    profesorLogueado.getCedula(),
                    texto,
                    tipoId,
                    contenidoId,
                    null // nivelId placeholder
                    // ,visibilidadId placeholder
            );
            listaPreguntasDTO.setAll(preguntasEncontradas);
            if (preguntasEncontradas.isEmpty()){
                tablaBancoPreguntas.setPlaceholder(new Label("No se encontraron preguntas con los filtros aplicados."));
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar las preguntas: " + e.getMessage());
            e.printStackTrace();
            listaPreguntasDTO.clear();
            tablaBancoPreguntas.setPlaceholder(new Label("Error al cargar preguntas."));
        }
    }

    @FXML
    private void handleCrearNuevaPreguntaBanco(ActionEvent event) {
        abrirDialogoCrearEditarPregunta(null);
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
                dialogController.setPreguntaParaEdicion(pregunta);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(pregunta == null ? "Crear Nueva Pregunta" : "Editar Pregunta - ID: " + pregunta.getIdPregunta());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // dialogStage.initOwner(btnCrearNuevaPreguntaBanco.getScene().getWindow()); // Opcional
            Scene scene = new Scene(parent);
            dialogStage.setScene(scene);
            dialogController.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            handleAplicarFiltros(null); // Refrescar la tabla

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el diálogo de creación/edición de pregunta.");
        }
    }

    private void handleEliminarPregunta(PreguntaBancoDTO preguntaDTO) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("Eliminar Pregunta ID: " + preguntaDTO.getIdPregunta());
        confirmacion.setContentText("¿Está seguro de que desea eliminar la pregunta: \"" + preguntaDTO.getTexto().substring(0, Math.min(preguntaDTO.getTexto().length(), 50)) + "...\"?\nEsta acción no se puede deshacer y podría afectar exámenes no presentados.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = preguntaRepository.eliminarPreguntaPorId(preguntaDTO.getIdPregunta());
                if (eliminado) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminación Exitosa", "La pregunta ha sido eliminada.");
                    handleAplicarFiltros(null); // Refrescar tabla
                } else {
                    // El PL/SQL podría no haber eliminado por restricciones (ej. en uso) sin lanzar excepción
                    mostrarAlerta(Alert.AlertType.WARNING, "Eliminación Parcial", "La pregunta no se eliminó. Puede estar en uso o ya fue eliminada.");
                }
            } catch (SQLException e) {
                // El PL/SQL debería idealmente lanzar una excepción específica si no se puede eliminar
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "Error al eliminar la pregunta: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

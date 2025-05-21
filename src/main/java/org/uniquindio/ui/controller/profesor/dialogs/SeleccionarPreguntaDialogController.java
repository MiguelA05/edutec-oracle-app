package org.uniquindio.ui.controller.profesor.dialogs;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.uniquindio.model.dto.PreguntaSeleccionDTO; // Nuevo DTO para esta tabla
import org.uniquindio.model.entity.academico.*;
import org.uniquindio.model.entity.evaluacion.*; // Entidad Pregunta
import org.uniquindio.model.entity.catalogo.Nivel;
import org.uniquindio.model.entity.catalogo.TipoPregunta;
import org.uniquindio.model.entity.usuario.Profesor;

// Importar repositorios
// import org.uniquindio.repository.impl.PreguntaRepositoryImpl;
// import org.uniquindio.repository.impl.CatalogoRepositoryImpl;
// import org.uniquindio.repository.impl.ContenidoRepositoryImpl;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SeleccionarPreguntaDialogController implements Initializable {

    @FXML private TextField txtBusquedaTexto;
    @FXML private ComboBox<TipoPregunta> comboFiltroTipoPregunta;
    @FXML private ComboBox<Nivel> comboFiltroNivel;
    @FXML private ComboBox<Contenido> comboFiltroContenido; // Para filtrar por el tema del examen actual
    @FXML private Button btnBuscarPreguntas;
    @FXML private TableView<PreguntaSeleccionDTO> tablaPreguntasDisponibles;
    @FXML private TableColumn<PreguntaSeleccionDTO, CheckBox> colSeleccionar;
    @FXML private TableColumn<PreguntaSeleccionDTO, String> colTextoPreguntaDisponible;
    @FXML private TableColumn<PreguntaSeleccionDTO, String> colTipoPreguntaDisponible;
    @FXML private TableColumn<PreguntaSeleccionDTO, String> colNivelDisponible;
    @FXML private TableColumn<PreguntaSeleccionDTO, Double> colPorcentajeExamen; // Para que el profesor ingrese el % aquí

    @FXML private Button btnAnadirSeleccionadas;
    @FXML private Button btnCancelarSeleccion;

    private Stage dialogStage;
    private Profesor profesorLogueado;
    private Contenido contenidoExamenActual; // El tema/contenido del examen que se está editando
    private ObservableList<PreguntaSeleccionDTO> preguntasDisponiblesList = FXCollections.observableArrayList();
    private List<PreguntaSeleccionDTO> preguntasSeleccionadas = new ArrayList<>();

    // Repositorios
    // private PreguntaRepositoryImpl preguntaRepository = new PreguntaRepositoryImpl();
    // private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    // private ContenidoRepositoryImpl contenidoRepository = new ContenidoRepositoryImpl();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarFiltros();
        // Cargar todas las preguntas relevantes inicialmente o después de la primera búsqueda
        // handleBuscarPreguntas(null);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setProfesorYContexto(Profesor profesor, Contenido contenidoExamen) {
        this.profesorLogueado = profesor;
        this.contenidoExamenActual = contenidoExamen;
        // Pre-seleccionar el filtro de contenido si se pasa el contexto del examen
        if (contenidoExamenActual != null) {
            comboFiltroContenido.setValue(contenidoExamenActual);
            comboFiltroContenido.setDisable(true); // Opcional: deshabilitar si el contexto es fijo
        }
        // Cargar preguntas iniciales basadas en el contexto
        handleBuscarPreguntas(null);
    }

    public List<PreguntaSeleccionDTO> getPreguntasSeleccionadas() {
        return preguntasSeleccionadas;
    }

    private void configurarTabla() {
        colSeleccionar.setCellValueFactory(cellData -> {
            PreguntaSeleccionDTO dto = cellData.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().bindBidirectional(dto.seleccionadoProperty());
            return new SimpleObjectProperty<>(checkBox);
        });
        colTextoPreguntaDisponible.setCellValueFactory(new PropertyValueFactory<>("textoPregunta"));
        colTipoPreguntaDisponible.setCellValueFactory(new PropertyValueFactory<>("tipoPreguntaNombre")); // Asumiendo que DTO tiene esto
        colNivelDisponible.setCellValueFactory(new PropertyValueFactory<>("nivelNombre")); // Asumiendo que DTO tiene esto

        colPorcentajeExamen.setCellValueFactory(new PropertyValueFactory<>("porcentajeEnExamen"));
        colPorcentajeExamen.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object == null ? "" : String.format("%.2f", object);
            }
            @Override
            public Double fromString(String string) {
                try {
                    if (string == null || string.isEmpty()) return 0.0;
                    double val = Double.parseDouble(string);
                    return Math.max(0.0, Math.min(100.0, val)); // Limitar entre 0 y 100
                } catch (NumberFormatException e) {
                    return 0.0; // O manejar el error de otra forma
                }
            }
        }));
        colPorcentajeExamen.setEditable(true); // Permitir edición directa en la tabla
        colPorcentajeExamen.setOnEditCommit(event -> {
            PreguntaSeleccionDTO dto = event.getRowValue();
            dto.setPorcentajeEnExamen(event.getNewValue());
        });


        tablaPreguntasDisponibles.setItems(preguntasDisponiblesList);
        tablaPreguntasDisponibles.setEditable(true); // Para permitir edición del porcentaje
    }

    private void cargarFiltros() {
        // TODO: Cargar datos para ComboBoxes de filtro desde la BD usando repositorios y PL/SQL
        // comboFiltroTipoPregunta.setItems(FXCollections.observableArrayList(catalogoRepository.listarTiposPreguntaConOpcionTodos()));
        // comboFiltroNivel.setItems(FXCollections.observableArrayList(catalogoRepository.listarNivelesConOpcionTodos()));
        // comboFiltroContenido.setItems(FXCollections.observableArrayList(contenidoRepository.listarContenidosActivosConOpcionTodos()));

        // Placeholder
        // comboFiltroTipoPregunta.getItems().add(null); // Para "Todos"
        // comboFiltroNivel.getItems().add(null);
        // comboFiltroContenido.getItems().add(null);
    }

    @FXML
    private void handleBuscarPreguntas(ActionEvent event) {
        // TODO: Implementar la lógica de búsqueda/filtrado
        // 1. Obtener los valores de los filtros (txtBusquedaTexto, combos)
        // String textoBusqueda = txtBusquedaTexto.getText();
        // TipoPregunta tipoFiltro = comboFiltroTipoPregunta.getValue();
        // Nivel nivelFiltro = comboFiltroNivel.getValue();
        // Contenido contenidoFiltro = (contenidoExamenActual != null) ? contenidoExamenActual : comboFiltroContenido.getValue();

        // 2. Llamar a un procedimiento/función PL/SQL (via Repositorio) que devuelva las preguntas filtradas.
        //    Esta función PL/SQL debería considerar:
        //    - El texto de búsqueda (LIKE '%texto%')
        //    - tipoPreguntaId, nivelId, contenidoId (si se seleccionaron)
        //    - visibilidadId (preguntas públicas + privadas del profesorLogueado)
        // List<Pregunta> preguntasEncontradas = preguntaRepository.buscarPreguntasFiltradas(
        //        profesorLogueado.getCedula(),
        //        textoBusqueda,
        //        tipoFiltro != null ? tipoFiltro.getId() : null,
        //        nivelFiltro != null ? nivelFiltro.getId() : null,
        //        contenidoFiltro != null ? contenidoFiltro.getId_contenido() : null
        // );

        // 3. Convertir List<Pregunta> a List<PreguntaSeleccionDTO>
        // preguntasDisponiblesList.clear();
        // if (preguntasEncontradas != null) {
        //     for (Pregunta p : preguntasEncontradas) {
        //         // Necesitarás obtener los nombres de TipoPregunta y Nivel si solo tienes IDs en Pregunta
        //         preguntasDisponiblesList.add(new PreguntaSeleccionDTO(p.getIdPregunta(), p.getTexto(), "Tipo Placeholder", "Nivel Placeholder", 10.0)); // 10.0 como % por defecto
        //     }
        // }

        // Placeholder
        preguntasDisponiblesList.clear();
        // preguntasDisponiblesList.add(new PreguntaSeleccionDTO(1L, "¿Qué es Java?", "Opción Múltiple", "Básico", 10.0));
        // preguntasDisponiblesList.add(new PreguntaSeleccionDTO(2L, "Explica la herencia.", "Respuesta Corta", "Intermedio", 15.0));
        // preguntasDisponiblesList.add(new PreguntaSeleccionDTO(3L, "2+2=4 (V/F)", "Verdadero/Falso", "Básico", 5.0));
        System.out.println("Búsqueda de preguntas (simulada). Implementar lógica real.");

    }

    @FXML
    private void handleAnadirSeleccionadas(ActionEvent event) {
        preguntasSeleccionadas.clear();
        for (PreguntaSeleccionDTO dto : preguntasDisponiblesList) {
            if (dto.isSeleccionado()) {
                if (dto.getPorcentajeEnExamen() <= 0) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Porcentaje Inválido", "La pregunta '" + dto.getTextoPregunta().substring(0, Math.min(30, dto.getTextoPregunta().length())) + "...' debe tener un porcentaje mayor a cero.");
                    return;
                }
                preguntasSeleccionadas.add(dto);
            }
        }

        if (preguntasSeleccionadas.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sin Selección", "No ha seleccionado ninguna pregunta para añadir.");
            return;
        }
        // Cerrar el diálogo y el CrearEditarExamenController recuperará las preguntas
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelarSeleccion(ActionEvent event) {
        preguntasSeleccionadas.clear(); // Limpiar por si acaso
        if (dialogStage != null) {
            dialogStage.close();
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
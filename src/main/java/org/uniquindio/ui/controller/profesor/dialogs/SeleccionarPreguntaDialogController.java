package org.uniquindio.ui.controller.profesor.dialogs;

import javafx.beans.property.*;
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
import lombok.Getter;
import lombok.Setter;
import org.uniquindio.model.dto.PreguntaBancoDTO;
import org.uniquindio.model.dto.PreguntaSeleccionDTO;
import org.uniquindio.model.entity.academico.*;
import org.uniquindio.model.entity.evaluacion.*;
import org.uniquindio.model.entity.catalogo.Nivel;
import org.uniquindio.model.entity.catalogo.TipoPregunta;
import org.uniquindio.model.entity.usuario.Profesor;
import org.uniquindio.repository.impl.CatalogoRepositoryImpl;
import org.uniquindio.repository.impl.ContenidoRepositoryImpl;
import org.uniquindio.repository.impl.PreguntaRepositoryImpl;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Setter
@Getter
public class SeleccionarPreguntaDialogController implements Initializable {

    @FXML private TextField txtBusquedaTexto;
    @FXML private ComboBox<TipoPregunta> comboFiltroTipoPregunta;
    @FXML private ComboBox<Nivel> comboFiltroNivel;
    @FXML private ComboBox<Contenido> comboFiltroContenido;
    @FXML private Button btnBuscarPreguntas;
    @FXML private TableView<PreguntaSeleccionDTO> tablaPreguntasDisponibles;
    @FXML private TableColumn<PreguntaSeleccionDTO, CheckBox> colSeleccionar;
    @FXML private TableColumn<PreguntaSeleccionDTO, String> colTextoPreguntaDisponible;
    @FXML private TableColumn<PreguntaSeleccionDTO, String> colTipoPreguntaDisponible;
    @FXML private TableColumn<PreguntaSeleccionDTO, String> colNivelDisponible;
    @FXML private TableColumn<PreguntaSeleccionDTO, Double> colPorcentajeExamen;

    @FXML private Button btnAnadirSeleccionadas;
    @FXML private Button btnCancelarSeleccion;

    private Stage dialogStage;
    private Profesor profesorLogueado;
    private Curso cursoExamenActual;
    private ObservableList<PreguntaSeleccionDTO> preguntasDisponiblesList = FXCollections.observableArrayList();
    private List<PreguntaSeleccionDTO> preguntasSeleccionadas = new ArrayList<>();

    private PreguntaRepositoryImpl preguntaRepository = new PreguntaRepositoryImpl();
    private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    private ContenidoRepositoryImpl contenidoRepository = new ContenidoRepositoryImpl();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        // La carga de filtros y preguntas se hará en setProfesorYContexto
    }

    /**
     * Inicializa el diálogo con el profesor y el curso del examen actual.
     * @param profesor El profesor que está usando el diálogo.
     * @param cursoExamen El curso para el cual se está creando/editando el examen.
     */
    public void setProfesorYContexto(Profesor profesor, Curso cursoExamen) {
        this.profesorLogueado = profesor;
        this.cursoExamenActual = cursoExamen;
        cargarFiltros();


        if (cursoExamenActual != null) {
            comboFiltroContenido.setDisable(false);
        } else {

            comboFiltroContenido.setDisable(true);
            comboFiltroContenido.setPromptText("Seleccione un curso primero en el examen");
        }
        handleBuscarPreguntas(null); // Cargar preguntas iniciales (o todas si no hay filtros)
    }

    private void configurarTabla() {
        colSeleccionar.setCellValueFactory(cellData -> {
            PreguntaSeleccionDTO dto = cellData.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().bindBidirectional(dto.seleccionadoProperty());
            return new SimpleObjectProperty<>(checkBox);
        });
        colTextoPreguntaDisponible.setCellValueFactory(new PropertyValueFactory<>("textoPregunta"));
        colTipoPreguntaDisponible.setCellValueFactory(new PropertyValueFactory<>("tipoPreguntaNombre"));
        colNivelDisponible.setCellValueFactory(new PropertyValueFactory<>("nivelNombre"));

        colPorcentajeExamen.setCellValueFactory(new PropertyValueFactory<>("porcentajeEnExamen"));
        colPorcentajeExamen.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object == null ? "10.00" : String.format("%.2f", object);
            }
            @Override
            public Double fromString(String string) {
                try {
                    if (string == null || string.trim().isEmpty()) return 10.0;
                    double val = Double.parseDouble(string.replace(",", "."));
                    return Math.max(0.01, Math.min(100.0, val));
                } catch (NumberFormatException e) {
                    return 10.0;
                }
            }
        }));
        colPorcentajeExamen.setEditable(true);
        colPorcentajeExamen.setOnEditCommit(event -> {
            PreguntaSeleccionDTO dto = event.getRowValue();
            dto.setPorcentajeEnExamen(event.getNewValue() != null ? event.getNewValue() : 10.0);
        });

        tablaPreguntasDisponibles.setItems(preguntasDisponiblesList);
        tablaPreguntasDisponibles.setEditable(true);
        tablaPreguntasDisponibles.setPlaceholder(new Label("No hay preguntas disponibles para los filtros seleccionados."));
    }

    private void cargarFiltros() {
        try {
            List<TipoPregunta> tipos = catalogoRepository.listarTiposPregunta();
            comboFiltroTipoPregunta.setItems(FXCollections.observableArrayList(tipos));
            comboFiltroTipoPregunta.getItems().add(0, null);
            comboFiltroTipoPregunta.setConverter(new StringConverter<>() {
                @Override public String toString(TipoPregunta object) { return object == null ? "Todos los Tipos" : object.getNombre(); }
                @Override public TipoPregunta fromString(String string) { return null; }
            });

            List<Nivel> niveles = catalogoRepository.listarNiveles();
            comboFiltroNivel.setItems(FXCollections.observableArrayList(niveles));
            comboFiltroNivel.getItems().add(0, null);
            comboFiltroNivel.setConverter(new StringConverter<>() {
                @Override public String toString(Nivel object) { return object == null ? "Todos los Niveles" : object.getNombre(); }
                @Override public Nivel fromString(String string) { return null; }
            });

            if (cursoExamenActual != null) {
                List<Contenido> temasDelCurso = contenidoRepository.listarContenidosPorCurso(cursoExamenActual.getIdCurso());
                comboFiltroContenido.setItems(FXCollections.observableArrayList(temasDelCurso));
                comboFiltroContenido.setDisable(temasDelCurso.isEmpty());
            } else {
                List<Contenido> todosLosTemas = contenidoRepository.listarTodosLosContenidos();
                comboFiltroContenido.setItems(FXCollections.observableArrayList(todosLosTemas));
                comboFiltroContenido.setDisable(todosLosTemas.isEmpty());
            }
            comboFiltroContenido.getItems().add(0, null);
            comboFiltroContenido.setConverter(new StringConverter<>() {
                @Override public String toString(Contenido object) {
                    if (object == null && cursoExamenActual != null) return "Todos los Temas (del Curso)";
                    if (object == null) return "Todos los Temas";
                    return object.getNombre();
                }
                @Override public Contenido fromString(String string) { return null; }
            });


        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los filtros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBuscarPreguntas(ActionEvent event) {
        if (profesorLogueado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Profesor no identificado.");
            return;
        }

        String texto = txtBusquedaTexto.getText().trim();
        Integer tipoId = (comboFiltroTipoPregunta.getValue() != null) ? comboFiltroTipoPregunta.getValue().getId() : null;
        Integer nivelId = (comboFiltroNivel.getValue() != null) ? comboFiltroNivel.getValue().getId() : null;
        Integer contenidoIdFiltro = (comboFiltroContenido.getValue() != null) ? comboFiltroContenido.getValue().getIdContenido() : null;

        // Si no se selecciona un contenido específico y hay un curso actual,
        // la función PL/SQL `buscarPreguntasBancoDTO` debería poder filtrar por todos los contenidos de ese curso.
        // Esto requeriría que `buscarPreguntasBancoDTO` acepte un `p_id_curso` también o que se pasen los IDs de contenido.
        // Por ahora, se pasa el contenidoIdFiltro seleccionado.
        // Si `contenidoIdFiltro` es null pero `cursoExamenActual` no, se podrían buscar todas las preguntas del curso.

        try {
            List<PreguntaBancoDTO> preguntasEncontradas = preguntaRepository.buscarPreguntasBancoDTO(
                    profesorLogueado.getCedula(),
                    texto.isEmpty() ? null : texto,
                    tipoId,
                    contenidoIdFiltro, // Si es null, PL/SQL podría buscar en todos los contenidos del curso o todos los públicos.
                    nivelId
                    // ,p_id_curso_contexto => cursoExamenActual != null ? cursoExamenActual.getId_curso() : null // Opcional para el PL/SQL
            );
            List<PreguntaSeleccionDTO> preguntasSeleccionDTOList = preguntasEncontradas.stream()
                    .map(preguntaBanco -> new PreguntaSeleccionDTO(
                            preguntaBanco.getIdPregunta(),
                            preguntaBanco.getTexto(),
                            preguntaBanco.getTipoPreguntaNombre(),
                            preguntaBanco.getNivelNombre(),
                            false,
                            10.0
                    ))
                    .collect(Collectors.toList());

            preguntasDisponiblesList.setAll(preguntasSeleccionDTOList);
            if (preguntasEncontradas.isEmpty()){
                tablaPreguntasDisponibles.setPlaceholder(new Label("No se encontraron preguntas con los filtros aplicados."));
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar las preguntas: " + e.getMessage());
            e.printStackTrace();
            preguntasDisponiblesList.clear();
            tablaPreguntasDisponibles.setPlaceholder(new Label("Error al cargar preguntas."));
        }
    }

    @FXML
    private void handleAnadirSeleccionadas(ActionEvent event) {
        preguntasSeleccionadas.clear();
        preguntasDisponiblesList.stream()
                .filter(PreguntaSeleccionDTO::isSeleccionado)
                .forEach(dto -> {
                    if (dto.getPorcentajeEnExamen() <= 0) {
                        // Marcar o notificar error, pero continuar recolectando para una sola alerta
                    }
                    preguntasSeleccionadas.add(dto);
                });

        boolean hayPorcentajeInvalido = preguntasSeleccionadas.stream().anyMatch(dto -> dto.getPorcentajeEnExamen() <= 0);
        if (hayPorcentajeInvalido) {
            mostrarAlerta(Alert.AlertType.ERROR, "Porcentaje Inválido", "Todas las preguntas seleccionadas deben tener un porcentaje mayor a cero.");
            return;
        }

        if (preguntasSeleccionadas.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sin Selección", "No ha seleccionado ninguna pregunta para añadir.");
            return;
        }

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
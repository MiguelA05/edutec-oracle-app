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
    private Curso cursoExamenActual; // Curso para el cual se está creando/editando el examen
    private ObservableList<PreguntaSeleccionDTO> preguntasDisponiblesList = FXCollections.observableArrayList();
    private List<PreguntaSeleccionDTO> preguntasSeleccionadas = new ArrayList<>();

    private PreguntaRepositoryImpl preguntaRepository = new PreguntaRepositoryImpl();
    private CatalogoRepositoryImpl catalogoRepository = new CatalogoRepositoryImpl();
    private ContenidoRepositoryImpl contenidoRepository = new ContenidoRepositoryImpl();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
    }

    public void setProfesorYContexto(Profesor profesor, Curso cursoExamen) {
        this.profesorLogueado = profesor;
        this.cursoExamenActual = cursoExamen;
        cargarFiltros();
        handleBuscarPreguntas(null); // Cargar preguntas iniciales
    }

    private void configurarTabla() {
        colSeleccionar.setCellValueFactory(cellData -> {
            PreguntaSeleccionDTO dto = cellData.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().bindBidirectional(dto.seleccionadoProperty());
            // Deshabilitar selección si es una subpregunta (idPreguntaPadre != null)
            // Esto se hará al poblar la lista, ya que el DTO no tiene idPreguntaPadre directamente.
            // Se filtrarán las preguntas en handleBuscarPreguntas.
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
        tablaPreguntasDisponibles.setPlaceholder(new Label("No hay preguntas principales disponibles para los filtros seleccionados."));
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

            ObservableList<Contenido> contenidosParaCombo = FXCollections.observableArrayList();
            contenidosParaCombo.add(null);

            if (cursoExamenActual != null) {
                List<Contenido> temasDelCurso = contenidoRepository.listarContenidosPorCurso(cursoExamenActual.getIdCurso());
                contenidosParaCombo.addAll(temasDelCurso);
                comboFiltroContenido.setDisable(temasDelCurso.isEmpty() && contenidosParaCombo.size() <=1 );
                comboFiltroContenido.setPromptText("Seleccione un tema del curso");
            } else {
                List<Contenido> todosLosTemas = contenidoRepository.listarTodosLosContenidos();
                contenidosParaCombo.addAll(todosLosTemas);
                comboFiltroContenido.setDisable(todosLosTemas.isEmpty() && contenidosParaCombo.size() <=1);
                comboFiltroContenido.setPromptText("Seleccione un tema");
            }
            comboFiltroContenido.setItems(contenidosParaCombo);
            comboFiltroContenido.setConverter(new StringConverter<>() {
                @Override public String toString(Contenido object) {
                    if (object == null) {
                        return cursoExamenActual != null ? "Todos los Temas (del Curso)" : "Todos los Temas";
                    }
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
        if (cursoExamenActual == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Contexto Faltante", "No se ha especificado un curso para el examen.");
            tablaPreguntasDisponibles.setPlaceholder(new Label("Seleccione un curso en la pantalla del examen para buscar preguntas."));
            preguntasDisponiblesList.clear();
            return;
        }

        String texto = txtBusquedaTexto.getText().trim();
        Integer tipoId = (comboFiltroTipoPregunta.getValue() != null) ? comboFiltroTipoPregunta.getValue().getId() : null;
        Integer nivelId = (comboFiltroNivel.getValue() != null) ? comboFiltroNivel.getValue().getId() : null;
        Contenido contenidoSeleccionado = comboFiltroContenido.getValue();
        Integer contenidoIdFiltro = (contenidoSeleccionado != null) ? contenidoSeleccionado.getIdContenido() : null;
        Integer cursoIdParaFiltrarContextual = (contenidoIdFiltro == null && cursoExamenActual != null) ? cursoExamenActual.getIdCurso() : null;

        try {
            List<PreguntaBancoDTO> preguntasEncontradas = preguntaRepository.buscarPreguntasBancoDTO(
                    profesorLogueado.getCedula(),
                    texto.isEmpty() ? null : texto,
                    tipoId,
                    contenidoIdFiltro,
                    nivelId,
                    cursoIdParaFiltrarContextual
            );

            // Filtrar para mostrar solo preguntas principales (PREGUNTA_PADRE IS NULL)
            List<PreguntaSeleccionDTO> preguntasPrincipalesDTOList = preguntasEncontradas.stream()
                    .filter(pbDto -> pbDto.getIdPreguntaPadre() == null) // Solo preguntas principales
                    .map(preguntaBanco -> new PreguntaSeleccionDTO(
                            preguntaBanco.getIdPregunta(),
                            preguntaBanco.getTexto(),
                            preguntaBanco.getTipoPreguntaNombre(),
                            preguntaBanco.getNivelNombre(),
                            false,
                            preguntaBanco.getPorcentajeDefecto() != null ? preguntaBanco.getPorcentajeDefecto().doubleValue() : 10.0
                    ))
                    .collect(Collectors.toList());

            preguntasDisponiblesList.setAll(preguntasPrincipalesDTOList);
            if (preguntasPrincipalesDTOList.isEmpty()){
                tablaPreguntasDisponibles.setPlaceholder(new Label("No se encontraron preguntas principales con los filtros aplicados para el curso actual."));
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
                    preguntasSeleccionadas.add(dto);
                });

        boolean hayPorcentajeInvalido = preguntasSeleccionadas.stream()
                .anyMatch(dto -> dto.getPorcentajeEnExamen() <= 0 || dto.getPorcentajeEnExamen() > 100);
        if (hayPorcentajeInvalido) {
            mostrarAlerta(Alert.AlertType.ERROR, "Porcentaje Inválido", "Todas las preguntas seleccionadas deben tener un porcentaje entre 0.01 y 100.00.");
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
        preguntasSeleccionadas.clear();
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

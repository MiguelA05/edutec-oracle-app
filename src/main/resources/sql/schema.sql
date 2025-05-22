-- ==========================================================================
-- SCRIPT CREACIÓN - BASE DE DATOS "EDUTEC"
-- Fecha: Mayo 2025
-- Autores originales: Juan M. Isaza, Miguel A. Mira, Santiago Quintero
-- ==========================================================================

-- --------------------------------------------------------
-- 0. ELIMINACIÓN DE OBJETOS Y TABLESPACES EXISTENTES
-- --------------------------------------------------------
-- Este bloque intenta eliminar las tablas en orden inverso de dependencia
-- y luego los tablespaces.
-- NOTA: Requiere privilegios DBA o similares.
DECLARE
    v_count NUMBER;
    v_sql_stmt VARCHAR2(1000);
    v_default_temp_ts VARCHAR2(128); -- Aumentado tamaño por si acaso
    v_temp_ts_to_drop VARCHAR2(128) := 'TEMP_EDU';
    v_aux_temp_ts VARCHAR2(128) := 'TEMP_EDU_AUX';
    v_original_default_temp_ts VARCHAR2(128); -- Para restaurar si es necesario

    PROCEDURE drop_object(p_obj_type IN VARCHAR2, p_obj_name IN VARCHAR2) IS
        v_obj_count NUMBER;
        v_user VARCHAR2(128);
    BEGIN
        SELECT SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') INTO v_user FROM DUAL;
        IF p_obj_type = 'TABLE' THEN
            SELECT COUNT(*) INTO v_obj_count FROM all_tables WHERE table_name = UPPER(p_obj_name) AND owner = v_user;
        ELSE
            DBMS_OUTPUT.PUT_LINE('Tipo de objeto ' || p_obj_type || ' no soportado para drop_object en esta versión.');
            RETURN;
        END IF;

        IF v_obj_count > 0 THEN
            DBMS_OUTPUT.PUT_LINE('Intentando eliminar ' || p_obj_type || ' ' || v_user || '.' || UPPER(p_obj_name) || '...');
            v_sql_stmt := 'DROP ' || p_obj_type || ' ' || v_user || '.' || UPPER(p_obj_name) || ' CASCADE CONSTRAINTS';
            BEGIN
                EXECUTE IMMEDIATE v_sql_stmt;
                DBMS_OUTPUT.PUT_LINE(p_obj_type || ' ' || v_user || '.' || UPPER(p_obj_name) || ' eliminado exitosamente.');
            EXCEPTION
                WHEN OTHERS THEN
                    DBMS_OUTPUT.PUT_LINE('Error al eliminar ' || p_obj_type || ' ' || v_user || '.' || UPPER(p_obj_name) || ': ' || SQLCODE || ' - ' || SQLERRM);
            END;
        ELSE
            DBMS_OUTPUT.PUT_LINE(p_obj_type || ' ' || v_user || '.' || UPPER(p_obj_name) || ' no encontrado, no se requiere eliminación.');
        END IF;
    END drop_object;

    PROCEDURE drop_ts(p_ts_name IN VARCHAR2) IS
        v_ts_exists NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_ts_exists FROM dba_tablespaces WHERE tablespace_name = UPPER(p_ts_name);
        IF v_ts_exists > 0 THEN
            DBMS_OUTPUT.PUT_LINE('Intentando eliminar tablespace ' || UPPER(p_ts_name) || '...');
            v_sql_stmt := 'DROP TABLESPACE ' || UPPER(p_ts_name) || ' INCLUDING CONTENTS AND DATAFILES';
            BEGIN
                EXECUTE IMMEDIATE v_sql_stmt;
                DBMS_OUTPUT.PUT_LINE('Tablespace ' || UPPER(p_ts_name) || ' eliminado exitosamente.');
            EXCEPTION
                WHEN OTHERS THEN
                    DBMS_OUTPUT.PUT_LINE('Error al ejecutar DROP TABLESPACE ' || UPPER(p_ts_name) || ': ' || SQLCODE || ' - ' || SQLERRM);
                -- No relanzar, permitir que el script continúe intentando crear.
            END;
        ELSE
            DBMS_OUTPUT.PUT_LINE('Tablespace ' || UPPER(p_ts_name) || ' no encontrado, no se requiere eliminación.');
        END IF;
    END drop_ts;

BEGIN
    DBMS_OUTPUT.ENABLE(buffer_size => NULL);

    DBMS_OUTPUT.PUT_LINE('--- Iniciando limpieza de tablas (orden inverso de dependencia) ---');
    drop_object('TABLE', 'RespuestaPregunta');
    drop_object('TABLE', 'PreguntaExamenEstudiante');
    drop_object('TABLE', 'PresentacionExamen');
    drop_object('TABLE', 'DetallePreguntaExamen');
    drop_object('TABLE', 'Examen');
    drop_object('TABLE', 'OpcionPregunta');
    drop_object('TABLE', 'Pregunta');
    drop_object('TABLE', 'DetalleCursoHorario');
    drop_object('TABLE', 'DetalleEstudianteCurso');
    drop_object('TABLE', 'Contenido');
    drop_object('TABLE', 'Unidad');
    drop_object('TABLE', 'Horario_Clase');
    drop_object('TABLE', 'Curso');
    drop_object('TABLE', 'Categoria');
    drop_object('TABLE', 'Creacion');
    drop_object('TABLE', 'Visibilidad');
    drop_object('TABLE', 'Nivel');
    drop_object('TABLE', 'TipoPregunta');
    drop_object('TABLE', 'Dia_Semana');
    drop_object('TABLE', 'Profesor');
    drop_object('TABLE', 'Estudiante');
    DBMS_OUTPUT.PUT_LINE('--- Limpieza de tablas completada ---');

    DBMS_OUTPUT.PUT_LINE('--- Iniciando limpieza de tablespaces ---');
    -- Guardar el default temporary tablespace actual
    SELECT property_value INTO v_original_default_temp_ts FROM database_properties WHERE property_name = 'DEFAULT_TEMP_TABLESPACE';
    DBMS_OUTPUT.PUT_LINE('El tablespace temporal por defecto actual es: ' || v_original_default_temp_ts);

    -- Si el tablespace a eliminar es el default temporary, cambiarlo a uno auxiliar
    IF UPPER(v_original_default_temp_ts) = UPPER(v_temp_ts_to_drop) THEN
        DBMS_OUTPUT.PUT_LINE(v_temp_ts_to_drop || ' es el tablespace temporal por defecto. Se requiere un auxiliar.');
        BEGIN
            SELECT COUNT(*) INTO v_count FROM dba_tablespaces WHERE tablespace_name = UPPER(v_aux_temp_ts);
            IF v_count = 0 THEN
                DBMS_OUTPUT.PUT_LINE('Creando tablespace temporal auxiliar ' || v_aux_temp_ts || '...');
                -- !! AJUSTA ESTA RUTA SI ES NECESARIO PARA TU SISTEMA !!
                EXECUTE IMMEDIATE 'CREATE TEMPORARY TABLESPACE ' || v_aux_temp_ts || ' TEMPFILE ''C:\oracleXE\oradata\EDUTEC\' || LOWER(v_aux_temp_ts) || '_01.dbf'' SIZE 10M AUTOEXTEND ON NEXT 1M MAXSIZE 50M';
                DBMS_OUTPUT.PUT_LINE(v_aux_temp_ts || ' creado.');
            ELSE
                DBMS_OUTPUT.PUT_LINE(v_aux_temp_ts || ' ya existe. Usándolo como auxiliar.');
            END IF;

            DBMS_OUTPUT.PUT_LINE('Estableciendo ' || v_aux_temp_ts || ' como tablespace temporal por defecto...');
            EXECUTE IMMEDIATE 'ALTER DATABASE DEFAULT TEMPORARY TABLESPACE ' || v_aux_temp_ts;
            DBMS_OUTPUT.PUT_LINE(v_aux_temp_ts || ' es ahora el default temporal.');
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Error configurando ' || v_aux_temp_ts || ': ' || SQLCODE || ' - ' || SQLERRM);
                DBMS_OUTPUT.PUT_LINE('No se podrá eliminar ' || v_temp_ts_to_drop || ' si sigue siendo el default.');
        END;
    END IF;

    -- Intentar eliminar el tablespace temporal principal (TEMP_EDU)
    drop_ts(v_temp_ts_to_drop);

    -- Eliminar otros tablespaces permanentes
    drop_ts('TS_INDICES');
    drop_ts('TS_HISTORICO');
    drop_ts('TS_EVALUACIONES');
    drop_ts('TS_USUARIOS');
    drop_ts('TS_ACADEMICO');

    -- Si el default original era TEMP_EDU y se cambió a TEMP_EDU_AUX,
    -- y TEMP_EDU se va a recrear y volver a ser default más adelante en el script,
    -- podríamos eliminar TEMP_EDU_AUX aquí DESPUÉS de que TEMP_EDU sea recreado y establecido como default.
    -- Por seguridad, si TEMP_EDU_AUX se usó, se deja. El script lo volverá a establecer a TEMP_EDU.

    DBMS_OUTPUT.PUT_LINE('--- Limpieza de tablespaces completada ---');
    DBMS_OUTPUT.PUT_LINE('Bloque de limpieza PL/SQL completado.');

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error GRAVE en el bloque de limpieza PL/SQL: ' || SQLCODE || ' - ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('Es posible que deba realizar limpieza manual de tablespaces y/o tablas.');
END;
/

-- --------------------------------------------------------
-- 1. CREACIÓN DE TABLESPACES
-- --------------------------------------------------------
-- !! IMPORTANTE !!
-- !! ASEGÚRATE DE QUE LA RUTA 'C:\oracleXE\oradata\EDUTEC\' (O LA QUE CORRESPONDA A TU SISTEMA) EXISTA !!
-- !! Y ORACLE TENGA PERMISOS DE ESCRITURA ANTES DE EJECUTAR ESTO.                                  !!
-- !! SI LA RUTA NO EXISTE, ESTAS SENTENCIAS FALLARÁN.                               !!
-- --------------------------------------------------------

-- Tablespace temporal
CREATE TEMPORARY TABLESPACE temp_edu
    TEMPFILE 'C:\oracleXE\oradata\EDUTEC\temp_edu_01.dbf' -- Ajusta esta ruta si es diferente
    SIZE 20M
    AUTOEXTEND ON NEXT 2M MAXSIZE 200M
    EXTENT MANAGEMENT LOCAL;

-- Establece temp_edu como el tablespace temporal por defecto
ALTER DATABASE DEFAULT TEMPORARY TABLESPACE temp_edu;

-- Tablespace para datos académicos
CREATE TABLESPACE TS_ACADEMICO
    DATAFILE 'C:\oracleXE\oradata\EDUTEC\academico_01.dbf' -- Ajusta esta ruta
    SIZE 5M
    AUTOEXTEND ON NEXT 5M MAXSIZE 500M
    EXTENT MANAGEMENT LOCAL
    SEGMENT SPACE MANAGEMENT AUTO;

-- Tablespace para información de usuarios
CREATE TABLESPACE TS_USUARIOS
    DATAFILE 'C:\oracleXE\oradata\EDUTEC\usuarios_01.dbf' -- Ajusta esta ruta
    SIZE 5M
    AUTOEXTEND ON NEXT 1M MAXSIZE 50M
    EXTENT MANAGEMENT LOCAL
    SEGMENT SPACE MANAGEMENT AUTO;

-- Tablespace para evaluaciones
CREATE TABLESPACE TS_EVALUACIONES
    DATAFILE 'C:\oracleXE\oradata\EDUTEC\evaluaciones_01.dbf' -- Ajusta esta ruta
    SIZE 5M
    AUTOEXTEND ON NEXT 1M MAXSIZE 100M
    EXTENT MANAGEMENT LOCAL
    SEGMENT SPACE MANAGEMENT AUTO;

-- Tablespace para datos históricos
CREATE TABLESPACE TS_HISTORICO
    DATAFILE 'C:\oracleXE\oradata\EDUTEC\historico_01.dbf' -- Ajusta esta ruta
    SIZE 12M
    AUTOEXTEND ON NEXT 2M MAXSIZE 200M
    EXTENT MANAGEMENT LOCAL
    SEGMENT SPACE MANAGEMENT AUTO;

-- Tablespace específico para los índices
CREATE TABLESPACE TS_INDICES
    DATAFILE 'C:\oracleXE\oradata\EDUTEC\indices_01.dbf' -- Ajusta esta ruta
    SIZE 10M
    AUTOEXTEND ON NEXT 1M MAXSIZE 100M
    EXTENT MANAGEMENT LOCAL
    SEGMENT SPACE MANAGEMENT AUTO;

-- --------------------------------------------------------
-- 2. CREACIÓN DE TABLAS
-- --------------------------------------------------------
-- (Las definiciones de las tablas son las mismas que en la v3, ya que se crearon correctamente)

-- === TABLESPACE: TS_USUARIOS ===
CREATE TABLE Estudiante (
                            cedula NUMBER(10),
                            nombre VARCHAR2(30) NOT NULL,
                            correo VARCHAR2(30) NOT NULL,
                            contrasena VARCHAR2(20) NOT NULL,
                            CONSTRAINT pk_estudiante PRIMARY KEY (cedula) USING INDEX TABLESPACE TS_INDICES
) TABLESPACE TS_USUARIOS;

CREATE TABLE Profesor (
                          cedula NUMBER(10),
                          nombre VARCHAR2(30) NOT NULL,
                          correo VARCHAR2(30) NOT NULL,
                          contrasena VARCHAR2(20) NOT NULL,
                          CONSTRAINT pk_profesor PRIMARY KEY (cedula) USING INDEX TABLESPACE TS_INDICES
) TABLESPACE TS_USUARIOS;

-- === TABLESPACE: TS_ACADEMICO ===
CREATE TABLE Dia_Semana (
                            id INTEGER,
                            nombre VARCHAR2(20) NOT NULL,
                            descripcion VARCHAR2(100),
                            CONSTRAINT pk_dia_semana PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES
) TABLESPACE TS_ACADEMICO;

-- === TABLESPACE: TS_EVALUACIONES ===
CREATE TABLE TipoPregunta (
                              id INTEGER,
                              nombre VARCHAR2(30) NOT NULL,
                              descripcion VARCHAR2(100),
                              CONSTRAINT pk_tipo_pregunta PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES
) TABLESPACE TS_EVALUACIONES;

CREATE TABLE Nivel (
                       id INTEGER,
                       nombre VARCHAR2(30) NOT NULL,
                       descripcion VARCHAR2(100),
                       CONSTRAINT pk_nivel PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES
) TABLESPACE TS_EVALUACIONES;

CREATE TABLE Visibilidad (
                             id INTEGER,
                             nombre VARCHAR2(30) NOT NULL,
                             descripcion VARCHAR2(100),
                             CONSTRAINT pk_visibilidad PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES
) TABLESPACE TS_EVALUACIONES;

CREATE TABLE Creacion (
                          id INTEGER,
                          nombre VARCHAR2(20) NOT NULL,
                          descripcion VARCHAR2(100),
                          CONSTRAINT pk_creacion PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES
) TABLESPACE TS_EVALUACIONES;

CREATE TABLE Categoria (
                           id INTEGER,
                           nombre VARCHAR2(20) NOT NULL,
                           descripcion VARCHAR2(100),
                           CONSTRAINT pk_categoria PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES
) TABLESPACE TS_EVALUACIONES;

-- === TABLESPACE: TS_ACADEMICO ===
CREATE TABLE Curso (
                       id_curso INTEGER,
                       nombre VARCHAR2(20) NOT NULL,
                       descripcion VARCHAR2(100),
                       profesor_cedula NUMBER(10) NOT NULL,
                       CONSTRAINT pk_curso PRIMARY KEY (id_curso) USING INDEX TABLESPACE TS_INDICES,
                       CONSTRAINT fk_profesor FOREIGN KEY (profesor_cedula) REFERENCES Profesor(cedula)
) TABLESPACE TS_ACADEMICO;

CREATE TABLE Horario_Clase (
                               id_horario INTEGER,
                               hora_inicio DATE NOT NULL,
                               hora_fin DATE NOT NULL,
                               dia_semana_id INTEGER NOT NULL,
                               CONSTRAINT pk_horario_clase PRIMARY KEY (id_horario) USING INDEX TABLESPACE TS_INDICES,
                               CONSTRAINT fk_dia_semana FOREIGN KEY (dia_semana_id) REFERENCES Dia_Semana(id)
) TABLESPACE TS_ACADEMICO;

CREATE TABLE Unidad (
                        id_unidad INTEGER,
                        nombre VARCHAR2(20) NOT NULL,
                        id_curso INTEGER NOT NULL,
                        CONSTRAINT pk_unidad PRIMARY KEY (id_unidad) USING INDEX TABLESPACE TS_INDICES,
                        CONSTRAINT fk_unidad_curso FOREIGN KEY (id_curso) REFERENCES Curso(id_curso)
) TABLESPACE TS_ACADEMICO;

CREATE TABLE Contenido (
                           id_contenido INTEGER,
                           nombre VARCHAR2(20) NOT NULL,
                           id_unidad INTEGER NOT NULL,
                           CONSTRAINT pk_contenido PRIMARY KEY (id_contenido) USING INDEX TABLESPACE TS_INDICES,
                           CONSTRAINT fk_contenido_unidad FOREIGN KEY (id_unidad) REFERENCES Unidad(id_unidad)
) TABLESPACE TS_ACADEMICO;

CREATE TABLE DetalleEstudianteCurso (
                                        estudiante_cedula NUMBER(10) NOT NULL,
                                        curso_id INTEGER NOT NULL,
                                        CONSTRAINT pk_detalle_estudiante_curso PRIMARY KEY (estudiante_cedula, curso_id) USING INDEX TABLESPACE TS_INDICES,
                                        CONSTRAINT fk_detalle_estudiante FOREIGN KEY (estudiante_cedula) REFERENCES Estudiante(cedula),
                                        CONSTRAINT fk_detalle_curso FOREIGN KEY (curso_id) REFERENCES Curso(id_curso)
) TABLESPACE TS_ACADEMICO;

CREATE TABLE DetalleCursoHorario (
                                     curso_id INTEGER NOT NULL,
                                     horario_clase_id INTEGER NOT NULL,
                                     CONSTRAINT pk_detalle_curso_horario PRIMARY KEY (curso_id, horario_clase_id) USING INDEX TABLESPACE TS_INDICES, -- Nombre de PK actualizado
                                     CONSTRAINT fk_dch_curso FOREIGN KEY (curso_id) REFERENCES Curso(id_curso), -- Nombre de FK actualizado
                                     CONSTRAINT fk_dch_horario FOREIGN KEY (horario_clase_id) REFERENCES Horario_Clase(id_horario) -- Nombre de FK actualizado
) TABLESPACE TS_ACADEMICO;

-- === TABLESPACE: TS_EVALUACIONES ===
CREATE TABLE Pregunta (
                          id_pregunta        INTEGER,
                          texto              VARCHAR2(300) NOT NULL,
                          tiempo_estimado    NUMBER,
                          porcentaje         NUMBER(5,2),
                          tipo_pregunta_id   INTEGER,
                          visibilidad_id     INTEGER,
                          nivel_id           INTEGER,
                          pregunta_padre     INTEGER,
                          contenido_id       INTEGER,
                          creador_cedula_profesor NUMBER(10),
                          fecha_creacion     DATE DEFAULT SYSDATE,
                          fecha_modificacion  DATE,
                          usuario_modificacion VARCHAR2(30),
                          CONSTRAINT pk_pregunta PRIMARY KEY (id_pregunta) USING INDEX TABLESPACE TS_INDICES,
                          CONSTRAINT fk_tipo_pregunta FOREIGN KEY (tipo_pregunta_id) REFERENCES TipoPregunta(id),
                          CONSTRAINT fk_visibilidad FOREIGN KEY (visibilidad_id) REFERENCES Visibilidad(id),
                          CONSTRAINT fk_nivel FOREIGN KEY (nivel_id) REFERENCES Nivel(id),
                          CONSTRAINT fk_pregunta_padre FOREIGN KEY (pregunta_padre) REFERENCES Pregunta(id_pregunta),
                          CONSTRAINT fk_contenido FOREIGN KEY (contenido_id) REFERENCES Contenido(id_contenido),
                          CONSTRAINT fk_pregunta_profesor_creador FOREIGN KEY (creador_cedula_profesor) REFERENCES Profesor(cedula)
) TABLESPACE TS_EVALUACIONES;

CREATE TABLE OpcionPregunta (
                                id INTEGER,
                                respuesta VARCHAR2(255) NOT NULL,
                                es_correcta CHAR(1) NOT NULL CHECK (es_correcta IN ('S', 'N')),
                                pregunta_id INTEGER NOT NULL,
                                CONSTRAINT pk_opcion_pregunta PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES,
                                CONSTRAINT fk_pregunta_opcion FOREIGN KEY (pregunta_id) REFERENCES Pregunta(id_pregunta)
) TABLESPACE TS_EVALUACIONES;

CREATE TABLE Examen (
                        id INTEGER,
                        nombre VARCHAR2(50) NOT NULL,
                        tiempo NUMBER,
                        numero_preguntas NUMBER(10),
                        fecha DATE,
                        hora DATE,
                        calificacion_min_aprobatoria NUMBER(5,2),
                        peso_curso NUMBER(5,2),
                        descripcion VARCHAR2(100),
                        creacion_id INTEGER,
                        categoria_id INTEGER,
                        curso_id INTEGER,
                        CONSTRAINT pk_examen PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES,
                        CONSTRAINT fk_examen_creacion FOREIGN KEY (creacion_id) REFERENCES Creacion(id),
                        CONSTRAINT fk_examen_categoria FOREIGN KEY (categoria_id) REFERENCES Categoria(id),
                        CONSTRAINT fk_examen_curso FOREIGN KEY (curso_id) REFERENCES Curso(id_curso)
) TABLESPACE TS_EVALUACIONES;

CREATE TABLE DetallePreguntaExamen (
                                       examen_id INTEGER,
                                       pregunta_id INTEGER,
                                       porcentaje NUMBER(5,2),
                                       CONSTRAINT pk_detalle_pregunta_examen PRIMARY KEY (examen_id, pregunta_id) USING INDEX TABLESPACE TS_INDICES,
                                       CONSTRAINT fk_detallepex_examen FOREIGN KEY (examen_id) REFERENCES Examen(id),
                                       CONSTRAINT fk_detallepex_pregunta FOREIGN KEY (pregunta_id) REFERENCES Pregunta(id_pregunta)
) TABLESPACE TS_EVALUACIONES;

-- === TABLESPACE: TS_HISTORICO ===
CREATE TABLE PresentacionExamen (
                                    id INTEGER,
                                    calificacion NUMBER(5,2),
                                    respuestas_correctas NUMBER(10),
                                    respuestas_incorrectas NUMBER(10),
                                    tiempo NUMBER,
                                    hora_inicio DATE,
                                    hora_fin DATE,
                                    direccion_ip VARCHAR2(45),
                                    examen_id INTEGER,
                                    estudiante_cedula NUMBER(10),
                                    CONSTRAINT pk_presentacion_examen PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES,
                                    CONSTRAINT fk_presentex_examen FOREIGN KEY (examen_id) REFERENCES Examen(id),
                                    CONSTRAINT fk_presentex_estudiante FOREIGN KEY (estudiante_cedula) REFERENCES Estudiante(cedula)
) TABLESPACE TS_HISTORICO;

CREATE TABLE PreguntaExamenEstudiante (
                                          id INTEGER,
                                          tiempo NUMBER,
                                          presentacion_examen_id INTEGER,
                                          detalle_pregunta_examen_examen_id INTEGER,
                                          detalle_pregunta_examen_pregunta_id INTEGER,
                                          CONSTRAINT pk_pregunta_examen_estudiante PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES,
                                          CONSTRAINT fk_pexes_presentacion_examen FOREIGN KEY (presentacion_examen_id) REFERENCES PresentacionExamen(id),
                                          CONSTRAINT fk_pexes_detalle_examen_pregunta FOREIGN KEY (detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id)
                                              REFERENCES DetallePreguntaExamen(examen_id, pregunta_id)
) TABLESPACE TS_HISTORICO;

CREATE TABLE RespuestaPregunta (
                                   id NUMBER,
                                   respuesta_dada VARCHAR2(100),
                                   opcion_pregunta_id INTEGER,
                                   preguntaExamenEstudiante_id INTEGER NOT NULL,
                                   CONSTRAINT pk_respuesta_pregunta PRIMARY KEY (id) USING INDEX TABLESPACE TS_INDICES,
                                   CONSTRAINT fk_resp_opcion_pregunta FOREIGN KEY (opcion_pregunta_id) REFERENCES OpcionPregunta(id),
                                   CONSTRAINT fk_resp_pregunta_examen_est FOREIGN KEY (preguntaExamenEstudiante_id) REFERENCES PreguntaExamenEstudiante(id)
) TABLESPACE TS_HISTORICO;

COMMIT;

BEGIN
    DBMS_OUTPUT.PUT_LINE('Script de creación de EDUTEC ejecutado.');
END;
/

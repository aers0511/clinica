-- Base de datos para el proyecto Clinica
-- Compatible con las entidades JPA del proyecto (MySQL 8.x).
-- Este script NO elimina bases de datos ni tablas existentes.

CREATE DATABASE IF NOT EXISTS laboratorio
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE laboratorio;

CREATE TABLE IF NOT EXISTS tblClientes (
    id INT NOT NULL AUTO_INCREMENT,
    nombreCompleto VARCHAR(255) NOT NULL,
    fechaNacimiento DATE NOT NULL,
    sexo VARCHAR(255) NOT NULL,
    tipoSangre VARCHAR(255) NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblDoctores (
    id INT NOT NULL AUTO_INCREMENT,
    nombreCompleto VARCHAR(255) NOT NULL,
    sexo VARCHAR(255) NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblEmpresas (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    rfc VARCHAR(255) NULL,
    direccion VARCHAR(255) NULL,
    telefono VARCHAR(255) NULL,
    cedula VARCHAR(255) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblTiposMuestra (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblEstudios (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    notaDescriptiva VARCHAR(255) NULL,
    idTipoMuestra INT NOT NULL,
    PRIMARY KEY (id),
    activo boolean,
    INDEX idx_estudios_tipo_muestra (idTipoMuestra),
    CONSTRAINT fk_estudios_tipo_muestra
        FOREIGN KEY (idTipoMuestra)
        REFERENCES tblTiposMuestra(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblParametros (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    orden INT NOT NULL,
    notaDescriptiva VARCHAR(255) NULL,
    unidadMedida VARCHAR(255) NULL,
    rangoInicial DECIMAL(12,4) NOT NULL,
    rangoFinal DECIMAL(12,4) NOT NULL,
    edadInicial INT NULL,
    edadFinal INT NULL,
    idEstudio INT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_parametros_estudio (idEstudio),
    CONSTRAINT fk_parametros_estudio
        FOREIGN KEY (idEstudio)
        REFERENCES tblEstudios(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblPruebas (
    id INT NOT NULL AUTO_INCREMENT,
    folio VARCHAR(255) NOT NULL,
    fechaHora DATETIME NOT NULL,
    idCliente INT NOT NULL,
    idDoctor INT NULL,
    activo boolean,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pruebas_folio (folio),
    INDEX idx_pruebas_cliente (idCliente),
    INDEX idx_pruebas_doctor (idDoctor),
    CONSTRAINT fk_pruebas_cliente
        FOREIGN KEY (idCliente)
        REFERENCES tblClientes(id),
    CONSTRAINT fk_pruebas_doctor
        FOREIGN KEY (idDoctor)
        REFERENCES tblDoctores(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblPruebaEstudios (
    id INT NOT NULL AUTO_INCREMENT,
    idPrueba INT NOT NULL,
    idEstudio INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_prueba_estudio (idPrueba, idEstudio),
    INDEX idx_pruebaestudios_estudio (idEstudio),
    CONSTRAINT fk_pruebaestudios_prueba
        FOREIGN KEY (idPrueba)
        REFERENCES tblPruebas(id),
    CONSTRAINT fk_pruebaestudios_estudio
        FOREIGN KEY (idEstudio)
        REFERENCES tblEstudios(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblResultados (
    id INT NOT NULL AUTO_INCREMENT,
    idPrueba INT NOT NULL,
    idParametro INT NOT NULL,
    valor DECIMAL(12,4) NOT NULL,
    observacion VARCHAR(255) NULL,
    rangoInicial DECIMAL(12,4) NOT NULL,
    rangoFinal DECIMAL(12,4) NOT NULL,
    unidadMedida VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_resultado_prueba_parametro (idPrueba, idParametro),
    INDEX idx_resultados_parametro (idParametro),
    CONSTRAINT fk_resultados_prueba
        FOREIGN KEY (idPrueba)
        REFERENCES tblPruebas(id),
    CONSTRAINT fk_resultados_parametro
        FOREIGN KEY (idParametro)
        REFERENCES tblParametros(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblUsuarios (
    id INT NOT NULL AUTO_INCREMENT,
    usuario VARCHAR(255) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_usuario (usuario)
) ENGINE=InnoDB;

-- Verificación rápida de las tablas creadas:
SHOW TABLES;

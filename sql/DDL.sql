SET NAMES utf8;

CREATE TABLE PERSONA (
	id_persona INTEGER AUTO_INCREMENT,
	nombres VARCHAR(60) NOT NULL,
	apellidos VARCHAR(60) NOT NULL,
	tipo_doc SMALLINT NOT NULL,
	nro_doc BIGINT NOT NULL,
	fecha_nacimiento DATE NOT NULL,
	sexo CHAR(1) NOT NULL,
	domicilio VARCHAR(120),
	telefono VARCHAR(16),
	descripcion TEXT,
	estado CHAR(1) NOT NULL,
	
	UNIQUE (tipo_doc,nro_doc),
	PRIMARY KEY (id_persona)
);

CREATE TABLE USUARIO (
	id_usuario INTEGER NOT NULL AUTO_INCREMENT,
	id_persona INTEGER,
	nombre_usuario VARCHAR(30) NOT NULL,
	password VARCHAR(32) NOT NULL,
	email VARCHAR(40) NOT NULL,
	descripcion TEXT,
	estado CHAR(1) NOT NULL, -- PODRIA SER DEL TIPO: A=ACTIVO, B=BAJA, S=SUSPENDIDO, ETC
	tipo CHAR(1), -- ESTE SE USA AUTOMATICO PARA LA HERENCIA 

	PRIMARY KEY (id_usuario),
	UNIQUE (nombre_usuario),
	UNIQUE (email),
	FOREIGN KEY (id_persona) REFERENCES PERSONA (id_persona)
);

CREATE TABLE CLIENTE(
	id_usuario Integer NOT NULL,
	puntos Integer NOT NULL DEFAULT 0,
	reputacion Integer NOT NULL DEFAULT 3,
	foto_registro VARCHAR(120),
	foto VARCHAR(120),
    saldo float NOT NULL DEFAULT 5,

	PRIMARY KEY(id_usuario),
	FOREIGN KEY (id_usuario) REFERENCES USUARIO (id_usuario)

);

CREATE TABLE ROL (
	id_rol INTEGER NOT NULL AUTO_INCREMENT,
	nombre_rol VARCHAR(30) NOT NULL,
	nombre_amigable VARCHAR(30) NOT NULL,
	descripcion TEXT,
	estado CHAR(1) NOT NULL,

	PRIMARY KEY (id_rol),
	UNIQUE (nombre_rol)
);

CREATE TABLE USUARIO_ROL (
	id_usuario INTEGER NOT NULL,
	id_rol INTEGER NOT NULL,
	fecha_modificacion DATETIME NOT NULL,
	
	PRIMARY KEY (id_usuario, id_rol),
	FOREIGN KEY (id_usuario) REFERENCES USUARIO (id_usuario),
	FOREIGN KEY (id_rol) REFERENCES ROL (id_rol)
);


CREATE TABLE PERMISO (
	id_permiso INTEGER AUTO_INCREMENT,
	nombre_permiso VARCHAR (50) NOT NULL,
	funcionalidad VARCHAR(200),
	descripcion TEXT,
	estado CHAR(1) NOT NULL,

	PRIMARY KEY (id_permiso),
	UNIQUE (nombre_permiso)
);

CREATE TABLE PERMISO_ROL (
	id_permiso INTEGER NOT NULL,
	id_rol INTEGER NOT NULL,
	fecha_modificacion DATETIME NOT NULL,

	PRIMARY KEY(id_permiso,id_rol),
	FOREIGN KEY (id_permiso) REFERENCES PERMISO(id_permiso),
	FOREIGN KEY (id_rol) REFERENCES ROL (id_rol)

);
CREATE TABLE LOCALIDAD (
	id_localidad INTEGER NOT NULL,
	nombre VARCHAR (200),
	nombre_ascii VARCHAR (200),
	nombres_alternativos VARCHAR(10000),
	lat FLOAT,
	lng FLOAT,
	clasificacion CHAR(1),
	clase_punto VARCHAR(10),
	pais  CHAR(2),
	paises_alternativos VARCHAR(200),
	admin1 VARCHAR(20),
	admin2 VARCHAR(80),
	admin3 VARCHAR(20),
	admin4 VARCHAR(20),
	poblacion BIGINT,
	elevacion INTEGER,
	dem INTEGER,
	zona_horaria VARCHAR(40),
	fecha_modificacion DATE,


	PRIMARY KEY (id_localidad)
);

CREATE TABLE LOCALIDAD_CLASIFICACION (
	codigo CHAR(1),
	nombre_clase VARCHAR(50),

	PRIMARY KEY (codigo)
);

CREATE TABLE VEHICULO (
	id_vehiculo INTEGER NOT NULL AUTO_INCREMENT,
	anio INTEGER NOT NULL,
	marca VARCHAR(30) NOT NULL,
	modelo VARCHAR(30) NOT NULL,
	patente VARCHAR(15) NOT NULL,
	verificado CHAR(1) NOT NULL,
	estado CHAR(1) NOT NULL,
	fecha_verificacion DATETIME,
	color VARCHAR(20),
    cantidad_asientos INTEGER NOT NULL,
    aire_acondicionado CHAR(1),
    seguro CHAR(1) NOT NULL,
    foto VARCHAR(120),
    
	PRIMARY KEY (id_vehiculo),
	UNIQUE (patente)
);

CREATE TABLE MANEJA (
	id_cliente INTEGER NOT NULL,
	id_vehiculo INTEGER NOT NULL,
	fecha_inicio DATETIME NOT NULL,
	fecha_fin DATETIME,
	

	PRIMARY KEY (id_cliente,id_vehiculo,fecha_inicio),
	FOREIGN KEY (id_cliente) REFERENCES CLIENTE (id_usuario) ON DELETE CASCADE,
	FOREIGN KEY (id_vehiculo) REFERENCES VEHICULO (id_vehiculo) ON DELETE CASCADE
);

CREATE TABLE ESTADO_VIAJE(
	id_estado_viaje CHAR(1) NOT NULL,
	nombre_estado VARCHAR(30) NOT NULL,
	
	PRIMARY KEY (id_estado_viaje)
);

CREATE TABLE VIAJE (
	id_viaje INTEGER NOT NULL AUTO_INCREMENT,
	nombre_amigable VARCHAR(30),
	asientos_disponibles INTEGER NOT NULL,
	estado CHAR(1) NOT NULL,
	fecha_inicio DATETIME NOT NULL,
	fecha_alta DATETIME NOT NULL,
	fecha_finalizacion DATETIME,
	fecha_cancelacion DATETIME,
	id_vehiculo INTEGER NOT NULL,
    id_cliente INTEGER NOT NULL,
    fecha_inicio_maneja DATETIME NOT NULL,
    viaje_complementario INTEGER,
	precio FLOAT,
	
	PRIMARY KEY (id_viaje),
	FOREIGN KEY (id_cliente, id_vehiculo, fecha_inicio_maneja) REFERENCES MANEJA (id_cliente, id_vehiculo, fecha_inicio) ON DELETE CASCADE,
    FOREIGN KEY (viaje_complementario) REFERENCES VIAJE (id_viaje) ON DELETE CASCADE,
	FOREIGN KEY (estado) REFERENCES ESTADO_VIAJE(id_estado_viaje) ON DELETE CASCADE
);


CREATE TABLE LOCALIDAD_VIAJE (
	id_viaje INTEGER NOT NULL,
	id_localidad INTEGER NOT NULL,
	cantidad_pasajeros INTEGER NOT NULL,
    kms_a_localidad_siguiente long NOT NULL,
    ordinal INTEGER NOT NULL,

	unique(id_viaje,id_localidad),
	PRIMARY KEY (id_viaje, id_localidad),
	FOREIGN KEY (id_viaje) REFERENCES VIAJE (id_viaje) ON DELETE CASCADE,
	FOREIGN KEY (id_localidad) REFERENCES LOCALIDAD (id_localidad) ON DELETE CASCADE
);

CREATE TABLE COMISION (
	id_comision Int(11) NOT NULL AUTO_INCREMENT,
    limite_inferior INTEGER NOT NULL,
    limite_superior INTEGER NOT NULL,
    precio FLOAT NOT NULL,
    fecha_inicio DATE NOT NULL ,
    fecha_fin DATE default NULL,
    -- precio_comision int(11) NOT NULL,
    
    PRIMARY KEY (id_comision)
    -- FOREIGN KEY (precio_comision) REFERENCES precio_comision (id_comision) ON DELETE CASCADE
);

CREATE TABLE COMISION_COBRADA (
	id_comision_cobrada INTEGER AUTO_INCREMENT,
	monto DECIMAL (10, 2) NOT NULL,
    estado CHAR(1),
	id_comision INTEGER,
	id_movimiento_saldo INTEGER,
	id_pasajero_viaje INTEGER,
	fecha DATETIME,
	PRIMARY KEY (id_comision_cobrada)
);

CREATE TABLE ESTADO_COMISION(
	id_estado_comision CHAR(1) NOT NULL,
	nombre_estado VARCHAR(30) NOT NULL,
	
	PRIMARY KEY (id_estado_comision)
);

CREATE TABLE PASAJERO_VIAJE (
	id_pasajero_viaje INTEGER AUTO_INCREMENT,
	id_viaje INTEGER NOT NULL,
	id_cliente INTEGER NOT NULL,
	kilometros FLOAT,
	estado CHAR(1) NOT NULL,
	id_calificacion INTEGER ,
	id_comision_cobrada INTEGER NOT NULL,
	id_localidad_subida INTEGER NOT NULL,
	id_localidad_bajada INTEGER NOT NULL,
	nro_asientos INTEGER NOT NULL DEFAULT 1,
    
	PRIMARY KEY (id_pasajero_viaje),
    UNIQUE(id_viaje,id_cliente),
	FOREIGN KEY (id_viaje) REFERENCES VIAJE (id_viaje) ON DELETE CASCADE,
	FOREIGN KEY (id_cliente) REFERENCES CLIENTE (id_usuario) ON DELETE CASCADE,
	FOREIGN KEY (id_comision_cobrada) REFERENCES COMISION_COBRADA (id_comision_cobrada) ON DELETE CASCADE,
	FOREIGN KEY (id_viaje, id_localidad_subida) REFERENCES LOCALIDAD_VIAJE (id_viaje, id_localidad)ON DELETE CASCADE,
	FOREIGN KEY (id_viaje, id_localidad_bajada) REFERENCES LOCALIDAD_VIAJE (id_viaje, id_localidad) ON DELETE CASCADE
);

CREATE TABLE TIPO_MOV_PUNTOS(
	id_tipo_mov_puntos int(11) NOT NULL AUTO_INCREMENT,
	descripcion varchar(255) NOT NULL,
    PRIMARY KEY (id_tipo_mov_puntos)
);

CREATE TABLE MOVIMIENTO_PUNTOS (
  ID_MOVIMIENTOS_PUNTOS int(11) NOT NULL AUTO_INCREMENT,
  FECHA date NOT NULL,
  MONTO int(11) NOT NULL,
  ID_CLIENTE int(11) DEFAULT NULL,
  TIPO int(11) NOT NULL,
  
  PRIMARY KEY (ID_MOVIMIENTOS_PUNTOS),
 
  FOREIGN KEY (ID_CLIENTE) REFERENCES USUARIO (ID_USUARIO) ON DELETE CASCADE,
  FOREIGN KEY (tipo) REFERENCES tipo_mov_puntos (id_tipo_mov_puntos) ON DELETE CASCADE
);

CREATE TABLE CALIFICACION (
	id_calificacion INTEGER NOT NULL AUTO_INCREMENT ,
	id_pasajero_viaje INTEGER NOT NULL,
    id_conductor INTEGER NOT NULL,
	calificacion_para_conductor INTEGER,
	calificacion_para_pasajero INTEGER,
	participo_conductor CHAR(1),
    participo_pasajero CHAR(1),
    comentario_conductor text,
    comentario_pasajero text,
	id_movimiento_puntos_chofer INTEGER,
    id_movimiento_puntos_pasajero INTEGER,

	UNIQUE(id_pasajero_viaje,id_conductor),
	PRIMARY KEY (id_calificacion),
    FOREIGN KEY (id_pasajero_viaje) REFERENCES pasajero_viaje (id_pasajero_viaje)  ON DELETE CASCADE,
    FOREIGN KEY (id_conductor) REFERENCES CLIENTE (id_usuario)  ON DELETE CASCADE,
	FOREIGN KEY (id_movimiento_puntos_chofer) REFERENCES movimiento_puntos (ID_MOVIMIENTOS_PUNTOS) ON DELETE SET NULL,
	FOREIGN KEY (id_movimiento_puntos_pasajero) REFERENCES movimiento_puntos (ID_MOVIMIENTOS_PUNTOS)  ON DELETE SET NULL
);

CREATE TABLE NOTIFICACION(
	ID_NOTIFICACION INTEGER NOT NULL AUTO_INCREMENT,
	FECHA DATETIME NOT NULL,
	TEXTO TEXT NOT NULL,
	LINK VARCHAR(200),
	ESTADO CHAR(1) NOT NULL,
	ID_CLIENTE INTEGER NOT NULL,

	primary key(ID_NOTIFICACION),
	FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_USUARIO) ON DELETE CASCADE
);
  
  CREATE TABLE TIPO_SANCION (
  ID_TIPO_SANCION int(11) NOT NULL AUTO_INCREMENT,
  DESCRIPCION varchar(255) NOT NULL,
  DIAS_SANCION int(11) NOT NULL,
  
  PRIMARY KEY (ID_TIPO_SANCION)
);

CREATE TABLE SANCION (
  ID_SANCION int(11) NOT NULL AUTO_INCREMENT,
  ESTADO int(11) NOT NULL,
  FECHA_FIN date NOT NULL,
  FECHA_INICIO date NOT NULL,
  ID_CLIENTE int(11) DEFAULT NULL,
  ID_MOVIMIENTO_PUNTOS int(11) DEFAULT NULL,
  ID_TIPO_SANCION int(11) DEFAULT NULL,
  
  PRIMARY KEY (ID_SANCION),
  
  FOREIGN KEY (ID_CLIENTE) REFERENCES USUARIO (ID_USUARIO) ON DELETE CASCADE,
  FOREIGN KEY (ID_MOVIMIENTO_PUNTOS) REFERENCES MOVIMIENTO_PUNTOS (ID_MOVIMIENTOS_PUNTOS) ON DELETE CASCADE,
  FOREIGN KEY (ID_TIPO_SANCION) REFERENCES TIPO_SANCION (ID_TIPO_SANCION) ON DELETE CASCADE
 );

CREATE TABLE PAGO(
	id_pago int(11) not null auto_increment,
    fecha date not null,
    monto float not null,
    id_cliente int(11) not null,
    
    
    PRIMARY KEY (id_pago),
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_usuario) ON DELETE CASCADE
   
);

CREATE TABLE TIPO_MOV_SALDO(
	id_tipo_mov_saldo int(11) NOT NULL AUTO_INCREMENT,
	descripcion varchar(255) NOT NULL,
    PRIMARY KEY (id_tipo_mov_saldo)
);


CREATE TABLE MOVIMIENTO_SALDO(
	id_movimiento_saldo int(11) NOT NULL AUTO_INCREMENT,
    fecha date NOT NULL,
    monto FLOAT NOT NULL,
    id_comision_cobrada int(11) default null,
    id_pago int(11) default null,
    tipo int(11) NOT NULL,
    id_cliente int(11) not null,
    PRIMARY KEY(id_movimiento_saldo),
    FOREIGN KEY (id_comision_cobrada) REFERENCES comision_cobrada (id_comision_cobrada) ON DELETE CASCADE,
    FOREIGN KEY (id_pago) REFERENCES pago (id_pago) ON DELETE CASCADE,
    FOREIGN KEY (tipo) REFERENCES tipo_mov_saldo (id_tipo_mov_saldo) ON DELETE CASCADE,
	FOREIGN KEY (id_cliente) REFERENCES cliente (id_usuario)
);

CREATE TABLE COMENTARIO_VIAJE(
	id_comentario_viaje int(11) NOT NULL AUTO_INCREMENT,
    fecha datetime NOT NULL,
    id_cliente int(11) NOT NULL,
    id_viaje int(11) default null,
    texto TEXT,
    
    PRIMARY KEY(id_comentario_viaje),
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_viaje) REFERENCES viaje (id_viaje) ON DELETE CASCADE
);

CREATE TABLE SEGUIDOR_VIAJE(
	id_seguidor_viaje int(11) NOT NULL AUTO_INCREMENT,
    fecha datetime NOT NULL,
    id_cliente int(11) NOT NULL,
    id_viaje int(11) default null,
    estado char(1) NOT NULL,
    
    PRIMARY KEY(id_seguidor_viaje),
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_viaje) REFERENCES viaje (id_viaje) ON DELETE CASCADE
);
SET NAMES utf8;
INSERT INTO ROL (id_rol, nombre_rol, nombre_amigable, descripcion, estado) VALUES (1, "super_usuario", "Super Usuario", "Este usuario tiene todos los privilegios", "A");
INSERT INTO ROL (id_rol, nombre_rol, nombre_amigable, descripcion, estado) VALUES (2, "cliente", "Cliente", "Este usuario es cliente", "A");

INSERT INTO PERSONA (id_persona, nombres, apellidos, tipo_doc, nro_doc, fecha_nacimiento, sexo, domicilio, telefono, descripcion, estado) VALUES (1, "Admin", "Admin", "1", "33111311", "1992-09-25", "O", "Lavalle, 123", "011-15-422123", NULL, "A");
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (NULL, 1, "administrador", "adminpass0", "admin@localhost", "Usuario administrador", "A","U");

-- CREO LAS PERSONAS DE LOS USUARIOS
-- persona 2 Jasmin
INSERT INTO PERSONA (id_persona, nombres, apellidos, tipo_doc, nro_doc, fecha_nacimiento, sexo, domicilio, telefono, descripcion, estado) VALUES (2, "Jasmin", "Paolino", "1", "36718455", "1992-10-28", "F", "Pastorini, 2470, La Reja", "011-15-33922364", NULL, "A");
-- persona 3 Luz
INSERT INTO PERSONA (id_persona, nombres, apellidos, tipo_doc, nro_doc, fecha_nacimiento, sexo, domicilio, telefono, descripcion, estado) VALUES (3, "Luz", "Bárcena", "1", "36600140", "1991-08-24", "F", "15, 773, Luján", "02227-15-488021", NULL, "A");
-- persona 4 fede
INSERT INTO PERSONA (id_persona, nombres, apellidos, tipo_doc, nro_doc, fecha_nacimiento, sexo, domicilio, telefono, descripcion, estado) VALUES (4, "Federico", "Retamal", "1", "35525238", "1991-02-05", "M", "Sarmiento, 1153, General Rodriguez", "11-66071845", NULL, "A");
-- persona 5 lucas
INSERT INTO PERSONA (id_persona, nombres, apellidos, tipo_doc, nro_doc, fecha_nacimiento, sexo, domicilio, telefono, descripcion, estado) VALUES (5, "Lucas", "Mufato", "1", "35942784", "1992-04-11", "M", "Dr. Muñiz, 1482, Luján", "02323-15-606525", NULL, "A");
-- persona 6 Juan
INSERT INTO PERSONA (id_persona, nombres, apellidos, tipo_doc, nro_doc, fecha_nacimiento, sexo, domicilio, telefono, descripcion, estado) VALUES (6, "Juan", "Cardona", "1", "36072559", "1992-09-23", "M", "25 de mayo, 1168, Luján", "02323-15-609065", NULL, "A");
-- persona 7 Pablo
INSERT INTO PERSONA (id_persona, nombres, apellidos, tipo_doc, nro_doc, fecha_nacimiento, sexo, domicilio, telefono, descripcion, estado) VALUES (7, "Pablo", "Cabrera", "1", "94727190", "1989-11-29", "M", "Pueyrredon 237, Los Cardales", "011-15-69150213", NULL, "A");

-- CREO UNOS USUARIOS PARA HACER PRUEBAS EN EL SISTEMA Y QUE ANDEN PARA TODOS

-- usuario 2 Jasmin
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (2,2,"jasminp","jasmin123", "jasmin-p@hotmail.es",NUll,"A","C");
-- usuario 3 Luz
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (3,3,"luzbarcena","barcena", "mluzbarcena@gmail.com",NUll,"A","C");
-- usuario 4 fede
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (4,4,"federicoretamal","retamal", "reta.atlas@gmail.com",NUll,"A","C");
-- usuario 5 Lucas
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (5,5,"lucasmufato","mufato", "l.mufato@gmail.com",NUll,"A","C");
-- usuario 6 Juan
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (6,6,"juancardona","cardona", "juancruz.23@hotmail.com",NUll,"A","C");
-- usuario 7 Pablo
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (7,7,"pablocabrera","cabrera", "binchmod777@gmail.com",NUll,"A","C");

-- este metodo le da al user administrador el rol super_usuario
INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (1, 1, CURRENT_DATE);

-- LES ASIGNO A LOS USUARIO Q CREO PARA LAS PRUEBAS EL ROL DE CLIENTE
-- Jasmin
INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (2, 2, CURRENT_DATE);
-- Luz
INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (3, 2, CURRENT_DATE);
-- Fede
INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (4, 2, CURRENT_DATE);
-- lucas
INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (5, 2, CURRENT_DATE);
-- juan
INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (6, 2, CURRENT_DATE);
-- pablo
INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (7, 2, CURRENT_DATE);

-- LOS CREO COMO CLIENTES A ESTOS MISMOS USUARIOS
-- Jasmin
INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo) VALUES (2,default,default,NULL,NULL,1000);
-- Luz
INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo) VALUES (3,default,default,NULL,NULL,1000);
-- Fede
INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo) VALUES (4,default,default,NULL,NULL,1000);
-- lucas
INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo) VALUES (5,default,default,NULL,NULL,1000);
-- juan
INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo) VALUES (6,default,default,NULL,NULL,1000);
-- pablo
INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo) VALUES (7,default,default,NULL,NULL,1000);

-- agrego permisos de funcional

INSERT INTO PERMISO (id_permiso, nombre_permiso, funcionalidad,descripcion,estado ) VALUES
	(1, "administrar_usuarios","Administrar usuarios","Permite Alta/Baja/Modificacion de usuarios","A"),
	(2, "generar_reportes", "Generar reportes", "Permite generar reportes", "A"),

	(3, "loguearse","loguearse","permite ingresar al sistema","A"),
	(4, "acceder_perfil", "Acceder a perfil de usuario", "Permite acceder a perfil de usuario", "A"),
	(5, "modificar_perfil", "Modificar datos de perfil", "Permite modificar perfil de usuario", "A"),
	(6, "desactivar_cuenta", "Desactivar cuenta de usuario", "Permite desactivar la cuenta de un usuario", "A"),

	(7, "crear_viajes","Crear viajes","Permite crear viajes","A"),
	(8, "buscar_viajes","Buscar viajes","Permite buscar viajes","A"),
	(9, "eliminar_viajes", "Eliminar viajes", "Permite eliminar un viaje", "A"),
	(10, "acceder_mis_viajes", "Acceder a 'Mis viajes'", "Permite acceder a listado 'Mis viajes'", "A"),
	(11, "ver_detalle_viajes", "Ver detalle de viajes", "Permite ver detalles de un viaje", "A"),
	(12, "participar_viajes","Participar viajes","Permite participar en viajes","A"),
	(13, "cancelar_viajes","Cancelar viaje","Permite cancelar un viaje","A"),

	(14, "calificar","Calificar","Permite calificar usuarios","A"),

	(15, "crear_vehiculos","Crear vehiculos","Permite crear vehiculos","A"),
	(16, "ver_vehiculos", "Ver vehiculos", "Permite ver informacion de un vehiculo", "A"),
	(17, "modificar_vehiculos", "Modificar vehiculos", "Permite modificar datos de un vehiculo", "A"),
	(18, "asignar_conductores","Asignar conductores","Permite asignar conductores a un vehiculo","A"),
	(19, "desasignar_conductores","Desasignar conductores","Permite desasignar conductores a un vehiculo","A"),

	(20, "cargar_saldo", "Cargar saldo", "Permite cargar saldo", "A"),
	(21, "acceder_mis_vehiculos", "Acceder a 'Mis vehiculos'", "Permite acceder a listado 'Mis vehiculos'", "A"),

	(22, "administrar_comisiones", "Administrar comisiones", "Permite Alta/Baja/Modificacion de comisiones", "A"),
	(23, "acceder_puntos", "Acceder a 'puntos'", "Permite ver puntos actuales y los movimientos", "A"),
	(24, "acceder_ayuda", "Acceder a 'ayuda'", "Permite ver el manual de ayuda para el usuario", "A"),
	(25, "acceder_notificaciones", "Acceder a 'notificaciones'", "Permite ver las notificaciones del usuario", "A");

	
INSERT INTO PERMISO_ROL (id_permiso, id_rol, fecha_modificacion) VALUES
	(1,1,CURRENT_DATE),
	(2,1,CURRENT_DATE),
	(3,2,CURRENT_DATE),
	(4,2,CURRENT_DATE),
	(4,1,CURRENT_DATE),
	(5,2,CURRENT_DATE),
	(6,2,CURRENT_DATE),
	(7,2,CURRENT_DATE),
	(8,2,CURRENT_DATE),
	(9,2,CURRENT_DATE),
	(10,2,CURRENT_DATE),
	(11,2,CURRENT_DATE),
	(12,2,CURRENT_DATE),
	(13,2,CURRENT_DATE),
	(14,2,CURRENT_DATE),
	(15,2,CURRENT_DATE),
	(16,2,CURRENT_DATE),
	(17,2,CURRENT_DATE),
	(18,2,CURRENT_DATE),
	(19,2,CURRENT_DATE),
	(20,2,CURRENT_DATE),
	(21,2,CURRENT_DATE),
	(22,1,CURRENT_DATE),
	(23,2,CURRENT_DATE),
	(24,2,CURRENT_DATE),
	(25,2,CURRENT_DATE);

INSERT INTO LOCALIDAD_CLASIFICACION(codigo, nombre_clase) VALUES
	("A", "Ciudad/Estado/Región"),
	("H", "Flujo de agua/Lago"),
	("L", "Parque/Area"),
	("P", "Ciudad/Pueblo"),
	("R", "Calle/Camino/Ruta"),
	("S", "Espacio/Edificio/Granja"),
	("T", "Montaña/Cerro"),
	("U", "Bajo el agua"),
	("V", "Bosque/Arbustos");

INSERT INTO ESTADO_VIAJE(id_estado_viaje, nombre_estado) VALUES
	("0", "Iniciado"),
	("1", "No iniciado"),
	("2", "Finalizado"),
	("3", "Cancelado");
	
INSERT INTO ESTADO_COMISION(id_estado_comision, nombre_estado) VALUES
	("0", "Pendiente"),
	("1", "Debitado"),
	("2", "Vencido"),
	("3", "Informativa"),
	("4", "Desestimada");

-- Para crear viajes:
-- INSERT INTO PERSONA (id_persona, nombres, apellidos, tipo_doc, nro_doc, fecha_nacimiento, sexo, domicilio, telefono, descripcion, estado)
-- VALUES (NULL, "Juan", "Cardona", "1", "36072559", "1992-09-23", "M", "25 de mayo, 1168", "2323-15-609065", "Hayq ue ponerle personas a los usuarios o se rompe el pobrecito perfil, sepan entender", "A");
-- USUARIO Nro 5:
-- INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo)
-- VALUES (NULL,2,"pablocabrera","cabrera", "cabrera@hotmail.com","usuario para participar en viajes","A","C");
-- INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (5, 2, CURRENT_DATE);

-- INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo)
-- VALUES (5,default,default,null,null,100000);
-- INSERT INTO VEHICULO (id_vehiculo, anio, marca, modelo, patente, verificado, estado, fecha_verificacion, color, cantidad_asientos, aire_acondicionado, seguro, foto)
-- VALUES (NULL, '1992', 'Ford', 'Focus', 'abc123', 'N', 'A', NULL, '000000', '5', 'N', 'N', NULL);
-- INSERT INTO VEHICULO (id_vehiculo, anio, marca, modelo, patente, verificado, estado, fecha_verificacion, color, cantidad_asientos, aire_acondicionado, seguro, foto)
-- VALUES (NULL, '1980', 'Ford', 'Falcon', 'abd124', 'S', 'A', '2016-05-18', 'FF1122', '4', 'S', 'S', NULL);
-- INSERT INTO MANEJA (id_cliente, id_vehiculo, fecha_inicio, fecha_fin)
-- VALUES ('5', '1', '2016-05-16', NULL), ('5', '2', '2016-05-01', NULL);
-- INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
-- VALUES (NULL, 'otro viaje', '1', '1', '2016-05-18 00:00:00', '2016-02-18 00:00:00', NULL, NULL, '1', '5', '2016-05-16', NULL, '50');
-- INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
-- VALUES (NULL, 'viaje', '2', '2', '2016-04-12 00:00:00', '2016-03-10 00:00:00','2016-04-13 00:00:00', NULL, '2', '5', '2016-05-01', NULL, '25');
-- USUARIO Nro 6:
-- INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo)
-- VALUES (NULL,2,"jasminpaolino","paolino", "paolino@usuario.us",NULL,"A","C");
-- INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo)
-- VALUES (6,default,default,null,null,100000);
-- INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (6, 2, CURRENT_DATE);

-- INSERT INTO VEHICULO (id_vehiculo, anio, marca, modelo, patente, verificado, estado, fecha_verificacion, color, cantidad_asientos, aire_acondicionado, seguro, foto)
-- VALUES (NULL, '1972', 'Chevrolet', 'Chevrolet', 'aaa111', 'N', 'A', NULL, 'FFFFFF', '2', 'N', 'N', NULL);
-- INSERT INTO VEHICULO (id_vehiculo, anio, marca, modelo, patente, verificado, estado, fecha_verificacion, color, cantidad_asientos, aire_acondicionado, seguro, foto)
-- VALUES (NULL, '2010', 'Volkwagen', 'Gol', 'xxx111', 'N', 'A', '2016-05-10', 'FF1122', '4', 'S', 'S', NULL);
-- INSERT INTO MANEJA (id_cliente, id_vehiculo, fecha_inicio, fecha_fin)
-- VALUES ('6', '3', '2015-12-16', NULL), ('6', '4', '2016-05-01', NULL);
-- INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
-- VALUES (NULL, 'Mi primer viaje', '1', '0', '2016-05-15 00:00:00', '2016-05-10 00:00:00', NULL, NULL, '3', '6', '2015-12-16', NULL, '250');
-- INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
-- VALUES (NULL, 'viaje re loco', '2', '3', '2016-04-18 00:00:00', '2016-04-10 00:00:00',NULL, '2016-04-13 00:00:00', '4', '6', '2016-05-01', NULL, '125');


-- localidades de viajes
-- INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
-- VALUES ('1', '3430988', '0', '10','1'), ('1', '3433781', '0', '10','2'), ('1', '3429980', '0', '0','3');
-- INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
-- VALUES ('2', '3430988', '0', '10','1'), ('2', '3433781', '0', '0','2');
-- INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
-- VALUES ('3', '3430987', '0', '10','1'), ('3', '3433780', '0', '10','2'), ('3', '3429979', '0', '0','3');
-- INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
-- VALUES ('4', '3430977', '0', '5','1'), ('4', '3430978', '0', '5','2'), ('4', '3430979', '0', '0','3');

-- INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (NULL,1,"luzbarcena","barcena", "barcena@mail.com","usuario para participar en viajes","A","C");
-- INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (NULL,1,"micaelaguerrero","guerrero", "guerrero@mail.com","usuario para participar en viajes","A","C");
-- INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (NULL,1,"matiasmedrano","medrano", "medrano@mail.com","usuario para participar en viajes","A","C");

-- INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (7, 2, CURRENT_DATE);
-- INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (8, 2, CURRENT_DATE);
-- INSERT INTO USUARIO_ROL (id_usuario, id_rol, fecha_modificacion) VALUES (9, 2, CURRENT_DATE);

-- INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo) VALUES (7,default,default,NULL,NULL,100000);
-- INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo) VALUES (8,default,default,NULL,NULL,100000);
-- INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo) VALUES (9,default,default,NULL,NULL,100000);

-- fede
INSERT INTO TIPO_SANCION (id_tipo_sancion,descripcion,dias_sancion) VALUES (1,"Descuento de Puntos por Cancelación de viaje con Pasajeros",0);
INSERT INTO TIPO_SANCION (id_tipo_sancion,descripcion,dias_sancion) VALUES (2,"Descuento de Puntos por Cancelación tardía de participación en viaje",0);
INSERT INTO TIPO_SANCION (id_tipo_sancion,descripcion,dias_sancion) VALUES (3,"Sanción por Cancelación de viaje con Pasajeros",0);
INSERT INTO TIPO_SANCION (id_tipo_sancion,descripcion,dias_sancion) VALUES (4,"Sanción por Cancelación tardía de participación en viaje",0);
-- completarlas
-- comision por viajes corto, medio y largo
INSERT INTO comision (ID_COMISION,LIMITE_INFERIOR,LIMITE_SUPERIOR,PRECIO,FECHA_INICIO,FECHA_FIN) VALUES(1,0,100,0.15,CURRENT_DATE,NULL);
INSERT INTO comision (ID_COMISION,LIMITE_INFERIOR,LIMITE_SUPERIOR,PRECIO,FECHA_INICIO,FECHA_FIN) VALUES(2,100,500,0.13,CURRENT_DATE,NULL);
INSERT INTO comision (ID_COMISION,LIMITE_INFERIOR,LIMITE_SUPERIOR,PRECIO,FECHA_INICIO,FECHA_FIN) VALUES(3,500,100000,0.10,CURRENT_DATE,NULL);


INSERT INTO TIPO_MOV_SALDO(id_tipo_mov_saldo,descripcion) VALUES (1,"Cobro de comisión por viaje");
INSERT INTO TIPO_MOV_SALDO(id_tipo_mov_saldo,descripcion) VALUES (2,"Acreditación de saldo por pago");

INSERT INTO TIPO_MOV_PUNTOS(id_tipo_mov_puntos,descripcion) VALUES (1,"Puntos como consecuencia de calificación");
INSERT INTO TIPO_MOV_PUNTOS(id_tipo_mov_puntos,descripcion) VALUES (2,"Deducción de puntos por canje de beneficio");
INSERT INTO TIPO_MOV_PUNTOS(id_tipo_mov_puntos,descripcion) VALUES (3,"Sanción");

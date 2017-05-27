SET NAMES utf8;

-- dml para reportes de juan
INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
VALUES (5, 'pruebaReporte1', '3', '0', '2016-06-07 23:00:00', '2016-06-07 22:00:00', NULL, NULL, '1', '5', '2016-05-16', NULL, '50');
INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
VALUES (6, 'pruebaReporte2', '6', '0', '2016-06-07 23:10:00', '2016-06-07 22:00:00', NULL, NULL, '3', '6', '2015-12-16', NULL, '40');
INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
VALUES (7, 'pruebaReporte3', '1', '0', '2016-06-07 23:02:00', '2016-06-07 22:02:00', NULL, NULL, '2', '5', '2016-05-01', NULL, '40000');
INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
VALUES (8, 'pruebaReporte4', '3', '0', '2016-06-07 23:02:00', '2016-06-07 22:02:00', NULL, NULL, '4', '6', '2016-05-01', NULL, '5');

INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (10,1,"prueba_parareporte1","parareporte1", "reporte1@mail.com","usuario para participar en viajes","A","C");
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (11,1,"prueba_parareporte2","parareporte2", "reporte2@mail.com","usuario para participar en viajes","A","C");
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (12,1,"prueba_parareporte3","parareporte3", "reporte3@mail.com","usuario para participar en viajes","A","C");
INSERT INTO USUARIO (id_usuario, id_persona, nombre_usuario, password, email, descripcion, estado,tipo) VALUES (13,1,"prueba_parareporte4","parareporte3", "reporte4@mail.com","usuario para participar en viajes","A","C");

INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo)
VALUES (10,default,default,null,null,100000);
INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo)
VALUES (11,default,default,null,null,100000);
INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo)
VALUES (12,default,default,null,null,100000);
INSERT INTO CLIENTE (id_usuario,puntos,reputacion, foto_registro,foto,saldo)
VALUES (13,default,default,null,null,100000);

INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
VALUES ('5', '3430988', '0', '10','1'), ('5', '3433781', '2', '10','2'), ('5', '3429980', '0', '0','3');
INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
VALUES ('6', '3430988', '0', '10','1'), ('6', '3433781', '2', '0','2');
INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
VALUES ('7', '3430987', '0', '10','1'), ('7', '3433780', '1', '10','2'), ('7', '3429979', '0', '0','3');
INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
VALUES ('8', '3430977', '0', '5','1'), ('8', '3430978', '1', '5','2'), ('8', '3430979', '0', '0','3');


INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(1,4,'0',1,null,1, '2016-06-07 00:05:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(2,8,'1',3,null,2,'2016-06-08 05:05:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(3,6,'1',2,null,3,'2016-06-06 10:05:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(4,4,'1',1,null,4,'2016-06-07 13:55:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(5,15,'4',4,null,5,'2016-06-07 17:05:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(6,25,'1',5,null,6, '2016-06-07 22:15:20');

INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('1','5','11', '10','0',null,'1','3430988', '3429980',2);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('2','5','12', '50','0',null,'2','3430988','3433781',1);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('3','6','13', '20','0',null,'3','3430988','3433781',3);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('4','6','10', '13','0',null,'4','3430988','3433781',1);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('5','7','11', '150','1',null,'5','3430987','3433780',2);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('6','8','13', '250','0',null,'6','3430977','3430979',1);

-- segunda parte dml juan

INSERT INTO `seminario`.`comision` (`id_comision`, `limite_inferior`, `limite_superior`, `precio`, `fecha_inicio`) VALUES ('9', '0', '20', '5', '2016-06-08');
UPDATE `seminario`.`comision` SET `fecha_fin`='2016-06-08' WHERE `id_comision`='1';
INSERT INTO `seminario`.`comision` (`id_comision`, `limite_inferior`, `limite_superior`, `precio`, `fecha_inicio`) VALUES ('10', '20', '50', '7', '2016-06-08');
UPDATE `seminario`.`comision` SET `fecha_fin`='2016-06-08' WHERE `id_comision`='2';
INSERT INTO `seminario`.`comision` (`id_comision`, `limite_inferior`, `limite_superior`, `precio`, `fecha_inicio`) VALUES ('11', '50', '100', '9', '2016-06-08');
UPDATE `seminario`.`comision` SET `fecha_fin`='2016-06-08' WHERE `id_comision`='3';
INSERT INTO `seminario`.`comision` (`id_comision`, `limite_inferior`, `limite_superior`, `precio`, `fecha_inicio`) VALUES ('12', '100', '250', '16', '2016-06-08');
INSERT INTO `seminario`.`comision` (`id_comision`, `limite_inferior`, `limite_superior`, `precio`, `fecha_inicio`) VALUES ('13', '250', '500', '26', '2016-06-08');
INSERT INTO `seminario`.`comision` (`id_comision`, `limite_inferior`, `limite_superior`, `precio`, `fecha_inicio`) VALUES ('14', '500', '1000', '41', '2016-06-08');
INSERT INTO `seminario`.`comision` (`id_comision`, `limite_inferior`, `limite_superior`, `precio`, `fecha_inicio`) VALUES ('15', '1000', '2000', '81', '2016-06-08');
INSERT INTO `seminario`.`comision` (`id_comision`, `limite_inferior`, `limite_superior`, `precio`, `fecha_inicio`) VALUES ('16', '2000', '10000', '151', '2016-06-08');
UPDATE `seminario`.`comision` SET `fecha_fin`='2016-06-08' WHERE `id_comision`='4';
UPDATE `seminario`.`comision` SET `fecha_fin`='2016-06-08' WHERE `id_comision`='5';
UPDATE `seminario`.`comision` SET `fecha_fin`='2016-06-08' WHERE `id_comision`='6';
UPDATE `seminario`.`comision` SET `fecha_fin`='2016-06-08' WHERE `id_comision`='8';
UPDATE `seminario`.`comision` SET `fecha_fin`='2016-06-08' WHERE `id_comision`='7';

INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
VALUES (9, 'pruebaReporte1', '3', '0', '2016-06-07 23:00:00', '2016-12-07 22:00:00', NULL, NULL, '1', '5', '2016-05-16', NULL, '50');
INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
VALUES (10, 'pruebaReporte2', '6', '0', '2016-06-07 23:10:00', '2016-12-07 22:00:00', NULL, NULL, '3', '6', '2015-12-16', NULL, '40');
INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
VALUES (11, 'pruebaReporte3', '1', '0', '2016-06-07 23:02:00', '2016-12-07 22:02:00', NULL, NULL, '2', '5', '2016-05-01', NULL, '40000');
INSERT INTO VIAJE (id_viaje, nombre_amigable, asientos_disponibles, estado, fecha_inicio, fecha_alta, fecha_finalizacion, fecha_cancelacion, id_vehiculo, id_cliente, fecha_inicio_maneja, viaje_complementario, precio)
VALUES (12, 'pruebaReporte4', '3', '0', '2016-06-07 23:02:00', '2016-12-07 22:02:00', NULL, NULL, '4', '6', '2016-05-01', NULL, '5');

INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
VALUES ('9', '3430988', '0', '10','1'), ('9', '3433781', '0', '10','2'), ('9', '3429980', '0', '0','3');
INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
VALUES ('10', '3430988', '0', '10','1'), ('10', '3433781', '0', '0','2');
INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
VALUES ('11', '3430987', '0', '10','1'), ('11', '3433780', '0', '10','2'), ('11', '3429979', '0', '0','3');
INSERT INTO LOCALIDAD_VIAJE (id_viaje, id_localidad, cantidad_pasajeroS, kms_a_localidad_siguiente,ordinal)
VALUES ('12', '3430977', '0', '5','1'), ('12', '3430978', '0', '5','2'), ('12', '3430979', '0', '0','3');


INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(7,5,'0',9,null,7, '2016-12-07 00:05:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(8,9,'1',11,null,8,'2016-12-08 05:05:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(9,7,'1',10,null,9,'2016-12-06 10:05:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(10,5,'1',11,null,10,'2016-12-07 13:55:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(11,16,'4',12,null,11,'2016-12-07 17:05:26');
INSERT INTO comision_cobrada (id_comision_cobrada,monto,estado,id_comision,id_movimiento_saldo,id_pasajero_viaje,fecha)
VALUES(12,26,'1',13,null,12, '2016-12-07 22:15:20');

INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('7','9','11', '10','0',null,'7','3430988', '3429980',2);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('8','9','12', '50','0',null,'8','3430988','3433781',1);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('9','10','13', '20','0',null,'9','3430988','3433781',3);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('10','10','10', '13','0',null,'10','3430988','3433781',1);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('11','11','11', '150','1',null,'11','3430987','3433780',2);
INSERT INTO pasajero_viaje (id_pasajero_viaje,id_viaje,id_cliente, kilometros,estado,id_calificacion, id_comision_cobrada,id_localidad_subida,id_localidad_bajada,nro_asientos)
VALUES('12','12','13', '250','0',null,'12','3430977','3430979',1);
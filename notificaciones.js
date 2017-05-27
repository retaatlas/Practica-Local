notificaciones = [];


var cargarNotificaciones = function() {
	var onsuccess = function (jsonData) {
		notificaciones = jsonData.notificaciones;
		mostrarNotificaciones();
	}

	var sendData = {
		entity: 'notificaciones',
		action: 'ver_todas'
	};

	vc.peticionAjax("/notificaciones", sendData, "GET", onsuccess);
	//simular();
}

var simular = function () {
	notificaciones = [
		{
			id_notificacion: 1,
			fecha: "2016-05-29 12:00:00",
			texto: "marina38 se ha postulado en tu viaje",
			link: "/detalle_viaje.html",
			estado: "no_leido"
		},
		{
			id_notificacion: 2,
			fecha: "2016-05-28 17:30:00",
			texto: "jorge97 a rechazado tu postulacion en su viaje",
			link: "/detalle_viaje.html",
			estado: "no_leido"
		},
		{
			id_notificacion: 3,
			fecha: "2016-05-28 10:45:53",
			texto: "Has sido asignado como conductor del vehiculo Volskwagen Gold DBW351",
			link: "ver_vehiculo.html",
			estado: "leido"
		},
		{
			id_notificacion: 4,
			fecha: "2016-05-28 08:34:00",
			texto: "El viaje <<fantastico>> en el cual participaste ha finalizado",
			link: "/detalle_viaje.html",
			estado: "leido"
		},
		{
			id_notificacion: 5,
			fecha: "2016-05-28 08:34:00",
			texto: "Has recibido una calificacion de diego32",
			link: "/calificar_usuarios.html",
			estado: "no_leido"
		},

	];
	mostrarNotificaciones();
}

var mostrarNotificaciones = function () {
	notificaciones.forEach(function(notificacion) {
		notificacion.fecha=vc.toFechaLocal(notificacion.fecha);
		$("#contenedor-notificaciones").append(Mustache.render(notificacionTemplate, notificacion));
	});

	$(".loadingScreen").fadeOut();
}

var verNotificacion = function (id_notificacion) {
	var i=0;
	while (i< notificaciones.length)  {
		if(notificaciones[i].id_notificacion == id_notificacion){
			var notificacion = notificaciones[i];
			var sendData = {
				'entity': 'notificaciones',
				'action': 'marcar_leida',
				'idNotificacion': id_notificacion
			}
			var onsuccess = function() {
				var selector = "#notificacion-"+id_notificacion;
				$(selector).removeClass("no_leido");
				$(selector).addClass("leido");
				if(notificacion.link != null) {
					window.location = notificacion.link;
				}
			}
			 vc.peticionAjax("/notificaciones", sendData, "POST", onsuccess);
			// Estamos simulando
			//onsuccess();
		}
		i++;
	}

}

window.onload = function () {
	notificacionTemplate = $("#notificacion-template").html();
	cargarNotificaciones();
}

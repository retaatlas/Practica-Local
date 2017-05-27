var data = {};
data.viaje = {};
data.viaje.id = getUrlVars()["id"];
data.conductor = {};
data.vehiculo = {};
data.localidades = [];
data.comentarios = [];
data.usuario_logueado = {};

var sendAjax = function(sendData,callback){
	//console.log("mando: ",sendData);
	$.ajax({
		url: '/viajes',
		dataType: 'json',
		method: 'POST',
		data: sendData,
		success: function (jsonData) {
			DEBUGresponse = jsonData;
			callback(jsonData);
		},
		error: function (er1, err2, err3) {
			document.body.innerHTML = er1.responseText;
			window.alert (err3);
		}
	});
}


data.loadData = function() {
	var sendData = {
		entity: "viaje",
		action: "detalle",
		"id_viaje": data.viaje.id
	}
	var onsuccess = function(jsonData){
		if(jsonData.result){
			$('.loadingScreen').fadeOut();
			data.viaje = jsonData.viaje;
			data.conductor = jsonData.conductor;
			data.vehiculo = jsonData.vehiculo;
			data.localidades = jsonData.localidades;
			data.comentarios = jsonData.comentarios;
			data.usuario_logueado = jsonData.usuario_logueado;
			data.comentarios = jsonData.comentarios;

			if(jsonData.recargar_en != undefined) {
				window.setTimeout(data.loadData, jsonData.recargar_en);
			}
			configurarUi();
			cargarViaje();
			cargarVehiculo();
			cargarComentarios();
			cargarConductor();
			cargarRutaEnMapa();



		} else if (jsonData.redirect != undefined) {
			window.location = jsonData.redirect;
		} else if (jsonData.msg) {
			modalMessage("error",jsonData.msg,"Carga de viaje");
		}
	}

	//simular(sendData);

	sendAjax(sendData,onsuccess);
}

initUI = function() {
	data.loadData();
	loadMap();
};
window.onload=initUI;

////carga de mapa///////

function loadMap() {
  var script = document.createElement("script");
  script.src = "http://maps.googleapis.com/maps/api/js?key=AIzaSyCu2P6zmQmOyESf872DSdZgYam9PMJnzwg&callback=initMap";
  document.body.appendChild(script);
}

mapData = {
	marcadores: []
}
initMap = function() {
	mapData.map = new google.maps.Map(document.getElementById('mapa'), {
		center: {lat: -34.5774135, lng: -59.0909557},
		drawingControl: false,
		zoom: 16
	});

	mapData.directionsService = new google.maps.DirectionsService();
	mapData.directionsDisplay = new google.maps.DirectionsRenderer({suppressMarkers: true});
	mapData.directionsDisplay.setMap (mapData.map);
	cargarRutaEnMapa();
}

var cargarRutaEnMapa = function(){
	if(data.viaje.recorrido == undefined) {
		// Esto puede suceder si el mapa se carga antes que los datos del viaje
		return null;
	}
	mapData.marcadores = [];
	var i=1; //para que label de los marcadores muestre secuencia
	data.viaje.recorrido.forEach(function(id){
		var item = localidadPorId(id);

		mapData.marcadores.push(new google.maps.Marker({
				position: {
					lat: parseFloat(item.lat),
					lng: parseFloat(item.lng)
				},
				map: mapData.map,
				title: item.nombre,
				label: i.toString()
			}));
		i++;
	});

	pedirRuta();
}

var pedirRuta = function () {
	var origen = mapData.marcadores[0];
	var destino = mapData.marcadores[mapData.marcadores.length-1];

	var solicitud = {
		origin: origen.position,
		destination: destino.position,
		travelMode:google.maps.TravelMode.DRIVING,
		waypoints: listarPuntosIntermedios()
	};

  mapData.directionsService.route(solicitud, function(result, status) {
	console.log ("Se ha recibido respuesta");
	DEBUGresult=result;
    if (status == google.maps.DirectionsStatus.OK) {
      mapData.directionsDisplay.setDirections(result);
    }
  });
}

var listarPuntosIntermedios = function () {
	var puntos = [];
	for (i=1; i<mapData.marcadores.length-1; i++) {
		puntos.push({location:mapData.marcadores[i].position});
	}
	return puntos;
}

////fin carga mapa///

var simular = function(json){
	data.viaje = {
		id: data.viaje.id ,
		nombre_amigable: "Un alto viaje",
		asientos_disponibles: "3",
		estado: "no_iniciado",
		tipo: "ida",
		id_viaje_complementario: "4",
		origen: "324",
		destino: "880",
		fecha_inicio: "12/09/2016",
		precio: "30",
		cantidad_pasajeros_calificables: "1",
		recorrido: ["324", "112","880"]
	};
	data.conductor = {
		nombre_usuario: "Juanc23",
		reputacion: "3",
		mail:"juan322@hotmail.com",
		telefono:"02323 011 423377",
		foto: "upload/foto.jpg",
		foto_registro: "upload/foto_reg.jpg"
	};
	data.vehiculo = {
		marca: "Ford",
		modelo: "Falcon",
		anio: "2012",
		patente: "LKJ 890",
		aire: "S",
		seguro: "N",
		verificado: "S",
		foto: "upload/auto.jpg"
	};
	data.localidades = [{
		id: "324",
		nombre: "Lujan",
		lat: "-34.5703",
		lng: "-59.105"
	},{
		id: "112",
		nombre: "Rodriguez",
		lat: "-34.6084",
		lng: "-58.9525"
	},{
		id: "880",
		nombre: "Moreno",
		lat: "-34.634",
		lng: "-58.7914"
	}];
	data.usuario_logueado = {
		es_conductor: false,
		es_aceptado: false,
		es_rechazado: false,
		es_postulado: false,
		es_seguidor: false,
		es_finalizo: false,
		ha_calificado: false
	};
	data.comentarios=[{
		foto:"/img/perfil/default.png",
		nombre_usuario:"juan23",
		fecha:"01/06/2016",
		comentario:"Excelente viaje pero aceptas pagos con tarjeta?"
	},{
		foto:"/img/perfil/default.png",
		nombre_usuario:"usuario",
		fecha:"01/06/2016",
		comentario:"Hola! pasarias por rodríguez luego de Luján? gracias"
	}];
	configurarUi();
	cargarViaje();
	cargarConductor();
	cargarVehiculo();
	cargarRutaEnMapa();
}

var configurarUi = function(){

	 $("#message-text").val("");
  ocultarDataPrivadaConductor();

	var esConductor = data.usuario_logueado.es_conductor;
	var esAceptado = data.usuario_logueado.es_aceptado;
	var esRechazado = data.usuario_logueado.es_rechazado;
	var esPostulado = data.usuario_logueado.es_postulado;
	var esFinalizo = data.usuario_logueado.es_finalizo;
	var esSeguidor = data.usuario_logueado.es_seguidor;
	var haCalificado = data.usuario_logueado.ha_calificado;
	var estado = estadoString(data.viaje.estado);

	$('#infomsg-conductor').hide();
	$('#infomsg-postulado').hide();
	$('#infomsg-seguidor').hide();
	$('#infomsg-pasajero-aceptado').hide();
	$('#infomsg-pasajero-rechazado').hide();
	$('#infomsg-pasajero-finalizo').hide();
	$('#infomsg-calificacion-pendiente').hide();

	if(esConductor) {$('#infomsg-conductor').show();}
	if(esPostulado) {$('#infomsg-postulado').show();}
	if(esSeguidor) {$('#infomsg-seguidor').show();}
	if(esAceptado) {$('#infomsg-pasajero-aceptado').show();}
	if(esRechazado) {$('#infomsg-pasajero-rechazado').show();}
	if(esFinalizo) {$('#infomsg-pasajero-finalizo').show();}
	if(!haCalificado && ((esConductor && estado=="Finalizado" && data.viaje.cantidad_pasajeros_calificables>0) || esFinalizo)) {$('#infomsg-calificacion-pendiente').show();}

	if (esAceptado || esPostulado || esConductor || esFinalizo){
		$("#botonera-cliente").hide();
		if (esConductor){
			$("#botonera-conductor").show();
			$("#botonera-pasajero").hide();
			if (data.viaje.cantidad_pasajeros_calificables > 0 || data.viaje.cantidad_pasajeros_postulados > 0){
				$("#btnModificar").hide();
			}
			//Boton finalizar solo si esta en iniciado
			if (estado == "Iniciado"){
				$("#btnViajeFinalizado").show();
				$("#btnCalificar").hide();
			}else{
				$("#btnViajeFinalizado").hide();

				//solo califica si hay pasajeros
				if (data.viaje.cantidad_pasajeros_calificables > 0){
					// lo comento porque calificar ahora tiene calificacion dada y recibida
					//if (haCalificado){
						//$("#btnCalificar").hide();
					//}else{
						$("#btnCalificar").show();
					//}
				}else{
					$("#btnCalificar").hide();
				}
			}
		}else if (esAceptado){
			mostrarDataPrivadaConductor();
			$("#botonera-conductor").hide();
			$("#botonera-pasajero").show();
			$("#btnCalificar").hide();
			if (estado == "Iniciado" || estado == "Finalizado"){
				$("#btnViajeFinalizado").show();
			}else{
				$("#btnViajeFinalizado").hide();
			}
		}else if (esFinalizo){
			$("#botonera-conductor").hide();
			$("#botonera-pasajero").hide();
			$("#btnCalificar").show();
			$("#btnViajeFinalizado").hide();
		} else if (esPostulado){
			$("#botonera-conductor").hide();
			$("#botonera-pasajero").show();
			$("#btnCalificar").hide();
			$("#btnViajeFinalizado").hide();
		}
	} else {
		$("#btnCalificar").hide();
		$("#btnViajeFinalizado").hide();
		$("#botonera-conductor").hide();
		$("#botonera-pasajero").hide();
		$("#botonera-cliente").show();
		$("#btnParticipar").show();
		if (esSeguidor){
			$("#btnDejarSeguir").show();
			$("#btnSeguir").hide();
		} else{
			$("#btnDejarSeguir").hide();
			$("#btnSeguir").show();
		}
	}

	if (estado=="Cancelado"){
		$("#botonera-conductor").hide();
		$("#botonera-pasajero").hide();
		$("#botonera-cliente").hide();
		$("#btnCalificar").hide();
		$("#btnViajeFinalizado").hide();
		ocultarDataPrivadaConductor();
	} else if(estado == "Iniciado"){

	} else if(estado == "No iniciado"){
		$("#btnCalificar").hide();
		$("#btnViajeFinalizado").hide();
	} else if(estado == "Finalizado"){
		$("#botonera-conductor").hide();
		$("#botonera-pasajero").hide();
		$("#botonera-cliente").hide();
		ocultarDataPrivadaConductor();
	}
}

var mostrarDataPrivadaConductor = function(){
	$("#telefono-conductor").text(data.conductor.telefono);
	$("#mail-conductor").text(data.conductor.mail);
	$("#telefono-conductor").show();
	$("#mail-conductor").show();
}
var ocultarDataPrivadaConductor = function(){
	 $("#mail-conductor").hide();
	 $("#telefono-conductor").hide();
}
var cargarVehiculo = function(){
	$("#panel-foto-vehiculo a").attr('href',data.vehiculo.foto || "/img/perfil/sin_imagen.jpg");
	$("#panel-foto-vehiculo img").attr('src',data.vehiculo.foto || "/img/perfil/sin_imagen.jpg");
	$("#marca").text(data.vehiculo.marca);
	$("#modelo").text(data.vehiculo.modelo);
	$("#anio").text(data.vehiculo.anio);
	$("#patente").text(data.vehiculo.patente);
	$("#aire").html(generarEmoticon(data.vehiculo.aire));
	$("#seguro").html(generarEmoticon(data.vehiculo.seguro));
	$("#verificado").html(generarEmoticon(data.vehiculo.verificado));

}

var cargarViaje = function(){
	$("#tipo").text(tipoString(data.viaje.tipo));
	setearViajeComplementario(data.viaje.id_viaje_complementario);

	$("#estado").text(estadoString (data.viaje.estado));
	$("#origen").text(localidadNombre (data.viaje.origen));
	$("#destino").text(localidadNombre (data.viaje.destino));
	$("#fecha").text(vc.toFechaLocal(data.viaje.fecha_inicio));
	$("#precio").text("$"+data.viaje.precio);
	$("#nombre_amigable").text(data.viaje.nombre_amigable);

	$("#recorrido").html("");
	var ultimo = data.viaje.recorrido.length -1;
	data.viaje.recorrido.forEach(function(elem, index){
		if (index>0)$("#recorrido").append(' <span class="glyphicon glyphicon-arrow-right"></span> ');

		var label_class="";
		var asientos_tramo = data.viaje.disponibilidad_asientos[index];
		var msg_asientos=localidadNombre(elem);

		// Label colors
		if(data.viaje.estado != "iniciado" && data.viaje.estado != "no_iniciado") {
			label_class="label-primary";
		} else if (asientos_tramo < 1 || index == ultimo) {
			label_class="label-danger";
		} else {
			label_class="label-success";
		}

		// Mensaje asientos
		if(data.viaje.estado == "iniciado" || data.viaje.estado == "no_iniciado") {
			if (index == ultimo) {
				msg_asientos = "Este es el final del viaje";
			} else if (asientos_tramo < 1) {
				msg_asientos = "No hay asientos libres";
			} else if (asientos_tramo == 1) {
				msg_asientos = "1 asiento libre";
			} else if (asientos_tramo > 1) {
				msg_asientos = asientos_tramo + " asientos libres";
			}
		}
		$("#recorrido").append('<span class="label '+label_class+'" data-toggle="tooltip" title="'+msg_asientos+'">'+localidadNombre(elem)+'</span>');
	});
	$('[data-toggle="tooltip"]').tooltip();

}

var cargarConductor = function(){
	$("#panel-foto-conductor a").attr('href',data.conductor.foto || "/img/perfil/default.png");
	$("#panel-foto-conductor img").attr('src',data.conductor.foto || "/img/perfil/default.png");
	$("#panel-foto-registro a").attr('href',data.conductor.foto_registro || "/img/perfil/sin_registro.jpg");
	$("#panel-foto-registro img").attr('src',data.conductor.foto_registro || "/img/perfil/sin_registro.jpg");
	$("#reputacion").text(reputacionStars(data.conductor.reputacion));
	$("#nombreConductor").text(data.conductor.nombre_usuario);
	$("#nombreConductor").attr('href',"/perfil.html?usuario="+data.conductor.nombre_usuario);
}

var cargarComentarios = function(){
	if (!data.comentarios || !data.comentarios.length){
		$("#busqueda-paginacion").hide();
		var html = "<div class='jumbotron'>"
			+"<h4 class='text-center text-primary'>No hay comentarios</h4>"
			+"</div>";
		$("#panel-comentarios").html(html);
	}else{
		$("#comentarios-paginacion").show();
		autogeneratePages();
		changePage(1);
	}
}

var enviarComentario = function(){
	var sendData = {
		entity: "comentario",
		action: "new",
		id_viaje: data.viaje.id,
		comentario: $("#message-text").val()
	}
	var onsuccess = function(jsonData){
		data.loadData();
	}
	//console.log("mando: ",sendData);
	vc.peticionAjax("/viajes",sendData,"POST",onsuccess);
}

/////paginacion////
var current_page = 1;
var records_per_page = 10;

function autogeneratePages(){
	//Ir a pagina anterior
	var prevPage = "<li id='previous-page'>"+
					  "<a href='javascript:prevPage()' aria-label='Previous'>"+
						"<span aria-hidden='true'>&laquo;</span>"+
					  "</a>"+
					"</li>";

	//paginas de resultado
	var html="";
	for (i=1; i<=numPages();i++){
		html += "<li><a href='javascript:changePage("+i+")'>"+i+"</a></li>";
	}

	// Siguiente pagina
	var nextPage = "<li id='next-page'>"+
					  "<a href='javascript:nextPage()' aria-label='Next'>"+
						"<span aria-hidden='true'>&raquo;</span>"+
					  "</a>"+
					"</li>";

	$("#comentarios-paginacion").html(prevPage+html+nextPage);
}

function changePage(page){
	current_page = page;

    var btn_prev = $("#previous-page");
    var btn_next = $("#next-page");


    // Validate page
    if (page < 1) page = 1;
    if (page > numPages()) page = numPages();

    for (var i = (page-1) * records_per_page; i < (page * records_per_page); i++) {
		generarHtmlComentario(i);
    }

	$( "#comentarios-paginacion").find(".active").removeClass("active");
	$( "#comentarios-paginacion li:eq("+page+")" ).addClass("active");

    if (page == 1) {
        btn_prev.addClass("disabled");
    } else {
        btn_prev.removeClass("disabled");
    }

    if (page == numPages()) {
        btn_next.addClass("disabled");
    } else {
        btn_next.removeClass("disabled");
    }
}
function numPages(){
    return Math.ceil(data.comentarios.length / records_per_page);
}
function prevPage(){
    if (current_page > 1) {
        current_page--;
        changePage(current_page);
    }
}

function nextPage(){
    if (current_page < numPages()) {
        current_page++;
        changePage(current_page);
    }
}

///////////fin-paginacion//////////

var generarHtmlComentario = function(indiceComentario){
	// si es el primer comentario borro datos del panel
	if (indiceComentario % records_per_page == 0) $("#panel-comentarios").html("");
	var comentario = data.comentarios[indiceComentario];
	if (comentario){
		comentario.fecha = vc.toFechaLocal(comentario.fecha);
		comentario.foto_revisada = comentario.foto || "/img/perfil/default.png";
		var template = $("#comentario-template").html();
		$("#panel-comentarios").append(Mustache.render(template, comentario));
	}
}

function setearViajeComplementario(idComp){
	if (idComp){
		var link = "/detalle_viaje.html?id=" + idComp;
		$("#tipo").append(" <a href='"+link+"'></br><small>Viaje complemento</small></a>")
	}
}

var mostrarVentanaParticipar = function(){
	cargarTramos(data.viaje.recorrido);
	$('#modal-participar').modal('show');
}

var cargarTramos = function(recorrido){
	var queryOrigen = "select[name=origenPasajero]";
	var queryDestino = "select[name=destinoPasajero]";
	$(queryOrigen+","+queryDestino).empty();
	for (var i=0; i<recorrido.length; i++){
		var valor = recorrido[i];
		var texto = (i+1)+" - "+localidadNombre(recorrido[i]);
		var op = createOp(valor,texto);
		if (i == 0){
			//agrego primer elemento solo a origen
			$(queryOrigen).append(op);
		} else if(i==recorrido.length-1){
			//agrego ultimo elemento solo a destino
			$(queryDestino).append(op);
		} else{
			//agrego todos los demas
			$(queryOrigen+","+queryDestino).append(op);
		}
	}
}

var createOp = function(valor,texto){
	var option = document.createElement("OPTION");
	option.text = texto;
	option.value = valor;
	return option;
}

var participarViaje = function(){
	var sendJson = {
		entity: "viaje",
		action: "participar",
		id_viaje: data.viaje.id,
		origen:  $("select[name=origenPasajero]").val(),
		destino:  $("select[name=destinoPasajero]").val(),
		asientos:  $("input[name=asientosPasajero]").val()
	}
	var onsuccess = function(jsonData){
		closeModal('participar');
		if (jsonData.result){
			data.loadData();
			postulacionCorrecta(jsonData.msg,jsonData.conductor);
		}else{
			errorMessage(jsonData.msg);
		}
	}
	if (esTramoValido()){
		sendAjax(sendJson,onsuccess);
	};
}
var postulacionCorrecta = function(mensaje,conductor){
	var modalName = 'postulacion-correcta';

	// Lleno datos del conductor en el modal
	var nombre = conductor.persona.apellidos+", "+conductor.persona.nombres;
	$("#nombre-conductor").html(nombre);
	var perfil = 'perfil.html?usuario='+conductor.cliente.nombre_usuario;
	var usuario = "<a href='"+perfil+"'>"
		+ conductor.cliente.nombre_usuario
		+ "</div>";
	$("#usuario-conductor").html(usuario);
	var linkMail = "mailto:"+conductor.cliente.email;
	var mail = "<a href='"+linkMail+"'>"
		+ conductor.cliente.email
		+ "</div>";
	$("#mail-conductor").html(mail);
	var tel = conductor.persona.telefono;
	$("#telefono-conductor").html(tel);

	//muestro modal
	modalMessage(modalName,mensaje,"Postulación a viaje");
}
var verMisViajes = function(){
	window.location = "/mis_viajes.html";
}
var esTramoValido = function(){
	var origen = parseInt($("select[name=origenPasajero]").val());
	var destino = parseInt($("select[name=destinoPasajero]").val());
	var indexOrigen = data.viaje.recorrido.indexOf(origen);
	var indexDestino = data.viaje.recorrido.indexOf(destino);
	if (indexOrigen >= indexDestino){
		var msg = "Tramo no válido para este viaje";
		var panel = "#panel-error-tramo";
		var elemento = "select[name=origenPasajero],select[name=destinoPasajero]";
		customAlert(panel,elemento,msg);
		return false;
	}
	return true;
}

var seguirViaje = function(){
	var sendJson = {
		entity:"viaje",
		action: "seguir",
		id_viaje: data.viaje.id
	}
	var onsuccess = function(jsonData){
		if (jsonData.result){
			//modalMessage('success',jsonData.msg);//opcional
			data.loadData();
		}else{
			errorMessage(jsonData.msg);
		}
	}
	sendAjax(sendJson,onsuccess);
}
var dejarDeSeguirViaje = function(){
	var sendJson = {
		entity:"viaje",
		action: "dejar_de_seguir",
		id_viaje: data.viaje.id
	}
	var onsuccess = function(jsonData){
		if (jsonData.result){
			//modalMessage('success',jsonData.msg);//opcional
			data.loadData();
		}else{
			errorMessage(jsonData.msg);
		}
	}
	sendAjax(sendJson,onsuccess);
}

var cancelarParticipacion = function(){
	var modalName='warning';
	var confirmarCancelacion = function(){
		closeModal(modalName);
		var sendJson = {
			entity: "viaje",
			action: "cancelar_participacion",
			id_viaje: data.viaje.id
		}
		var onsuccess = function(jsonData){
			if (jsonData.result){
				// Lo saco porque se hace muy denso andar cerrando ventanitas a cada rato
				// (esta es la tercera seguida que se abre!)
				//modalMessage("warning",jsonData.msg,"Participación cancelada");
				data.loadData();
			}else{
				errorMessage(jsonData.msg);
			}
		}
		sendAjax(sendJson,onsuccess);
	}
	/*	es_conductor: false,
		es_aceptado: false,
		es_postulado: false,
		es_seguidor: false,
		es_finalizo: false,
		ha_calificado: false*/
	if (data.usuario_logueado.es_postulado){
		confirmarCancelacion();
	}else if (data.usuario_logueado.es_aceptado){
		var title = "¿Confirma cancelación?";
		var msg = "Al confirmar esta acción usted podría ser sancionado con puntos.";
		var btn = document.createElement("BUTTON");
		btn.className="btn btn-danger dinamico";
		btn.innerHTML = '<span class="glyphicon glyphicon-remove"></span> Confirmar cancelación';
		btn.name = "confirmarCancelacion";
		btn.onclick=confirmarCancelacion;
		modalButton(modalName,btn);
		modalMessage(modalName,msg,title);
	}
}
var modificarViaje = function(){
	window.location = "/modificar_viaje.html?id="+data.viaje.id;
}
var cancelarViaje = function(){
	var modalName='warning';
	var confirmarCancelacion = function(){
		closeModal(modalName);
		var sendJson = {
			entity:"viaje",
			action: "cancelar",
			id_viaje: data.viaje.id
		}
		var onsuccess = function(jsonData){
			if (jsonData.result){
				data.loadData();
				//window.location = "/mis_viajes.html"; //mejorar esto
			} else if (jsonData.relocate){
				window.location = jsonData.relocate;
			} else if (jsonData.msg){
				errorMessage(jsonData.msg);
			}
		}
		sendAjax(sendJson,onsuccess);
	}
	var title = "¿Confirma cancelación?";
	var msg = "Al confirmar esta acción usted podría recibir una sanción de puntos"
		+" si existieran pasajeros aceptados.";
	var btn = document.createElement("BUTTON");
	btn.className="btn btn-danger dinamico";
	btn.innerHTML = '<span class="glyphicon glyphicon-remove"></span> Confirmar cancelación';
	btn.name = "confirmarCancelacion";
	btn.onclick=confirmarCancelacion;
	modalButton(modalName,btn);
	modalMessage(modalName,msg,title);
}

var verPostulantes = function(){
	window.location = "/listado_postulantes.html?id="+data.viaje.id;
}

var calificar = function(){
	window.location = "/calificar_usuarios.html?id="+data.viaje.id;
}

var viajeFinalizado = function(){
	var modalName='warning';
	var confirmarFinalizacion = function(){
		closeModal(modalName);
		var sendJson = {
			entity:"viaje",
			action: "finalizar_viaje",
			id_viaje: data.viaje.id
		}
		var onsuccess = function(jsonData){
			if (jsonData.result){
				data.loadData();
				var msg = jsonData.msg;
				if(data.viaje.cantidad_pasajeros_calificables) {
					var btn = document.createElement("BUTTON");
					btn.className="btn btn-success dinamico";
					btn.innerHTML = '<span class="glyphicon glyphicon-ok"></span> Calificar';
					btn.name = "calificar";
					btn.onclick=calificar;
					modalButton("success",btn);
				}
				modalMessage("success",msg);
			}else{
				errorMessage(jsonData.msg);
			}
		}
		sendAjax(sendJson,onsuccess);
	}
	var title = "¿Finaliza su participación en el viaje?";
	var msg = " Esta acción no se puede deshacer.";
	var btn = document.createElement("BUTTON");
	btn.className="btn btn-success dinamico";
	btn.innerHTML = 'Confirmar finalización';
	btn.name = "confirmarFinalizacion";
	btn.onclick=confirmarFinalizacion;
	modalButton(modalName,btn);
	modalMessage(modalName,msg,title);
}

var customAlert = function(panel,elemento,msg){
	$(panel).append(generateAlert(msg));

	$(elemento).change(function(){
		$(panel).empty();
	});
}
var localidadPorId = function(id){
	var l;
	data.localidades.forEach(function(elem){
		if (elem.id == id){
			l = elem;
		}
	});
	return l;
}
var localidadNombre = function(id){
	var nombre = "";
	data.localidades.forEach(function(elem){
		if (elem.id == id){
			nombre = elem.nombre;
		}
	});
	return nombre;
}
var tipoString = function(caracter){
	switch (caracter) {
		case 'ida': return "Ida";
		case 'ida_vuelta': return "Ida y vuelta";
		case '': return "No especificado";
		case null: return "No especificado";
		default: return "Desconocido";
	}
}
var estadoString = function (caracter) {
	switch (caracter) {
		case 'finalizado': return "Finalizado";
		case 'no_iniciado': return "No iniciado";
		case 'iniciado': return "Iniciado";
		case 'cancelado': return "Cancelado";
		case '': return "No especificado";
		case null: return "No especificado";
		default: return "Desconocido";
	}
}

var reputacionStars = function(caracter){
	caracter = Math.round(caracter);
	var stars = "";
	while (caracter > 0){
		stars += "★";
		caracter--;
	}
	return stars;
}

var generarEmoticon = function(caracter){
	var span = document.createElement("SPAN");
	if (caracter=="S"){
		span.className = "glyphicon glyphicon-ok text-success";
		return span;
	}else{
		span.className = "glyphicon glyphicon-remove text-danger";
		return span;
	}
}

var generateAlert = function(msg){
	return html = '<div class=\"alert alert-danger\" role=\"alert\">'
		+'<span class=\"glyphicon glyphicon-exclamation-sign\" aria-hidden=\"true\"></span>'
		+'<span class=\"sr-only\">Error:</span> '
		+msg+'</div>';
}

//MODALS
//eliminar elementos de modal que fueron generados dinamicamente
$(document).on('hide.bs.modal', function (e) {
  $(".dinamico").each(function(){
	$(this).remove();
  });

});

var errorMessage = function (textMsg) {
	$('#error-message').text(textMsg);
	$('#modal-error').modal('show');
}
var modalMessage = function (modalName,textMsg,titleMsg) {
	$('#'+modalName+'-message').text(textMsg);
	if (titleMsg){
		$('#modal-'+modalName +" .dialog-title").text(titleMsg);
	}
	$('#modal-'+modalName).modal('show');
}
var modalButton = function(modalName,btn){
	//$('#modal-'+modalName+' button[name='+btn.name+']').remove(); //si ya existe, elimino el elemento antes de crearlo
	$('#modal-'+modalName+' .modal-footer').append(btn);
}
var closeModal = function (name) {
	$('#modal-' + name).modal('hide');
}


// funciones robadas

function getUrlVars() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
		vars[key] = value;
	});
	return vars;
}

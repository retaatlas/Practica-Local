var idViaje = getUrlVars()["id"];
var postulantes = [];

var loadData = function() {

	var sendData = {
		entity:"viaje",
		action: "ver_postulantes",
		"id_viaje": idViaje
	}
	var onsuccess = function(jsonData){
		if(jsonData.result){
			$('.loadingScreen').fadeOut();
			postulantes = jsonData.postulantes;
			if (postulantes.length) cargarPostulantes();
		} else if (jsonData.redirect != undefined) {
			window.location = jsonData.redirect;// si no es conductor de este viaje, acceso denegado
		}
	}
	//simular();

	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
}

var initUI = function(){
	loadData();
	$('[data-toggle="tooltip"]').tooltip();

}

window.onload=initUI;



var simular = function(){
	postulantes = [{
		estado_postulacion: "1", //1: postulado, 2: aceptado, 3: rechazado
		nombre_usuario: "Carolo4",
		foto:"img/home/administracion_usuarios.png",
		reputacion:"1",
		origen: "San Andres de Giles, Buenos Aires, Argentina",
		destino: "Carmen de areco, Buenos Aires, Argentina",
		apellido: "Perez",
		nombres: "Carolo",
		telefono: "421154",
		mail: "carolo4@gmail.com"
	},{
		estado_postulacion: "2", //1: postulado, 2: aceptado, 3: rechazado
		nombre_usuario: "KarinaK100",
		foto:"upload/foto.jpg",
		reputacion:"2",
		origen: "Navarro, Buenos Aires, Argentina",
		destino: "Rosario, Santa Fe, Argentina",
		apellido: "Krenz",
		nombres: "Karina",
		telefono: "497673",
		mail: "Karina234123@outlook.com"
	},{
		estado_postulacion: "3",
		nombre_usuario: "RodolfoU",
		foto:"upload/foto.jpg",
		reputacion:"3",
		origen: "Rosario, Santa Fe, Argentina",
		destino: "Montevideo, Uruguay",
		apellido: "Uber",
		nombres: "Rodolfo",
		telefono: "3434 15421154",
		mail: "rodolfoU@hotmail.com"
	}];
	if (postulantes.length) cargarPostulantes();
}

var cargarPostulantes = function(){
	console.log("postulantes que me traje: ",postulantes);

	var template = $("#postulante-template").html();
	var htmlPendientes = "";
	var htmlNoPendientes = "";
	postulantes.forEach(function(elem){
		elem.estado_string = estadoString(elem.estado_postulacion);
		elem.color_panel = colorPanel(elem.estado_postulacion);
		elem.reputacion_stars = reputacionStars(elem.reputacion);
		elem.foto_revisada = elem.foto || "/img/perfil/default.png";
		elem.comision_tramo = Number(elem.comision_tramo).toFixed(2);
		if (elem.es_pendiente = elem.estado_postulacion == "postulado"){
			htmlPendientes += Mustache.render(template, elem);
		}else{
			elem.es_rechazado = elem.estado_postulacion=="rechazado";
			htmlNoPendientes += Mustache.render(template, elem);
		}
	});
	if (htmlPendientes != "" && htmlNoPendientes != ""){
		var html = "<div class=' col-md-6'>"
			+htmlPendientes+
		"</div>"+
		"<div class=' col-md-6'>"
			+htmlNoPendientes+
		"</div>"
	}else{
		var html = "<div class='row col-xs-12 col-sm-12 col-md-12 col-lg-12' >"
			+htmlPendientes+htmlNoPendientes+
			"</div>";
	}
	$("#panel-postulantes").html(html);

	// Lo que solo anda una vez que se cargo toda la data.
	setearEventos();
}

var setearEventos = function(){
	$('[data-toggle="tooltip"]').tooltip();
}

var aceptarPostulante = function(nombre_usuario){
	console.log(nombre_usuario);
	var sendData = {
		entity:"viaje",
		action: "aceptar_rechazar_postulante",
		"id_viaje": idViaje,
		"nombre_postulante": nombre_usuario,
		"decision": 1 //aceptar
	}
	var onsuccess = function(jsonData){
		if(jsonData.result){
			loadData();
		} else {
			modalMessage("error",jsonData.msg,"Aceptar postulante");
		}
	}
	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
}

var rechazarPostulante = function(nombre_usuario){
	var sendData = {
		entity:"viaje",
		action: "aceptar_rechazar_postulante",
		"id_viaje": idViaje,
		"nombre_postulante": nombre_usuario,
		"decision": 2 //rechazado
	}
	var onsuccess = function(jsonData){
		if(jsonData.result){
			loadData();
		} else {
			modalMessage("error",jsonData.msg,"Rechazar postulante");
		}
	}
	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
}

var verViaje = function(){
	window.location = "detalle_viaje.html?id="+idViaje;
}

var estadoString = function (caracter) {
	switch (caracter) {
		case 'postulado': return "Postulado";
		case 'aceptado': return "Aceptado";
		case 'rechazado': return "Rechazado";
		case 'cancelado': return "Cancelado";
		case 'ausente': return "Ausente";
		case 'finalizo_viaje': return "Finalizó viaje";
		case null: return "No especificado";
		default: return "Desconocido";
	}
}

var colorPanel = function(caracter){
	switch (caracter) {
		case 'postulado': return "info";
		case 'aceptado': return "success";
		case 'rechazado': return "danger";
		case null: return "default";
		default: return "default";
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

var modalMessage = function (modalName,textMsg,titleMsg) {
	$('#'+modalName+'-message').text(textMsg);
	if (titleMsg){
		$('#modal-'+modalName+" .dialog-title").text(titleMsg);
	}
	$('#modal-'+modalName).modal('show');
}
var closeModal = function (name) {
	$('#modal-' + name).modal('hide');
}

function getUrlVars() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
		vars[key] = value;
	});
	return vars;
}

var viajes = [];
var magic = {};

initUI = function() {
	/* Bootstrap */
	$('button').addClass('btn');
	$('table').addClass('table table-hover');
	$('input, select, textarea').addClass('form-control');
	$('label').addClass('control-label');

	$('#fechadesde').datetimepicker({
        format: 'dd/mm/yyyy',
    	language: "es",
    	startView: 3,
    	minView: 2,
    	maxView: 2,
    	autoclose: true,
    	todayBtn: true
	});
	var ahora = new Date();
	var anio = ahora.getFullYear();
	var mes = ahora.getMonth()+1;
	if(mes<10) {mes = "0"+mes;}
	var dia = ahora.getDate();
	$('#fechadesde').val(dia+"/"+mes+"/"+anio);
	$('#fechahasta').datetimepicker({
        format: 'dd/mm/yyyy',
    	language: "es",
    	startView: 3,
    	minView: 2,
    	maxView: 2,
    	autoclose: true,
    	todayBtn: true
	});

	magic.inputOrigen = $('input[name=origen]').magicSuggest({
		method: 'GET',
		data: '/autocompletado',
		mode: 'remote',
		allowFreeEntries: false,
		selectFirst: true,
		maxSelection: 1,
		hideTrigger: true,
		placeholder: 'Buscar Localidades',
		noSuggestionText: 'No hay sugerencias',
		minChars: 3,
		maxSelectionRenderer: function(){},
		minCharsRenderer: function() {},
		dataUrlParams: {
			entity: "localidad"
		}
	});

	magic.inputDestino = $('input[name=destino]').magicSuggest({
		method: 'GET',
		data: '/autocompletado',
		mode: 'remote',
		allowFreeEntries: false,
		selectFirst: true,
		maxSelection: 1,
		hideTrigger: true,
		placeholder: 'Buscar Localidades',
		noSuggestionText: 'No hay sugerencias',
		minChars: 3,
		maxSelectionRenderer: function(){},
		minCharsRenderer: function() {},
		dataUrlParams: {
			entity: "localidad"
		}
	});

	/*-----------*/
	$('.loadingScreen').fadeOut();
};

window.onload = function () {
	initUI();
	$("#panel-resultados").hide();
}

var showViajes = function(){
	$("#panel-resultados").show();
	if (viajes.length){
		/*
		jsonData.viajes.forEach(function (elem){
			html += Mustache.render(template, elem);
		});
		*/
		autogeneratePages();
		changePage(1);
	}else{
		var html = 	"<div class='jumbotron'>"
						+"<h4 class='text-center text-primary'>No hay resultados</h4>"
					+"</div>"
		$("#busqueda-paginacion").hide();
		$("#viajes-busqueda").html(html);
	}

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

	$("#busqueda-paginacion").html(prevPage+html+nextPage);
}

function changePage(page){
	current_page = page;

    var btn_prev = $("#previous-page");
    var btn_next = $("#next-page");
    var listing_table = $("#viajes-busqueda");

    // Validate page
    if (page < 1) page = 1;
    if (page > numPages()) page = numPages();

    var html = "";
	var template = $("#viaje-template").html();
    for (var i = (page-1) * records_per_page; i < (page * records_per_page); i++) {
			if (viajes[i]){
				viajes[i].reputacion_stars = aux.reputacionStars(viajes[i].reputacion);
				viajes[i].foto = viajes[i].foto || "/img/perfil/default.png";
				viajes[i].fecha_inicio = vc.toFechaLocal(viajes[i].fecha_inicio);
				for (var j=0; j<viajes[i].recorrido.length; j++){
					viajes[i].recorrido[j].es_destino = j==viajes[i].recorrido.length-1;
				}
			  html += Mustache.render(template, viajes[i]);
			}
    }
    listing_table.html(html);

	$( "#busqueda-paginacion").find(".active").removeClass("active");
	$( "#busqueda-paginacion li:eq("+page+")" ).addClass("active");

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
    return Math.ceil(viajes.length / records_per_page);
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

var buscarViaje = function(){
	var sendData = {};

	sendData.action="buscar";
	sendData.entity="viaje";
	sendData.origen = magic.inputOrigen.getValue()[0];
	sendData.destino = magic.inputDestino.getValue()[0];
	sendData.fecha_desde = vc.fechaAMD($('#formBusqueda input[name=fechadesde]').val());
	sendData.fecha_hasta = vc.fechaAMD($('#formBusqueda input[name=fechahasta]').val());
	sendData.conductor = $('#formBusqueda input[name=conductor]').val().toLowerCase();
	sendData.estado = $('#formBusqueda select[name=estadoviaje]').val();

	//simular(sendData, showViajes);
	var onsuccess = function(jsonData) {
		viajes = jsonData.viajes;
		showViajes();
	}
	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);

}

var simular = function (sendData, onsuccess) {

	//SIMULO DATA RECIBIDA:
	var jsonData = {
		"result" : "true",
		"viajes" : [{
			"id" : 3,
			"origen" : "Luján",
			"destino" : "Pilar",
			"fecha_inicio" : "2016-05-17",
			"estado" : "0",
			"conductor" : "Carlos Ruiz",
			"reputacion" : "3",
			"precio": "200",
			"foto":"upload/foto.jpg"
		},{
			"id" : 10,
			"origen" : "Jauregui",
			"destino" : "La quiaca",
			"fecha_inicio" : "2016-03-12",
			"estado" : "1",
			"conductor" : "Lisandro Pedrera",
			"reputacion" : "1",
			"precio": "200",
			"foto":""
		},{
			"id" : 123,
			"origen" : "Navarro",
			"destino" : "Navarro",
			"fecha_inicio" : "2016-05-17",
			"estado" : "1",
			"conductor" : "Maria Cardenas",
			"reputacion" : 3,
			"precio": "456",
			"foto":"img/home/administracion_usuarios.png"
		},{
			"id" : 2,
			"origen" : "San Luis",
			"destino" : "Rosario",
			"fecha_inicio" : "2012-12-27",
			"estado" : "0",
			"conductor" : "Renata Lopez",
			"reputacion" : "5",
			"precio": "290",
			"foto":"upload/foto.jpg"
		}]
	};
	viajes = jsonData.viajes;
	onsuccess();

	/*
	console.log("Data a enviar: ",sendData);
	$.ajax({
		url: '/viajes',
		method: 'POST',
		data: sendData,
		dataType: 'json',
		success: function (jsonData) {
			DEBUGresponse = jsonData;
			if (jsonData.result) {
				viajes = jsonData.viajes;
				onsuccess ();
			} else {
				errorMessage (jsonData.msg);
			}
		},
		error: function (er1, err2, err3) {
			document.body.innerHTML = er1.responseText;
			window.alert (err3);
		}
	});
	*/
}

aux = {};
aux.disableButtons = function () {
	$('#verViajeButton').prop('disabled', true);
}

aux.enableButtons = function () {
	$('#verViajeButton').prop('disabled', false);
}

aux.estadoString = function (caracter) {
	switch (caracter) {
		case '2': return "Terminado";
		case '0': return "No iniciado";
		case '1': return "Iniciado";
		case '3': return "Cancelado";
		case '': return "No especificado";
		case null: return "No especificado";
		default: return "Desconocido";
	}
}

aux.reputacionStars = function(caracter){
	caracter = Math.round(caracter);
	var stars = "";
	while (caracter > 0){
		stars += "★";
		caracter--;
	}
	return stars;
}

//MODALS

errorMessage = function (textMsg) {
	$('#errorMessage').text(textMsg);
	$('#modalError').modal('show');
}
closeModal = function (name) {
	$('#modal' + name).modal('hide');
}

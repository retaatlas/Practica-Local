var nombre_usuario = getUrlVars()["usuario"];
var viajes = [];

var loadData = function() {

	var sendData = {
		entity: "viaje",
		action: "ver_mis_viajes",
		nombre_usuario: nombre_usuario,
	}
	var onsuccess = function(jsonData){
		console.log("recibo: ",jsonData);
		if(jsonData.result){
			$('.loadingScreen').fadeOut();
			viajes = jsonData.viajes;
			showViajes();
		} else if (jsonData.redirect != undefined) {
			window.location = jsonData.redirect;
		}
	}
	//simular();

	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);

}

var initUI = function(){
	ocultarPaneles();
	loadData();
	$('[data-toggle="tooltip"]').tooltip();
	$('#fechadesde, #fechahasta').datetimepicker({
        format: 'dd/mm/yyyy',
    	language: "es",
    	startView: 3,
    	minView: 2,
    	maxView: 2,
		autoclose: true,
    	todayBtn: true,
		clearBtn: true,
	});
}

var ocultarPaneles = function(){
	$("#panel-mis-viajes").hide();
	$("#formFilter").hide();
}

window.onload=initUI;

var simular = function(){
	//DESACTUALIZADO, VER CONSOLE.LOG PARA SABER LO QUE SE RECIBE
	viajes =  [{
			"id" : 3,
			"origen" : "Luján",
			"destino" : "Pilar",
			"fecha_inicio" : "2016-05-17",
			"conductor" : "Carlos Ruiz",
			"reputacion" : "3",
			"precio": "200",
			"foto":"upload/foto.jpg"
		},{
			"id" : 10,
			"origen" : "Jauregui",
			"destino" : "La quiaca",
			"fecha_inicio" : "2016-03-12",
			"conductor" : "Lisandro Pedrera",
			"reputacion" : "1",
			"precio": "200",
			"foto":""
		},{
			"id" : 123,
			"origen" : "Navarro",
			"destino" : "Navarro",
			"fecha_inicio" : "2016-05-17",
			"conductor" : "Maria Cardenas",
			"reputacion" : 3,
			"precio": "456",
			"foto":"img/home/administracion_usuarios.png"
		},{
			"id" : 2,
			"origen" : "San Luis",
			"destino" : "Rosario",
			"fecha_inicio" : "2012-12-27",
			"conductor" : "Renata Lopez",
			"reputacion" : "5",
			"precio": "290",
			"foto":"upload/foto.jpg"
		}];
	showViajes();
}

var filterer = function(item){
	var rctLocalidad = omitirAcentos($('#formFilter input[name=localidad]').val().toLowerCase());
	var rctConductor = omitirAcentos($('#formFilter input[name=conductor]').val().toLowerCase());
	var rctFechaDesde = $('#formFilter input[name=fechadesde]').val();
	var rctFechaHasta = $('#formFilter input[name=fechahasta]').val();
	var rctPrecioDesde = Number($('#formFilter input[name=preciodesde]').val());
	var rctPrecioHasta = Number($('#formFilter input[name=preciohasta]').val());

	rctFechaDesde = (rctFechaDesde === "")? "" : new Date(Date.parse(vc.fechaAMD(rctFechaDesde).split(" ").join("T")));
	rctFechaHasta = (rctFechaHasta === "")? "" : new Date(Date.parse(vc.fechaAMD(rctFechaHasta).split(" ").join("T")));
	var destinoNormalizado = omitirAcentos(item.destino.toLowerCase());
	var origenNormalizado = omitirAcentos(item.origen.toLowerCase());
	var conductorNormalizado = omitirAcentos(item.conductor.toLowerCase());

	//busco si localidad se encuentra en recorrido
	var esLocalidad = false;
	var i=0;
	while (!esLocalidad && i<item.recorrido.length){
		var loc = omitirAcentos(item.recorrido[i].nombre_localidad.toLowerCase());
		esLocalidad = esLocalidad || loc.contains(rctLocalidad);
		i++;
	}

	var fecha = new Date (Date.parse(item.fecha_inicio.split(" ").join("T")));

	return (esLocalidad)
			&& (conductorNormalizado.contains(rctConductor))
			&& (fecha >= rctFechaDesde || rctFechaDesde==="")
			&& (fecha <= rctFechaHasta || rctFechaHasta==="")
			&& (item.precio >= rctPrecioDesde || rctPrecioDesde==0)
			&& (item.precio <= rctPrecioHasta || rctPrecioHasta==0);
}

var showViajes = function(){
	$("#panel-mis-viajes").show();
	if (viajes.filter(filterer).length){
		$("#busqueda-paginacion").show();
		autogeneratePages();
		changePage(1);
	}else{
		var msg = (viajes.length)? "No hay viajes": "Aun no ha realizado Viajes" ;
		var html = 	"<div class='jumbotron'>"
						+"<h4 class='text-center text-primary'>"+msg+"</h4>"
					+"</div>"
		$("#busqueda-paginacion").hide();
		$("#mis-viajes").html(html);
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


    // Validate page
    if (page < 1) page = 1;
    if (page > numPages()) page = numPages();

    for (var i = (page-1) * records_per_page; i < (page * records_per_page); i++) {
		generarHtmlViaje(i);
    }

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
    return Math.ceil(viajes.filter(filterer).length / records_per_page);
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

var generarHtmlViaje = function(indiceViaje){
	if (indiceViaje % records_per_page == 0) $("#mis-viajes").html("");
	var viaje = viajes.filter(filterer)[indiceViaje];
	if (viaje){
		viaje.foto_revisada = viaje.foto || "/img/perfil/default.png";
		var template = $("#viaje-template").html();
		viaje.reputacion_stars = reputacionStars(viaje.reputacion);
		viaje.fecha_inicio_revisada = vc.toFechaLocal(viaje.fecha_inicio);
		for (var i=0; i<viaje.recorrido.length; i++){
			viaje.recorrido[i].es_destino = i==viaje.recorrido.length-1;
		}
		$("#mis-viajes").append(Mustache.render(template, viaje));
	}
}

var toggleFilter = function(){
	if ($("#formFilter").is(":visible")){
		$("#formFilter input").attr("disabled",true);
		$("#formFilter").slideUp();
	}else{
		$("#formFilter input").attr("disabled",false);
		$("#formFilter").slideDown();
	}
}


reputacionStars = function(caracter){
	caracter = Math.round(caracter);
	var stars = "";
	while (caracter > 0){
		stars += "★";
		caracter--;
	}
	return stars;
}

// funciones robadas

/* Para navegadores viejos */
if (Array.prototype.includes == undefined) {
	Array.prototype.includes = function (item) {
		return this.indexOf(item) != -1;
	}
}

if (String.prototype.contains == undefined) {
	String.prototype.contains = function (item) {
		return this.indexOf(item) != -1;
	}
}

function getUrlVars() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
		vars[key] = value;
	});
	return vars;
}

function omitirAcentos(text) {
    var acentos = "ÃÀÁÄÂÈÉËÊÌÍÏÎÒÓÖÔÙÚÜÛãàáäâèéëêìíïîòóöôùúüûÑñÇç";
    var original = "AAAAAEEEEIIIIOOOOUUUUaaaaaeeeeiiiioooouuuunncc";
    for (var i=0; i<acentos.length; i++) {
        text = text.replace(acentos.charAt(i), original.charAt(i));
    }
    return text;
}

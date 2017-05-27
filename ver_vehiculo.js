var idVehiculo = getUrlVars()["id"];
var dataVehiculo = {}
dataVehiculo.conductores = [];
dataVehiculo.vehiculo = {};

var simular = function(){
	dataVehiculo.conductores = [{
		id: "48",
		nombre_usuario: "Lucho85"
	},{
		id: "34",
		nombre_usuario: "Lore92"
	}, {
		id: "15",
		nombre_usuario: "Pepa15"
	},{
		id: "2",
		nombre_usuario: "Lolo91"
	}];
	dataVehiculo.vehiculo = {
			id: "1",
			marca: "ford",
			modelo: "focus",
			color: "303F9F",
			anio: "2010",
			patente: "ifg 999",
			seguro: "S",
			aire: "S",
			cantidad_asientos: "4",
			//cliente_vinculado: ["48"],
			foto: "http://www.coches.com/fotos_historicas/ford/Focus/med_ford_focus-2014_r3.jpg.pagespeed.ce.JW0wSPihZv.jpg",
			vehiculo_verificado: "S"
	}
	cargarDataVehiculo();
};

var loadData = function() {

	var sendData = {
		entity: "vehiculo",
		action: "ver_un_vehiculo",
		"id_vehiculo": idVehiculo
	}
	var onsuccess = function(jsonData){
		if(jsonData.result){
			$('.loadingScreen').fadeOut();
			dataVehiculo.vehiculo = jsonData.vehiculo;
			dataVehiculo.conductores = jsonData.conductores;
			cargarDataVehiculo();
		} else if (jsonData.redirect != undefined) {
			window.location = jsonData.redirect;
		}else{
			modalMessage('error',jsonData.msg)
		}
	}

	//simular();
	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
}
var magic = {};
var initUI = function(){
	magic.inputConductores = $('form input[name=conductores]').magicSuggest({
		method: 'GET',
		data: '/autocompletado',
		mode: 'remote',
		allowFreeEntries: false,
		selectFirst: true,
		hideTrigger: true,
		placeholder: 'Buscar Conductores',
		noSuggestionText: 'No hay sugerencias',
		minChars: 3,
		maxSelectionRenderer: function(){},
		minCharsRenderer: function() {},
		dataUrlParams: {
			entity: "cliente"
		}
	});

	loadData();
}

window.onload=initUI;

var cargarDataVehiculo = function(){
	//Limpio UI
	$("#panel-vehiculo").empty();

	if (dataVehiculo.vehiculo){
		dataVehiculo.vehiculo.foto_revisada = dataVehiculo.vehiculo.foto || "img/vehiculo/vehiculo.png";
	}
	dataVehiculo.vehiculo.tieneSeguro = dataVehiculo.vehiculo.seguro == "S";
	dataVehiculo.vehiculo.tieneAire = dataVehiculo.vehiculo.aire == "S";
	dataVehiculo.vehiculo.color_hex = "#"+dataVehiculo.vehiculo.color;
	var template = $("#vehiculo-template").html();
	var html = Mustache.render(template,dataVehiculo);
	$("#panel-vehiculo").html(html);
	setearEventos();
}

function setearEventos(){

	$('[data-toggle="tooltip"]').tooltip();

	new jscolor($('.jscolor')[0]);
	$("input[type='file']").change(function(){
		readURL(this);
	});
	$('#foto_vehiculo').load(function() {
		var imageSrc = $(this).attr("src");
		if (imageSrc != dataVehiculo.vehiculo.foto && imageSrc != dataVehiculo.vehiculo.foto_revisada){
			enviarFoto(imageSrc);
		}
	});

	$("#tablaVehiculo input[name=anio]").attr("max",new Date().getFullYear());
	$("#tablaVehiculo input[name=anio]").blur(validarAnio);
	$("#tablaVehiculo input[name=color]").blur(validarCampoObligatorio);
	$("#tablaVehiculo select[name=seguro]").blur(validarSioNo);
	$("#tablaVehiculo select[name=aire]").blur(validarSioNo);
	$("#tablaVehiculo input[name=cantidad_asientos]").blur(validarAsientos);
}

var enviarFoto = function(src){
	var sendData = {
		entity: "vehiculo",
		"id_vehiculo" : dataVehiculo.vehiculo.id,
		action : "modificar_imagen_vehiculo",
		foto: src
	}
	var onsuccess = function(jsonData){
		if (jsonData.result){
			loadData();
		} else{
			modalMessage("error",jsonData.msg,"Error al subir imagen de vehiculo");
		}
	}
	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
}
//-----------------------------Asignar/Desasignar Conductor------------------------------------------------------------//
var activarAsignarConductor = function(){
	magic.inputConductores.clear();
	$('#modal-asignar-conductor').modal('show');
}
var eliminarVehiculo = function(){

	var msg = "¿Esta seguro que desea eliminar el vehículo? Esta acción no puede deshacerse";
	var title= "Eliminar Vehículo";
	var btn = document.createElement("BUTTON");
	btn.className="btn btn-danger dinamico";
	btn.innerHTML = "<span class='glyphicon glyphicon-tint'></span>"+" Confirmar";
	btn.name = "confirmar";
	btn.onclick=confirmar;
	modalButton(modalName,btn);
	modalMessage(modalName,msg,title);
}
var desasignarConductor = function(id){
	var modalName='warning';
	var confirmar = function(){
		closeModal(modalName);
		var sendJson = {
			entity: "vehiculo",
			action: "desasignar_vehiculo_cliente",
			"id_vehiculo":dataVehiculo.vehiculo.id,
			"id_conductor": id
		}
		var onsuccess = function(jsonData){
			if (jsonData.result == true && jsonData.redirect != undefined) {
				window.location = jsonData.redirect;
			}
			loadData();
			modalMessage("success",jsonData.msg,"Desasignar conductor");
		}
		vc.peticionAjax("/viajes",sendJson,"POST",onsuccess);
	}
	var msg = "¿Esta seguro que desea desasignar a este conductor del vehículo? Esta acción no puede deshacerse";
	var title= "Desasignar conductor";
	var btn = document.createElement("BUTTON");
	btn.className="btn btn-danger dinamico";
	btn.innerHTML = "<span class='glyphicon glyphicon-warning-sign'></span>"+" Confirmar";
	btn.name = "confirmar";
	btn.onclick=confirmar;
	modalButton(modalName,btn);
	modalMessage(modalName,msg,title);
}

var asignarConductores = function(){
	closeModal("asignar-conductor");

	var conductores = magic.inputConductores.getValue();
	console.log(conductores);
	if (conductores.length > 0){
		var sendJson = {
			entity: "vehiculo",
			action: "asignar_vehiculo_clientes",
			id:dataVehiculo.vehiculo.id,
			conductores: conductores
		}
		var onsuccess = function(jsonData){
			if (jsonData.result == false && jsonData.redirect != undefined) {
				window.location = jsonData.redirect;
			}
			loadData();
			modalMessage("success",jsonData.msg,"Asignar conductores a vehículo");
		}
		vc.peticionAjax("/viajes",sendJson,"POST",onsuccess);
	}
}

//-----------------------------------------------MODIFICAR--------------------------------------------------
var activarModificar = function(){
	$("#table-vehiculo input, #table-vehiculo select").attr("disabled",false);
	generarNuevosBotones();
}

var generarNuevosBotones = function(){
	var btnGuardar = "<button class='btn btn-success' onclick='modificarVehiculo();'>"
		+"<span class='glyphicon glyphicon-check'></span> Guardar"
		+"</button>";
	var btnCancelar = "<button class='btn btn-danger' onclick='cancelarModificar();'>"
		+"<span class='glyphicon glyphicon-remove'></span> Cancelar"
		+"</button>";
	var html = "<div class='btn-group'>"+btnCancelar+btnGuardar+"</div>";
	$("#botonera-modificar-vehiculo").html(html);
}

var cancelarModificar = function(){
	cargarDataVehiculo();
}

var modificarVehiculo = function(){
	var sendData = {
		entity:"vehiculo",
		action: "modificar_vehiculo",
		vehiculo: dataVehiculo.vehiculo
	}
	sendData.vehiculo.anio = $("table input[name=anio]").val();
	sendData.vehiculo.color = $("table input[name=color]").val();
	sendData.vehiculo.cantidad_asientos = $("table input[name=cantidad_asientos]").val();
	sendData.vehiculo.seguro = $("table select[name=seguro]").val();
	sendData.vehiculo.aire = $("table select[name=aire]").val();

	var onsuccess = function(jsonData){
		if (jsonData.result){
			loadData();
		} else{
			modalMessage("error", jsonData.msg, "Modificar Vehículo");
		}
	}

	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
}

var eliminarVehiculo = function(){
	var modalName='warning';
	var confirmar = function(){
		closeModal(modalName);
		var sendJson = {
			entity: "vehiculo",
			action: "eliminar_vehiculo",
			id: dataVehiculo.vehiculo.id
		}
		var onsuccess = function(jsonData){
			if (jsonData.result){
				window.location = jsonData.redirect;
			}else{
				if (jsonData.redirect != undefined){
					window.location = jsonData.redirect;
				}else if(jsonData.msg != undefined){
					modalMessage("error",jsonData.msg,"Eliminar Vehículo");
				}
			}
		}
		vc.peticionAjax("/viajes", sendJson, "POST", onsuccess);
	}
	var msg = "¿Esta seguro que desea eliminar el vehículo? Esta acción no puede deshacerse";
	var title= "Eliminar Vehículo";
	var btn = document.createElement("BUTTON");
	btn.className="btn btn-danger dinamico";
	btn.innerHTML = "<span class='glyphicon glyphicon-tint'></span>"+" Confirmar";
	btn.name = "confirmar";
	btn.onclick=confirmar;
	modalButton(modalName,btn);
	modalMessage(modalName,msg,title);
}

var mostrarSiNo = function(caracter) {
	switch(caracter) {
		case 'S': return "Sí";
		case 'N': return "No";
		case 'Sí': return "Sí";
		case 'No': return "No";
	}
}
//-----------------------------------------------VALIDACIONES------------------------------------------------
var validarCampoObligatorio = function(){
	var input = $(this);
	if (input.val().length==0){
		customAlert(input, "Completar campo obligatorio");
	}else{
		customAlertSuccess(input);
	}
}

var validarSioNo = function(){
	var input = $(this);
	var valor = input.val().toLowerCase();
	if (valor.length > 0){
		if (valor == "s" || valor == "n"){
			customAlertSuccess(input);
		}else{
			customAlert(input,"Valores válidos son: 'Sí' y 'No'");
		}
	}else{
		customAlert(input, "Completar campo obligatorio");
	}
}

var validarAsientos = function(){
	var input = $(this);
	var valor = parseInt(input.val());
	if (valor > 0){
		if (valor>0 && valor <=30){
			customAlertSuccess(input);
		}else{
			customAlert(input,"Demasiados asientos asignados a este vehículo");
		}
	}else{
		customAlert(input, "Completar campo obligatorio");
	}
}

var validarAnio = function(){
	var input = $(this);
	var valor = input.val();
	if (valor.length > 0){
		var anio = new Date().getFullYear();
		if (anio >= valor){
			customAlertSuccess(input);
		}else{
			customAlert(input,"El año no puede ser mayor que el actual");
		}
	}else{
		customAlert(input, "Completar campo obligatorio");
	}
}

var customAlert = function(elemento,msg){
	var mensaje = msg;
	var popoverTemplate = ['<div class="popover-error popover">',
        '<div class="arrow arrow-error"></div>',
        '<div class="popover-content popover-content-error">',
        '</div>',
        '</div>'].join('');
	$(elemento).popover({
		animation: true,
		trigger: 'manual',
		placement: 'top',
		template: popoverTemplate,
		content: function() {
			return mensaje;
		}
	});
	$(elemento).popover("show");
	$(elemento).closest("tr").removeClass('has-success').addClass('has-error');

	$(elemento).focus(function(){
		$(elemento).popover("destroy");
		$(elemento).closest("tr").removeClass('has-error')
	});
}

var customAlertSuccess = function(elemento){
	var popoverTemplate = ['<div class="popover-success popover">',
        '<div class="arrow arrow-success"></div>',
        '<div class="popover-content popover-content-success">',
        '</div>',
        '</div>'].join('');
	$(elemento).popover({
		animation: true,
		trigger: 'manual',
		placement: 'left',
		template: popoverTemplate,
		html:true,
		content: function() {
			return "<span class='glyphicon glyphicon-ok'></span>";
		}
	});
	$(elemento).popover("show");
	$(elemento).closest("tr").removeClass('has-error').addClass('has-success');

	$(elemento).focus(function(){
		$(elemento).popover("destroy");
		$(elemento).closest("tr").removeClass('has-success')
	});
}

var siNoCaracter = function(entrada){
	switch (entrada.toLowerCase()) {
		case 'si': return "S";
		case 'no': return "N";
		case 'sí': return "S";
		case '': return "";
		case null: return "";
		default: return "";
	}
}

var imagenValida = function(file){
	var maxTam = 1500000; // tamano maximo 1.5MB
	if (file.size >= maxTam){
		modalMessage("error", "Archivo es demasiado grande", "Modificar Imagen");
		return false;
	}
    if (file.type.indexOf("image") == -1){
		modalMessage("error", "Debe seleccionar una imagen", "Modificar Imagen");
		return false;
	}
	return true;
}

function readURL(input) {
	var id = $(input).attr("name");
    if (input.files && input.files[0] && imagenValida(input.files[0])) {
		var reader = new FileReader();

		reader.onload = function (e) {
			$("#"+id).attr('src', e.target.result).show();
		}

		reader.readAsDataURL(input.files[0]);
    }
}

// modal

$(document).on('hide.bs.modal', function (e) {
  $(".dinamico").each(function(){
	$(this).remove();
  });
});

var modalMessage = function (modalName,textMsg,titleMsg) {
	$('#'+modalName+'-message').text(textMsg);
	if (titleMsg){
		$('#modal-'+modalName +" .dialog-title").text(titleMsg);
	}
	$('#modal-'+modalName).modal('show');
}
var closeModal = function (name) {
	$('#modal-' + name).modal('hide');
}
var modalButton = function(modalName,btn){
	$('#modal-'+modalName+' .modal-footer').append(btn);
}
// funciones robadas // ladron que roba a ladron


function getUrlVars() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
		vars[key] = value;
	});
	return vars;
}

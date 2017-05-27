var usuario_perfil = getUrlVars()["usuario"];
var data = {}

data.persona = {}
data.usuario = {};
data.cliente = {}
data.usuario_logueado = {};
data.sponsor= {};
data.super_usuario = {};

var loadData = function() {

	var sendData = {
		usuario_perfil: usuario_perfil
	}
	var onsuccess = function(jsonData){
		if(jsonData.result){
			console.log("Me traje: ",jsonData);
			$('.loadingScreen').fadeOut();
			data.usuario_logueado = jsonData.usuario_logueado;
			data.cliente = jsonData.cliente;
			data.sponsor = jsonData.sponsor;
			data.super_usuario = jsonData.super_usuario; // El admin no tiene datos propios asi que esto quedara vacio.
			data.persona = jsonData.persona;
			data.usuario = jsonData.usuario;
			cargarPerfil();
		} else if (jsonData.redirect != undefined) {
			window.location = jsonData.redirect;
		}
	}
	$.ajax({
		url: '/perfil',
		dataType: 'json',
		method: 'GET',
		data: sendData,
		success: function (jsonData) {
			DEBUGresponse = jsonData;
			onsuccess(jsonData);
		},
		error: function (er1, err2, err3) {
			document.body.innerHTML = er1.responseText;
			window.alert (err3);
		}
	});
}
var magic = {};
var initUI = function(){
	loadData();
	$('[data-toggle="tooltip"]').tooltip();
		magic.inputCliente = $('form input[name=cliente]').magicSuggest({
			method: 'GET',
			data: '/autocompletado',
			mode: 'remote',
			allowFreeEntries: false,
			selectFirst: true,
			maxSelection: 1,
			hideTrigger: true,
			placeholder: 'Buscar usuario',
			noSuggestionText: 'No hay sugerencias',
			minChars: 3,
			maxSelectionRenderer: function(){},
			minCharsRenderer: function() {},
			valueField: 'nombre_usuario',
			dataUrlParams: {
				entity: "cliente"
			}
		});
}

window.onload=initUI;

var cargarPerfil = function(){
	//Limpio UI
	$("#panel-perfil").empty();

	// TRADUZCO DATA
	if (data.cliente){
		data.cliente.foto_revisada = data.cliente.foto || "img/perfil/default.png";
		data.cliente.foto_registro_revisada = data.cliente.foto_registro || "img/perfil/sin_registro.jpg";
		data.cliente.reputacion_stars =  reputacionStars(data.cliente.reputacion);
	}
	data.persona.tipo_doc_string = tipoDocString(data.persona.tipo_doc);
	data.persona.fecha_revisada = data.persona.fecha_nacimiento.replace(/-/g, '\/').split("/").reverse().join("/");//vc.toFechaLocal(data.persona.fecha_nacimiento.replace(/-/g, '\/')).split(" ")[0];
	data.persona.esM = data.persona.sexo == "M";
	data.persona.esF = data.persona.sexo == "F";
	data.persona.esO = data.persona.sexo == "O";

	// GENERO HTML DINAMICO
	var template = $("#perfil-template").html();
	$("#panel-perfil").append(Mustache.render(template,data));
	$(".ocultable").hide();
	setearEventos();

	function convertDate(inputFormat) {
	  function pad(s) { return (s < 10) ? '0' + s : s; }
	  var d = new Date(inputFormat);
	  return [pad(d.getDate()), pad(d.getMonth()+1), d.getFullYear()].join('/');
	}
}

function setearEventos(){
	$('#fecha').datetimepicker({
				format: 'dd/mm/yyyy',
			language: "es",
			startView: 3,
			minView: 2,
			maxView: 2,
		autoclose: true,
			todayBtn: true,
			endDate: new Date(),
		clearBtn: true,
	});

	$("input[type='file']").change(function(){
		readURL(this);
	});
	$('#foto_perfil').load(function() {
		var imageSrc = $(this).attr("src");
		if (imageSrc != data.cliente.foto && imageSrc != data.cliente.foto_revisada){
			enviarFoto("perfil",imageSrc);
		}
	});
	$('#foto_registro').load(function() {
		var imageSrc = $(this).attr("src");
		if (imageSrc != data.cliente.foto_registro && imageSrc != data.cliente.foto_registro_revisada){
			enviarFoto("registro",imageSrc);
		}
	});
	$("#tableCliente input").not(".validar").blur(validarCampoObligatorio);
	$("#tableCliente input[name='mail-cliente']").blur(validarMail);
	$("#tableCliente input[name='pass-cliente']").blur(validarPass);
	//$("#tableCliente select[name=sexo]").blur(validarSexo);
}

var enviarFoto = function(atributo, src){
	var sendData = {
		nombre_usuario : data.usuario.nombre_usuario,
		action : "modificar_imagen"
	}
	if (atributo == "registro"){
		sendData.foto_registro = src;
	} else if (atributo == "perfil"){
		sendData.foto = src;
	}
	var onsuccess = function(jsonData){
		if (jsonData.result){
			loadData();
		}
	}
	vc.peticionAjax ("/perfil", sendData, "POST", onsuccess);
}

var activarModificar = function(){
	$("#table-perfil input,#table-perfil select").attr("disabled",false);
	$(".ocultable").show();
	generarNuevosBotones();
}

var generarNuevosBotones = function(){
	var btnGuardar = "<button class='btn btn-success' onclick='modificarPerfilCliente();'>"
		+"<span class='glyphicon glyphicon-check'></span> Guardar"
		+"</button>";
	var btnCancelar = "<button class='btn btn-danger' onclick='cancelarModificar();'>"
		+"<span class='glyphicon glyphicon-remove'></span> Cancelar"
		+"</button>";
	var html = "<div class='btn-group'>"+btnCancelar+btnGuardar+"</div>";
	$("#botonera-modificar-cliente-"+data.usuario.nombre_usuario).html(html);
}

var cancelarModificar = function(){
	cargarPerfil();
}

var desactivarCuenta = function(){
	var modalName='warning';
	var confirmar = function(){
		closeModal(modalName);
		var sendJson = {
			action: "desactivar_cuenta",
			nombre_usuario: data.usuario.nombre_usuario
		}
		var onsuccess = function(jsonData){
			if (jsonData.result){
				window.location = jsonData.redirect;
			}else{
				modalMessage("error",jsonData.msg,"Desactivar Cuenta");
			}
		}
		vc.peticionAjax("/perfil", sendJson, "POST", onsuccess);
	}
	var msg = "¿Esta seguro que desea desactivar su cuenta? Esta acción no puede deshacerse";
	var title= "Desactivar Cuenta";
	var btn = document.createElement("BUTTON");
	btn.className="btn btn-danger dinamico";
	btn.innerHTML = "<span class='glyphicon glyphicon-tint'></span>"+" Confirmar";
	btn.name = "confirmar";
	btn.onclick=confirmar;
	modalButton(modalName,btn);
	modalMessage(modalName,msg,title);
}

var modificarPerfilCliente = function(){
	if (esValido()){
		var sendData = {
			action: "modificar_cliente",
			nombre_usuario: data.usuario.nombre_usuario,
			usuario:{},
			persona:{},
			cliente:{}
		}
		sendData.usuario.mail = $("table input[name=mail-cliente]").val();
		sendData.usuario.pass = $("table input[name=pass-cliente]").val();
		sendData.persona.domicilio = $("table input[name=domicilio-cliente]").val();
		sendData.persona.telefono = $("table input[name=telefono-cliente]").val();
		sendData.persona.apellidos = $("table input[name=apellidos-cliente]").val();
		sendData.persona.nombres = $("table input[name=nombres-cliente]").val();
		sendData.persona.fecha_nacimiento = vc.fechaAMD($("#tableCliente input[name=fecha_nacimiento]").val());
		sendData.persona.sexo = $("#tableCliente select[name=sexo]").val();

		var onsuccess = function(jsonData){
			if (jsonData.result){
				loadData();
			} else{
				modalMessage("error", jsonData.msg, "Modificar perfil");
			}
		}

		vc.peticionAjax("/perfil", sendData, "POST", onsuccess);
	}else{
		$("body").get(0).scrollIntoView();
	}

}

// BUSCAR cliente

var activarBuscarCliente = function(){
	magic.inputCliente.clear();
	$('#modal-buscar-cliente').modal('show');
}
var buscarCliente = function(){
	var cliente = magic.inputCliente.getValue();
	if (cliente.length){
		window.location="/perfil.html?usuario="+cliente;
	}
}
//validaciones//

var esValido = function(){
	//$("table"+" input").focus();
	//$("table"+" input").last().blur();
	return $("table").find(".has-error").length == 0;
}

var validarCampoObligatorio = function(){
	var input = $(this);
	if (input.val().length==0){
		customAlert(input, "Completar campo obligatorio");
	}else{
		customAlertSuccess(input);
	}
}
var validarMail = function(){
  var inputMail = $(this);
  var regex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  if (inputMail.val().length > 0){
    if (!regex.test(inputMail.val())){
		customAlert(inputMail,"Mail no válido");
	}else{
		var sendData = {
		  action: 'mail_existe',
		  mail: inputMail.val(),
		};
		var onsuccess = function(jsonData){
			if (jsonData.result)
				if (jsonData.es_valido){
					customAlertSuccess(inputMail);
				}else{
					customAlert(inputMail, "Mail existente");
				}
		}
		vc.peticionAjax("/perfil", sendData, "POST", onsuccess);
	}
  }else{
	customAlert(inputMail, "Completar campo obligatorio");
  }
}

var validarPass = function(){
	var inputPass = $(this);
	if (inputPass.val().length > 0){
		if (inputPass.val().length<6){
			customAlert(inputPass,"Mínimo 6 caracteres");
		}else{
			customAlertSuccess(inputPass);
		}
	} else{
		customAlert(inputPass, "Completar campo obligatorio");
	}
}
/*
var validarSexo = function(){
	var inputSexo = $(this);
	var valor = inputSexo.val().toLowerCase();
	if (valor.length > 0){
		if (valor == "masculino" || valor == "femenino" || valor == "otro"){
			customAlertSuccess(inputSexo);
		}else{
			customAlert(inputSexo,"Valores válidos son: 'Masculino', 'Femenino' y 'Otro'");
		}
	}else{
		customAlert(inputSexo, "Completar campo obligatorio");
	}
}
*/
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
////////////////

var tipoDocString = function(caracter){
	switch (caracter) {
		case 1: return "DNI";
		case 2: return "LE";
		case 3: return "LC";
		case '': return "No especificado";
		case null: return "No especificado";
		default: return "Desconocido";
	}
}
var sexoString = function(caracter){
	switch (caracter) {
		case 'O': return "Otro";
		case 'F': return "Femenino";
		case 'M': return "Masculino";
		case '': return "No especificado";
		case null: return "No especificado";
		default: return "Desconocido";
	}
}
var sexoCaracter = function(sexo){
	switch (sexo.toLowerCase()) {
		case 'otro': return "O";
		case 'femenino': return "F";
		case 'masculino': return "M";
		case '': return "";
		case null: return "";
		default: return "";
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

//img
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

//eliminar elementos de modal que fueron generados dinamicamente
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
// funciones robadas

function getUrlVars() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
		vars[key] = value;
	});
	return vars;
}

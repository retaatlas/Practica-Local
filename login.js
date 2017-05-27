var userValido = false;
var passValido = false;


initUI = function() {
	/* Bootstrap */
	$('button').addClass('btn');
	$('table').addClass('table table-hover');
	$('input, select, textarea').addClass('form-control');
	$('label').addClass('control-label');
	/*-----------*/
	// si esta logueado, lo mando a la home
	if (getCookie("nombre_usuario")!="") {
		return window.location.replace("home.html");
	}
};

$(document).ready(function(){
	ocultarMensajesError();
	$("form input").first().focus();
    setearEventos();
	$('.loadingScreen').fadeOut(); 
	$("form input[name='usuario']").val(getUrlVars()["usuario"]);
});

function setearEventos(){
  $('input[name=usuario]').focusout(validarNombreUsuario);
  $('input[name=pass]').focusout(validarPass);
}
 
validarNombreUsuario = function(){
	var inputUsuario = $(this);
	var valor = inputUsuario.val();
	if (valor.length < 6){
		customAlert(inputUsuario, "Mínimo 6 caracteres");
	}
}

validarPass = function(){
	var inputPass = $(this);
	if (inputPass.val().length<6){
		customAlert(inputPass,"Mínimo 6 caracteres");
	}
}
function customAlert(input,msg){
   sendMsgError(msg, input);
   input.closest(".form-group").addClass("has-error");
   input.fadeIn(100).fadeOut(100).fadeIn(100).fadeOut(100).fadeIn(100);
   input.focus(function(){
     input.unbind('focus');//para el IE
     input.closest(".form-group").removeClass("has-error"); 
	 deleteMsgError(input);
   });
}
 
sendMsgError = function(msg, input){
	input.closest(".form-group").find(".msg_error").show();
}

deleteMsgError = function(input){
	input.closest(".form-group").find(".msg_error").hide();
}

function ocultarMensajesError(){
  $(".msg_error").hide();
}
function validarDatos(){
	$("form input").focusout();
	var error=0;
	if ($("form").find(".has-error").length == 0) {
		enviarForm();
	}
	return false;
}
function enviarForm(){
    var sendData = {
      accion: 'login',
      usuario: $("input[name=usuario]").val(),
      pass: $("input[name=pass]").val(),
    };
	var callback = function(jsonData){
	    if (jsonData.result) {
          window.location = jsonData.redirect;
        } else {
		  $("#myModal").find(".modal-body").html(jsonData.msg);
		  $("#myModal").modal();
        }
	}
	sendAjax(sendData,callback);
}
function sendAjax(sendData,callback){
    $.ajax({
      url: '/login',
      method: 'POST',
      data: sendData,
      dataType: 'json',
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

function getCookie(nombreCookie) {
    var nombre = nombreCookie + "=";
    var propiedadesCookies = document.cookie.split(';');
    for(var i=0; i<propiedadesCookies.length; i++) {
        var c = propiedadesCookies[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(nombre) == 0) return c.substring(nombre.length,c.length);
    }
    return "";
}

function getUrlVars() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
		vars[key] = value;
	});
	return vars;
}
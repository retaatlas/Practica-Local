configuracionExitosa=false;

window.onload=function() {
	$('#panel_db').hide();
	$('#panel_progreso').hide();
	$('#panel_adminpass').hide();
	comprobar_configuracion();
}

comprobar_configuracion = function() {
	// Esta funcion deberia enviar una query al servidor para preguntarle si ya esta configurado
	// Si no esta configurado muestra el panel de configuracion
	// Si ya esta configurado muestra el cartel "ya esta configurado" y redirije al index
	$.ajax({
		url: '/dbconfig',
		method: 'POST',
		data: {action: "esta_configurado"},
		dataType: 'json',
		success: function(recv) {
			if (recv.configurado) {
				configuracionExitosa=true;
				cartel_exito();
			} else {
				paso1();
			}
		}
	});
}

paso1 = function() {
	// Esta funcion tiene que mostrar el primer panel
	$('#panel_progreso').hide();
	$('#panel_adminpass').hide();
	$('#panel_db').show();
}

paso2 = function() {
	// Esta funcion tiene que mandar los datos ingresados en el formulario al servlet y mostrar el panel de progreso
	// El panel de progreso tiene que actualizarse cada cierto tiempo para mostrar los cambios

	if (configuracionExitosa) {
		$('#panel_db').hide();
		$('#panel_adminpass').hide();
		$('#panel_progreso').show();
		return null;
	}
	
	$("#buttonVolverAPaso1").prop("disabled", false);
	$("#buttonSiguienteAPaso3").prop("disabled", true);
	$("#progressbar_instalacion").removeClass("progress-bar-success");
	$("#progressbar_instalacion").removeClass("progress-bar-danger");
	$("#progressbar_instalacion").removeClass("active");
	$("#progressbar_instalacion").css({width: "1%"});
	relojitos(0);

	var sendData = {
		action: 'configurar',
		host: $('input[name=db_host]').val(),
		port: $('input[name=db_port]').val(),
		username: $('input[name=db_username]').val(),
		password: $('input[name=db_password]').val(),
		dbname: $('input[name=db_dbname]').val(),
		mode: $('select[name=db_mode]').val()
	}


	$.ajax({
		url: '/dbconfig',
		method: 'POST',
		data: sendData,
		dataType: 'json',
		success: function(recv) {
			if (recv.result) {
				llave_pass = recv.llave;
				$('#panel_db').hide();
				$('#panel_adminpass').hide();
				$('#panel_progreso').show();
				window.setTimeout(comprobar_estado, 1000);
			} else {
				$('#errorMessage').html(recv.msg);
				$('#modalError').modal('show');
			}
		}
	});
	
}

paso3 = function() {
	// Esta funcion se ejecuta cuando se ha completado la instalación
	$('#panel_db').hide();
	$('#panel_progreso').hide();
	$('#panel_adminpass').show();
}

paso4 = function() {
	var pass = $('#adminpass').val();

	if (pass.length < 6) {
		$('#errorMessage').html('La contrase&ntilde;a introducida es demasiado corta.<br>Ingrese una contrase&ntilde;a de al menos 6 caracteres');
		$('#modalError').modal('show');
		return null;
	}

	var sendData = 

	$.ajax({
		url: '/dbconfig',
		method: 'POST',
		data: {
			action: 'set_password',
			password: pass,
			llave: llave_pass
		},
		dataType: 'json',
		success: function(recv) {
			if (recv.result) {
				$('#panel_adminpass').hide();
				cartel_exito();
			} else {
				$('#errorMessage').html(recv.msg);
				$('#modalError').modal('show');
			}
		}
	});
}

comprobar_estado = function() {
	// Esta funcion pregunta al servlet el estado actual del proceso y lo muestra en el panel de progreso
	$.ajax({
		url: '/dbconfig',
		method: 'GET',
		dataType: 'json',
		success: function(recv) {
			var recomprobar = true;
			var pb=$("#progressbar_instalacion");
			switch (recv.estado) {
				case "no_iniciado":
					pb.removeClass("progress-bar-success");
					pb.removeClass("progress-bar-danger");
					pb.removeClass("active");
					pb.css({width: "1%"});
					relojitos(0);
					break;
				case "trabajando":
					pb.removeClass("progress-bar-success");
					pb.removeClass("progress-bar-danger");
					pb.addClass("active");
					var porcentaje = 20*recv.step;
					relojitos(recv.step);
					pb.css({width: porcentaje+"%"});
					break;
				case "completo":
					recomprobar = false;
					pb.removeClass("progress-bar-danger");
					pb.addClass("progress-bar-success");
					pb.removeClass("active");
					pb.css({width: "100%"});
					$("#buttonVolverAPaso1").prop("disabled", true);
					$("#buttonSiguienteAPaso3").prop("disabled", false);
					configuracionExitosa=true;
					relojitos(5);
					break;
				case "fallo":
					recomprobar = false;
					pb.removeClass("progress-bar-success");
					pb.addClass("progress-bar-danger");
					pb.removeClass("active");
					relojitos(recv.step, "cancelado");
					break;
			}
			if (recomprobar) {
				window.setTimeout(comprobar_estado, 1000);
			}
			
		}
	});
}

relojitos = function (step, err) {
	for (var i = 1; i<6; i++) {
		var item = $('#icon-step-'+i)[0];
		if(i<=step) {
			item.className="glyphicon glyphicon-ok";
		} else if(err) {
			item.className="glyphicon glyphicon-remove";
		} else {
			item.className="glyphicon glyphicon-hourglass";
		}
	}
}

cartel_exito = function() {
	$('#successMessage').html("El servidor fue configurado correctamente <br>Ya puede ingresar al sistema con un usuario y una contrase&ntilde;a");
	$('#modalSuccess').modal('show');
}
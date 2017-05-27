mapData = {
	marcadores: []
}

vehiculos = [];

magic={
	origen: null,
	intermedios: [],
	destino: null
}

initMapa = function() {
	mapData.map = new google.maps.Map(document.getElementById('divMapa'), {
		center: {lat: -37, lng: -61},
		drawingControl: false,
		zoom: 4
	});

	mapData.directionsService = new google.maps.DirectionsService();
	mapData.directionsDisplay = new google.maps.DirectionsRenderer({suppressMarkers: true});
	mapData.directionsDisplay.setMap (mapData.map);
}

puntosRecorrido = function(){
	var devolver = [];
	if (magic.origen != null) {
		devolver.push (magic.origen);
	}
	magic.intermedios.forEach(function (item) {
		devolver.push(item);
	});
	if (magic.destino != null) {
		devolver.push (magic.destino);
	}
	return devolver;
}

redibujar = function() {
	/* Quitar marcadores antiguos del mapa */
	mapData.marcadores.forEach(function(item) {
		item.setMap(null);
	});

	/* Vaciar lista de marcadores */
	mapData.marcadores = [];

	/* Borrar ruta anterior */
	mapData.directionsDisplay.setDirections({routes:[]});

	/* Agregar marcadores nuevos al mapa y a la lista */
	var recorrido = puntosRecorrido();
	if (recorrido == null) {
		return null;
	}
	recorrido.forEach(function(item) {

		mapData.marcadores.push (new google.maps.Marker({
			position: {
				lat: item.lat,
				lng: item.lng
			},
			map: mapData.map,
			title: item.nombre
		}));
	});

	/* Mostrar la nueva ruta */
	if (mapData.marcadores.length == 1) {
		mapData.map.setCenter(mapData.marcadores[0].position);
	} else if (mapData.marcadores.length > 1) {
		pedirRuta();
	}
}

listarPuntosIntermedios = function () {
	var puntos = [];
	for (i=1; i<mapData.marcadores.length-1; i++) {
		puntos.push({location:mapData.marcadores[i].position});
	}
	return puntos;
}
pedirRuta = function () {
	var origen = mapData.marcadores[0];
	var destino = mapData.marcadores[mapData.marcadores.length-1];
	var solicitud = {
		origin: origen.position,
		destination: destino.position,
		travelMode:google.maps.TravelMode.DRIVING,
		waypoints: listarPuntosIntermedios()
	};

  mapData.directionsService.route(solicitud, function(result, status) {
    if (status == google.maps.DirectionsStatus.OK) {
      mapData.directionsDisplay.setDirections(result);
    }
  });

}

validar = function () {
	return true;
}

enviarForm = function ()  {
	// CIERRO EL MODAL DE comisiones
	closeModal("confirmar-viaje");
	if (validar()) {

		var sendData = {
			entity: 'viaje',
			action: 'new',
			origen: magic.inputOrigen.getValue()[0],
			destino: magic.inputDestino.getValue()[0],
			intermedios: magic.inputIntermedios.getValue(),
			nombre_amigable: $('#form-alta-viaje input[name=nombre_amigable]').val(),
			precio_ida: $('#form-alta-viaje input[name=precio-ida]').val(),
			precio_vuelta: $('#form-alta-viaje input[name=precio-vuelta]').val(),
			tipo_viaje: $('#form-alta-viaje select[name=tipo_viaje]').val(),
			fecha_ida: vc.fechaAMD($('#form-alta-viaje input[name=fecha-ida]').val()),
			vehiculo_ida: $('#form-alta-viaje select[name=vehiculo]').val(),
			asientos_ida: $('#form-alta-viaje select[name=asientos-ida]').val(),

		}


		if ($('#form-alta-viaje select[name=tipo_viaje]').val() == 'ida_vuelta') {
			sendData.fecha_vuelta = vc.fechaAMD($('#form-alta-viaje input[name=fecha-vuelta]').val());
			sendData.vehiculo_vuelta = $('#form-alta-viaje select[name=vehiculo]').val();
			sendData.asientos_vuelta= $('#form-alta-viaje select[name=asientos-vuelta]').val();
		}

		var onsuccess = function (jsonData) {
			// Viaje creado
			// Deberia redireccionar a detalle viaje
			// Pero como parche va a redireccionar a mis viajes
			vc.ventana_mensaje ("Viaje creado correctamente<br>Redireccionando a mis viajes");
			setTimeout(function(){window.location="/mis_viajes.html"}, 1000);
		};

		vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
	}

	return false;
}

addVehiculosSelectOption = function (vehiculo) {
	var opt = document.createElement ("OPTION");
	opt.value = vehiculo.patente;
	opt.textContent= vehiculo.marca + " " + vehiculo.modelo + " ["+ vehiculo.patente +"]";
	$('select[name=vehiculo]').append(opt);
}

cargarVehiculosSelect = function() {
	var onsuccess = function (jsonData) {
		vehiculos = jsonData.vehiculos;
		if(vehiculos.length) {
			vehiculos.forEach(function (vehiculo) {
				if(vehiculo.estado=="A") {
					addVehiculosSelectOption(vehiculo);
				}
			});
			actualizarCantidadAsientos();
		} else {
			vc.ventana_mensaje("Usted no tiene ningun vehiculo asociado.<br> <a href='/alta_vehiculo.html' class='btn btn-primary'>Nuevo vehiculo</a>");
		}
	}

	var sendData = {
		action: "ver_mis_vehiculos",
		entity: "vehiculo"
	}

	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
}

actualizarFormularioVuelta = function (){
	if ($('select[name=tipo_viaje]').val() == 'ida_vuelta') {
		$('#divVuelta').slideDown();
	} else {
		$('#divVuelta').slideUp();
	}
}
actualizarCantidadAsientos = function() {
	var vehiculo = getSelectedVehiculo();
	$('select[name=asientos-ida]').html('');
	$('select[name=asientos-vuelta]').html('');

	if(vehiculo) {
		for (i = vehiculo.cantidad_asientos-1; i>0; i--) {
			var opt1 = document.createElement("OPTION");
			var opt2 = document.createElement("OPTION");
			opt1.value=i;
			opt2.value=i;
			opt1.textContent=i;
			opt2.textContent=i;
			$('select[name=asientos-ida]').append(opt1);
			$('select[name=asientos-vuelta]').append(opt2);
		}
	}
}

getSelectedVehiculo = function() {
	var encontrado = null;
	var veh_pat = $('select[name=vehiculo]').val();
	if(veh_pat) {
		vehiculos.forEach(function (v) {
			if (v.patente == veh_pat) {
				encontrado = v;
			}
		})
	}

	return encontrado;

}

window.onload = function () {

	$('#fecha').datetimepicker({
		format: 'dd/mm/yyyy hh:ii:00',
		language: 'es',
		todayBtn: 1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 0,
		startDate: new Date(),
		pickerPosition: 'top-left'
	})

	$('#fechahasta').datetimepicker({
		format: 'dd/mm/yyyy hh:ii:00',
		language: 'es',
		todayBtn: 1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 0,
		startDate: new Date(),
		pickerPosition: 'top-left'
	})

	magic.inputOrigen = $('.localidad-origen-input').magicSuggest({
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

	magic.inputDestino = $('.localidad-destino-input').magicSuggest({
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

	magic.inputIntermedios = $('.localidad-intermedias-input').magicSuggest({
		method: 'GET',
		data: '/autocompletado',
		mode: 'remote',
		allowFreeEntries: false,
		selectFirst: true,
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

	$(magic.inputOrigen).on('selectionchange', function(e, mismo, records) {
		magic.origen = records[0];
		redibujar();
	});
	$(magic.inputDestino).on('selectionchange', function(e, mismo, records) {
		magic.destino = records[0];
		redibujar();
	});
	$(magic.inputIntermedios).on('selectionchange', function(e, mismo, records) {
		magic.intermedios = records;
		redibujar();
	});


	$('select[name=vehiculo]').on('change', actualizarCantidadAsientos);
	$('select[name=tipo_viaje]').on('change', actualizarFormularioVuelta);

	actualizarFormularioVuelta();
	cargarVehiculosSelect();

	$('.loadingScreen').fadeOut();
}

// COMISION ESTIMADA DEL VIAJE POR TRAMO Y PASAJERO
var activarConfirmarViaje = function(){
	cargarComisiones();
	return false;
}
var cargarComisiones = function(){
	var sendData = {
		entity:"viaje",
		action:"informar_comision",
		origen: magic.inputOrigen.getValue()[0],
		destino: magic.inputDestino.getValue()[0],
		intermedios: magic.inputIntermedios.getValue()
	}
	var onsuccess = function(json){
		comisiones = json.comisiones;
		var template = $("#comisiones-template").html();
		var html = Mustache.render(template,json);
		$("#comisiones").html(html);
		$("#modal-confirmar-viaje").modal("show");
	}
	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
}

var closeModal = function (name) {
	$('#modal-' + name).modal('hide');
}

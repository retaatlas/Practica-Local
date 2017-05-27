mapData = {
	marcadores: []
}

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
	if (validar()) {

		var sendData = {
			entity: 'viaje',
			action: 'new',
			origen: magic.inputOrigen.getValue()[0],
			destino: magic.inputDestino.getValue()[0],
			intermedios: magic.inputIntermedios.getValue(),
			nombre_amigable: $('#form-alta-viaje input[name=nombre_amigable]').val(),
			precio: $('#form-alta-viaje input[name=precio]').val(),
			tipo_viaje: $('#form-alta-viaje select[name=tipo_viaje]').val(),
			fecha_ida: $('#form-alta-viaje input[name=fecha-ida]').val(),
			vehiculo_ida: $('#form-alta-viaje select[name=vehiculo-ida]').val(),
			asientos_ida: $('#form-alta-viaje select[name=asientos-ida]').val(),

		}


		if ($('#form-alta-viaje select[name=tipo_viaje]').val() == 'ida_vuelta') {
			sendData.fecha_vuelta = $('#form-alta-viaje input[name=fecha-vuelta]').val();
			sendData.vehiculo_vuelta = $('#form-alta-viaje select[name=vehiculo-vuelta]').val();
			sendData.asientos_vuelta= $('#form-alta-viaje select[name=asientos-vuelta]').val();
		}

		$.ajax({
			url: '/viajes',
			method: 'POST',
			dataType: 'json',
			data: sendData,
			success: function (jsonData) {
				DEBUGresponse = jsonData;
				if(jsonData.result){
					window.alert ("Viaje creado, redireccionar");
					// Viaje creado
					// Redireccionar a detalle viaje
				} else {
					window.alert (jsonData.msg);
					// Viaje no creado
					// Mostrar error
				}
			},
			error: function (er1, err2, err3) {
				document.body.innerHTML = er1.responseText;
				window.alert (err3);
			}
		});

	} 

	return false;
}

window.onload = function () {

	$('#fecha').datetimepicker({
		format: 'yyyy-mm-dd hh:ii:ss',
		language: 'es',
		todayBtn: 1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 0
	})

	$('#fechahasta').datetimepicker({
		format: 'yyyy-mm-dd hh:ii:ss',
		language: 'es',
		todayBtn: 1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 0
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

}



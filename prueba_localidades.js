mapData = {
	marcadores: []
}

window.onload = function () {
	magicLocalidad = $('#inputLocalidades').magicSuggest({
		method: 'GET',
		data: '/autocompletado',
		mode: 'remote',
		allowFreeEntries: false,
		hideTrigger: true,
		placeholder: 'Buscar Localidades',
		noSuggestionText: 'No hay sugerencias',
		minChars: 3,
		maxSelectionRenderer: function(){},
		dataUrlParams: {
			entity: "localidad"
		}
	});

	$(magicLocalidad).on('selectionchange', function(e, mismo, records) {
		/* Quitar marcadores antiguos del mapa */
		mapData.marcadores.forEach(function(item) {
			item.setMap(null);
		});

		/* Vaciar lista de marcadores */
		mapData.marcadores = [];

		/* Agregar marcadores nuevos al mapa y a la lista */
		records.forEach(function(item) {
			
			console.log (item.id + " " + item.name + " lat:"+item.lat + " lng:" + item.lng);
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
	});

}

initMapa = function () {
	mapData.map = new google.maps.Map(document.getElementById('divMapa'), {
		center: {lat: -34.5774135, lng: -59.0909557},
		drawingControl: false,
		zoom: 8
	});
	
	mapData.directionsService = new google.maps.DirectionsService();
	mapData.directionsDisplay = new google.maps.DirectionsRenderer({suppressMarkers: true});
	mapData.directionsDisplay.setMap (mapData.map);
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
	console.log ("Se ha recibido respuesta");
	DEBUGresult=result;
    if (status == google.maps.DirectionsStatus.OK) {
      mapData.directionsDisplay.setDirections(result);
    }
  });

}

listarPuntosIntermedios = function () {
	var puntos = [];
	for (i=1; i<mapData.marcadores.length-1; i++) {
		puntos.push({location:mapData.marcadores[i].position});
	}
	return puntos;
}

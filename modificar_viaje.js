mapData = {
	marcadores: []
}

vehiculos = [];
viaje = {};
viaje.datos = {};
viaje.vehiculo = {};
viaje.localidades = [];
id_viaje = getUrlVars()["id"]; 

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

cargarViaje = function(){
	var sendData = {
		entity: "viaje",
		action: "detalle",
		privacy: "conductor",
		"id_viaje": id_viaje
	}
	var onsuccess = function(jsonData){
		$('.loadingScreen').fadeOut();

		viaje.datos = jsonData.viaje;
		viaje.vehiculo = jsonData.vehiculo;
		viaje.localidades = jsonData.localidades;
						
		cargarVehiculosSelect();
		mostrarDataViaje();
	}
	vc.peticionAjax("/viajes",sendData,"POST",onsuccess);
}

mostrarDataViaje = function(){
	if (viaje.datos.cantidad_pasajeros_calificables > 0 || viaje.datos.estado == "finalizado" || viaje.datos.estado == "cancelado"){
		vc.ventana_mensaje("Usted ya no tiene permitido modificar este viaje","Modificar","error");
		$("#boton").hide();
	}
	$('#form-modificar-viaje input[name=nombre_amigable]').val(viaje.datos.nombre_amigable);
	$('#form-modificar-viaje input[name=precio]').val(viaje.datos.precio);
	$('#form-modificar-viaje input[name=fecha]').val(viaje.datos.fecha_inicio);
	
	cargarRecorrido(viaje.datos.recorrido);
}

cargarRecorrido = function(recorrido){
	for (var i=0; i<recorrido.length; i++){
		var l = getItem(localidadPorId(recorrido[i]));
		if (i==0){
			magic.inputOrigen.addToSelection(l);
		}else if (i==recorrido.length-1){	
			magic.inputDestino.addToSelection(l);
		}else{
			magic.inputIntermedios.addToSelection(l);
		}
	}
	setTimeout(function(){redibujar()}, 2000);

	function getItem(l){
		return item = {
			id:l.id,
			name:l.nombre,
			lat:l.lat,
			lng:l.lng
		}
	}
}

validar = function () {
	return true;
}

enviarForm = function ()  {
	if (validar()) {

		var sendData = {
			entity: 'viaje',
			action: 'edit',
			id_viaje: id_viaje,
			origen: magic.inputOrigen.getValue()[0],
			destino: magic.inputDestino.getValue()[0],
			intermedios: magic.inputIntermedios.getValue(),
			nombre_amigable: $('#form-modificar-viaje input[name=nombre_amigable]').val(),
			precio: $('#form-modificar-viaje input[name=precio]').val(),
			tipo_viaje: $('#form-modificar-viaje select[name=tipo_viaje]').val(),
			fecha: $('#form-modificar-viaje input[name=fecha]').val(),
			vehiculo: $('#form-modificar-viaje select[name=vehiculo]').val(),
			asientos: $('#form-modificar-viaje select[name=asientos]').val(),

		}

		var onsuccess = function (jsonData) {
			// Viaje modificado
			// Deberia redireccionar a detalle viaje
			// Pero como parche va a redireccionar a mis viajes
			vc.ventana_mensaje ("Viaje modificado correctamente<br>Redireccionando al detalle del viaje");
			setTimeout(function(){window.location="/detalle_viaje.html?id="+id_viaje}, 3000);
		};
		console.log("mando: ",sendData);
		vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
	} 

	return false;
}

addVehiculosSelectOption = function (vehiculo) {
	var opt = document.createElement ("OPTION");
	opt.value = vehiculo.patente;
	opt.textContent= vehiculo.marca + " " + vehiculo.modelo + " ["+ vehiculo.patente +"]";
	if (vehiculo.patente == viaje.vehiculo.patente){
		opt.selected = true;
	}
	$('select[name=vehiculo]').append(opt);
}

cargarVehiculosSelect = function() {
	var onsuccess = function (jsonData) {
		vehiculos = jsonData.vehiculos;
		if(vehiculos.length) {
			vehiculos.forEach(function (vehiculo) {
				if (vehiculo.estado=="A") {
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

actualizarCantidadAsientos = function() {
	var vehiculo = getSelectedVehiculo();
	$('select[name=asientos]').html('');

	if(vehiculo) {
		for (i = vehiculo.cantidad_asientos-1; i>0; i--) {
			var opt1 = document.createElement("OPTION");
			opt1.value=i;
			opt1.textContent=i;
			if (i==viaje.datos.asientos_disponibles){
				opt1.selected = true;
			}
			$('select[name=asientos]').append(opt1);
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
		format: 'yyyy-mm-dd hh:ii:00',
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
		format: 'yyyy-mm-dd hh:ii:00',
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

	cargarViaje();
}


function getUrlVars() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
		vars[key] = value;
	});
	return vars;
}
var localidadPorId = function(id){
	var l;
	viaje.localidades.forEach(function(elem){
		if (elem.id == id){
			l = elem;
		}
	});
	return l;
}

var irDetalleViaje = function(){
	window.location = "detalle_viaje.html?id="+id_viaje;
}
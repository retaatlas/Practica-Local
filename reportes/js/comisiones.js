reporte = {};

reporte.loadData = function(){
	var sendData = {
		action: "cargar_data"
	};
	var onsuccess = function(jsonData){
		if (jsonData.redirect != undefined){
			window.location = jsonData.redirect;console.log(jsonData);
		}
		$('.loadingScreen').fadeOut();
	}
	vc.peticionAjax("/reportes",sendData,"POST",onsuccess);
}

window.onload= function(){
	reporte.loadData();
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
	
	inputConductor = $('#reportForm input[name=conductor]').magicSuggest({
		method: 'GET',
		data: '/autocompletado',
		mode: 'remote',
		allowFreeEntries: false,
		selectFirst: true,
		maxSelection: 1,
		hideTrigger: true,
		placeholder: 'Conductor',
		noSuggestionText: 'No hay sugerencias',
		minChars: 3,
		maxSelectionRenderer: function(){},
		minCharsRenderer: function() {},
		valueField: 'nombre_usuario',
		dataUrlParams: {
			entity: "cliente"
		}
	});	
	/*
	inputPatente = $('#reportForm input[name=id-comision-desde]').magicSuggest({
		method: 'GET',
		data: '/autocompletado',
		mode: 'remote',
		allowFreeEntries: false,
		selectFirst: true,
		maxSelection: 1,
		hideTrigger: true,
		placeholder: 'Patente',
		noSuggestionText: 'No hay sugerencias',
		minChars: 3,
		maxSelectionRenderer: function(){},
		minCharsRenderer: function() {},
		valueField: 'patente',
		dataUrlParams: {
			entity: "vehiculo"
		}
	});	
	*/
	
	
}


var generarReporte = function(){
	var data = {};
	data.id_viaje_desde = $("#reportForm input[name=id-viaje-desde]").val();
	data.id_viaje_hasta = $("#reportForm input[name=id-viaje-hasta]").val();	
	data.id_comision_desde = $("#reportForm input[name=id-comision-desde]").val(); //es la patente
	//data.id_comision_desde = (inputPatente.getValue()[0]==undefined)? "" : inputPatente.getValue()[0];
	data.id_comision_hasta = $("#reportForm input[name=id-comision-hasta]").val();
	var fecha_desde = $("#reportForm input[name=fechadesde]").val();
	data.fecha_desde = (fecha_desde)? vc.fechaAMD(fecha_desde) : fecha_desde;
	var fecha_hasta = $("#reportForm input[name=fechahasta]").val();
	data.fecha_hasta  = (fecha_hasta)? vc.fechaAMD(fecha_hasta) : fecha_hasta;
	data.monto_desde = $("#reportForm input[name=monto-desde]").val();
	data.monto_hasta = $("#reportForm input[name=monto-hasta]").val();
	data.km_desde = $("#reportForm input[name=kmdesde]").val();
	data.km_hasta = $("#reportForm input[name=kmhasta]").val();
	data.conductor = (inputConductor.getValue()[0]==undefined)? "" : inputConductor.getValue()[0];
	data.estado = $("#reportForm select[name=estado]").val();
	data.asientos_desde = $("#reportForm input[name=asientos-desde]").val();
	data.asientos_hasta = $("#reportForm input[name=asientos-hasta]").val();
	var sendData = {
		action: "reporte_comisiones",
		data: data
	};
	var onsuccess = function(jsonData){
		$(".loadingScreen").fadeOut();
			window.open(jsonData.link,"_blank");console.log(jsonData);
	}
	//console.log("mando", data);
	$(".loadingScreen").fadeIn();
	vc.peticionAjax("/reportes",sendData,"POST",onsuccess);
}

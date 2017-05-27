reporte = {};

reporte.loadData = function(){
	var sendData = {
		action: "cargar_data"
	};
	var onsuccess = function(jsonData){
		if (jsonData.redirect != undefined){
			window.location = jsonData.redirect;
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
}


var generarReporte = function(){
	var data = {};
	data.id_desde = $("#reportForm input[name=iddesde]").val();
	data.id_hasta = $("#reportForm input[name=idhasta]").val();
	var fecha_desde = $("#reportForm input[name=fechadesde]").val();
	data.fecha_desde = (fecha_desde)? vc.fechaAMD(fecha_desde) : fecha_desde;
	var fecha_hasta = $("#reportForm input[name=fechahasta]").val();
	data.fecha_hasta  = (fecha_hasta)? vc.fechaAMD(fecha_hasta) : fecha_hasta;
	data.precio_desde = $("#reportForm input[name=preciodesde]").val();
	data.precio_hasta = $("#reportForm input[name=preciohasta]").val();
	data.km_desde = $("#reportForm input[name=kmdesde]").val();
	data.km_hasta = $("#reportForm input[name=kmhasta]").val();
	data.pasajeros_desde = $("#reportForm input[name=pasajerosdesde]").val();
	data.pasajeros_hasta = $("#reportForm input[name=pasajeroshasta]").val();
	data.conductor = (inputConductor.getValue()[0]==undefined)? "" : inputConductor.getValue()[0];
	data.estado = $("#reportForm select[name=estado]").val();

	var sendData = {
		action: "reporte_viajes",
		data: data
	};
	var onsuccess = function(jsonData){
		$(".loadingScreen").fadeOut();
			window.open(jsonData.link,"_blank");
	}
	$(".loadingScreen").fadeIn();
	vc.peticionAjax("/reportes",sendData,"POST",onsuccess);
}

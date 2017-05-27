var vehiculos = [];
var clientes = [];

var loadData = function() {

	var sendData = {
		action: "ver_mis_vehiculos",
		entity: "vehiculo"
	}
	var onsuccess = function(jsonData){
		if(jsonData.result){
			$('.loadingScreen').fadeOut();
			vehiculos = jsonData.vehiculos;
			clientes = jsonData.clientes;
			if (vehiculos && vehiculos.length) {
				cargarVehiculos();
			}
		} else if (jsonData.redirect != undefined) {
			window.location = jsonData.redirect;// si no es conductor de este viaje, acceso denegado
		}
	}
	//simular();
	
	vc.peticionAjax("/viajes", sendData, "POST", onsuccess);
}

var initUI = function(){
	loadData();
	$('[data-toggle="tooltip"]').tooltip(); 
	
}

window.onload=initUI;

var simular = function(){
	clientes = [{
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
	vehiculos = [{
		id: "1",
		marca: "ford",
		modelo: "focus",
		color: "azul",
		anio: "2010",
		patente: "ifg 999",
		seguro: "si",
		aire: "si",
		cantidad_asientos: "4",
		conductores: ["48"],
		foto: "http://www.coches.com/fotos_historicas/ford/Focus/med_ford_focus-2014_r3.jpg.pagespeed.ce.JW0wSPihZv.jpg",
		verificado: "S"
	},{
		id: "11",
		marca: "ford",
		modelo: "focus",
		color: "gris",
		anio: "2010",
		patente: "ifg 111",
		seguro: "si",
		aire: "si",
		cantidad_asientos: "4",
		conductores: ["48","15"],
		foto: "https://www.16valvulas.com.ar/wp-content/uploads/2007/10/ford-focus-2008.jpg",
		verificado: "N"
	},{
		id: "21",
		marca: "ford",
		modelo: "focus",
		color: "gris",
		anio: "2010",
		patente: "ifg 222",
		seguro: "si",
		aire: "si",
		cantidad_asientos: "4",
		conductores: ["48","34"],
		foto: "https://www.16valvulas.com.ar/wp-content/uploads/2007/10/ford-focus-2008.jpg",
		verificado: "N"
	},{
		id: "31",
		marca: "ford",
		modelo: "focus",
		color: "gris",
		anio: "2010",
		patente: "ifg 444",
		seguro: "si",
		aire: "si",
		cantidad_asientos: "4",
		conductores: ["48","15","34"],
		foto: "https://www.16valvulas.com.ar/wp-content/uploads/2007/10/ford-focus-2008.jpg",
		verificado: "S"
	}];
	if (vehiculos.length) {
		cargarVehiculos();
	}
}

var clientePorId = function(id){
	var l;
	clientes.forEach(function(elem){
		if (elem.id == id){
			l = elem;
		}
	});
	return l.nombre_usuario;
}

var cargarAsociados = function(elem) {
	elem.conductores.forEach(function(cli){
		console.log(clientePorId(cli));
		html += $("#clientes_asociados").append('<li>'+clientePorId(cli)+'</li>');
	});
}

var cargarVehiculos = function(){
	var template = $("#vehiculo-template").html();
	var htmlVehiculosVerificados = "";
	var htmlVehiculosNoVerificados = "";
	vehiculos.forEach(function(elem){
		if(!elem.foto) {
			elem.foto="/img/vehiculo/vehiculo.png";
		}
		elem.color = panelColor(elem.verificado);
		if (elem.es_verificado = elem.verificado == "S"){
			htmlVehiculosVerificados += Mustache.render(template, elem);
		}else{
			htmlVehiculosNoVerificados += Mustache.render(template, elem);
		}
	});
	if (htmlVehiculosVerificados != "" && htmlVehiculosNoVerificados != ""){
		var html = "<div class=' col-md-6'>"
			+htmlVehiculosVerificados+
		"</div>"+
		"<div class=' col-md-6'>"
			+htmlVehiculosNoVerificados+
		"</div>"
	}else{
		var html = "<div class='row col-xs-12 col-sm-12 col-md-12 col-lg-12' >"
			+htmlVehiculosVerificados+htmlVehiculosNoVerificados+
			"</div>";
	}
	$("#panel-vehiculos").html(html);

}

var estadoString = function (caracter) {
	switch (caracter) {
		case '1': return "Verificado";
		case '2': return "No verificado";
		case null: return "No especificado";
		default: return "Desconocido";
	}
}

var colorPanel = function(caracter){
	switch (caracter) {
		case '1': return "info";
		case '2': return "success";
		case '3': return "warning";
		case null: return "default";
		default: return "default";
	}
}


var panelColor = function (caracter) {
	switch(caracter) {
		case 'S': return "info";
		case 'N': return "danger";
	}
}


var modalMessage = function (modalName,textMsg,titleMsg) {
	$('#'+modalName+'-message').text(textMsg);
	if (titleMsg){
		$('#modal-'+modalName+" .dialog-title").text(titleMsg);
	}
	$('#modal-'+modalName).modal('show');
}
var closeModal = function (name) {
	$('#modal-' + name).modal('hide');
}

function getUrlVars() {
	var vars = {};
	var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
		vars[key] = value;
	});
	return vars;
}
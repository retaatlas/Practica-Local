window.onload=function() {
	consultar_saldo();
}

var consultar_saldo = function() {
	var sendData = {
		entity: "saldo",
		action: "consultar"
	};
	
	var onsuccess = function(recibido) {
		actualizarSaldo(recibido.saldo);
		$(".loadingScreen").fadeOut();
	}
	
	vc.peticionAjax("/comisiones", sendData, "GET", onsuccess);
}

var actualizarSaldo = function(saldo) {
	$("#saldo_actual").text("$ "+saldo.toFixed(2));
}

var enviarCarga = function () {
	var monto = $("input[name=monto_cargar]").val();
	var sendData = {
		entity: "saldo",
		action: "cargar",
		monto: monto
	};
	var onsuccess = function(recibido) {
		if(recibido.msg) {
			vc.ventana_mensaje(recibido.msg, "Carga exitosa", "success");
		}
		consultar_saldo();
	}

	vc.peticionAjax("/comisiones", sendData, "POST", onsuccess);
}
window.onload=function() {
	consultar_saldo();
  cargarMovSaldo();
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

// movimientos de saldo
var cargarMovSaldo = function(){
  /*
  movsaldo = [{
    fecha:"22/04/1025",
    tipo:"a",
    monto:"500"
  }];
  */
  var sendData = {
    entity:"saldo",
    action:"movimientos"
  }
  var onsuccess = function(json){
    showMovSaldo(json.mov_saldo);
  }
  vc.peticionAjax("/comisiones", sendData, "POST", onsuccess);

}

var showMovSaldo = function(movSaldo){
  var tbody = $('#table-mov-saldo tbody')[0];
  var tr;


  tbody.innerHTML = '';

  if (!movSaldo || !movSaldo.length){
    var td;

    tr = document.createElement ('TR');
    td = document.createElement ('TD');
    td.setAttribute ('colspan', 3);
    td.textContent = "No hay resultados para la busqueda";
    td.className = "warning";

    tbody.appendChild (tr);
    tr.appendChild (td);
  }
  movSaldo.forEach(function (elem) {

      tr = document.createElement ('TR');
      tr.appendChild (getTd (elem.fecha.replace(/-/g, '\/').split("/").reverse().join("/")));
      tr.appendChild (getTd (elem.tipo));
      tr.appendChild (getTd (elem.monto.toFixed(2)));

      var thistr = tr;
      tr.onclick = function () {
        clearSelectedRow (tbody);
        $(thistr).addClass('info');
      }
      tbody.appendChild(tr);
  });

}

var getTd = function (text){
	var td = document.createElement('TD');
	td.appendChild(document.createTextNode(text));
	return td;
}

var clearSelectedRow = function (tbody) {
	debugTBODY = tbody;
	var i = 0;

	while (i < tbody.childNodes.length) {
		$(tbody.childNodes[i]).removeClass('info');
		i++;
	}
}

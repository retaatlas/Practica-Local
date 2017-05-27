window.onload=function() {
	consultar_puntos();
  cargarMovPuntos();
}

var consultar_puntos = function() {
	var sendData = {
		entity: "puntos",
		action: "consultar"
	};

	var onsuccess = function(recibido) {
		actualizarPuntos(recibido.puntos);
		$(".loadingScreen").fadeOut();
	}

	vc.peticionAjax("/puntos", sendData, "GET", onsuccess);
}

var actualizarPuntos = function(puntos) {
	$("#puntos_actual").text(puntos);
}

// movimientos de Puntos
var cargarMovPuntos = function(){
  var sendData = {
    entity:"puntos",
    action:"movimientos"
  }
  var onsuccess = function(json){
    showMovPuntos(json.mov_puntos);
  }
  vc.peticionAjax("/puntos", sendData, "POST", onsuccess);

}

var showMovPuntos = function(movPuntos){
  var tbody = $('#table-mov-puntos tbody')[0];
  var tr;


  tbody.innerHTML = '';

  if (!movPuntos || !movPuntos.length){
    var td;

    tr = document.createElement ('TR');
    td = document.createElement ('TD');
    td.setAttribute ('colspan', 3);
    td.textContent = "No hay resultados para la busqueda";
    td.className = "warning";

    tbody.appendChild (tr);
    tr.appendChild (td);
  }
  movPuntos.forEach(function (elem) {

      tr = document.createElement ('TR');
      tr.appendChild (getTd (vc.toFechaLocal(elem.fecha).split(" 00:00:00")[0]));
      tr.appendChild (getTd (elem.descripcion));
      tr.appendChild (getTd (elem.monto));

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

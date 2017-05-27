var comisiones = [];
var selected = null;

cargarDatos = function () {
	$.ajax({
		url: "/comisiones",
		method: "GET",
		dataType: "json",
		data: {
			entity: "comision",
			action: "vigentes"
		},
		success: function(recv) {
			if (recv.result) {
				comisiones = recv.comisiones;
				actualizarTabla();
				$(".loadingScreen").fadeOut();
			}
		}
	})
}

newButtonPressed = function () {
	$("input[name=id_comision]").val("new");
	$("input[name=limite_inferior]").val("0");
	$("input[name=limite_superior]").val("10");
	$("input[name=precio]").val("5");
	$("#modalComision").modal("show");
}

editButtonPressed = function () {
	if (selected) {
		$("input[name=id_comision]").val(selected.id_comision);
		$("input[name=limite_inferior]").val(selected.limite_inferior);
		$("input[name=limite_superior]").val(selected.limite_superior);
		$("input[name=precio]").val(selected.precio);
		$("#modalComision").modal("show");
	}
}

sendButtonPressed = function () {
	var id_comision = $("input[name=id_comision]").val();
	if (id_comision == "new") {
		enviarNuevaComision();
	} else {
		enviarEditarComision();
	}
}

enviarNuevaComision = function () {
	$.ajax({
		url: "/comisiones",
		method: "POST",
		dataType: "json",
		data: {
			entity: "comision",
			action: "new",
			limite_inferior: $("input[name=limite_inferior]").val(),
			limite_superior: $("input[name=limite_superior]").val(),
			precio: $("input[name=precio]").val(),
		},
		success: function() {
			cargarDatos();
			$("#modalComision").modal("hide");
		}
	});
}

enviarEditarComision = function () {
	$.ajax({
		url: "/comisiones",
		method: "POST",
		dataType: "json",
		data: {
			entity: "comision",
			action: "edit",
			id_comision: $("input[name=id_comision]").val(), 
			limite_inferior: $("input[name=limite_inferior]").val(),
			limite_superior: $("input[name=limite_superior]").val(),
			precio: $("input[name=precio]").val(),
		},
		success: function() {
			cargarDatos();
			$("#modalComision").modal("hide");
		}
	})
}

deleteButtonPressed = function () {
	$.ajax({
		url: "/comisiones",
		method: "POST",
		dataType: "json",
		data: {
			entity: "comision",
			action: "finalizar",
			id_comision: getSelected().id_comision
		},
		success: cargarDatos
	})
}

getSelected = function() {
	return selected;
}

setSelected = function(comision) {
	selected = comision;
}

actualizarTabla = function() {
	$("#tableComisiones tbody").html("");
	comisiones.forEach(function(cm) {
		var tr = document.createElement("TR");
		var fechaLocal = cm.fecha_inicio.replace(/-/g, '\/').split(" ")[0].split("/").reverse().join("/");
		//tr.appendChild(cTD(cm.fecha_inicio));
		tr.appendChild(cTD(fechaLocal));
		tr.appendChild(cTD(cm.limite_inferior));
		tr.appendChild(cTD(cm.limite_superior));
		tr.appendChild(cTD("$"+cm.precio));
		$("#tableComisiones tbody")[0].appendChild(tr);
		$(tr).on("click", function() {
			clearSelected ();
			setSelected (cm);
			$(tr).addClass("info");
		});
	});
}

cTD = function(texto) {
	var td = document.createElement("TD");
	td.textContent = texto;
	return td;
}

clearSelected = function() {
	selected = null;
	$("#tableComisiones tbody tr").removeClass("info");
}

window.onload = cargarDatos;
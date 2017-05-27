ui = {};
ui.currentTab = 'personas';
ui.selectedId = null;

data={};

data.personas=[];
data.usuarios=[];
data.roles=[];



data.loadData = function() {
	$.ajax({
		url: '/users',
		dataType: 'json',
		success: function (jsonData) {
			DEBUGresponse = jsonData;
			data.personas = jsonData.personas;
			data.usuarios = jsonData.usuarios;
			data.roles = jsonData.roles;
			data.permisos = jsonData.permisos;
			ui.updatePersonasTable();
			ui.updateUsuariosTable();
			ui.updateRolesTable();
			ui.updatePermisosTable();
			ui.updatePersonasSelect();
		},
		error: function (er1, err2, err3) {
			document.body.innerHTML = er1.responseText;
			window.alert (err3);
		}
	});
}

Array.prototype.getById = function (id) {
	var pos = 0;
	var found = null;
	var retval = null;
	while (found == null && pos < this.length) {
		if (this[pos].id == id) {
			retval = this[pos];
		}
		pos++;
	}
	return retval;
}

ui.sendPersonaForm = function () {
	var sendData = {};
	sendData.entity = 'persona';
	sendData.id_persona = $('#formPersona input[name=id]').val();
	sendData.action = (sendData.id_persona == -1)? 'new': 'edit';
	sendData.apellidos = $('#formPersona input[name=apellidos]').val() || null;
	sendData.nombres= $('#formPersona input[name=nombres]').val() || null;
	sendData.tipo_doc= $('#formPersona select[name=tipo_doc]').val() || null;
	sendData.nro_doc= $('#formPersona input[name=nro_doc]').val() || null;
	sendData.fecha_nacimiento= $('#formPersona input[name=fecha_nacimiento]').val() || null;
	sendData.sexo= $('#formPersona select[name=sexo]').val() || null;
	sendData.domicilio= $('#formPersona input[name=domicilio]').val() || null;
	sendData.foto= $('#formPersona input[name=foto]').val() || null;
	sendData.telefono= $('#formPersona input[name=telefono]').val() || null;
	sendData.descripcion= $('#formPersona textarea[name=descripcion]').val() || null;
	sendData.estado= $('#formPersona select[name=estado]').val() || null;
	sendData.foto_registro= $('#formPersona input[name=foto_registro]').val() || null;

	aux.sendForm(sendData, data.loadData)

	$('#formPersona').hide();
}

ui.sendUsuarioForm = function() {
	var sendData = {};
	sendData.entity = 'usuario';
	sendData.id_usuario = $('#formUsuario input[name=id]').val() || null;
	sendData.action = (sendData.id_usuario == -1)? 'new': 'edit';
	sendData.id_persona = $('#formUsuario select[name=id_persona]').val() || null;
	sendData.nombre_usuario= $('#formUsuario input[name=nombre_usuario]').val() || null;
	sendData.password = $('#formUsuario input[name=password]').val() || null;
	sendData.email = $('#formUsuario input[name=email]').val() || null;
	sendData.descripcion = $('#formUsuario textarea[name=descripcion]').val() || null;
	sendData.estado = $('#formUsuario select[name=estado]').val() || null;
	aux.sendForm(sendData, data.loadData)

	$('#formUsuario').hide();
}

ui.sendRolForm = function() {
	var sendData = {};
	sendData.entity = 'rol';
	sendData.id_rol = $('#formRol input[name=id]').val() || null;
	sendData.action = (sendData.id_rol == -1)? 'new': 'edit';
	sendData.nombre_rol = $('#formRol input[name=nombre_rol]').val() || null;
	sendData.nombre_amigable = $('#formRol input[name=nombre_amigable]').val() || null;
	sendData.descripcion= $('#formRol textarea[name=descripcion]').val() || null;
	sendData.estado = $('#formRol select[name=estado]').val() || null;
	aux.sendForm(sendData, data.loadData);

	$('#formRol').hide();
}

ui.sendPermisoForm = function() {
	var sendData = {};
	sendData.entity = 'permiso';
	sendData.id_permiso = $('#formPermiso input[name=id]').val() || null;
	sendData.action = (sendData.id_permiso == -1)? 'new': 'edit';
	sendData.nombre_permiso = $('#formPermiso input[name=nombre_permiso]').val() || null;
	sendData.funcionalidad= $('#formPermiso input[name=funcionalidad]').val() || null;
	sendData.descripcion= $('#formPermiso textarea[name=descripcion]').val() || null;
	sendData.estado = $('#formPermiso select[name=estado]').val() || null;
	aux.sendForm(sendData, data.loadData);

	$('#formPermiso').hide();
}
ui.requestPersonaDeletion = function() {
	var sendData = {};
	sendData.entity = 'persona';
	sendData.id = ui.selectedId;
	sendData.action = 'delete';
	aux.sendForm(sendData, data.loadData);
}

ui.requestUsuarioDeletion = function() {
	var sendData = {};
	sendData.entity = 'usuario';
	sendData.id = ui.selectedId;
	sendData.action = 'delete';
	aux.sendForm(sendData, data.loadData);
}

ui.requestRolDeletion = function() {
	var sendData = {};
	sendData.entity = 'rol';
	sendData.id = ui.selectedId;
	sendData.action = 'delete';
	aux.sendForm(sendData, data.loadData);
}

ui.requestPermisoDeletion = function() {
	var sendData = {};
	sendData.entity = 'permiso';
	sendData.id = ui.selectedId;
	sendData.action = 'delete';
	aux.sendForm(sendData, data.loadData);
}

initUI = function() {
	data.loadData();
};



ui.activatePersonasTab = function () {
	ui.currentTab = 'personas';
	ui.hideTables();
	$('#personasTable').show();
};


ui.activateUsuariosTab = function () {
	ui.currentTab = 'usuarios';
	ui.hideTables();
	$('#usuariosTable').show();
};

ui.activateRolesTab = function () {
	ui.currentTab = 'roles';
	ui.hideTables();
	$('#rolesTable').show();
};

ui.activatePermisosTab = function () {
	ui.currentTab = 'permisos';
	ui.hideTables();
	$('#permisosTable').show();
};

ui.newButtonPressed = function () {
	switch (ui.currentTab) {
		case 'personas':
			ui.showNewPersonaForm();
			break;
		case 'usuarios':
			ui.showNewUsuarioForm();
			break;
		case 'roles':
			ui.showNewRolForm();
			break;
		case 'permisos':
			ui.showNewPermisoForm();
			break;
	}
};

ui.editButtonPressed = function () {
	switch (ui.currentTab) {
		case 'personas':
			ui.showEditPersonaForm();
			break;
		case 'usuarios':
			ui.showEditUsuarioForm();
			break;
		case 'roles':
			ui.showEditRolForm();
			break;
		case 'permisos':
			ui.showEditPermisoForm();
			break;
	}
};

ui.deleteButtonPressed = function () {
	switch (ui.currentTab) {
		case 'personas':
			ui.requestPersonaDeletion();
			break;
		case 'usuarios':
			ui.requestUsuarioDeletion();
			break;
		case 'roles':
			ui.requestRolDeletion();
			break;
		case 'permisos':
			ui.requestPermisoDeletion();
			break;
	}
};

ui.updatePersonasSelect = function() {
	var select = $('#formUsuario select[name=id_persona]')[0];
	var newOpt;

	select.textContent = '';
	data.personas.forEach (function (persona) {
		newOpt = document.createElement ('OPTION');
		newOpt.value = persona.id
		newOpt.textContent = persona.nombres+' '+persona.apellidos;
		select.appendChild (newOpt);
	});
}

ui.showNewPersonaForm = function () {
	$('#formPersona input[name=id]').hide()
	$('#formPersona label[for=id]').hide()
	$('#formPersonaTitle').html('Nueva Persona');
	$('#formPersona').show();
	$('#formPersona input[name=id]').val('-1');
	$('#formPersona input[name=apellidos]').val('');
	$('#formPersona input[name=nombres]').val('');
	$('#formPersona select[name=tipo_doc]').val(1);
	$('#formPersona input[name=nro_doc]').val('');
	$('#formPersona input[name=fecha_nacimiento]').val('1990-01-01');
	$('#formPersona select[name=sexo]').val('M');
	$('#formPersona input[name=foto]').val('');
	$('#formPersona input[name=domicilio]').val('');
	$('#formPersona input[name=telefono]').val('');
	$('#formPersona textarea[name=descripcion]').val('');
	$('#formPersona select[name=estado]').val('A');
	$('#formPersona input[name=foto_registro]').val('');
	$('#formPersonaUsuario select[name=usuarios]').html('');
};

ui.showNewUsuarioForm = function () {
	$('#formUsuario').show();
	$('#formUsuario input[name=id]').hide();
	$('#formUsuario label[for=id]').hide();
	$('#formUsuario input[name=id]').val('-1');
	$('#formUsuario input[name=id_persona]').val('');
	$('#formUsuario input[name=nombre_usuario]').val('');
	$('#formUsuario input[name=password]').val('');
	$('#formUsuario input[name=email]').val('');
	$('#formUsuario textarea[name=descripcion]').val('');
	$('#formUsuario select[name=estado]').val('A');
	$('#formUsuarioRol').hide();
	$('#formUsuarioRol select[name=roles_asignados]').html('');
	$('#formUsuarioRol select[name=roles_no_asignados]').html('');

	var newOption;
	var noAsignados =  $('#formUsuarioRol select[name=roles_no_asignados]')[0];

	data.roles.forEach(function (rol) {
		newOption = document.createElement ('OPTION');
		newOption.value = rol.id;
		newOption.textContent = rol.nombre_amigable;
		noAsignados.appendChild (newOption);
	});
};

ui.showNewRolForm = function () {
	$('#formRol').show();
	$('#formRol input[name=id]').hide();
	$('#formRol label[for=id]').hide();
	$('#formRol input[name=id]').val(-1);
	$('#formRol input[name=nombre_rol]').val('');
	$('#formRol input[name=nombre_amigable]').val('');
	$('#formRol textarea[name=descripcion]').val('');
	$('#formRol input[name=estado]').val('A');
	$('#formRolPermiso').hide();
}

ui.showNewPermisoForm = function () {
	$('#formPermiso').show();
	$('#formPermiso input[name=id]').hide();
	$('#formPermiso label[for=id]').hide();
	$('#formPermiso input[name=id]').val(-1);
	$('#formPermiso input[name=nombre_permiso]').val('');
	$('#formPermiso input[name=funcionalidad]').val('');
	$('#formPermiso textarea[name=descripcion]').val('');
	$('#formPermiso input[name=estado]').val('A');
};;

ui.showEditPersonaForm = function () {
	var selected = ui.getSelectedElement();
	if (selected == null) return;
	$('#formPersonaTitle').html('Modificar Persona');
	$('#formPersona').show();
	$('#formPersona input[name=id]').show();
	$('#formPersona label[for=id]').show();
	$('#formPersona input[name=id]').val(selected.id);
	$('#formPersona input[name=id_persona]').val(selected.id_persona);
	$('#formPersona input[name=apellidos]').val(selected.apellidos);
	$('#formPersona input[name=nombres]').val(selected.nombres);
	$('#formPersona select[name=tipo_doc]').val(selected.tipo_doc);
	$('#formPersona input[name=nro_doc]').val(selected.nro_doc);
	$('#formPersona input[name=fecha_nacimiento]').val(selected.fecha_nacimiento);
	$('#formPersona select[name=sexo]').val(selected.sexo);
	$('#formPersona input[name=foto]').val(selected.foto);
	$('#formPersona input[name=domicilio]').val(selected.domicilio);
	$('#formPersona input[name=telefono]').val(selected.telefono);
	$('#formPersona textarea[name=descripcion]').val(selected.descripcion);
	$('#formPersona select[name=estado]').val(selected.estado);
	$('#formPersona input[name=foto_registro]').val(selected.foto_registro);
	$('#formPersonaUsuario select[name=usuarios]').html('');

	var newOption;
	var usuario;
	var usuariosSelect = $('#formPersonaUsuario select[name=usuarios]')[0];

	data.personas.getById (selected.id).usuarios.forEach(function (idUsuario) {
		usuario = data.usuarios.getById(idUsuario);
		newOption = document.createElement ('OPTION');
		newOption.value = idUsuario;
		newOption.textContent = usuario.nombre_usuario;
		usuariosSelect.appendChild (newOption);
	});
};

ui.showEditUsuarioForm = function() {
	var selected = ui.getSelectedElement();
	if (selected == null) return;
	$('#formUsuario').show();
	$('#formUsuario input[name=id]').hide();
	$('#formUsuario label[for=id]').hide();
	$('#formUsuario input[name=id]').val(selected.id);
	$('#formUsuario select[name=id_persona]').val(selected.id_persona);
	$('#formUsuario input[name=nombre_usuario]').val(selected.nombre_usuario);
	$('#formUsuario input[name=password]').val(selected.password);
	$('#formUsuario input[name=email]').val(selected.email);
	$('#formUsuario textarea[name=descripcion]').val(selected.descripcion);
	$('#formUsuario select[name=estado]').val((selected.estado));
	$('#formUsuarioRol').show();
	$('#formUsuarioRol select[name=roles_asignados]').html('');
	$('#formUsuarioRol select[name=roles_no_asignados]').html('');

	var newOption;
	var usuario;
	var asignados =  $('#formUsuarioRol select[name=roles_asignados]')[0];
	var noAsignados =  $('#formUsuarioRol select[name=roles_no_asignados]')[0];

	usuario = data.usuarios.getById(selected.id);
	data.roles.forEach(function (rol) {
		newOption = document.createElement ('OPTION');
		newOption.value = rol.id;
		newOption.textContent = rol.nombre_amigable;
		if (usuario.roles.includes(rol.id)) {
			asignados.appendChild (newOption);
		} else {
			noAsignados.appendChild (newOption);
		}
	});

}

ui.showEditRolForm = function() {
	var selected = ui.getSelectedElement();
	if (selected == null) return;
	$('#formRol').show();
	$('#formRol input[name=id]').show();
	$('#formRol label[for=id]').show();
	$('#formRol input[name=id]').val(selected.id);
	$('#formRol input[name=nombre_rol]').val(selected.nombre_rol);
	$('#formRol input[name=nombre_amigable]').val(selected.nombre_amigable);
	$('#formRol textarea[name=descripcion]').val(selected.descripcion);
	$('#formRol select[name=estado]').val((selected.estado));
	$('#formRolPermiso').show();
	$('#formRolPermiso select[name=permisos_asignados]').html('');
	$('#formRolPermiso select[name=permisos_no_asignados]').html('');

	var newOption;
	var rol;
	var asignados =  $('#formRolPermiso select[name=permisos_asignados]')[0];
	var noAsignados =  $('#formRolPermiso select[name=permisos_no_asignados]')[0];

	rol = data.roles.getById(selected.id);
	data.permisos.forEach(function (permiso) {
		newOption = document.createElement ('OPTION');
		newOption.value = permiso.id;
		newOption.textContent = permiso.nombre_permiso;
		if (rol.permisos.includes(permiso.id)) {
			asignados.appendChild (newOption);
		} else {
			noAsignados.appendChild (newOption);
		}
	});
}

ui.showEditPermisoForm = function() {
	var selected = ui.getSelectedElement();
	if (selected == null) return;
	$('#formPermiso').show();
	$('#formPermiso input[name=id]').show();
	$('#formPermiso label[for=id]').show();
	$('#formPermiso input[name=id]').val(selected.id);
	$('#formPermiso input[name=nombre_permiso]').val(selected.nombre_permiso);
	$('#formPermiso input[name=funcionalidad]').val(selected.funcionalidad);
	$('#formPermiso textarea[name=descripcion]').val(selected.descripcion);
	$('#formPermiso select[name=estado]').val(selected.estado);
}

ui.hideTables = function () {
	$('#personasTable').hide();
	$('#usuariosTable').hide();
	$('#rolesTable').hide();
	$('#permisosTable').hide();
}

ui.closeForms = function () {
	$('#formPersona').hide();
	$('#formUsuario').hide();
	$('#formRol').hide();
	$('#formPermiso').hide();
}


ui.updatePersonasTable = function () {
	var tbody = $('#personasTable tbody')[0];
	var tr;

	tbody.innerHTML = '';
	data.personas.forEach(function (elem) {
		tr = document.createElement ('TR');
		tr.appendChild (aux.td (elem.id));
		tr.appendChild (aux.td (elem.apellidos));
		tr.appendChild (aux.td (elem.nombres));
		tr.appendChild (aux.td (aux.tipoDoc(elem.tipo_doc)));
		tr.appendChild (aux.td (elem.nro_doc));
		tr.appendChild (aux.td (elem.fecha_nacimiento));
		tr.appendChild (aux.td (aux.sexoString(elem.sexo)));
		tr.appendChild (aux.td (elem.domicilio));
		tr.appendChild (aux.td (elem.telefono));
		tr.appendChild (aux.td (aux.estadoString(elem.estado)));
		var thistr = tr;
		tr.onclick = function () {
			ui.selectedId = elem.id;
			aux.clearSelectedRow (tbody);
			thistr.className='selectedRow';
		}
		tbody.appendChild(tr);
	});
	
}

ui.updateUsuariosTable = function () {
	var tbody = $('#usuariosTable tbody')[0];
	tbody.innerHTML = '';
	data.usuarios.forEach(function (elem) {
		tr = document.createElement ('TR');
		tr.appendChild (aux.td (elem.id));
		tr.appendChild (aux.td (elem.nombre_usuario));
		tr.appendChild (aux.td (elem.email));
		tr.appendChild (aux.td (aux.estadoString(elem.estado)));
		var thistr = tr;
		tr.onclick = function () {
			ui.selectedId = elem.id;
			aux.clearSelectedRow (tbody);
			thistr.className='selectedRow';
		}
		tbody.appendChild(tr);
	});
}

ui.updateRolesTable = function () {
	var tbody = $('#rolesTable tbody')[0];
	tbody.innerHTML = '';
	data.roles.forEach(function (elem) {
		tr = document.createElement ('TR');
		tr.appendChild (aux.td (elem.id));
		tr.appendChild (aux.td (elem.nombre_rol));
		tr.appendChild (aux.td (elem.nombre_amigable));
		tr.appendChild (aux.td (aux.estadoString(elem.estado)));
		var thistr = tr;
		tr.onclick = function () {
			ui.selectedId = elem.id;
			aux.clearSelectedRow (tbody);
			thistr.className='selectedRow';
		}
		tbody.appendChild(tr);
	});
}

ui.updatePermisosTable = function () {
	var tbody = $('#permisosTable tbody')[0];
	tbody.innerHTML = '';
	data.permisos.forEach(function (elem) {
		tr = document.createElement ('TR');
		tr.appendChild (aux.td (elem.id));
		tr.appendChild (aux.td (elem.nombre_permiso));
		tr.appendChild (aux.td (aux.estadoString(elem.estado)));
		var thistr = tr;
		tr.onclick = function () {
			ui.selectedId = elem.id;
			aux.clearSelectedRow (tbody);
			thistr.className='selectedRow';
		}
		tbody.appendChild(tr);
	});
}

ui.getSelectedElement = function() {
	var elem, list, index, found;
	index = 0;
	found = false;
	elem = null;
	switch (ui.currentTab) {
		case 'personas':
			list = data.personas;
			break;
		case 'usuarios':
			list = data.usuarios;
			break;
		case 'roles':
			list = data.roles;
			break;
		case 'permisos':
			list = data.permisos;
			break;
	}
	while (!found & index < list.length) {
		elem= list[index];
		if (list[index].id == ui.selectedId) {
			found=true;
			elem=list[index];
		}
		index++;
	}
	return elem;
}

ui.assignRolUsuario = function () {
	var sendData = {
		entity: 'usuario',
		action: 'assignRol',
		id_usuario: $('#formUsuario input[name=id]').val(),
		id_rol: $('#formUsuarioRol select[name=roles_no_asignados]').val()
	};

	var onsuccess = function (jsonData) {
		data.usuarios.getById(jsonData.id_usuario).roles.push(jsonData.id_rol);

		if ($('#formUsuario input[name=id]').val() == jsonData.id_usuario) {
			$('#formUsuarioRol select[name=roles_no_asignados] option[value='+jsonData.id_rol+']')
				.detach()
				.appendTo('#formUsuarioRol select[name=roles_asignados]');
		}
	};
	aux.sendForm (sendData, onsuccess);
}

ui.removeRolUsuario = function () {
	var sendData = {
		entity: 'usuario',
		action: 'removeRol',
		id_usuario: $('#formUsuario input[name=id]').val(),
		id_rol: $('#formUsuarioRol select[name=roles_asignados]').val()
	};

	var onsuccess = function (jsonData) {
		data.usuarios.getById(jsonData.id_usuario).roles.removeElement(jsonData.id_rol);

		if ($('#formUsuario input[name=id]').val() == jsonData.id_usuario) {
			$('#formUsuarioRol select[name=roles_asignados] option[value='+jsonData.id_rol+']')
				.detach()
				.appendTo('#formUsuarioRol select[name=roles_no_asignados]');
		}
	};
	aux.sendForm (sendData, onsuccess);
}

ui.assignPermisoRol = function () {
	var sendData = {
		entity: 'rol',
		action: 'assignPermiso',
		id_rol: $('#formRol input[name=id]').val(),
		id_permiso: $('#formRolPermiso select[name=permisos_no_asignados]').val()
	};

	var onsuccess = function (jsonData) {
		data.roles.getById(jsonData.id_rol).permisos.push(jsonData.id_permiso);

		if ($('#formRol input[name=id]').val() == jsonData.id_rol) {
			$('#formRolPermiso select[name=permisos_no_asignados] option[value='+jsonData.id_permiso+']')
				.detach()
				.appendTo('#formRolPermiso select[name=permisos_asignados]');
		}
	};
	aux.sendForm (sendData, onsuccess);
}
ui.revokePermisoRol = function () {
	var sendData = {
		entity: 'rol',
		action: 'revokePermiso',
		id_rol: $('#formRol input[name=id]').val(),
		id_permiso: $('#formRolPermiso select[name=permisos_asignados]').val()
	};

	var onsuccess = function (jsonData) {
		data.roles.getById(jsonData.id_rol).permisos.removeElement(jsonData.id_permiso);

		if ($('#formRol input[name=id]').val() == jsonData.id_rol) {
			$('#formRolPermiso select[name=permisos_asignados] option[value='+jsonData.id_permiso+']')
				.detach()
				.appendTo('#formRolPermiso select[name=permisos_no_asignados]');
		}
	};
	aux.sendForm (sendData, onsuccess);
}

/* Funciones auxiliares */
aux = {};
aux.td = function (text){
	var td = document.createElement('TD');
	td.appendChild(document.createTextNode(text));
	return td;
}

aux.tipoDoc = function(number) {
	switch (number) {
		case 1:
			return 'DNI';
		case 2:
			return 'LE';
		case 3:
			return 'LC';
		default:
			return number;
	}
}

aux.clearSelectedRow = function (tbody) {
	debugTBODY = tbody;
	var i = 0;

	while (i < tbody.childNodes.length) {
		tbody.childNodes[i].className = '';
		i++;
	}
}

aux.sendForm = function (sendData, onsuccess) {
	$.ajax({
		url: '/users',
		method: 'POST',
		data: sendData,
		dataType: 'json',
		success: function (jsonData) {
			DEBUGresponse = jsonData;
			if (jsonData.result) {
				onsuccess (jsonData);
			} else {
				window.alert ("Ocurrio un error");
			}
		},
		error: function (er1, err2, err3) {
			document.body.innerHTML = er1.responseText;
			window.alert (err3);
		}
	});
}

aux.estadoString = function (caracter) {
	switch (caracter) {
		case 'A': return "Activo";
		case 'B': return "Inactivo";
		case 'S': return "Suspendido";
		case '': return "No especificado";
		case null: return "No especificado";
		default: return "Desconocido";
	}
}

aux.sexoString = function (caracter) {
	switch (caracter) {
		case 'F': return "Femenino";
		case 'M': return "Masculino";
		case 'O': return "Otro";
		case '': return "No especificado";
		case null: return "No especificado";
		default: return "Desconocido";
	}
}

Array.prototype.removeElement = function(item) {
	var pos = this.indexOf (item);
	if (pos != -1) {
		this.splice(pos, 1);
	}
	return (pos != -1)? item: null;
}

/* Para navegadores viejos */
if (Array.prototype.includes == undefined) {
	Array.prototype.includes = function (item) {
		return this.indexOf(item) != -1;
	}
}

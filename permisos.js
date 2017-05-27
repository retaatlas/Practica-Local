permisosData={};
permisosData.permisosp=[];
permisosData.usuariop={};
permisosData.rolesp=[];

permisosData.iniciarScriptPermisos = function(){
	permisosData.getPermisosUsuario();
}

permisosData.getPermisosUsuario = function() {
	var sendData = {
		action: 'get_permisos'
	};
	var callback = function (jsonData){
		if (jsonData.result){
			permisosData.permisosp = jsonData.permisos;
			permisosData.usuariop = jsonData.usuario;
			permisosData.rolesp = jsonData.roles;
			permisosData.mostrarFunciones();
		}else if (jsonData.redirect != undefined){
			window.location = jsonData.redirect;
		}
	}
	permisosData.send(sendData,callback);
}

permisosData.send = function(sendData,callback){
	$.ajax({
		url: '/users',
		method: 'POST',
		data: sendData,
		dataType: 'json',
		success: function (jsonData) {
			DEBUGresponse = jsonData;
			callback(jsonData);
		},
		error: function (er1, err2, err3) {
			document.body.innerHTML = er1.responseText;
			window.alert (err3);
		}
	});
}

permisosData.mostrarFunciones = function(){
	//console.log("Permisos que me traje: ",permisosData.permisosp);
	//console.log("roles que me traje: ",permisosData.rolesp);
	//console.log("usuario que me traje: ",permisosData.usuariop);
	permisosData.cargarNavbarSidebarPermisos();
}
permisosData.cargarNavbarSidebarPermisos = function(){
	$.getScript( "/js/mustache.js", function() {
		$.ajax({
			url: '/navegacion.html',
			type: 'GET',
			success: function(data) {
				var dom = $(data);
				var template;
				dom.filter('script').each(function(){
					if (this.id == "navbar-navegacion-template"){
						templateNavbar = this.innerHTML;
					}
					if (this.id == "botonera-sidebar-template"){
						templateSidebar = this.innerHTML;
					}
				});
				var permisosFlags = {};
				if (permisosData.permisosp){
					var permiso=0; var es_admin = false;
					for (permiso in permisosData.permisosp){
						var nombrePermiso=permisosData.permisosp[permiso]["nombre_permiso"];
						var estadoPermiso=permisosData.permisosp[permiso]["estado"];
						permisosFlags[nombrePermiso] = nombrePermiso && estadoPermiso=="A";
						if(nombrePermiso=='administrar_usuarios'){es_admin=true;}
					}
				}
				var htmlNavbar = Mustache.render(templateNavbar,permisosFlags);
				var htmlSidebar = Mustache.render(templateSidebar,permisosFlags);
				
				if(es_admin){
					document.getElementById("common-sidebar").style.backgroundColor = "white";
					document.getElementById("common-navbar").style.backgroundColor = "#c6dcee";
					//document.getElementById("navbar").style.backgroundColor = "blue";
					
				}
				
				
				$("#navbar-dinamico").html(htmlNavbar);
				$("#dropdown-usuario").html(permisosData.usuariop.nombre_usuario+" ");
				$("#botonera-sidebar").html(htmlSidebar);
				
				comprobarNotificaciones();
				
			}
		});
	});
}

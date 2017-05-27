initUI = function() {
	$('#anio').datetimepicker({
        viewMode: 'years',
        format: 'yyyy',
    	language: "es",
    	startView: 4,
    	minView: 4,
    	maxView: 4,
    	autoclose: true
	});
	$('#fechadesde').datetimepicker({
        format: 'yyyy-mm-dd',
    	language: "es",
    	startView: 3,
    	minView: 2,
    	maxView: 2,
    	autoclose: true,
    	todayBtn: true
	});
	$('#fechahasta').datetimepicker({
        format: 'yyyy-mm-dd',
    	language: "es",
    	startView: 3,
    	minView: 2,
    	maxView: 2,
    	autoclose: true,
    	todayBtn: true
	});
};

//window.onload = initUI;
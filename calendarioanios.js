initUI = function() {
	$('#anio').datetimepicker({
        viewMode: 'years',
        format: 'yyyy',
    	language: "es",
    	startView: 4,
    	minView: 4,
    	maxView: 4,
    	autoclose: true
	})
};
window.onload = initUI;
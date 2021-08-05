var retryPeriod = 30000;
var retryLimit = 10;

function initAjaxHandling(initData) {
	if (initData.retryLimit) {
		retryLimit = initData.retryLimit;
	}
	if (initData.retryPeriod) {
		retryPeriod = initData.retryPeriod;
	}
}


$(document).ready(function() {
	var sessionExpiredDialog = $("#sessionExpired").dialog({
		modal: true,
		width: 300,
		resizable: false,
		autoOpen: false,
		open: function() {			
			var sessionExpiredDialogDiv = $("#sessionExpired").closest(".ui-dialog");
			
			sessionExpiredDialogDiv.find(".ui-dialog-titlebar").remove();
			sessionExpiredDialogDiv.find(".ui-dialog-buttonpane").remove();
			sessionExpiredDialogDiv.find(".ui-dialog-buttonset").remove();
			sessionExpiredDialogDiv.removeClass("ui-front ui-corner-all").addClass("padding-none");
			sessionExpiredDialogDiv.find(".ui-corner-all").removeClass("ui-corner-all");
		}
	});
	var siteUnreachableDialog = $("#siteDown").dialog({
		modal: true,
		width: 510,
		resizable: false,
		autoOpen: false,
		open: function() {			
			var siteUnreachableDialogDiv = $("#siteDown").closest(".ui-dialog");
			
			siteUnreachableDialogDiv.find(".ui-dialog-titlebar").remove();
			siteUnreachableDialogDiv.find(".ui-dialog-buttonpane").remove();
			siteUnreachableDialogDiv.find(".ui-dialog-buttonset").remove();
			siteUnreachableDialogDiv.removeClass("ui-front ui-corner-all").addClass("padding-none");
			siteUnreachableDialogDiv.find(".ui-corner-all").removeClass("ui-corner-all");
		}
	});
	
})
.ajaxSend(function(e, xhr, options) {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	if (typeof header != 'undefined' && typeof token != 'undefined') {
		xhr.setRequestHeader(header, token);
	}
})
.ajaxSuccess(function(){
	if (typeof(hideAjaxError) == "function") {
		hideAjaxError();
	}
	resetErrorDialogs();
})
.ajaxError(function(event, jqXHR, ajaxReq, error) {
	handleAjaxError(jqXHR, ajaxReq, error);
});

function resetErrorDialogs() {
	$("#sessionExpired").addClass("hide").dialog("close");
	$("#siteDown").addClass("hide").dialog("close");
}

function handleAjaxError(jqXHR, ajaxReq, error) {
	resetErrorDialogs();
    if(ajaxCallIsAborted(jqXHR)){
        return;
    }
	var normalRetry = true;
	if (error == "timeout") {
		displayDefaultAjaxError(jqXHR, ajaxReq, error);
	} else {
		normalRetry = handleErrorCode(jqXHR, ajaxReq, error);
	}
	if (normalRetry && !ajaxReq.ignoreGlobalError) {
		doNormalRetry(ajaxReq);
	}
}

function ajaxCallIsAborted(xhr){
    return xhr.status === 0 && xhr.statusText === 'abort';
}

function handleErrorCode(jqXHR, ajaxReq, error) {
	switch (jqXHR.status) {
		case 0:
		case 502:
			$("#siteDown").removeClass("hide").dialog("open");
			retry(ajaxReq, retryPeriod);
			return false;
		case 401:
		case 405:
			$("#sessionExpired").removeClass("hide").dialog("open");
			retry(ajaxReq, 5000);
			return false;
		case 403:
		    showErrorDialog("Permission denied");
		    return false;
		default:
			displayDefaultAjaxError(jqXHR, ajaxReq, error);
			return true;
	}
}

function displayDefaultAjaxError(jqXHR, ajaxReq, error) {
	if (useGlobalError(ajaxReq) && displayErrorFunctionExists()) {
		displayAjaxError(jqXHR, error, ajaxReq);
	}
}

function useGlobalError(ajaxReq) {
	return !ajaxReq.ignoreGlobalError;
}

function displayErrorFunctionExists() {
	return typeof(displayAjaxError) == "function";
}

function doNormalRetry(ajaxReq) {
	if (!ajaxReq.hasOwnProperty("retryCount")) {
		ajaxReq.retryCount = 0;
	}
	if (++ajaxReq.retryCount < retryLimit) {
		retry(ajaxReq, retryPeriod);
	}
}

function retry(ajaxReq, delay) {
	setTimeout(function() {
		$.ajax(ajaxReq);
	}, delay);
}
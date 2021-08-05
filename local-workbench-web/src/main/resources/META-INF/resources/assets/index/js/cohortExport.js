var pollDelay = 5000;
var maxCohortSizeForFactExport;

$(document).ready(function(){
	maxCohortSizeForFactExport = parseInt($("#maxCohortSizeForFactExport").val());
	cohortSize = parseInt($("#cohort-size").val())
	if(cohortSize == null || cohortSize > maxCohortSizeForFactExport){
	    $('#form-contentType option[value=PATIENTFACTS]').attr("disabled","disabled").attr("title","Patient facts can't be exported because the cohort exceeds the maximum size of " + maxCohortSizeForFactExport + " for fact export");
	}

	$("#form-export").on("click", exportCohort);
	$("#form-startdate").fdatepicker({
		format: 'yyyy-mm-dd'
	});
	$("#form-enddate").fdatepicker({
		format: 'yyyy-mm-dd'
	});
	
	$("#form-contentType").on("change", contentTypeChanged);
	
	fillExportTable();
	setInterval(fillExportTable, pollDelay);
});

function exportCohort() {
	var maxConcurrentExports = $("#maxConcurrentExports").val();
	var cohortSize = $("#cohort-size").val();
	if ($(".export-status:contains('RUNNING')").length >= maxConcurrentExports) {
		$("#export-error-message .error-text").text("Only " + maxConcurrentExports + " concurrently running exports allowed.");
		$("#export-error-message").addClass("is-active");
		setTimeout(function() {
			$("#export-error-message").removeClass("is-active");
		}, 3000);
	}
	else if ($('#form-contentType option:selected').val() == "PATIENTFACTS" && ($("#cohort-size").val() == null || $("#cohort-size").val() > maxCohortSizeForFactExport)) {
        $("#export-error-message .error-text").text("Cohort exceeds the maximum size of " + maxCohortSizeForFactExport + " for fact export" );
        $("#export-error-message").addClass("is-active");
        setTimeout(function() {
            $("#export-error-message").removeClass("is-active");
        }, 3000);
    }
	else {
		var cohortId = $("#cohort-id").val();
		var fileName = $("#form-filename").val();
		var formatType = $('#form-formatType option:selected').val();
		var contentType = $('#form-contentType option:selected').val();
		var singlePatientIdeSource = $('#form-patientIdeSourceSingle').is(':checked');
		var patientIdeSource = $('#form-patientIdeSource option:selected').val();
		var startDate = $("#form-startdate").val();
		var endDate = $("#form-enddate").val();
		var exportURL = lwbSettings.cohortExportURL;
		var exportData = {
			'cohortId': cohortId,
			'fileName': fileName,
			'formatType': formatType,
			'contentType': contentType,
			'singlePatientIdeSource': singlePatientIdeSource,
			'patientIdeSource': patientIdeSource,
			'startDate': startDate,
			'endDate': endDate
		};
		
		var validated = true;
		var validationError;
		if (fileName == "") {
			validated = false;
			validationError = "Filename cannot be empty!";
		}
		else if (startDate != "" && endDate != "" && startDate > endDate) {
			validated = false;
			validationError = "Facts start date should be earlier than facts end date!";
		}
		
		if (!validated) {
			$("#export-error-message .error-text").text(validationError);
			$("#export-error-message").addClass("is-active");
			setTimeout(function() {
				$("#export-error-message").removeClass("is-active");
			}, 3000);
		}
		else {
			$.ajax({
				'url': exportURL,
				'type': "POST",
				'ignoreGlobalError': true,
				'data': JSON.stringify(exportData),
				'contentType': "application/json",
				'success': function() {
					fillExportTable();
				},
				'error': function(xhr, statusStr, text) {
					if (xhr.status == 412) {
						$("#export-error-message .error-text").text("Only " + maxConcurrentExports + " concurrently running exports allowed.");
					}
					else if (xhr.status == 400) {
						$("#export-error-message .error-text").text("File name contains one or more disallowed symbols.");
					}
					else {
						$("#export-error-message .error-text").text("Unexpected error occurred while exporting cohort.");
					}
					$("#export-error-message").addClass("is-active");
					setTimeout(function() {
						$("#export-error-message").removeClass("is-active");
					}, 3000);
				}
			});
		}
	}
}

function contentTypeChanged() {
	var contentType = $(this).find("option:selected").val();
	if (contentType == "PATIENTS") {
		$(".facts-start-date").addClass("hide");
		$(".facts-end-date").addClass("hide");
	}
	else {
		$(".facts-start-date").removeClass("hide");
		$(".facts-end-date").removeClass("hide");
	}
}

function fillExportTable() {
	var exportListURL = lwbSettings.cohortExportListURL;
	$.ajax({
		'url': exportListURL,
		'type': "GET",
		'dataType': "json",
		'ignoreGlobalError': true,
		'success': function(data) {
			fillExports(data);
		},
		'error': function() {
		    showErrorDialog("Failed retrieving cohort exports");
		}
	});
}

function fillExports(data) {
	if (data.length != 0) {
		$("#export-table").removeClass("hide");
	}
	else {
		$("#export-table").addClass("hide");
	}
	var tablebody = $("#export-table tbody").empty();
	$.each(data, function(index, cohortExport) {
        createExportRow(cohortExport).appendTo(tablebody);
	});
}

function createExportRow(cohortExport) {
    var row = $("<tr>");
    createNameElement(cohortExport).appendTo(row);
    createSizeElement(cohortExport).appendTo(row);
    createDateElement(cohortExport).appendTo(row);
    createStatusElement(cohortExport).appendTo(row);
    createActionsElement(cohortExport).appendTo(row);
    return row;
}

function createNameElement(cohortExport) {
    var fileNameTD = $("<td>").addClass("export-fileName");
    if (cohortExport.status === EXPORT_STATUS.SUCCEEDED) {
        $("<a>").attr("href", "#").text(cohortExport.fileName).appendTo(fileNameTD).on("click", function() {
            downloadExport(cohortExport.id);
        });
    } else {
        fileNameTD.text(cohortExport.fileName);
    }
    return fileNameTD;
}

function createSizeElement(cohortExport) {
    return $("<td>").addClass("export-size").text(cohortExport.size + "KB");
}

function createDateElement(cohortExport) {
    return $("<td>").addClass("export-date").text(formatDate(cohortExport.date));
}

function createStatusElement(cohortExport) {
    var statusTD = $("<td>").addClass("export-status").text(cohortExport.status);
    if (cohortExport.status === EXPORT_STATUS.FAILED) {
        var errorMessageIcon = $("<i>").addClass("fa fa-info-circle").attr("title", "").tooltip({
            content: cohortExport.errorMessage
        });
        statusTD.append(" ");
        statusTD.append(errorMessageIcon);
    }
    return statusTD;
}

function createActionsElement(cohortExport) {
    var actions = $("<td>").addClass("export-action");
    if (cohortExport.status === EXPORT_STATUS.RUNNING) {
        createActionCancel(cohortExport).appendTo(actions);
    } else {
        createActionRemove(cohortExport).appendTo(actions);
    }
    return actions;
}

function createActionCancel(cohortExport) {
    var action = $("<a>").attr("href", "#").text(i18next.t("common:Cancel"));
    action.on("click", function() {
        cancelExport(cohortExport.id);
    });
    return action;
}

function createActionRemove(cohortExport) {
    var action = $("<a>").attr("href", "#").text(i18next.t("common:Remove"));
    action.on("click", function() {
        removeExport(cohortExport.id);
    });
    return action;
}

function cancelExport(exportId) {
	var exportCancelURL = lwbSettings.cohortExportCancelURL;
	var exportCancelData = {
		'exportId': exportId
	}
	$.ajax({
		'url': exportCancelURL,
		'type': "POST",
		'ignoreGlobalError': true,
		'data': JSON.stringify(exportCancelData),
		'contentType': "application/json",
		'success': function(data) {
			fillExportTable();
		},
		'error': function() {
			//alert("Cancel failed");
		}
	});
}

function removeExport(exportId) {
	var exportRemoveURL = lwbSettings.cohortExportRemoveURL;
	var exportRemoveData = {
		'exportId': exportId
	}
	$.ajax({
		'url': exportRemoveURL,
		'type': "POST",
		'ignoreGlobalError': true,
		'data': JSON.stringify(exportRemoveData),
		'contentType': "application/json",
		'success': function(data) {
			fillExportTable();
		},
		'error': function() {
			//alert("Remove failed");
		}
	});
}

function downloadExport(exportId) {
	var exportDownloadURL = lwbSettings.cohortExportDownloadURL + "?exportId=" + exportId;
	$.ajax({
		type: "GET",
		url: exportDownloadURL,
		ignoreGlobalError: true,
		complete: function(jqXHR, textStatus) {
		    switch (jqXHR.status) {
		        case 200:
					location.replace(exportDownloadURL);
		            break;
		        default:
		            showErrorDialog("Error: An unexpected error occurred while downloading Cohort report.");
		    }
		}
	});
}
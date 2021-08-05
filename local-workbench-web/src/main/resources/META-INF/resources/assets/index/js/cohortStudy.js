$(document).ready(function(){
	$(".table-overview")
	.on("click", "tr .recalculate-icon", recalculate_cohort)
	.on("click", "tr .edit-icon", openEditDialog)
	.on("click", "tr .delete-icon", show_delete_icons)
	.on("click", "tr .delete-confirm-cancel .confirm-button", confirm_delete)
	.on("click", "tr .delete-confirm-cancel .cancel-button", cancel_delete)
	.on("click", "tr .info-icon", function(event) {
		show_cohort_sidebar(event);
	});
	
	$(".cohort-details-sidebar-content")
	.on("click", ".close-icon", hide_details)
	.on("click", ".cohort-edit", function() {
		$(".table-overview tr.is-active .edit-icon").trigger("click");
	})
	.on("click", ".cohort-recalculate", function() {
		$(".table-overview tr.is-active .recalculate-icon")[0].click();
	})
	.on("click", ".cohort-export", function() {
		$(".table-overview tr.is-active .download-icon")[0].click();
	})
	.on("click", ".cohort-delete", function() {
		$(".cohort-details-sidebar-content .delete-confirm-cancel").removeClass("hide");
		$(".cohort-details-sidebar-content .cohort-delete").addClass("hide");
	})
	.on("click", ".delete-confirm-cancel .confirm", confirm_delete_details)
	.on("click", ".delete-confirm-cancel .cancel", function() {
		$(".cohort-details-sidebar-content .delete-confirm-cancel").addClass("hide");
		$(".cohort-details-sidebar-content .cohort-delete").removeClass("hide");
	})
	.on("click", ".cohort-download", downloadCohort)
	.on("click", ".cohort-reuse", reuseCohort);
	
	$(".sidebar-left").on("keydown", ".search-field", filter_studies_live);
	$(".sidebar-left").on("click", ".button.search", filter_studies);
	$("#page-bread-crumbs").on("click", ".page-crumb", filter_studies_reset);
	$(".page-items .page-item-numbers").on("click", "li", update_pager_size);
	
	$(".import-cohort").on("click", import_cohort);
	$("#importDialog").on("click", ".import-file-text", import_browse_button);
	$("#importDialog").on("change", ".cohort-import-browse", import_browse_clicked);
	
	$(".cohorts-table").on("click", "th", function() {
		setTimeout(update_sort_icons, 100);
	});
	setTimeout(update_sort_icons, 100);
	
	var cohortsTable = $(".cohorts-table");
	if ($(cohortsTable).find("tr.cohort-detail").length) {
		$.tablesorter.addParser({
			id: 'modified',
			is: function(s) {
				return false;
			},
			format: function(val, table, cell) {
				var input = $(cell).find("input[type='hidden']");
				return input.val();
			},
			type: 'isoDate'
		});
		$(cohortsTable).tablesorter({
			sortList: [[3, 1]],
			headers: {
				0: { sorter: true, filter: true },
				1: { sorter: true, filter: false },
				2: { sorter: true, filter: false },			
				3: { sorter: 'modified', filter: false },
				4: { sorter: false, filter: false },
				5: { sorter: false, filter: false },
				6: { sorter: false, filter: false }
			},
			cssAsc: "asc",
			cssDesc: "desc",
			widgets: ["filter"],
			widgetOptions: {
				filter_cssFilter   : 'filter-item',
				filter_childRows   : false,
				filter_hideFilters : true,
				filter_ignoreCase  : true,
				filter_saveFilters : false,
				filter_searchDelay : 300,
				filter_startsWith  : false,
				filter_hideFilters : true,
				filter_filteredRow : 'hide',
				filter_functions: {
					0 : function(e, n, f, i, $r, c, data) {
						var searchValue = f.toUpperCase();
						var title = $r.find(".cohort-title");
						var patients = $r.find(".cohort-patientcount");
						var author = $r.find(".cohort-author");
						var modified = $r.find(".cohort-modified");
						var keywordFilter = (title.text().toUpperCase().indexOf(searchValue) >= 0)
											|| (patients.text().toUpperCase().indexOf(searchValue) >= 0)
											|| (author.text().toUpperCase().indexOf(searchValue) >= 0)
											|| (modified.text().toUpperCase().indexOf(searchValue) >= 0);
						return keywordFilter;
			        }
				}
			}
		}).tablesorterPager({
			container: $(".footer"),
			size: 10,
			output: '{page}/{totalPages}',
	
		    updateArrows: true,
		    page: 0,
		    removeRows: false,
		    
		    cssNext: '.next',
		    cssPrev: '.prev',
		    cssFirst: '.first',
		    cssLast: '.last',
		    cssGoto: '.move-page',
		    cssDisabled: 'disabled',
			cssPageSize: '.pager-size',
		    positionFixed: false
		});
		$(".tablesorter-filter-row").hide();
	}
	
	if (! $(".blank-state-button").length) {
		checkBusy();
	}
});

function displayAjaxError(xhr, textStatus, ajaxReq) {
	if (! ajaxReq.ignoreGlobalError) {
		$("#global-ajax-error").addClass("is-active");
		setTimeout(function() {
			$("#global-ajax-error").removeClass("is-active");
		}, 3000);
	}	
}

function checkBusy(noRefresh) {
	var busyURL = lwbSettings.busyURL + "?studyId=" + $("#study-id").val();
	
	$.ajax({
		'url': busyURL,
		'type': "GET",
		'success': function(data) {
			$(".title-cell .cohort-busy").remove();
			$(".cohort-edit-options.hide").removeClass("hide");
			if (data.length) {
				$.each(data, function(index, value) {
					var tableRow = $("input[name='cohort-id-"+value+"']").closest("tr");
					tableRow.find(".title-cell").append("<span class='small cohort-busy'>(cohort in use)</div>");
					tableRow.find(".cohort-edit-options").addClass("hide");
				});
			}

			if (!noRefresh) {
				setTimeout(checkBusy, 10000);
			}
		}
	});
}

function recalculate_cohort() {
	var cohortId = $(this).closest("tr").find(".cohort-id").val();
	open_recalculate_dialog(cohortId);
}

function downloadCohort() {
    var cohortId = $(".table-overview tr.is-active").find(".cohort-id").val();
    downloadCohortFilters(cohortId);
}

function reuseCohort() {
    var cohortId = $(".table-overview tr.is-active").find(".cohort-id").val();
    location.replace(lwbSettings.reuseURL + "?cohortId=" + cohortId);
}

function openEditDialog() {
	var tabRow = $(this).closest("tr");
	var editDialog = $("#editDialog");
	
	var cohortId = tabRow.find(".cohort-id").val();
	var title = tabRow.find(".cohort-name").val();
	var description = tabRow.find(".cohort-descr").val();

	editDialog.find(".edit-id").val(cohortId);
	editDialog.find(".cohort-title").val(title);
	editDialog.find(".cohort-descr").val(description);
	
	editDialog.dialog({
		title: i18next.t("editDialog.title"),
		modal: true,
		width: 600,
		resizable: false,
		autoOpen: false,
		buttons: [ {
				text: i18next.t("common:Save"),
				click: function() {
					var title = editDialog.find(".cohort-title").val();
					var description = editDialog.find(".cohort-descr").val();
					var cohortId = editDialog.find(".edit-id").val();
					
					if (cohortId && title) {
						var editURL = lwbSettings.updateURL;
						var editData = {
							'cohortId': cohortId,
							'title': title,
							'description': description
						};
						$.ajax({
							'url': editURL,
							'type': "POST",
							'data': JSON.stringify(editData),
							'contentType': "application/json",
							'success': function() {
								$("#editDialog").dialog("close");
								location.reload();
							},
						});
					}
					else {
						editDialog.find(".cohort-title").addClass("form-field-error");
						editDialog.find(".form-text-error").remove();
						$("<div class='form-text-error margin-medium-bottom'>Cohort title is required</div>")
						.insertAfter(editDialog.find(".cohort-title"));
					}
				}
			},
			{
				text: i18next.t("common:discardChanges"),
				click: function() {
					$("#editDialog").dialog("close");
				}
			}	
		],
		open: function() {
			open_edit_dialog(editDialog);
		}
	});
	editDialog.removeClass("hide");
	editDialog.dialog("open");
}

function open_edit_dialog(editDialog) {
	clear_dialog_errors(editDialog);
	add_title_bar(editDialog, i18next.t("editDialog.title"));
	add_button_classes(editDialog);
	add_dialog_styling(editDialog);
}

function clear_dialog_errors(dialog) {
	dialog.find(".form-text-error").remove();
	dialog.find(".form-field-error").removeClass("form-field-error");			
}

function add_title_bar(dialog, title) {
	var dialogDiv = dialog.closest(".ui-dialog");
	var closeIcon = $("<a>").html("<i class='fa fa-fw fa-close'></i>")
	.on("click", function() {
		dialog.dialog("close");
	});
	var titlebar = dialogDiv.find(".ui-dialog-titlebar").attr("class", "primary title-bar").empty();
	$("<div>").html(title).addClass("center title").appendTo(titlebar);
	$("<span>").addClass("right").append(closeIcon).appendTo(titlebar);
}

function add_button_classes(dialog) {
	var dialogDiv = dialog.closest(".ui-dialog");
	dialogDiv.find("button").attr("class", "button")
	.addClass(function(index) {
		var className = "";
		if (index != 0) className += " secondary";
		return className;
	});
}

function add_dialog_styling(dialog) {
	var dialogDiv = dialog.closest(".ui-dialog");
	dialogDiv.find(".ui-dialog-buttonpane").attr("class", "padding-left");
	dialogDiv.find(".ui-dialog-buttonset").removeClass("ui-dialog-buttonset");
	dialogDiv.removeClass("ui-front ui-corner-all").addClass("padding-none");
	dialogDiv.find(".ui-corner-all").removeClass("ui-corner-all");
}

function show_delete_icons() {
	var tabRow = $(this).closest("tr");
	
	tabRow.find(".delete-icon").addClass("hide");
	tabRow.find(".edit-icon").addClass("hide");
	tabRow.find(".delete-confirm-cancel").removeClass("hide");
}

function confirm_delete() {
	var tabRow = $(this).closest("tr");
	var cohortId = tabRow.find(".cohort-id").val();
	var deleteURL = lwbSettings.deleteURL + "?cohortId=" + cohortId;
	$.ajax({
		'url': deleteURL,
		'type': "POST",
		'success': function() {
			tabRow.remove();
			showDeletionSucceeded();
		},
		'error': function(xhr, statusStr, text) {
			if (xhr.status == 412) {
				$("#cohort-deleted-analysis-associated-error").addClass("is-active");
				setTimeout(function() {
					$("#cohort-deleted-analysis-associated-error").removeClass("is-active");
				}, 5000);
			}
			else {
				if (xhr.status == 401) {
					checkBusy(true);
				}
				$("#cohort-deleted-error").addClass("is-active");
				setTimeout(function() {
					$("#cohort-deleted-error").removeClass("is-active");
				}, 3000);
			}
		},
		'complete': function() {
			tabRow.find(".edit-icon").removeClass("hide");
			tabRow.find(".delete-icon").removeClass("hide");
			tabRow.find(".delete-confirm-cancel").addClass("hide");
		},
		'ignoreGlobalError': true
	});
}

function confirm_delete_details() {
	var tabRow = $(".cohorts-table tr.is-active");
	var cohortId = tabRow.find(".cohort-id").val();
	var deleteURL = lwbSettings.deleteURL + "?cohortId=" + cohortId;
	$.ajax({
		'url': deleteURL,
		'type': "POST",
		'success': function() {
			tabRow.remove();
			$(".cohort-details-sidebar-content .close-icon").trigger("click");
			showDeletionSucceeded();
		},
		'error': function(xhr, statusStr, text) {
			$(".cohort-details-sidebar-content .cohort-delete").removeClass("hide");
			$(".cohort-details-sidebar-content .delete-confirm-cancel").addClass("hide");
			
			if (xhr.status == 412) {
				$("#cohort-deleted-analysis-associated-error").addClass("is-active");
				setTimeout(function() {
					$("#cohort-deleted-analysis-associated-error").removeClass("is-active");
				}, 5000);
			}
			else {
				if (xhr.status == 401) {
					checkBusy(true);
				}
				$("#cohort-deleted-error").addClass("is-active");
				setTimeout(function() {
					$("#cohort-deleted-error").removeClass("is-active");
				}, 3000);
			}
		},
		'ignoreGlobalError': true
	});
}

function showDeletionSucceeded() {
	if ($(".cohorts-table tbody tr").length == 0) {
		location.reload();
	}
	else {
		$("#cohort-deleted-success").addClass("is-active");
		setTimeout(function() {
			$("#cohort-deleted-success").removeClass("is-active");
		}, 3000);
	}
}

function cancel_delete() {
	var tabRow = $(this).closest("tr");
	
	tabRow.find(".edit-icon").removeClass("hide");
	tabRow.find(".delete-icon").removeClass("hide");
	tabRow.find(".delete-confirm-cancel").addClass("hide");
}

function show_cohort_sidebar(event) {
	var detailsView = $(".cohort-details-sidebar-content");
	var cohortRow = $(event.target).closest(".cohort-detail");
	var cohortId = cohortRow.find(".cohort-id").val();
	$(".details-hide").addClass("hide");
	
	var cohortDetailsUrl = lwbSettings.detailsURL + "?studyId=0&cohortId=" + cohortId;
	$.ajax({
		'url': cohortDetailsUrl,
		'type': "POST",
		'dataType': "json",
		'success': function(cohortData) {
			fill_cohort_sidebar(detailsView, cohortData);
			highlight_row(cohortRow);
			detailsView.removeClass("hide");
		},
		'ignoreGlobalError': true,
	});
}

function fill_cohort_sidebar(detailsView, cohortData) {
	detailsView.find(".description").text(cohortData.description);
	detailsView.find(".cohort-author").text(cohortData.author);
	detailsView.find(".cohort-modified").text(cohortData.modifiedLabel);
	detailsView.find(".cohort-source").text(cohortData.source);
	detailsView.find(".cohort-refDate").text(cohortData.referenceDate);
	renderDynamicComponents(detailsView, cohortData);
	detailsView.find(".filters-definition").attr("href", lwbSettings.definitionURL + "?cohortId=" + cohortData.id);
}

function renderDynamicComponents(detailsView, cohortData) {
	var renderSettings = getRenderSettings(cohortData);
    renderTitle(detailsView, renderSettings, cohortData);
    renderPatientCount(detailsView, renderSettings, cohortData);
    renderStatusLabels(detailsView, renderSettings);
    renderButtons(detailsView, renderSettings);
}

function renderTitle(detailsView, renderSettings, cohortData) {
    if (renderSettings.showDetailsLink) {
        var cohortURL = lwbSettings.cohortURL + "?cohortId=" + cohortData.id;
        detailsView.find(".title").empty().append($("<a>").attr("href", cohortURL).text(cohortData.name));
    } else {
        detailsView.find(".title").empty().text(cohortData.name);
    }
}

function renderPatientCount(detailsView, renderSettings, cohortData) {
    if (renderSettings.showPatientCount) {
        detailsView.find(".patients").removeClass("hide");
        detailsView.find(".patients").html(get_patient_count_label(cohortData));
    } else {
        detailsView.find(".patients").addClass("hide");
    }
}

function renderStatusLabels(detailsView, renderSettings) {
    if (renderSettings.showSavingLabel) {
        detailsView.find(".saving").removeClass("hide");
    } else {
        detailsView.find(".saving").addClass("hide");
    }
    if (renderSettings.showRecalculatingLabel) {
        detailsView.find(".recalculating").removeClass("hide");
    } else {
        detailsView.find(".recalculating").addClass("hide");
    }
    if (renderSettings.showErrorLabel) {
        detailsView.find(".error").removeClass("hide");
    } else {
        detailsView.find(".error").addClass("hide");
    }
}

function renderButtons(detailsView, renderSettings) {
    if (renderSettings.showRecalculateButton) {
        detailsView.find(".cohort-recalculate").removeClass("hide");
    } else {
        detailsView.find(".cohort-recalculate").addClass("hide");
    }
    if (renderSettings.showExportButton) {
        detailsView.find(".cohort-export").removeClass("hide");
    } else {
        detailsView.find(".cohort-export").addClass("hide");
    }
}

function getRenderSettings(cohortData) {
	switch(cohortData.status) {
	case cohortStatus.started:
		return getSavingStateRenderSettings();
	case cohortStatus.recalculating:
		return getRecalculatingStateRenderSettings(cohortData.detailsPermitted);
	case cohortStatus.error:
	    return getErrorStateRenderSettings();
	default:
		return getDoneStateRenderSettings(cohortData.detailsPermitted);
	}
}

function getDefaultRenderSettings() {
    return {
        "showDetailsLink": false,
        "showPatientCount": false,
        "showSavingLabel": false,
        "showRecalculatingLabel": false,
        "showErrorLabel": false,
        "showExportButton": false,
        "showRecalculateButton": false
    }
}

function getSavingStateRenderSettings() {
    var settings = getDefaultRenderSettings();
    settings.showSavingLabel = true;
    return settings;
}

function getRecalculatingStateRenderSettings(areDetailsPermitted) {
    var settings = getDefaultRenderSettings();
    settings.showPatientCount = true;
    settings.showRecalculatingLabel = true;
    if (areDetailsPermitted) {
        settings.showDetailsLink = true;
        settings.showExportButton = true;
    }
    return settings;
}

function getDoneStateRenderSettings(areDetailsPermitted) {
    var settings = getDefaultRenderSettings();
    settings.showPatientCount = true;
    settings.showRecalculateButton = true;
    if (areDetailsPermitted) {
        settings.showDetailsLink = true;
        settings.showExportButton = true;
    }
    return settings;
}

function getErrorStateRenderSettings() {
    var settings = getDefaultRenderSettings();
    settings.showErrorLabel = true;
    settings.showRecalculateButton = true;
    return settings;
}

function get_patient_count_label(cohortData) {
	var patientCount = cohortData.patientCount;
	if (patientCount == 1) {
		return patientCount + " " + i18next.t("sidebar.patient");
	} else {
		return patientCount + " " + i18next.t("sidebar.patients");
	}
}

function highlight_row(cohortRow) {
	$(".cohorts-table tr.is-active").removeClass("is-active");
	cohortRow.addClass("is-active");
}

function hide_details() {
	var detailsView = $(".cohort-details-sidebar-content");

	$(".cohorts-table tr.is-active").removeClass("is-active");
	
	detailsView.addClass("hide");
	$(".details-hide").removeClass("hide");
}

function update_sort_icons() {
	$(".cohorts-table th").each(function() {
		if (! $(this).hasClass("sorter-false")) {
	        if ($(this).hasClass("asc")) {
	        	$(this).find("i").attr("class", "fa fa-sort-asc")
	        }
	        else if ($(this).hasClass("desc")) {
	        	$(this).find("i").attr("class", "fa fa-sort-desc")
	        }
	        else {
	        	$(this).find("i").attr("class", "fa fa-sort")
	        }
		}

	});
}

function update_pager_size(event) {
	var pager_size = $(event.target).text();
	$(".page-items .pager-size").val(pager_size).trigger("change");
	$.tablesorter.storage( $(".cohorts-table")[0], "tablesorter-pager", pager_size );
	
	$(".page-items li.is-active").removeClass("is-active");
	$(this).addClass("is-active");
}

function make_pager_number(number) {
	var result = $("<li>").addClass("move-page update-pager").attr("page", number-1);
	$("<a>").attr("href", "#").html(number).appendTo(result);
	return result;
}

function filter_studies_live() {
	setTimeout(filter_studies, 10);
}

function filter_studies() {
	var filterValue = $(".sidebar-left .search-field").val();
	var filters = [];
	filters[0] = filterValue;
	$.tablesorter.setFilters( $(".cohorts-table"), filters, true );
	
	var search_val = "search results \"" + filterValue + "\"";
	breadcrumb_set_filter(search_val);
}

function filter_studies_reset() {
	var filters = [];
	$.tablesorter.setFilters( $(".cohorts-table"), filters, true );
	
	$(".sidebar-left .search-field").val("");
	breadcrumb_remove_filter();
}

function import_cohort() {
	var importDialog = $("#importDialog")
	importDialog.removeClass("hide").dialog({
		title: i18next.t("importDialog.title"),
		modal: true,
		width: 400,
		resizable: false,
		autoOpen: true,
		buttons: [{
				text: i18next.t("importDialog.create"),
				click: function() {
					var token = $("meta[name='_csrf']").attr("content");					
					var importForm = $("#importDialog form");					
					var csrf_url = importForm.attr("action") + "&_csrf=" + token;
					importForm.attr("action", csrf_url);
					
					$("#importForm").submit();
				}
			},
			{
				text: i18next.t("common:Cancel"),
				click: function() {
					$("#importDialog").dialog("close");
				}
			}
		],
		open: function() {
			var importDialogDiv = importDialog.closest(".ui-dialog");
			var closeIcon = $("<a>").html("<i class='fa fa-fw fa-close'></i>")
			.on("click", function() {
				$( "#importDialog" ).dialog("close");
			});
			
			var titlebar = importDialogDiv.find(".ui-dialog-titlebar").attr("class", "primary title-bar").empty();
			$("<div>").html(i18next.t("importDialog.title")).addClass("center title").appendTo(titlebar);
			$("<span>").addClass("right").append(closeIcon).appendTo(titlebar);
			
			importDialogDiv.find("button").attr("class", "button")
			.addClass(function(index) {
				var className = "";
				if (index != 0) className += " secondary cancel-import";
				else className += " disabled do-import";
				return className;
			});
			importDialogDiv.find(".ui-dialog-buttonpane").attr("class", "padding-left");
			importDialogDiv.find(".ui-dialog-buttonset").removeClass("ui-dialog-buttonset");
			importDialogDiv.removeClass("ui-front ui-corner-all").addClass("padding-none");
			importDialogDiv.find(".ui-corner-all").removeClass("ui-corner-all");
		}
	});
}

function import_browse_button() {
	$("#importDialog .cohort-import-browse").trigger("click");
}

function import_browse_clicked() {
	var files = $(this).prop('files');
	$("#importDialog .import-file-text").val(files[0].name);
	$("#importDialog").closest(".ui-dialog").find(".do-import").removeClass("disabled");
}


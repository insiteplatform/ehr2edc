$(document).ready(function() {
	$(".table-overview")
	.on("click", "tr .edit-icon", openEditDialog)
	.on("click", "tr .delete-icon", show_delete_icons)
	.on("click", "tr .delete-confirm-cancel .confirm-button", confirm_delete)
	.on("click", "tr .delete-confirm-cancel .cancel-button", cancel_delete)
	$(".create-study").on("click", function() {
		openStudyDialog();
		var studyDialog = $("#studyDialog");
		
		studyDialog.find(".study-title").val("");
		studyDialog.find(".study-description").val("");
		studyDialog.removeClass("hide").dialog("open");
	});
	
	$(".sidebar-left").on("keydown", ".search-field", filter_studies_live);
	$(".sidebar-left").on("click", ".search-button", filter_studies);
	$("#page-bread-crumbs").on("click", ".page-crumb", filter_studies_reset);
	var cohortstudiesTable = $(".cohortstudies-table");
	if ($(cohortstudiesTable).find("tr.cohortstudy-detail").length) {
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
		$(cohortstudiesTable).tablesorter({
			sortList: [[3, 1]],
			headers: {
				0: { sorter: true, filter: true },
				1: { sorter: true, filter: false },
				2: { sorter: true, filter: false },			
				3: { sorter: 'modified', filter: false },
				4: { sorter: true, filter: true },
				5: { sorter: false, filter: false }
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
						var title = $r.find(".cohortstudy-title");
						var cohorts = $r.find(".cohortstudy-cohortcount");
						var author = $r.find(".cohortstudy-author");
						var modified = $r.find(".cohortstudy-modified");
						var keywordFilter = (title.text().toUpperCase().indexOf(searchValue) >= 0)
											|| (cohorts.text().toUpperCase().indexOf(searchValue) >= 0)
											|| (author.text().toUpperCase().indexOf(searchValue) >= 0)
											|| (modified.text().toUpperCase().indexOf(searchValue) >= 0);
						return keywordFilter;
			        }
				}
			}
		}).tablesorterPager({
			container: $(".footer"),
			size: $.tablesorter.storage( cohortstudiesTable[0], 'tablesorter-pagesize') || 10,
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
		init_pager_size(cohortstudiesTable);
        $(".page-items .page-item-numbers").on("click", "li", update_pager_size);
		cohortstudiesTable.on("click", "th", function() {
			setTimeout(update_sort_icons, 100);
		});
		update_sort_icons();
	}
});

function openStudyDialog(studyId) {
	if (typeof studyId !== "undefined") {
		var dialog_title = i18next.t("createDialog.editTitle");
		var submit_text = i18next.t("common:Save");
		var submit_class = "save-cohortstudy"
	}
	else {
		var dialog_title = i18next.t("createDialog.title");
		var submit_text = i18next.t("createDialog.createStudy");
		var submit_class = "create-cohortstudy";
	}
	
	
	
	$("#studyDialog").dialog({
		modal: true,
		resizable: false,
		width: 600,
		autoOpen: false,
		title: dialog_title,
		buttons: [ {
				text: submit_text,
				class: submit_class,
				click: function() {
					var title = $("#studyDialog .study-title").val();
					var description = $("#studyDialog .study-description").val();
					
					if (!title || title == "") {
						$(".form-text-error").remove();
						$(".study-title").addClass("form-field-error");
						$("<div>").addClass("form-text-error margin-top").html("Cohort study title is required")
						.appendTo($(".study-description").closest("label"));
					}
					else if (typeof studyId !== "undefined") {
						var editData = {
							'studyId': studyId,
							'title': title,
							'description': description
						};
						$.ajax({
							url: studyUpdateURL,
							type: "POST",
							data: JSON.stringify(editData),
							contentType: "application/json",
							success: function() {
								$( "#studyDialog" ).dialog("close");
								location.reload();
							}
						});
					}
					else {
						var studyData = {
							"studyName": title,
							"description": description
						};
						$.ajax({
							url: studyCreateURL,
							type: "POST",
							data: JSON.stringify(studyData),
							contentType: "application/json",
							success: function(data) {
								if (!isNaN(data)) {
									$( "#studyDialog" ).dialog("close");
									var studyURL = studyDetailsURL + "?studyId=" + data;
									location.href = studyURL;
								}
							}
						});
					}
				} // End click
			},
			{
				text: i18next.t("common:Cancel"),
				class: "dialog-cancel",
				click: function() {
					$( "#studyDialog" ).dialog("close");
				}
			}
		],
		open: function() {
			var closeIcon = $("<a>").html("<i class='fa fa-fw fa-close'></i>")
			.on("click", function() {
				$( "#studyDialog" ).dialog("close");
			});
			
			var titlebar = $(".ui-dialog-titlebar").attr("class", "primary title-bar").empty();
			if (!titlebar.length) {
				titlebar = $(".primary.title-bar").empty();
			}
			$("<div>").html(dialog_title).addClass("center title").appendTo(titlebar);
			$("<span>").addClass("right").append(closeIcon).appendTo(titlebar);
			
			$("button.ui-button").removeClass("ui-state-default ui-button ui-button ui-button-text-only")
			.html(function(index, oldHTML) {
				return $(this).attr("text");
			}).addClass(function(index) {
				var className = "button";
				if (index != 0) className += " secondary";
				return className;
			});
			$(".ui-dialog-buttonpane").attr("class", "padding-left");
			$(".ui-dialog-buttonset").removeClass("ui-dialog-buttonset");
			$(".ui-dialog").removeClass("ui-front ui-corner-all").addClass("padding-none");
			$(".ui-corner-all").removeClass("ui-corner-all");
			
		}
	});
}

function openEditDialog() {
	var tabRow = $(this).closest("tr");
	var editDialog = $("#studyDialog");
	
	var studyId = tabRow.find(".study-id").val();
	var title = tabRow.find(".study-title").val();
	var description = tabRow.find(".study-descr").val();

	editDialog.find(".study-title").val(title);
	editDialog.find(".study-description").val(description);
	
	openStudyDialog(studyId);
	 $("#studyDialog").removeClass("hide").dialog("open");
}

function show_delete_icons() {
	var tabRow = $(this).closest("tr");
	
	tabRow.find(".edit-icon").addClass("hide");
	tabRow.find(".delete-icon").addClass("hide");
	tabRow.find(".delete-confirm-cancel").removeClass("hide");
}

function confirm_delete() {
	var tabRow = $(this).closest("tr");
	var cohortId = tabRow.find(".study-id").val();
	var deleteURL = studyDeleteURL + "?studyId=" + cohortId;
	$.ajax({
		'url': deleteURL,
		'type': "POST",
		'success': function() {
			tabRow.find(".edit-icon").removeClass("hide");
			tabRow.find(".delete-icon").removeClass("hide");
			tabRow.find(".delete-confirm-cancel").addClass("hide");
			tabRow.remove();
			
			$("#study-deleted-success").addClass("is-active");
			setTimeout(function() {
				$("#study-deleted-success").removeClass("is-active");
			}, 3000);
		},
		'error': function(xhr, statusStr, text) {
			tabRow.find(".edit-icon").removeClass("hide");
			tabRow.find(".delete-icon").removeClass("hide");
			tabRow.find(".delete-confirm-cancel").addClass("hide");
			if (xhr.status == 412) {
				$("#study-deleted-analysis-associated-error").addClass("is-active");
				setTimeout(function() {
					$("#study-deleted-analysis-associated-error").removeClass("is-active");
				}, 5000);
			}
			else {
				$("#study-deleted-error").addClass("is-active");
				setTimeout(function() {
					$("#study-deleted-error").removeClass("is-active");
				}, 3000);
			}
		},
		'ignoreGlobalError': true
	});
}

function cancel_delete() {
	var tabRow = $(this).closest("tr");
	
	tabRow.find(".edit-icon").removeClass("hide");
	tabRow.find(".delete-icon").removeClass("hide");
	tabRow.find(".delete-confirm-cancel").addClass("hide");
}

function displayAjaxError() {
	$("#global-ajax-error").addClass("is-active");
	setTimeout(function() {
		$("#global-ajax-error").removeClass("is-active");
	}, 3000);
}

function update_sort_icons() {
	$(".cohortstudies-table th").each(function() {
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
	$.tablesorter.storage( $(".cohortstudies-table")[0], "tablesorter-pagesize", pager_size );
	
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
	$.tablesorter.setFilters( $(".cohortstudies-table"), filters, true );
	
	var search_val = "search results \"" + filterValue + "\"";
	breadcrumb_set_filter(search_val);
}

function filter_studies_reset() {
	var filters = [];
	$.tablesorter.setFilters( $(".cohortstudies-table"), filters, true );
	
	$(".sidebar-left .search-field").val("");
	breadcrumb_remove_filter();
}
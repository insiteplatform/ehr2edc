function open_recalculate_dialog(cohortId) {
	var recalculateDialog = $("#recalculateDialog");
	recalculateDialog.dialog({
		title: i18next.t("recalculateDialog.title"),
		modal: true,
		width: 600,
		resizable: false,
		autoOpen: false,
		buttons: [{
				text: i18next.t("common:Yes"),
				click: function() {
					recalculate_cohort(cohortId);
				}
			},{
				text: i18next.t("common:No"),
				click: function() {
					recalculateDialog.dialog("close");
				}
			}
		],
		open: function() {
			open_dialog(recalculateDialog);
		}
	});
	recalculateDialog.removeClass("hide");
	recalculateDialog.dialog("open");
	
	function recalculate_cohort(cohortId) {
		$.ajax({
			'url': lwbSettings.recalculateURL + "?cohortId=" + cohortId,
			'type': "POST",
			'success': function() {
				$("#recalculateDialog").dialog("close");
				location.reload();
			},
		});
	}

	function open_dialog(recalculateDialog) {
		add_title_bar(recalculateDialog, i18next.t("recalculateDialog.title"));
		add_button_classes(recalculateDialog);
		add_dialog_styling(recalculateDialog);
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
			if (index !== 0) className += " secondary";
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
}


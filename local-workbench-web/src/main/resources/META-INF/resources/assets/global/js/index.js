$(document).ready(function () {
	$(".user-menu").on("click", function () {
		$(".user-menu").toggleClass("is-active");
	});
	$("body").on("click", function (event) {
		if (!$(event.target).closest(".user-menu").length) {
			$("#user-menu .user-menu").removeClass("is-active");
		}
	});
	$(".static-notification .close-button").on("click", function() {
		$(this).closest(".static-notification").removeClass("is-active");
	});
});

function showErrorDialog(text, duration) {
	if (typeof duration === 'undefined') {
		duration = 5000;
	}
	$("#error-dialog-text").text(text);
	$("#error-dialog").addClass("is-active");
	setTimeout(function() {
		$("#error-dialog").removeClass("is-active");
	}, duration);
}

function showSuccessDialog(text, duration) {
	if (typeof duration === 'undefined') {
		duration = 5000;
	}
	$("#success-dialog-text").text(text);
	$("#success-dialog").addClass("is-active");
	setTimeout(function() {
		$("#success-dialog").removeClass("is-active");
	}, duration);
}

function update_table_sort_icons(table) {
	table.find("th").each(function() {
		if (! $(this).hasClass("sorter-false")) {
			if ($(this).hasClass("asc")) {
				$(this).find("i").attr("class", "fa fa-sort-asc")
			} else if ($(this).hasClass("desc")) {
				$(this).find("i").attr("class", "fa fa-sort-desc")
			} else {
				$(this).find("i").attr("class", "fa fa-sort")
			}
		}
	});
}

function init_pager_size(table) {
	var pagesize = $.tablesorter.storage( table[0], 'tablesorter-pagesize') || "10";
	$(".page-item-numbers a").each(function() {
		if ($(this).text() === pagesize) {
			$(this).parent().addClass("is-active");
		}
	});
}

function update_pager(pager) {
	if (pager === undefined) {
		pager = $(".pager");
	}
	var splits = pager.find(".pagedisplay").val().split('/');
	var pageNr = parseInt(splits[0]),			// 1-based
		totalPages = parseInt(splits[1]);
	var pages = pager.find(".pagination");
	pages.find("li").remove(".move-page").remove(".ellipsis");

	if (totalPages < 7) {
		for (i = 0; i < totalPages; i++) {
			var pageItem = make_pager_number(i + 1, pager).insertBefore(pages.find(".next"));
			if (pageNr == i + 1) {
				pageItem.addClass("is-active");
			}
		}
	}
	else if (pageNr < 5) {
		for (i = 0; i < 4; i++) {
			var pageItem = make_pager_number(i + 1, pager).insertBefore(pages.find(".next"));
			if (pageNr == i + 1) {
				pageItem.addClass("is-active");
			}
		}
		$("<li>").html("&#8230;").addClass("ellipsis").insertBefore(pages.find(".next"));
		make_pager_number(totalPages, pager).insertBefore(pages.find(".next"));
	}
	else if (totalPages - pageNr <= 3) {
		for (i = 0; i < 4; i++) {
			var realNr = totalPages - i;
			var pageItem = make_pager_number(realNr, pager).insertAfter(pages.find(".prev"));

			if (pageNr == realNr) {
				pageItem.addClass("is-active");
			}
		}
		$("<li>").html("&#8230;").addClass("ellipsis").insertAfter(pages.find(".prev"));
	}
	else {
		$("<li>").html("&#8230;").addClass("ellipsis").insertBefore(pages.find(".next"));
		make_pager_number(pageNr - 1, pager).insertBefore(pages.find(".next"));
		make_pager_number(pageNr, pager).addClass("is-active").insertBefore(pages.find(".next"));
		make_pager_number(pageNr + 1, pager).insertBefore(pages.find(".next"));

		$("<li>").html("&#8230;").addClass("ellipsis").insertBefore(pages.find(".next"));
		make_pager_number(totalPages, pager).insertBefore(pages.find(".next"));
	}
}

function breadcrumb_set_filter(search_val) {
	breadcrumb_remove_filter();
	var bread_crumbs = $("#page-bread-crumbs");

	var active_crumb = bread_crumbs.find(".is-active");
	var active_crumb_text = active_crumb.text();
	active_crumb.empty();
	$("<a>").attr("href", "#").text(active_crumb_text).appendTo(active_crumb);
	active_crumb.removeClass("is-active").addClass("page-crumb");

	$("<li>").addClass("is-active").text(search_val).appendTo(bread_crumbs);
}

function breadcrumb_remove_filter() {
	var bread_crumbs = $("#page-bread-crumbs");

	var search_crumb = bread_crumbs.find(".page-crumb");
	search_crumb.next().addClass("is-active").text(search_crumb.text());
	search_crumb.remove();
}

function add_dialog_title_bar(dialog, title) {
	var dialogDiv = $(dialog).closest(".ui-dialog");
	var closeIcon = $("<a>").html("<i class='fa fa-fw fa-close'></i>")
	.on("click", function() {
		$(dialog).dialog("close");
	});
	var titlebar = dialogDiv.find(".ui-dialog-titlebar").attr("class", "primary title-bar").empty();
	$("<div>").html(title).addClass("center title").appendTo(titlebar);
	$("<span>").addClass("right").append(closeIcon).appendTo(titlebar);
}

function add_dialog_styling(dialog) {
	var dialogDiv = $(dialog).closest(".ui-dialog");
	dialogDiv.find(".ui-dialog-buttonpane").attr("class", "padding-left dialog-buttons");
	dialogDiv.find(".ui-dialog-buttonset").removeClass("ui-dialog-buttonset");
	dialogDiv.removeClass("ui-front ui-corner-all").addClass("padding-none");
	dialogDiv.find(".ui-corner-all").removeClass("ui-corner-all");
}

function recenter_dialog(dialog) {
	dialog.dialog('widget').position({
	   my: "center",
	   at: "center",
	   of: window
	});
}

function formatDate(ISOFormattedDate) {
	var date = moment(ISOFormattedDate);
	var now = moment();
	var yesterday = moment().subtract(1, "days");
	if (date.isSame(now, "day")) {
		return "Today, " + date.format("kk:mm");
	} else if (date.isSame(yesterday, "day")) {
		return "Yesterday, " + date.format("kk:mm");
	} else if (date.isSame(now, "year")) {
		return date.format("MMM D");
	} else {
		return date.format("MMM D, YYYY");
	}
}

function replacePathVariable(url, id, value) {
	return url.replace(new RegExp('\{' + id + '\}', 'gi'), value);
}
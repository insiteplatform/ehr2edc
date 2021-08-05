function init_pager(footer) {
    if (footer === undefined) {
        footer = $(".footer");
    }
    footer.find(".page-items .page-item-numbers").on("click", "li", update_pager_size);
}

function update_pager_size(event) {
	var pagerSize = $(event.target).text();
	var pageItems = $(event.target).closest(".page-items");
	pageItems.find(".pager-size").val(pagerSize).trigger("change");
	pageItems.find("li.is-active").removeClass("is-active");
	$(this).addClass("is-active");
}

function make_pager_number(number, pager) {
	var result = $("<li>").addClass("move-page update-pager");
	$("<a>").attr("href", "#").html(number).appendTo(result);
	result.on("click", function() {
	    var pagerNr = pager.find(".pager-nr");
		pagerNr.val(number);
		pagerNr.trigger("change");
	});
	return result;
}

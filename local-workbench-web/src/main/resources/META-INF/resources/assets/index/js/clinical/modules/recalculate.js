var $recalculateBtn;
var $recalculateBtnIcon;

$(document).ready(function() {
    $recalculateBtn = $("#recalculate-btn");
    $recalculateBtnIcon = $("#recalculate-btn .fa-refresh");
    $recalculateBtn.on("click", recalculate);
});

function recalculate() {
    disableRecalculateBtn();
    $.ajax({
        'url': recalculateUrl,
        'method': "POST",
        'success': reloadResults,
        'error': handleError
    });

    function handleError() {
        enableRecalculateBtn();
        showErrorDialog("Failed recalculating results");
    }
}

function updateRecalculateButton(isCompleted) {
    if (isCompleted) {
        enableRecalculateBtn();
    } else {
        disableRecalculateBtn();
    }
}

function disableRecalculateBtn() {
    $recalculateBtn.addClass("disabled");
    $recalculateBtnIcon.addClass("fa-spin");
}

function enableRecalculateBtn() {
    $recalculateBtn.removeClass("disabled");
    $recalculateBtnIcon.removeClass("fa-spin");
}
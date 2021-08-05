$(document).ready(function() {
	$("#recoverAccount").on("click", recoverAccount);
}).ajaxSend(function(e, xhr) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    if (typeof header != 'undefined' && typeof token != 'undefined') {
        xhr.setRequestHeader(header, token);
    }
});

function recoverAccount() {
    if (validateInput()) {
        var request = createRequest();
        $.ajax({
            'url': window.location,
            'type': "POST",
            'contentType': "application/json",
            'data': JSON.stringify(request),
            'success': handleSuccess,
            'error': handleError,
            'ignoreGlobalError': true
        });
    }
}

function validateInput() {
    clearValidation();
    return validateInputPassword();
}

function clearValidation() {
    $(".form-field-error").removeClass("form-field-error");
    $(".form-text-error").remove();
    $(".alert-box").addClass("hide");
}

function validateInputPassword() {
    var isValid = true;
    var password = $("#password").val();
    var repeatPassword = $("#password2").val();
    if (!password) {
        addValidationMessage($("#password"), "Required");
        isValid = false;
    }
    if (!repeatPassword) {
        addValidationMessage($("#password2"), "Required");
        isValid = false;
    } else if (password !== repeatPassword) {
        addValidationMessage($("#password2"), "Passwords do not match");
        isValid = false;
    }
    return isValid;
}

function addValidationMessage(element, message) {
    $(element).addClass("form-field-error");
    $(element).after($("<p>").addClass("form-text-error").text(message));
}

function createRequest() {
    var userId = $("#user-id").val();
    var password = $("#password").val();
    return {
        'userId': userId,
        'password': password
    };
}

function handleSuccess() {
    show($(".recover-success"));
}

function handleError(jqXHR) {
    if (jqXHR.responseText !== "") {
        showMessage($(".recover-error"), jqXHR.responseText);
    } else {
        showMessage($(".recover-error"), "Unexpected error while recovering your password.");
    }
}

function showMessage(element, message) {
	element.text(message);
	show(element);
}

function show(element) {
    element.removeClass("hide");
}



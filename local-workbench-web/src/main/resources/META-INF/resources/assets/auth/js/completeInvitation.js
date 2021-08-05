$(document).ready(function() {
	$("#inviteAccept").on("click", acceptInvitation);
}).ajaxSend(function(e, xhr) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    if (typeof header != 'undefined' && typeof token != 'undefined') {
        xhr.setRequestHeader(header, token);
    }
});

function acceptInvitation() {
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
    var validPassword = validateInputPassword();
    var validQuestion = validateInputQuestion();
    return validPassword && validQuestion;
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

function validateInputQuestion() {
    var isValid = true;
    var securityQuestionId = $("#secQuestion").val();
    var securityAnswer = $("#secAnswer").val();
    if (!securityQuestionId) {
        addValidationMessage($("#secQuestion"), "Required");
        isValid = false;
    }
    if (!securityAnswer) {
        addValidationMessage($("#secAnswer"), "Required");
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
    var securityQuestionId = $("#secQuestion").val();
    var securityAnswer = $("#secAnswer").val();
    return {
        'userId': userId,
        'password': password,
        'securityQuestionId': securityQuestionId,
        'securityQuestionAnswer': securityAnswer
    };
}

function handleSuccess() {
    show($(".accept-success"));
}

function handleError(jqXHR) {
    if (jqXHR.responseText !== "") {
        showMessage($(".accept-error"), jqXHR.responseText);
    } else {
        showMessage($(".accept-error"), "Unexpected error while activating your account.");
    }
}

function showMessage(element, message) {
	element.text(message);
	show(element);
}

function show(element) {
    element.removeClass("hide");
}




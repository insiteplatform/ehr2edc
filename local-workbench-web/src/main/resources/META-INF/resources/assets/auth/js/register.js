BASE_URL = BASE_URL || '';
PATH_REGISTER = PATH_REGISTER || '';

$(document)
    .ajaxSend(function (e, xhr, options) {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        if (typeof header != 'undefined' && typeof token != 'undefined') {
            xhr.setRequestHeader(header, token);
        }
    })
    .ready(function () {
        $("#btnSubmit").on("click", function () {
            resetValidation();
            var isValid = check();
            if (isValid) {
                $(".errors").empty();

                var userName = $("#name").val();
                var email = $("#email").val().toLocaleLowerCase();

                $.ajax({
                    url: BASE_URL + PATH_REGISTER,
                    data: JSON.stringify({
                        "username": userName, "email": email
                    }),
                    type: "POST",
                    contentType: "application/json",
                    success: function () {
                        window.location.replace(BASE_URL + PATH_LOGIN + "?register=success");
                    },
                    error: function (jqXHR, error) {
                        switch (jqXHR.status) {
                            case 400:
                                $(".errors").append("<p class='form-text-error'>This e-mail or username is already in use</span>");
                                break;
                            default:
                                $(".errors").append("<p class='form-text-error'>An error occured while requesting an account.</span>");
                                break
                        }
                    },
                });
            }


        });

    });

function resetValidation() {
    $(".form-field-error").removeClass("form-field-error margin-bottom");
    $(".form-text-error").remove();
}

function check() {
    var isValid = true;
    if (!$("#name").val()) {
        $("#name").addClass("form-field-error margin-bottom");
        isValid = false;
    }
    if (!validateEmail($("#email").val())) {
        $("#email").addClass("form-field-error margin-bottom");
        isValid = false;
    }

    return isValid;
};

function validateEmail(email) {
    var emailReg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return emailReg.test(email);
}
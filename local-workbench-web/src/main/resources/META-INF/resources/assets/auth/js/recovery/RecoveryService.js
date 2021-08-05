function RecoveryService(recoveryView) {
    var VALID_EMAIL_REGEX = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    this.checkMail = checkMail;
    this.checkAnswer = checkAnswer;

    function checkMail() {
        var mail = recoveryView.getEmailField().val();
        recoveryView.hideAllAlerts();

        if (!validateEmail(mail)) {
            recoveryView.getEmailField().addClass("form-field-error");
            recoveryView.addFieldError("Valid e-mail is required", recoveryView.getEmailField());
        }
        else {
            var data = {
                'userMail': mail.toLowerCase()
            };
            doRecoveryCall(data);
        }
    }

    function checkAnswer() {
        var mail = recoveryView.getEmailField().val();
        var answer = recoveryView.getSecurityAnswerField().val();

        recoveryView.hideAllAlerts();

        if (mail === "") {
            recoveryView.getEmailField().addClass("form-field-error");
            recoveryView.addFieldError("E-mail is required", recoveryView.getEmailField());
        }
        else if (answer === "") {
            recoveryView.getEmailField().addClass("form-field-error");
            recoveryView.addFieldError("Please fill in your answer", recoveryView.getSecurityAnswerField());
        }
        else {
            recoveryView.onRecoveryLoading();
            var data = {
                'userMail': mail,
                'userAnswer': answer
            };
            $.ajax({
                'url': BASE_URL + PATH_RECOVER,
                'type': "POST",
                'contentType': 'application/json',
                'data': JSON.stringify(data),
                'success': recoveryView.onRecoverySuccess,
                'error': onRecoveryError
            });
        }
    }

    function onRecoveryError(jqXHR) {
        if (jqXHR.responseText !== "") {
            recoveryView.onRecoveryError(jqXHR.responseText);
        } else {
            recoveryView.onRecoveryError("Unexpected error while recovering your account.");
        }
    }

    function validateEmail(email) {
        return VALID_EMAIL_REGEX.test(email);
    }

    function doRecoveryCall(data) {
        $.ajax({
            'url': BASE_URL + PATH_RECOVER,
            'type': "POST",
            'data': JSON.stringify(data),
            'contentType': 'application/json',
            'dataType': "text",
            'success': function (questionData) {
                recoveryView.getSecurityQuestionLabel().removeClass("hide");
                recoveryView.getSecurityQuestion().text(questionData);
            },
            error: function () {
                recoveryView.getNoQuestionAlert().removeClass("hide");
            }
        });
    }
}